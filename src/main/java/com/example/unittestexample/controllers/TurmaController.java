package com.example.unittestexample.controllers;

import com.example.unittestexample.dtos.TurmaDetalhesDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.mappers.TurmaMapper;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.services.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/turmas")
public class TurmaController {

  private final TurmaService service;
  private final TurmaMapper mapper;

  @PostMapping
  public ResponseEntity<TurmaResumoDto> cadastrarTurma(@Valid @RequestBody TurmaDto dto) {
    Turma turma = mapper.paraEntity(dto);
    Turma turmaSalva = service.salvar(turma);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.paraResumoDto(turmaSalva));
  }

  @GetMapping
  public ResponseEntity<Page<TurmaResumoDto>> listarTurmas(
      @RequestParam(defaultValue = "0") Integer pagina,
      @RequestParam(defaultValue = "10") Integer limite) {
    return ResponseEntity.ok(service.listarTurmas(pagina, limite));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TurmaDetalhesDto> buscarTurma(@PathVariable Long id) {
    return ResponseEntity.ok(service.buscarTurmaDetalhada(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
    service.deletar(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("{id}")
  public ResponseEntity<TurmaResumoDto> alterarTurma(
      @PathVariable Long id, @Valid @RequestBody TurmaDto dto) {
    return ResponseEntity.ok(service.alterar(id, dto));
  }
}
