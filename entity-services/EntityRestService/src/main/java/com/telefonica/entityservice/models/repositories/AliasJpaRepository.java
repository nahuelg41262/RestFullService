package com.telefonica.entityservice.models.repositories;

import com.telefonica.entityservice.models.entities.Alias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AliasJpaRepository extends JpaRepository<Alias, Long>{
		
		List<Alias> findByValor(String valor);
}
