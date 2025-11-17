package com.example.unittestexample.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.unittestexample.configs.ApplicationProperties;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.exceptions.AlunoExisteMesmoNomeException;
import com.example.unittestexample.exceptions.AlunoNaoEncontradoException;
import com.example.unittestexample.exceptions.IdadeInvalidaException;
import com.example.unittestexample.exceptions.ParametrosListagemInvalidosException;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.utils.DateUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

  @InjectMocks private AlunoService alunoService;

  @Mock private AlunoRepository alunoRepository;
  @Mock private ApplicationProperties applicationProperties;
  @Mock private DateUtils dateUtils;

  @Test
  void salvar_ComAlunoValido_RetornarAlunoSalvo() {
    Aluno aluno = new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    aluno.setId(1L);
    when(applicationProperties.getMaximoIdade()).thenReturn(10);
    when(applicationProperties.getMinimoIdade()).thenReturn(2);

    when(dateUtils.diferencaEmAnosDataAtual(any(LocalDate.class))).thenReturn(4);
    when(alunoRepository.findByNomeCompleto(aluno.getNomeCompleto())).thenReturn(Optional.empty());
    when(alunoRepository.save(aluno)).thenReturn(aluno);

    Aluno alunoSalvo = alunoService.salvar(aluno);

    Assertions.assertNotNull(alunoSalvo);
    Assertions.assertEquals(aluno.getId(), alunoSalvo.getId());
    verify(alunoRepository, times(1)).save(aluno);
  }

  @Test
  void salvar_ComIdadeMenorQueMinima_RetornarIdadeInvalidaException() {
    Aluno aluno = new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(applicationProperties.getMaximoIdade()).thenReturn(10);
    when(applicationProperties.getMinimoIdade()).thenReturn(2);

    when(dateUtils.diferencaEmAnosDataAtual(any(LocalDate.class))).thenReturn(1);

    assertThrows(IdadeInvalidaException.class, () -> alunoService.salvar(aluno));

    verify(alunoRepository, never()).save(aluno);
  }

  @Test
  void salvar_ComIdadeMaiorQueMaxima_RetornarIdadeInvalidaException() {
    Aluno aluno = new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(applicationProperties.getMaximoIdade()).thenReturn(10);
    when(applicationProperties.getMinimoIdade()).thenReturn(2);

    when(dateUtils.diferencaEmAnosDataAtual(any(LocalDate.class))).thenReturn(11);

    assertThrows(IdadeInvalidaException.class, () -> alunoService.salvar(aluno));

    verify(alunoRepository, never()).save(aluno);
  }

  @Test
  void salvar_ComNomeRepetido_RetornarAlunoExisteMesmoNomeException() {
    Aluno aluno = new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(applicationProperties.getMaximoIdade()).thenReturn(10);
    when(applicationProperties.getMinimoIdade()).thenReturn(2);

    when(dateUtils.diferencaEmAnosDataAtual(any(LocalDate.class))).thenReturn(4);
    when(alunoRepository.findByNomeCompleto(aluno.getNomeCompleto()))
        .thenReturn(Optional.of(aluno));

    assertThrows(AlunoExisteMesmoNomeException.class, () -> alunoService.salvar(aluno));

    verify(alunoRepository, never()).save(aluno);
  }

  @Test
  void buscarPorId_ComIdExistente_RetornarAluno() {
    Aluno aluno = new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

    Aluno alunoId = alunoService.buscarPorId(1L);

    Assertions.assertNotNull(alunoId);
    Assertions.assertEquals(aluno, alunoId);
  }

  @Test
  void buscarPorId_ComIdInexistente_RetornarAlunoNaoEncontradoException() {
    when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.buscarPorId(99L));
  }

  @Test
  void atualizarAluno_ComIdExistente_RetornarAlunoAtualizado() {

    Aluno alunoAtualizado =
        new Aluno(1L, "Karine Araujo", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(alunoRepository.findById(1L)).thenReturn(Optional.of(alunoAtualizado));

    alunoService.atualizarAluno(1L, alunoAtualizado);

    assertEquals("Karine Araujo", alunoAtualizado.getNomeCompleto());
    verify(alunoRepository, times(1)).save(alunoAtualizado);
  }

  @Test
  void atualizarAluno_ComIdInexistente_RetornarAlunoNaoEncontradoException() {
    Aluno alunoAtualizado =
        new Aluno(99L, "Karine Araujo", Genero.FEMININO, LocalDate.now().minusYears(4L));
    when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
        AlunoNaoEncontradoException.class, () -> alunoService.atualizarAluno(99L, alunoAtualizado));

    verify(alunoRepository, never()).save(alunoAtualizado);
  }

  @Test
  void deletarAluno_ComIdExistente_SemRetorno() {
    Aluno aluno =
        new Aluno(null, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    Long alunoId = 1L;
    when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

    alunoService.deletarAluno(alunoId);

    verify(alunoRepository, times(1)).delete(aluno);
  }

  @Test
  void deletarAluno_ComIdInexistente_RetornarAlunoNaoEncontradoException() {
    Aluno aluno =
        new Aluno(null, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));
    Long alunoId = 99L;
    when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());

    assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.deletarAluno(alunoId));

    verify(alunoRepository, never()).delete(aluno);
  }

  @Test
  void listarAlunos_ComIdadeInvalida_RetornarParametrosListagemInvalidosException() {
    Integer pagina = 0;
    Integer limite = 10;
    AlunoFilters alunoFilters = mock(AlunoFilters.class);

    Mockito.lenient().when(applicationProperties.getMaximoIdade()).thenReturn(10);
    Mockito.lenient().when(applicationProperties.getMinimoIdade()).thenReturn(2);

    Mockito.lenient().when(dateUtils.diferencaEmAnosDataAtual(any(LocalDate.class))).thenReturn(1);

    assertThrows(
        ParametrosListagemInvalidosException.class,
        () -> alunoService.listarAlunos(alunoFilters, pagina, limite));

    verify(alunoRepository, never()).findAll();
  }

  @Test
  void listarAlunos_ComPaginacaoNulla_RetornarNullPointerException() {
    Integer pagina = null;
    Integer limite = 10;
    AlunoFilters alunoFilters = mock(AlunoFilters.class);

    when(alunoFilters.getIdadeMinima()).thenReturn(2);
    when(alunoFilters.getIdadeMaxima()).thenReturn(10);

    assertThrows(
        NullPointerException.class, () -> alunoService.listarAlunos(alunoFilters, pagina, limite));

    verify(alunoRepository, never()).findAll();
  }

  @Test
  void listarAlunos_ComNomeCompleto_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);
    String nome = "Karine Ferreira";

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getNomeCompleto()).thenReturn(nome);

    when(alunoFilters.getGenero()).thenReturn(null);
    when(alunoFilters.getIdadeMinima()).thenReturn(null);
    when(alunoFilters.getIdadeMaxima()).thenReturn(null);

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }

  @Test
  void listarAlunos_ComIdadeMinima_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);
    LocalDate data = LocalDate.now().minusYears(4L);

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getNomeCompleto()).thenReturn(null);
    when(alunoFilters.getGenero()).thenReturn(null);
    when(alunoFilters.getIdadeMinima()).thenReturn(2);
    when(alunoFilters.getIdadeMaxima()).thenReturn(null);

    when(dateUtils.recuperarDataEmAnos(2)).thenReturn(data);

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(dateUtils, times(1)).recuperarDataEmAnos(2);
    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }

  @Test
  void listarAlunos_ComIdadeMaxima_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);
    LocalDate data = LocalDate.now().minusYears(4L);

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getNomeCompleto()).thenReturn(null);
    when(alunoFilters.getGenero()).thenReturn(null);
    when(alunoFilters.getIdadeMinima()).thenReturn(null);
    when(alunoFilters.getIdadeMaxima()).thenReturn(10);

    when(dateUtils.recuperarDataEmAnos(10)).thenReturn(data);

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(dateUtils, times(1)).recuperarDataEmAnos(10);
    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }

  @Test
  void listarAlunos_ComGenero_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);
    Genero genero = Genero.FEMININO;

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getNomeCompleto()).thenReturn(null);
    when(alunoFilters.getGenero()).thenReturn(genero);
    when(alunoFilters.getIdadeMinima()).thenReturn(null);
    when(alunoFilters.getIdadeMaxima()).thenReturn(null);

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }

  @Test
  void listarAlunos_ComTodosOsFiltros_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);
    Genero genero = Genero.FEMININO;
    LocalDate dataMaxima = LocalDate.of(2006, 6, 18);
    LocalDate dataMinima = LocalDate.of(2020, 6, 18);
    String nome = "Karine Ferreira";

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getNomeCompleto()).thenReturn(nome);
    when(alunoFilters.getGenero()).thenReturn(genero);
    when(alunoFilters.getIdadeMinima()).thenReturn(10);
    when(alunoFilters.getIdadeMaxima()).thenReturn(30);

    when(dateUtils.recuperarDataEmAnos(10)).thenReturn(dataMinima);
    when(dateUtils.recuperarDataEmAnos(30)).thenReturn(dataMaxima);

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(dateUtils, times(1)).recuperarDataEmAnos(10);
    verify(dateUtils, times(1)).recuperarDataEmAnos(30);
    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }

  @Test
  void listarAlunos_ComNenhumFiltro_RetonarSpecificationCorreta() {
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno1 = mock(Aluno.class);
    Aluno aluno2 = mock(Aluno.class);
    List<Aluno> listaAlunos = Arrays.asList(aluno1, aluno2);
    AlunoFilters alunoFilters = mock(AlunoFilters.class);

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    Page<Aluno> pageMock = new PageImpl<>(listaAlunos, pageableEsperado, listaAlunos.size());

    when(alunoFilters.getIdadeMinima()).thenReturn(2);
    when(alunoFilters.getIdadeMaxima()).thenReturn(10);

    when(dateUtils.recuperarDataEmAnos(2)).thenReturn(LocalDate.of(2023, 1, 1));
    when(dateUtils.recuperarDataEmAnos(10)).thenReturn(LocalDate.of(2015, 1, 1));

    when(alunoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageMock);

    alunoService.listarAlunos(alunoFilters, pagina, limite);

    verify(dateUtils, times(1)).recuperarDataEmAnos(2);
    verify(dateUtils, times(1)).recuperarDataEmAnos(10);
    verify(alunoRepository, times(1)).findAll(any(Specification.class), eq(pageableEsperado));
  }
}
