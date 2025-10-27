package com.pessoas.Case.Pessoas.infra.adapter;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.repository.AvaliacaoRepositoryPort;
import com.pessoas.Case.Pessoas.infra.mapper.AvaliacaoMapper;
import com.pessoas.Case.Pessoas.infra.persistence.*;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaAvaliacaoComportamentalRepository;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaAvaliacaoEntregaRepository;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AvaliacaoRepositoryAdapter implements AvaliacaoRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoRepositoryAdapter.class);
    private final JpaAvaliacaoComportamentalRepository compRepo;
    private final JpaAvaliacaoEntregaRepository entregaRepo;
    private final JpaColaboradorRepository colaboradorRepo;
    private final AvaliacaoMapper mapper;

    @Override
    @Transactional
    public Avaliacao salvar(Avaliacao avaliacao) {
        String matricula = avaliacao.getColaborador().getMatricula();
        int numero = avaliacao.getNumero();
        AvaliacaoId id = new AvaliacaoId(matricula, numero);

        log.debug("Buscando ColaboradorEntity para matrícula: {}", matricula);
        ColaboradorEntity colabEntity = colaboradorRepo.findById(matricula)
                .orElseThrow(() -> {
                    log.error("Colaborador {} não encontrado! Impossível salvar/atualizar avaliação {}.", matricula, numero);
                    return new RegraDeNegocioException("Colaborador com matrícula " + matricula + " não encontrado ao salvar avaliação.");
                });
        log.debug("ColaboradorEntity encontrado para matrícula: {}", matricula);

        log.debug("Verificando se AvaliacaoComportamentalEntity já existe para ID: {}", id);
        Optional<AvaliacaoComportamentalEntity> existingCompOpt = compRepo.findById(id);
        log.debug("AvaliacaoComportamentalEntity {} para ID: {}", existingCompOpt.isPresent() ? "encontrada" : "não encontrada", id);

        log.debug("Mapeando Avaliacao (domain) para AvaliacaoComportamentalEntity para ID: {}", id);
        AvaliacaoComportamentalEntity entityCompToSave = mapper.toEntityComportamental(avaliacao, colabEntity, existingCompOpt);
        log.debug("Mapeamento toEntityComportamental finalizado.");

        if (entityCompToSave != null) {
            log.debug("Executando save/update em avaliacoes_comportamentais para {}/{}", matricula, numero);
            compRepo.save(entityCompToSave);
        } else if (existingCompOpt.isPresent()) {
            log.debug("Executando delete em avaliacoes_comportamentais para ID: {}", id);
            compRepo.deleteById(id);
        }

        log.debug("Verificando se AvaliacaoEntregaEntity já existe para ID: {}", id);
        Optional<AvaliacaoEntregaEntity> existingEntregaOpt = entregaRepo.findById(id);// O mapper.toEntityEntrega retorna null se avaliacao.getDesafios() for null ou vazio
        log.debug("AvaliacaoEntregaEntity {} para ID: {}", existingEntregaOpt.isPresent() ? "encontrada" : "não encontrada", id);

        log.debug("Mapeando Avaliacao (domain) para AvaliacaoEntregaEntity para ID: {}", id);
        AvaliacaoEntregaEntity entityEntregaToSave = mapper.toEntityEntrega(avaliacao, colabEntity, existingEntregaOpt);
        log.debug("Resultado do mapeamento toEntityEntrega para ID {}: {}", id, (entityEntregaToSave != null ? "Entidade pronta para salvar/atualizar (desafios cascade)" : "Entidade nula (sem desafios no domínio)"));

        if (entityEntregaToSave != null) {
            log.debug("Executando save/update em avaliacoes_entregas (e desafios via cascade) para ID: {}", id);
            entregaRepo.save(entityEntregaToSave);
        } else if (existingEntregaOpt.isPresent()) {
            log.debug("Executando delete em avaliacoes_entregas (e desafios via cascade) para ID: {}", id);
            entregaRepo.deleteById(id);
        }

        log.info("Persistência da avaliação finalizada com sucesso para {}/{}", matricula, numero);
        return buscarPorMatriculaENumero(matricula, numero)
                .orElseThrow(() -> new IllegalStateException("Falha ao buscar avaliação recém-salva com ID: " + id));
    }

    @Override
    public Optional<Avaliacao> buscarPorMatriculaENumero(String matricula, int numero) {
        AvaliacaoId id = new AvaliacaoId(matricula, numero);
        log.info("Buscando avaliação no banco para {}/{}", matricula, numero);

        log.debug("Buscando AvaliacaoComportamentalEntity para ID: {}", id);
        Optional<AvaliacaoComportamentalEntity> compOpt = compRepo.findById(id);

        log.debug("Buscando AvaliacaoEntregaEntity para ID: {}", id);
        Optional<AvaliacaoEntregaEntity> entregaOpt = entregaRepo.findById(id);

        if (compOpt.isEmpty() && entregaOpt.isEmpty()) {
            log.info("Nenhuma parte da avaliação encontrada para {}/{}", matricula, numero);
            return Optional.empty();
        }

        log.debug("Mapeando entidades encontradas para o modelo de domínio Avaliacao para {}/{}", matricula, numero);
        Avaliacao avaliacao = mapper.toDomain(compOpt, entregaOpt);

        log.info("Avaliação reconstruída com sucesso para {}/{}", matricula, numero);

        return Optional.ofNullable(avaliacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarPorMatricula(String matricula) {
        log.info("Buscando histórico de avaliações no banco para matrícula {}", matricula);

        log.debug("Buscando todas AvaliacaoComportamentalEntity para matrícula {}", matricula);
        List<AvaliacaoComportamentalEntity> compList = compRepo.findById_ColaboradorMatricula(matricula);
        log.debug("Encontradas {} partes comportamentais para matrícula {}", compList.size(), matricula);

        log.debug("Buscando todas AvaliacaoEntregaEntity para matrícula {}", matricula);
        List<AvaliacaoEntregaEntity> entregaList = entregaRepo.findById_ColaboradorMatricula(matricula);
        log.debug("Encontradas {} partes de entrega para matrícula {}", entregaList.size(), matricula);

        log.debug("Agrupando entidades por 'numero' para matrícula {}", matricula);
        Map<Integer, AvaliacaoComportamentalEntity> compMap = compList.stream()
                .collect(Collectors.toMap(c -> c.getId().getNumero(), c -> c));
        Map<Integer, AvaliacaoEntregaEntity> entregaMap = entregaList.stream()
                .collect(Collectors.toMap(e -> e.getId().getNumero(), e -> e));

        Set<Integer> numeros = new HashSet<>(compMap.keySet());
        numeros.addAll(entregaMap.keySet());
        log.debug("Encontrados {} 'numeros' de avaliação únicos para matrícula {}", numeros.size(), matricula);

        log.debug("Reconstruindo objetos Avaliacao (domain) para matrícula {}", matricula);
        List<Avaliacao> avaliacoesReconstruidas = numeros.stream()
                .map(num -> mapper.toDomain(
                                Optional.ofNullable(compMap.get(num)),
                                Optional.ofNullable(entregaMap.get(num))
                        )
                )
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Avaliacao::getNumero))
                .collect(Collectors.toList());

        log.info("Histórico de {} avaliações reconstruído com sucesso para matrícula {}", avaliacoesReconstruidas.size(), matricula);
        return avaliacoesReconstruidas;
    }
}