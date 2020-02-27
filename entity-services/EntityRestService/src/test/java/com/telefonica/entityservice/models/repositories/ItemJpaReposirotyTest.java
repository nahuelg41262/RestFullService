package com.telefonica.entityservice.models.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;


@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true )
public class ItemJpaReposirotyTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemJpaRepository itemRepository;

    private Entidad entidad;
    
    @Before
    public void setContext() {
    	entidad = new Entidad();
    	entidad.setNombre("EntidadTest");
    	entidad.setItems(new ArrayList<Item>());
    	
        entityManager.persist(entidad);
        entityManager.flush();

    }

    @Test
    public void givenItem_WhenFindById_ItemIsRetrived(){
        //given
    	Item item = new Item();
        item.setEntidad(entidad);
        item.setNombreItem("itemTest1");

        entityManager.persist(item);
        entityManager.flush();
        
    	assertThat(itemRepository.findById(item.getId()).get().getNombreItem()).isEqualTo("itemTest1");
    }
    
    @Test
    public void givenItem_WhenFindByNombre_ItemIsRetrived(){
        //given
    	Item item = new Item();
        item.setEntidad(entidad);
        item.setNombreItem("itemTest2");

        entityManager.persist(item);
        entityManager.flush();
        
        //when
        Item itemByNombre = itemRepository.findByNombreItem("itemTest2").get(0);
        
        //then
    	assertThat(itemByNombre.getId()).isEqualTo(item.getId());
    }

    @Test
    public void givenItem_WhenFindByNombreItemAndEntidad_ItemIsRetrived(){
        //given
    	Item item = new Item();
        item.setEntidad(entidad);
        item.setNombreItem("itemTest2");

        entityManager.persist(item);
        entityManager.flush();
        
        //when
        Item itemByNombre = itemRepository.findByNombreItemAndEntidad("itemTest2", "EntidadTest");
        
        //then
    	assertThat(itemByNombre.getId()).isEqualTo(item.getId());
    }

}

