package com.pessoas.Case.Pessoas.application.service;


import com.pessoas.Case.Pessoas.application.dto.AuthRequestDTO;
import com.pessoas.Case.Pessoas.application.dto.AuthResponseDTO;
import com.pessoas.Case.Pessoas.application.portApi.AutenticacaoUseCase;
import com.pessoas.Case.Pessoas.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoUseCase {

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.security.client.id}")
    private String staticClientId;
    @Value("${api.security.client.secret}")
    private String staticClientSecretHash;

    public AutenticacaoServiceImpl(TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDTO autenticar(AuthRequestDTO dto) {
        if (!staticClientId.equals(dto.clientId())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }
        if (!passwordEncoder.matches(dto.password(), staticClientSecretHash)) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }
        String tokenJwt = tokenService.gerarToken(dto.clientId());
        return new AuthResponseDTO(tokenJwt);
    }
}
