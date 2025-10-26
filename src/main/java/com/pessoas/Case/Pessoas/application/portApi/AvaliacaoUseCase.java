package com.pessoas.Case.Pessoas.application.portApi;

import com.pessoas.Case.Pessoas.application.dto.*; // Importe todos os DTOs de Avaliação
import java.util.List;

public interface AvaliacaoUseCase {

    PerformanceGeralDTO cadastrarAvaliacaoComportamental(String matricula, int numero, AvaliacaoComportamentalDTO dto);

    PerformanceGeralDTO cadastrarAvaliacaoEntregas(String matricula, int numero, AvaliacaoEntregasRequestDTO dto);

    PerformanceGeralDTO cadastrarAvaliacaoCompleta(String matricula, int numero, AvaliacaoCompletaRequestDTO dto);

    PerformanceGeralDTO recuperarPerformance(String matricula, int numero);

    List<PerformanceGeralDTO> recuperarHistoricoPerformance(String matricula);
}