package com.example.unittestexample.repositories;

import com.example.unittestexample.models.Aluno;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository
    extends JpaRepository<Aluno, Long>, JpaSpecificationExecutor<Aluno> {

  Optional<Aluno> findByNomeCompleto(String nomeCompleto);
}
