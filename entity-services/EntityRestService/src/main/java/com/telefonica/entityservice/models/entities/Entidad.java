package com.telefonica.entityservice.models.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class Entidad implements Serializable {


	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ent_id")
	private Long id;
	
	@NotEmpty(message = "El nombre de la entidad es obligatorio")
	@Column(unique = true)
    @ApiModelProperty(notes = "Nombre de la entidad, Unico e irrepetible",required = true)
	private String nombre;

	@OneToMany(orphanRemoval=true ,cascade=CascadeType.ALL, fetch = FetchType.EAGER )
	@JoinColumn(name = "ent_id" )
	@Valid
	@NotNull(message = "La lista de items es obligatorio")
    @ApiModelProperty(notes = "Lista de items", required = true)
	private List<Item> items;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	public Entidad() {
		super();
	}


	private static final long serialVersionUID = 1L;
	
	public Entidad( @NotEmpty(message = "El nombre de la entidad es obligatorio") String nombre,
			@Valid @NotNull(message = "La lista de items es obligatorio") List<Item> items) {
		super();
		this.nombre = nombre;
		this.items = items;
	}

	public Entidad(Long id, @NotEmpty(message = "El nombre de la entidad es obligatorio") String nombre,
			@Valid @NotNull(message = "La lista de items es obligatorio") List<Item> items) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.items = items;
	}
	
}
