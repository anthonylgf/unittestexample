package com.example.unittestexample.services;

import com.example.unittestexample.dtos.TurmaDetalhesDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.exceptions.*;
import com.example.unittestexample.mappers.TurmaMapper;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.repositories.TurmaRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TurmaService {

  private static final int LIMITE_MAXIMO_DURACAO_HORAS = 5;
  private final TurmaRepository turmaRepository;
  private final TurmaMapper turmaMapper;

  @Transactional
  public Turma salvar(Turma turma) {

    turmaRepository
        .findByNome(turma.getNome())
        .ifPresent(
            t -> {
              throw new TurmaJaExistenteException(turma.getNome());
            });
    calcularDuracao(turma);

    Turma turmaSalva = turmaRepository.save(turma);

    return turmaSalva;
  }

  private void calcularDuracao(Turma turma) {
    if (turma.getHorarioInicio() != null && turma.getHorarioFim() != null) {
      if (turma.getHorarioFim().isBefore(turma.getHorarioInicio())) {
        throw new FimAntesDoInicioException(turma.getHorarioFim(), turma.getHorarioInicio());
      }

      long horas = Duration.between(turma.getHorarioInicio(), turma.getHorarioFim()).toHours();

      if (horas > LIMITE_MAXIMO_DURACAO_HORAS) {
        throw new DuracaoMaiorQuePermitidoException((int) horas);
      }

      turma.setDuracao((int) horas);
    }
  }

  @Transactional(readOnly = true)
  public Page<TurmaResumoDto> listarTurmas(Integer pagina, Integer limite) {
    if (pagina == null || pagina < 0 || limite == null || limite <= 0) {
      throw new ParametrosListagemInvalidosException("Página ou limite de listagem inválidos.");
    }
    Pageable pageable = PageRequest.of(pagina, limite, Sort.by("nome").ascending());

    return turmaRepository.findAll(pageable).map(turma -> turmaMapper.paraResumoDto(turma));
  }

  @Transactional(readOnly = true)
  public Turma buscarPorId(Long id) {
    return turmaRepository.findById(id).orElseThrow(() -> new TurmaNaoEncontradaException(id));
  }

  public TurmaDetalhesDto buscarTurmaDetalhada(Long id) {
    Turma turma = buscarPorId(id);
    return turmaMapper.paraDetalhesDto(turma);
  }

  @Transactional
  public void deletar(Long id) {
    Turma turma = buscarPorId(id);

    if (!turma.getAlunos().isEmpty()) {
      throw new TurmaPossuiAlunosException(turma.getNome(), turma.getAlunos().size());
    }
    turmaRepository.delete(turma);
  }

  @Transactional
  public TurmaResumoDto alterar(Long id, TurmaDto dto) {

    Turma turmaExistente = buscarPorId(id);

    turmaRepository
        .findByNome(dto.getNome())
        .ifPresent(
            t -> {
              if (!t.getId().equals(id)) {
                throw new TurmaJaExistenteException(dto.getNome());
              }
            });

    turmaExistente.setNome(dto.getNome());
    turmaExistente.setHorarioInicio(dto.getHorarioInicio());
    turmaExistente.setHorarioFim(dto.getHorarioFim());
    turmaExistente.setLimiteTurma(dto.getLimiteTurma());

    Turma turmaAtualizada = turmaRepository.save(turmaExistente);

    return turmaMapper.paraResumoDto(turmaAtualizada);
  }
}
