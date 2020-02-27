package com.telefonica.entityservice.models.repositories;

import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VariableJpaRepository extends JpaRepository<Variable, Long> {

    @Override
    Variable save(Variable variable);

    List<Variable> findByIdContrato(Long idContrato);

    @Query("SELECT v FROM Variable v , Item i  WHERE  v.idContrato = ?1 AND i.entidad.nombre = ?2 AND v.idItem = i.id ")
    List<Variable> findByIdContratoAndIdEntidad(Long idContrato , String nombreDeEntidad);


    @Query("SELECT v FROM Variable v , Item i  WHERE  v.idContrato = ?1 AND i.entidad.nombre = ?2 AND v.idItem = i.id AND i.nombreItem = ?3")
    List<Variable> findByIdContratoAndNombreEntidadAndNombreDeItem(Long idContrato , String nombreDeEntidad , String nombreDeItem);

    @Query("SELECT v FROM Variable v  WHERE v.idItem.entidad.nombre = ?1")
    List<Variable> finByNombreEntidad(String nombre);

    @Override
    Optional<Variable> findById(Long id);





}
