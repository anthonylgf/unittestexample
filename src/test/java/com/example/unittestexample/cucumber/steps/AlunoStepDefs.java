package com.example.unittestexample.cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.unittestexample.cucumber.context.IntegrationTestsContext;
import com.example.unittestexample.cucumber.models.PaginaAlunos;
import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.repositories.AlunoRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.es.Dado;
import io.cucumber.java.it.Quando;
import io.cucumber.java.pt.Entao;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AlunoStepDefs {

  private final AlunoRepository alunoRepository;

  private final WebTestClient webTestClient;

  private final IntegrationTestsContext integrationTestsContext;

  private WebTestClient.ResponseSpec responseSpec;

  private final Aluno aluno =
      new Aluno(1L, "KARINE FERREIRA", Genero.FEMININO, LocalDate.of(2021, 10, 31));

  private final Aluno alunoIdInexistente =
      new Aluno(99L, "Aluno Test", Genero.FEMININO, LocalDate.now().minusYears(4L));

  private final int pagina = 0;

  private final int limite = 2;

  @Before
  @After
  @Transactional
  public void limparBaseParaTeste() {
    alunoRepository.deleteAll();
    System.out.println("Base de dados limpa com sucesso.");
  }

  private String gerarNomeAleatorio() {
    return UUID.randomUUID().toString().replace("-", "").replaceAll("[0-9]", "a").toUpperCase();
  }

  @Dado("que o banco de dados está vazio")
  public void bancoDados() {
    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }
  }

  @Quando("eu tento criar um aluno")
  public void requisicaoCadastrar201() {

    String jsonDeEntrada =
        "{"
            + "\"nome\": \"KARINE\","
            + "\"sobrenome\": \"FERREIRA\","
            + "\"genero\": \"FEMININO\","
            + "\"dataNascimento\": \"31-10-2021\""
            + "}";
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonDeEntrada)
            .exchange();
  }

  @Entao("o aluno tem que ser Criado no banco")
  public void respostaCadastrar201() {
    responseSpec.expectStatus().isCreated();
  }

  @E("o aluno deve existir no banco de dados")
  public void alunoCadastrarNoBanco201() {
    List<Aluno> alunosSalvo = alunoRepository.findAll();
    assertEquals(1, alunosSalvo.size(), "Deve existir 1 aluno salvo no banco ");

    Aluno alunoSalvo = alunosSalvo.getFirst();
    assertEquals(aluno.getNomeCompleto(), alunoSalvo.getNomeCompleto());
    assertEquals(aluno.getDataNascimento(), alunoSalvo.getDataNascimento());
    assertEquals(aluno.getGenero(), alunoSalvo.getGenero());

    System.out.println("Aluno cadastrado com sucesso");
  }

  @Quando("eu tento criar um aluno com nome invalido")
  public void requisicaoCadastrar400() {
    Aluno aluno400 =
        new Aluno(1L, "Karine Ferreira1", Genero.FEMININO, LocalDate.now().minusYears(4L));
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(aluno400)
            .exchange();
  }

  @Entao("a resposta deve ser um erro de formatação")
  public void respostaCadastrar400() {
    responseSpec.expectStatus().isBadRequest();
  }

  Aluno test = new Aluno(1L, "Ana Clara", Genero.FEMININO, LocalDate.now().minusYears(4L));

  @Dado("que o banco de dados esteja com um aluno salvo")
  public void bancoDadosNomeDuplicado() {
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(test)
            .exchange();
  }

  @Quando("eu tento criar um aluno com o mesmo nome")
  public void requisicaoCadastrarNomeDuplicado() {
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(test)
            .exchange();
  }

  @Dado("que o banco de dados não está vazio")
  public void bancoDadosProcurarId() {
    AlunoDto alunoSalvar = new AlunoDto();
    alunoSalvar.setNome(gerarNomeAleatorio());
    alunoSalvar.setSobrenome(gerarNomeAleatorio());
    alunoSalvar.setDataNascimento(LocalDate.now().minusYears(4L));
    alunoSalvar.setGenero(Genero.FEMININO);
    AlunoDto alunoCriado =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoSalvar)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();
    Assertions.assertNotNull(alunoCriado, "Aluno nulo na criação");
    integrationTestsContext.setAlunoCriadoBanco(alunoCriado);
    System.out.println("ID do Aluno obtido da resposta: " + alunoCriado.getId());
  }

  @Quando("eu tendo procurar um aluno atraves do id")
  public void requisicaoProcurarId() {
    AlunoDto alunoDto = integrationTestsContext.getAlunoCriadoBanco();
    Assertions.assertNotNull(alunoDto);
    AlunoDto alunoRecuperado =
        webTestClient
            .get()
            .uri("/alunos/{id}", alunoDto.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();
    Assertions.assertNotNull(alunoRecuperado);
    integrationTestsContext.setAlunoRecuperadoDoBanco(alunoRecuperado);
  }

  @Entao("retorne o aluno procurado")
  public void respostaProcurarId() {
    AlunoDto alunoCriado = integrationTestsContext.getAlunoCriadoBanco();
    AlunoDto alunoRecuperado = integrationTestsContext.getAlunoRecuperadoDoBanco();
    Assertions.assertEquals(alunoCriado.getNome(), alunoRecuperado.getNome());
  }

  @Dado("que eu passo um id inexistente")
  public void bancoDadosProcurarId_Retornar404() {

    System.out.println("Aluno com ID: " + alunoIdInexistente.getId());
  }

  @Quando("eu tendo procurar um aluno com id inexistente")
  public void requisicaoProcurarId_Retornar404() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", alunoIdInexistente.getId()).exchange();
  }

  @Entao("retorna que nao conseguiu encontrar o recurso solicitado")
  public void respostaProcurarId_Retornar404() {
    responseSpec.expectStatus().isNotFound();
  }

  private final AlunoFilters alunoFilters = new AlunoFilters();

  @Dado("que o banco de dados possua alunos")
  public void bancoDeDadosPossuaAlunos() {
    AlunoDto alunoDto =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L));
    AlunoDto alunoDto2 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L));
    System.out.println("Limpando o banco...");

    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }
    System.out.println("Banco limpo!");
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto)
            .exchange();
    responseSpec.expectStatus().isCreated();
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto2)
            .exchange();
    responseSpec.expectStatus().isCreated();

    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @E("eu configuro o filtro de idade mínima e máxima")
  public void confuguracaIdadeMinEMax() {
    alunoFilters.setIdadeMinima(10);
    alunoFilters.setIdadeMaxima(2);
  }

  @Quando("eu procurar os alunos")
  public void requisicaoGetFiltroNomeCompleto_RetornarSucesso() {
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

  @Dado("que o banco de dados possua alunos com idades abaixo do minimo e acima do maximo")
  public void bancoDeDadosPossuaAlunosComIdadesInvalidas_RetornarErro() {
    AlunoDto alunoDtoTest1 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(1L));
    AlunoDto alunoDtoTest2 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.MASCULINO,
            LocalDate.now().minusYears(11L));
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest1)
            .exchange()
            .expectStatus()
            .isBadRequest();
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest2)
            .exchange()
            .expectStatus()
            .isBadRequest();
    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @Quando("eu procurar os alunos com parametros Idade maxima e minima")
  public void requisicaoGetFiltroIdadesInvalidas_RetornarErro() {
    int idadeMin = 10;
    int idadeMax = 2;
    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", pagina)
                        .queryParam("limite", limite)
                        .queryParam("IdadeMinima", idadeMin)
                        .queryParam("IdadeMaxima", idadeMax)
                        .build())
            .exchange();
  }

  @Quando("eu procurar os alunos com parametros de Idade")
  public void requisicaoGetFiltroIdades() {
    int idadeMin = 2;
    int idadeMax = 10;
    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", pagina)
                        .queryParam("limite", limite)
                        .queryParam("IdadeMinima", idadeMin)
                        .queryParam("IdadeMaxima", idadeMax)
                        .build())
            .exchange();
  }

  @Entao("deve retornar nenhum aluno")
  public void respostaTodosOsAlunosDentroDaIdadeValida() {
    var pageAlunos =
        responseSpec
            .expectStatus()
            .isOk()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();

    Assertions.assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");

    Assertions.assertEquals(
        0,
        pageAlunos.getContent().size(),
        "O número de alunos retornados deve ser 0, pois nenhum aluno está na faixa de idade [2, 10].");

    Assertions.assertEquals(
        0, pageAlunos.getPage().totalElements(), "O total de elementos deve ser 0.");
  }

  private Genero generoFiltro;

  @Dado("que o banco de dados possua alunos de diferentes gêneros")
  public void bancoDeDadosPossuaAlunosComGenerosDiferentes() {
    AlunoDto alunoDtoTest1 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L));
    AlunoDto alunoDtoTest2 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.MASCULINO,
            LocalDate.now().minusYears(6L));
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest1)
            .exchange();
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest2)
            .exchange();
    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @E("eu filtro por gênero {string}")
  public void euFiltroPorGenero(String genero) {
    this.generoFiltro = Genero.valueOf(genero.toUpperCase());
    alunoFilters.setGenero(this.generoFiltro);
  }

  @Quando("eu procuro os alunos com o filtro de genero")
  public void requisicaoGetFiltroGenero() {
    responseSpec =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", pagina)
                        .queryParam("limite", limite)
                        .queryParam("genero", this.generoFiltro.name())
                        .build())
            .exchange();
  }

  @Entao("retorne uma lista contendo apenas alunos do gênero filtrado")
  public void retorneUmaListaDeAlunosDoGeneroFiltrado() {
    responseSpec.expectStatus().isOk();
    responseSpec
        .expectBody()
        .jsonPath("$.content.length()")
        .isEqualTo(1)
        .jsonPath("$.content[0].genero")
        .isEqualTo(this.generoFiltro.name())
        .jsonPath("$.content[0].genero")
        .value(Matchers.not(Genero.MASCULINO.name()));
  }

  @Dado("que o banco de dados possua alunos e passo o limite e a paginacao")
  public void bancoDadosTodosOsAlunos() {
    AlunoDto alunoDto =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L));
    AlunoDto alunoDto2 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L));
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto)
            .exchange();
    responseSpec.expectStatus().isCreated();
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto2)
            .exchange();
    responseSpec.expectStatus().isCreated();

    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @Quando("eu faco uma requisicao GET para alunos")
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

  @Entao("retorne uma lista somente com os alunos que estao dentro do que pediram")
  public void respostaTodosOsAlunos() {
    var pageAlunos =
        responseSpec
            .expectStatus()
            .isOk()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();
    Assertions.assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");
    Assertions.assertEquals(
        this.limite,
        pageAlunos.getContent().size(),
        "O número de alunos retornados deve ser igual ao limite da página.");
    Assertions.assertNotNull(pageAlunos.getPage(), "Os metadados da página não devem ser nulos.");
    Assertions.assertEquals(
        this.limite, pageAlunos.getPage().size(), "O tamanho da página deve ser igual ao limite.");
    Assertions.assertEquals(
        this.pagina, pageAlunos.getPage().number(), "O número da página deve ser zero.");
    Assertions.assertEquals(
        2L, pageAlunos.getPage().totalElements(), "O total de elementos deve ser 2.");
    Assertions.assertEquals(1, pageAlunos.getPage().totalPages(), "O total de páginas deve ser 1.");
  }

  private final String paginaInvalida = "abc";
  private final String limiteInvalido = "xyz";

  @Dado("que eu passe alunos no banco")
  public void bancoDadosTodosOsAlunos_Erro400() {

    Aluno aluno2 =
        new Aluno(null, gerarNomeAleatorio(), Genero.MASCULINO, LocalDate.now().minusYears(4L));
    Aluno aluno3 =
        new Aluno(null, gerarNomeAleatorio(), Genero.FEMININO, LocalDate.now().minusYears(5L));

    alunoRepository.save(aluno2);
    alunoRepository.save(aluno3);
  }

  @Quando("eu procuro os alunos com parametros de listagem invalidos")
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

  @Entao("retorne erro de formatação")
  public void respostaTodosOsAlunos_ComParametrosInvalidos_Erro400() {
    responseSpec.expectStatus().isBadRequest();
    System.out.println("Erro 400 - Parametros inválidos");
  }

  @Dado("que eu passe o id do aluno")
  public void bancoDeletarAluno_204() {
    System.out.println("Aluno com ID: " + aluno.getId());
  }

  @Quando("eu tento deletar o aluno do id passado")
  public void requisicaoDeletarAluno_204() {
    responseSpec = webTestClient.delete().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  @E("quando eu tentar procurar o aluno atraves do id novamente")
  public void novarequisicaoDeletarAluno_204() {
    responseSpec = webTestClient.get().uri("/alunos/{id}", aluno.getId()).exchange();
  }

  @Entao(
      "como ele não deve mais existir no banco tem que retornar que nao conseguiu encontrar o recurso solicitado")
  public void respostaDeletarAluno_204() {
    responseSpec.expectStatus().isNotFound();
  }

  @Dado("que eu passe um id inexistente do aluno")
  public void bancoDeletarAluno_Erro404() {
    System.out.println("Aluno com ID: " + alunoIdInexistente.getId());
  }

  @Quando("eu tento deletar o aluno com o id inexistente")
  public void requisicaoDeletarAluno_Erro404() {
    responseSpec =
        webTestClient.delete().uri("/alunos/{id}", alunoIdInexistente.getId()).exchange();
  }

  @Dado("que o aluno esteja no banco de dados e eu passe o id do aluno e novos dados")
  public void salvarAlunoNoBanco_Retornar204() {
    AlunoDto alunoTest = new AlunoDto();
    alunoTest.setNome(gerarNomeAleatorio());
    alunoTest.setSobrenome(gerarNomeAleatorio());
    alunoTest.setDataNascimento(LocalDate.now().minusYears(4L));
    alunoTest.setGenero(Genero.FEMININO);
    AlunoDto alunoCriado =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoTest)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();
    Assertions.assertNotNull(alunoCriado, "Aluno nulo na criação");
    integrationTestsContext.setAlunoCriadoBanco(alunoCriado);
    System.out.println("ID do Aluno obtido da resposta: " + alunoCriado.getId());
  }

  @Quando("eu tento atualizar o aluno passado")
  public void requisicaoAlterarAluno_Retornar204() {
    String novoNome = gerarNomeAleatorio();
    String novoSobrenome = gerarNomeAleatorio();

    AlunoDto alunoModificado = new AlunoDto();
    alunoModificado.setNome(novoNome);
    alunoModificado.setSobrenome(novoSobrenome);
    alunoModificado.setGenero(null);
    alunoModificado.setDataNascimento(null);

    AlunoDto alunoDto = integrationTestsContext.getAlunoCriadoBanco();
    Assertions.assertNotNull(alunoDto);
    System.out.println("Aluno do Id: " + alunoDto.getId());

    webTestClient
        .patch()
        .uri("/alunos/{id}", alunoDto.getId())
        .bodyValue(alunoModificado)
        .exchange()
        .expectStatus()
        .isNoContent();

    integrationTestsContext.setAlunoRecuperadoDoBanco(alunoDto);

    Assertions.assertNotNull(alunoDto);
  }

  @Entao("retonar solicitação com sucesso")
  public void respostaAlterarAluno_Retornar204() {
    AlunoDto alunoComNovosDadosEsperados = integrationTestsContext.getAlunoRecuperadoDoBanco();
    Assertions.assertNotNull(alunoComNovosDadosEsperados, "Dados esperados não encontrados.");

    AlunoDto alunoDoBancoAposPatch =
        webTestClient
            .get()
            .uri("/alunos/{id}", alunoComNovosDadosEsperados.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();
    ;
    Assertions.assertNotNull(alunoDoBancoAposPatch, "Aluno não encontrado após o PATCH.");

    Assertions.assertEquals(
        alunoComNovosDadosEsperados.getSobrenome(),
        alunoDoBancoAposPatch.getSobrenome(),
        "O sobrenome não foi atualizado corretamente.");
  }

  @Dado("que eu passe um id inexistente do aluno e novos dados")
  public void bancoAlterarAluno_Retornar404() {
    System.out.println("Aluno com id:  " + alunoIdInexistente);
  }

  @Quando("eu tento atualizar o aluno com id inexistente")
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
            .uri("/alunos/{id}", alunoIdInexistente.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto1)
            .exchange();
  }
}
