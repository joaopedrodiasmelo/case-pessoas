package com.pessoas.Case.Pessoas.infra.persistence;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "desafios")
@Getter
@Setter
public class DesafioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "colaborador_matricula", referencedColumnName = "colaborador_matricula", nullable = false),
            @JoinColumn(name = "numero", referencedColumnName = "numero", nullable = false)
    })
    private AvaliacaoEntregaEntity avaliacaoEntrega;

    @Column(name = "descricao_desafio", nullable = false)
    private String descricaoDesafio;

    @Column(name = "nota_desafio", nullable = false)
    private int notaDesafio;

    public DesafioEntity() {}
}