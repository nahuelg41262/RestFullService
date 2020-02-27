package com.telefonica.entityservice.models.repositories;

import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true )
public class VariableJpaReposirotyTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VariableJpaRepository variableRepository;

    @Test
    public void givenVariable_WhenFindById_VariableIsRetrived(){
        //given
        Variable var = new Variable();
        var.setIdContrato(1L);

        entityManager.persist(var);
        entityManager.flush();

        //when
        List<Variable> founds = variableRepository.findAll();
        Optional<Variable> found = variableRepository.findById(var.getId());

        //then
        assertThat(found.get().getIdContrato()).isEqualTo(var.getIdContrato());
    }
    @Test
    public void givenVariable_WhenFindById_VariableIsNotRetrived(){
        //given
        Variable var = new Variable();
        var.setIdContrato(1L);

        entityManager.persist(var);
        entityManager.flush();

        //when
        Optional<Variable> found = variableRepository.findById(2546L);

        //then
        assertThat(found).isEqualTo(Optional.empty());
    }
    @Test
    public void givenVariable_WhenFindByContratoId_VariablesIsRetrived(){
        //given
        Variable var = new Variable();
        var.setIdContrato(1L);
        var.setValor("variable.test");


        Variable var2 = new Variable();
        var2.setIdContrato(1L);
        var2.setValor("variable.test2");

        entityManager.persist(var);
        entityManager.persist(var2);
        entityManager.flush();

        //when
        List<Variable> found = variableRepository.findByIdContrato(1L);

        //then
        assertThat(found.get(0).getIdContrato()).isEqualTo(var.getIdContrato());
        assertThat(found.get(0).getValor()).isEqualTo(var.getValor());

        assertThat(found.get(1).getIdContrato()).isEqualTo(var2.getIdContrato());
        assertThat(found.get(1).getValor()).isEqualTo(var2.getValor());
    }
    @Test
    public void givenEntidadAndVariable_WhenfindByIdContratoAndIdEntidad_VariableIsRetrived() {
        //given
        Alias alias = new Alias();
        alias.setValor("alias.test");
        List<Alias> aliases = new ArrayList<>();
        aliases.add(alias);
        Item item = new Item();
        item.setNombreItem("item.test");
        item.setAlias(aliases);
        alias.setItem(item);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(items);

        Variable variable = new Variable();
        variable.setIdContrato(2L);
        variable.setIdItem(item);
        variable.setValor("VALOR");

        item.setVariables(List.of(variable));

        entityManager.persist(entidad);
        entityManager.flush();
    //when
       List<Variable> found =  variableRepository.findByIdContratoAndIdEntidad(2L,entidad.getNombre());
    //then
       assertThat(found.get(0).getValor()).isEqualTo(variable.getValor());
       assertThat(found.get(0).getIdContrato()).isEqualTo(variable.getIdContrato());

    }
    @Test
    public void givenEntidadAndVariables_WhenfindByIdContratoAndIdEntidad_TwoVariableIsRetrived(){
        //given
        Alias alias = new Alias();
        alias.setValor("alias.test");
        List<Alias> aliases = new ArrayList<>();
        aliases.add(alias);
        Item item  = new Item();
        item.setNombreItem("item.test");
        item.setAlias(aliases);
        alias.setItem(item);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(items);

        //Se crean tres variables para la misma entidad pero con diferente numero de idContrato
        Variable variable = new Variable();
        variable.setIdContrato(2L);
        variable.setIdItem(item);
        variable.setValor("VALOR");

        Variable variable2 = new Variable();
        variable2.setIdContrato(2L);
        variable2.setIdItem(item);
        variable2.setValor("VALOR2");

        Variable variable3 = new Variable();
        variable3.setIdContrato(3L);
        variable3.setIdItem(item);
        variable3.setValor("VALOR3");

        item.setVariables(List.of(variable,variable2,variable3));
        entityManager.persist(entidad);
        entityManager.flush();
        //when
        List<Variable> found =  variableRepository.findByIdContratoAndIdEntidad(2L,entidad.getNombre());
        //then
        assertThat(found.get(0).getValor()).isEqualTo(variable.getValor());
        assertThat(found.get(0).getIdContrato()).isEqualTo(variable.getIdContrato());

        assertThat(found.get(1).getValor()).isEqualTo(variable2.getValor());
        assertThat(found.get(1).getIdContrato()).isEqualTo(variable2.getIdContrato());
        try {
            found.get(3).getValor();
        }catch (Exception e){
            assertThat(e).isInstanceOf(IndexOutOfBoundsException.class);
        }

    }
    @Test
    public void a(){
        Alias alias = new Alias();
        alias.setValor("alias.test");
        List<Alias> aliases = new ArrayList<>();
        aliases.add(alias);

        Item item  = new Item();
        item.setNombreItem("item.test");
        item.setAlias(aliases);
        alias.setItem(item);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(items);

        Variable variable4 = new Variable();
        variable4.setIdContrato(2L);
        variable4.setIdItem(item);
        variable4.setValor("VALOR4");

        item.setVariables(List.of(variable4));

        entityManager.persist(entidad);
        entityManager.flush();

        //when
        //Deberia estar vacia debido a que no hay contratos para le id 3
        List<Variable> found =  variableRepository.findByIdContratoAndIdEntidad(3L,"entidad.test");
        //then
         assertThat(found.isEmpty()).isEqualTo(true);
    }
    @Test
    public void givenVariableAndItemAndEntidad_WhenfindByIdContratoAndIdEntidadAndNombreDeItem_VaribleIsRetrived(){
        //given
        Alias alias = new Alias();
        alias.setValor("alias.test");
        List<Alias> aliases = new ArrayList<>();
        aliases.add(alias);
        Item item  = new Item();
        item.setNombreItem("item.test");
        item.setAlias(aliases);
        alias.setItem(item);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Entidad entidad = new Entidad();
        entidad.setNombre("entidad.test");
        entidad.setItems(items);

        //Se crean tres variables para la misma entidad pero con diferente numero de idContrato
        Variable variable = new Variable();
        variable.setIdContrato(1L);
        variable.setIdItem(item);
        variable.setValor("VALOR");

        Variable variable2 = new Variable();
        variable2.setIdContrato(2L);
        variable2.setIdItem(item);
        variable2.setValor("VALOR2");

        Variable variable3 = new Variable();
        variable3.setIdContrato(3L);
        variable3.setIdItem(item);
        variable3.setValor("VALOR3");

        item.setVariables(List.of(variable,variable2,variable3));

        entityManager.persist(entidad);
        entityManager.flush();

        //when
       List<Variable> found = variableRepository.findByIdContratoAndNombreEntidadAndNombreDeItem(2L,"entidad.test", "item.test");
       //then
        assertThat(found.get(0)).isEqualTo(variable2);
     }

     @Test
    public void givenVariableInEntidad_WhenFindByNombreEntidad_VariablesIsRetrived(){
         Alias alias = new Alias();
         alias.setValor("alias.test");
         List<Alias> aliases = new ArrayList<>();
         aliases.add(alias);

         Item item  = new Item();
         item.setNombreItem("item.test");
         item.setAlias(aliases);
         alias.setItem(item);

         Variable variable = new Variable();
         variable.setIdContrato(1L);
         variable.setIdItem(item);
         variable.setValor("VALOR");

         Variable variable2 = new Variable();
         variable2.setIdContrato(2L);
         variable2.setIdItem(item);
         variable2.setValor("VALOR2");

         Variable variable3 = new Variable();
         variable3.setIdContrato(3L);
         variable3.setIdItem(item);
         variable3.setValor("VALOR3");

         item.setVariables(List.of(variable,variable2,variable3));

         List<Item> items = new ArrayList<>();

         items.add(item);

         Entidad entidad = new Entidad();
         entidad.setNombre("entidad.test");
         items.get(0).setEntidad(entidad);
         entidad.setItems(items);

        entityManager.persist(entidad);
         entityManager.flush();

         //when
         List<Variable> found = variableRepository.finByNombreEntidad("entidad.test");

         assertThat(found.size()).isEqualTo(3);

     }

}

