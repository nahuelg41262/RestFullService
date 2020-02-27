package com.telefonica.entityservice.controller;


import com.telefonica.entityservice.models.dtos.*;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;
import com.telefonica.entityservice.models.services.impl.ItemServiceImpl;
import com.telefonica.entityservice.models.services.impl.VariableServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VariableRestControllerTest {
    @MockBean
    VariableServiceImpl variableService;

    @MockBean
    EntidadServiceImpl entidadService;
    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void givenVariable_WhenVariableIsretrived_then200AndVariableIsReceived() {
        Variable variable = new Variable();
        variable.setIdItem(new Item());
        variable.setId(1L);
        variable.setIdContrato(7L);
        variable.setValor("variable.test");
        List<Variable> variables = new ArrayList<>();
        variables.add(variable);
        when(variableService.findByIdContrato(1L)).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<List<Variable>> varieableResponse = (ResponseEntity<List<Variable>>)
                restTemplate.getForEntity("/variable-api/variables?idDeContrato=1", variables.getClass());

        String compare = "[{id=1, idContrato=7, valor=variable.test}]";

        assertThat(varieableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(varieableResponse.getBody().toString()).isEqualTo(compare);

    }

    @Test
    public void givenVariableDoesNotExists_WhenVariableIsretrived_then404AIsReceived() {
        //given
        List<Variable> variables = new ArrayList<>();
        //when
        when(variableService.findByIdContrato(2L)).thenReturn(Optional.of(variables));
        ResponseEntity<HashMap<String, String>> varieableResponse = (ResponseEntity<HashMap<String, String>>)
                restTemplate.getForEntity("/variable-api/variables?idDeContrato=2", new HashMap<String, String>().getClass());

        assertThat(varieableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varieableResponse.getBody().containsValue("no existen variables para el contrato con el id 2")).isEqualTo(true);
    }

    @Test
    public void givenVariable_WhenVariableIsretrived_then200AndVariblesIsReceived() {
        //given
        Variable variable = new Variable();
        variable.setIdItem(new Item());
        variable.setId(1L);
        variable.setIdContrato(7L);
        variable.setValor("variable.test");

        Variable variable2 = new Variable();
        variable2.setIdItem(new Item());
        variable2.setId(1L);
        variable2.setIdContrato(7L);
        variable2.setValor("variable2.test");
        List<Variable> variables = new ArrayList<>();
        variables.add(variable);
        variables.add(variable2);
        EntidadDTO entidadDTO = new EntidadDTO();
        entidadDTO.setItems(List.of(new ItemDTO()));

        //when
        when(entidadService.findByNombre("entity.test")).thenReturn(entidadDTO);
        when(variableService.findByEntidad("entity.test")).thenReturn(Optional.of(variables));
        ResponseEntity<List<Variable>> varieableResponse = (ResponseEntity<List<Variable>>)
                restTemplate.getForEntity("/variable-api/variablesPorNombreEntidad?nombreDeEntidad=entity.test", variables.getClass());

        //then
        assertThat(varieableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(varieableResponse.getBody().size()).isEqualTo(2);
        assertThat(varieableResponse.getBody().size()).isEqualTo(2);
        assertThat(varieableResponse.getBody().toString()).isEqualTo("[{id=1, idContrato=7, valor=variable.test}, {id=1, idContrato=7, valor=variable2.test}]");

    }

    @Test
    public void givenEntidadDoesNotExists_WhenVariableIsretrived_then404AIsReceived() {
        //when
        ResponseEntity<HashMap<String, String>> varieableResponse = (ResponseEntity<HashMap<String, String>>)
                restTemplate.getForEntity("/variable-api/variablesPorNombreEntidad?nombreDeEntidad=entity.test", new HashMap<String, String>().getClass());

        assertThat(varieableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varieableResponse.getBody().containsValue("No existe la entidad con el nombre entity.test en la base de datos .")).isEqualTo(true);
        assertThat(varieableResponse.getBody().size()).isEqualTo(1);
    }

    @Test
    public void givenItemsDoesNotExists_WhenVariableIsretrived_then404AIsReceived() {
        //given
        EntidadDTO entidadDTO = new EntidadDTO();

        //when
        when(entidadService.findByNombre("entity.test")).thenReturn(entidadDTO);
        ResponseEntity<HashMap<String, String>> varieableResponse = (ResponseEntity<HashMap<String, String>>)
                restTemplate.getForEntity("/variable-api/variablesPorNombreEntidad?nombreDeEntidad=entity.test", new HashMap<String, String>().getClass());

        assertThat(varieableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varieableResponse.getBody().containsValue("No existen items asociados para la entidad con el nombre entity.test")).isEqualTo(true);
        assertThat(varieableResponse.getBody().size()).isEqualTo(1);
    }

    @Test
    public void givenEntidadDoesNotExists_WhenVariablesResquestDTOIsCreated_then400AndErrorsIsRetrived() {
        //given
        VariableResquestDTO varaible = new VariableResquestDTO();
        varaible.setNombreDeEntidad("entidad.test");
        varaible.setValor("valor.test");
        VariablesRequestDTO variablesPOST = new VariablesRequestDTO();
        variablesPOST.setIdDeContrato(8L);
        variablesPOST.setListaDeVariables(List.of(varaible));
        //when
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.empty());
        Map<String, Map<String, Object>> res = new HashMap<>();

        ResponseEntity<Map<String, Map<String, Object>>> varariableResponse = (ResponseEntity<Map<String, Map<String, Object>>>) restTemplate.postForEntity("/variable-api/variables", List.of(variablesPOST), res.getClass());
        //then
        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Variables Insertadas ={}, Variables con Errores={error 1=no existe la entidad con el id ingresado Entidad : entidad.test}}");
    }

    @Test
    public void givenEntidadDoesntHaveItem_WhenVariablesResquestDTOIsCreated_then400AndErrorsIsRetrived() {
        //given
        VariableResquestDTO varaible = new VariableResquestDTO();
        varaible.setNombreDeEntidad("entidad.test");
        varaible.setValor("valor.test");
        varaible.setNombreDeItem("item.test");
        VariablesRequestDTO variablesPOST = new VariablesRequestDTO();
        variablesPOST.setIdDeContrato(8L);
        variablesPOST.setListaDeVariables(List.of(varaible));

        Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test.distinc", List.of(new Alias("alias.test")))));
        //when
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        when(itemService.findByNombreItem("item.test")).thenReturn(List.of());
        Map<String, Map<String, Object>> res = new HashMap<>();

        ResponseEntity<Map<String, Map<String, Object>>> varariableResponse = (ResponseEntity<Map<String, Map<String, Object>>>) restTemplate.postForEntity("/variable-api/variables", List.of(variablesPOST), res.getClass());
        //then
        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Variables Insertadas ={}, Variables con Errores={error 1=no existe el item con el nombre :  item.test en la entidad pasada por parametro , puede crear la variable omitiendo el campo nombreDeItem  . La variable valor.test no fue creada}}");
    }

    @Test
    public void givenEntidadWithOutAnyMatchOfVariableValor_WhenVariablesResquestDTOIsCreated_then201AndVariableValorIsRetrived() {
        //given
        VariableResquestDTO varaible = new VariableResquestDTO();
        varaible.setNombreDeEntidad("entidad.test");
        varaible.setValor("valor.test");
        VariablesRequestDTO variablesPOST = new VariablesRequestDTO();
        variablesPOST.setIdDeContrato(8L);
        variablesPOST.setListaDeVariables(List.of(varaible));
        Item item = new Item("item.test", List.of(new Alias("alias.test")));
        Entidad entidad = new Entidad("entity.test", List.of(item));

        //when
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        doNothing().when(itemService).addNewItemInEntidad(entidad, varaible.getValor(), variablesPOST.getIdDeContrato());
        Map<String, Map<String, Object>> res = new HashMap<>();

        ResponseEntity<Map<String, Map<String, Object>>> varariableResponse = (ResponseEntity<Map<String, Map<String, Object>>>) restTemplate.postForEntity("/variable-api/variables", List.of(variablesPOST), res.getClass());
        //then
        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Creada 1=valor.test, fue creada referenciando a un item con su valor como nombre del item}");
    }

    @Test
    public void givenEntidadWithVariableValor_WhenVariablesResquestDTOIsCreated_then201AndVariableValorIsRetrived() {
        //given
        VariableResquestDTO varaible = new VariableResquestDTO();
        varaible.setNombreDeEntidad("entidad.test");
        varaible.setValor("valor.mismo.test");
        VariablesRequestDTO variablesPOST = new VariablesRequestDTO();
        variablesPOST.setIdDeContrato(8L);
        variablesPOST.setListaDeVariables(List.of(varaible));
        Item item = new Item("valor.mismo.test", List.of(new Alias("alias.test")));
        Entidad entidad = new Entidad("entity.test", List.of(item));

        //when
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        doNothing().when(itemService).addNewItemInEntidad(entidad, varaible.getValor(), variablesPOST.getIdDeContrato());
        Map<String, Map<String, Object>> res = new HashMap<>();

        ResponseEntity<Map<String, Map<String, Object>>> varariableResponse = (ResponseEntity<Map<String, Map<String, Object>>>) restTemplate.postForEntity("/variable-api/variables", List.of(variablesPOST), res.getClass());
        //then
        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Creada 1=valor.mismo.test, fue creada referenciando al item valor.mismo.test en la entidad entity.test}");
    }

    @Test
    public void givenListVariableResquestDTO_WhenIsCreated_then201AndSuccesIsRetrived() {
        //given
        VariableResquestDTO varaible = new VariableResquestDTO();
        varaible.setNombreDeEntidad("entidad.test");
        varaible.setValor("valor.mismo.test");
        VariablesRequestDTO variablesPOST = new VariablesRequestDTO();
        variablesPOST.setIdDeContrato(8L);
        variablesPOST.setListaDeVariables(List.of(varaible));
        Item item = new Item("valor.mismo.test", List.of(new Alias("alias.test")));
        Entidad entidad = new Entidad("entity.test", List.of(item));

        //when
        when(entidadService.findByNombreEntidad("entidad.test")).thenReturn(Optional.of(entidad));
        doNothing().when(itemService).addNewItemInEntidad(entidad, varaible.getValor(), variablesPOST.getIdDeContrato());
        Map<String, Map<String, Object>> res = new HashMap<>();

        ResponseEntity<Map<String, Map<String, Object>>> varariableResponse = (ResponseEntity<Map<String, Map<String, Object>>>) restTemplate.postForEntity("/variable-api/variables", List.of(variablesPOST, variablesPOST), res.getClass());
        //then
        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Creada 1=valor.mismo.test, fue creada referenciando al item valor.mismo.test en la entidad entity.test, Creada 2=valor.mismo.test, fue creada referenciando al item valor.mismo.test en la entidad entity.test}");

    }

    @Test
    public void givenVariableDoesNotExists_WhenDeleteVariable_404AndMessageErrorIsRetrived() {

        when(variableService.findById(1L)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<?> variableResponse = restTemplate.exchange("/variable-api/variables?id=1", HttpMethod.DELETE, entity, new HashMap<String, Object>().getClass());

        assertThat(variableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(variableResponse.getBody().toString()).isEqualTo("{Error=La variable con el id 1 no existe en la base de datos}");

    }

    @Test
    public void givenVariable_WhenDeleteVariable_200AndMessageIsRetrived() {

        Variable variable = new Variable();
        variable.setIdContrato(2L);

        when(variableService.findById(1L)).thenReturn(Optional.of(variable));
        doNothing().when(variableService).delete(variable);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<?> variableResponse = restTemplate.exchange("/variable-api/variables?id=1", HttpMethod.DELETE, entity, new HashMap<String, Object>().getClass());

        assertThat(variableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(variableResponse.getBody().toString()).isEqualTo("{Borrado=La variable con el id 1 fue eliminada de la base de datos}");
    }

    @Test
    public void givenVariableDoesNotExists_whenUpdateVariable_404AndErrorMessageIsRetrived(){
        VariableDTO variable = new VariableDTO();

        when(variableService.findById(8L)).thenReturn(Optional.empty());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>( variable, headers);

        ResponseEntity<?> variableResponse = restTemplate.exchange("/variable-api/variables?id=8", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(variableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(variableResponse.getBody().toString()).isEqualTo("{Error=La variable con el id 8 no existe en la base de datos}");
    }
    @Test
    public void givenIdItemDoesNotExists_whenUpdateVariable_404AndErrorMessageIsRetrived(){
        VariableDTO variableToSend = new VariableDTO();
        variableToSend.setIdContrato(8L);
        variableToSend.setIdItem(5L);

        Variable variable = new Variable();
        variable.setId(8L);
        variable.setIdContrato(2L);
        variable.setIdItem(new Item());
        variable.setValor("variable.test");

        when(variableService.findById(8L)).thenReturn(Optional.of(variable));
        when(itemService.findById(5L)).thenReturn(Optional.empty());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>( variableToSend, headers);

        ResponseEntity<?> variableResponse = restTemplate.exchange("/variable-api/variables?id=8", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(variableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(variableResponse.getBody().toString()).isEqualTo("{Error=El id del item no existe en la base de datos}");
    }

    @Test
    public void givenVariable_whenUpdated_200AndMessageIsRetrived(){
        VariableDTO variableToSend = new VariableDTO();
        variableToSend.setIdContrato(8L);
        variableToSend.setIdItem(5L);

        Variable variable = new Variable();
        variable.setId(8L);
        variable.setIdContrato(2L);
        variable.setIdItem(new Item());
        variable.setValor("variable.test");

        when(variableService.findById(8L)).thenReturn(Optional.of(variable));
        when(variableService.save(variable)).thenReturn(variable);
        when(itemService.findById(5L)).thenReturn(Optional.of(new Item()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>( variableToSend, headers);

        ResponseEntity<?> variableResponse = restTemplate.exchange("/variable-api/variables?id=8", HttpMethod.PUT, entity, new HashMap<String, Object>().getClass());

        assertThat(variableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(variableResponse.getBody().toString()).isEqualTo("{Actualizada ={id=8, idContrato=8, valor=variable.test}}");
    }

    @Test
    public void givenVariableAndItemAndEntidad_WhenVariableIsRetrived_then200AndListOfVariablesIsReceived() {
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());
        Entidad entidad = new Entidad("entidad.test", List.of(item));

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of(variable)));
        when(entidadService.findByNombre(entidad.getNombre())).thenReturn(new EntidadDTO());
        when(itemService.findByNombreItem(item.getNombreItem())).thenReturn(List.of(item));
        when(variableService.findByIdContratoAndNombreEntidadAndNombreDeItem(variable.getIdContrato(), entidad.getNombre(), item.getNombreItem())).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorIdDeContratoNombreDeEntidadNombreDeItem?idDeContrato=2&nombreDeEntidad=entidad.test&nombreDeItem=item.test", new ArrayList<Variable>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("[{id=null, idContrato=2, valor=null}]");

    }

    @Test
    public void givenNombreItemDoesNotExists_whenVariableIsRetrived_404AndErrorMessageIsRecived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());
        Entidad entidad = new Entidad("entidad.test", List.of(item));

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of(variable)));
        when(entidadService.findByNombre(entidad.getNombre())).thenReturn(new EntidadDTO());
        when(itemService.findByNombreItem(item.getNombreItem())).thenReturn(List.of());
        when(variableService.findByIdContratoAndNombreEntidadAndNombreDeItem(variable.getIdContrato(), entidad.getNombre(), item.getNombreItem())).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorIdDeContratoNombreDeEntidadNombreDeItem?idDeContrato=2&nombreDeEntidad=entidad.test&nombreDeItem=item.test", new HashMap<String, Object>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Error 3 =no existe el item con el nombre item.test}");

    }

    @Test
    public void givenNombreEntidadDoesNotExists_whenVariableIsRetrived_404AndErrorMessageIsRecived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());
        Entidad entidad = new Entidad("entidad.test", List.of(item));

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of(variable)));
        when(entidadService.findByNombre(entidad.getNombre())).thenReturn(null);
        when(itemService.findByNombreItem(item.getNombreItem())).thenReturn(List.of(item));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorIdDeContratoNombreDeEntidadNombreDeItem?idDeContrato=2&nombreDeEntidad=entidad.test&nombreDeItem=item.test", new HashMap<String, Object>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Error 2 =no existe la entidad con el nombre entidad.test}");
    }

    @Test
    public void givenVariableDoesNotExists_whenVariableIsRetrived_404AndErrorMessageIsRecived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());
        Entidad entidad = new Entidad("entidad.test", List.of(item));

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of()));
        when(entidadService.findByNombre(entidad.getNombre())).thenReturn(new EntidadDTO());
        when(itemService.findByNombreItem(item.getNombreItem())).thenReturn(List.of(item));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorIdDeContratoNombreDeEntidadNombreDeItem?idDeContrato=2&nombreDeEntidad=entidad.test&nombreDeItem=item.test", new HashMap<String, Object>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Error 1=no existen variables para el contrato con el id 2}");
    }

    @Test
    public void givenVariable_WhenVairableIsRetrived_200AndMessageIsReceived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());
        Entidad entidad = new Entidad("entidad.test", List.of(item));

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of(variable)));
        when(entidadService.findByNombre(entidad.getNombre())).thenReturn(new EntidadDTO());
        when(variableService.findByIdContratoAndIdEntidad(2L,"entidad.test")).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorContratoYNombreDeEntidad?idDeContrato=2&nombreDeEntidad=entidad.test", new ArrayList<Variable>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("[{id=null, idContrato=2, valor=null}]");

    }

    @Test
    public void givenVariable_WhenVairableIsRetrived_404AndErrorMessageIsReceived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of(variable)));
        when(entidadService.findByNombre("entidad.test")).thenReturn(null);
        when(variableService.findByIdContratoAndIdEntidad(2L,"entidad.test")).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorContratoYNombreDeEntidad?idDeContrato=2&nombreDeEntidad=entidad.test", new HashMap<String, Object>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Error 2 =no existe la entidad con el id entidad.test}");

    }

    @Test
    public void givenContratoIdDoesNotExist_WhenVairableIsRetrived_404AndErrorMessageIsReceived(){
        //given
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        Item item = new Item("item.test", new ArrayList<Alias>());

        //when
        when(variableService.findByIdContrato(variable.getIdContrato())).thenReturn(Optional.of(List.of()));
        when(entidadService.findByNombre("entidad.test")).thenReturn(new EntidadDTO());
        when(variableService.findByIdContratoAndIdEntidad(2L,"entidad.test")).thenReturn(Optional.of(List.of(variable)));

        ResponseEntity<?> varariableResponse =
                restTemplate.getForEntity("/variable-api/variablesPorContratoYNombreDeEntidad?idDeContrato=2&nombreDeEntidad=entidad.test", new HashMap<String, Object>().getClass());

        assertThat(varariableResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(varariableResponse.getBody().toString()).isEqualTo("{Error 1=no existen variables para el contrato con el id 2}");

    }
}
