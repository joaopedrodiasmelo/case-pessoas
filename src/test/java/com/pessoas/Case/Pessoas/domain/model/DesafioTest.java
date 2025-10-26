package com.pessoas.Case.Pessoas.domain.model;

import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DesafioTest {


    @Test
    @DisplayName("Deve criar Desafio com descrição e nota válidas")
    void construtor_ComDadosValidos_CriaInstanciaComSucesso() {
        String descricaoValida = "Implementar funcionalidade X";
        int notaValida = 4;

        Desafio desafio = assertDoesNotThrow(
                () -> new Desafio(descricaoValida, notaValida)
        );

        assertNotNull(desafio);
        assertEquals(descricaoValida, desafio.getDescricao());
        assertEquals(notaValida, desafio.getNota());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5}) // Notas válidas
    @DisplayName("Deve criar Desafio com notas válidas (1 a 5)")
    void construtor_ComNotasValidas_CriaInstanciaComSucesso(int notaValida) {
        String descricaoValida = "Teste nota " + notaValida;

        Desafio desafio = assertDoesNotThrow(
                () -> new Desafio(descricaoValida, notaValida)
        );
        assertEquals(notaValida, desafio.getNota());
    }


    @Test
    @DisplayName("Deve lançar RegraDeNegocioException para descrição nula")
    void construtor_ComDescricaoNula_LancaExcecao() {
        String descricaoNula = null;
        int notaValida = 3;

        RegraDeNegocioException ex = assertThrows(
                RegraDeNegocioException.class,
                () -> new Desafio(descricaoNula, notaValida)
        );

        assertTrue(ex.getMessage().contains("descrição do desafio não pode ser vazia"));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException para descrição vazia")
    void construtor_ComDescricaoVazia_LancaExcecao() {
        String descricaoVazia = "";
        int notaValida = 3;

        RegraDeNegocioException ex = assertThrows(
                RegraDeNegocioException.class,
                () -> new Desafio(descricaoVazia, notaValida)
        );
        assertTrue(ex.getMessage().contains("descrição do desafio não pode ser vazia"));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException para descrição em branco")
    void construtor_ComDescricaoEmBranco_LancaExcecao() {
        String descricaoBranco = "   "; // Espaços
        int notaValida = 3;

        RegraDeNegocioException ex = assertThrows(
                RegraDeNegocioException.class,
                () -> new Desafio(descricaoBranco, notaValida)
        );
        assertTrue(ex.getMessage().contains("descrição do desafio não pode ser vazia"));
    }


    @ParameterizedTest // Testa múltiplos valores inválidos para a nota
    @ValueSource(ints = {0, -1, 6, 10}) // Notas inválidas
    @DisplayName("Deve lançar RegraDeNegocioException para notas inválidas (fora de 1-5)")
    void construtor_ComNotasInvalidas_LancaExcecao(int notaInvalida) {
        String descricaoValida = "Teste nota inválida";

        RegraDeNegocioException ex = assertThrows(
                RegraDeNegocioException.class,
                () -> new Desafio(descricaoValida, notaInvalida)
        );
        assertTrue(ex.getMessage().contains("deve ser entre 1 e 5"));
    }
}