package com.pessoas.Case.Pessoas.domain.model;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException; // Importe sua exceção
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoComportamentalTest {


    @Test
    @DisplayName("Deve criar AvaliacaoComportamental com todas as notas válidas (ex: 4)")
    void construtor_ComNotasValidas_CriaInstanciaComSucesso() {
        AvaliacaoComportamental ac = assertDoesNotThrow(
                () -> new AvaliacaoComportamental(4, 4, 4, 4)
        );
        assertEquals(4, ac.getPromoveAmbienteColaborativo());
        assertEquals(4, ac.getSeAtualizaEaprende());
        assertEquals(4, ac.getUtilizaDadosDecisoes());
        assertEquals(4, ac.getTrabalhaComAutonomia());
    }

    @Test
    @DisplayName("Deve criar AvaliacaoComportamental com notas nos limites (1 e 5)")
    void construtor_ComNotasNosLimites_CriaInstanciaComSucesso() {
        assertDoesNotThrow(() -> new AvaliacaoComportamental(1, 5, 1, 5));
    }


    @ParameterizedTest
    @CsvSource({
            "0, 4, 4, 4",  // Primeira nota inválida (<1)
            "4, 0, 4, 4",  // Segunda nota inválida (<1)
            "4, 4, 0, 4",  // Terceira nota inválida (<1)
            "4, 4, 4, 0",  // Quarta nota inválida (<1)
            "6, 4, 4, 4",  // Primeira nota inválida (>5)
            "4, 6, 4, 4",  // Segunda nota inválida (>5)
            "4, 4, 6, 4",  // Terceira nota inválida (>5)
            "4, 4, 4, 6",  // Quarta nota inválida (>5)
            "0, 0, 0, 0",  // Todas inválidas (<1)
            "6, 6, 6, 6"   // Todas inválidas (>5)
    })
    @DisplayName("Deve lançar RegraDeNegocioException se alguma nota for inválida (fora de 1-5)")
    void construtor_ComAlgumaNotaInvalida_LancaExcecao(int n1, int n2, int n3, int n4) {
        RegraDeNegocioException ex = assertThrows(
                RegraDeNegocioException.class,
                () -> new AvaliacaoComportamental(n1, n2, n3, n4)
        );
        assertTrue(ex.getMessage().contains("Todas as notas comportamentais devem ser entre 1 e 5"));
    }


    @Test
    @DisplayName("Deve calcular média ponderada corretamente (pesos iguais)")
    void calcularMediaPonderada_ComNotasValidas_RetornaMediaCorreta() {
        AvaliacaoComportamental ac = new AvaliacaoComportamental(4, 5, 3, 4); // Soma = 16
        double mediaEsperada = 16.0 / 4.0;

        double mediaCalculada = ac.calcularMediaPonderada();

        assertEquals(mediaEsperada, mediaCalculada, 0.01, "A média calculada está incorreta.");
    }

    @Test
    @DisplayName("Deve calcular média ponderada corretamente com notas diferentes")
    void calcularMediaPonderada_ComNotasDiferentes_RetornaMediaCorreta() {
        AvaliacaoComportamental ac = new AvaliacaoComportamental(1, 2, 3, 5); // Soma = 11
        double mediaEsperada = 11.0 / 4.0;

        double mediaCalculada = ac.calcularMediaPonderada();

        assertEquals(mediaEsperada, mediaCalculada, 0.01);
    }

    @Test
    @DisplayName("Deve calcular média ponderada corretamente com todas as notas iguais a 5")
    void calcularMediaPonderada_ComNotasMaximas_RetornaMediaCorreta() {

        AvaliacaoComportamental ac = new AvaliacaoComportamental(5, 5, 5, 5); // Soma = 20
        double mediaEsperada = 20.0 / 4.0;

        double mediaCalculada = ac.calcularMediaPonderada();

        assertEquals(mediaEsperada, mediaCalculada, 0.01);
    }
}