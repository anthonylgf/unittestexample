package com.example.unittestexample.repositories;

import com.example.unittestexample.models.Turma;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

  Optional<Turma> findByNome(String nome);
}
