package com.telefonica.entityservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.services.IEntidadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Servicio para manejo de Entidades", description = "Alta baja modificacion de Entidades con sus respectivas propiedades")
@RequestMapping("/entity-api")
public class EntidadRestController {

	@Autowired
	private IEntidadService entidadImpl;

	@ApiOperation(value = "Obtener lista de entidades", response = Entidad.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Se obtuvo la lista de entidades"),
			@ApiResponse(code = 404, message = "No existe ninguna entidad en la base de datos") })
	@GetMapping("/entidades")
	public ResponseEntity<?> findAll() {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("mensaje", "No existen entidades en la base de datos");
		
		List<EntidadDTO> found = entidadImpl.findAll();
		
		return found == null ? new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND)
				: new ResponseEntity<List<EntidadDTO>>(found, HttpStatus.OK);
	}

	@ApiOperation(value = "Obtener una entidad por su nombre", response = Entidad.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Se obtuvo la entidad"),
			@ApiResponse(code = 404, message = "No existe la entidad en la base de datos") })
	@GetMapping("/entidad")
	public ResponseEntity<?> findByNombre(@RequestParam(value = "nombre", defaultValue = "") String nombreEntidad) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("mensaje",
				"La entidad ".concat(nombreEntidad.toString().concat(" no existe en la base de datos")));

		return entidadImpl.findByNombre(nombreEntidad) == null
				? new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND)
				: new ResponseEntity<EntidadDTO>(entidadImpl.findByNombre(nombreEntidad), HttpStatus.OK);
	}

	@ApiOperation(value = "Creacion de una nueva entidad", consumes = "application/json", response = Entidad.class)
	@ApiResponses({ @ApiResponse(code = 201, message = "La entidad se creo satisfactoriamente"),
			@ApiResponse(code = 400, message = "Error en la estructura json  => Campos con errores") })
	@PostMapping("/entidad")
	public ResponseEntity<?> createEntity(@Valid @RequestBody(required = true) Entidad ent) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		EntidadDTO entidad = entidadImpl.findByNombre(ent.getNombre());
		
		if (entidad == null) {
			entidadImpl.save(ent);
			response.put("mensaje", "La entidad fue creada");
			return new ResponseEntity<Map<String, Object>>(response ,HttpStatus.CREATED);

		}
		response.put("mensaje", "Ya existe una entidad con el nombre indicado");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
	}

	@ApiOperation(value = "Actualizacion de una entidad", consumes = "application/json", response = Entidad.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "La entidad se actualizo satisfactoriamente"),
			@ApiResponse(code = 400, message = "Error en la estructura json => Campos con errores") })
			
	@PutMapping("/entidad")
	public ResponseEntity<?> updateEntity(@Valid @RequestBody(required = true) Entidad ent,
			@RequestParam(value = "id", required = true) Long id) {

		Map<String, Object> response = new HashMap<String, Object>();		
		Optional<Entidad> entidad = entidadImpl.findEntidadById(id);

		if (entidad.isPresent()) {
			EntidadDTO entidadPorNombre = entidadImpl.findByNombre(ent.getNombre()); 
			
			//Se valida que el nombre no se encuentre asignado a otra entidad
			//o bien que se trate de la misma entidad
			if (entidadPorNombre == null || entidadPorNombre.getId() == entidad.get().getId()) {
				Entidad toSave = actualizarCamposEntidad(entidad, ent);
				entidadImpl.save(toSave);
				response.put("mensaje", "La entidad con el id ".concat(id.toString().concat(" fue actualizada")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
				
			} else {
				response.put("mensaje", "Ya existe una entidad con el nombre indicado");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
			}

		} else {
			response.put("mensaje",
					"La entidad con el id ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
	}

	@ApiOperation(value = "Borar una Entidad")
	@ApiResponses({ @ApiResponse(code = 200, message = "La entidad se borro satisfactoriamente"),
			@ApiResponse(code = 404, message = "La entidad no existe en la base de datos") })
	@DeleteMapping("/entidad")
	public ResponseEntity<?> deleteEntity(@RequestParam(value = "id", required = true) Long id) {
		Map<String, Object> response = new HashMap<String, Object>();

		if (entidadImpl.findById(id) != null) {

			entidadImpl.delete(id);
			response.put("mensaje", "La entidad con el id ".concat(id.toString().concat(" fue eliminada")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);

		} else {
			response.put("mensaje",
					"La entidad con el id ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
	}
	
	public static Entidad actualizarCamposEntidad(Optional<Entidad> entidadVieja, Entidad entidadActualizada) {
		entidadVieja.get().setNombre(entidadActualizada.getNombre());
		entidadVieja.get().getItems().clear();
		entidadVieja.get().getItems().addAll(entidadActualizada.getItems());
		
		return entidadVieja.get();

	}

}
