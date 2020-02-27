package com.telefonica.entityservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telefonica.entityservice.functions.Function1;
import com.telefonica.entityservice.functions.Function2;
import com.telefonica.entityservice.functions.Functions;
import com.telefonica.entityservice.models.dtos.AliasDTO;
import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.services.IEntidadService;
import com.telefonica.entityservice.models.services.impl.ItemServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Servicios para consulta de Items ", description = " Consulta de Items  con nombre de entidad y valor del alias")

@RequestMapping("/item-api")
public class ItemRestController {

	@Autowired
	private ItemServiceImpl itemService;
	@Autowired
	private IEntidadService entidadService;
	
	@ApiOperation(value = "Obtener una Item por su nombre de entidad y alias", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Se obtuvo el item solicitado"),
			@ApiResponse(code = 404, message = "No existe el item para los valores solicitados") })
	@GetMapping("/items")
	public ResponseEntity<?> findItemsByEntityAndAlias(
			@RequestParam(value="nombreEntidad" , defaultValue = "" )String nombreEntidad,
			@RequestParam(value="nombreAlias" , defaultValue = "" )String nombreAlias){
		
		Optional<?> opt =  itemService.findItemByEntityAndAlias(nombreEntidad,nombreAlias);
		
		
		return opt.get().getClass() == ItemDTO.class? 
				new ResponseEntity<ItemDTO>( (ItemDTO) opt.get() , HttpStatus.OK):
				new ResponseEntity<String>(opt.get().toString(), HttpStatus.NOT_FOUND);
		}

	@ApiOperation(value = "Creacion de un nuevo item", consumes = "application/json", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 201, message = "El item se creo satisfactoriamente"),
			@ApiResponse(code = 400, message = "Error en la estructura json  => Campos con errores") })
	@PostMapping("/item")
	public ResponseEntity<?> createItem(@Valid @RequestBody(required = true) Item item,
			@RequestParam(value = "nombreEntidad", required = true) String nombreEntidad) {
		Map<String, Object> response = new HashMap<String, Object>();

		Optional<EntidadDTO> entidad = Optional.ofNullable(entidadService.findByNombre(nombreEntidad));
		
		if (entidad.isPresent()) {
			
			if (saveItem(entidad.get().getId(), item, createValidator(), createPrepareItem())) {
				response.put("mensaje", "El item fue creado");
				return new ResponseEntity<Map<String, Object>>(response ,HttpStatus.CREATED);
			} else {
				response.put("mensaje", "La entidad ".concat(nombreEntidad).concat(" ya tiene un item con el nombre ").concat(item.getNombreItem()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
			}
			
		} else {
			response.put("mensaje",
					"La entidad " + nombreEntidad + " no existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	}
	
	@ApiOperation(value = "Borrar un item")
	@ApiResponses({ @ApiResponse(code = 200, message = "El item se borro satisfactoriamente"),
			@ApiResponse(code = 404, message = "El item no existe en la base de datos") })
	@DeleteMapping("/item")
	public ResponseEntity<?> deleteItem(@RequestParam(value = "id", required = true) Long id) {
		Map<String, Object> response = new HashMap<String, Object>();

		if (itemService.findItemById(id).isPresent()) {

			itemService.delete(id);
			response.put("mensaje", "El item con el id ".concat(id.toString().concat(" fue eliminado")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);

		} else {
			response.put("mensaje",
					"El item con el id ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Actualizacion de un item", consumes = "application/json", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "El item se actualizo satisfactoriamente"),
	@ApiResponse(code = 400, message = "Error en la estructura json => Campos con errores") })
	@PutMapping("/item")
	public ResponseEntity<?> updateItem(@Valid @RequestBody(required = true) ItemDTO itemModificado,
			@RequestParam(value = "nombreEntidadActual", required = true) String nombreEntidadActual, @RequestParam(value = "nombreItemActual", required = true) String nombreItemActual) {

		Map<String, Object> response = new HashMap<String, Object>();
		Optional<EntidadDTO> entidad = Optional.ofNullable(entidadService.findByNombre(nombreEntidadActual));

		if (!entidad.isPresent()) {
			response.put("mensaje",
					"La entidad ".concat(nombreEntidadActual.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		Optional<Item> item = itemService.findByNombreItemAndEntidad(nombreItemActual, nombreEntidadActual);

		if (!item.isPresent()) {
			response.put("mensaje",
					"El item ".concat(nombreItemActual.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		Optional<Entidad> entidadNueva = Optional.ofNullable(null);
		
		if (!StringUtils.isEmpty(itemModificado.getEntidad())) {
			entidadNueva = entidadService.findByNombreEntidad(itemModificado.getEntidad());
			
			if (!entidadNueva.isPresent()) {
				response.put("mensaje",
						"La entidad ".concat(itemModificado.getEntidad().concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		}

		actualizarCamposEntidad(item, itemModificado, entidadNueva);

		//Evaluamos si dentro de la entidad existe otro item con el mismo nombre del item que se quiere modificar
		if (!existeItemConIgualNombre(item)) {
			itemService.save(item.get());
			response.put("mensaje", "El item ".concat(item.get().getNombreItem().concat(" fue actualizado")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
		} else {
			response.put("mensaje", "La entidad '" + item.get().getEntidad().getNombre() + "' ya tiene un item con el nombre " + itemModificado.getNombreItem());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

	}
	
	@ApiOperation(value = "Agrega uno o mas alias a un item", consumes = "application/json", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "El item se actualizo satisfactoriamente"),
	@ApiResponse(code = 400, message = "Error en la estructura json => Campos con errores") })
	@PutMapping("/agregarAlias")
	public ResponseEntity<?> agregarAlias(@Valid @RequestBody(required = true) Item itemModificado,
			@RequestParam(value = "nombreEntidad", required = true) String nombreEntidad) {

		Map<String, Object> response = new HashMap<String, Object>();
		//List<Item> listItems = itemService.findByNombreItem(itemModificado.getNombreItem());
		Optional<EntidadDTO> entidad = Optional.ofNullable(entidadService.findByNombre(nombreEntidad));
		
		if (!entidad.isPresent()) {
			response.put("mensaje",
					"La entidad ".concat(nombreEntidad.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		Optional<Item> itemViejo = itemService.findByNombreItemAndEntidad(itemModificado.getNombreItem(), nombreEntidad);

		if (itemViejo.isPresent()) {
			
			itemViejo.get().getAlias().addAll(itemModificado.getAlias());
			itemService.save(itemViejo.get());
			response.put("mensaje", "El item ".concat(itemViejo.get().getNombreItem().concat(" fue actualizado")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
			
		} else {
			response.put("mensaje",
					"El item ".concat(itemModificado.getNombreItem().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

	}
	
	@ApiOperation(value = "Elimina uno o mas alias de un item", consumes = "application/json", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "El item se actualizo satisfactoriamente"),
	@ApiResponse(code = 400, message = "Error en la estructura json => Campos con errores") })
	@PutMapping("/eliminarAlias")
	public ResponseEntity<?> eliminarAlias(@Valid @RequestBody(required = true) Item itemModificado,
			@RequestParam(value = "nombreEntidad", required = true) String nombreEntidad) {

		Map<String, Object> response = new HashMap<String, Object>();
		Optional<EntidadDTO> entidad = Optional.ofNullable(entidadService.findByNombre(nombreEntidad));
		
		if (!entidad.isPresent()) {
			response.put("mensaje",
					"La entidad ".concat(nombreEntidad.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		Optional<Item> itemViejo = itemService.findByNombreItemAndEntidad(itemModificado.getNombreItem(), nombreEntidad);

		if (itemViejo.isPresent()) {
			
			itemViejo.get().getAlias().removeAll(itemModificado.getAlias());
			itemService.save(itemViejo.get());
			response.put("mensaje", "El item ".concat(itemViejo.get().getNombreItem().concat(" fue actualizado")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
			
		} else {
			response.put("mensaje",
					"El item ".concat(itemModificado.getNombreItem().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

	}

	@ApiOperation(value = "Actualización de un item con los alias y variables provenientes de otros items. Los alias y variables se unificarán en el primer item de la lista de nombres de items", consumes = "application/json", response = Item.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "El item se actualizó satisfactoriamente") })
	@PutMapping("/unificar")
	public ResponseEntity<?> unificar(@RequestParam(value="nombreEntidad", required = true) String nombreEntidad,
			@RequestBody(required = true) List<String> listItemsNombres) {

		Map<String, Object> response = new HashMap<String, Object>();
		Optional<EntidadDTO> entidad = Optional.ofNullable(entidadService.findByNombre(nombreEntidad));
		
		if (!entidad.isPresent()) {
			response.put("mensaje", "No existe la entidad");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		List<Optional<Item>> listItems = new ArrayList<>();
		//buscamos los items recibidos por parámetro 
		listItemsNombres.forEach(nombreItem -> listItems.add(itemService.findByNombreItemAndEntidad(nombreItem, nombreEntidad)));
		
		//Obtenemos aquellos items no encontrados
		List<Optional<Item>> listInexistentes = listItems.stream().filter(item -> !item.isPresent()).collect(Collectors.toList());
		
		if (listInexistentes.isEmpty()) {
			Optional<Item> item = listItems.get(0);
			
			List<Alias> aliases = Functions.unirListas(item, listItems, funcListAlias());
			item.get().getAlias().clear();
			item.get().getAlias().addAll(aliases);
			
			List<Variable> variables = Functions.unirListas(item, listItems, funcListVariables());
			item.get().getVariables().clear();
			item.get().getVariables().addAll(variables);
			
			itemService.save(item.get());
			
			//Borramos lo items que fueron unificados en el item destino
			listItems.remove(item);
			listItems.forEach(i -> itemService.delete(i.get().getId()));
			
			response.put("mensaje", "Se unificaron los alias en el item ".concat(item.get().getNombreItem()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
		} else {
			response.put("mensaje", "Verifique la existencia de los items");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	}

	private Function2<Item, List<Alias>> funcListAlias() {
		return new Function2<Item, List<Alias>>() {
			@Override
			public List<Alias> perform(Item item) {
				return item.getAlias();
			}
		};
	}
	
	private Function2<Item, List<Variable>> funcListVariables() {
		return new Function2<Item, List<Variable>>() {
			@Override
			public List<Variable> perform(Item item) {
				return item.getVariables();
			}
		};
	}

	public Boolean existeItemConIgualNombre(Optional<Item> itemModificado) {
		List<Item> itemsConIgualNombre = itemService.findByNombreItem(itemModificado.get().getNombreItem());
		
		return itemsConIgualNombre.stream().anyMatch(i -> i.getEntidad().getId().equals(itemModificado.get().getEntidad().getId()) && !itemModificado.get().getId().equals(i.getId()));
	}
		
	private Function1<Long, Item, Boolean> createValidator() {
		return new Function1<Long, Item, Boolean>() {
			@Override
			public Boolean perform(Long entidadId, Item itemInfo) {
				List<Item> itemsConIgualNombre = itemService.findByNombreItem(itemInfo.getNombreItem());
				boolean nombreRepetido = itemsConIgualNombre.stream().anyMatch(i -> entidadId.equals(i.getEntidad().getId()));
				
				return nombreRepetido;
			}
		};
	}
	
	private Function1<Long, Item, Item> createPrepareItem() {
		 return new Function1<Long, Item, Item>() {
			@Override
			public Item perform(Long entidadId, Item itemInfo) {
				
				Item item = new Item();
				item.setAlias(itemInfo.getAlias());
				item.setNombreItem(itemInfo.getNombreItem());
				item.setEntidad(entidadService.findEntidadById(entidadId).get());
				
				return item;
			}
		};
	}
	
	public boolean saveItem(Long id, Item itemModificado, Function1<Long, Item, Boolean> functionVal, Function1<Long, Item, Item> functionPrepare) {
		if (!functionVal.perform(id, itemModificado)) {
			Item itemPrepared = functionPrepare.perform(id, itemModificado);
			itemService.save(itemPrepared);
			return true;
		}
		return false;
	}
	
	public void actualizarCamposEntidad(Optional<Item> itemViejo, ItemDTO itemActualizado, Optional<Entidad> entidadNueva) {
		if (!StringUtils.isEmpty(itemActualizado.getNombreItem())) {
			itemViejo.get().setNombreItem(itemActualizado.getNombreItem());
		}
		
		if (Optional.ofNullable(itemActualizado.getAlias()).isPresent() && !itemActualizado.getAlias().isEmpty()) {
			itemViejo.get().getAlias().clear();
			List<Alias> aliases = new ArrayList<Alias>();
			for (AliasDTO aliasDTO : itemActualizado.getAlias()) {
				aliases.add(new Alias(aliasDTO.getValor()));
			}
			
			itemViejo.get().getAlias().addAll(aliases);

		}
		
		if (!StringUtils.isEmpty(itemActualizado.getEntidad())) {
			itemViejo.get().setEntidad(entidadNueva.get());
		}
	}

}
