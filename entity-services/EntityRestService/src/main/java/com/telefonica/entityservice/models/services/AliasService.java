package com.telefonica.entityservice.models.services;

import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Item;

import java.util.List;
import java.util.Optional;

public interface AliasService {
	
	List<Alias> findByName(String name);
	Optional<Alias> findById(Long id);
	void delete(Alias alias);
	void save (String valor , Item item);
	void save(Alias alias);

}
