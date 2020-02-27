package com.telefonica.entityservice.models.services;

import java.util.List;
import java.util.Optional;

import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;

public interface IItemService {

	Optional<?> findItemByEntityAndAlias (String nombreEntidad,String nombreAlias);

	Optional<Item> findById(Long id);

	void save(Item item);

	ItemDTO findItemDTOById(Long id);

	Optional<Item> findItemById(Long id);

	void delete (Long id);

	List<Item> findByNombreItem(String nombreItem);

	void addNewItemInEntidad(Entidad ent , String nombreDeItem , Long contratoId);

	void upsertItemWithVarible(Item item, Long idDeContrato, String valor, Long idVariable);
	
	Optional<Item> findByNombreItemAndEntidad(String nombreItem, String nombreEntidad);

}
