package com.example.unittestexample.mappers;

import com.example.unittestexample.dtos.TurmaDetalhesDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.models.Turma;
import java.time.LocalTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TurmaMapperTest {

  TurmaMapper INSTANCE = Mappers.getMapper(TurmaMapper.class);

  @Test
  void paraResumoDto() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());
    TurmaResumoDto turmaDto = INSTANCE.paraResumoDto(turma);

    Assertions.assertNotNull(turmaDto, "O DTO não deveria ser nulo");
    Assertions.assertEquals("TURMA_A1", turma.getNome());
    Assertions.assertEquals(turma.getId(), turmaDto.id());
    Assertions.assertEquals(LocalTime.of(19, 0), turmaDto.horarioInicio());
    Assertions.assertEquals(LocalTime.of(21, 0), turmaDto.horarioFim());
  }

  @Test
  void paraDetalhesDto() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());
    TurmaDetalhesDto turmaDto = INSTANCE.paraDetalhesDto(turma);

    Assertions.assertNotNull(turmaDto, "O DTO não deveria ser nulo");
    Assertions.assertEquals("TURMA_A1", turma.getNome());
    Assertions.assertEquals(turma.getId(), turmaDto.id());
    Assertions.assertEquals(LocalTime.of(19, 0), turmaDto.horarioInicio());
    Assertions.assertEquals(LocalTime.of(21, 0), turmaDto.horarioFim());
  }

  @Test
  void paraEntity() {
    TurmaDto turmaDto = new TurmaDto(1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 30);

    Turma turma = INSTANCE.paraEntity(turmaDto);

    Assertions.assertNotNull(turmaDto, "O DTO não deveria ser nulo");
    Assertions.assertEquals("TURMA_A1", turma.getNome());
    Assertions.assertEquals(turma.getId(), turmaDto.getId());
    Assertions.assertEquals(LocalTime.of(19, 0), turmaDto.getHorarioInicio());
    Assertions.assertEquals(LocalTime.of(21, 0), turmaDto.getHorarioFim());
  }
}
