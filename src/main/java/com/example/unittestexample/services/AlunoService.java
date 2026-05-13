package com.example.unittestexample.services;

import static java.util.Objects.*;

import com.example.unittestexample.configs.ApplicationProperties;
import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.exceptions.*;
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.publisher.AlunoPublisher;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.repositories.AlunoSpecificationFactory;
import com.example.unittestexample.repositories.TurmaRepository;
import com.example.unittestexample.utils.DateUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlunoService {

  private final AlunoRepository alunoRepository;
  private final ApplicationProperties applicationProperties;
  private final DateUtils dateUtils;
  private final AlunoPublisher alunoPublisher;
  private final TurmaRepository turmaRepository;
  private final AlunoMapper mapper;
  private final MeterRegistry registry;

  private final List<Aluno> filaPendentes = new CopyOnWriteArrayList<>();

  public AlunoService(
      MeterRegistry registry,
      AlunoRepository repository,
      ApplicationProperties applicationProperties,
      DateUtils dateUtils,
      AlunoPublisher alunoPublisher,
      TurmaRepository turmaRepository,
      AlunoMapper mapper) {

    this.alunoRepository = repository;
    this.applicationProperties = applicationProperties;
    this.dateUtils = dateUtils;
    this.alunoPublisher = alunoPublisher;
    this.turmaRepository = turmaRepository;
    this.mapper = mapper;
    this.registry = registry;

    Gauge.builder("aluno.fila.pendentes", filaPendentes, java.util.List::size)
        .description("Quantidade de alunos aguardando processamento")
        .register(registry);
  }

  @Transactional
  public Aluno salvar(Aluno aluno) {
    return Timer.builder("aluno.criar.duration")
        .description("Tempo de execucao da operacao criar aluno")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(registry)
        .record(
            () -> {
              filaPendentes.add(aluno);
              try {
                // Verificar se a idade do aluno eh valida
                int idadeAluno = dateUtils.diferencaEmAnosDataAtual(aluno.getDataNascimento());
                if (!idadeValida(idadeAluno)) {
                  throw new IdadeInvalidaException(
                      idadeAluno,
                      applicationProperties.getMinimoIdade(),
                      applicationProperties.getMaximoIdade());
                }
                if (aluno.getTurma() == null) {
                  throw new TurmaObrigatoriaException();
                }

                Turma turma =
                    turmaRepository
                        .findById(aluno.getTurma().getId())
                        .orElseThrow(
                            () -> new TurmaNaoEncontradaException(aluno.getTurma().getId()));

                if (turma.getAlunos().size() >= turma.getLimiteTurma()) {
                  throw new TurmaLotadaException(turma.getNome(), turma.getLimiteTurma());
                }

                aluno.setTurma(turma);

                // Verificar se nao existe um aluno com o mesmo nome
                Optional<Aluno> alunoMesmoNome =
                    alunoRepository.findByNomeCompleto(aluno.getNomeCompleto());
                if (alunoMesmoNome.isPresent()) {
                  throw new AlunoExisteMesmoNomeException(aluno.getNomeCompleto());
                }
                Aluno alunoSalvo = alunoRepository.save(aluno);

                Counter.builder("aluno.criado")
                    .description("Total de alunos criados com sucesso")
                    .tag("origem", "api")
                    .register(registry)
                    .increment();

                alunoPublisher.sendAluno(alunoSalvo);

                return alunoSalvo;
              } finally {
                filaPendentes.remove(aluno);
              }
            });
  }

  public void atualizarAluno(Long id, Aluno aluno) {
    Aluno alunoSalvo =
        alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));

    mapper.merge(aluno, alunoSalvo);
    alunoRepository.save(alunoSalvo);
  }

  public void deletarAluno(Long id) {
    Aluno alunoSalvo =
        alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));

    alunoRepository.delete(alunoSalvo);
  }

  @Cacheable(value = "alunos", key = "#id")
  public AlunoDto buscarPorId(Long id) {
    Aluno aluno =
        alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));

    return mapper.mapearParaAlunoDto(aluno);
  }

  public Page<Aluno> listarAlunos(AlunoFilters filters, Integer pagina, Integer limite) {
    // Verifica se os parametros estao corretos
    if (!parametrosIdadeValidos(filters)) {
      throw new ParametrosListagemInvalidosException(
          "Parametros de idade invalidos. Idade maxima deve ser maior que idade minima.");
    }

    // Construir query dinamica
    var specificationList = new ArrayList<Specification<Aluno>>();
    if (nonNull(filters.getNomeCompleto())) {
      specificationList.add(
          AlunoSpecificationFactory.nomeCompletoIgualA(filters.getNomeCompleto()));
    }

    if (nonNull(filters.getIdadeMinima())) {
      var dataNascimento = dateUtils.recuperarDataEmAnos(filters.getIdadeMinima());
      specificationList.add(AlunoSpecificationFactory.dataNascimentoMenorIgualA(dataNascimento));
    }

    if (nonNull(filters.getIdadeMaxima())) {
      var dataNascimento = dateUtils.recuperarDataEmAnos(filters.getIdadeMaxima());
      specificationList.add(AlunoSpecificationFactory.dataNascimentoMaiorIgualA(dataNascimento));
    }

    if (nonNull(filters.getGenero())) {
      specificationList.add(AlunoSpecificationFactory.generoIgualA(filters.getGenero()));
    }

    Specification<Aluno> query = Specification.allOf(specificationList);
    Pageable pageable = recuperarPaginacao(pagina, limite);

    return alunoRepository.findAll(query, pageable);
  }

  private boolean idadeValida(int idadeAluno) {
    return idadeAluno >= applicationProperties.getMinimoIdade()
        && idadeAluno <= applicationProperties.getMaximoIdade();
  }

  private boolean parametrosIdadeValidos(AlunoFilters alunoFilters) {
    if (isNull(alunoFilters.getIdadeMaxima()) || isNull(alunoFilters.getIdadeMinima())) {
      return true;
    }

    return alunoFilters.getIdadeMaxima() > alunoFilters.getIdadeMinima();
  }

  private Pageable recuperarPaginacao(Integer pagina, Integer limite) {
    requireNonNull(pagina, "pagina nao pode ser nulo.");
    requireNonNull(limite, "limite nao pode ser nulo.");
    return Pageable.ofSize(limite).withPage(pagina);
  }

  @Transactional
  public AlunoDto transferirAluno(Long alunoId, Long novaTurmaId) {
    Aluno aluno =
        alunoRepository
            .findById(alunoId)
            .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));

    Turma novaTurma =
        turmaRepository
            .findById(novaTurmaId)
            .orElseThrow(() -> new TurmaNaoEncontradaException(novaTurmaId));

    int alunosMatriculados = novaTurma.getAlunos().size();
    if (alunosMatriculados >= novaTurma.getLimiteTurma()) {
      throw new TurmaLotadaException(novaTurma.getNome(), novaTurma.getLimiteTurma());
    }

    aluno.setTurma(novaTurma);
    Aluno alunoSalvo = alunoRepository.save(aluno);

    return mapper.mapearParaAlunoDto(alunoSalvo);
  }
}
