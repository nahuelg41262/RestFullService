package com.telefonica.entityservice.models.services.impl;

import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.repositories.AliasJpaRepository;
import com.telefonica.entityservice.models.services.AliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class AliasServiceImpl extends ServiceDTO implements AliasService {

    @Autowired
    AliasJpaRepository aliasJpaRepository;

    @Transactional(readOnly = true)
    public List<Alias> findByName(String name) {
        return aliasJpaRepository.findByValor(name);
    }

    @Transactional(readOnly = true)
    public Optional<Alias> findById(Long id) {
        return aliasJpaRepository.findById(id);
    }

    @Transactional
    public void delete(Alias alias) {
        aliasJpaRepository.delete(alias);
    }

    @Transactional
    public void save(String valor, Item item) {
        Alias alias = new Alias();
        alias.setValor(valor);
        alias.setItem(item);
        aliasJpaRepository.save(alias);
    }
	@Transactional
    public void save(Alias alias) {
		aliasJpaRepository.save(alias);
    }
}
