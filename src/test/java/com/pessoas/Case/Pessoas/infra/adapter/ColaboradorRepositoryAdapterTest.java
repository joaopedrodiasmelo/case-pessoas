package com.pessoas.Case.Pessoas.infra.adapter;

import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.infra.mapper.ColaboradorMapper;
import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaColaboradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ColaboradorRepositoryAdapterTest {

    @Mock
    private JpaColaboradorRepository jpaRepository;

    @Mock
    private ColaboradorMapper mapper;

    @InjectMocks
    private ColaboradorRepositoryAdapter adapter;

    private Colaborador colaboradorDomain;
    private ColaboradorEntity colaboradorEntity;
    private final String matricula = "123456789";
    private final LocalDate dataAdmissao = LocalDate.now();

    @BeforeEach
    void setUp() {
        colaboradorDomain = new Colaborador(matricula, "Nome Domain", dataAdmissao, "Cargo Domain");
        colaboradorEntity = new ColaboradorEntity();
        colaboradorEntity.setMatricula(matricula);
        colaboradorEntity.setNome("Nome Entity");
        colaboradorEntity.setDataAdmissao(dataAdmissao);
        colaboradorEntity.setCargo("Cargo Entity");
    }


    @Test
    @DisplayName("salvar deve mapear para entidade, chamar JpaRepository.save e mapear de volta para domínio")
    void salvar_DeveMapearChamarSaveEMapearDeVolta() {

        when(mapper.toEntity(colaboradorDomain)).thenReturn(colaboradorEntity);
        when(jpaRepository.save(colaboradorEntity)).thenReturn(colaboradorEntity);
        when(mapper.toDomain(colaboradorEntity)).thenReturn(colaboradorDomain);

        Colaborador resultado = adapter.salvar(colaboradorDomain);

        assertNotNull(resultado);
        assertEquals(colaboradorDomain, resultado); // Verifica se o objeto retornado é o esperado

        verify(mapper, times(1)).toEntity(colaboradorDomain);
        verify(jpaRepository, times(1)).save(colaboradorEntity);
        verify(mapper, times(1)).toDomain(colaboradorEntity);
    }


    @Test
    @DisplayName("buscarPorMatricula deve retornar Optional com Colaborador quando encontrado")
    void buscarPorMatricula_QuandoEncontrado_RetornaOptionalComColaborador() {

        when(jpaRepository.findById(matricula)).thenReturn(Optional.of(colaboradorEntity));
        when(mapper.toDomain(colaboradorEntity)).thenReturn(colaboradorDomain);

        Optional<Colaborador> resultadoOpt = adapter.buscarPorMatricula(matricula);

        assertTrue(resultadoOpt.isPresent()); // Verifica se o Optional não está vazio
        assertEquals(colaboradorDomain, resultadoOpt.get()); // Verifica o conteúdo do Optional
        verify(jpaRepository, times(1)).findById(matricula);
        verify(mapper, times(1)).toDomain(colaboradorEntity);
    }

    @Test
    @DisplayName("buscarPorMatricula deve retornar Optional vazio quando não encontrado")
    void buscarPorMatricula_QuandoNaoEncontrado_RetornaOptionalVazio() {

        when(jpaRepository.findById(matricula)).thenReturn(Optional.empty());

        Optional<Colaborador> resultadoOpt = adapter.buscarPorMatricula(matricula);

        assertTrue(resultadoOpt.isEmpty()); // Verifica se o Optional está vazio
        verify(jpaRepository, times(1)).findById(matricula);

        verify(mapper, never()).toDomain(any(ColaboradorEntity.class));
    }


    @Test
    @DisplayName("existePorMatricula deve retornar true quando JpaRepository.existsById retorna true")
    void existePorMatricula_QuandoExiste_RetornaTrue() {

        when(jpaRepository.existsById(matricula)).thenReturn(true);

        boolean existe = adapter.existePorMatricula(matricula);

        assertTrue(existe);
        verify(jpaRepository, times(1)).existsById(matricula);
    }

    @Test
    @DisplayName("existePorMatricula deve retornar false quando JpaRepository.existsById retorna false")
    void existePorMatricula_QuandoNaoExiste_RetornaFalse() {

        when(jpaRepository.existsById(matricula)).thenReturn(false);

        boolean existe = adapter.existePorMatricula(matricula);

        assertFalse(existe);
        verify(jpaRepository, times(1)).existsById(matricula);
    }


    @Test
    @DisplayName("deletarPorMatricula deve chamar JpaRepository.deleteById")
    void deletarPorMatricula_DeveChamarDeleteById() {

        doNothing().when(jpaRepository).deleteById(matricula);

        adapter.deletarPorMatricula(matricula);

        verify(jpaRepository, times(1)).deleteById(matricula);
    }
}