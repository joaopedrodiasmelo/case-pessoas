package com.pessoas.Case.Pessoas.infra.persistence;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class AvaliacaoId implements Serializable {

    @Column(name = "colaborador_matricula", length = 9)
    private String colaboradorMatricula;

    @Column(name = "numero")
    private int numero;

    public AvaliacaoId() {
    }

    public AvaliacaoId(String mat, int num) {
        this.colaboradorMatricula = mat;
        this.numero = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvaliacaoId that = (AvaliacaoId) o;
        return numero == that.numero && Objects.equals(colaboradorMatricula, that.colaboradorMatricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colaboradorMatricula, numero);
    }
}