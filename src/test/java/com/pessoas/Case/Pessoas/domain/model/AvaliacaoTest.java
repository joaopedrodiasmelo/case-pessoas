package com.pessoas.Case.Pessoas.domain.model;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoTest {

    private Colaborador colaboradorPadrao;
    private final int numeroPadrao = 2025; // Número de exemplo para a avaliação

    @BeforeEach
    void setUp() {
        colaboradorPadrao = new Colaborador("123456789", "Teste Unitario", LocalDate.now(), "Tester");
    }

    @Test
    @DisplayName("Construtor deve funcionar com colaborador válido")
    void construtor_ComColaboradorValido_CriaInstancia() {
        assertDoesNotThrow(() -> new Avaliacao(colaboradorPadrao, numeroPadrao));
    }

    @Test
    @DisplayName("Construtor deve lançar exceção com colaborador nulo")
    void construtor_ComColaboradorNulo_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Avaliacao(null, numeroPadrao),
                "Deveria lançar IllegalArgumentException para colaborador nulo.");
    }

    @Test
    @DisplayName("setComportamental deve aceitar avaliação válida")
    void setComportamental_ComAvaliacaoValida_DefineCorretamente() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        AvaliacaoComportamental comp = new AvaliacaoComportamental(4, 4, 4, 4); // Assume construtor válido
        assertDoesNotThrow(() -> avaliacao.setComportamental(comp));
        assertEquals(comp, avaliacao.getComportamental());
    }

    @Test
    @DisplayName("setComportamental deve lançar exceção para avaliação nula")
    void setComportamental_ComAvaliacaoNula_LancaIllegalArgumentException() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        assertThrows(IllegalArgumentException.class,
                () -> avaliacao.setComportamental(null),
                "Deveria lançar IllegalArgumentException para comportamental nulo.");
    }


    @Test
    @DisplayName("setEntregas deve aceitar lista com 2 desafios")
    void setEntregas_Com2Desafios_DefineCorretamente() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = List.of(new Desafio("D1", 5), new Desafio("D2", 4)); // Assume construtor válido
        assertDoesNotThrow(() -> avaliacao.setEntregas(desafios));
        assertEquals(desafios, avaliacao.getDesafios());
        assertEquals(2, avaliacao.getDesafios().size());
    }

    @Test
    @DisplayName("setEntregas deve aceitar lista com 4 desafios")
    void setEntregas_Com4Desafios_DefineCorretamente() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = List.of(
                new Desafio("D1", 5), new Desafio("D2", 4),
                new Desafio("D3", 3), new Desafio("D4", 2)
        );
        assertDoesNotThrow(() -> avaliacao.setEntregas(desafios));
        assertEquals(desafios, avaliacao.getDesafios());
        assertEquals(4, avaliacao.getDesafios().size());
    }

    @Test
    @DisplayName("setEntregas deve lançar exceção para lista com 1 desafio")
    void setEntregas_Com1Desafio_LancaRegraDeNegocioException() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = List.of(new Desafio("D1", 5));
        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> avaliacao.setEntregas(desafios));
        assertTrue(ex.getMessage().contains("mínimo 2 e no máximo 4"));
    }

    @Test
    @DisplayName("setEntregas deve lançar exceção para lista com 5 desafios")
    void setEntregas_Com5Desafios_LancaRegraDeNegocioException() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = List.of(
                new Desafio("D1", 1), new Desafio("D2", 1), new Desafio("D3", 1),
                new Desafio("D4", 1), new Desafio("D5", 1)
        );
        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> avaliacao.setEntregas(desafios));
        assertTrue(ex.getMessage().contains("mínimo 2 e no máximo 4"));
    }

    @Test
    @DisplayName("setEntregas deve lançar exceção para lista nula")
    void setEntregas_ComListaNula_LancaRegraDeNegocioException() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = null;
        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> avaliacao.setEntregas(desafios));
        assertTrue(ex.getMessage().contains("mínimo 2 e no máximo 4"));
    }


    @Test
    @DisplayName("calcularMediaEntregas deve retornar média correta")
    void calcularMediaEntregas_ComDesafiosValidos_RetornaMedia() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        List<Desafio> desafios = List.of(new Desafio("D1", 5), new Desafio("D2", 4), new Desafio("D3", 3)); // Média = 4.0
        avaliacao.setEntregas(desafios);
        assertEquals(4.0, avaliacao.calcularMediaEntregas(), 0.01);
    }

    @Test
    @DisplayName("calcularMediaEntregas deve retornar 0.0 sem desafios")
    void calcularMediaEntregas_SemDesafios_RetornaZero() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        assertEquals(0.0, avaliacao.calcularMediaEntregas());
    }


    @Test
    @DisplayName("calcularMediaComportamental deve retornar média correta")
    void calcularMediaComportamental_ComAvaliacaoValida_RetornaMedia() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        AvaliacaoComportamental comp = new AvaliacaoComportamental(4, 5, 3, 4); // Média = 4.0 (assumindo peso igual)
        avaliacao.setComportamental(comp);
        assertEquals(4.0, avaliacao.calcularMediaComportamental(), 0.01);
    }

    @Test
    @DisplayName("calcularMediaComportamental deve retornar 0.0 sem avaliação")
    void calcularMediaComportamental_SemAvaliacao_RetornaZero() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        assertEquals(0.0, avaliacao.calcularMediaComportamental());
    }


    @Test
    @DisplayName("calcularNotaFinal deve retornar nota correta com ambas as partes")
    void calcularNotaFinal_ComAmbasPartes_RetornaNotaPonderada() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        avaliacao.setComportamental(new AvaliacaoComportamental(4, 5, 3, 4));
        avaliacao.setEntregas(List.of(new Desafio("D1", 5), new Desafio("D2", 4), new Desafio("D3", 3)));
        assertEquals(4.0, avaliacao.calcularNotaFinal(), 0.01);
    }

    @Test
    @DisplayName("calcularNotaFinal deve retornar nota correta só com comportamental")
    void calcularNotaFinal_SoComportamental_RetornaNotaPonderada() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        avaliacao.setComportamental(new AvaliacaoComportamental(4, 5, 3, 4)); // Média = 4.0
        assertEquals(1.6, avaliacao.calcularNotaFinal(), 0.01);
    }

    @Test
    @DisplayName("calcularNotaFinal deve retornar nota correta só com entregas")
    void calcularNotaFinal_SoEntregas_RetornaNotaPonderada() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        avaliacao.setEntregas(List.of(new Desafio("D1", 5), new Desafio("D2", 4), new Desafio("D3", 3))); // Média = 4.0
        assertEquals(2.4, avaliacao.calcularNotaFinal(), 0.01);
    }

    @Test
    @DisplayName("calcularNotaFinal deve retornar 0.0 sem nenhuma parte")
    void calcularNotaFinal_SemPartes_RetornaZero() {
        Avaliacao avaliacao = new Avaliacao(colaboradorPadrao, numeroPadrao);
        assertEquals(0.0, avaliacao.calcularNotaFinal());
    }
}