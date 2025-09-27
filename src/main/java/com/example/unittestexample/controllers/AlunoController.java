package com.example.unittestexample.controllers;

import com.example.unittestexample.annotations.Create;
import com.example.unittestexample.annotations.Update;
import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.services.AlunoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/alunos")
public class AlunoController {

  private final AlunoService alunoService;

  @PostMapping
  public ResponseEntity<AlunoDto> cadastrarAluno(
      @Validated(Create.class) @RequestBody AlunoDto alunoDto) {
    log.info("Cadastrando aluno: {}", alunoDto);
    var alunoSalvo = alunoService.salvar(AlunoMapper.INSTANCE.mapearParaAluno(alunoDto));
    var alunoSalvoDto = AlunoMapper.INSTANCE.mapearParaAlunoDto(alunoSalvo);
    return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvoDto);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> atualizarAluno(
      @PathVariable Long id, @Validated(Update.class) @RequestBody AlunoDto alunoDto) {
    log.info("Atualizando aluno de id {}, com os valores: {}", id, alunoDto);
    alunoService.atualizarAluno(id, AlunoMapper.INSTANCE.mapearParaAluno(alunoDto));
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
    log.info("Deletando aluno de id {}", id);
    alunoService.deletarAluno(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<AlunoDto> buscarPorId(@PathVariable Long id) {
    log.info("Buscando aluno de id {}", id);
    var aluno = alunoService.buscarPorId(id);
    var alunoDto = AlunoMapper.INSTANCE.mapearParaAlunoDto(aluno);
    return ResponseEntity.ok(alunoDto);
  }

  @GetMapping
  public ResponseEntity<Page<AlunoDto>> listarAlunos(
      @Valid AlunoFilters filters,
      @RequestParam @PositiveOrZero Integer pagina,
      @RequestParam @Positive Integer limite) {
    log.info("Listando alunos com filtros: {}, pagina: {}, limite: {}", filters, pagina, limite);
    var alunos = alunoService.listarAlunos(filters, pagina, limite);
    var alunosDto = AlunoMapper.INSTANCE.mapearParaAlunoDtoPage(alunos);
    return ResponseEntity.ok(alunosDto);
  }
}
