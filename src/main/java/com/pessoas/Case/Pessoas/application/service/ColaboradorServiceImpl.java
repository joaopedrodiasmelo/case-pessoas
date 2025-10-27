package com.pessoas.Case.Pessoas.application.service;

import com.pessoas.Case.Pessoas.application.dto.ColaboradorDTO;
import com.pessoas.Case.Pessoas.application.exception.RecursoNaoEncontradoException;
import com.pessoas.Case.Pessoas.application.exception.RegraDeNegocioException;
import com.pessoas.Case.Pessoas.application.mapper.ColaboradorApiMapper;
import com.pessoas.Case.Pessoas.application.portApi.ColaboradorUseCase;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.domain.repository.ColaboradorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ColaboradorServiceImpl implements ColaboradorUseCase {

    private static final Logger log = LoggerFactory.getLogger(ColaboradorServiceImpl.class);
    private final ColaboradorRepositoryPort colaboradorRepository;
    private final ColaboradorApiMapper apiMapper;

    @Override
    public ColaboradorDTO criarColaborador(ColaboradorDTO dto) {
        log.info("Iniciando caso de uso [criarColaborador] para matrícula {}", dto.matricula());

        if (colaboradorRepository.existePorMatricula(dto.matricula())) {
            log.warn("Tentativa de cadastrar matrícula duplicada: {}", dto.matricula());
            throw new RegraDeNegocioException("Matrícula " + dto.matricula() + " já cadastrada.");
        }

        Colaborador colaborador = apiMapper.toDomain(dto);
        Colaborador salvo = colaboradorRepository.salvar(colaborador);
        log.info("Caso de uso [criarColaborador] finalizado com sucesso para matrícula {}", salvo.getMatricula());

        return apiMapper.toDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public ColaboradorDTO buscarColaborador(String matricula) {
        log.info("Iniciando caso de uso [buscarColaborador] para matrícula {}", matricula);

        Colaborador colaborador = colaboradorRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Colaborador com matrícula " + matricula + " não encontrado."
                ));

        log.info("Caso de uso [buscarColaborador] finalizado com sucesso para matrícula {}", matricula);
        return apiMapper.toDTO(colaborador);
    }

    @Override
    @Transactional
    public ColaboradorDTO atualizarColaborador(String matricula, ColaboradorDTO dto) {
        log.info("Iniciando caso de uso [atualizarColaborador] para matrícula {}", matricula);

        if (!matricula.equals(dto.matricula())) {
            log.warn("Tentativa de alterar matrícula de {} para {}", matricula, dto.matricula());
            throw new RegraDeNegocioException("Não é permitido alterar a matrícula de um colaborador.");
        }

        Colaborador colaboradorExistente = colaboradorRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Colaborador com matrícula " + matricula + " não encontrado para atualização."
                ));

        colaboradorExistente.setNome(dto.nome());
        colaboradorExistente.setCargo(dto.cargo());
        colaboradorExistente.setDataAdmissao(dto.dataAdmissao());

        Colaborador atualizado = colaboradorRepository.salvar(colaboradorExistente);

        log.info("Caso de uso [atualizarColaborador] finalizado com sucesso para matrícula {}", matricula);
        return apiMapper.toDTO(atualizado);
    }

    @Override
    @Transactional
    public void deletarColaborador(String matricula) {
        log.info("Iniciando caso de uso [deletarColaborador] para matrícula {}", matricula);

        if (!colaboradorRepository.existePorMatricula(matricula)) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador com matrícula " + matricula + " não encontrado para deleção."
            );
        }

        log.info("Caso de uso [deletarColaborador] finalizado com sucesso para matrícula {}", matricula);
        colaboradorRepository.deletarPorMatricula(matricula);
    }
}