package com.telefonica.entityservice.models.repositories;

import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.repositories.EntidadJpaRepository;

import java.util.ArrayList;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true )
public class EntidadJpaRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntidadJpaRepository entidadRepository;

    @Test
    public void whenfindByNombre_thenReturnEntidad() {
        // given
        Entidad entidad = new Entidad();
        entidad.setNombre("entity.test");
        entidad.setItems(new ArrayList<Item>());
        entityManager.persist(entidad);
        entityManager.flush();
     
        // when
        Entidad found = entidadRepository.findByNombre(entidad.getNombre());
     
        // then
        assertThat(found.getNombre())
          .isEqualTo(entidad.getNombre());
    }
    
    

}