package com.telefonica.entityservice.models.repositories;

import com.telefonica.entityservice.models.entities.Alias;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true )
public class AliasJpaRepositoryTest {
	
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AliasJpaRepository aliasRepository;
    
    @Test
    public void whenfindByValor_thenReturnAliasList(){
    	//given
    	Alias alias = new Alias();
    	alias.setValor("alias.test");
    	entityManager.persist(alias);
        entityManager.flush();
        
    	
    	// when
    	List<Alias> found = aliasRepository.findByValor(alias.getValor());
    	
    	// then
    	assertThat(found.get(0).getValor())
    		.isEqualTo(alias.getValor());
    }
}
