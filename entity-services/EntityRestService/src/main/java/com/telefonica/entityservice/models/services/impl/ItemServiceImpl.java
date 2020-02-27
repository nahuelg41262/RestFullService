package com.telefonica.entityservice.models.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.repositories.ItemJpaRepository;
import com.telefonica.entityservice.models.services.IItemService;

@Service
public class ItemServiceImpl extends ServiceDTO implements IItemService {


    @Autowired
    private AliasServiceImpl aliasService;

    @Autowired
    private EntidadServiceImpl entidadService;

    @Autowired
    private VariableServiceImpl variableService;

    @Autowired
    private ItemJpaRepository itemRepository;


    public Optional<?> findItemByEntityAndAlias(String nombreEntidad, String nombreAlias) {

        EntidadDTO entidad = entidadService.findByNombre(nombreEntidad);
        List<Alias> alias = aliasService.findByName(nombreAlias);

        if (entidad == null) {
            return Optional.of("La entidad No existe en la base de datos");
        }
        if (alias.isEmpty()) {
            return Optional.of("El Alias No existe en la base de datos");
        }

        List<Alias> found = alias.stream()
                .filter(t -> t.getItem().getEntidad().getId() == entidad.getId())
                .collect(Collectors.toList());

        return Optional.of(super.DTOConverter(found.get(0).getItem(), ItemDTO.class));

    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO findItemDTOById(Long id) {
        Optional<Item> found = itemRepository.findById(id);
        return found.isEmpty() ? null : super.DTOConverter(found.get(), ItemDTO.class);
    }


    @Override
    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        itemRepository.delete(itemRepository.findById(id).get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Item> findByNombreItem(String nombreItem) {
        return itemRepository.findByNombreItem(nombreItem);
    }

    @Transactional(readOnly = true)
    public Optional<Item> findByNombreItemAndEntidad(String nombreItem, String nombreEntidad) {
        return Optional.ofNullable(itemRepository.findByNombreItemAndEntidad(nombreItem, nombreEntidad));
    }

    @Override
    @Transactional
    public void addNewItemInEntidad(Entidad entidad, String valor, Long contratoId) {
        //Se persite el nuevo item con el nombre de la variable , y se le da referencia al mismo en la tabla variables
        Variable variable = new Variable();
        variable.setIdContrato(contratoId);
        variable.setValor(valor);

        Item item = new Item();
        item.setNombreItem(valor);
        item.setEntidad(entidad);
        item.setAlias(new ArrayList<Alias>(List.of(new Alias(valor, item))));
        variable.setIdItem(item);
        item.setVariables(new ArrayList<Variable>(List.of(variable)));

        entidad.getItems().add(item);
        entidadService.save(entidad);

    }

    @Transactional
    public void upsertItemWithVarible(Item item, Long idDeContrato, String valor, Long idVariable) {
        //Se actualiza la lista de variables del item y atravez del cascade se crea la variable nueva con referencia al item
        Optional.of(item.getVariables())
                .ifPresent(variables -> variables
                        .stream()
                        .filter(variable -> variable.getId().equals(idDeContrato))
                        .collect(Collectors.toList())
                        .stream()
                        .findFirst()
                        .ifPresentOrElse(variable -> {
                        	//borro la variable vieja de la lista
							item.getVariables().remove(variable);
							//Actualizo los valores
							variable.setIdContrato(idDeContrato);
							variable.setValor(valor);
							item.getVariables().add(variable);
							itemRepository.save(item);
                        }, () -> {
                        	//Creo la variable en la lista del item
							Variable variable = new Variable();
							variable.setIdContrato(idDeContrato);
							variable.setValor(valor);
							variable.setIdItem(item);
                        	item.getVariables().add(variable);
                        	itemRepository.save(item);
                        })
               );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

}
