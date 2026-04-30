package com.example.unittestexample.mappers;

import com.example.unittestexample.dtos.TurmaDetalhesDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.models.Turma;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {AlunoMapper.class})
public interface TurmaMapper {

  TurmaResumoDto paraResumoDto(Turma turma);

  TurmaDetalhesDto paraDetalhesDto(Turma turma);

  Turma paraEntity(TurmaDto dto);
}
