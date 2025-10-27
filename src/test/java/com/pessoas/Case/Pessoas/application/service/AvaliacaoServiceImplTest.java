package com.pessoas.Case.Pessoas.application.service;

import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.application.exception.RecursoNaoEncontradoException;
import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.application.mapper.AvaliacaoApiMapper;
import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import com.pessoas.Case.Pessoas.domain.repository.AvaliacaoRepositoryPort;
import com.pessoas.Case.Pessoas.domain.repository.ColaboradorRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceImplTest {

    @Mock
    private AvaliacaoRepositoryPort avaliacaoRepository;
    @Mock
    private ColaboradorRepositoryPort colaboradorRepository;
    @Mock
    private AvaliacaoApiMapper apiMapper;

    @InjectMocks
    private AvaliacaoServiceImpl avaliacaoService;

    private Colaborador colaborador;
    private Avaliacao avaliacaoExistente;
    private AvaliacaoComportamentalDTO comportamentalDTO;
    private AvaliacaoComportamental comportamental;
    private AvaliacaoEntregasRequestDTO entregasDTO;
    private List<Desafio> desafios;
    private AvaliacaoCompletaRequestDTO completaDTO; // Novo DTO para teste
    private PerformanceGeralDTO performanceDTO;
    private final String matricula = "111222333";
    private final int numero = 2025;

    @BeforeEach
    void setUp() {
        colaborador = new Colaborador(matricula, "Colab Teste", LocalDate.now(), "Tester");
        avaliacaoExistente = new Avaliacao(colaborador, numero);
        comportamentalDTO = new AvaliacaoComportamentalDTO(4, 4, 4, 4);
        comportamental = new AvaliacaoComportamental(4, 4, 4, 4);
        desafios = List.of(new Desafio("D1", 5), new Desafio("D2", 3));
        entregasDTO = new AvaliacaoEntregasRequestDTO(List.of(new DesafioDTO("D1", 5), new DesafioDTO("D2", 3)));
        completaDTO = new AvaliacaoCompletaRequestDTO(comportamentalDTO, List.of(new DesafioDTO("D1", 5), new DesafioDTO("D2", 3))); // Usa DTOs comportamental e desafios
        performanceDTO = new PerformanceGeralDTO(matricula, numero, 0, 0, 0);
    }


    @Test
    @DisplayName("findOrCreate deve criar nova Avaliacao se não existir")
    void findOrCreate_QuandoNaoExiste_CriaNova() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.empty());
        when(colaboradorRepository.buscarPorMatricula(matricula)).thenReturn(Optional.of(colaborador));
        when(avaliacaoRepository.salvar(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apiMapper.toPerformanceDTO(any(Avaliacao.class))).thenReturn(performanceDTO);
        when(apiMapper.toDomain(comportamentalDTO)).thenReturn(comportamental);

        avaliacaoService.cadastrarAvaliacaoComportamental(matricula, numero, comportamentalDTO);

        verify(colaboradorRepository, times(1)).buscarPorMatricula(matricula);
        verify(avaliacaoRepository, times(1)).salvar(argThat(arg -> arg.getNumero() == numero && arg.getColaborador() == colaborador && arg.getComportamental() != null));
    }

    @Test
    @DisplayName("findOrCreate deve retornar Avaliacao existente")
    void findOrCreate_QuandoExiste_RetornaExistente() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente));
        when(avaliacaoRepository.salvar(any(Avaliacao.class))).thenReturn(avaliacaoExistente);
        when(apiMapper.toPerformanceDTO(any(Avaliacao.class))).thenReturn(performanceDTO);
        when(apiMapper.toDomain(comportamentalDTO)).thenReturn(comportamental);

        avaliacaoService.cadastrarAvaliacaoComportamental(matricula, numero, comportamentalDTO);

        verify(colaboradorRepository, never()).buscarPorMatricula(anyString());
        verify(avaliacaoRepository, times(1)).salvar(avaliacaoExistente);
    }

    @Test
    @DisplayName("findOrCreate deve lançar exceção se colaborador não existe ao criar")
    void findOrCreate_QuandoColaboradorNaoExisteAoCriar_LancaExcecao() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.empty());
        when(colaboradorRepository.buscarPorMatricula(matricula)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> avaliacaoService.cadastrarAvaliacaoComportamental(matricula, numero, comportamentalDTO));

        verify(avaliacaoRepository, never()).salvar(any());
    }


    @Test
    @DisplayName("cadastrarComportamental deve chamar salvar e retornar DTO")
    void cadastrarComportamental_FluxoNormal_ChamaSalvarERetornaDTO() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente)); // Simula existente
        when(apiMapper.toDomain(comportamentalDTO)).thenReturn(comportamental);
        when(avaliacaoRepository.salvar(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o arg
        when(apiMapper.toPerformanceDTO(any(Avaliacao.class))).thenReturn(performanceDTO);

        PerformanceGeralDTO resultado = avaliacaoService.cadastrarAvaliacaoComportamental(matricula, numero, comportamentalDTO);

        assertEquals(performanceDTO, resultado);
        verify(avaliacaoRepository, times(1)).salvar(argThat(a -> a.getComportamental() == comportamental));
        verify(apiMapper, times(1)).toPerformanceDTO(any(Avaliacao.class));
    }

    @Test
    @DisplayName("cadastrarEntregas deve chamar salvar e retornar DTO")
    void cadastrarEntregas_FluxoNormal_ChamaSalvarERetornaDTO() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente));
        when(apiMapper.toDomain(entregasDTO)).thenReturn(desafios);
        when(avaliacaoRepository.salvar(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apiMapper.toPerformanceDTO(any(Avaliacao.class))).thenReturn(performanceDTO);

        PerformanceGeralDTO resultado = avaliacaoService.cadastrarAvaliacaoEntregas(matricula, numero, entregasDTO);

        assertEquals(performanceDTO, resultado);
        verify(avaliacaoRepository, times(1)).salvar(argThat(a -> a.getDesafios() == desafios));
        verify(apiMapper, times(1)).toPerformanceDTO(any(Avaliacao.class));
    }

    @Test
    @DisplayName("cadastrarEntregas deve lançar exceção se setEntregas falhar (ex: 1 desafio)")
    void cadastrarEntregas_QuandoSetEntregasFalha_LancaExcecao() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente));
        AvaliacaoEntregasRequestDTO dtoInvalido = new AvaliacaoEntregasRequestDTO(List.of(new DesafioDTO("D1", 1)));
        List<Desafio> desafiosInvalidos = List.of(new Desafio("D1", 1));
        when(apiMapper.toDomain(dtoInvalido)).thenReturn(desafiosInvalidos);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> avaliacaoService.cadastrarAvaliacaoEntregas(matricula, numero, dtoInvalido));

        assertTrue(ex.getMessage().contains("mínimo 2 e no máximo 4"));
        verify(avaliacaoRepository, never()).salvar(any());
    }


    @Test
    @DisplayName("cadastrarCompleta deve setar ambas as partes, chamar salvar e retornar DTO")
    void cadastrarCompleta_FluxoNormal_ChamaSalvarERetornaDTO() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente)); // Simula existente
        when(apiMapper.toDomain(completaDTO.comportamental())).thenReturn(comportamental);

        AvaliacaoEntregasRequestDTO tempEntregasDTO = new AvaliacaoEntregasRequestDTO(completaDTO.desafios());
        when(apiMapper.toDomain(tempEntregasDTO)).thenReturn(desafios);
        when(avaliacaoRepository.salvar(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apiMapper.toPerformanceDTO(any(Avaliacao.class))).thenReturn(performanceDTO);

        PerformanceGeralDTO resultado = avaliacaoService.cadastrarAvaliacaoCompleta(matricula, numero, completaDTO);

        assertEquals(performanceDTO, resultado);
        verify(avaliacaoRepository, times(1)).salvar(argThat(a -> a.getComportamental() == comportamental && a.getDesafios() == desafios));
        verify(apiMapper, times(1)).toPerformanceDTO(any(Avaliacao.class));
    }

    @Test
    @DisplayName("cadastrarCompleta deve lançar exceção se setEntregas falhar")
    void cadastrarCompleta_QuandoSetEntregasFalha_LancaExcecao() {

        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente));
        when(apiMapper.toDomain(completaDTO.comportamental())).thenReturn(comportamental); // Mapeia comportamental ok

        AvaliacaoCompletaRequestDTO dtoInvalido = new AvaliacaoCompletaRequestDTO(comportamentalDTO, List.of(new DesafioDTO("D1", 1)));

        AvaliacaoEntregasRequestDTO tempEntregasDTO = new AvaliacaoEntregasRequestDTO(dtoInvalido.desafios());
        List<Desafio> desafiosInvalidos = List.of(new Desafio("D1", 1));
        when(apiMapper.toDomain(tempEntregasDTO)).thenReturn(desafiosInvalidos);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> avaliacaoService.cadastrarAvaliacaoCompleta(matricula, numero, dtoInvalido));

        assertTrue(ex.getMessage().contains("mínimo 2 e no máximo 4"));
        verify(avaliacaoRepository, never()).salvar(any()); // Verifica que não tentou salvar
    }



    @Test
    @DisplayName("recuperarPerformance deve retornar DTO quando avaliação existe")
    void recuperarPerformance_QuandoAvaliacaoExiste_RetornaDTO() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.of(avaliacaoExistente));
        when(apiMapper.toPerformanceDTO(avaliacaoExistente)).thenReturn(performanceDTO);

        PerformanceGeralDTO resultado = avaliacaoService.recuperarPerformance(matricula, numero);

        assertEquals(performanceDTO, resultado);
        verify(avaliacaoRepository, times(1)).buscarPorMatriculaENumero(matricula, numero);
        verify(apiMapper, times(1)).toPerformanceDTO(avaliacaoExistente);
    }

    @Test
    @DisplayName("recuperarPerformance deve lançar exceção quando avaliação não existe")
    void recuperarPerformance_QuandoAvaliacaoNaoExiste_LancaExcecao() {
        when(avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> avaliacaoService.recuperarPerformance(matricula, numero));

        verify(apiMapper, never()).toPerformanceDTO(any());
    }


    @Test
    @DisplayName("recuperarHistorico deve retornar lista de DTOs")
    void recuperarHistorico_QuandoExistemAvaliacoes_RetornaListaDTO() {
        Avaliacao avaliacao2 = new Avaliacao(colaborador, 2026);
        List<Avaliacao> listaAvaliacoes = List.of(avaliacaoExistente, avaliacao2);
        PerformanceGeralDTO dto2 = new PerformanceGeralDTO(matricula, 2026, 0,0,0);

        when(avaliacaoRepository.buscarPorMatricula(matricula)).thenReturn(listaAvaliacoes);
        when(apiMapper.toPerformanceDTO(avaliacaoExistente)).thenReturn(performanceDTO);
        when(apiMapper.toPerformanceDTO(avaliacao2)).thenReturn(dto2);

        List<PerformanceGeralDTO> resultado = avaliacaoService.recuperarHistoricoPerformance(matricula);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(performanceDTO));
        assertTrue(resultado.contains(dto2));

        verify(avaliacaoRepository, times(1)).buscarPorMatricula(matricula);
        verify(apiMapper, times(2)).toPerformanceDTO(any(Avaliacao.class));
    }

    @Test
    @DisplayName("recuperarHistorico deve retornar lista vazia se não houver avaliações")
    void recuperarHistorico_QuandoNaoExistemAvaliacoes_RetornaListaVazia() {
        when(avaliacaoRepository.buscarPorMatricula(matricula)).thenReturn(Collections.emptyList());

        List<PerformanceGeralDTO> resultado = avaliacaoService.recuperarHistoricoPerformance(matricula);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(avaliacaoRepository, times(1)).buscarPorMatricula(matricula);
        verify(apiMapper, never()).toPerformanceDTO(any());
    }
}