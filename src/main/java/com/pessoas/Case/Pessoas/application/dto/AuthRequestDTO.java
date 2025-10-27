package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank(message = "O clientId não pode ser nulo ou vazio.")
        String clientId,

        @NotBlank(message = "O password não pode ser nulo ou vazio.")
        String password
) {
}