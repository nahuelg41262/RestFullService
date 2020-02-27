package com.telefonica.entityservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telefonica.entityservice.models.entities.Variable;

import java.util.List;

public class ItemDTO {
	
	private String nombreItem;
	private List<AliasDTO> alias;
	@JsonIgnore
	private String entidad;
	private List<Variable> variables;

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public String getNombreItem() {
		return nombreItem;
	}

	public void setNombreItem(String nombreItem) {
		this.nombreItem = nombreItem;
	}

	public List<AliasDTO> getAlias() {
		return alias;
	}

	public void setAlias(List<AliasDTO> alias) {
		this.alias = alias;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	
}
