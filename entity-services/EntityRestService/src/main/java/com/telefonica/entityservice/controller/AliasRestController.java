package com.telefonica.entityservice.controller;

import com.telefonica.entityservice.models.dtos.AliasDTO;
import com.telefonica.entityservice.models.dtos.AliasResquestDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.services.AliasService;
import com.telefonica.entityservice.models.services.IEntidadService;
import com.telefonica.entityservice.models.services.IItemService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@Api(value = "Servicio para manejo de Alias", description = "Alta baja modificacion de Alias")
@RequestMapping("/alias-api")
public class AliasRestController {

    @Autowired
    private AliasService aliasService;

    @Autowired
    private IEntidadService entidadService;

    @Autowired
    private IItemService itemService;

    @ApiOperation(value = "Obtener lista de Alias por item y entidad ", response = AliasDTO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Se obtuvo la lista de alias para el item y entidad indicado"),
            @ApiResponse(code = 404, message = "Error no existe la entidad y/o  item solicitado")})
    @GetMapping("/aliasPorNombreDeEntidadYNombreDeItem")
    public ResponseEntity<?> findByItem(
            @RequestParam(required = true, value = "nombreEntidad") String nombreEntidad,
            @RequestParam(required = true, value = "nombreItem") String nombreItem) {

        Map<String, Object> errors = new HashMap<>();
        List<AliasDTO> alias = new ArrayList<>();

        entidadService.findByNombreEntidad(nombreEntidad)
                .ifPresentOrElse(entidad -> {
                            entidad.getItems()
                                    .stream()
                                    .filter(item -> item.getNombreItem().equals(nombreItem))
                                    .findFirst()
                                    .ifPresentOrElse(item -> {
                                                alias.addAll(item.getAlias().stream()
                                                        .map(aliasEncontrado -> new AliasDTO(aliasEncontrado.getId(), aliasEncontrado.getValor()))
                                                        .collect(Collectors.toList())
                                                );
                                            },
                                            () -> errors.put("error 1", "No existe el item con el nombre " + nombreItem + " en la entidad " + nombreEntidad)
                                    );
                        }, () -> errors.put("error 2", "No existe la entidad con el nombre " + nombreEntidad)
                );

        return errors.isEmpty()
                ? new ResponseEntity<List<AliasDTO>>(alias, HttpStatus.OK)
                : new ResponseEntity<Map<String, Object>>(Map.of("Error : ", errors), HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Crear uno o muchos Alias para un Item determinado ", response = Alias.class)
    @ApiResponses({@ApiResponse(code = 201, message = "Se creo el alias "),
            @ApiResponse(code = 404, message = "No existen Alias en la base de datos para el contrato indicado")})
    @PostMapping("/alias")
    public ResponseEntity<?> createAlias(
            @RequestParam(required = true, value = "nombreEntidad") String nombreEntidad,
            @RequestParam(required = true, value = "nombreItem") String nombreItem,
            @ApiParam(name = "aliasList", value = "Lista de Alias a ser creados en el item indicado . ")
            @Valid @RequestBody List<Alias> aliasList) {

        Map<String, Object> errors = new HashMap<>();
        List<Alias> alias = new ArrayList<>();

        entidadService.findByNombreEntidad(nombreEntidad)
                .ifPresentOrElse(entidad -> {
                            entidad.getItems()
                                    .stream()
                                    .filter(item -> item.getNombreItem().equals(nombreItem))
                                    .collect(Collectors.toList())
                                    .stream()
                                    .findFirst()
                                    .ifPresentOrElse(item -> {
                                                item.getAlias().addAll(aliasList);
                                                itemService.save(item);
                                            },
                                            () -> errors.put("error 1", "No existe el item con el nombre " + nombreItem + " en la entidad " + nombreEntidad)
                                    );
                        }, () -> errors.put("error 2", "No existe la entidad con el nombre " + nombreEntidad)
                );
        return errors.isEmpty()
                ? new ResponseEntity<Map<String, Object>>(Map.of("Creado", "Los alias fueron creados "), HttpStatus.CREATED)
                : new ResponseEntity<Map<String, Object>>(Map.of("Error : ", errors), HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Eliminar un o muchos Alias")
    @ApiResponses({@ApiResponse(code = 200, message = "Se elimino el/los alias"),
            @ApiResponse(code = 404, message = "No existen Alias en la base de datos el/los IDs indicados")})
    @DeleteMapping("/alias")
    public ResponseEntity<?> deleteAlias(@ApiParam(name = "ids", value = "Array de IDs a ser eliminados . Ejemplo : [1, 2 , 3] -> eliminar los alias con dichos IDs") @RequestBody(required = true) @Valid List<Long> ids) {

        Map<String, Object> errors = new HashMap<>();
        AtomicInteger counterErrors = new AtomicInteger(1);
        ids.forEach(id -> {
            aliasService.findById(id)
                    .ifPresentOrElse(alias -> {
                                //Si el alias que se va a eliminar es el ultimo , se crea un nuevo alias con el nombre del item
                                if (alias.getItem().getAlias().size() > 1) {
                                    aliasService.delete(alias);
                                } else {
                                    aliasService.save(alias.getItem().getNombreItem(), alias.getItem());
                                    aliasService.delete(alias);
                                }
                            }, () -> errors.put("error " + counterErrors.getAndIncrement(), "No existe alias con el id : " + id.toString())
                    );
        });

        return errors.isEmpty()
                ? new ResponseEntity<Map<String, Object>>(Map.of("Eliminado", "Los / el alias fueron eliminado"), HttpStatus.OK)
                : new ResponseEntity<Map<String, Object>>(Map.of("Error : ", errors), HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Modificar un o muchos Alias")
    @ApiResponses({@ApiResponse(code = 200, message = "Se actualizo el alias"),
            @ApiResponse(code = 404, message = "No existen Alias en la base de datos el ID indicado")})
    @PutMapping("/alias")
    public ResponseEntity<?> updateAlias(
            @ApiParam(name = "alias", value = "Campos por ser actulizados para el alias con el ID pasado en el campo \"id\".En caso de ingresar \"nombreItem\" es necesario indicar el \"nombreEntidad\", los campos \"nombreItem\" y \"valor\" pueden omitirse en caso de no querer ser cambiados . ")
            @Valid @RequestBody(required = true) List<AliasResquestDTO> alias) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> success = new HashMap<>();

        AtomicInteger counterSuccess = new AtomicInteger(1);

        AtomicInteger counterErrors = new AtomicInteger(1);

        alias.forEach(aliasResquestDTO -> {
            aliasResquestDTO.getId().ifPresentOrElse(id -> {
                aliasService.findById(id).ifPresentOrElse(aliasEncontrado -> {
                            aliasResquestDTO.getNombreDeItem().ifPresent(nombreItem -> {
                                //Se validad que ingreso la entidad para procesar el item
                                aliasResquestDTO.getNombreDeEntidad().ifPresentOrElse(nombreEntidad -> {
                                    //Se validad que exista la entidad
                                    entidadService.findByNombreEntidad(nombreEntidad).ifPresentOrElse(entidad -> {
                                        //Se valida que exista el item en la entidad
                                        aliasResquestDTO.getNombreDeItem().ifPresent(nombreDelItemIngresadp -> {
                                            entidad.getItems().stream()
                                                    .filter(item -> item.getNombreItem().equals(nombreDelItemIngresadp))
                                                    .collect(Collectors.toList())
                                                    .stream()
                                                    .findFirst()
                                                    .ifPresentOrElse(item -> {
                                                        aliasEncontrado.setItem(item);
                                                    }, () -> {
                                                        errors.put("Error " + counterErrors.getAndIncrement(), "El item no existe en la entidad indicada , la referencia al item para el alias"+aliasEncontrado.getValor()+" no fue actualizada");
                                                    });
                                        });
                                    }, () -> {
                                        errors.put("Error " + counterErrors.getAndIncrement(), "La entidad no existe en la base de datos , la referencia al item para el alias"+aliasEncontrado.getValor()+"no fue actualizada");
                                    });
                                }, () -> {
                                    errors.put("Error " + counterErrors.getAndIncrement(), "Para cambiar la referencia del item es necesario indicar el nombre de la entidad . La referencia al item para el alias "+ aliasEncontrado.getValor()+"no fue actualizada");
                                });
                            });
                            aliasResquestDTO.getValor().ifPresent(aliasEncontrado::setValor);
                            aliasService.save(aliasEncontrado);
                            success.put("Creado "+counterSuccess.getAndIncrement(),"La variable con el id "+id.toString()+" fue actualizada");
                        }, () -> {
                            errors.put("Error " + counterErrors.getAndIncrement(), "No existe el alias con el id " + id.toString());
                        }
                );
            }, () -> errors.put("Error " + counterErrors.getAndIncrement(), " El campo ID es obligatorio, la variable no fue actualizada "));
        });

        return errors.isEmpty()
                ? new ResponseEntity<Map<String, Object>>(success, HttpStatus.OK)
                : new ResponseEntity<Map<String, Object>>(Map.of("Errores : ", errors , "Actualizados : " ,success), HttpStatus.BAD_REQUEST);
    }

}
