package com.example.unittestexample.services;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.TurmaDetalhesDto;
import com.example.unittestexample.dtos.TurmaDto;
import com.example.unittestexample.dtos.TurmaResumoDto;
import com.example.unittestexample.exceptions.*;
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.mappers.TurmaMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.repositories.TurmaRepository;
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

  private final TurmaRepository turmaRepository;
  private final AlunoRepository alunoRepository;
  private final AlunoMapper alunoMapper;
  private final TurmaMapper turmaMapper;

  @Transactional
  public Turma salvar(Turma turma) {

    turmaRepository
        .findByNome(turma.getNome())
        .ifPresent(
            t -> {
              throw new TurmaJaExistenteException(turma.getNome());
            });

    Turma turmaSalva = turmaRepository.save(turma);

    return turmaSalva;
  }

  @Transactional(readOnly = true)
  public Page<TurmaResumoDto> listarTurmas(Integer pagina, Integer limite) {
    Pageable pageable = PageRequest.of(pagina, limite, Sort.by("nome").ascending());

    return turmaRepository.findAll(pageable).map(turma -> turmaMapper.paraResumoDto(turma));
  }

  @Transactional
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
  public AlunoDto transferirAluno(Long alunoId, Long novaTurmaId) {
    Aluno aluno =
        alunoRepository
            .findById(alunoId)
            .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));

    Turma novaTurma = buscarPorId(novaTurmaId);

    int alunosMatriculados = novaTurma.getAlunos().size();
    if (alunosMatriculados >= novaTurma.getLimiteTurma()) {
      throw new TurmaLotadaException(novaTurma.getNome(), novaTurma.getLimiteTurma());
    }

    aluno.setTurma(novaTurma);
    Aluno alunoSalvo = alunoRepository.save(aluno);

    return alunoMapper.mapearParaAlunoDto(alunoSalvo);
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
