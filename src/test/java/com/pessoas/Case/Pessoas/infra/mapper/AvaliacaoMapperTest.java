package com.pessoas.Case.Pessoas.infra.mapper;

import com.pessoas.Case.Pessoas.domain.model.Avaliacao;
import com.pessoas.Case.Pessoas.domain.model.AvaliacaoComportamental;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.model.Desafio;
import com.pessoas.Case.Pessoas.infra.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AvaliacaoMapperTest {

    @Mock
    private ColaboradorMapper colaboradorMapper;

    @InjectMocks
    private AvaliacaoMapper avaliacaoMapper;

    private Colaborador colaboradorDomain;
    private ColaboradorEntity colaboradorEntity;
    private AvaliacaoId avaliacaoId;
    private AvaliacaoComportamentalEntity compEntity;
    private AvaliacaoEntregaEntity entregaEntity;
    private DesafioEntity desafioEntity1;
    private DesafioEntity desafioEntity2;
    private Avaliacao avaliacaoCompleta;
    private Avaliacao avaliacaoSoComp;
    private Avaliacao avaliacaoSoEntrega;
    private final String matricula = "MAP01";
    private final int numero = 2024;

    @BeforeEach
    void setUp() {
        colaboradorDomain = new Colaborador(matricula, "Map Colab", LocalDate.now(), "Mapper");
        colaboradorEntity = new ColaboradorEntity();
        colaboradorEntity.setMatricula(matricula);

        avaliacaoId = new AvaliacaoId(matricula, numero);

        compEntity = new AvaliacaoComportamentalEntity();
        compEntity.setId(avaliacaoId);
        compEntity.setColaborador(colaboradorEntity);
        compEntity.setNotaPergunta1(4);
        compEntity.setNotaPergunta2(3);
        compEntity.setNotaPergunta3(5);
        compEntity.setNotaPergunta4(4);

        entregaEntity = new AvaliacaoEntregaEntity();
        entregaEntity.setId(avaliacaoId);
        entregaEntity.setColaborador(colaboradorEntity);

        desafioEntity1 = new DesafioEntity();
        desafioEntity1.setDescricaoDesafio("Desc 1");
        desafioEntity1.setNotaDesafio(5);
        desafioEntity1.setAvaliacaoEntrega(entregaEntity);

        desafioEntity2 = new DesafioEntity();
        desafioEntity2.setDescricaoDesafio("Desc 2");
        desafioEntity2.setNotaDesafio(3);
        desafioEntity2.setAvaliacaoEntrega(entregaEntity);

        entregaEntity.setDesafios(new ArrayList<>(List.of(desafioEntity1, desafioEntity2)));

        avaliacaoCompleta = new Avaliacao(colaboradorDomain, numero);
        avaliacaoCompleta.setComportamental(new AvaliacaoComportamental(4, 3, 5, 4));
        avaliacaoCompleta.setEntregas(List.of(new Desafio("Desc 1", 5), new Desafio("Desc 2", 3)));

        avaliacaoSoComp = new Avaliacao(colaboradorDomain, numero);
        avaliacaoSoComp.setComportamental(new AvaliacaoComportamental(4, 3, 5, 4));

        avaliacaoSoEntrega = new Avaliacao(colaboradorDomain, numero);
        avaliacaoSoEntrega.setEntregas(List.of(new Desafio("Desc 1", 5), new Desafio("Desc 2", 3)));

        lenient().when(colaboradorMapper.toDomain(colaboradorEntity)).thenReturn(colaboradorDomain);
        lenient().when(colaboradorMapper.toEntity(colaboradorDomain)).thenReturn(colaboradorEntity);
    }


    @Test
    @DisplayName("toDomain deve reconstruir Avaliacao completa")
    void toDomain_QuandoAmbasEntidadesPresentes_RetornaAvaliacaoCompleta() {
        Avaliacao resultado = avaliacaoMapper.toDomain(Optional.of(compEntity), Optional.of(entregaEntity));

        assertNotNull(resultado);
        assertEquals(numero, resultado.getNumero());
        assertEquals(colaboradorDomain, resultado.getColaborador());
        assertNotNull(resultado.getComportamental());
        assertNotNull(resultado.getDesafios());
        assertEquals(2, resultado.getDesafios().size());
        assertEquals(5, resultado.getDesafios().get(0).getNota());
    }

    @Test
    @DisplayName("toDomain deve reconstruir Avaliacao só com comportamental")
    void toDomain_QuandoSoCompPresente_RetornaAvaliacaoParcialComp() {
        Avaliacao resultado = avaliacaoMapper.toDomain(Optional.of(compEntity), Optional.empty());

        assertNotNull(resultado);
        assertEquals(numero, resultado.getNumero());
        assertEquals(colaboradorDomain, resultado.getColaborador());
        assertNotNull(resultado.getComportamental());
        assertNull(resultado.getDesafios());
    }

    @Test
    @DisplayName("toDomain deve reconstruir Avaliacao só com entrega")
    void toDomain_QuandoSoEntregaPresente_RetornaAvaliacaoParcialEntrega() {
        Avaliacao resultado = avaliacaoMapper.toDomain(Optional.empty(), Optional.of(entregaEntity));

        assertNotNull(resultado);
        assertEquals(numero, resultado.getNumero());
        assertEquals(colaboradorDomain, resultado.getColaborador());
        assertNull(resultado.getComportamental());
        assertNotNull(resultado.getDesafios());
        assertEquals(2, resultado.getDesafios().size());
    }

    @Test
    @DisplayName("toDomain deve retornar null se ambas entidades ausentes")
    void toDomain_QuandoAmbasAusentes_RetornaNull() {
        Avaliacao resultado = avaliacaoMapper.toDomain(Optional.empty(), Optional.empty());
        assertNull(resultado);
    }

    @Test
    @DisplayName("toDomain deve lançar exceção se entidade não tiver colaborador ou id")
    void toDomain_QuandoFaltamDadosChave_LancaExcecao() {
        compEntity.setColaborador(null);
        assertThrows(NoSuchElementException.class, () -> avaliacaoMapper.toDomain(Optional.of(compEntity), Optional.empty()));
    }



    @Test
    @DisplayName("toEntityComportamental deve criar nova entidade")
    void toEntityComportamental_QuandoNovaAvaliacao_CriaEntidade() {
        AvaliacaoComportamentalEntity resultado = avaliacaoMapper.toEntityComportamental(avaliacaoCompleta, colaboradorEntity, Optional.empty());

        assertNotNull(resultado);
        assertEquals(avaliacaoId, resultado.getId());
        assertEquals(colaboradorEntity, resultado.getColaborador());
        assertEquals(4, resultado.getNotaPergunta1());
    }

    @Test
    @DisplayName("toEntityComportamental deve atualizar entidade existente")
    void toEntityComportamental_QuandoAtualizaAvaliacao_AtualizaEntidade() {
        AvaliacaoComportamentalEntity existente = new AvaliacaoComportamentalEntity();
        existente.setId(avaliacaoId);
        existente.setColaborador(colaboradorEntity);
        existente.setNotaPergunta1(1); // Nota antiga

        AvaliacaoComportamentalEntity resultado = avaliacaoMapper.toEntityComportamental(avaliacaoCompleta, colaboradorEntity, Optional.of(existente));

        assertNotNull(resultado);
        assertSame(existente, resultado); // Deve ser a mesma instância
        assertEquals(4, resultado.getNotaPergunta1()); // Nota atualizada
    }

    @Test
    @DisplayName("toEntityComportamental deve retornar null se domínio não tem parte comportamental")
    void toEntityComportamental_QuandoDominioSemComp_RetornaNull() {
        assertNull(avaliacaoMapper.toEntityComportamental(avaliacaoSoEntrega, colaboradorEntity, Optional.empty()));
    }


    @Test
    @DisplayName("toEntityEntrega deve criar nova entidade com desafios")
    void toEntityEntrega_QuandoNovaAvaliacao_CriaEntidadeComDesafios() {
        AvaliacaoEntregaEntity resultado = avaliacaoMapper.toEntityEntrega(avaliacaoCompleta, colaboradorEntity, Optional.empty());

        assertNotNull(resultado);
        assertEquals(avaliacaoId, resultado.getId());
        assertEquals(colaboradorEntity, resultado.getColaborador());
        assertNotNull(resultado.getDesafios());
        assertEquals(2, resultado.getDesafios().size());
        assertEquals("Desc 1", resultado.getDesafios().get(0).getDescricaoDesafio());
        assertSame(resultado, resultado.getDesafios().get(0).getAvaliacaoEntrega()); // Verifica link bidirecional
    }

    @Test
    @DisplayName("toEntityEntrega deve atualizar entidade existente limpando e adicionando desafios")
    void toEntityEntrega_QuandoAtualizaAvaliacao_AtualizaEntidadeEDesafios() {
        AvaliacaoEntregaEntity existente = new AvaliacaoEntregaEntity();
        existente.setId(avaliacaoId);
        existente.setColaborador(colaboradorEntity);
        // Adiciona um desafio antigo para testar o clear()
        DesafioEntity desafioAntigo = new DesafioEntity();
        desafioAntigo.setDescricaoDesafio("Antigo");
        desafioAntigo.setNotaDesafio(1);
        existente.addDesafio(desafioAntigo);
        assertEquals(1, existente.getDesafios().size());

        AvaliacaoEntregaEntity resultado = avaliacaoMapper.toEntityEntrega(avaliacaoCompleta, colaboradorEntity, Optional.of(existente));

        assertNotNull(resultado);
        assertSame(existente, resultado); // Mesma instância
        assertEquals(2, resultado.getDesafios().size()); // Lista atualizada
        assertEquals("Desc 1", resultado.getDesafios().get(0).getDescricaoDesafio());
        assertEquals("Desc 2", resultado.getDesafios().get(1).getDescricaoDesafio());
    }

    @Test
    @DisplayName("toEntityEntrega deve retornar null se domínio não tem desafios")
    void toEntityEntrega_QuandoDominioSemDesafios_RetornaNull() {
        assertNull(avaliacaoMapper.toEntityEntrega(avaliacaoSoComp, colaboradorEntity, Optional.empty()));
    }
}