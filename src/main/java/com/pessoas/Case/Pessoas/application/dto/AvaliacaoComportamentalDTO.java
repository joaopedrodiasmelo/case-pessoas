package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AvaliacaoComportamentalDTO(
        @Min(value = 1, message = "A nota de 'promoveAmbienteColaborativo' deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota de 'promoveAmbienteColaborativo' deve ser no máximo 5.")
        int promoveAmbienteColaborativo,

        @Min(value = 1, message = "A nota de 'seAtualizaEaprende' deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota de 'seAtualizaEaprende' deve ser no máximo 5.")
        int seAtualizaEaprende,

        @Min(value = 1, message = "A nota de 'utilizaDadosDecisoes' deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota de 'utilizaDadosDecisoes' deve ser no máximo 5.")
        int utilizaDadosDecisoes,

        @Min(value = 1, message = "A nota de 'trabalhaComAutonomia' deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota de 'trabalhaComAutonomia' deve ser no máximo 5.")
        int trabalhaComAutonomia
) {
}
