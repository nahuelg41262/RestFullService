package com.telefonica.entityservice.models.service.impl;

import com.telefonica.entityservice.models.entities.Alias;
import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.repositories.AliasJpaRepository;
import com.telefonica.entityservice.models.services.impl.AliasServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AliasServiceImplTest {

    @InjectMocks
    AliasServiceImpl aliasService;

    @Mock
    AliasJpaRepository aliasJpaRepository;

    @Test
    public void givenAlias_whenFindByName_thenAliasIsRetrived(){
        //when
        when(aliasJpaRepository.findByValor("valor.test")).thenReturn(List.of(new Alias("valor.test")));
        List<Alias> alias =  aliasService.findByName("valor.test");
        //then
        assertThat(alias.get(0).getValor()).isEqualTo("valor.test");
        verify(aliasJpaRepository, times(1)).findByValor("valor.test");
    }

    @Test
    public void givenAliasDoesNotExists_whenFindByName_thenEmpyListIsRetrived(){
        //when
        when(aliasJpaRepository.findByValor("valor.test")).thenReturn(new ArrayList<Alias>());
        List<Alias> alias =  aliasService.findByName("valor.test");
        //then
        assertThat(alias.isEmpty()).isEqualTo(true);
        verify(aliasJpaRepository, times(1)).findByValor("valor.test");
    }

    @Test
    public void givenAlias_whenDelete_thenDeleteIsExecute(){
        //given
        Alias alias = new Alias();
       //when
        doNothing().when(aliasJpaRepository).delete(alias);
        aliasService.delete(alias);
        //then
        verify(aliasJpaRepository, times(1)).delete(alias);
    }

    @Test
    public void givenAlias_WhenFindByID_thenAliasIsRetrived(){
        //when
        when(aliasJpaRepository.findById(1L)).thenReturn(Optional.of(new Alias("valor.test")));
        Optional<Alias> alias =  aliasService.findById(1L);
        //then
        assertThat(alias.get().getValor()).isEqualTo("valor.test");
        verify(aliasJpaRepository, times(1)).findById(1L);
    }

    @Test
    public void givenAliasDoesNotExists_WhenFindByID_thenAliasIsRetrived(){
        //when
        when(aliasJpaRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Alias> alias =  aliasService.findById(1L);
        //then
        assertThat(alias.isEmpty()).isEqualTo(true);
        verify(aliasJpaRepository, times(1)).findById(1L);
    }

    @Test
    public void givenVariable_WhenSave_thenSaveIsExecuted(){
        Alias alias = new Alias("valor.test");
        //when
        when(aliasJpaRepository.save(alias)).thenReturn(alias);
        aliasService.save(alias);
        //then
        verify(aliasJpaRepository, times(1)).save(alias);
    }
    @Test
    public void givenValorAndItem_WhenSaveAlias_ThenAliasHasValorAndItemAndSaveIsExecuted(){
        Alias alias = new Alias("valor.test");
        Item item = new Item();


        when(aliasJpaRepository.save(alias)).thenReturn(alias);
        aliasService.save("valor.test", item );

ArgumentCaptor<Alias> argument = ArgumentCaptor.forClass(Alias.class);
        verify(aliasJpaRepository, times(1)).save(argument.capture());
        assertThat("valor.test").isEqualTo(argument.getValue().getValor());
        assertThat(item).isEqualTo(argument.getValue().getItem());

    }
}
