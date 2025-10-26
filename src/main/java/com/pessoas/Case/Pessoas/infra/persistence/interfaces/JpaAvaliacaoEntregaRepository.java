package com.pessoas.Case.Pessoas.infra.persistence.interfaces;


import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoEntregaEntity;
import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAvaliacaoEntregaRepository extends JpaRepository<AvaliacaoEntregaEntity, AvaliacaoId> {

    Optional<AvaliacaoEntregaEntity> findById(AvaliacaoId id);

    List<AvaliacaoEntregaEntity> findById_ColaboradorMatricula(String matricula);
}