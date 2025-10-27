package com.pessoas.Case.Pessoas.infra.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.application.portApi.AutenticacaoUseCase;
import com.pessoas.Case.Pessoas.application.portApi.AvaliacaoUseCase;
import com.pessoas.Case.Pessoas.application.portApi.ColaboradorUseCase;
import com.pessoas.Case.Pessoas.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ColaboradorController.class)
class ColaboradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private AutenticacaoUseCase autenticacaoUseCase;
    @MockitoBean
    private ColaboradorUseCase colaboradorUseCase;
    @MockitoBean
    private AvaliacaoUseCase avaliacaoUseCase;

    // Dados de Teste
    private AuthRequestDTO authRequestDTO;
    private AuthResponseDTO authResponseDTO;
    private ColaboradorDTO colaboradorDTO;
    private AvaliacaoComportamentalDTO comportamentalDTO;
    private AvaliacaoEntregasRequestDTO entregasRequestDTO;
    private AvaliacaoCompletaRequestDTO completaRequestDTO;
    private PerformanceGeralDTO performanceDTO;
    private final String matricula = "123456789";
    private final int numero = 2024;
    private final String baseUrl = "/api/v1";

    @BeforeEach
    void setUp() {
        authRequestDTO = new AuthRequestDTO("client-test", "pass123");
        authResponseDTO = new AuthResponseDTO("jwt.token.test");
        colaboradorDTO = new ColaboradorDTO(matricula, "Controller Test", LocalDate.now(), "Tester");
        performanceDTO = new PerformanceGeralDTO(matricula, numero, 4.1, 4.2, 4.16); // Status removido

        comportamentalDTO = new AvaliacaoComportamentalDTO(4,5,3,4);
        List<DesafioDTO> desafiosDTO = List.of(new DesafioDTO("d1", 5), new DesafioDTO("d2", 4));
        entregasRequestDTO = new AvaliacaoEntregasRequestDTO(desafiosDTO);
        completaRequestDTO = new AvaliacaoCompletaRequestDTO(comportamentalDTO, desafiosDTO);
    }


    @Test
    @DisplayName("POST /colaboradores - Success")
    @WithMockUser
    void cadastrarColaborador_Success() throws Exception {
        when(colaboradorUseCase.criarColaborador(any(ColaboradorDTO.class))).thenReturn(colaboradorDTO);

        mockMvc.perform(post(baseUrl + "/colaboradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(colaboradorDTO))
                        .with(csrf())) // Adiciona CSRF token
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(colaboradorUseCase).criarColaborador(any(ColaboradorDTO.class));
    }

    @Test
    @DisplayName("GET /colaboradores/{matricula} - Success")
    @WithMockUser
    void recuperarColaborador_Success() throws Exception {
        when(colaboradorUseCase.buscarColaborador(matricula)).thenReturn(colaboradorDTO);

        mockMvc.perform(get(baseUrl + "/colaboradores/{matricula}", matricula)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(colaboradorUseCase).buscarColaborador(matricula);
    }

    @Test
    @DisplayName("PUT /colaboradores/{matricula} - Success")
    @WithMockUser
    void atualizarColaborador_Success() throws Exception {
        when(colaboradorUseCase.atualizarColaborador(eq(matricula), any(ColaboradorDTO.class))).thenReturn(colaboradorDTO);

        mockMvc.perform(put(baseUrl + "/colaboradores/{matricula}", matricula)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(colaboradorDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(colaboradorUseCase).atualizarColaborador(eq(matricula), any(ColaboradorDTO.class));
    }

    @Test
    @DisplayName("DELETE /colaboradores/{matricula} - Success")
    @WithMockUser
    void deletarColaborador_Success() throws Exception {
        doNothing().when(colaboradorUseCase).deletarColaborador(matricula);

        mockMvc.perform(delete(baseUrl + "/colaboradores/{matricula}", matricula)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(colaboradorUseCase).deletarColaborador(matricula);
    }

    @Test
    @DisplayName("POST /avaliacoes/completa - Success")
    @WithMockUser
    void cadastrarAvaliacaoCompleta_Success() throws Exception {
        when(avaliacaoUseCase.cadastrarAvaliacaoCompleta(eq(matricula), eq(numero), any(AvaliacaoCompletaRequestDTO.class)))
                .thenReturn(performanceDTO);

        mockMvc.perform(post(baseUrl + "/colaboradores/{mat}/avaliacoes/{num}/completa", matricula, numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completaRequestDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value(matricula))
                .andExpect(jsonPath("$.numero").value(numero));

        verify(avaliacaoUseCase).cadastrarAvaliacaoCompleta(eq(matricula), eq(numero), any(AvaliacaoCompletaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /avaliacoes/comportamental - Success")
    @WithMockUser
    void cadastrarAvaliacaoComportamental_Success() throws Exception {
        when(avaliacaoUseCase.cadastrarAvaliacaoComportamental(eq(matricula), eq(numero), any(AvaliacaoComportamentalDTO.class)))
                .thenReturn(performanceDTO);

        mockMvc.perform(post(baseUrl + "/colaboradores/{mat}/avaliacoes/{num}/comportamental", matricula, numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comportamentalDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(avaliacaoUseCase).cadastrarAvaliacaoComportamental(eq(matricula), eq(numero), any(AvaliacaoComportamentalDTO.class));
    }

    @Test
    @DisplayName("POST /avaliacoes/entregas - Success")
    @WithMockUser
    void cadastrarAvaliacaoEntregas_Success() throws Exception {
        when(avaliacaoUseCase.cadastrarAvaliacaoEntregas(eq(matricula), eq(numero), any(AvaliacaoEntregasRequestDTO.class)))
                .thenReturn(performanceDTO);

        mockMvc.perform(post(baseUrl + "/colaboradores/{mat}/avaliacoes/{num}/entregas", matricula, numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entregasRequestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(avaliacaoUseCase).cadastrarAvaliacaoEntregas(eq(matricula), eq(numero), any(AvaliacaoEntregasRequestDTO.class));
    }

    @Test
    @DisplayName("GET /performance/{numero} - Success")
    @WithMockUser
    void recuperarPerformance_Success() throws Exception {
        when(avaliacaoUseCase.recuperarPerformance(matricula, numero)).thenReturn(performanceDTO);

        mockMvc.perform(get(baseUrl + "/colaboradores/{mat}/performance/{num}", matricula, numero)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula));

        verify(avaliacaoUseCase).recuperarPerformance(matricula, numero);
    }

    @Test
    @DisplayName("GET /performance - Success")
    @WithMockUser
    void recuperarHistoricoPerformance_Success() throws Exception {
        List<PerformanceGeralDTO> lista = Collections.singletonList(performanceDTO);
        when(avaliacaoUseCase.recuperarHistoricoPerformance(matricula)).thenReturn(lista);

        mockMvc.perform(get(baseUrl + "/colaboradores/{mat}/performance", matricula)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].matricula").value(matricula));

        verify(avaliacaoUseCase).recuperarHistoricoPerformance(matricula);
    }

    @Test
    @DisplayName("GET /performance - Empty List Success")
    @WithMockUser
    void recuperarHistoricoPerformance_EmptyList_Success() throws Exception {
        when(avaliacaoUseCase.recuperarHistoricoPerformance(matricula)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(baseUrl + "/colaboradores/{mat}/performance", matricula)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(avaliacaoUseCase).recuperarHistoricoPerformance(matricula);
    }
}

