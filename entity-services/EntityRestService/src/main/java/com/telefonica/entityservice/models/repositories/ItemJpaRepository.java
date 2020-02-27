package com.telefonica.entityservice.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.telefonica.entityservice.models.entities.Item;

public interface ItemJpaRepository extends JpaRepository<Item, Long>{
		
		List<Item> findByNombreItem(String nombreItem);
		
		@Query("SELECT i FROM Item i WHERE i.nombreItem = ?1 and i.entidad.nombre = ?2")
		Item findByNombreItemAndEntidad(String nombreItem, String entidad);
}
