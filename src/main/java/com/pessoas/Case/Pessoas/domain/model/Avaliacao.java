package com.pessoas.Case.Pessoas.domain.model;


import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Avaliacao {

    private static final double PESO_COMPORTAMENTAL = 0.4;
    private static final double PESO_ENTREGAS = 0.6;

    private final Colaborador colaborador;
    private final int numero;
    private AvaliacaoComportamental comportamental;
    private List<Desafio> desafios;

    public Avaliacao(Colaborador colaborador, int numero) {
        if (colaborador == null) {
            throw new IllegalArgumentException("Colaborador não pode ser nulo.");
        }
        this.colaborador = colaborador;
        this.numero = numero;
    }

    /**
     * REGRA DE NEGÓCIO: Adiciona a parte comportamental.
     */
    public void setComportamental(AvaliacaoComportamental comportamental) {
        if (comportamental == null) {
            throw new IllegalArgumentException("Avaliação comportamental não pode ser nula.");
        }
        this.comportamental = comportamental;
    }

    /**
     * REGRA DE NEGÓCIO: Adiciona as entregas (desafios).
     * Valida a regra "Cadastro de no mínimo 2 e no máximo 4 desafios".
     */
    public void setEntregas(List<Desafio> desafios) {
        if (desafios == null || desafios.size() < 2 || desafios.size() > 4) {
            throw new RegraDeNegocioException("A avaliação deve ter no mínimo 2 e no máximo 4 desafios.");
        }
        this.desafios = desafios;
    }

    /**
     * REGRA DE NEGÓCIO: "Cálculo da média ponderada das notas (peso igual para todos os desafios)."
     * Isso significa uma média aritmética simples da lista de desafios.
     * @return A média das notas dos desafios, ou 0.0 se não houver desafios.
     */
    public double calcularMediaEntregas() {
        if (this.desafios == null || this.desafios.isEmpty()) {
            return 0.0;
        }

        // "peso igual" = média aritmética simples
        return this.desafios.stream()
                .mapToInt(Desafio::getNota)
                .average()
                .orElse(0.0);
    }

    /**
     * REGRA DE NEGÓCIO: "gerar uma média ponderada da avaliação comportamental"
     * Delega o cálculo para o objeto AvaliacaoComportamental.
     * @return A média ponderada, ou 0.0 se não houver avaliação comportamental.
     */
    public double calcularMediaComportamental() {
        if (this.comportamental == null) {
            return 0.0;
        }
        return this.comportamental.calcularMediaPonderada();
    }

    /**
     * REGRA DE NEGÓCIO: "Cálculo da nota final de performance (comportamental + entregas)."
     * Aplica os pesos definidos (40% / 60%) nas médias calculadas.
     * @return A nota final de performance.
     */
    public double calcularNotaFinal() {
        double notaComportamental = calcularMediaComportamental();
        double notaEntregas = calcularMediaEntregas();

        return (notaComportamental * PESO_COMPORTAMENTAL) + (notaEntregas * PESO_ENTREGAS);
    }
}