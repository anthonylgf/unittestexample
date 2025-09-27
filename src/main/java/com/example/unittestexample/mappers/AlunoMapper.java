package com.example.unittestexample.mappers;

import static java.util.Objects.isNull;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.models.Aluno;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper
public interface AlunoMapper {

  AlunoMapper INSTANCE = Mappers.getMapper(AlunoMapper.class);

  Aluno mapearParaAluno(AlunoDto alunoDto);

  AlunoDto mapearParaAlunoDto(Aluno aluno);

  default Page<AlunoDto> mapearParaAlunoDtoPage(Page<Aluno> alunos) {
    if (isNull(alunos)) {
      return null;
    }

    return alunos.map(this::mapearParaAlunoDto);
  }

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(Aluno source, @MappingTarget Aluno target);

  @AfterMapping
  default void posMapping(@MappingTarget Aluno aluno, AlunoDto alunoDto) {
    var nomeCompleto = String.join(" ", alunoDto.getNome(), alunoDto.getSobrenome());
    aluno.setNomeCompleto(nomeCompleto);
  }

  @AfterMapping
  default void posMapping(@MappingTarget AlunoDto alunoDto, Aluno aluno) {
    String[] nomeCompleto = aluno.getNomeCompleto().split(" ");
    alunoDto.setNome(nomeCompleto[0]);
    alunoDto.setSobrenome(nomeCompleto[1]);
  }
}
