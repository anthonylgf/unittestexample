package com.example.unittestexample.services;

import static java.util.Objects.*;

import com.example.unittestexample.configs.ApplicationProperties;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.exceptions.AlunoExisteMesmoNomeException;
import com.example.unittestexample.exceptions.AlunoNaoEncontradoException;
import com.example.unittestexample.exceptions.IdadeInvalidaException;
import com.example.unittestexample.exceptions.ParametrosListagemInvalidosException;
import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.publisher.AlunoPublisher;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.repositories.AlunoSpecificationFactory;
import com.example.unittestexample.utils.DateUtils;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoService {

  private final AlunoRepository alunoRepository;
  private final ApplicationProperties applicationProperties;
  private final DateUtils dateUtils;
  private final AlunoPublisher alunoPublisher;

  @Transactional
  public Aluno salvar(Aluno aluno) {
    // Verificar se a idade do aluno eh valida
    int idadeAluno = dateUtils.diferencaEmAnosDataAtual(aluno.getDataNascimento());
    if (!idadeValida(idadeAluno)) {
      throw new IdadeInvalidaException(
          idadeAluno,
          applicationProperties.getMinimoIdade(),
          applicationProperties.getMaximoIdade());
    }

    // Verificar se nao existe um aluno com o mesmo nome
    Optional<Aluno> alunoMesmoNome = alunoRepository.findByNomeCompleto(aluno.getNomeCompleto());
    if (alunoMesmoNome.isPresent()) {
      throw new AlunoExisteMesmoNomeException(aluno.getNomeCompleto());
    }
    Aluno alunoSalvo = alunoRepository.save(aluno);

    alunoPublisher.sendAluno(alunoSalvo);

    return alunoSalvo;
  }

  public void atualizarAluno(Long id, Aluno aluno) {
    Aluno alunoSalvo =
        alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));

    AlunoMapper.INSTANCE.merge(aluno, alunoSalvo);
    alunoRepository.save(alunoSalvo);
  }

  public void deletarAluno(Long id) {
    Aluno alunoSalvo =
        alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));

    alunoRepository.delete(alunoSalvo);
  }

  public Aluno buscarPorId(Long id) {
    return alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));
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
}
