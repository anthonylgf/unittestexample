package com.example.unittestexample.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.exceptions.*;
import com.example.unittestexample.mappers.TurmaMapper;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.services.TurmaService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({TurmaController.class, GlobalExceptionHandler.class})
public class TurmaControllerTest {

  @MockitoBean TurmaService turmaService;

  @Autowired MockMvc testClient;

  @MockitoBean TurmaMapper turmaMapper;

  @Test
  public void cadastrarTurma_ComDadosValidos_RetornarTurmaComStatus201() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";
    Turma turmaRetornada =
        new Turma(
            1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaMapper.paraEntity(any())).thenReturn(new Turma());
    when(turmaService.salvar(any(Turma.class))).thenReturn(turmaRetornada);

    testClient
        .perform(post("/turmas").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isCreated());

    Assertions.assertEquals("TURMA-A1", turmaRetornada.getNome());
    Assertions.assertEquals(LocalTime.of(19, 0), turmaRetornada.getHorarioInicio());
    Assertions.assertEquals(LocalTime.of(21, 0), turmaRetornada.getHorarioFim());
    Assertions.assertEquals(30, turmaRetornada.getLimiteTurma());
  }

  @Test
  public void cadastrarTurma_ComDadosInvalidos_RetornarTurmaComStatus400() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA_A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    testClient
        .perform(post("/turmas").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void cadastrarTurma_ComDadosJaExistentes_RetornarTurmaComStatus409() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    when(turmaMapper.paraEntity(any())).thenReturn(new Turma());
    when(turmaService.salvar(any(Turma.class)))
        .thenThrow(new TurmaJaExistenteException("TURMA-A1"));

    testClient
        .perform(post("/turmas").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isConflict());
  }

  @Test
  public void cadastrarTurma_ComDuracaoMaiorQuePermitida_RetornarStatus400() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"10:00\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    when(turmaMapper.paraEntity(any())).thenReturn(new Turma());
    when(turmaService.salvar(any(Turma.class))).thenThrow(new DuracaoMaiorQuePermitidoException(6));

    testClient
        .perform(post("/turmas").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void cadastrarTurma_ComFimAntesDoInicio_RetornarStatus400() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"16:00\","
            + "\"horarioFim\": \"11:00\","
            + "\"limiteTurma\": 2"
            + "}";

    LocalTime inicio = LocalTime.of(16, 0);
    LocalTime fim = LocalTime.of(11, 0);
    when(turmaMapper.paraEntity(any())).thenReturn(new Turma());
    when(turmaService.salvar(any(Turma.class)))
        .thenThrow(new FimAntesDoInicioException(inicio, fim));

    testClient
        .perform(post("/turmas").contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void listarTurmas_ComDadosValidos_RetornarTurmasStatus201() throws Exception {
    Integer pagina = 0;
    Integer limite = 10;

    TurmaResumoDto resumoDto =
        new TurmaResumoDto(1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30);

    Page<TurmaResumoDto> pageTurma = new PageImpl<>(List.of(resumoDto));

    TurmaDto turmaDto = new TurmaDto();
    turmaDto.setId(1L);

    when(turmaService.listarTurmas(anyInt(), anyInt())).thenReturn(pageTurma);

    testClient
        .perform(
            get("/turmas").param("pagina", pagina.toString()).param("limite", limite.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", is(1)));

    verify(turmaService).listarTurmas(eq(pagina), eq(limite));
  }

  @Test
  public void listarTurmas_ComDadosInvalidos_RetornarTurmasStatus400() throws Exception {
    Integer pagina = 99;
    Integer limite = 99;

    TurmaResumoDto resumoDto =
        new TurmaResumoDto(1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30);

    Page<TurmaResumoDto> pageTurma = new PageImpl<>(List.of(resumoDto));

    TurmaDto turmaDto = new TurmaDto();
    turmaDto.setId(1L);

    when(turmaService.listarTurmas(anyInt(), anyInt()))
        .thenThrow(ParametrosListagemInvalidosException.class);

    testClient
        .perform(
            get("/turmas").param("pagina", pagina.toString()).param("limite", limite.toString()))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void buscarTurma_ComDadosValidos_RetornarTurmasStatus201() throws Exception {
    Turma turmaRetornada =
        new Turma(
            1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    when(turmaService.buscarPorId(any(Long.class))).thenReturn(turmaRetornada);

    testClient.perform(get("/turmas/{id}", turmaRetornada.getId())).andExpect(status().isOk());
  }

  @Test
  public void buscarTurma_ComIdInvalido_RetornarTurmasStatus404() throws Exception {

    Long turmaRetornada = 99L;

    when(turmaService.buscarTurmaDetalhada(eq(turmaRetornada)))
        .thenThrow(TurmaNaoEncontradaException.class);
    testClient.perform(get("/turmas/{id}", turmaRetornada)).andExpect(status().isNotFound());
  }

  @Test
  public void deletarTurma_ComDadosValidos_RetornarTurmasStatus201() throws Exception {
    Turma turmaRetornada =
        new Turma(
            1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    doNothing().when(turmaService).deletar(any(Long.class));

    testClient
        .perform(delete("/turmas/{id}", turmaRetornada.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  public void deletarTurma_PossuindoAlunosNaTurma_RetornarStatus409() throws Exception {
    Turma turmaRetornada =
        new Turma(
            1L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    doThrow(new TurmaPossuiAlunosException(turmaRetornada.getNome(), 2))
        .when(turmaService)
        .deletar(turmaRetornada.getId());
    testClient
        .perform(delete("/turmas/{id}", turmaRetornada.getId()))
        .andExpect(status().isConflict());
  }

  @Test
  public void deletarTurma_TurmaNaoEncontrado_RetornarStatus400() throws Exception {
    Turma turmaRetornada =
        new Turma(
            99L, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    doThrow(new TurmaNaoEncontradaException(turmaRetornada.getId()))
        .when(turmaService)
        .deletar(turmaRetornada.getId());
    testClient
        .perform(delete("/turmas/{id}", turmaRetornada.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void trasferirAlunoDeTurma_DadosValidos_RetornarStatus200() throws Exception {

    Turma turmaNova =
        new Turma(
            2L, "TURMA-A2", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30, new ArrayList<>());

    Long alunoId = 1L;

    AlunoDto alunoTransferido =
        new AlunoDto(
            alunoId,
            "Karine",
            "Ferreira",
            Genero.FEMININO,
            LocalDate.now().minusYears(4L),
            turmaNova.getId());

    when(turmaService.transferirAluno(eq(alunoId), eq(turmaNova.getId())))
        .thenReturn(alunoTransferido);

    testClient
        .perform(
            patch("/turmas/{alunoId}/transferir/{idNovaTurma}", alunoId, turmaNova.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(alunoId))
        .andExpect(jsonPath("$.turmaId").value(2));
  }

  @Test
  public void trasferirAlunoDeTurma_TurmaNovaLotada_RetornarStatus409() throws Exception {

    Turma turmaNova =
        new Turma(
            2L, "TURMA-A2", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 1, new ArrayList<>());

    Long alunoId = 1L;

    when(turmaService.transferirAluno(eq(alunoId), eq(turmaNova.getId())))
        .thenThrow(TurmaLotadaException.class);

    testClient
        .perform(
            patch("/turmas/{alunoId}/transferir/{idNovaTurma}", alunoId, turmaNova.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  public void alterarTurma_ComDadosValidos_RetornarStatus200() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    Long id = 1L;

    TurmaResumoDto resumoDto =
        new TurmaResumoDto(id, "TURMA-A1", LocalTime.of(19, 0), LocalTime.of(21, 0), 2, 30);

    when(turmaService.alterar(eq(id), any(TurmaDto.class))).thenReturn(resumoDto);

    testClient
        .perform(
            put("/turmas/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isOk());
  }

  @Test
  public void alterarTurma_ComIdInvalido_RetornarStatus404() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA-A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    Long id = 99L;

    when(turmaService.alterar(eq(id), any(TurmaDto.class)))
        .thenThrow(TurmaNaoEncontradaException.class);

    testClient
        .perform(
            put("/turmas/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isNotFound());
  }

  @Test
  public void alterarTurma_ComDadosInvalidos_RetornarStatus400() throws Exception {
    String jsonDeEntrada =
        "{"
            + "\"nome\": \"TURMA_A1\","
            + "\"horarioInicio\": \"13:30\","
            + "\"horarioFim\": \"16:00\","
            + "\"limiteTurma\": 2"
            + "}";

    Long id = 99L;

    when(turmaService.alterar(eq(id), any(TurmaDto.class)))
        .thenThrow(TurmaNaoEncontradaException.class);

    testClient
        .perform(
            put("/turmas/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonDeEntrada))
        .andExpect(status().isBadRequest());
  }
}
