package com.pessoas.Case.Pessoas.infra.web.controller;

import com.pessoas.Case.Pessoas.application.dto.*;
import com.pessoas.Case.Pessoas.application.dto.AvaliacaoEntregasRequestDTO;
import com.pessoas.Case.Pessoas.application.portApi.AutenticacaoUseCase;
import com.pessoas.Case.Pessoas.application.portApi.AvaliacaoUseCase;
import com.pessoas.Case.Pessoas.application.portApi.ColaboradorUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ColaboradorController {

    private static final Logger log = LoggerFactory.getLogger(ColaboradorController.class);
    private final AutenticacaoUseCase autenticacaoUseCase;
    private final ColaboradorUseCase colaboradorUseCase;
    private final AvaliacaoUseCase avaliacaoUseCase;

    @PostMapping("/auth/token")
    public ResponseEntity<AuthResponseDTO> autenticar(@RequestBody @Valid AuthRequestDTO dto) {
        log.info(">>> POST /auth/token - clientId: {}", dto.clientId());

        AuthResponseDTO token = autenticacaoUseCase.autenticar(dto);
        log.info("<<< POST /auth/token - Token gerado com sucesso.");

        return ResponseEntity.ok(token);
    }

    @PostMapping("/colaboradores")
    public ResponseEntity<ColaboradorDTO> cadastrarColaborador(@RequestBody @Valid ColaboradorDTO dto) {
        log.info(">>> POST /colaboradores - matricula: {}", dto.matricula());

        ColaboradorDTO novo = colaboradorUseCase.criarColaborador(dto);
        log.info("<<< POST /colaboradores - Colaborador {} criado. Retornando 201.", novo.matricula());

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @GetMapping("/colaboradores/{matricula}")
    public ResponseEntity<ColaboradorDTO> recuperarColaborador(@PathVariable String matricula) {
        log.info(">>> GET /colaboradores/{}", matricula);

        ColaboradorDTO dto = colaboradorUseCase.buscarColaborador(matricula);
        log.info("<<< GET /colaboradores/{} - Colaborador encontrado. Retornando 200.", matricula);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/colaboradores/{matricula}")
    public ResponseEntity<ColaboradorDTO> atualizarColaborador(@PathVariable String matricula, @RequestBody @Valid ColaboradorDTO dto) {
        log.info(">>> PUT /colaboradores/{}", matricula);
        ColaboradorDTO atualizado = colaboradorUseCase.atualizarColaborador(matricula, dto);
        log.info("<<< PUT /colaboradores/{} - Colaborador atualizado. Retornando 200.", matricula);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/colaboradores/{matricula}")
    public ResponseEntity<Void> deletarColaborador(@PathVariable String matricula) {
        log.info(">>> DELETE /colaboradores/{}", matricula);

        colaboradorUseCase.deletarColaborador(matricula);
        log.info("<<< DELETE /colaboradores/{} - Colaborador deletado. Retornando 204.", matricula);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/colaboradores/{matricula}/avaliacoes/{numero}/completa")
    public ResponseEntity<PerformanceGeralDTO> cadastrarAvaliacaoCompleta(
            @PathVariable String matricula,
            @PathVariable int numero,
            @RequestBody @Valid AvaliacaoCompletaRequestDTO dto) {
        log.info(">>> POST /colaboradores/{}/avaliacoes/{}/completa", matricula, numero);

        PerformanceGeralDTO resultado = avaliacaoUseCase.cadastrarAvaliacaoCompleta(matricula, numero, dto);
        log.info("<<< POST /colaboradores/{}/avaliacoes/{}/completa - Avaliação cadastrada/atualizada. Retornando 201.", matricula, numero);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @PostMapping("/colaboradores/{matricula}/avaliacoes/{numero}/comportamental")
    public ResponseEntity<PerformanceGeralDTO> cadastrarAvaliacaoComportamental(
            @PathVariable String matricula,
            @PathVariable int numero,
            @RequestBody @Valid AvaliacaoComportamentalDTO dto) {
        log.info(">>> POST /colaboradores/{}/avaliacoes/{}/comportamental", matricula, numero);
        PerformanceGeralDTO resultado = avaliacaoUseCase.cadastrarAvaliacaoComportamental(matricula, numero, dto);
        log.info("<<< POST /colaboradores/{}/avaliacoes/{}/comportamental - Avaliação cadastrada/atualizada. Retornando 200.", matricula, numero);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/colaboradores/{matricula}/avaliacoes/{numero}/entregas")
    public ResponseEntity<PerformanceGeralDTO> cadastrarAvaliacaoEntregas(
            @PathVariable String matricula,
            @PathVariable int numero,
            @RequestBody @Valid AvaliacaoEntregasRequestDTO dto) {
        log.info(">>> POST /colaboradores/{}/avaliacoes/{}/entregas - {} desafios", matricula, numero, dto.desafios().size());
        PerformanceGeralDTO resultado = avaliacaoUseCase.cadastrarAvaliacaoEntregas(matricula, numero, dto);
        log.info("<<< POST /colaboradores/{}/avaliacoes/{}/entregas - Avaliação cadastrada/atualizada. Retornando 200.", matricula, numero);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/colaboradores/{matricula}/performance/{numero}")
    public ResponseEntity<PerformanceGeralDTO> recuperarPerformance(
            @PathVariable String matricula,
            @PathVariable int numero) {
        log.info(">>> GET /colaboradores/{}/performance/{}", matricula, numero);
        PerformanceGeralDTO nota = avaliacaoUseCase.recuperarPerformance(matricula, numero);
        log.info("<<< GET /colaboradores/{}/performance/{} - Performance encontrada. Retornando 200.", matricula, numero);
        return ResponseEntity.ok(nota);
    }

    @GetMapping("/colaboradores/{matricula}/performance")
    public ResponseEntity<List<PerformanceGeralDTO>> recuperarHistoricoPerformance(
            @PathVariable String matricula) {
        log.info(">>> GET /colaboradores/{}/performance (histórico)", matricula);
        List<PerformanceGeralDTO> notas = avaliacaoUseCase.recuperarHistoricoPerformance(matricula);
        log.info("<<< GET /colaboradores/{}/performance - {} registros encontrados. Retornando 200.", matricula, notas.size());
        return ResponseEntity.ok(notas);
    }
}