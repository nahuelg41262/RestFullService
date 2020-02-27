package com.telefonica.entityservice.models.services;

import java.util.List;
import java.util.Optional;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.entities.Entidad;

public interface IEntidadService  {
	
	public List<EntidadDTO> findAll();
	
	public void delete (Long id);

	public EntidadDTO findById(Long id );
	
	
	public EntidadDTO findByNombre(String nombre);
	
	public void save(Entidad e);
	
	public Optional<Entidad> findEntidadById(Long id);

	Optional<Entidad> findByNombreEntidad(String entidadId);

}
