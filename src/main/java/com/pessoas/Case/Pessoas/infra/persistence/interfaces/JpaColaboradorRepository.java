package com.pessoas.Case.Pessoas.infra.persistence.interfaces;


import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaColaboradorRepository extends JpaRepository<ColaboradorEntity, String> {
}
