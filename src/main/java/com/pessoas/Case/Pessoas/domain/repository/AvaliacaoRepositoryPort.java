package com.pessoas.Case.Pessoas.domain.repository;

import com.pessoas.Case.Pessoas.domain.model.Avaliacao;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepositoryPort {

    Avaliacao salvar(Avaliacao avaliacao);

    Optional<Avaliacao> buscarPorMatriculaENumero(String matricula, int numero);

    List<Avaliacao> buscarPorMatricula(String matricula);
}