package com.pessoas.Case.Pessoas.application.service;

import com.pessoas.Case.Pessoas.application.dto.AuthRequestDTO;
import com.pessoas.Case.Pessoas.application.dto.AuthResponseDTO;
import com.pessoas.Case.Pessoas.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticacaoServiceImpl autenticacaoService;


    private final String testClientId = "test-client";
    private final String testClientSecretHash = "$2a$10$somehashvalue";
    private final String testRawPassword = "test-password";
    private final String generatedToken = "fake-jwt-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(autenticacaoService, "staticClientId", testClientId);
        ReflectionTestUtils.setField(autenticacaoService, "staticClientSecretHash", testClientSecretHash);
    }


    @Test
    @DisplayName("autenticar deve retornar DTO com token quando credenciais são válidas")
    void autenticar_ComCredenciaisValidas_RetornaAuthResponseDTO() {
        AuthRequestDTO requestDTO = new AuthRequestDTO(testClientId, testRawPassword);

        when(passwordEncoder.matches(testRawPassword, testClientSecretHash)).thenReturn(true);
        when(tokenService.gerarToken(testClientId)).thenReturn(generatedToken);

        AuthResponseDTO responseDTO = autenticacaoService.autenticar(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(generatedToken, responseDTO.token());

        verify(passwordEncoder, times(1)).matches(testRawPassword, testClientSecretHash);
        verify(tokenService, times(1)).gerarToken(testClientId);
    }


    @Test
    @DisplayName("autenticar deve lançar BadCredentialsException quando clientId é inválido")
    void autenticar_ComClientIdInvalido_LancaBadCredentialsException() {
        AuthRequestDTO requestDTO = new AuthRequestDTO("wrong-client-id", testRawPassword);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> autenticacaoService.autenticar(requestDTO)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).gerarToken(anyString());
    }

    @Test
    @DisplayName("autenticar deve lançar BadCredentialsException quando password é inválido")
    void autenticar_ComPasswordInvalido_LancaBadCredentialsException() {
        AuthRequestDTO requestDTO = new AuthRequestDTO(testClientId, "wrong-password");

        when(passwordEncoder.matches("wrong-password", testClientSecretHash)).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> autenticacaoService.autenticar(requestDTO)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());

        verify(passwordEncoder, times(1)).matches("wrong-password", testClientSecretHash);
        verify(tokenService, never()).gerarToken(anyString());
    }
}