package com.pessoas.Case.Pessoas.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Colaborador {

    private String matricula;
    private String nome;
    private LocalDate dataAdmissao;
    private String cargo;

    public Colaborador() {}

    public Colaborador(String matricula, String nome, LocalDate dataAdmissao, String cargo) {
        this.matricula = matricula;
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.cargo = cargo;
    }
}