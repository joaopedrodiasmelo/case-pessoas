package com.pessoas.Case.Pessoas.infra.adapter;

import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.repository.ColaboradorRepositoryPort;
import com.pessoas.Case.Pessoas.infra.mapper.ColaboradorMapper;
import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import com.pessoas.Case.Pessoas.infra.persistence.interfaces.JpaColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ColaboradorRepositoryAdapter implements ColaboradorRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(ColaboradorRepositoryAdapter.class);
    private final JpaColaboradorRepository jpaRepository;
    private final ColaboradorMapper mapper;

    @Override
    public Colaborador salvar(Colaborador colaborador) {
        log.debug("Salvando/Atualizando colaborador no banco: {}", colaborador.getMatricula());
        ColaboradorEntity entity = mapper.toEntity(colaborador);
        ColaboradorEntity salvo = jpaRepository.save(entity);
        log.debug("Colaborador {} salvo/atualizado com sucesso no banco.", colaborador.getMatricula());

        return mapper.toDomain(salvo);
    }

    @Override
    public Optional<Colaborador> buscarPorMatricula(String matricula) {
        log.debug("Buscando ColaboradorEntity no banco com matrícula: {}", matricula);

        Optional<ColaboradorEntity> entityOpt = jpaRepository.findById(matricula);
        log.debug("Resultado da busca no banco para {}: {}", matricula, entityOpt.isPresent() ? "Encontrado" : "Não encontrado");

        return entityOpt.map(mapper::toDomain);
    }

    @Override
    public boolean existePorMatricula(String matricula) {
        log.debug("Verificando existência no banco para matrícula: {}", matricula);

        boolean existe = jpaRepository.existsById(matricula);
        log.debug("Resultado da verificação para {}: {}", matricula, existe);

        return existe;
    }

    @Override
    @Transactional
    public void deletarPorMatricula(String matricula) {
        log.debug("Deletando colaborador no banco com matrícula: {}", matricula);
        jpaRepository.deleteById(matricula);

        log.debug("Comando deleteById({}) executado.", matricula);
    }
}