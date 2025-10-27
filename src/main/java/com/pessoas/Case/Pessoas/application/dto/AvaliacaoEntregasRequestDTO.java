package com.pessoas.Case.Pessoas.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AvaliacaoEntregasRequestDTO(

        @NotNull(message = "A lista de desafios não pode ser nula.")
        @Size(min = 2, max = 4, message = "Deve haver no mínimo 2 e no máximo 4 desafios.")
        @Valid
        List<DesafioDTO> desafios

) {}