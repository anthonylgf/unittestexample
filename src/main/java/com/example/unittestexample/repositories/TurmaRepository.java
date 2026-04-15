package com.example.unittestexample.repositories;

import com.example.unittestexample.models.Turma;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

  Optional<Turma> findByNome(String nome);

  @Query("SELECT t FROM Turma t LEFT JOIN FETCH t.alunos WHERE t.nome = :nome")
  Optional<Turma> findByNomeWithAlunos(@Param("nome") String nome);
}
