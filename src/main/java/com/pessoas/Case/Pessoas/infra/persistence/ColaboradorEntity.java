package com.pessoas.Case.Pessoas.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "colaboradores")
public class ColaboradorEntity {

    @Id
    @Column(length = 9)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(nullable = false)
    private String cargo;

    public ColaboradorEntity() {}
}