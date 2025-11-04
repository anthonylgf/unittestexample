package com.example.unittestexample.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class AlunoMapperTest {

  AlunoMapper alunoMapper = AlunoMapper.INSTANCE;

  private Aluno aluno =
      new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
  private AlunoDto alunoDto = new AlunoDto();
  private Aluno alunoTarget;
  private Aluno alunoService;

  @BeforeEach
  void setup() {
    alunoDto.setNome("Karine");
    alunoDto.setSobrenome("Ferreira");
    alunoDto.setGenero(Genero.FEMININO);
    alunoDto.setDataNascimento(LocalDate.now().minusYears(4L));

    alunoTarget = new Aluno(1L, "Nome", Genero.MASCULINO, LocalDate.now());
    alunoTarget.setNomeCompleto("Jose William");
    alunoTarget.setGenero(Genero.MASCULINO);
    alunoTarget.setId(1L);
    alunoTarget.setDataNascimento(LocalDate.now().minusYears(4L));

    alunoService = new Aluno(1L, "Nome", Genero.MASCULINO, LocalDate.now());
    alunoService.setNomeCompleto("Jose Ferreira");
    alunoService.setGenero(Genero.MASCULINO);
    alunoService.setId(99L);
    alunoService.setDataNascimento(null);
  }

  @Test
  void mapearParaAluno() {
    String nomeCompleto = "Karine Ferreira";
    Aluno aluno1 = AlunoMapper.INSTANCE.mapearParaAluno(alunoDto);
    assertNotNull(aluno1, "Aluno não pode ser nulo");
    assertEquals(nomeCompleto, aluno1.getNomeCompleto());
    assertEquals(alunoDto.getGenero(), aluno1.getGenero());
    assertEquals(alunoDto.getDataNascimento(), aluno1.getDataNascimento());
  }

  @Test
  void mapearParaAlunoDto() {
    AlunoDto aluno1 = AlunoMapper.INSTANCE.mapearParaAlunoDto(aluno);
    assertNotNull(aluno1, "Aluno não pode ser nulo");
    assertEquals("Karine", aluno1.getNome());
    assertEquals("Ferreira", aluno1.getSobrenome());
    assertEquals(aluno.getGenero(), aluno1.getGenero());
    assertEquals(aluno.getDataNascimento(), aluno1.getDataNascimento());
  }

  @Test
  void mapearParaAlunoDtoPage() {
    PageRequest pagina = PageRequest.of(0, 2);
    Aluno aluno1 = new Aluno(1L, "Jose William", Genero.MASCULINO, LocalDate.now().minusYears(4L));

    List<Aluno> alunoList = List.of(aluno, aluno1);
    Page<Aluno> alunos = new PageImpl<>(alunoList, pagina, alunoList.size());
    Page<AlunoDto> dtoPage = AlunoMapper.INSTANCE.mapearParaAlunoDtoPage(alunos);

    assertNotNull(dtoPage);
    assertEquals(alunos.getTotalElements(), dtoPage.getTotalElements());
    assertEquals(alunos.getTotalPages(), dtoPage.getTotalPages());
    assertEquals(alunos.getSize(), dtoPage.getSize());

    AlunoDto alunoDto1 = dtoPage.getContent().get(0);
    AlunoDto alunoDto2 = dtoPage.getContent().get(1);

    assertEquals("Jose", alunoDto2.getNome());
    assertEquals("William", alunoDto2.getSobrenome());
    assertEquals("Karine", alunoDto1.getNome());
    assertEquals("Ferreira", alunoDto1.getSobrenome());
  }

  @Test
  void merge() {
    AlunoMapper.INSTANCE.merge(alunoService, alunoTarget);

    assertEquals(1L, alunoTarget.getId());
    assertEquals("Jose Ferreira", alunoTarget.getNomeCompleto());
    assertEquals(Genero.MASCULINO, alunoTarget.getGenero());
    assertEquals(LocalDate.now().minusYears(4L), alunoTarget.getDataNascimento());
  }
}
