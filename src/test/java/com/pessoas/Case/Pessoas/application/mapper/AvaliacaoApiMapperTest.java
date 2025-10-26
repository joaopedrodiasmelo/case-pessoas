package com.pessoas.Case.Pessoas.application.mapper;

import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoApiMapperTest {

    private AvaliacaoApiMapper mapper;
    private Colaborador colaboradorPadrao;

    @BeforeEach
    void setUp() {
        mapper = new AvaliacaoApiMapper();
        colaboradorPadrao = new Colaborador("123456789", "Teste Mapper", LocalDate.now(), "Mapper Tester");
    }


    @Test
    @DisplayName("toDomain(AvaliacaoComportamentalDTO) deve converter DTO válido para Domínio")
    void toDomain_ComportamentalDTOValido_ConverteCorretamente() {
        AvaliacaoComportamentalDTO dto = new AvaliacaoComportamentalDTO(4, 5, 3, 4);

        AvaliacaoComportamental domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals(4, domain.getPromoveAmbienteColaborativo());
        assertEquals(5, domain.getSeAtualizaEaprende());
        assertEquals(3, domain.getUtilizaDadosDecisoes());
        assertEquals(4, domain.getTrabalhaComAutonomia());
    }

    @Test
    @DisplayName("toDomain(AvaliacaoComportamentalDTO) deve retornar null para DTO nulo")
    void toDomain_ComportamentalDTONulo_RetornaNull() {
        assertNull(mapper.toDomain((AvaliacaoComportamentalDTO) null));
    }


    @Test
    @DisplayName("toDomain(AvaliacaoEntregasRequestDTO) deve converter DTO válido para Lista de Domínio")
    void toDomain_EntregasDTOValido_ConverteCorretamente() {
        DesafioDTO d1 = new DesafioDTO("D1", 5);
        DesafioDTO d2 = new DesafioDTO("D2", 4);
        AvaliacaoEntregasRequestDTO dto = new AvaliacaoEntregasRequestDTO(List.of(d1, d2));

        List<Desafio> domainList = mapper.toDomain(dto);

        assertNotNull(domainList);
        assertEquals(2, domainList.size());
        assertEquals("D1", domainList.get(0).getDescricao());
        assertEquals(5, domainList.get(0).getNota());
        assertEquals("D2", domainList.get(1).getDescricao());
        assertEquals(4, domainList.get(1).getNota());
    }

    @Test
    @DisplayName("toDomain(AvaliacaoEntregasRequestDTO) deve retornar lista vazia para DTO nulo")
    void toDomain_EntregasDTONulo_RetornaListaVazia() {
        List<Desafio> domainList = mapper.toDomain((AvaliacaoEntregasRequestDTO) null);
        assertNotNull(domainList);
        assertTrue(domainList.isEmpty());
    }

    @Test
    @DisplayName("toDomain(AvaliacaoEntregasRequestDTO) deve retornar lista vazia para lista de desafios nula")
    void toDomain_EntregasDTOComListaNula_RetornaListaVazia() {
        AvaliacaoEntregasRequestDTO dto = new AvaliacaoEntregasRequestDTO(null);
        List<Desafio> domainList = mapper.toDomain(dto);
        assertNotNull(domainList);
        assertTrue(domainList.isEmpty());
    }

    @Test
    @DisplayName("toDomain(AvaliacaoEntregasRequestDTO) deve retornar lista vazia para lista de desafios vazia")
    void toDomain_EntregasDTOComListaVazia_RetornaListaVazia() {
        AvaliacaoEntregasRequestDTO dto = new AvaliacaoEntregasRequestDTO(Collections.emptyList());
        List<Desafio> domainList = mapper.toDomain(dto);
        assertNotNull(domainList);
        assertTrue(domainList.isEmpty());
    }


    @Test
    @DisplayName("toPerformanceDTO deve converter Domínio completo para DTO")
    void toPerformanceDTO_DominioCompleto_ConverteCorretamente() {
        Avaliacao domain = new Avaliacao(colaboradorPadrao, 2025);
        domain.setComportamental(new AvaliacaoComportamental(4, 5, 3, 4)); // Média 4.0
        domain.setEntregas(List.of(new Desafio("D1", 5), new Desafio("D2", 3))); // Média 4.0

        PerformanceGeralDTO dto = mapper.toPerformanceDTO(domain);

        assertNotNull(dto);
        assertEquals("123456789", dto.matricula());
        assertEquals(2025, dto.numero());
        assertEquals(4.0, dto.notaComportamentalPonderada(), 0.01);
        assertEquals(4.0, dto.notaEntregasMedia(), 0.01);
        assertEquals(4.0, dto.notaFinal(), 0.01);
    }

    @Test
    @DisplayName("toPerformanceDTO deve converter Domínio parcial (só comp) para DTO")
    void toPerformanceDTO_DominioSoComportamental_ConverteCorretamente() {

        Avaliacao domain = new Avaliacao(colaboradorPadrao, 2025);
        domain.setComportamental(new AvaliacaoComportamental(4, 5, 3, 4)); // Média 4.0

        PerformanceGeralDTO dto = mapper.toPerformanceDTO(domain);

        assertNotNull(dto);
        assertEquals(4.0, dto.notaComportamentalPonderada(), 0.01);
        assertEquals(0.0, dto.notaEntregasMedia(), 0.01);
        assertEquals(1.6, dto.notaFinal(), 0.01);
    }

    @Test
    @DisplayName("toPerformanceDTO deve converter Domínio parcial (só entregas) para DTO")
    void toPerformanceDTO_DominioSoEntregas_ConverteCorretamente() {

        Avaliacao domain = new Avaliacao(colaboradorPadrao, 2025);
        domain.setEntregas(List.of(new Desafio("D1", 5), new Desafio("D2", 3))); // Média 4.0

        PerformanceGeralDTO dto = mapper.toPerformanceDTO(domain);

        assertNotNull(dto);
        assertEquals(0.0, dto.notaComportamentalPonderada(), 0.01);
        assertEquals(4.0, dto.notaEntregasMedia(), 0.01);
        assertEquals(2.4, dto.notaFinal(), 0.01);
    }

    @Test
    @DisplayName("toPerformanceDTO deve retornar null para Domínio nulo")
    void toPerformanceDTO_DominioNulo_RetornaNull() {

        Avaliacao domain = null;

        PerformanceGeralDTO dto = mapper.toPerformanceDTO(domain);

        assertNull(dto);
    }
}