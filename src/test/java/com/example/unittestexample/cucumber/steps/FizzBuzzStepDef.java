package com.example.unittestexample.cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@RequiredArgsConstructor
public class FizzBuzzStepDef {

  private String jsonDeEntrada =
      "{"
          + "\"nome\": \"KARINE\","
          + "\"sobrenome\": \"FERREIRA\","
          + "\"genero\": \"FEMININO\","
          + "\"dataNascimento\": \"18-06-2019\"" // Formato correto!
          + "}";

  private final AlunoRepository alunoRepository;

  private final WebTestClient webTestClient;

  private WebTestClient.ResponseSpec responseSpec;

  private Aluno aluno = new Aluno("KARINE FERREIRA", Genero.FEMININO, LocalDate.of(2019, 6, 18));

  private Long id;

  private Page<Aluno> page;

  @Given("que o banco de dados está vazio")
  public void bancoDados() {
    System.out.println("Limpando o banco...");

    if (alunoRepository != null) {
      alunoRepository.deleteAll();
    }

    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }
    System.out.println("Banco limpo!");
  }

  @When("eu faco uma requisicao POST para aluno com dados validos")
  public void requisicao204() {

    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonDeEntrada)
            .exchange();
  }

  @Then("o codigo de resposta deve ser 204")
  public void resposta204() {
    responseSpec.expectStatus().isCreated();
  }

  @And("o aluno deve existir no banco de dados")
  public void alunoDeveExistirNoBanco204() {
    List<Aluno> alunosSalvo = alunoRepository.findAll();
    assertEquals(1, alunosSalvo.size(), "Deve existir 1 aluno salvo no banco ");

    Aluno alunoSalvo = alunosSalvo.get(0);
    assertEquals(aluno.getNomeCompleto(), alunoSalvo.getNomeCompleto());
    assertEquals(aluno.getDataNascimento(), alunoSalvo.getDataNascimento());
    assertEquals(aluno.getGenero(), alunoSalvo.getGenero());

    System.out.println("Aluno cadastrado com sucesso");
  }

  private Aluno aluno400 = new Aluno("Karine Ferreira", Genero.FEMININO, LocalDate.of(2026, 6, 18));

  @When("eu faco uma requisicao POST para aluno com dados invalidos")
  public void requisicao400() {
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(aluno400)
            .exchange();
  }

  @Then("o codigo de resposta deve ser 400")
  public void resposta400() {
    responseSpec.expectStatus().isBadRequest();
  }

  @Given("que o banco de dados não está vazio")
  public void bancoDadosId() {

    System.out.println("Adicionando aluno");

    if (alunoRepository != null) {
      alunoRepository.deleteAll();
    }

    Aluno alunoSalvo = alunoRepository.save(aluno);

    this.id = alunoSalvo.getId();
    System.out.println("Aluno inserido com ID: " + this.id);
  }

  @When("eu faco uma requisicao GET para aluno atraves do id")
  public void requisicaoId() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", this.id).exchange();
  }

  @Then("retorna os dados do aluno")
  public void respostaId() {
    responseSpec
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(this.id)
        .jsonPath("$.nome")
        .isEqualTo("KARINE")
        .jsonPath("$.genero")
        .isEqualTo(aluno.getGenero())
        .jsonPath("$.dataNascimento")
        .isEqualTo("18-06-2019");
  }

  @Given("que eu passo um id inexistente")
  public void bancoDadosId404() {

    if (alunoRepository != null) {
      alunoRepository.deleteAll();
    }
    Long idInexistente = 99L;
    this.id = idInexistente;
    System.out.println("Aluno com ID: " + this.id);
  }

  @When("eu faco uma requisicao GET para aluno com id inexistente")
  public void requisicaoId404() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", this.id).exchange();
  }

  @Then("retorna erro 404")
  public void respostaId404() {
    responseSpec.expectStatus().isNotFound();
  }

  @Given("que eu passe \\{int} alunos")
  public void bancoDadosTodosOsAlunos(Integer totalAlunos) {
    totalAlunos = 2;
    Integer pagina = 2;
    Integer limite = 2;
    Aluno aluno2 = new Aluno("José William", Genero.MASCULINO, LocalDate.of(2019, 6, 9));
    List<Aluno> alunos = Arrays.asList(aluno, aluno2);

    Pageable pageableEsperado = Pageable.ofSize(limite).withPage(pagina);
    page = new PageImpl<>(alunos, pageableEsperado, alunos.size());
  }

  @When("eu faco uma requisicao GET para alunos")
  public void requisicaoTodosOsAlunos() {
    Integer pagina = page.getPageable().getPageNumber();
    Integer limite = page.getPageable().getPageSize();

    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("page", pagina)
                        .queryParam("size", limite)
                        .build())
            .exchange();
  }

  @Then("retorne uma lista de alunos")
  public List<Aluno> respostaTodosOsAlunos() {
    responseSpec
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].nome")
        .isEqualTo(page.getContent().get(0).getNomeCompleto())
        .jsonPath("$.content[1].nome")
        .isEqualTo(page.getContent().get(1).getNomeCompleto())
        .jsonPath("$.number")
        .isEqualTo(page.getNumber())
        .jsonPath("$.size")
        .isEqualTo(page.getSize())
        .jsonPath("$.totalElements")
        .isEqualTo(page.getTotalElements());

    return page.getContent();
  }
}
