package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AvaliacaoEntregasDTO(

        @NotNull
        @Size(min = 2, max = 4, message = "A avaliação deve ter entre 2 e 4 desafios.")
        List<DesafioDTO> desafios
) {
}
