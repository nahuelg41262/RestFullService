package com.telefonica.entityservice.models.services;

import com.telefonica.entityservice.models.dtos.EntidadDTO;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;

import java.util.List;
import java.util.Optional;

public interface IVariableService {

   Optional<List<Variable>> findByIdContrato(Long id);

   Variable save(Variable variable);

   Optional<List<Variable>> findByIdContratoAndIdEntidad(Long idContrato, String nombreDeEntidad);

   Optional<List<Variable>> findByIdContratoAndNombreEntidadAndNombreDeItem(Long idContrato, String nombreDeEntidad, String nombreDeItem);

   Optional<Variable> findById(Long id);

   void delete(Variable variable);

   void save(Item i , String Valor , Long contratoId );

   Optional<List<Variable>> findByEntidad(String nombreEntidad);
}

