package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AvaliacaoCompletaRequestDTO(
        @NotNull(message = "Avaliação comportamental não pode ser nula ou vazia")
        AvaliacaoComportamentalDTO comportamental,

        @NotNull
        @Size(min = 2, max = 4, message = "Deve haver no mínimo 2 e no máximo 4 desafios.")
        List<DesafioDTO> desafios
) {
}
