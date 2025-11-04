package com.example.unittestexample.cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import io.cucumber.java.an.E;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
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
          + "\"dataNascimento\": \"31-10-2021\"" // Formato correto!
          + "}";

  private final AlunoRepository alunoRepository;

  private final WebTestClient webTestClient;

  private WebTestClient.ResponseSpec responseSpec;

  private Aluno aluno =
      new Aluno(1L, "KARINE FERREIRA", Genero.FEMININO, LocalDate.of(2021, 10, 31));

  private Long id;

  private String gerarNomeAleatorio() {
    return UUID.randomUUID().toString().replace("-", "").replaceAll("[0-9]", "a").toUpperCase();
  }

  private List<Aluno> alunos;

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
  public void requisicaoCadastrar201() {

    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonDeEntrada)
            .exchange();
  }

  @Then("o codigo de resposta deve ser 201")
  public void respostaCadastrar201() {
    responseSpec.expectStatus().isCreated();
  }

  @And("o aluno deve existir no banco de dados")
  public void alunoCadastrarNoBanco201() {
    List<Aluno> alunosSalvo = alunoRepository.findAll();
    assertEquals(1, alunosSalvo.size(), "Deve existir 1 aluno salvo no banco ");

    Aluno alunoSalvo = alunosSalvo.get(0);
    assertEquals(aluno.getNomeCompleto(), alunoSalvo.getNomeCompleto());
    assertEquals(aluno.getDataNascimento(), alunoSalvo.getDataNascimento());
    assertEquals(aluno.getGenero(), alunoSalvo.getGenero());

    System.out.println("Aluno cadastrado com sucesso");
  }

  private Aluno aluno400 =
      new Aluno(1L, "Karine Ferreira", Genero.FEMININO, LocalDate.now().minusYears(4L));

  @When("eu faco uma requisicao POST para aluno com dados invalidos")
  public void requisicaoCadastrar400() {
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(aluno400)
            .exchange();
  }

  @Then("o codigo de resposta deve ser 400")
  public void respostaCadastrar400() {
    responseSpec.expectStatus().isBadRequest();
  }

  private final Aluno alunotest = new Aluno();

  @Given("que o banco de dados não está vazio")
  public void bancoDadosProcurarId() {

    System.out.println("Adicionando aluno");

    if (alunoRepository != null) {
      alunoRepository.deleteAll();
    }
    alunotest.setNomeCompleto("Jose William");
    alunotest.setGenero(Genero.MASCULINO);
    alunotest.setDataNascimento(LocalDate.of(2021, 10, 31));

    Aluno alunoSalvo = alunoRepository.save(alunotest);

    this.id = alunoSalvo.getId();
    System.out.println("Aluno inserido com ID: " + this.id);
  }

  @When("eu faco uma requisicao GET para aluno atraves do id")
  public void requisicaoProcurarId() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", this.id).exchange();
  }

  @Then("retorna os dados do aluno")
  public void respostaProcurarId() {
    responseSpec
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(this.id)
        .jsonPath("$.nome")
        .isEqualTo("Jose")
        .jsonPath("$.genero")
        .isEqualTo(alunotest.getGenero())
        .jsonPath("$.dataNascimento")
        .isEqualTo("31-10-2021");
  }

  @Given("que eu passo um id inexistente")
  public void bancoDadosProcurarId_Retornar404() {

    if (alunoRepository != null) {
      alunoRepository.deleteAll();
    }
    long idInexistente = 99L;
    this.id = idInexistente;
    System.out.println("Aluno com ID: " + this.id);
  }

  @When("eu faco uma requisicao GET para aluno com id inexistente")
  public void requisicaoProcurarId_Retornar404() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", this.id).exchange();
  }

  @Then("retorna erro 404")
  public void respostaProcurarId_Retornar404() {
    responseSpec.expectStatus().isNotFound();
  }

  private int pagina;
  private int limite;

  public static class RestPageImpl<T> extends PageImpl<T> {
    public RestPageImpl(List<T> content, Pageable pageable, long total) {
      super(content, pageable, total);
    }
  }

  @Given("que eu passe {int} alunos")
  public void bancoDadosTodosOsAlunos(Integer totalAlunos) {
    this.pagina = 0;
    this.limite = 2;
    Aluno aluno2 =
        new Aluno(null, gerarNomeAleatorio(), Genero.MASCULINO, LocalDate.now().minusYears(4L));
    Aluno aluno3 =
        new Aluno(null, gerarNomeAleatorio(), Genero.FEMININO, LocalDate.now().minusYears(5L));

    alunoRepository.save(aluno2);
    alunoRepository.save(aluno3);
  }

  @When("eu faco uma requisicao GET para alunos")
  public void requisicaoTodosOsAlunos() {
    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", pagina)
                        .queryParam("limite", limite)
                        .build())
            .exchange();
  }

  @Then("retorne uma lista de alunos")
  public List<Aluno> respostaTodosOsAlunos() {
    alunos =
        responseSpec
            .expectStatus()
            .isOk()
            .expectBodyList(Aluno.class)
            .returnResult()
            .getResponseBody();
    Assertions.assertNotNull(alunos, "A lista de alunos não deve ser nula.");
    Assertions.assertEquals(
        this.limite,
        alunos.size(),
        "O número de alunos retornados deve ser igual ao limite da página.");
    if (alunos != null) {
      alunos.forEach(a -> System.out.println("Aluno: " + a.getNomeCompleto()));
      System.out.println("Total de alunos: " + alunos.size());
    }
    return alunos;
  }

  private final String paginaInvalida = "abc";
  private final String limiteInvalido = "xyz";

  @Given("que eu passe {int} alunos no banco")
  public void bancoDadosTodosOsAlunos_Erro400(Integer totalAlunos) {
    Aluno aluno2 =
        new Aluno(null, gerarNomeAleatorio(), Genero.MASCULINO, LocalDate.now().minusYears(4L));
    Aluno aluno3 =
        new Aluno(null, gerarNomeAleatorio(), Genero.FEMININO, LocalDate.now().minusYears(5L));

    alunoRepository.save(aluno2);
    alunoRepository.save(aluno3);
  }

  @When("eu faco uma requisicao GET para alunos com parametros de listagem invalidos")
  public void requisicaoTodosOsAlunos_ComParametrosInvalidos_Erro500() {
    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", paginaInvalida)
                        .queryParam("limite", limiteInvalido)
                        .build())
            .exchange();
  }

  @Then("retorne erro 400")
  public void respostaTodosOsAlunos_ComParametrosInvalidos_Erro400() {
    responseSpec.expectStatus().isBadRequest();
    System.out.println("Erro 400 - Parametros inválidos");
  }

  @Given("que eu passe o id do aluno")
  public void bancoDeletarAluno_204() {
    Long id = 1L;
    aluno.setId(id);
    System.out.println("Aluno com ID: " + id);
  }

  @When("eu faco uma requisicao DEL para o id do aluno")
  public void requisicaoDeletarAluno_204() {
    responseSpec = webTestClient.delete().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  @E("eu faco uma requisicao GET para aluno atraves do id novamente")
  public void novarequisicaoDeletarAluno_204() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  @Then("e tem que retornar 404")
  public void respostaDeletarAluno_204() {
    responseSpec.expectStatus().isNotFound();
  }

  @Given("que eu passe um id inexistente do aluno")
  public void bancoDeletarAluno_Erro404() {
    Long id = 99L;
    aluno.setId(id);
    System.out.println("Aluno com ID: " + id);
  }

  @When("eu faco uma requisicao DEL para o id inexistente do aluno")
  public void requisicaoDeletarAluno_Erro404() {
    responseSpec = webTestClient.delete().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  @E("eu faco uma nova requisicao GET para aluno atraves do id inexistente novamente")
  public void novarequisicaoDeletarAluno_Erro404() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  private AlunoDto alunoDto =
      new AlunoDto(
          null,
          gerarNomeAleatorio(),
          gerarNomeAleatorio(),
          Genero.FEMININO,
          LocalDate.now().minusYears(4L));

  @Given("que o aluno esteja no banco de dados")
  public void salvarAlunoNoBanco_Retornar204() {
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(this.alunoDto)
            .exchange();
    responseSpec.expectStatus().isCreated();
  }

  @E("que eu passe o id do aluno e novos dados")
  public void bancoAlterarAluno_Retornar204() {
    AlunoDto alunoDtoCreate =
        responseSpec.returnResult(AlunoDto.class).getResponseBody().single().block();

    Assertions.assertNotNull(alunoDtoCreate.getId());
    this.id = alunoDtoCreate.getId();
  }

  @When("eu faco uma requisicao PATCH para o id do aluno")
  public void requisicaoAlterarAluno_Retornar204() {
    String novoNome = gerarNomeAleatorio();
    String novoSobrenome = gerarNomeAleatorio();

    AlunoDto alunoDto1 = new AlunoDto();
    alunoDto1.setNome(novoNome);
    alunoDto1.setSobrenome(novoSobrenome);

    Assertions.assertEquals(novoNome, alunoDto1.getNome());
    Assertions.assertEquals(novoSobrenome, alunoDto1.getSobrenome());
    responseSpec =
        webTestClient
            .patch()
            .uri("/alunos/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto1)
            .exchange();
  }

  @Then("e tem que retornar 204")
  public void respostaAlterarAluno_Retornar204() {
    responseSpec.expectStatus().isNoContent();
  }

  @Given("que eu passe um id inexistente do aluno e novos dados")
  public void bancoAlterarAluno_Retornar404() {
    this.id = 99L;
  }

  @When("eu faco  uma requisicao PATCH para o id do aluno inexistente")
  public void requisicaoAlterarAluno_Retornar404() {
    String novoNome = gerarNomeAleatorio();
    String novoSobrenome = gerarNomeAleatorio();

    AlunoDto alunoDto1 = new AlunoDto();
    alunoDto1.setNome(novoNome);
    alunoDto1.setSobrenome(novoSobrenome);

    Assertions.assertEquals(novoNome, alunoDto1.getNome());
    Assertions.assertEquals(novoSobrenome, alunoDto1.getSobrenome());
    responseSpec =
        webTestClient
            .patch()
            .uri("/alunos/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto1)
            .exchange();
  }
}
