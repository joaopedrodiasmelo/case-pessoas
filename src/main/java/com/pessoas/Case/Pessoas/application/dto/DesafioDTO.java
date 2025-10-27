package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DesafioDTO(
        @NotBlank(message = "Descrição do Desafio não pode ser nula ou vazia") String descricao,
        @Min(value = 1, message = "A nota do desafio deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota do desafio deve ser no máximo 5.")
        int nota
) {
}
