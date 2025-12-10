package com.example.unittestexample.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.exceptions.AlunoExisteMesmoNomeException;
import com.example.unittestexample.exceptions.AlunoNaoEncontradoException;
import com.example.unittestexample.exceptions.ParametrosListagemInvalidosException;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.services.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlunoController.class)
class AlunoControllerTest {

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean AlunoRepository repository;

  @MockitoBean AlunoService service;

  @MockitoBean AlunoMapper alunoMapper;

  @Autowired MockMvc testClient;

  @Captor private ArgumentCaptor<Aluno> alunoCaptor;

  @BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  private final Aluno aluno =
      new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));

  @Test
  public void criarAluno_ComDadosValidos_RetornarAlunoComStatus201() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"31-10-2021\"" // Formato correto!
            + "}";

    Aluno alunoEsperado = new Aluno();
    alunoEsperado.setId(1L);
    alunoEsperado.setNomeCompleto("KARINE FERREIRA");
    alunoEsperado.setGenero(Genero.FEMININO);
    alunoEsperado.setDataNascimento(LocalDate.of(2021, 10, 31));

    when(service.salvar(any(Aluno.class))).thenReturn(alunoEsperado); // Quando

    testClient
        .perform(
            post("/alunos") // simula uma requisição post
                .contentType(
                    MediaType
                        .APPLICATION_JSON) // .Define o cabeçalho Content-Type da requisição enviada
                // ao controller.
                .content(
                    jsonDeEntrada) // Define o corpo (body) da requisição HTTP que será enviada.
            )
        .andExpect(status().isCreated()); // Verifica se o status HTTP da resposta foi o esperado.
    Assertions.assertEquals("KARINE FERREIRA", alunoEsperado.getNomeCompleto());
    Assertions.assertEquals(LocalDate.of(2021, 10, 31), alunoEsperado.getDataNascimento());
    verify(service)
        .salvar(
            alunoCaptor
                .capture()); // Confirma que o metodo salvar() no service foi chamado exatamente //
    // uma vez durante a execução da requisição HTTP
  }

  @Test
  public void criarAluno_dataNascimentoInvalida_RetornarErroMessage400() throws Exception {

    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"18-06-2026\""
            + "}";

    testClient
        .perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
    System.out.println("Data de nascimento inválida.");
    verify(service, never()).salvar(any(Aluno.class));
  }

  @Test
  public void criarAluno_ComDadosJaExistentes_RetornarErroMessage409() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"31-10-2021\""
            + "}";
    when(service.salvar(any(Aluno.class))).thenThrow(AlunoExisteMesmoNomeException.class);

    testClient
        .perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isConflict());
    System.out.println("Aluno com estes dados já existe.");
    verify(service).salvar(any(Aluno.class));
  }

  @Test
  public void alterarAluno_ComIdExistentes_RetornarStatus204() throws Exception {
    Long aluno_id = 1L;

    String jsonDeEntrada = "{" + "\"nome\": \"JOSE\"," + "\"sobrenome\": \"WILLIAM\"" + "}";

    doNothing().when(service).atualizarAluno(eq(aluno_id), any(Aluno.class));

    testClient
        .perform(
            patch("/alunos/{id}", aluno_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDeEntrada))
        .andExpect(status().isNoContent());
    verify(service).atualizarAluno(eq(aluno_id), alunoCaptor.capture());

    Aluno alunoAtualizado = alunoCaptor.getValue();

    Assertions.assertEquals("JOSE WILLIAM", alunoAtualizado.getNomeCompleto());
  }

  @Test
  public void atualizarAluno_ComIdInexistentes_RetornarErroMessage404() throws Exception {
    Long aluno_id = 99L;

    String jsonDeEntrada = "{" + "\"nome\": \"JOSE\"," + "\"sobrenome\": \"WILLIAM\"" + "}";

    doThrow(new AlunoNaoEncontradoException(aluno_id))
        .when(service)
        .atualizarAluno(eq(aluno_id), any(Aluno.class));

    testClient
        .perform(
            patch("/alunos/{id}", aluno_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDeEntrada))
        .andExpect(status().isNotFound());
    System.out.println("Id não encontrado");
    verify(service).atualizarAluno(eq(aluno_id), any(Aluno.class)); // eq
  }

  @Test
  public void atualizarAluno_ComNomeInvalido_RetornarErroMessage400() throws Exception {
    Long aluno_id = 1L;

    String jsonDeEntrada = "{" + "\"nome\": \"Jo5e\"," + "\"sobrenome\": \"William!\"" + "}";

    testClient
        .perform(
            patch("/alunos/{id}", aluno_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
    System.out.println("Caracteres inválidos no nome do aluno.");
  }

  @Test
  public void deletarAluno_RetornarStatus204() throws Exception {
    Long id = 1L;
    doNothing().when(service).deletarAluno(any(Long.class));

    testClient.perform(delete("/alunos/{id}", id)).andExpect(status().isNoContent());
    verify(service).deletarAluno(id);
  }

  @Test
  public void deletarAluno_ComIdInexistente_RetornarErroMessage404() throws Exception {
    Long id = 99L;
    doThrow(new AlunoNaoEncontradoException(id)).when(service).deletarAluno(any(Long.class));

    testClient.perform(delete("/alunos/{id}", id)).andExpect(status().isNotFound());
    verify(service).deletarAluno(id);
  }

  @Test
  public void buscarAluno_ComIdInexistente_RetornarErroMessage404() throws Exception {
    Long id = 99L;

    when(service.buscarPorId(any(Long.class))).thenThrow(new AlunoNaoEncontradoException(id));

    testClient.perform(get("/alunos/{id}", id)).andExpect(status().isNotFound());
    System.out.println("Id não encontrado");
    verify(service).buscarPorId(id);
  }

  @Test
  public void buscarAluno_ComIdExistente_RetornarAlunoComStatus200() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"31-10-2021\""
            + "}";

    Long id = 1L;
    when(service.buscarPorId(any(Long.class))).thenReturn(aluno);

    testClient.perform(get("/alunos/{id}", id)).andExpect(status().isOk());
    verify(service).buscarPorId(id);
  }

  @Test
  public void buscarTodosOsAlunos_ComFiltrosValidos_RetornarListaComAlunosComStatus200()
      throws Exception {
    Integer pagina = 0;
    Integer limite = 10;

    Aluno alunoComId =
        new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));

    Page<Aluno> pageAluno;
    pageAluno = new PageImpl<>(List.of(alunoComId));

    AlunoDto alunoDtoMock = new AlunoDto();
    alunoDtoMock.setId(1L);

    Page<AlunoDto> alunosDtoPageMock = new PageImpl<>(List.of(alunoDtoMock));

    when(service.listarAlunos(any(AlunoFilters.class), eq(pagina), eq(limite)))
        .thenReturn(pageAluno);

    when(alunoMapper.mapearParaAlunoDtoPage(any())).thenReturn(alunosDtoPageMock);

    testClient
        .perform(
            get("/alunos")
                .param("pagina", pagina.toString())
                .param("limite", limite.toString())
                .param("nome", "Jose"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", is(1)));

    verify(service).listarAlunos(any(AlunoFilters.class), eq(pagina), eq(limite));
  }

  @Test
  public void buscarTodosOsAlunos_ComParametrosListagemInvalidos_RetornarErro400()
      throws Exception {
    Integer pagina = 0;
    Integer limite = 10;

    when(service.listarAlunos(any(AlunoFilters.class), eq(pagina), eq(limite)))
        .thenThrow(ParametrosListagemInvalidosException.class);

    testClient
        .perform(
            get("/alunos")
                .param("pagina", pagina.toString())
                .param("limite", limite.toString())
                .param("nome", "Jose"))
        .andExpect(status().isBadRequest());
    System.out.println("Parametros de listagem invalidos");

    verify(service).listarAlunos(any(AlunoFilters.class), eq(pagina), eq(limite));
  }
}
