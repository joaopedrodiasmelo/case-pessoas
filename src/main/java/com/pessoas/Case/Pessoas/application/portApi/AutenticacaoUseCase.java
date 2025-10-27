package com.pessoas.Case.Pessoas.application.portApi;

import com.pessoas.Case.Pessoas.application.dto.AuthRequestDTO;
import com.pessoas.Case.Pessoas.application.dto.AuthResponseDTO;

public interface AutenticacaoUseCase {
    AuthResponseDTO autenticar(AuthRequestDTO dto);
}
