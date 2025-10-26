package com.pessoas.Case.Pessoas.application.mapper;


import com.pessoas.Case.Pessoas.application.dto.ColaboradorDTO;
import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorApiMapper {

    public Colaborador toDomain(ColaboradorDTO dto) {
        if (dto == null) return null;

        return new Colaborador(
                dto.matricula(),
                dto.nome(),
                dto.dataAdmissao(),
                dto.cargo()
        );
    }

    public ColaboradorDTO toDTO(Colaborador domain) {
        if (domain == null) return null;

        return new ColaboradorDTO(
                domain.getMatricula(),
                domain.getNome(),
                domain.getDataAdmissao(),
                domain.getCargo()
        );
    }
}