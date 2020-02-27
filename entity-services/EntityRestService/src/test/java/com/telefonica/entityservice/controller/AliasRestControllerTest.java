package com.telefonica.entityservice.controller;


import com.telefonica.entityservice.models.dtos.AliasResquestDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.services.AliasService;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;
import com.telefonica.entityservice.models.services.impl.ItemServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AliasRestControllerTest {

    @MockBean
    AliasService aliasService;
    @MockBean
    EntidadServiceImpl entidadService;
    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void givenItemsWithAlias_whenVariablesIsRetrived_then200AndAliasIsReceived() {
        Entidad entidad = new Entidad();
        entidad.setItems(
                List.of(new Item(1L, "item.test", List.of(new Variable()), List.of(new Alias("valor.test1"), new Alias("valor.test2"))),
                        new Item(2L, "item.test4", List.of(new Variable()), List.of(new Alias("valor.test3"))),
                        new Item(3L, "item.test8", List.of(new Variable()), List.of(new Alias("valor.test4"))))
        );

        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));

        ResponseEntity<?> aliasList =
                restTemplate.getForEntity("/alias-api/aliasPorNombreDeEntidadYNombreDeItem?nombreEntidad=entidad.test&nombreItem=item.test", new ArrayList<Alias>().getClass());

        assertThat(aliasList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(aliasList.getBody().toString()).isEqualTo("[{id=null, valor=valor.test1}, {id=null, valor=valor.test2}]");

    }

    @Test
    public void givenEntidadDoesNotExists_whenVariablesIsRetrived_then404AndErrorMessageIsReceived() {

        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.empty());

        ResponseEntity<?> aliasList =
                restTemplate.getForEntity("/alias-api/aliasPorNombreDeEntidadYNombreDeItem?nombreEntidad=entidad.test&nombreItem=item.test", new HashMap<String, String>().getClass());

        assertThat(aliasList.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(aliasList.getBody().toString()).isEqualTo("{Error : ={error 2=No existe la entidad con el nombre entidad.test}}");
    }

    @Test
    public void givenItemsDoesNotExists_whenVariablesIsRetrived_then404AndErrorMessageIsReceived() {
        Entidad entidad = new Entidad();
        entidad.setItems(
                List.of(new Item(1L, "item.test", List.of(new Variable()), List.of(new Alias("valor.test1"), new Alias("valor.test2")))));

        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));

        ResponseEntity<?> aliasList =
                restTemplate.getForEntity("/alias-api/aliasPorNombreDeEntidadYNombreDeItem?nombreEntidad=entidad.test&nombreItem=item.test.45879", new HashMap<String, String>().getClass());

        assertThat(aliasList.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(aliasList.getBody().toString()).isEqualTo("{Error : ={error 1=No existe el item con el nombre item.test.45879 en la entidad entidad.test}}");
    }

    @Test
    public void givenEntidadAndItem_WhenCreateAlias_then201AndMessageIsRecived() {
        Item item = new Item(1L, "item.test", List.of(new Variable()), List.of(new Alias("valor.test1"), new Alias("valor.test2")));

        List<Alias> aliases = new ArrayList<>();
        aliases.add(new Alias("valor.test1"));
        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(
                List.of(item, new Item(1L, "item.test2", aliases)));

        doNothing().when(itemService).save(item);
        when(entidadService.findByNombreEntidad(entidad.getNombre())).thenReturn(Optional.of(entidad));
        Alias alias1 = new Alias();
        alias1.setValor("valor.test.insertado");

        ResponseEntity<?> aliasResponse =
                restTemplate.postForEntity("/alias-api/alias?nombreEntidad=entidad.test&nombreItem=item.test2", List.of(alias1), new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Creado=Los alias fueron creados }");
    }

    @Test
    public void givenEntidadDoesNotExists_WhenCreateAlias_then404AndErrorMessageIsRecived() {
        Item item = new Item(1L, "item.test", List.of(new Variable()), List.of(new Alias("valor.test1"), new Alias("valor.test2")));

        List<Alias> aliases = new ArrayList<>();
        aliases.add(new Alias("valor.test1"));
        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(
                List.of(item, new Item(1L, "item.test2", aliases)));

        doNothing().when(itemService).save(item);
        when(entidadService.findByNombreEntidad(entidad.getNombre())).thenReturn(Optional.empty());
        Alias alias1 = new Alias();
        alias1.setValor("valor.test.insertado");

        ResponseEntity<?> aliasResponse =
                restTemplate.postForEntity("/alias-api/alias?nombreEntidad=entidad.test&nombreItem=item.test2", List.of(alias1), new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Error : ={error 2=No existe la entidad con el nombre entidad.test}}");
    }

    @Test
    public void givenItemDoesNotExists_WhenCreateAlias_then404AndErrorMessageIsRecived() {
        Item item = new Item(1L, "item.test", List.of(new Variable()), List.of(new Alias("valor.test1"), new Alias("valor.test2")));

        List<Alias> aliases = new ArrayList<>();
        aliases.add(new Alias("valor.test1"));
        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(List.of());

        doNothing().when(itemService).save(item);
        when(entidadService.findByNombreEntidad(entidad.getNombre())).thenReturn(Optional.of(entidad));
        Alias alias1 = new Alias();
        alias1.setValor("valor.test.insertado");

        ResponseEntity<?> aliasResponse =
                restTemplate.postForEntity("/alias-api/alias?nombreEntidad=entidad.test&nombreItem=item.test2", List.of(alias1), new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Error : ={error 1=No existe el item con el nombre item.test2 en la entidad entidad.test}}");
    }

    @Test
    public void givenAlias_WhenDelete_200AndMessageIsReceived() {
        Alias alias = new Alias();
        alias.setValor("valor1");
        alias.setItem(new Item("item.test", List.of(alias, new Alias())));
        Alias alias2 = new Alias();
        alias2.setItem(new Item("item.test", List.of(alias2, new Alias())));
        alias2.setValor("valor2");


        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        when(aliasService.findById(2L)).thenReturn(Optional.of(alias2));
        doNothing().when(aliasService).delete(alias);
        doNothing().when(aliasService).delete(alias2);


        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(1L, 2L), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.DELETE, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Eliminado=Los / el alias fueron eliminado}");
    }

    @Test
    public void givenItemWithOneAlias_WhenDelete_then200AndMessageIsReceivedAndSaveIsinvoked() {
        Alias alias = new Alias();
        alias.setValor("valor1");
        alias.setItem(new Item("item.test", List.of(alias, new Alias())));
        Alias alias2 = new Alias();
        //Este Item deberia tenerse a si mismo como su propio alias
        Item item = new Item("item.test", List.of(alias2));
        alias2.setItem(item);
        alias2.setValor("valor2");


        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        when(aliasService.findById(2L)).thenReturn(Optional.of(alias2));
        doNothing().when(aliasService).delete(alias);
        doNothing().when(aliasService).delete(alias2);


        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(1L, 2L), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.DELETE, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(aliasService , times(1)).save("item.test", alias2.getItem() );
      assertThat(aliasResponse.getBody().toString()).isEqualTo("{Eliminado=Los / el alias fueron eliminado}");
    }

    @Test
    public void givenAliasDoesNotExists_WhenDelete_404AndErrorsMessageIsReceived() {

        when(aliasService.findById(1L)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(1L), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.DELETE, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Error : ={error 1=No existe alias con el id : 1}}");
    }

    @Test
    public void givenEmpyIdInRequest_whenUpdateVariable_then400AndMessageErrorIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(null ,"valor.test", null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Actualizados : ={}, Errores : ={Error 1= El campo ID es obligatorio, la variable no fue actualizada }}");
    }

    @Test
    public void givenAliasDoesNotExists_whenUpdateVariable_then400AndMessageErrorIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", null, null);

        when(aliasService.findById(1L)).thenReturn(Optional.empty());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Actualizados : ={}, Errores : ={Error 1=No existe el alias con el id 1}}");
    }

    @Test
    public void givenAliasResquestDTOWithOutNombres_whenUpdateVariable_then200AndMessageIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", null, null);
        Alias alias = new Alias();
        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        doNothing().when(aliasService).save(alias);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(alias.getValor()).isEqualTo("valor.test");
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Creado 1=La variable con el id 1 fue actualizada}");
    }

    @Test
    public void givenAliasResquestDTOWithNombreItemAndWithOutNombreEntidad_whenUpdateVariable_then400AndMessageIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", "item.test", null);
        Alias alias = new Alias();
        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        doNothing().when(aliasService).save(alias);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Actualizados : ={Creado 1=La variable con el id 1 fue actualizada}, Errores : ={Error 1=Para cambiar la referencia del item es necesario indicar el nombre de la entidad . La referencia al item para el alias nullno fue actualizada}}"
);
    }

    @Test
    public void givenEntidadDoesNotExists_whenUpdateVariable_then400AndMessageIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", "item.test", "entidad.test");
        Alias alias = new Alias();
        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.empty());
        doNothing().when(aliasService).save(alias);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Actualizados : ={Creado 1=La variable con el id 1 fue actualizada}, Errores : ={Error 1=La entidad no existe en la base de datos , la referencia al item para el aliasnullno fue actualizada}}");
    }

    @Test
    public void givenItemDoesNotExistsInEntidad_whenUpdateVariable_then400AndMessageIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", "item.test", "entidad.test");
        Alias alias = new Alias();
        Entidad entidad =  new Entidad("entidad.test", List.of(new Item("item.noEncontrado" , List.of(new Alias()))));
        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        doNothing().when(aliasService).save(alias);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Actualizados : ={Creado 1=La variable con el id 1 fue actualizada}, Errores : ={Error 1=El item no existe en la entidad indicada , la referencia al item para el aliasnull no fue actualizada}}");
    }

    @Test
    public void givenEntidadAndItemAndVariable_whenUpdateVariable_then200AndMessageIsReceived(){
        AliasResquestDTO aliasRequest = new AliasResquestDTO(1L ,"valor.test", "item.test", "entidad.test");
        Alias alias = new Alias();
        Entidad entidad =  new Entidad("entidad.test", List.of(new Item("item.test" , List.of(new Alias()))));
        when(aliasService.findById(1L)).thenReturn(Optional.of(alias));
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        doNothing().when(aliasService).save(alias);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(List.of(aliasRequest), headers);
        ResponseEntity<?> aliasResponse = restTemplate.exchange("/alias-api/alias", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(aliasResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(alias.getValor()).isEqualTo("valor.test");
        assertThat(alias.getItem().getNombreItem()).isEqualTo("item.test");
        assertThat(aliasResponse.getBody().toString()).isEqualTo("{Creado 1=La variable con el id 1 fue actualizada}");
    }
}
