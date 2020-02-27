package com.telefonica.entityservice.models.services.impl;

import org.modelmapper.ModelMapper;


public abstract class ServiceDTO {
		
	
	public <T , D> D DTOConverter(T entity , Class<D> dto) {
		ModelMapper modelMapper = new ModelMapper();
		
		return modelMapper.map(entity,dto);
	}
}
