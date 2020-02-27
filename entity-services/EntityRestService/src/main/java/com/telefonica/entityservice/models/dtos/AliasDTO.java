package com.telefonica.entityservice.models.dtos;

public class AliasDTO {

	private Long id ;
	private String valor;
	
	public AliasDTO() {}

	public AliasDTO(Long id, String valor) {
		this.id = id;
		this.valor = valor;
	}
	
	public String getValor() {
		return valor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
