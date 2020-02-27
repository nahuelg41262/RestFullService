package com.telefonica.entityservice.models.services.impl;

import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.repositories.EntidadJpaRepository;
import com.telefonica.entityservice.models.repositories.VariableJpaRepository;
import com.telefonica.entityservice.models.services.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Service
public class VariableServiceImpl extends ServiceDTO implements IVariableService {

    @Autowired
    private VariableJpaRepository JPAVariableDao;

    @Autowired
    private EntidadJpaRepository  JPAEntidadDao;

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Variable>> findByIdContrato(Long id) {

        return Optional.of(JPAVariableDao.findByIdContrato(id));
    }

    @Override
    @Transactional
    public void save(Item item , String valor , Long contratoId ){

        Variable variable =new Variable();
        variable.setIdItem(item);
        variable.setValor(valor);
        variable.setIdContrato(contratoId);

        JPAVariableDao.save(variable);
    }

    @Override
    @Transactional
    public Variable save(Variable variable) {
        return JPAVariableDao.save(variable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Variable>> findByIdContratoAndIdEntidad(Long idContrato, String nombreDeEntidad) {

        return Optional.of(JPAVariableDao.findByIdContratoAndIdEntidad(idContrato , nombreDeEntidad));
    }

    @Override
    public Optional<List<Variable>> findByIdContratoAndNombreEntidadAndNombreDeItem(Long idContrato, String nombreDeEntidad, String nombreDeItem) {

        return Optional.of(JPAVariableDao.findByIdContratoAndNombreEntidadAndNombreDeItem(idContrato, nombreDeEntidad,nombreDeItem));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Variable> findById(Long id) {
        return JPAVariableDao.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<List<Variable>> findByEntidad(String nombreDeEntidad){

        List<Variable> variables = JPAVariableDao.finByNombreEntidad(nombreDeEntidad);

        return Optional.of(variables);
    };

    @Override
    public void delete(Variable variable) {
        JPAVariableDao.delete(variable);
    }
}
