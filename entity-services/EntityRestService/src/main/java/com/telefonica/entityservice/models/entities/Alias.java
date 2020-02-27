package com.telefonica.entityservice.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="alias")
public class Alias implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ali_id")
	@JsonIgnore
	private Long id ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "it_id")
	@JsonIgnore
	private Item item ;
	
	@NotNull(message = "el valor del alias es obligatorio")
    @ApiModelProperty(notes = "Valor | nombre del alias", required = true)
	private String valor ; 
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public Alias() {
		super();
	}
	public Alias( @NotNull(message = "el valor del alias es obligatorio") String valor) {
		super();
		this.valor = valor;
	}
	public Alias( @NotNull(message = "el valor del alias es obligatorio") String valor , Item item) {
		super();
		this.valor = valor;
		this.item = item ;
	}
	public Alias(Long id,@NotNull(message = "el valor del alias es obligatorio") String valor) {
		super();
		this.id = id;
		this.valor = valor;
	}
	
	@Override
	public boolean equals(Object obj) {
		Alias alias = (Alias) obj;
		
		return alias == null ? false : alias.valor.equals(this.valor);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}
	
}
