package com.telefonica.entityservice.models.dtos;

import io.swagger.annotations.ApiModel;

import java.util.Optional;

@ApiModel(value = "CamposVariable")
public class VariableDTO {

    private Long idContrato;
    private Long idItem;
    private String valor ;

    public Optional<Long> getIdContrato() {
        return Optional.ofNullable(idContrato);
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Optional<Long> getIdItem() {
        return Optional.ofNullable(idItem);
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Optional<String> getValor() {
        return Optional.ofNullable(valor);
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public VariableDTO() {
    }

    public VariableDTO(Long idContrato, Long idItem, String valor) {
        this.idContrato = idContrato;
        this.idItem = idItem;
        this.valor = valor;
    }
}
