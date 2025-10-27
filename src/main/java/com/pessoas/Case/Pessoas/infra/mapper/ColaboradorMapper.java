package com.pessoas.Case.Pessoas.infra.mapper;


import com.pessoas.Case.Pessoas.domain.model.Colaborador;
import com.pessoas.Case.Pessoas.infra.persistence.ColaboradorEntity;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public ColaboradorEntity toEntity(Colaborador domain) {
        ColaboradorEntity entity = new ColaboradorEntity();
        entity.setMatricula(domain.getMatricula());
        entity.setNome(domain.getNome());
        entity.setDataAdmissao(domain.getDataAdmissao());
        entity.setCargo(domain.getCargo());
        return entity;
    }

    public Colaborador toDomain(ColaboradorEntity entity) {
        return new Colaborador(
                entity.getMatricula(),
                entity.getNome(),
                entity.getDataAdmissao(),
                entity.getCargo()
        );
    }
}