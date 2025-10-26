package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ColaboradorDTO(
        @NotBlank(message = "A matrícula não pode ser nula ou vazia.")
        @Pattern(regexp = "\\d{9}", message = "A matrícula deve conter exatamente 9 dígitos numéricos.")
        String matricula,

        @NotBlank(message = "O nome não pode ser nulo ou vazio.")
        String nome,

        @NotNull(message = "A data de admissão não pode ser nula.")
        @PastOrPresent(message = "A data de admissão não pode ser uma data futura.")
        LocalDate dataAdmissao,

        @NotBlank(message = "O cargo não pode ser nulo ou vazio.")
        String cargo
) {
}