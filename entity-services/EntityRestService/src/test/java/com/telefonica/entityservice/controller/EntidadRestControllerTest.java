package com.telefonica.entityservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.telefonica.entityservice.controller.EntidadRestController;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EntidadRestControllerTest{

	   @MockBean
	    private EntidadServiceImpl entidadService;
	 
	    @Autowired
	    private TestRestTemplate restTemplate;

	    
	    @Test
	    public void givenEntidad_whenEntidadesIsRetrived_then200IsReceived() {
	    	
	    	EntidadDTO entidad = new EntidadDTO();
	    	entidad.setId(1L);
	    	entidad.setNombreEntidad("entity.test");
	    	entidad.setItems(new ArrayList<ItemDTO>());
	    	List<EntidadDTO> listaEntidades =new LinkedList<EntidadDTO>();
	    	listaEntidades.add(entidad);
	    	when(entidadService.findAll()).thenReturn(listaEntidades);
	    	
	    	ResponseEntity<List<EntidadDTO>> entidadResponse = (ResponseEntity<List<EntidadDTO>>) restTemplate.getForEntity("/entity-api/entidades", listaEntidades.getClass());
	    	
	    	
	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(entidadResponse.getBody().isEmpty() != true );

	    	
	    }
	    @Test
	    public void givenEntidadDoesNotExists_whenEntidadesIsRetrived_then404IsReceived() {
	    	
	    	List<EntidadDTO> listaEntidades =new ArrayList<EntidadDTO>();
	    	when(entidadService.findAll()).thenReturn(null);
	    	
	    	ResponseEntity<String> entidadResponse = restTemplate.getForEntity("/entity-api/entidades", String.class);
	    	String compare = "\"{\"mensaje\":\"No existen entidades en la base de datos\"}\"";
	    			
	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	        assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));
	        
	    }
	    @Test 
	    public void givenNombreEntidadDoesNotExists_whenEntidadIsCreated_Then201IsReceived() {
	    	Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
	    	
	    	when(entidadService.findByNombre(entidad.getNombre())).thenReturn(null);
	    	doNothing().when(entidadService).save(entidad);
	    	
	    	ResponseEntity<String> entidadResponse = restTemplate.postForEntity("/entity-api/entidad", entidad, String.class);
	    	String compare = "\"{\"mensaje\":\"La entidad fue creada\"}\"";
	    	
	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));
	    	
	    }
	    @Test 
	    public void givenNombreEntidadDoesExists_whenEntidadIsCreated_Then409IsReceived() {
	    	Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
	    	EntidadDTO entidadDto = new EntidadDTO();
	    	entidadDto.setNombreEntidad("entity.test");
	    	
	    	when(entidadService.findByNombre(entidad.getNombre())).thenReturn(entidadDto);
	    	
	    	ResponseEntity<String> entidadResponse = restTemplate.postForEntity("/entity-api/entidad", entidad, String.class);
	    	String compare = "\"{\"mensaje\":\"Ya existe una entidad con el nombre indicado\"}\"";

	    	//saca las comillas de mas
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));
	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	    	
	    }
	    @Test
	    public void givenEntidadDoesExists_whenEntidadIsDelete_then202IsReceived() {
	    	EntidadDTO entidadDto = new EntidadDTO();
	    	entidadDto.setNombreEntidad("entity.test");
	    	
	    	when(entidadService.findById(1L)).thenReturn(entidadDto);
	    	doNothing().when(entidadService).delete(1L);
	    	
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	    	HttpEntity<?> entity =new HttpEntity<>(headers);
	    	ResponseEntity<String> entidadResponse = restTemplate.exchange("/entity-api/entidad?id=1" , HttpMethod.DELETE , entity , String.class);
	    	
	    	String compare = "\"{\"mensaje\":\"La entidad con el id 1 fue eliminada\"}\"";

	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));

	    }
	    @Test
	    public void givenEntidadDoesNotExists_whenEntidadIsDelete_then404IsReceived() {
	    	EntidadDTO entidadDto = new EntidadDTO();
	    	entidadDto.setNombreEntidad("entity.test");
	    	
	    	when(entidadService.findById(1L)).thenReturn(null);
	    	doNothing().when(entidadService).delete(1L);
	    	
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	    	HttpEntity<?> entity =new HttpEntity<>(headers);
	    	ResponseEntity<String> entidadResponse = restTemplate.exchange("/entity-api/entidad?id=1" , HttpMethod.DELETE , entity , String.class);
	    	
	    	String compare = "\"{\"mensaje\":\"La entidad con el id 1 no existe en la base de datos\"}\"";

	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));

	    }
	    @Test
	    public void givenEntidadDoesExistAndEntidadNombreDoesNotExist_whenEntidadIsUpdate_then202IsReceived() {
    		Entidad entidad = new Entidad(1L,"entity.test", new ArrayList<Item>(Arrays.asList(new Item("item.test" , new ArrayList<Alias>(Arrays.asList(new Alias("alias.test")))))));
    		
    		when(entidadService.findEntidadById(1L)).thenReturn(Optional.of(entidad));
	    	when(entidadService.findByNombre("entity.test")).thenReturn(null);
	    	doNothing().when(entidadService).save(entidad);
	    	
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	    	HttpEntity<?> entity =new HttpEntity<>(entidad , headers);
	    	ResponseEntity<String> entidadResponse = restTemplate.exchange("/entity-api/entidad?id=1" , HttpMethod.PUT , entity , String.class);
	    	
	    	String compare = "\"{\"mensaje\":\"La entidad con el id 1 fue actualizada\"}\"";

	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));

	    }
	    @Test 
	    public void givenEntidadDoesExistAndEntidadNombreDoesExistAndIdsAreEquals_whenENtidadIsUpdate_then202IsReecived() {
	    	
	    	Entidad entidad = new Entidad(1L,"entity.test", new ArrayList<Item>(Arrays.asList(new Item("item.test" , new ArrayList<Alias>(Arrays.asList(new Alias("alias.test")))))));
	    	EntidadDTO entidadPorNombre = new EntidadDTO();
	    	entidadPorNombre.setId(entidad.getId());
	    	entidadPorNombre.setNombreEntidad(entidad.getNombre());
    		when(entidadService.findEntidadById(1L)).thenReturn(Optional.of(entidad));
	    	when(entidadService.findByNombre("entity.test")).thenReturn(entidadPorNombre);
	    	
	    	doNothing().when(entidadService).save(entidad);
	    	
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	    	
	    	HttpEntity<?> entity =new HttpEntity<>(entidad , headers);
	    
	    	ResponseEntity<String> entidadResponse = restTemplate.exchange("/entity-api/entidad?id=1" , HttpMethod.PUT , entity , String.class);
	    	String compare = "\"{\"mensaje\":\"La entidad con el id 1 fue actualizada\"}\"";

	    	
	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));
	    	
	    }
	    @Test
	    	public void givenEntidadDoesExistAndEntidadNombreDoesExistAndIdsAreNotEquals_whenENtidadIsUpdate_then409IsReecived() {
	    	Entidad entidad = new Entidad(1L,"entity.test", new ArrayList<Item>(Arrays.asList(new Item("item.test" , new ArrayList<Alias>(Arrays.asList(new Alias("alias.test")))))));

	    	when(entidadService.findEntidadById(1L)).thenReturn(Optional.empty());
	    	
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	    	HttpEntity<?> entity =new HttpEntity<>(entidad , headers);
	    	ResponseEntity<String> entidadResponse = restTemplate.exchange("/entity-api/entidad?id=1" , HttpMethod.PUT , entity , String.class);
	    	
	    	String compare = "\"{\"mensaje\":\"La entidad con el id 1 no existe en la base de datos\"}\"";

	    	assertThat(entidadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    	assertThat(entidadResponse.getBody()).isEqualTo(compare.substring(1,compare.length()-1));
	    	
	    }
	    
	    
}
