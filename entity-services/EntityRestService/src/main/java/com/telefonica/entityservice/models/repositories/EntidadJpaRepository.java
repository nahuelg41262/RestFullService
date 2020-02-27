package com.telefonica.entityservice.models.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.telefonica.entityservice.models.entities.Entidad;

public interface EntidadJpaRepository extends JpaRepository<Entidad, Long> {
	
	Entidad findByNombre(String nombre);
	
}
