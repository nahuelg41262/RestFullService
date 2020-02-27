package com.telefonica.entityservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;
import com.telefonica.entityservice.models.services.impl.ItemServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ItemRestControllerTest {

    @MockBean
    private ItemServiceImpl itemService;
 
    @MockBean
    private EntidadServiceImpl entidadService;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void givenAliasDoesNotExists_whenItemIsRetrieved_then404IsReceived() {
        when((Optional<String>)itemService.findItemByEntityAndAlias("entidad", "alias")).thenReturn(Optional.of("El Alias No existe en la base de datos"));
 
        ResponseEntity<String> itemResponse = restTemplate.getForEntity("/item-api/items/?nombreEntidad=entidad&nombreAlias=alias", String.class);
 
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(itemResponse.getBody()).isEqualTo("El Alias No existe en la base de datos");
    }

    @Test
    public void givenEntidadDoesNotExists_whenItemIsRetrieved_then404IsReceived() {
        when((Optional<String>)itemService.findItemByEntityAndAlias("entidad2", "alias2")).thenReturn(Optional.of("La entidad No existe en la base de datos"));
 
        ResponseEntity<String> itemResponse = restTemplate.getForEntity("/item-api/items/?nombreEntidad=entidad2&nombreAlias=alias2", String.class);
 
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(itemResponse.getBody()).isEqualTo("La entidad No existe en la base de datos");
    }
    
    @Test
    public void givenEntidadAndAlias_whenItemIsRetrieved_then200IsReceived() {
    	ItemDTO itemDto = new ItemDTO();
    	itemDto.setNombreItem("itemEncontrado");
    	
        when((Optional<ItemDTO>)itemService.findItemByEntityAndAlias("entidad", "alias")).thenReturn(Optional.of(itemDto));
 
        ResponseEntity<ItemDTO> itemResponse = restTemplate.getForEntity("/item-api/items/?nombreEntidad=entidad&nombreAlias=alias", ItemDTO.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(itemResponse.getBody().getNombreItem()).isEqualTo("itemEncontrado");
    }

    @Test
    public void givenEntidadAndAlias_whenItemIsRetrieved_thenJsonMimeTypeIsReceived() {
    	
    	String jsonMimeType = "application/json";
    	ItemDTO itemDto = new ItemDTO();
    	itemDto.setNombreItem("itemEncontrado");
    	
        when((Optional<ItemDTO>)itemService.findItemByEntityAndAlias("entidad", "alias")).thenReturn(Optional.of(itemDto));
        
        ResponseEntity<ItemDTO> itemResponse = restTemplate.getForEntity("/item-api/items/?nombreEntidad=entidad&nombreAlias=alias", ItemDTO.class);
        
        assertThat(itemResponse.getHeaders().getContentType().toString()).isEqualTo(jsonMimeType);
        
    }
    
    @Test
    public void givenItemAndNombreEntidad_whenItemIsCreated_then200IsReceived() {
    	Item item = new Item();
    	item.setNombreItem("itemNuevo");
    	item.setAlias(new ArrayList<Alias>());
    	
    	EntidadDTO entidadDto = new EntidadDTO();
    	entidadDto.setId(1L);
    	
    	Entidad entidad = new Entidad();
    	entidad.setId(1L);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(entidadDto);
    	
    	when((List<Item>)itemService.findByNombreItem("itemNuevo")).thenReturn(new ArrayList<Item>());
    	
        when((Optional<Entidad>)entidadService.findEntidadById(1L)).thenReturn(Optional.of(entidad));
 
        ResponseEntity<String> itemResponse = restTemplate.postForEntity("/item-api/item/?nombreEntidad=entidad", item, String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item fue creado\"}");
    }
    
    @Test
    public void givenNombreItemRepeatedAndNombreEntidad_whenItemIsCreated_then409IsReceived() {
    	Item item = new Item();
    	item.setNombreItem("itemNuevo");
    	item.setAlias(new ArrayList<Alias>());
    	
    	EntidadDTO entidadDto = new EntidadDTO();
    	entidadDto.setId(1L);
    	
    	Entidad entidad = new Entidad();
    	entidad.setId(1L);
    	
    	List<Item> items = new ArrayList<Item>();
    	Item itemRepeated = new Item();
    	itemRepeated.setNombreItem("itemNuevo");
    	itemRepeated.setEntidad(entidad);
    	items.add(itemRepeated);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(entidadDto);
    	
    	when((List<Item>)itemService.findByNombreItem("itemNuevo")).thenReturn(items);
    	
        when((Optional<Entidad>)entidadService.findEntidadById(1L)).thenReturn(Optional.of(entidad));
 
        ResponseEntity<String> itemResponse = restTemplate.postForEntity("/item-api/item/?nombreEntidad=entidad", item, String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"La entidad entidad ya tiene un item con el nombre itemNuevo\"}");
    }

    @Test
    public void givenNombreItemRepeatedIntoDiferentEntidad_whenItemIsCreated_then200IsReceived() {
    	Item item = new Item();
    	item.setNombreItem("itemNuevo");
    	item.setAlias(new ArrayList<Alias>());
    	
    	EntidadDTO entidadDto = new EntidadDTO();
    	entidadDto.setId(1L);
    	
    	Entidad entidad = new Entidad();
    	entidad.setId(1L);
    	
    	Entidad entidad2 = new Entidad();
    	entidad2.setId(2L);
    	
    	List<Item> items = new ArrayList<Item>();
    	Item itemRepeated = new Item();
    	itemRepeated.setNombreItem("itemNuevo");
    	itemRepeated.setEntidad(entidad2);
    	items.add(itemRepeated);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(entidadDto);
    	
    	when((List<Item>)itemService.findByNombreItem("itemNuevo")).thenReturn(items);
    	
        when((Optional<Entidad>)entidadService.findEntidadById(1L)).thenReturn(Optional.of(entidad));

        ResponseEntity<String> itemResponse = restTemplate.postForEntity("/item-api/item/?nombreEntidad=entidad", item, String.class);

        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item fue creado\"}");
    }
    
    @Test
    public void givenNombreItemAndEntidadDoesNotExists_whenItemIsCreated_then404IsReceived() {
    	Item item = new Item();
    	item.setNombreItem("itemNuevo");
    	item.setAlias(new ArrayList<Alias>());
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(null);
    	
        ResponseEntity<String> itemResponse = restTemplate.postForEntity("/item-api/item/?nombreEntidad=entidad", item, String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"La entidad entidad no existe\"}");
    }
    
    @Test
    public void givenIdItem_whenItemIsDeleted_thenDeleteMethodIsCalled() {
    	when(itemService.findItemById(1L)).thenReturn(Optional.of(new Item()));
    	
        restTemplate.delete("/item-api/item/?id=1");
        
        verify(itemService, times(1)).delete(1L);
    }
    
    @Test
    public void givenIdItem_whenItemIsDeleted_thenDeleteMethodIsNotCalled() {
    	when(itemService.findItemById(1L)).thenReturn(Optional.ofNullable(null));
    	
        restTemplate.delete("/item-api/item/?id=1");
        
        verify(itemService, never()).delete(1L);
    }

    @Test
    public void givenNombreItemAndNombreEntidad_whenItemChangeName_then202IsReceived() {
    	ItemDTO item = new ItemDTO();
    	item.setNombreItem("itemModificado");

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(item , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("item", "entidad")).thenReturn(Optional.of(new Item()));
    	when(itemService.findByNombreItem("itemModificado")).thenReturn(new ArrayList<Item>());
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/item/?nombreEntidadActual=entidad&nombreItemActual=item" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item itemModificado fue actualizado\"}");
    }

    @Test
    public void givenNombreItemRepeatedAndNombreEntidad_whenItemIsUpdated_then409IsReceived() {
    	ItemDTO itemDTO = new ItemDTO();
    	itemDTO.setNombreItem("itemModificado");
    	
    	Entidad entidad = new Entidad();
    	entidad.setNombre("entidad");
    	entidad.setId(1L);
    	
    	Item item = new Item();
    	item.setId(1L);
    	item.setEntidad(entidad);

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(itemDTO , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("item", "entidad")).thenReturn(Optional.of(item));
    	
    	List<Item> items = new ArrayList<Item>();
    	Item item2 = new Item();
    	item2.setId(2L);
    	item2.setEntidad(entidad);

    	items.add(item2);
    	when(itemService.findByNombreItem("itemModificado")).thenReturn(items);
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/item/?nombreEntidadActual=entidad&nombreItemActual=item" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"La entidad 'entidad' ya tiene un item con el nombre itemModificado\"}");
    }

    @Test
    public void givenNombreItemAndNombreEntidad_whenItemIsUpdatedAndNombreItemIsRepeatedIntoDifferentEntidad_then202IsReceived() {
    	ItemDTO itemDTO = new ItemDTO();
    	itemDTO.setNombreItem("itemModificado");
    	
    	Entidad entidad = new Entidad();
    	entidad.setNombre("entidad");
    	entidad.setId(1L);
    	
    	Item item = new Item();
    	item.setId(1L);
    	item.setEntidad(entidad);

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(itemDTO , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("item", "entidad")).thenReturn(Optional.of(item));
    	
    	List<Item> items = new ArrayList<Item>();
    	Item item2 = new Item();
    	
    	Entidad entidad2 = new Entidad();
    	entidad2.setNombre("entidad2");
    	entidad2.setId(2L);

    	item2.setId(2L);
    	item2.setEntidad(entidad2);

    	items.add(item2);
    	when(itemService.findByNombreItem("itemModificado")).thenReturn(items);
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/item/?nombreEntidadActual=entidad&nombreItemActual=item" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item itemModificado fue actualizado\"}");
    }

    @Test
    public void givenNombreItemAndNombreEntidad_whenItemIsUpdatedAndChangeNameEntidad_then202IsReceived() {
    	ItemDTO itemDTO = new ItemDTO();
    	itemDTO.setNombreItem("itemModificado");
    	itemDTO.setEntidad("entidadModificada");
    	
    	Entidad entidad = new Entidad();
    	entidad.setNombre("entidad");
    	entidad.setId(1L);
    	
    	Item item = new Item();
    	item.setId(1L);
    	item.setEntidad(entidad);
    	
    	Entidad entidad2 = new Entidad();
    	entidad2.setNombre("entidadModificada");
    	entidad2.setId(2L);

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(itemDTO , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("item", "entidad")).thenReturn(Optional.of(item));
    	when(entidadService.findByNombreEntidad("entidadModificada")).thenReturn(Optional.of(entidad2));
    	
    	List<Item> items = new ArrayList<Item>();
	
    	when(itemService.findByNombreItem("itemModificado")).thenReturn(items);
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/item/?nombreEntidadActual=entidad&nombreItemActual=item" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item itemModificado fue actualizado\"}");
    }
    
    @Test
    public void givenNombreEntidadAndNombreItemAndAlias_whenAddNewAlias_then202IsReceived() {
    	Item itemRequest = new Item();
    	List<Alias> aliasesRequest = new ArrayList<>();
    	Alias aliasRequest = new Alias();
    	aliasRequest.setValor("AliasTest");
    	aliasesRequest.add(aliasRequest);
    	itemRequest.setNombreItem("itemTest");
    	itemRequest.setAlias(aliasesRequest);
    	
    	
    	Item item = new Item();
    	List<Alias> aliases = new ArrayList<>();

    	item.setNombreItem("itemTest");
    	item.setAlias(aliases);

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(itemRequest , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("itemTest", "entidad")).thenReturn(Optional.of(item));
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/agregarAlias/?nombreEntidad=entidad" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(item.getAlias().size()).isEqualTo(1L);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item itemTest fue actualizado\"}");
    }

    @Test
    public void givenNombreEntidadAndNombreItemAndAlias_whenDeleteAlias_then202IsReceived() {
    	Item itemRequest = new Item();
    	List<Alias> aliasesRequest = new ArrayList<>();
    	Alias aliasRequest = new Alias();
    	aliasRequest.setValor("AliasTest");
    	aliasesRequest.add(aliasRequest);
    	itemRequest.setNombreItem("itemTest");
    	itemRequest.setAlias(aliasesRequest);
    	
    	
    	Item item = new Item();
    	List<Alias> aliases = new ArrayList<>();
    	Alias alias = new Alias();
    	alias.setValor("AliasTest");
    	aliases.add(alias);
    	
    	item.setNombreItem("itemTest");
    	item.setAlias(aliases);

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(itemRequest , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("itemTest", "entidad")).thenReturn(Optional.of(item));
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/eliminarAlias/?nombreEntidad=entidad" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(item.getAlias().isEmpty());
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"El item itemTest fue actualizado\"}");
    }

    @Test
    public void givenNombreEntidadAndNombresItemsAlias_whenUnificamosAliasYVariables_then202IsReceived() {
    	Item item1 = new Item();
    	List<Alias> aliases1 = new ArrayList<>();
    	Alias alias1 = new Alias();
    	alias1.setValor("AliasTest1");
    	aliases1.add(alias1);
    	
    	item1.setId(1L);
    	item1.setNombreItem("itemTest1");
    	item1.setAlias(aliases1);
    	item1.setVariables(new ArrayList<>());
    	
    	Item item2 = new Item();
    	List<Alias> aliases2 = new ArrayList<>();
    	Alias alias2 = new Alias();
    	alias2.setValor("AliasTest2");
    	aliases2.add(alias2);
    	
    	item2.setId(2L);
    	item2.setNombreItem("itemTest2");
    	item2.setAlias(aliases2);
    	item2.setVariables(new ArrayList<>());
    	
    	List<String> nombreItems = List.of("itemTest1", "itemTest2");

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    	HttpEntity<?> itemReq =new HttpEntity<>(nombreItems , headers);
    	
    	when((EntidadDTO)entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
    	when(itemService.findByNombreItemAndEntidad("itemTest1", "entidad")).thenReturn(Optional.of(item1));
    	when(itemService.findByNombreItemAndEntidad("itemTest2", "entidad")).thenReturn(Optional.of(item2));
    	
    	ResponseEntity<String> itemResponse = restTemplate.exchange("/item-api/unificar/?nombreEntidad=entidad" , HttpMethod.PUT , itemReq , String.class);
  
        assertThat(itemResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(item1.getAlias().size()).isEqualTo(2L);
        assertThat(itemResponse.getBody()).isEqualTo("{\"mensaje\":\"Se unificaron los alias en el item itemTest1\"}");
    }

}
