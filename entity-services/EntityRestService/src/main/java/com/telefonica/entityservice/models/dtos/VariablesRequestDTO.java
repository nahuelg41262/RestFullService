package com.telefonica.entityservice.models.dtos;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/*
* Esta clase modela los datos que ingresan en el body para la creacion de una nueva Variable
* En donde Dado un idDeContrato se intentaran insertar una lista de variables extraidas del mismo .
*
* */
public class VariablesRequestDTO {

    @NotNull(message = "El id de contrato no puede ser nulo")
    @ApiModelProperty(notes = "ID del contrato del cual se extrajo la variable",required = true)
    private Long idDeContrato;

    @NotNull(message = "La lista de variables  no puede ser nulo")
    @Valid
    @ApiModelProperty(notes = "Lista de las variables que se desean insertar para el idContrato",required = true)
    private List<VariableResquestDTO> listaDeVariables ;

    public Long getIdDeContrato() {
        return idDeContrato;
    }

    public void setIdDeContrato(Long idDeContrato) {
        this.idDeContrato = idDeContrato;
    }

    public List<VariableResquestDTO> getListaDeVariables() {
        return listaDeVariables;
    }

    public void setListaDeVariables(List<VariableResquestDTO> listaDeVariables) {
        this.listaDeVariables = listaDeVariables;
    }
}
