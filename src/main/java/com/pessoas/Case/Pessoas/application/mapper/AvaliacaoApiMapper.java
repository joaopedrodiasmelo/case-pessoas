package com.pessoas.Case.Pessoas.application.mapper;

import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvaliacaoApiMapper {

    public AvaliacaoComportamental toDomain(AvaliacaoComportamentalDTO dto) {
        if (dto == null) return null;
        return new AvaliacaoComportamental(
                dto.promoveAmbienteColaborativo(),
                dto.seAtualizaEaprende(),
                dto.utilizaDadosDecisoes(),
                dto.trabalhaComAutonomia()
        );
    }

    public List<Desafio> toDomain(AvaliacaoEntregasRequestDTO dto) {
        if (dto == null || dto.desafios() == null) return Collections.emptyList();
        return dto.desafios().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Desafio toDomain(DesafioDTO dto) {
        if (dto == null) return null;
        return new Desafio(
                dto.descricao(),
                dto.nota()
        );
    }

    public PerformanceGeralDTO toPerformanceDTO(Avaliacao domain) {
        if (domain == null) return null;
        return new PerformanceGeralDTO(
                domain.getColaborador().getMatricula(),
                domain.getNumero(),
                domain.calcularMediaComportamental(),
                domain.calcularMediaEntregas(),
                domain.calcularNotaFinal()
        );
    }
}