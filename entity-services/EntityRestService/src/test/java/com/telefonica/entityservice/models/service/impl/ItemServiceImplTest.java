package com.telefonica.entityservice.models.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.dtos.ItemDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.repositories.ItemJpaRepository;
import com.telefonica.entityservice.models.services.impl.AliasServiceImpl;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;
import com.telefonica.entityservice.models.services.impl.ItemServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

	@InjectMocks
	private ItemServiceImpl itemService;
	
    @Mock
	private AliasServiceImpl aliasService;
	
    @Mock
	private EntidadServiceImpl entidadService ;
    
    @Mock
    private ItemJpaRepository itemRepository;

    @Test
    public void givenEntityDoesNotExists_whenItemIsRetrieved_thenErrorMessageIsReceived() {
        Mockito.when(entidadService.findByNombre("entidad")).thenReturn(null);
        
        assertThat(itemService.findItemByEntityAndAlias("entidad", "alias").get()).isEqualTo("La entidad No existe en la base de datos");
    }

    @Test
    public void givenAliasDoesNotExists_whenItemIsRetrieved_thenErrorMessageIsReceived() {
    	when(entidadService.findByNombre("entidad")).thenReturn(new EntidadDTO());
        when(aliasService.findByName("alias")).thenReturn(new ArrayList());
        
        assertThat(itemService.findItemByEntityAndAlias("entidad", "alias").get()).isEqualTo("El Alias No existe en la base de datos");
    }

    @Test
    public void givenAliasListWithSameName_whenItemIsRetrieved_thenItemIsReceived() {
    	EntidadDTO entidadDto = new EntidadDTO();
    	entidadDto.setId(1L);
    	entidadDto.setNombreEntidad("ent1");
    	
    	List<Alias> aliasList = new ArrayList<Alias>();
    	
    	/*Completamos una lista con 2 alias que posean el mismo nombre, cada uno asociado a un item distinto y 
    	en donde el primer item se encuentre relacionado a la entidad "ent1" */
    	Alias alias = new Alias(1L, "alias");
    	Item item = new Item();
    	item.setNombreItem("item1");
    	item.setEntidad(new Entidad(1L, "ent1", new ArrayList<>()));
    	alias.setItem(item);
    	
    	aliasList.add(alias);
    	
    	alias = new Alias(2L, "alias");
    	item = new Item();
    	item.setNombreItem("item2");
    	item.setEntidad(new Entidad(2L, "ent2", new ArrayList<>()));
    	alias.setItem(item);
    	aliasList.add(alias);
    	//-------------------------------------------------------------------

    	when(entidadService.findByNombre("ent1")).thenReturn(entidadDto);
        when(aliasService.findByName("alias")).thenReturn(aliasList);
        
        Optional<ItemDTO> itemOp = (Optional<ItemDTO>)itemService.findItemByEntityAndAlias("ent1", "alias"); 
        
        assertThat(itemOp.get().getNombreItem()).isEqualTo("item1");
    }
    
    @Test
    public void givenAliasListWithDifferentName_whenItemIsRetrieved_thenItemIsReceived() {
    	EntidadDTO entidadDto = new EntidadDTO();
    	entidadDto.setId(1L);
    	entidadDto.setNombreEntidad("ent1");
    	
    	List<Alias> aliasList = new ArrayList<Alias>();
    	
    	/*Completamos una lista con 2 alias que posean el mismo nombre, cada uno asociado a un item distinto y 
    	en donde el primer item se encuentre relacionado a la entidad "ent1" */
    	Alias alias = new Alias(1L, "alias");
    	Item item = new Item();
    	item.setNombreItem("item1");
    	item.setEntidad(new Entidad(1L, "ent1", new ArrayList<>()));
    	alias.setItem(item);
    	
    	aliasList.add(alias);
    	
    	alias = new Alias(2L, "alias2");
    	item = new Item();
    	item.setNombreItem("item2");
    	item.setEntidad(new Entidad(2L, "ent2", new ArrayList<>()));
    	alias.setItem(item);
    	aliasList.add(alias);
    	//-------------------------------------------------------------------

    	when(entidadService.findByNombre("ent1")).thenReturn(entidadDto);
        when(aliasService.findByName("alias")).thenReturn(aliasList);
        
        Optional<ItemDTO> itemOp = (Optional<ItemDTO>)itemService.findItemByEntityAndAlias("ent1", "alias"); 
        
        assertThat(itemOp.get().getNombreItem()).isEqualTo("item1");
    }
    
    @Test
    public void givenIdItem_whenItemIsRestriveById_thenItemDTOIsReceived() {
    	Item item = new Item();
    	item.setNombreItem("itemTest");
    	
    	when(((Optional<Item>)itemRepository.findById(1L))).thenReturn(Optional.of(item));
    	ItemDTO itemDTO = itemService.findItemDTOById(1L);
    	assertThat(itemDTO).isNotNull();
    }

    @Test
    public void givenEntidadAndValorAndIdContrato_whenItemIsSaved_thenItemIsReceived() {
    	Entidad entidad = new Entidad();
    	entidad.setItems(new ArrayList<Item>());
    	itemService.addNewItemInEntidad(entidad, "variableTest", 654L);

    	assertThat(entidad.getItems().size()).isEqualTo(1);
    	assertThat(entidad.getItems().get(0).getNombreItem()).isEqualTo("variableTest");
    	assertThat(entidad.getItems().get(0).getVariables().size()).isEqualTo(1);
    	assertThat(entidad.getItems().get(0).getVariables().get(0).getIdContrato()).isEqualTo(654);
    }
    
}
