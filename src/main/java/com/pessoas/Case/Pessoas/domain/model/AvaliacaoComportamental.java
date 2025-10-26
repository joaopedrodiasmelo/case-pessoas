package com.pessoas.Case.Pessoas.domain.model;


import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AvaliacaoComportamental {

    private final int promoveAmbienteColaborativo;
    private final int seAtualizaEaprende;
    private final int utilizaDadosDecisoes;
    private final int trabalhaComAutonomia;

    public AvaliacaoComportamental(int promoveAmbiente, int seAtualiza, int utilizaDados, int trabalhaAutonomia) {
        if (promoveAmbiente < 1 || promoveAmbiente > 5 ||
                seAtualiza < 1 || seAtualiza > 5 ||
                utilizaDados < 1 || utilizaDados > 5 ||
                trabalhaAutonomia < 1 || trabalhaAutonomia > 5) {
            throw new RegraDeNegocioException("Todas as notas comportamentais devem ser entre 1 e 5.");
        }

        this.promoveAmbienteColaborativo = promoveAmbiente;
        this.seAtualizaEaprende = seAtualiza;
        this.utilizaDadosDecisoes = utilizaDados;
        this.trabalhaComAutonomia = trabalhaAutonomia;
    }

    public double calcularMediaPonderada() {
        double peso = 1;
        double somaDasNotas = promoveAmbienteColaborativo +
                seAtualizaEaprende +
                utilizaDadosDecisoes +
                trabalhaComAutonomia;

        return somaDasNotas/4.0;
    }
}