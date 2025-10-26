package com.pessoas.Case.Pessoas.infra.mapper;

import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import com.pessoas.Case.Pessoas.infra.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper (Tradutor) da camada de Infraestrutura para Avaliações.
 * Converte Modelos de Domínio <-> Entidades JPA.
 */
@Component
public class AvaliacaoMapper {

    private final ColaboradorMapper colaboradorMapper;

    public AvaliacaoMapper(ColaboradorMapper colaboradorMapper) {
        this.colaboradorMapper = colaboradorMapper;
    }

    public Avaliacao toDomain(Optional<AvaliacaoComportamentalEntity> compOpt, Optional<AvaliacaoEntregaEntity> entregaOpt) {
        if (compOpt.isEmpty() && entregaOpt.isEmpty()) {
            return null;
        }

        ColaboradorEntity colabEntity = compOpt.map(AvaliacaoComportamentalEntity::getColaborador)
                .orElseGet(() -> entregaOpt.get().getColaborador()); // Assume que pelo menos uma existe
        AvaliacaoId avaliacaoId = compOpt.map(AvaliacaoComportamentalEntity::getId)
                .orElseGet(() -> entregaOpt.get().getId());

        if (colabEntity == null || avaliacaoId == null) {
            throw new IllegalStateException("Entidade de avaliação encontrada sem Colaborador ou ID associado.");
        }

        Colaborador colaborador = colaboradorMapper.toDomain(colabEntity);
        Avaliacao avaliacao = new Avaliacao(colaborador, avaliacaoId.getNumero());

        compOpt.ifPresent(compEntity ->
                avaliacao.setComportamental(toDomainComportamental(compEntity))
        );

        entregaOpt.ifPresent(entregaEntity -> {
            if (entregaEntity.getDesafios() != null && !entregaEntity.getDesafios().isEmpty()) {
                List<Desafio> desafiosDomain = entregaEntity.getDesafios().stream()
                        .map(this::toDomainDesafio)
                        .collect(Collectors.toList());
                avaliacao.setEntregas(desafiosDomain);
            }
        });

        return avaliacao;
    }

    private AvaliacaoComportamental toDomainComportamental(AvaliacaoComportamentalEntity entity) {
        return new AvaliacaoComportamental(
                entity.getNotaPergunta1(),
                entity.getNotaPergunta2(),
                entity.getNotaPergunta3(),
                entity.getNotaPergunta4()
        );
    }


    private Desafio toDomainDesafio(DesafioEntity entity) {
        return new Desafio(entity.getDescricaoDesafio(), entity.getNotaDesafio());
    }


    public AvaliacaoComportamentalEntity toEntityComportamental(Avaliacao domain, ColaboradorEntity colabEntity, Optional<AvaliacaoComportamentalEntity> existingOpt) {
        AvaliacaoComportamental compDomain = domain.getComportamental();
        if (compDomain == null) {
            return null;
        }

        AvaliacaoComportamentalEntity entity = existingOpt.orElse(new AvaliacaoComportamentalEntity());

        if (entity.getId() == null) {
            AvaliacaoId id = new AvaliacaoId(domain.getColaborador().getMatricula(), domain.getNumero());
            entity.setId(id);
            entity.setColaborador(colabEntity);
        }

        entity.setNotaPergunta1(compDomain.getPromoveAmbienteColaborativo());
        entity.setNotaPergunta2(compDomain.getSeAtualizaEaprende());
        entity.setNotaPergunta3(compDomain.getUtilizaDadosDecisoes());
        entity.setNotaPergunta4(compDomain.getTrabalhaComAutonomia());

        return entity;
    }

    public AvaliacaoEntregaEntity toEntityEntrega(Avaliacao domain, ColaboradorEntity colabEntity, Optional<AvaliacaoEntregaEntity> existingOpt) {
        List<Desafio> desafiosDomain = domain.getDesafios();
        if (desafiosDomain == null || desafiosDomain.isEmpty()) {
            return null;
        }

        AvaliacaoEntregaEntity entity = existingOpt.orElse(new AvaliacaoEntregaEntity());

        if (entity.getId() == null) {
            AvaliacaoId id = new AvaliacaoId(domain.getColaborador().getMatricula(), domain.getNumero());
            entity.setId(id);
            entity.setColaborador(colabEntity);
        }

        entity.getDesafios().clear();
        desafiosDomain.stream()
                .map(d -> toEntityDesafio(d, entity))
                .forEach(entity::addDesafio);

        return entity;
    }

    private DesafioEntity toEntityDesafio(Desafio domain, AvaliacaoEntregaEntity entregaEntity) {
        DesafioEntity entity = new DesafioEntity();
        entity.setDescricaoDesafio(domain.getDescricao());
        entity.setNotaDesafio(domain.getNota());
        return entity;
    }
}