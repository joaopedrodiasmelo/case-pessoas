package com.pessoas.Case.Pessoas.application.mapper;

import com.pessoas.Case.Pessoas.application.dto.ColaboradorDTO;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ColaboradorApiMapperTest {

    private ColaboradorApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ColaboradorApiMapper();
    }



    @Test
    @DisplayName("toDomain deve converter DTO válido para Modelo de Domínio")
    void toDomain_ComDtoValido_ConverteCorretamente() {

        LocalDate dataAdmissao = LocalDate.of(2023, 10, 26);
        ColaboradorDTO dto = new ColaboradorDTO("123456789", "Nome DTO", dataAdmissao, "Cargo DTO");

        Colaborador domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("123456789", domain.getMatricula());
        assertEquals("Nome DTO", domain.getNome());
        assertEquals(dataAdmissao, domain.getDataAdmissao());
        assertEquals("Cargo DTO", domain.getCargo());
    }

    @Test
    @DisplayName("toDomain deve retornar null para DTO nulo")
    void toDomain_ComDtoNulo_RetornaNull() {

        ColaboradorDTO dto = null;

        Colaborador domain = mapper.toDomain(dto);

        assertNull(domain);
    }


    @Test
    @DisplayName("toDTO deve converter Modelo de Domínio válido para DTO")
    void toDTO_ComDominioValido_ConverteCorretamente() {

        LocalDate dataAdmissao = LocalDate.of(2024, 1, 15);
        Colaborador domain = new Colaborador("987654321", "Nome Domain", dataAdmissao, "Cargo Domain");

        ColaboradorDTO dto = mapper.toDTO(domain);

        assertNotNull(dto);
        assertEquals("987654321", dto.matricula());
        assertEquals("Nome Domain", dto.nome());
        assertEquals(dataAdmissao, dto.dataAdmissao());
        assertEquals("Cargo Domain", dto.cargo());
    }

    @Test
    @DisplayName("toDTO deve retornar null para Modelo de Domínio nulo")
    void toDTO_ComDominioNulo_RetornaNull() {

        Colaborador domain = null;

        ColaboradorDTO dto = mapper.toDTO(domain);

        assertNull(dto);
    }
}