package com.pessoas.Case.Pessoas.application.portApi;

import com.pessoas.Case.Pessoas.application.dto.ColaboradorDTO;

public interface ColaboradorUseCase {

    ColaboradorDTO criarColaborador(ColaboradorDTO dto);

    ColaboradorDTO buscarColaborador(String matricula);

    ColaboradorDTO atualizarColaborador(String matricula, ColaboradorDTO dto);

    void deletarColaborador(String matricula);
}
