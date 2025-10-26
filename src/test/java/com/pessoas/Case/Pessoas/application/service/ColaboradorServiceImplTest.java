package com.pessoas.Case.Pessoas.application.service;

import com.pessoas.Case.Pessoas.application.dto.ColaboradorDTO;
import com.pessoas.Case.Pessoas.application.exception.RecursoNaoEncontradoException;
import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.application.mapper.ColaboradorApiMapper;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.repository.ColaboradorRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceImplTest {

    @Mock
    private ColaboradorRepositoryPort colaboradorRepository;

    @Mock
    private ColaboradorApiMapper apiMapper;

    @InjectMocks
    private ColaboradorServiceImpl colaboradorService;


    private ColaboradorDTO colaboradorDTO;
    private Colaborador colaborador;
    private Colaborador colaboradorAtualizado;
    private ColaboradorDTO colaboradorAtualizadoDTO;
    private final String matriculaExistente = "123456789";
    private final String matriculaNova = "987654321";
    private final LocalDate dataAdmissao = LocalDate.of(2023, 1, 1);

    @BeforeEach
    void setUp() {
        colaboradorDTO = new ColaboradorDTO(matriculaNova, "Novo Colaborador", dataAdmissao, "Dev Jr");
        colaborador = new Colaborador(matriculaNova, "Novo Colaborador", dataAdmissao, "Dev Jr");

        colaboradorAtualizadoDTO = new ColaboradorDTO(matriculaExistente, "Colaborador Atualizado", dataAdmissao.plusMonths(6), "Dev Pleno");
        colaboradorAtualizado = new Colaborador(matriculaExistente, "Colaborador Atualizado", dataAdmissao.plusMonths(6), "Dev Pleno");
    }


    @Test
    @DisplayName("criarColaborador deve salvar com sucesso quando matrícula não existe")
    void criarColaborador_QuandoMatriculaNova_DeveSalvarComSucesso() {

        when(colaboradorRepository.existePorMatricula(matriculaNova)).thenReturn(false); // Matrícula não existe
        when(apiMapper.toDomain(colaboradorDTO)).thenReturn(colaborador); // Mapper DTO -> Domain
        when(colaboradorRepository.salvar(any(Colaborador.class))).thenReturn(colaborador); // Repo Salva
        when(apiMapper.toDTO(colaborador)).thenReturn(colaboradorDTO); // Mapper Domain -> DTO (retorno)

        ColaboradorDTO resultado = colaboradorService.criarColaborador(colaboradorDTO);

        assertNotNull(resultado);
        assertEquals(matriculaNova, resultado.matricula());

        verify(colaboradorRepository, times(1)).existePorMatricula(matriculaNova);
        verify(apiMapper, times(1)).toDomain(colaboradorDTO);
        verify(colaboradorRepository, times(1)).salvar(colaborador);
        verify(apiMapper, times(1)).toDTO(colaborador);
    }

    @Test
    @DisplayName("criarColaborador deve lançar exceção quando matrícula já existe")
    void criarColaborador_QuandoMatriculaExiste_DeveLancarRegraDeNegocioException() {

        when(colaboradorRepository.existePorMatricula(matriculaNova)).thenReturn(true); // Matrícula JÁ existe

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            colaboradorService.criarColaborador(colaboradorDTO);
        });
        assertTrue(exception.getMessage().contains("já cadastrada"));

        verify(colaboradorRepository, never()).salvar(any(Colaborador.class));
        verify(apiMapper, never()).toDomain(any());
        verify(apiMapper, never()).toDTO(any());
    }


    @Test
    @DisplayName("buscarColaborador deve retornar DTO quando matrícula existe")
    void buscarColaborador_QuandoMatriculaExiste_DeveRetornarDTO() {

        when(colaboradorRepository.buscarPorMatricula(matriculaExistente)).thenReturn(Optional.of(colaborador));
        when(apiMapper.toDTO(colaborador)).thenReturn(colaboradorDTO); // Assume que o mapper retornaria um DTO correspondente

        ColaboradorDTO resultado = colaboradorService.buscarColaborador(matriculaExistente);

        assertNotNull(resultado);
        assertEquals(colaboradorDTO, resultado); // Compara os DTOs
        verify(colaboradorRepository, times(1)).buscarPorMatricula(matriculaExistente);
        verify(apiMapper, times(1)).toDTO(colaborador);
    }

    @Test
    @DisplayName("buscarColaborador deve lançar exceção quando matrícula não existe")
    void buscarColaborador_QuandoMatriculaNaoExiste_DeveLancarRecursoNaoEncontradoException() {

        when(colaboradorRepository.buscarPorMatricula(matriculaExistente)).thenReturn(Optional.empty()); // Repo não encontra

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            colaboradorService.buscarColaborador(matriculaExistente);
        });

        verify(apiMapper, never()).toDTO(any());
    }


    @Test
    @DisplayName("atualizarColaborador deve salvar com sucesso quando dados são válidos")
    void atualizarColaborador_QuandoDadosValidos_DeveAtualizarComSucesso() {

        Colaborador colaboradorOriginal = new Colaborador(matriculaExistente, "Nome Antigo", dataAdmissao, "Cargo Antigo");
        when(colaboradorRepository.buscarPorMatricula(matriculaExistente)).thenReturn(Optional.of(colaboradorOriginal)); // Encontra o original
        when(colaboradorRepository.salvar(any(Colaborador.class))).thenReturn(colaboradorAtualizado); // Simula o save retornando o atualizado
        when(apiMapper.toDTO(colaboradorAtualizado)).thenReturn(colaboradorAtualizadoDTO); // Mapper retorna DTO atualizado

        ColaboradorDTO resultado = colaboradorService.atualizarColaborador(matriculaExistente, colaboradorAtualizadoDTO);

        assertNotNull(resultado);
        assertEquals(colaboradorAtualizadoDTO, resultado);
        assertEquals("Colaborador Atualizado", colaboradorOriginal.getNome()); // Verifica se o objeto original foi modificado
        assertEquals("Dev Pleno", colaboradorOriginal.getCargo());
        verify(colaboradorRepository, times(1)).buscarPorMatricula(matriculaExistente);
        verify(colaboradorRepository, times(1)).salvar(colaboradorOriginal); // Verifica se salvou o objeto modificado
        verify(apiMapper, times(1)).toDTO(colaboradorAtualizado);
    }

    @Test
    @DisplayName("atualizarColaborador deve lançar exceção ao tentar mudar matrícula")
    void atualizarColaborador_QuandoMatriculaDiferente_DeveLancarRegraDeNegocioException() {

        ColaboradorDTO dtoComMatriculaDiferente = new ColaboradorDTO(matriculaNova, "Nome", dataAdmissao, "Cargo");

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            // A matrícula na URL (existente) é diferente da matrícula no DTO (nova)
            colaboradorService.atualizarColaborador(matriculaExistente, dtoComMatriculaDiferente);
        });
        assertTrue(exception.getMessage().contains("Não é permitido alterar a matrícula"));

        verify(colaboradorRepository, never()).buscarPorMatricula(anyString());
        verify(colaboradorRepository, never()).salvar(any(Colaborador.class));
    }

    @Test
    @DisplayName("atualizarColaborador deve lançar exceção quando colaborador não existe")
    void atualizarColaborador_QuandoColaboradorNaoExiste_DeveLancarRecursoNaoEncontradoException() {

        when(colaboradorRepository.buscarPorMatricula(matriculaExistente)).thenReturn(Optional.empty()); // Repo não encontra

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            colaboradorService.atualizarColaborador(matriculaExistente, colaboradorAtualizadoDTO);
        });

        verify(colaboradorRepository, never()).salvar(any(Colaborador.class));
    }


    @Test
    @DisplayName("deletarColaborador deve chamar delete quando matrícula existe")
    void deletarColaborador_QuandoMatriculaExiste_DeveChamarDelete() {

        when(colaboradorRepository.existePorMatricula(matriculaExistente)).thenReturn(true); // Repo encontra

        doNothing().when(colaboradorRepository).deletarPorMatricula(matriculaExistente);

        colaboradorService.deletarColaborador(matriculaExistente);

        verify(colaboradorRepository, times(1)).existePorMatricula(matriculaExistente);
        verify(colaboradorRepository, times(1)).deletarPorMatricula(matriculaExistente);
    }

    @Test
    @DisplayName("deletarColaborador deve lançar exceção quando matrícula não existe")
    void deletarColaborador_QuandoMatriculaNaoExiste_DeveLancarRecursoNaoEncontradoException() {

        when(colaboradorRepository.existePorMatricula(matriculaExistente)).thenReturn(false); // Repo não encontra

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            colaboradorService.deletarColaborador(matriculaExistente);
        });

        verify(colaboradorRepository, never()).deletarPorMatricula(anyString());
    }
}