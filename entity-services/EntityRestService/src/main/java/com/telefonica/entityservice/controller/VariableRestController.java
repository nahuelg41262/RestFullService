package com.telefonica.entityservice.controller;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.VariableDTO;
import com.telefonica.entityservice.models.dtos.VariablesRequestDTO;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.services.IEntidadService;
import com.telefonica.entityservice.models.services.IItemService;
import com.telefonica.entityservice.models.services.IVariableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@Api(value = "Servicio para manejo de Variables", description = "Alta baja modificacion de Variables")
@RequestMapping("/variable-api")
@Validated
public class VariableRestController {

    @Autowired
    private IVariableService variableService;
    @Autowired
    private IEntidadService entidadService;
    @Autowired
    private IItemService itemService;

    @ApiOperation(value = "Obtener lista de variables por contrato ", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Se obtuvo la lista de variables para el contrato indicado"),
            @ApiResponse(code = 404, message = "No existen variables en la base de datos para el contrato indicado")})
    @GetMapping("/variables")
    public ResponseEntity<?> findByIdContrato(@RequestParam(value = "idDeContrato", required = true) Long idDeContrato) {


        Optional<List<Variable>> variables = variableService.findByIdContrato(idDeContrato);
        return variables.get().isEmpty()
                ? new ResponseEntity<Map>(Map.of("mensaje", "no existen variables para el contrato con el id ".concat(idDeContrato.toString())), HttpStatus.NOT_FOUND)
                : new ResponseEntity<List<Variable>>(variables.get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Obtener lista de variables por Nombre de Entidad ", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Se obtuvo la lista de variables para la entidad indicada"),
            @ApiResponse(code = 404, message = "No existen variables en la base de datos para la entidad indicada")})
    @GetMapping("/variablesPorNombreEntidad")
    public ResponseEntity<?> findByNombreDeEntidad(@RequestParam(value = "nombreDeEntidad", required = true) String nombreDeEntidad) {

        EntidadDTO entidad = entidadService.findByNombre(nombreDeEntidad);

        if (entidad == null)
            return new ResponseEntity<Map<String, Object>>(Map.of("error", "No existe la entidad con el nombre " + nombreDeEntidad + " en la base de datos ."), HttpStatus.NOT_FOUND);

        if (entidad.getItems() == null)
            return new ResponseEntity<Map<String, Object>>(Map.of("error", "No existen items asociados para la entidad con el nombre " + nombreDeEntidad), HttpStatus.NOT_FOUND);

        Optional<List<Variable>> variables = variableService.findByEntidad(nombreDeEntidad);
        return variables.get().isEmpty()
                ? new ResponseEntity<Map>(Map.of("mensaje", "no existen variables para la entidad con el nombre " + nombreDeEntidad), HttpStatus.NOT_FOUND)
                : new ResponseEntity<List<Variable>>(variables.get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Obtener lista de variables por idDeContrato , Nombre de Entidad y Nombre Del Item", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Se obtuvo la lista de variables para los valores indicados"),
            @ApiResponse(code = 404, message = "No existen variables en la base de datos para la entidad indicada")})
    @GetMapping("/variablesPorIdDeContratoNombreDeEntidadNombreDeItem")
    public ResponseEntity<?> findByIdContratoAndIdEntidadAndNombreDeItem(
            @RequestParam(value = "idDeContrato", required = true) Long idDeContrato,
            @RequestParam(value = "nombreDeEntidad", required = true) String nombreDeEntidad,
            @RequestParam(value = "nombreDeItem", required = true) String nombreDeItem) {

        Map<String, Object> errors = new HashMap<>();
        //Se valida la existencia de la entidad , el item y el contrato requerido
        if (variableService.findByIdContrato(idDeContrato).get().isEmpty())
            errors.put("Error 1", "no existen variables para el contrato con el id ".concat(idDeContrato.toString()));

        if (entidadService.findByNombre(nombreDeEntidad) == null)
            errors.put("Error 2 ", "no existe la entidad con el nombre " + nombreDeEntidad);

        if (itemService.findByNombreItem(nombreDeItem).isEmpty())
            errors.put("Error 3 ", "no existe el item con el nombre " + nombreDeItem);

        if (!errors.isEmpty()) return new ResponseEntity<Map<String, Object>>(errors, HttpStatus.NOT_FOUND);


        Optional<List<Variable>> variables = variableService.findByIdContratoAndNombreEntidadAndNombreDeItem(idDeContrato, nombreDeEntidad, nombreDeItem);
        return variables.get().isEmpty()
                ? new ResponseEntity<Map>(Map.of("mensaje", "no existen variables existentes para los valores :  contrato = " + idDeContrato.toString() + ", Entidad = " + nombreDeEntidad + "Item = " + nombreDeItem + "."), HttpStatus.NOT_FOUND)
                : new ResponseEntity<List<Variable>>(variables.get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Obtener lista de variables por contrato y entidad ", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Se obtuvo la lista de variables para el contrato y entidad indicado"),
            @ApiResponse(code = 404, message = "No existe la entidad en la base de datos con el id indicado"),
            @ApiResponse(code = 404, message = "No existe variable en la base de datos para el contrato y la entidad indicada"),
            @ApiResponse(code = 404, message = "No existe variable en la base de datos para el contrato indicado")})
    @GetMapping("/variablesPorContratoYNombreDeEntidad")
    public ResponseEntity<?> findByIdContratoAndIdEntidad(@RequestParam(value = "idDeContrato", required = true) Long idDeContrato, @RequestParam(value = "nombreDeEntidad", required = true) String nombreDeEntidad) {

        Map<String, Object> errors = new HashMap<>();
        //Se valida la existencia de la entidad y el contrato requerido
        if (variableService.findByIdContrato(idDeContrato).get().isEmpty())
            errors.put("Error 1", "no existen variables para el contrato con el id ".concat(idDeContrato.toString()));

        if (entidadService.findByNombre(nombreDeEntidad) == null)
            errors.put("Error 2 ", "no existe la entidad con el id ".concat(nombreDeEntidad.toString()));

        if (!errors.isEmpty()) return new ResponseEntity<Map<String, Object>>(errors, HttpStatus.NOT_FOUND);

        Optional<List<Variable>> variables = variableService.findByIdContratoAndIdEntidad(idDeContrato, nombreDeEntidad);

        return variables.get().isEmpty()
                ? new ResponseEntity<Map>(Map.of("mensaje", "no existen variables para el contrato ".concat(idDeContrato.toString()).concat(" para la entidad con el id ").concat(nombreDeEntidad.toString())), HttpStatus.NOT_FOUND)
                : new ResponseEntity<List<Variable>>(variables.get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Insercion de Variables", consumes = "application/json", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "La Variable se creo"),
            @ApiResponse(code = 400, message = "Error en la estructura json => Campos con errores")})
    @PostMapping("/variables")
    public ResponseEntity<?> createVariables(@ApiParam(value = "Se insertaran la \"listaDeVaribles\" para el \"idDeContrato\" indicado")@Valid @RequestBody(required = true) List<VariablesRequestDTO> variables) {

        Map<String, Object> errors = new LinkedHashMap<>();
        Map<String, Object> succesed = new HashMap<>();
        AtomicInteger counterErrors = new AtomicInteger(1);
        AtomicInteger counterSuccesed = new AtomicInteger(1);

        variables.forEach(vars -> {
            vars.getListaDeVariables().forEach(variable -> {

                variable.getNombreDeEntidad().ifPresent(nombreDeEntidad -> {
                    Optional<Entidad> entidad = entidadService.findByNombreEntidad(nombreDeEntidad);

                    entidad.ifPresentOrElse(ent -> {

                        variable.getNombreDeItem().ifPresentOrElse(nombreDeItem -> {
                                    //Procesamiento vairable que ingrsa con el nombre de item
                                     ent.getItems().stream()
                                            .filter(item -> item.getNombreItem().equals(nombreDeItem))
                                            .collect(Collectors.toList())
                                            .stream()
                                            .findFirst()
                                            .ifPresentOrElse(item -> {
                                                itemService.upsertItemWithVarible(item, vars.getIdDeContrato(), variable.getValor(), item.getId());
                                                succesed.put("Creada ".concat(counterSuccesed.toString()), variable.getValor());
                                                counterSuccesed.getAndIncrement();

                                            }, () -> {
                                                errors.put("error ".concat(counterErrors.toString()),
                                                        "no existe el item con el nombre :  " + variable.getNombreDeItem().get() + " en la entidad pasada por parametro , puede crear la variable omitiendo el campo nombreDeItem  . La variable " + variable.getValor() + " no fue creada");
                                                counterErrors.getAndIncrement();
                                            });
                                }, () -> {
                                    //Procesamiento de variable que ingrsa solo con nombre de entidad
                                    ent.getItems()
                                            .stream()
                                            .filter(i -> i.getNombreItem().equals(variable.getValor()))
                                            .collect(Collectors.toList())
                                            .stream()
                                            .findFirst()
                                            .ifPresentOrElse(itemEncontrado -> {
                                                //Se actualiza la lista de variable en el itemEncontrado , referenciando la nueva variable al itemEncontrado
                                                variableService.save(itemEncontrado, variable.getValor(), vars.getIdDeContrato());
                                                succesed.put("Creada "+counterSuccesed.toString(), variable.getValor()+", fue creada referenciando al item "+itemEncontrado.getNombreItem()+" en la entidad "+ent.getNombre());
                                                counterSuccesed.getAndIncrement();
                                            }, () -> {
                                                // se Crea un Nuevo item con el Valor ingresado
                                                itemService.addNewItemInEntidad(ent, variable.getValor(), vars.getIdDeContrato());
                                                succesed.put("Creada "+counterSuccesed.toString(), variable.getValor()+", fue creada referenciando a un item con su valor como nombre del item");
                                                counterSuccesed.getAndIncrement();
                                            });
                                }
                        );
                    }, () -> {
                        //La entidad no existe
                        errors.put("error " + counterErrors.toString(), "no existe la entidad con el id ingresado Entidad : " + variable.getNombreDeEntidad().get());
                        counterErrors.getAndIncrement();
                    });

                });
            });
        });

        return errors.isEmpty()
                ? new ResponseEntity<Map<String, Object>>(succesed, HttpStatus.CREATED)
                : new ResponseEntity<Map<String, Map<String, Object>>>(Map.of("Variables con Errores", errors, "Variables Insertadas ", succesed), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/variables")
    @ApiOperation(value = "Actualizacion de una Variable", response = Variable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "La Variable se se actualizo"),
            @ApiResponse(code = 404, message = "No existe la variables para el id solicitado")})
    public ResponseEntity<?> updateVariable(@ApiParam(value = "id de la variable a ser actulizada") @RequestParam(required = true) Long id,
                                            @ApiParam(name = "values", value = "campos por ser actulizados") @Valid @RequestBody(required = true) VariableDTO updatedVariable) {
        AtomicBoolean flag = new AtomicBoolean(false);
        Map<String, Object> errors = new HashMap<String, Object>();

        Optional<Variable> variableToUpdate = variableService.findById(id);
        if (variableToUpdate.isEmpty())   return new ResponseEntity<Map<String, Object>>(Map.of("Error", "La variable con el id "+id.toString()+" no existe en la base de datos"), HttpStatus.NOT_FOUND);

        updatedVariable.getIdContrato().ifPresent(variableToUpdate.get()::setIdContrato);
        updatedVariable.getValor().ifPresent(variableToUpdate.get()::setValor);
        updatedVariable.getIdItem().ifPresent(idItem -> {
            itemService.findById(idItem).ifPresentOrElse(variableToUpdate.get()::setIdItem, () -> flag.set(true));
        });

        if (flag.get())
            return new ResponseEntity<Map<String, Object>>(Map.of("Error", "El id del item no existe en la base de datos"), HttpStatus.NOT_FOUND);

        Variable variableActualizada = variableService.save(variableToUpdate.get());

        return new ResponseEntity<Map<String, Object>>(Map.of("Actualizada ", variableActualizada), HttpStatus.OK);
    }

    @DeleteMapping("/variables")
    @ApiOperation(value = "Elimminar una Variable", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "La se elimino satisfactoriamente"),
            @ApiResponse(code = 404, message = "No existe la variables para el id solicitado")})
    public ResponseEntity<?> deleteVariable(@RequestParam(required = true) Long id) {

        Map<String, Object> errors = new HashMap<String, Object>();

        Optional<Variable> variableToBeDelete = variableService.findById(id);

        variableToBeDelete.ifPresentOrElse(variable -> variableService.delete(variable),
                () -> errors.put("Error", "La variable con el id " + id.toString() + " no existe en la base de datos"));

        return errors.isEmpty()
                ? new ResponseEntity<Map<String, Object>>(Map.of("Borrado", "La variable con el id ".concat(id.toString()).concat(" fue eliminada de la base de datos")), HttpStatus.OK)
                : new ResponseEntity<Map<String, Object>>(errors, HttpStatus.NOT_FOUND);
    }

    //Handler excepciones generadas generadas por la validacion de VariablesResquestDTO
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleException(ConstraintViolationException e) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        List<String> errors = e.getConstraintViolations()
                .stream()
                .map(x -> x.getMessage())
                .collect(Collectors.toList());
        body.put("errores en la estructura del json", errors);

        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

}
