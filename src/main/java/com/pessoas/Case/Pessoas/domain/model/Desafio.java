package com.pessoas.Case.Pessoas.domain.model;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Desafio {

    private final String descricao;
    private final int nota;

    public Desafio(String descricao, int nota) {
        if (descricao == null || descricao.isBlank()) {
            throw new RegraDeNegocioException("A descrição do desafio não pode ser vazia.");
        }
        if (nota < 1 || nota > 5) {
            throw new RegraDeNegocioException("A nota do desafio '" + descricao + "' deve ser entre 1 e 5.");
        }
        this.descricao = descricao;
        this.nota = nota;
    }
}
