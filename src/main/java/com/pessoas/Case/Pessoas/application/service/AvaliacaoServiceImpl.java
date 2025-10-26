package com.pessoas.Case.Pessoas.application.service;

import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.application.exception.RecursoNaoEncontradoException;
import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.application.mapper.AvaliacaoApiMapper;
import com.pessoas.Case.Pessoas.application.portApi.AvaliacaoUseCase;
import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import com.pessoas.Case.Pessoas.domain.repository.AvaliacaoRepositoryPort;
import com.pessoas.Case.Pessoas.domain.repository.ColaboradorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliacaoServiceImpl implements AvaliacaoUseCase {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoServiceImpl.class);
    private final AvaliacaoRepositoryPort avaliacaoRepository;
    private final ColaboradorRepositoryPort colaboradorRepository;
    private final AvaliacaoApiMapper apiMapper;

    @Override
    @Transactional
    public PerformanceGeralDTO cadastrarAvaliacaoComportamental(String matricula, int numero, AvaliacaoComportamentalDTO dto) {
        log.info("Iniciando caso de uso [cadastrarAvaliacaoComportamental] para {}/{}", matricula, numero);

        Avaliacao avaliacao = findOrCreateAvaliacaoDomain(matricula, numero);

        AvaliacaoComportamental comportamental = apiMapper.toDomain(dto);
        avaliacao.setComportamental(comportamental);

        Avaliacao avaliacaoSalva = avaliacaoRepository.salvar(avaliacao);

        log.info("Caso de uso [cadastrarAvaliacaoComportamental] finalizado com sucesso para {}/{}", matricula, numero);
        return apiMapper.toPerformanceDTO(avaliacaoSalva);
    }

    @Override
    @Transactional
    public PerformanceGeralDTO cadastrarAvaliacaoEntregas(String matricula, int numero, AvaliacaoEntregasRequestDTO dto) {
        log.info("Iniciando caso de uso [cadastrarAvaliacaoEntregas] para {}/{} com {} desafios", matricula, numero, dto.desafios().size());

        Avaliacao avaliacao = findOrCreateAvaliacaoDomain(matricula, numero);

        List<Desafio> desafios = apiMapper.toDomain(dto);

        try {
            avaliacao.setEntregas(desafios);
        } catch (RegraDeNegocioException ex) {
            log.warn("Falha ao definir entregas para {}/{}: {}", matricula, numero, ex.getMessage());
            throw ex;
        }

        Avaliacao avaliacaoSalva = avaliacaoRepository.salvar(avaliacao);

        log.info("Caso de uso [cadastrarAvaliacaoEntregas] finalizado com sucesso para {}/{}", matricula, numero);
        return apiMapper.toPerformanceDTO(avaliacaoSalva);
    }

    @Override
    @Transactional
    public PerformanceGeralDTO cadastrarAvaliacaoCompleta(String matricula, int numero, AvaliacaoCompletaRequestDTO dto) {
        log.info("Iniciando caso de uso [cadastrarAvaliacaoCompleta] para matrícula {} e número {}", matricula, numero);

        Avaliacao avaliacao = findOrCreateAvaliacaoDomain(matricula, numero);

        log.info("Mapeando partes comportamental e entregas para {}/{}", matricula, numero); // [LOG INFO: AÇÃO INTERNA]
        AvaliacaoComportamental comportamental = apiMapper.toDomain(dto.comportamental());
        avaliacao.setComportamental(comportamental);

        AvaliacaoEntregasRequestDTO entregasDTO = new AvaliacaoEntregasRequestDTO(dto.desafios());
        List<Desafio> desafios = apiMapper.toDomain(entregasDTO);
        try {
            avaliacao.setEntregas(desafios);
        } catch (RegraDeNegocioException ex) {
            log.warn("Falha ao definir entregas (cadastro completo) para {}/{}: {}", matricula, numero, ex.getMessage());
            throw ex;
        }
        log.info("Mapeamento finalizado para {}/{}", matricula, numero);

        Avaliacao avaliacaoSalva = avaliacaoRepository.salvar(avaliacao);

        log.info("Caso de uso [cadastrarAvaliacaoCompleta] finalizado com sucesso para matrícula {} e número {}", matricula, numero);
        return apiMapper.toPerformanceDTO(avaliacaoSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceGeralDTO recuperarPerformance(String matricula, int numero) {
        log.info("Iniciando caso de uso [recuperarPerformance] para {}/{}", matricula, numero);

        Avaliacao avaliacao = avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero)
                .orElseThrow(() -> {
                    log.warn("Avaliação {}/{} não encontrada.", matricula, numero);
                    return new RecursoNaoEncontradoException(
                            "Avaliação com número " + numero + " não encontrada para o colaborador " + matricula + "."
                    );
                });

        log.info("Caso de uso [recuperarPerformance] finalizado com sucesso para {}/{}", matricula, numero);
        return apiMapper.toPerformanceDTO(avaliacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceGeralDTO> recuperarHistoricoPerformance(String matricula) {
        log.info("Iniciando caso de uso [recuperarHistoricoPerformance] para matrícula {}", matricula);

        List<Avaliacao> avaliacoes = avaliacaoRepository.buscarPorMatricula(matricula);

        List<PerformanceGeralDTO> dtos = avaliacoes.stream()
                .map(apiMapper::toPerformanceDTO)
                .collect(Collectors.toList());

        log.info("Caso de uso [recuperarHistoricoPerformance] finalizado. Encontradas {} avaliações para matrícula {}", dtos.size(), matricula);
        return dtos;
    }

    private Avaliacao findOrCreateAvaliacaoDomain(String matricula, int numero) {
        log.info("Buscando avaliação existente para {}/{}", matricula, numero);
        Optional<Avaliacao> avaliacaoOpt = avaliacaoRepository.buscarPorMatriculaENumero(matricula, numero); // Adapter loga INFO/DEBUG

        if (avaliacaoOpt.isPresent()) {
            log.info("Avaliação existente encontrada para {}/{}. Atualizando.", matricula, numero);
            return avaliacaoOpt.get();
        } else {
            log.info("Nenhuma avaliação existente para {}/{}. Criando nova.", matricula, numero);
            Colaborador colaborador = colaboradorRepository.buscarPorMatricula(matricula)
                    .orElseThrow(() -> {
                        log.warn("Colaborador {} não encontrado ao tentar criar avaliação {}", matricula, numero);
                        return new RecursoNaoEncontradoException("Colaborador " + matricula + " não encontrado ao criar avaliação.");
                    });

            return new Avaliacao(colaborador, numero);
        }
    }

}