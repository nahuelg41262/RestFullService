package com.telefonica.entityservice.models.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.repositories.EntidadJpaRepository;
import com.telefonica.entityservice.models.services.impl.EntidadServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class EntidadServiceImplTest {

	@InjectMocks
	EntidadServiceImpl service;

	@Mock
	EntidadJpaRepository JPAEntidadDao;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test 
	public void whenfindByNombre_thenReturnEntidadDTO () {
		// Given
		Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
		
		// When
		when(JPAEntidadDao.findByNombre("entity.test")).thenReturn(entidad);
		
		EntidadDTO current = service.findByNombre("entity.test");
		
		// Then
		
		assertEquals("entity.test", current.getNombreEntidad());
		assertEquals("item.test", current.getItems().get(0).getNombreItem());
		assertEquals("alias.test", current.getItems().get(0).getAlias().get(0).getValor());
		
	}
	@Test
	public void whenfindById_thenReturnEntidadDTO() {
		// Given
		Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));

		// When
		when(JPAEntidadDao.findById(1L)).thenReturn(Optional.of(entidad));

		EntidadDTO current = service.findById(1L);

		// Then
		assertEquals("entity.test", current.getNombreEntidad());
		assertEquals("item.test", current.getItems().get(0).getNombreItem());
		assertEquals("alias.test", current.getItems().get(0).getAlias().get(0).getValor());
	}

	@Test
	public void Should_Null_When_IdNotExist() {

		EntidadDTO current = service.findById(2L);
		assertEquals(null, current);
		verify(JPAEntidadDao, atLeast(1)).findById(2L);
	}

	@Test
	public void Given_Entidades_When_findAll_Then_ThreeEntidades() {
		// given
		Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
		Entidad entidad2 = new Entidad("entity.test2",List.of(new Item("item.test2", List.of(new Alias("alias.test2")))));
		Entidad entidad3 = new Entidad("entity.test3",List.of(new Item("item.test3", List.of(new Alias("alias.test3")))));

		// When
		when(JPAEntidadDao.findAll()).thenReturn(List.of(entidad, entidad2, entidad3));
		
		List<EntidadDTO> currents = service.findAll();

		// Then
		assertEquals(3 , currents.size());
		verify(JPAEntidadDao, times(1)).findAll();

	}
	@Test
	public void Should_Null_When_NotExistEntidades() {

		List<EntidadDTO> currents = service.findAll();
		assertEquals(null, currents);
		verify(JPAEntidadDao, times(1)).findAll();
	}
	@Test
	public void Given_EntidadIdExist_When_delete_Then_EntidadWillDelete() {
		
		Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
		
		when(JPAEntidadDao.findById(1L)).thenReturn(Optional.of(entidad));
		service.delete(1L);
		
		verify(JPAEntidadDao ,times(1)).delete(entidad);
	}
	@Test
	public void  Given_ValidEntidad_When_save_Then_EntidadWillSave() {
	
		Entidad entidad = new Entidad("entity.test", List.of(new Item("item.test", List.of(new Alias("alias.test")))));
		
		JPAEntidadDao.save(entidad);
		
		verify(JPAEntidadDao, times(1)).save(entidad);
		
	}
}
