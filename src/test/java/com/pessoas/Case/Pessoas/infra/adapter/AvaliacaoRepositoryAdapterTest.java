package com.pessoas.Case.Pessoas.infra.adapter;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import com.pessoas.Case.Pessoas.infra.mapper.AvaliacaoMapper;
import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoComportamentalEntity;
import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoEntregaEntity;
import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoId;
import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaAvaliacaoComportamentalRepository;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaAvaliacaoEntregaRepository;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaColaboradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoRepositoryAdapterTest {

    @Mock
    private JpaAvaliacaoComportamentalRepository compRepo;
    @Mock
    private JpaAvaliacaoEntregaRepository entregaRepo;
    @Mock
    private JpaColaboradorRepository colaboradorRepo;
    @Mock
    private AvaliacaoMapper mapper;

    @InjectMocks
    private AvaliacaoRepositoryAdapter adapter;

    private Colaborador colaborador;
    private ColaboradorEntity colaboradorEntity;
    private Avaliacao avaliacaoCompleta;
    private Avaliacao avaliacaoSoComp;
    private Avaliacao avaliacaoSoEntrega;
    private AvaliacaoComportamentalEntity compEntity;
    private AvaliacaoEntregaEntity entregaEntity;
    private AvaliacaoId avaliacaoId;
    private final String matricula = "MAT01";
    private final int numero = 2024;

    @BeforeEach
    void setUp() {
        colaborador = new Colaborador(matricula, "Nome Colab", LocalDate.now(), "Cargo Colab");
        colaboradorEntity = new ColaboradorEntity();
        colaboradorEntity.setMatricula(matricula);

        avaliacaoId = new AvaliacaoId(matricula, numero);

        compEntity = new AvaliacaoComportamentalEntity();
        compEntity.setId(avaliacaoId);
        compEntity.setColaborador(colaboradorEntity);

        entregaEntity = new AvaliacaoEntregaEntity();
        entregaEntity.setId(avaliacaoId);
        entregaEntity.setColaborador(colaboradorEntity);

        avaliacaoCompleta = new Avaliacao(colaborador, numero);
        avaliacaoCompleta.setComportamental(new AvaliacaoComportamental(4, 4, 4, 4));
        avaliacaoCompleta.setEntregas(List.of(new Desafio("D1", 5), new Desafio("D2", 3)));

        avaliacaoSoComp = new Avaliacao(colaborador, numero);
        avaliacaoSoComp.setComportamental(new AvaliacaoComportamental(3, 3, 3, 3));

        avaliacaoSoEntrega = new Avaliacao(colaborador, numero);
        avaliacaoSoEntrega.setEntregas(List.of(new Desafio("D3", 2), new Desafio("D4", 1)));
    }


    @Test
    @DisplayName("salvar deve criar ambas entidades quando Avaliacao é completa e nova")
    void salvar_QuandoCompletaENova_CriaAmbasEntidades() {
        when(colaboradorRepo.findById(matricula)).thenReturn(Optional.of(colaboradorEntity));
        // Primeira chamada retorna empty, segunda retorna a entidade (para a busca final)
        when(compRepo.findById(avaliacaoId)).thenReturn(Optional.empty()).thenReturn(Optional.of(compEntity));
        when(entregaRepo.findById(avaliacaoId)).thenReturn(Optional.empty()).thenReturn(Optional.of(entregaEntity));
        // Stub do mapper SÓ para a primeira chamada (com Optional.empty)
        when(mapper.toEntityComportamental(avaliacaoCompleta, colaboradorEntity, Optional.empty())).thenReturn(compEntity);
        when(mapper.toEntityEntrega(avaliacaoCompleta, colaboradorEntity, Optional.empty())).thenReturn(entregaEntity);
        // Stub do mapper SÓ para a busca final (com Optional preenchido)
        when(mapper.toDomain(Optional.of(compEntity), Optional.of(entregaEntity))).thenReturn(avaliacaoCompleta);

        Avaliacao resultado = adapter.salvar(avaliacaoCompleta);

        assertNotNull(resultado);
        verify(compRepo, times(1)).save(compEntity);
        verify(entregaRepo, times(1)).save(entregaEntity);
        verify(compRepo, never()).deleteById(any());
        verify(entregaRepo, never()).deleteById(any());
        // findById foi chamado 2 vezes para cada repo
        verify(compRepo, times(2)).findById(avaliacaoId);
        verify(entregaRepo, times(2)).findById(avaliacaoId);
    }

    @Test
    @DisplayName("salvar deve criar só comportamental quando Avaliacao só tem essa parte e é nova")
    void salvar_QuandoSoComportamentalENova_CriaSoCompEntidade() {

        when(colaboradorRepo.findById(matricula)).thenReturn(Optional.of(colaboradorEntity));
        // Primeira chamada não encontra, segunda (busca final) encontra o que foi salvo
        when(compRepo.findById(avaliacaoId))
                .thenReturn(Optional.empty()) // Chamada 1: Não existe
                .thenReturn(Optional.of(compEntity)); // Chamada 2: Existe (após save simulado)
        // Ambas as chamadas não encontram entrega
        when(entregaRepo.findById(avaliacaoId))
                .thenReturn(Optional.empty()) // Chamada 1: Não existe
                .thenReturn(Optional.empty()); // Chamada 2: Continua não existindo
        // Stub do mapper SÓ para a primeira chamada (Optional.empty)
        when(mapper.toEntityComportamental(avaliacaoSoComp, colaboradorEntity, Optional.empty())).thenReturn(compEntity);
        // Stub do mapper entrega retorna null pois avaliacaoSoComp não tem entregas
        when(mapper.toEntityEntrega(avaliacaoSoComp, colaboradorEntity, Optional.empty())).thenReturn(null);
        // Stub do mapper para a busca final (depois do save)
        when(mapper.toDomain(Optional.of(compEntity), Optional.empty())).thenReturn(avaliacaoSoComp);

        Avaliacao resultado = adapter.salvar(avaliacaoSoComp);

        assertNotNull(resultado);
        verify(compRepo, times(1)).save(compEntity);
        verify(entregaRepo, never()).save(any());
        verify(compRepo, never()).deleteById(any());
        verify(entregaRepo, never()).deleteById(any());
        // findById foi chamado 2 vezes para cada repo
        verify(compRepo, times(2)).findById(avaliacaoId);
        verify(entregaRepo, times(2)).findById(avaliacaoId);
    }

    @Test
    @DisplayName("salvar deve deletar entrega quando Avaliacao passa a ter só comportamental")
    void salvar_QuandoRemoveEntregas_DeletaEntregaEntidade() {

        AvaliacaoEntregaEntity entregaExistente = new AvaliacaoEntregaEntity();
        entregaExistente.setId(avaliacaoId); // Simula a entidade que existia

        when(colaboradorRepo.findById(matricula)).thenReturn(Optional.of(colaboradorEntity));
        // Primeira chamada não encontra comp, segunda (busca final) encontra
        when(compRepo.findById(avaliacaoId))
                .thenReturn(Optional.empty()) // Chamada 1: Não existia
                .thenReturn(Optional.of(compEntity)); // Chamada 2: Existe (foi salvo)
        // Primeira chamada encontra entrega, segunda (busca final) NÃO encontra (foi deletada)
        when(entregaRepo.findById(avaliacaoId))
                .thenReturn(Optional.of(entregaExistente)) // Chamada 1: Existia
                .thenReturn(Optional.empty()); // Chamada 2: Não existe mais
        // Stub do mapper comp para a primeira chamada (Optional.empty)
        when(mapper.toEntityComportamental(avaliacaoSoComp, colaboradorEntity, Optional.empty())).thenReturn(compEntity);
        // Stub do mapper entrega para a primeira chamada (Optional preenchido), retornando null
        when(mapper.toEntityEntrega(avaliacaoSoComp, colaboradorEntity, Optional.of(entregaExistente))).thenReturn(null);
        // Stub do mapper para a busca final (depois do save e delete)
        when(mapper.toDomain(Optional.of(compEntity), Optional.empty())).thenReturn(avaliacaoSoComp);
        // Configura o mock do delete para não fazer nada (é void)
        doNothing().when(entregaRepo).deleteById(avaliacaoId);


        Avaliacao resultado = adapter.salvar(avaliacaoSoComp); // Salva só com comportamental

        assertNotNull(resultado);
        verify(compRepo, times(1)).save(compEntity); // Salva comportamental
        verify(entregaRepo, never()).save(any()); // Não salva entrega
        verify(compRepo, never()).deleteById(any());
        verify(entregaRepo, times(1)).deleteById(avaliacaoId); // DELETA entrega
        // findById foi chamado 2 vezes para cada repo
        verify(compRepo, times(2)).findById(avaliacaoId);
        verify(entregaRepo, times(2)).findById(avaliacaoId);
    }

    @Test
    @DisplayName("salvar deve lançar exceção se colaborador não for encontrado")
    void salvar_QuandoColaboradorNaoExiste_LancaExcecao() {

        when(colaboradorRepo.findById(matricula)).thenReturn(Optional.empty()); // Colaborador não existe

        assertThrows(RegraDeNegocioException.class, // Ou a exceção que você definiu no orElseThrow
                () -> adapter.salvar(avaliacaoCompleta));

        // Verifica que nenhuma interação com repos de avaliação ocorreu
        verify(compRepo, never()).findById(any());
        verify(entregaRepo, never()).findById(any());
        verify(compRepo, never()).save(any());
        verify(entregaRepo, never()).save(any());
    }



    @Test
    @DisplayName("buscarPorMatriculaENumero deve retornar Avaliacao completa quando ambas partes existem")
    void buscarPorMatriculaENumero_QuandoAmbasExistem_RetornaAvaliacaoCompleta() {

        when(compRepo.findById(avaliacaoId)).thenReturn(Optional.of(compEntity));
        when(entregaRepo.findById(avaliacaoId)).thenReturn(Optional.of(entregaEntity));
        // Simula o mapper reconstruindo o objeto completo
        when(mapper.toDomain(Optional.of(compEntity), Optional.of(entregaEntity))).thenReturn(avaliacaoCompleta);

        Optional<Avaliacao> resultadoOpt = adapter.buscarPorMatriculaENumero(matricula, numero);

        assertTrue(resultadoOpt.isPresent());
        assertEquals(avaliacaoCompleta, resultadoOpt.get());
        verify(mapper, times(1)).toDomain(Optional.of(compEntity), Optional.of(entregaEntity));
    }

    @Test
    @DisplayName("buscarPorMatriculaENumero deve retornar Avaliacao parcial quando só comportamental existe")
    void buscarPorMatriculaENumero_QuandoSoCompExiste_RetornaAvaliacaoParcial() {

        when(compRepo.findById(avaliacaoId)).thenReturn(Optional.of(compEntity));
        when(entregaRepo.findById(avaliacaoId)).thenReturn(Optional.empty());
        // Simula o mapper reconstruindo o objeto parcial
        when(mapper.toDomain(Optional.of(compEntity), Optional.empty())).thenReturn(avaliacaoSoComp);

        Optional<Avaliacao> resultadoOpt = adapter.buscarPorMatriculaENumero(matricula, numero);

        assertTrue(resultadoOpt.isPresent());
        assertEquals(avaliacaoSoComp, resultadoOpt.get());
        verify(mapper, times(1)).toDomain(Optional.of(compEntity), Optional.empty());
    }

    @Test
    @DisplayName("buscarPorMatriculaENumero deve retornar Optional vazio quando nenhuma parte existe")
    void buscarPorMatriculaENumero_QuandoNenhumaParteExiste_RetornaOptionalVazio() {

        when(compRepo.findById(avaliacaoId)).thenReturn(Optional.empty());
        when(entregaRepo.findById(avaliacaoId)).thenReturn(Optional.empty());

        Optional<Avaliacao> resultadoOpt = adapter.buscarPorMatriculaENumero(matricula, numero);

        assertTrue(resultadoOpt.isEmpty());
        verify(mapper, never()).toDomain(any(), any()); // Mapper não deve ser chamado
    }


    @Test
    @DisplayName("buscarPorMatricula deve retornar lista de Avaliacoes reconstruídas")
    void buscarPorMatricula_QuandoExistemAvaliacoes_RetornaLista() {

        int numero2 = 2025;
        AvaliacaoId id2 = new AvaliacaoId(matricula, numero2);
        AvaliacaoComportamentalEntity compEntity2 = new AvaliacaoComportamentalEntity(); // Mock para num 2
        compEntity2.setId(id2);
        AvaliacaoEntregaEntity entregaEntity1 = new AvaliacaoEntregaEntity(); // Mock só entrega para num 1
        entregaEntity1.setId(avaliacaoId);
        Avaliacao avaliacaoNum1 = new Avaliacao(colaborador, numero); // Só entrega reconstruída
        Avaliacao avaliacaoNum2 = new Avaliacao(colaborador, numero2); // Só comp reconstruída

        when(compRepo.findById_ColaboradorMatricula(matricula)).thenReturn(List.of(compEntity2)); // Só encontra num 2
        when(entregaRepo.findById_ColaboradorMatricula(matricula)).thenReturn(List.of(entregaEntity1)); // Só encontra num 1

        // Simula o mapper sendo chamado para cada número único (1 e 2)
        when(mapper.toDomain(Optional.empty(), Optional.of(entregaEntity1))).thenReturn(avaliacaoNum1);
        when(mapper.toDomain(Optional.of(compEntity2), Optional.empty())).thenReturn(avaliacaoNum2);

        List<Avaliacao> resultado = adapter.buscarPorMatricula(matricula);

        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // Espera encontrar duas avaliações (num 1 e num 2)
        assertTrue(resultado.contains(avaliacaoNum1));
        assertTrue(resultado.contains(avaliacaoNum2));
        // Verifica se o mapper foi chamado corretamente para cada 'numero'
        verify(mapper, times(1)).toDomain(Optional.empty(), Optional.of(entregaEntity1));
        verify(mapper, times(1)).toDomain(Optional.of(compEntity2), Optional.empty());
    }

    @Test
    @DisplayName("buscarPorMatricula deve retornar lista vazia quando não há avaliações")
    void buscarPorMatricula_QuandoNaoExistemAvaliacoes_RetornaListaVazia() {

        when(compRepo.findById_ColaboradorMatricula(matricula)).thenReturn(Collections.emptyList());
        when(entregaRepo.findById_ColaboradorMatricula(matricula)).thenReturn(Collections.emptyList());

        List<Avaliacao> resultado = adapter.buscarPorMatricula(matricula);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(mapper, never()).toDomain(any(), any()); // Mapper não deve ser chamado
    }
}