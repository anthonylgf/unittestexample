package com.example.unittestexample.repositories;

import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlunoSpecificationFactory {

  public static Specification<Aluno> nomeCompletoIgualA(String nomeCompleto) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("nome_completo"), nomeCompleto.toUpperCase());
  }

  public static Specification<Aluno> dataNascimentoMaiorIgualA(LocalDate dataNascimento) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.greaterThanOrEqualTo(root.get("dataNascimento"), dataNascimento);
  }

  public static Specification<Aluno> dataNascimentoMenorIgualA(LocalDate dataNascimento) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.lessThanOrEqualTo(root.get("dataNascimento"), dataNascimento);
  }

  public static Specification<Aluno> generoIgualA(Genero genero) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("genero"), genero);
  }
}
