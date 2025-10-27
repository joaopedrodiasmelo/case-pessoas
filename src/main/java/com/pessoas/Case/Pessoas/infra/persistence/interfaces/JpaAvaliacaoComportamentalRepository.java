package com.pessoas.Case.Pessoas.infra.persistence.interfaces;


import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoComportamentalEntity;
import com.pessoas.Case.Pessoas.infra.persistence.AvaliacaoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAvaliacaoComportamentalRepository extends JpaRepository<AvaliacaoComportamentalEntity, AvaliacaoId> {

    Optional<AvaliacaoComportamentalEntity> findById_ColaboradorMatriculaAndId_Numero(String matricula, int numero);

    List<AvaliacaoComportamentalEntity> findById_ColaboradorMatricula(String matricula);
}