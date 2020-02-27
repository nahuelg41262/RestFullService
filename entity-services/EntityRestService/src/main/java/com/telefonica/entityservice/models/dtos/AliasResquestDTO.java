package com.telefonica.entityservice.models.dtos;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class AliasResquestDTO {
    @NotNull
    private Long id;

    private String valor;

    private String nombreDeItem;

    private  String nombreDeEntidad;

    public Optional<String> getNombreDeEntidad() {
        return Optional.ofNullable(nombreDeEntidad);
    }

    public void setNombreDeEntidad(String nombreDeEntidad) {
        this.nombreDeEntidad = nombreDeEntidad;
    }

    public AliasResquestDTO(String valor, String nombreDeItem) {
        this.valor = valor;
        this.nombreDeItem = nombreDeItem;
    }

    public AliasResquestDTO() {
    }

    public AliasResquestDTO(@NotNull Long id, String valor, String nombreDeItem, String nombreDeEntidad) {
        this.id = id;
        this.valor = valor;
        this.nombreDeItem = nombreDeItem;
        this.nombreDeEntidad = nombreDeEntidad;
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(this.id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Optional<String> getValor() {
        return Optional.ofNullable(valor);
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Optional<String> getNombreDeItem() {
        return Optional.ofNullable(nombreDeItem);
    }

    public void setNombreDeItem(String nombreDeItem) {
        this.nombreDeItem = nombreDeItem;
    }
}
