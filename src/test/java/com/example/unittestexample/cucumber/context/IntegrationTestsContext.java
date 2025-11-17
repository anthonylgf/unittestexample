package com.example.unittestexample.cucumber.context;

import com.example.unittestexample.dtos.AlunoDto;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Getter
@Setter
@Component
@ScenarioScope
@NoArgsConstructor
public class IntegrationTestsContext {

  private AlunoDto alunoCriadoBanco;

  private AlunoDto alunoRecuperadoDoBanco;
}
