package com.pessoas.Case.Pessoas.infra.mapper;

import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ColaboradorMapperTest {

    private ColaboradorMapper mapper;

    private Colaborador colaboradorDomain;
    private ColaboradorEntity colaboradorEntity;
    private final String matricula = "MAPTEST123";
    private final String nome = "Mapper Test";
    private final LocalDate dataAdmissao = LocalDate.of(2024, 1, 1);
    private final String cargo = "Test Engineer";

    @BeforeEach
    void setUp() {
        mapper = new ColaboradorMapper();

        colaboradorDomain = new Colaborador(matricula, nome, dataAdmissao, cargo);

        colaboradorEntity = new ColaboradorEntity();
        colaboradorEntity.setMatricula(matricula);
        colaboradorEntity.setNome(nome);
        colaboradorEntity.setDataAdmissao(dataAdmissao);
        colaboradorEntity.setCargo(cargo);
    }

    @Test
    @DisplayName("toEntity deve converter Colaborador (domain) para ColaboradorEntity")
    void toEntity_ComDominioValido_ConverteParaEntidade() {
        ColaboradorEntity resultEntity = mapper.toEntity(colaboradorDomain);

        assertNotNull(resultEntity);
        assertEquals(matricula, resultEntity.getMatricula());
        assertEquals(nome, resultEntity.getNome());
        assertEquals(dataAdmissao, resultEntity.getDataAdmissao());
        assertEquals(cargo, resultEntity.getCargo());
    }

    @Test
    @DisplayName("toEntity deve lançar NullPointerException para Colaborador (domain) nulo")
    void toEntity_ComDominioNulo_LancaNullPointerException() {
        Colaborador domainNulo = null;
        assertThrows(NullPointerException.class, () -> mapper.toEntity(domainNulo));
    }

    @Test
    @DisplayName("toDomain deve converter ColaboradorEntity para Colaborador (domain)")
    void toDomain_ComEntidadeValida_ConverteParaDominio() {
        Colaborador resultDomain = mapper.toDomain(colaboradorEntity);

        assertNotNull(resultDomain);
        assertEquals(matricula, resultDomain.getMatricula());
        assertEquals(nome, resultDomain.getNome());
        assertEquals(dataAdmissao, resultDomain.getDataAdmissao());
        assertEquals(cargo, resultDomain.getCargo());
    }

    @Test
    @DisplayName("toDomain deve lançar NullPointerException para ColaboradorEntity nula")
    void toDomain_ComEntidadeNula_LancaNullPointerException() {
        ColaboradorEntity entityNula = null;
        assertThrows(NullPointerException.class, () -> mapper.toDomain(entityNula));
    }
}