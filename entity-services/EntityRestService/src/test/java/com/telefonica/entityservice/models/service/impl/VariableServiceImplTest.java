package com.telefonica.entityservice.models.service.impl;

import com.telefonica.entityservice.models.entities.Item;
import com.telefonica.entityservice.models.entities.Variable;
import com.telefonica.entityservice.models.repositories.EntidadJpaRepository;
import com.telefonica.entityservice.models.repositories.VariableJpaRepository;
import com.telefonica.entityservice.models.services.impl.VariableServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VariableServiceImplTest {

    @InjectMocks
    VariableServiceImpl varaiableService;


    @Mock
    VariableJpaRepository variableJpaRepository;

    @Mock
    EntidadJpaRepository entidadJpaRepository;

    @Test
    public void GivenVariable_WhenFindById_thenVariableIsRetrived() {
        //given
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setId(1L);
        variable.setIdContrato(2L);
        //when
        when(variableJpaRepository.findById(1L)).thenReturn(Optional.of(variable));
        Optional<Variable> var = varaiableService.findById(1L);
        //then
        assertThat(var.get().getValor()).isEqualTo("variable.test");
        verify(variableJpaRepository, times(1)).findById(1L);

    }

    @Test
    public void GivenVariableDoesNotExists_WhenFindById_thenEmpyOptionalIsRetrived() {
        //given
        // Any Variable
        //when
        when(variableJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThat(varaiableService.findById(1L)).isEqualTo(Optional.empty());
    }

    @Test
    public void GivenVariables_WhenFindByIdContrato_thenListOfVariblesIsRetrived() {
        //given
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setId(1L);
        variable.setIdContrato(2L);
        Variable variable2 = new Variable();
        variable2.setValor("variable2.test");
        variable2.setId(2L);
        variable2.setIdContrato(2L);

        //when
        when(variableJpaRepository.findByIdContrato(2L)).thenReturn(List.of(variable, variable2));
        List<Variable> found = varaiableService.findByIdContrato(2L).get();

        //then
        assertThat(found.get(0).getValor()).isEqualTo(variable.getValor());
        assertThat(found.get(1).getValor()).isEqualTo(variable2.getValor());
        verify(variableJpaRepository, times(1)).findByIdContrato(2L);
    }

    @Test
    public void GivenVariablesWithDiferentIdContratos_WhenFindByIdContrato_thenListOfOneVaribleIsRetrived() {
        //given
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setId(1L);
        variable.setIdContrato(2L);
        Variable variable2 = new Variable();
        variable2.setValor("variable2.test");
        variable2.setId(2L);
        variable2.setIdContrato(80L);

        //when
        when(variableJpaRepository.findByIdContrato(2L)).thenReturn(List.of(variable));
        List<Variable> found = varaiableService.findByIdContrato(2L).get();

        //then
        assertThat(found.get(0).getValor()).isEqualTo(variable.getValor());
        assertThat(found.size()).isEqualTo(1);
        verify(variableJpaRepository, times(1)).findByIdContrato(2L);

    }

    @Test
    public void GivenVariable_WhenFindByIdContratoAndIdEntidad_thenVaribleIsRetrived() {
        //given
        Variable variable = new Variable();
        variable.setIdContrato(1L);
        // when
        when(variableJpaRepository.findByIdContratoAndIdEntidad(2L, "entidad.test")).thenReturn(List.of(variable));
        List<Variable> found = varaiableService.findByIdContratoAndIdEntidad(2L, "entidad.test").get();

        assertThat(found.get(0).getIdContrato()).isEqualTo(1L);
        verify(variableJpaRepository, times(1)).findByIdContratoAndIdEntidad(2L, "entidad.test");

    }

    @Test
    public void GivenVariablesWithDiferentIdContratos_WhenFindByIdContratoAndIdEntidad_thenListOfOneVaribleIsRetrived() {
        //given
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setId(1L);
        variable.setIdContrato(2L);
        Variable variable2 = new Variable();
        variable2.setValor("variable2.test");
        variable2.setId(2L);
        variable2.setIdContrato(80L);

        //when
        when(variableJpaRepository.findByIdContratoAndIdEntidad(2L, "entidad.test")).thenReturn(List.of(variable));
        List<Variable> found = varaiableService.findByIdContratoAndIdEntidad(2L, "entidad.test").get();

        //then
        assertThat(found.get(0).getValor()).isEqualTo(variable.getValor());
        assertThat(found.size()).isEqualTo(1);
        verify(variableJpaRepository, times(1)).findByIdContratoAndIdEntidad(2L, "entidad.test");

    }

    @Test
    public void givenVariable_whenFindByIdContratoAndIdEntidadAndNombreDeItem_VariableJpaRepositoryIsExecutedAndVariablesIsRetrived() {

        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setIdContrato(1L);

        Variable variable2 = new Variable();
        variable2.setValor("variable2.test");
        variable2.setIdContrato(1L);
        //when
        when(variableJpaRepository.findByIdContratoAndNombreEntidadAndNombreDeItem(1L, "entidad.test", "item.test")).thenReturn(List.of(variable, variable2));
        Optional<List<Variable>> variables = varaiableService.findByIdContratoAndNombreEntidadAndNombreDeItem(1L, "entidad.test", "item.test");

        //then
        assertThat(variables.get().get(0)).isEqualTo(variable);
        assertThat(variables.get().size()).isEqualTo(2);
        verify(variableJpaRepository, times(1)).findByIdContratoAndNombreEntidadAndNombreDeItem(1L, "entidad.test", "item.test");
    }

    @Test
    public void ShouldEmpyListOfVariable_WhenListEmpyIsRetrived(){
        //when
        when(variableJpaRepository.findByIdContratoAndNombreEntidadAndNombreDeItem(1L, "entidad.test", "item.test")).thenReturn(List.of());
        Optional<List<Variable>> variables = varaiableService.findByIdContratoAndNombreEntidadAndNombreDeItem(1L, "entidad.test", "item.test");
        //then
        assertThat(variables.get().isEmpty()).isEqualTo(true);
    }

    @Test
    public void givenEntidadWithVariables_WhenFinByEntidad_ListOfVariableISRetrived() {
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setIdContrato(1L);

        Variable variable2 = new Variable();
        variable2.setValor("variable2.test");
        variable2.setIdContrato(1L);

        //when
        when(variableJpaRepository.finByNombreEntidad("entidad.test")).thenReturn(List.of(variable,variable2));
        Optional<List<Variable>> variables = varaiableService.findByEntidad( "entidad.test");

        //then
        assertThat(variables.get().get(0)).isEqualTo(variable);
        assertThat(variables.get().size()).isEqualTo(2);
    }

    @Test
    public void ShouldEmpyListOfVariable_WhenEntidadNotHaveAnyVariableVariablesIsRetrived() {
        //when
        when(variableJpaRepository.finByNombreEntidad("entidad.test")).thenReturn(List.of());
        Optional<List<Variable>> variables = varaiableService.findByEntidad( "entidad.test");

        //then
        assertThat(variables.get().isEmpty()).isEqualTo(true);
    }

    @Test
    public void ShouldEmpyListOfVariable_WhenEntidadDoesNotExist() {
        //when
        Optional<List<Variable>> variables = varaiableService.findByEntidad( "entidad.test");

        //then
        assertThat(variables.get().isEmpty()).isEqualTo(true);
    }


    @Test
    public void givenVariable_whenSave_VairableIsReceivedAndVariableJpaRepositorySaveIsExecuted(){
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setIdContrato(1L);
        variable.setId(1L);

        //when
        when(variableJpaRepository.save(variable)).thenReturn(variable);
        Variable saved = varaiableService.save(variable);

        verify(variableJpaRepository, times(1)).save(variable);
        assertThat(saved.getValor()).isEqualTo(variable.getValor());
    }

    @Test
    public void givenItemValorAndContratoId_whenSave_VariableJpaRepositoryIsExecuted(){
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setIdContrato(1L);
        variable.setId(1L);
        Item item = new Item();

        //when
        varaiableService.save(item ,"variable.test" ,1L);


        ArgumentCaptor<Variable> argument = ArgumentCaptor.forClass(Variable.class);
        verify(variableJpaRepository, times(1)).save(argument.capture());
        assertThat("variable.test").isEqualTo(argument.getValue().getValor());
        assertThat(item).isEqualTo(argument.getValue().getIdItem());
    }

    @Test
    public void givenVariable_whenDelete_VariableJpaRepositoryDeleteIsExecuted() {
        //given
        Variable variable = new Variable();
        variable.setValor("variable.test");
        variable.setId(1L);
        //when
        doNothing().when(variableJpaRepository).delete(variable);
        varaiableService.delete(variable);
        //then
        verify(variableJpaRepository, times(1)).delete(variable);
    }

}
