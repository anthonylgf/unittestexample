package com.example.unittestexample.cucumber.context;

import com.example.unittestexample.cucumber.models.PaginaAlunos;
import com.example.unittestexample.cucumber.subscriber.AlunoSubscriber;
import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.models.Turma;
import io.cucumber.spring.ScenarioScope;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ScenarioScope
@RequiredArgsConstructor
public class IntegrationTestsContext {

  private final AlunoSubscriber alunoSubscriber;

  private AlunoDto alunoCriadoBanco;

  private AlunoDto alunoRecuperadoDoBanco;

  private List<AlunoDto> alunosCriadosNoBanco;

  private AlunoFilters alunoFilters;

  private PaginaAlunos paginaAlunos;

  private Turma turmaCriadaBanco;
}
