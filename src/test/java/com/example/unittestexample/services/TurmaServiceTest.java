package com.example.unittestexample.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.exceptions.*;
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.mappers.TurmaMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.repositories.TurmaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class TurmaServiceTest {

  @Mock private TurmaRepository turmaRepository;
  @Mock private AlunoRepository alunoRepository;
  @Mock private AlunoMapper alunoMapper;
  @Mock private TurmaMapper turmaMapper;

  @InjectMocks private TurmaService turmaService;

  @Test
  void salvarTurma_ComDadosValidos_RetornarTurmaSalva() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaRepository.findByNome("TURMA_A1")).thenReturn(Optional.empty());
    lenient().when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

    Turma resultado = turmaService.salvar(turma);

    Assertions.assertNotNull(resultado);
    Assertions.assertEquals("TURMA_A1", resultado.getNome());
    verify(turmaRepository, times(1)).save(any(Turma.class));
    verify(turmaRepository, times(1)).findByNome("TURMA_A1");
  }

  @Test
  void salvarTurma_ComDadoJaExistentes_TurmaJaExistenteException() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaRepository.findByNome("TURMA_A1")).thenThrow(TurmaJaExistenteException.class);

    Assertions.assertThrows(TurmaJaExistenteException.class, () -> turmaService.salvar(turma));

    verify(turmaRepository, never()).save(any(Turma.class));
  }

  @Test
  void salvarTurma_ComHorasInvalidas_FimAntesDoInicioException() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaRepository.save(turma)).thenThrow(FimAntesDoInicioException.class);

    Assertions.assertThrows(FimAntesDoInicioException.class, () -> turmaService.salvar(turma));
  }

  @Test
  void salvarTurma_ComDuracaoInvalida_DuracaoMaiorQuePermitidoException() {
    Turma turma =
        new Turma(
            1L, "TURMA_A1", LocalTime.of(10, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaRepository.save(turma)).thenThrow(DuracaoMaiorQuePermitidoException.class);

    Assertions.assertThrows(
        DuracaoMaiorQuePermitidoException.class, () -> turmaService.salvar(turma));
  }

  @Test
  void listarTurmas_ComDadosValidos_RetornarTurmas() {
    Integer pagina = 0;
    Integer limite = 10;
    Pageable pageable = PageRequest.of(pagina, limite, Sort.by("nome").ascending());

    Turma turma1 = new Turma();
    turma1.setId(1L);
    turma1.setNome("Turma-A");
    Turma turma2 = new Turma();
    turma2.setId(2L);
    turma2.setNome("Turma-B");
    Page<Turma> pageSimulada = new PageImpl<>(Arrays.asList(turma1, turma2));

    when(turmaRepository.findAll(pageable)).thenReturn(pageSimulada);

    Page<TurmaResumoDto> resultado = turmaService.listarTurmas(pagina, limite);

    Assertions.assertNotNull(resultado);
    Assertions.assertEquals(2, resultado.getContent().size());

    verify(turmaRepository, times(1)).findAll(pageable);
  }

  @Test
  void listarTurmas_ComParametroDeListagemInvalidos_RetornarParametrosListagemInvalidosException() {
    Integer pagina = -99;
    Integer limite = -99;

    Assertions.assertThrows(
        ParametrosListagemInvalidosException.class,
        () -> turmaService.listarTurmas(pagina, limite));

    verify(turmaRepository, never()).findAll(any(Pageable.class));
  }

  @Test
  void buscarTurmaPorId_ComIdValido_RetornarTurma() {
    Turma turma1 = new Turma();
    turma1.setId(1L);
    turma1.setNome("TURMA-A1");

    lenient().when(turmaRepository.findById(turma1.getId())).thenReturn(Optional.of(turma1));

    Turma turmaEsperada =
        new Turma(
            1L, "TURMA-A1", LocalTime.of(10, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    Assertions.assertNotNull(turma1);
    Assertions.assertEquals(turmaEsperada.getNome(), turma1.getNome());
    Assertions.assertEquals(turmaEsperada.getId(), turma1.getId());
  }

  @Test
  void buscarTurmaPorId_ComIdInvalido_RetornarTurmaNaoEncontradaException() {
    lenient().when(turmaRepository.findById(99L)).thenThrow(TurmaNaoEncontradaException.class);

    assertThrows(TurmaNaoEncontradaException.class, () -> turmaService.buscarPorId(99L));
  }

  @Test
  void deletarTurma_ComIdValido_SemRetorno() {
    Turma turma1 = new Turma();
    turma1.setId(1L);
    turma1.setNome("TURMA-A1");

    lenient().when(turmaRepository.findById(turma1.getId())).thenReturn(Optional.of(turma1));

    turmaService.deletar(turma1.getId());

    verify(turmaRepository, times(1)).delete(turma1);
  }

  @Test
  void deletarTurma_ComIdInvalido_RetornarTurmaNaoEncontradaException() {
    Long idInvalido = 99L;

    lenient().when(turmaRepository.findById(idInvalido)).thenReturn(Optional.empty());

    assertThrows(TurmaNaoEncontradaException.class, () -> turmaService.deletar(idInvalido));

    verify(turmaRepository, never()).delete(any(Turma.class));
  }

  @Test
  void deletarTurma_ComAlunosCadastrados_RetornarTurmaPossuiAlunosException() {
    Turma turma1 = new Turma();
    turma1.setId(1L);
    turma1.setNome("TURMA-A1");

    turma1.setAlunos(List.of(new Aluno()));

    lenient().when(turmaRepository.findById(turma1.getId())).thenReturn(Optional.of(turma1));

    assertThrows(TurmaPossuiAlunosException.class, () -> turmaService.deletar(turma1.getId()));

    verify(turmaRepository, never()).delete(any(Turma.class));
  }

  @Test
  void transferirAluno_ComOsIdsValidos_RetornarAluno() {
    Turma turmaNova = new Turma();
    turmaNova.setId(2L);
    turmaNova.setNome("TURMA-A1");
    turmaNova.setAlunos(new ArrayList<>());
    turmaNova.setLimiteTurma(10);

    Aluno aluno =
        new Aluno(3L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L), null);

    AlunoDto alunoDto =
        new AlunoDto(
            3L,
            "Karine",
            "Ferreira",
            Genero.FEMININO,
            LocalDate.now().minusYears(4L),
            turmaNova.getId());

    when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno));
    when(turmaRepository.findById(2L)).thenReturn(Optional.of(turmaNova));
    when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);
    when(alunoMapper.mapearParaAlunoDto(any(Aluno.class))).thenReturn(alunoDto);

    AlunoDto resultado = turmaService.transferirAluno(3L, 2L);

    Assertions.assertNotNull(resultado);
    assertEquals(aluno.getId(), resultado.getId());
    assertEquals(turmaNova, aluno.getTurma());

    verify(alunoRepository, times(1)).save(any(Aluno.class));
    verify(alunoRepository, times(1)).findById(3L);
    verify(alunoMapper, times(1)).mapearParaAlunoDto(any(Aluno.class));
  }

  @Test
  void transferirAluno_ComTurmaNovaLotada_RetornarTurmaLotadaException() {
    Turma turmaNova = new Turma();
    turmaNova.setId(2L);
    turmaNova.setNome("TURMA-A1");
    turmaNova.setAlunos(new ArrayList<>());
    turmaNova.setLimiteTurma(10);

    Aluno aluno =
        new Aluno(3L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L), null);

    when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno));
    when(turmaRepository.findById(2L)).thenThrow(TurmaLotadaException.class);
    assertThrows(
        TurmaLotadaException.class,
        () -> turmaService.transferirAluno(aluno.getId(), turmaNova.getId()));
  }

  @Test
  void alterarTurma_ComDadosValidos_RetornarTurmaAtualizada() {
    Long idExistente = 1L;

    Turma turmaEntity = new Turma();
    turmaEntity.setId(idExistente);
    turmaEntity.setNome("Nome Antigo");

    TurmaDto turmaDtoParaAtualizar = new TurmaDto();
    turmaDtoParaAtualizar.setId(idExistente);
    turmaDtoParaAtualizar.setNome("TURMA-A1");

    when(turmaRepository.findById(idExistente)).thenReturn(Optional.of(turmaEntity));
    when(turmaRepository.save(any(Turma.class))).thenReturn(turmaEntity);

    turmaService.alterar(idExistente, turmaDtoParaAtualizar);

    Assertions.assertEquals("TURMA-A1", turmaEntity.getNome());

    Assertions.assertEquals(1L, turmaEntity.getId());

    verify(turmaRepository, times(1)).save(any(Turma.class));
  }

  @Test
  void alterarTurma_ComIdInvalido_RetornarTurmaNaoEncontradaException() {
    Long idInexistente = 99L;

    TurmaDto turmaDto = new TurmaDto();
    turmaDto.setNome("TURMA-A1");

    when(turmaRepository.findById(idInexistente)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class,
        () -> {
          turmaService.alterar(idInexistente, turmaDto);
        });
    verify(turmaRepository, never()).save(any(Turma.class));
  }
}
