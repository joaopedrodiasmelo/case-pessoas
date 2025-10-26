package com.pessoas.Case.Pessoas.infra.persistence;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avaliacoes_entregas")
@Getter
@Setter
public class AvaliacaoEntregaEntity {

    @EmbeddedId
    private AvaliacaoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colaboradorMatricula")
    @JoinColumn(name = "colaborador_matricula")
    private ColaboradorEntity colaborador;

    @OneToMany(mappedBy = "avaliacaoEntrega", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DesafioEntity> desafios = new ArrayList<>();


    public AvaliacaoEntregaEntity() {}

    public void addDesafio(DesafioEntity desafio) {
        desafios.add(desafio);
        desafio.setAvaliacaoEntrega(this);
    }
    public void removeDesafio(DesafioEntity desafio) {
        desafios.remove(desafio);
        desafio.setAvaliacaoEntrega(null);
    }
}