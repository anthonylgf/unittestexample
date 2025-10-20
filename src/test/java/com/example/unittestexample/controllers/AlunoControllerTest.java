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
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.services.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

  @Mock AlunoRepository repository;

  @MockitoBean AlunoService service;

  @MockitoBean AlunoMapper alunoMapper;

  @Autowired MockMvc testClient;

  @org.junit.jupiter.api.BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  private Aluno aluno = new Aluno("Karine Ferreira", Genero.FEMININO, LocalDate.of(2006, 6, 18));

  @Test
  public void criarAluno_ComDadosValidos_RetornarAlunoComStatus204() throws Exception {
    when(service.salvar(any(Aluno.class))).thenReturn(aluno); // Quando

    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"18-06-2006\"" // Formato correto!
            + "}";

    testClient
        .perform(
            post("/alunos") // simula uma requisição post
                .contentType(
                    MediaType
                        .APPLICATION_JSON) // .Define o cabeçalho Content-Type da requisição enviada
                // ao controller.
                .content(jsonDeEntrada) // Define o corpo (body) da requisição HTTP que será
            // enviada.25250nnbbbb ,,,,4
            )
        .andExpect(status().isCreated()); // Verifica se o status HTTP da resposta foi o esperado.
    verify(service)
        .salvar(
            any(Aluno.class)); // Confirma que o metodo salvar() no service foi chamado exatamente
    // uma vez durante a execução da requisição HTTP
  }

  @Test
  public void criarAluno_ComDadosInvalidos_RetornarErroMessage400() throws Exception {

    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"18-06-2026\"" // Formato correto!
            + "}";

    testClient
        .perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
    verify(service, never()).salvar(any(Aluno.class));
  }

  @Test
  public void criarAluno_ComDadosJaExistentes_RetornarErroMessage409() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"18-06-2006\"" // Formato correto!
            + "}";
    when(service.salvar(any(Aluno.class))).thenThrow(AlunoExisteMesmoNomeException.class);

    testClient
        .perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isConflict());
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
    verify(service).atualizarAluno(eq(aluno_id), any(Aluno.class));
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
    verify(service).atualizarAluno(eq(aluno_id), any(Aluno.class));
  }

  @Test
  public void atualizarAluno_ComDadosInvalidos_RetornarErroMessage400() throws Exception {
    Long aluno_id = 1L;

    String jsonDeEntrada = "{" + "\"nome\": \"Jo5e\"," + "\"sobrenome\": \"William!\"" + "}";

    testClient
        .perform(
            patch("/alunos/{id}", aluno_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
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
    verify(service).buscarPorId(id);
  }

  @Test
  public void buscarAluno_ComIdExistente_RetornarAlunoComStatus200()
      throws Exception { // Pode melhorar fazendo verificar se o JSON retornado está correto e se o
    // mapeamento para o DTO está funcionando conforme esperado, mas teria que
    // arrumar a classe AlunoDto
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"18-06-2006\""
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
    Long aluno_id = 1L;

    Aluno alunoComId = new Aluno("Karine Ferreira", Genero.FEMININO, LocalDate.of(2006, 6, 18));
    alunoComId.setId(aluno_id);

    Page<Aluno> pageAluno;
    pageAluno = new PageImpl<>(List.of(alunoComId));

    AlunoDto alunoDtoMock = new AlunoDto();
    alunoDtoMock.setId(aluno_id);

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
  public void buscarTodosOsAlunos_ComFiltrosInvalidos_RetornarErro400() throws Exception {
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

    verify(service).listarAlunos(any(AlunoFilters.class), eq(pagina), eq(limite));
  }
}
