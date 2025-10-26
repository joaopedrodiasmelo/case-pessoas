package com.pessoas.Case.Pessoas.domain.repository;


import com.pessoas.Case.Pessoas.domain.model.Colaborador;

import java.util.Optional;

public interface ColaboradorRepositoryPort {

    boolean existePorMatricula(String matricula);

    Colaborador salvar(Colaborador colaborador);

    Optional<Colaborador> buscarPorMatricula(String matricula);

    void deletarPorMatricula(String matricula);
}