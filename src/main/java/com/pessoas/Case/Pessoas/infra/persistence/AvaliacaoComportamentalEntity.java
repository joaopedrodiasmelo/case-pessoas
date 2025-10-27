package com.pessoas.Case.Pessoas.infra.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "avaliacoes_comportamentais")
public class AvaliacaoComportamentalEntity {

    @EmbeddedId
    private AvaliacaoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colaboradorMatricula")
    @JoinColumn(name = "colaborador_matricula")
    private ColaboradorEntity colaborador;

    @Column(name = "nota_pergunta_1", nullable = false)
    private int notaPergunta1;
    @Column(name = "nota_pergunta_2", nullable = false)
    private int notaPergunta2;
    @Column(name = "nota_pergunta_3", nullable = false)
    private int notaPergunta3;
    @Column(name = "nota_pergunta_4", nullable = false)
    private int notaPergunta4;

    public AvaliacaoComportamentalEntity() {}
}