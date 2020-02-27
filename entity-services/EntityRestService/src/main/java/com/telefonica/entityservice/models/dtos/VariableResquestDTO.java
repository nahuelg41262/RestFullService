package com.telefonica.entityservice.models.dtos;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/*
* Esta clase Modela una variable que debe ser ingresada en la entidad y/o el item correspondiente
* */
public class VariableResquestDTO {

    @ApiModelProperty(notes = "Nombre de la entidad en la cual sera buscado/insertado el valor en sus items",required = true)
    private String nombreDeEntidad;

    @ApiModelProperty(notes = "Nombre del Item en cual sera insertado el valor , puede ser removido en caso de no querer usarse",required = false)
    private String nombreDeItem;

    @ApiModelProperty(notes = " Variable extraida del contrato")
    @NotNull(message = "el valor no puede ser nulo")
    @Valid
    private String valor;


    public Boolean nombreItemIsPresent(){
        return this.nombreDeItem != null;
    }
    public Optional<String> getNombreDeEntidad() {
        return Optional.ofNullable(nombreDeEntidad);
    }

    public void setNombreDeEntidad(String nombreDeEntidad) {
        this.nombreDeEntidad = nombreDeEntidad;
    }

    public Optional<String> getNombreDeItem() {
        return Optional.ofNullable(nombreDeItem);
    }

    public void setNombreDeItem(String nombreDeItem) {
        this.nombreDeItem = nombreDeItem;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
