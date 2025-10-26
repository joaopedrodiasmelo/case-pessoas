package com.pessoas.Case.Pessoas.application.dto;

public record PerformanceGeralDTO(
        String matricula,
        int numero,
        double notaComportamentalPonderada,
        double notaEntregasMedia,
        double notaFinal
) {
}