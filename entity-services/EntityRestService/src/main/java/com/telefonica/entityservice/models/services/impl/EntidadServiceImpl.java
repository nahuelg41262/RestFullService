package com.telefonica.entityservice.models.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.entities.Entidad;
import com.telefonica.entityservice.models.repositories.EntidadJpaRepository;
import com.telefonica.entityservice.models.services.IEntidadService;

@Service
public class EntidadServiceImpl extends ServiceDTO  implements IEntidadService {

	@Autowired
	private EntidadJpaRepository JPAEntidadDao;

	@Transactional(readOnly = true)
	public List<EntidadDTO> findAll() {
		
		ModelMapper modelMapper = new ModelMapper();

		List<EntidadDTO> entidadesDTO = new ArrayList<EntidadDTO>();
		JPAEntidadDao.findAll().forEach(t -> {
			entidadesDTO.add( modelMapper.map(t, EntidadDTO.class));
		});
	
		return  entidadesDTO.isEmpty() ? null : entidadesDTO;
	}
	
	@Transactional(readOnly = true)
	public EntidadDTO findByNombre(String nombre) {
		Entidad found = JPAEntidadDao.findByNombre(nombre);
		return found != null ? super.DTOConverter(found, EntidadDTO.class): null;
		
	}
	@Transactional(readOnly = true)
	public Optional<Entidad> findByNombreEntidad(String nombre) {
		Entidad found = JPAEntidadDao.findByNombre(nombre);
		return found != null ? Optional.of(found) : Optional.empty();

	}
	
	@Transactional
	public void save(Entidad e) {
		JPAEntidadDao.save(e);
	}
	
	@Transactional
	public void delete (Long id) {
		JPAEntidadDao.delete(JPAEntidadDao.findById(id).get());
	}

	@Override
	@Transactional(readOnly = true)
	public EntidadDTO findById(Long id) {
		Optional<Entidad> found =  JPAEntidadDao.findById(id);
		return found.isEmpty() ? null : super.DTOConverter(found.get(), EntidadDTO.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Entidad> findEntidadById(Long id) {
		return JPAEntidadDao.findById(id);
	}
	
}
