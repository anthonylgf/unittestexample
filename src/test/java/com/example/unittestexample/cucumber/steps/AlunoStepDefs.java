package com.example.unittestexample.cucumber.steps;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import com.example.unittestexample.cucumber.context.IntegrationTestsContext;
import com.example.unittestexample.cucumber.models.PaginaAlunos;
import com.example.unittestexample.cucumber.subscriber.AlunoSubscriber;
import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.dtos.AlunoFilters;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.fixtures.AlunoFixture;
import com.example.unittestexample.fixtures.TurmaFixture;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.models.Turma;
import com.example.unittestexample.repositories.AlunoRepository;
import com.example.unittestexample.repositories.TurmaRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.es.Dado;
import io.cucumber.java.it.Quando;
import io.cucumber.java.pt.Entao;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("integration-test")
@RequiredArgsConstructor
@SpringBootTest
public class AlunoStepDefs {

  private final AlunoRepository alunoRepository;

  private final TurmaRepository turmaRepository;

  private final WebTestClient webTestClient;

  private final IntegrationTestsContext integrationTestsContext;

  private WebTestClient.ResponseSpec responseSpec;

  @Before
  @Transactional
  public void limparBaseCriarTurma() {
    alunoRepository.deleteAll();
    turmaRepository.deleteAll();
    System.out.println("Base de dados limpa com sucesso.");

    Turma turma =
        TurmaFixture.build(
            t -> {
              t.setId(null);
              t.setNome(gerarNomeAleatorio());
            });
    Turma turmaCriadaBanco = turmaRepository.save(turma);
    System.out.println("Turma criada com sucesso.");

    integrationTestsContext.setTurmaCriadaBanco(turmaCriadaBanco);
    integrationTestsContext.getAlunoSubscriber().setMensagensRecebidas(new ArrayList<>());
    assertEquals(0, alunoRepository.findAll().size());
  }

  @After
  @Transactional
  public void limparBase() {
    alunoRepository.deleteAll();
    turmaRepository.deleteAll();
    System.out.println("Base de dados limpa com sucesso.");
  }

  @Autowired KafkaListenerEndpointRegistry registry;

  @Before("@kafka")
  public void waitForKafka() {
    integrationTestsContext.getAlunoSubscriber().getMensagensRecebidas().clear();
  }

  private String gerarNomeAleatorio() {
    return UUID.randomUUID().toString().replace("-", "").replaceAll("[0-9]", "a").toUpperCase();
  }

  @Dado("que o banco de dados nao contenha nenhum aluno")
  public void bancoDados() {
    alunoRepository.deleteAll();
    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }
  }

  @Quando("eu tento criar um aluno")
  public void requisicaoCadastrar201() {

    String nome = gerarNomeAleatorio();
    String sobrenome = gerarNomeAleatorio();
    AlunoDto alunoTest =
        new AlunoDto(
            null,
            nome,
            sobrenome,
            Genero.FEMININO,
            LocalDate.now().minusYears(4L),
            integrationTestsContext.getTurmaCriadaBanco().getId());
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoTest)
            .exchange();

    var resultado =
        responseSpec
            .expectStatus()
            .isCreated()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();

    System.out.println("ID do aluno criado: " + resultado.getId());

    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @E("a mensagem foi enviada para o tópico com as informações dos alunos")
  public void mensagemNoKafka() throws InterruptedException {
    AlunoSubscriber alunoSubscriber = integrationTestsContext.getAlunoSubscriber();
    await()
        .atMost(30, SECONDS)
        .untilAsserted(
            () -> {
              List<String> conteudo = alunoSubscriber.getMensagensRecebidas();
              assertFalse(conteudo.isEmpty(), "A lista de mensagens do Kafka está vazia!");
            });
    alunoSubscriber.getMensagensRecebidas().clear();
  }

  @Entao("o aluno tem que ser Criado no banco")
  public void respostaCadastrar201() {
    responseSpec.expectStatus().isCreated();

    List<Aluno> alunosSalvo = alunoRepository.findAll();
    assertEquals(1, alunosSalvo.size(), "Deve existir 1 aluno salvo no banco ");

    System.out.println("Aluno cadastrado com sucesso");
  }

  @Quando("eu tento criar um aluno com nome invalido")
  public void requisicaoCadastrar400() {
    Aluno aluno400 =
        new Aluno(
            1L,
            "Karine Ferreira1",
            Genero.FEMININO,
            LocalDate.now().minusYears(4L),
            integrationTestsContext.getTurmaCriadaBanco());
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(aluno400)
            .exchange();
  }

  @E("a mensagem não foi enviada para o tópico com as informações dos alunos")
  public void mensagemNaoEnviadaNoKafka() throws InterruptedException {

    Thread.sleep(2000);
    assertTrue(
        integrationTestsContext.getAlunoSubscriber().getMensagensRecebidas().isEmpty(),
        "Erro: Uma mensagem foi enviada para o Kafka, mas o cadastro deveria ter falhado!");
  }

  @Quando("eu tento criar um aluno com o mesmo nome")
  public void requisicaoCadastrarNomeDuplicado() {
    AlunoDto alunoCriadoBanco = integrationTestsContext.getAlunoCriadoBanco();
    assertNotNull(alunoCriadoBanco, "O aluno deve ter sido criado no banco");

    webTestClient
        .post()
        .uri("/alunos")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(alunoCriadoBanco)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Dado("que o banco de dados possua um aluno")
  public void bancoDadosProcurarId() {
    AlunoDto alunoSalvar = new AlunoDto();
    alunoSalvar.setNome(gerarNomeAleatorio());
    alunoSalvar.setSobrenome(gerarNomeAleatorio());
    alunoSalvar.setDataNascimento(LocalDate.now().minusYears(4L));
    alunoSalvar.setGenero(Genero.FEMININO);
    alunoSalvar.setTurmaId(integrationTestsContext.getTurmaCriadaBanco().getId());
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
    assertNotNull(alunoCriado, "Aluno nulo na criação");
    integrationTestsContext.setAlunoCriadoBanco(alunoCriado);
    System.out.println("ID do Aluno obtido da resposta: " + alunoCriado.getId());
  }

  @Quando("eu tendo procurar um aluno atraves do id")
  public void requisicaoProcurarId() {
    AlunoDto alunoDto = integrationTestsContext.getAlunoCriadoBanco();
    assertNotNull(alunoDto);
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
    assertNotNull(alunoRecuperado);
    integrationTestsContext.setAlunoRecuperadoDoBanco(alunoRecuperado);
  }

  @Entao("retorne o aluno procurado")
  public void respostaProcurarId() {
    AlunoDto alunoCriado = integrationTestsContext.getAlunoCriadoBanco();
    AlunoDto alunoRecuperado = integrationTestsContext.getAlunoRecuperadoDoBanco();
    assertEquals(alunoCriado, alunoRecuperado);
  }

  @Entao("deve dar erro ao procurar aluno com id inexistente")
  public void requisicaoProcurarId_Retornar404() {
    Long id = 10L;

    webTestClient.get().uri("/alunos/{id}", id).exchange().expectStatus().isNotFound();
  }

  @Dado("que o banco de dados possua diversos alunos")
  public void bancoDeDadosPossuaAlunos() {
    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }

    AlunoDto alunoDto1 =
        AlunoFixture.buildDto(
            a -> {
              a.setId(null);
              a.setGenero(Genero.FEMININO);
              a.setDataNascimento(LocalDate.now().minusYears(4));
              a.setTurmaId(integrationTestsContext.getTurmaCriadaBanco().getId());
            });

    AlunoDto alunoDto2 =
        AlunoFixture.buildDto(
            a -> {
              a.setId(null);
              a.setGenero(Genero.FEMININO);
              a.setDataNascimento(LocalDate.now().minusYears(7));
              a.setTurmaId(integrationTestsContext.getTurmaCriadaBanco().getId());
            });

    AlunoDto alunoBanco1 =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto1)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();

    AlunoDto alunoBanco2 =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDto2)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlunoDto.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(alunoBanco1);
    assertNotNull(alunoBanco2);

    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);

    integrationTestsContext.setAlunosCriadosNoBanco(List.of(alunoBanco1, alunoBanco2));
  }

  @E("eu configuro o filtro de idade mínima e máxima")
  public void configurarFiltroIdade() {
    var alunoFilter = new AlunoFilters();
    alunoFilter.setIdadeMinima(3);
    alunoFilter.setIdadeMaxima(7);
    integrationTestsContext.setAlunoFilters(alunoFilter);
  }

  @E("eu configuro o filtro de de nome completo")
  public void configurarFiltroNomeCompleto() {
    Assertions.assertNotNull(
        integrationTestsContext.getAlunosCriadosNoBanco(), "Deve ter alunos criados no banco");

    AlunoDto alunoCriadoNoBanco = integrationTestsContext.getAlunosCriadosNoBanco().getFirst();
    String nomeCompleto = alunoCriadoNoBanco.getNome() + " " + alunoCriadoNoBanco.getSobrenome();

    var alunoFilter = new AlunoFilters();
    alunoFilter.setNomeCompleto(nomeCompleto);

    integrationTestsContext.setAlunoFilters(alunoFilter);
  }

  @Quando("eu procurar os alunos utilizando o filtro de nome completo")
  public void recuperarAlunosPeloNome() {
    String nomeCompleto = integrationTestsContext.getAlunoFilters().getNomeCompleto();

    PaginaAlunos paginaAlunos =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("nomeCompleto", nomeCompleto)
                        .queryParam("pagina", 0)
                        .queryParam("limite", 50)
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();

    Assertions.assertNotNull(paginaAlunos);
    integrationTestsContext.setPaginaAlunos(paginaAlunos);
  }

  @Quando("eu procurar os alunos sem utilizar filtros")
  public void requisicaoGetFiltroNomeCompleto_RetornarSucesso() {
    List<AlunoDto> alunosCriadosNoBanco = integrationTestsContext.getAlunosCriadosNoBanco();

    PaginaAlunos paginaAlunos =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", 0)
                        .queryParam("limite", alunosCriadosNoBanco.size())
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(paginaAlunos);
    integrationTestsContext.setPaginaAlunos(paginaAlunos);
  }

  @Quando("eu procurar {} alunos com os parametros de idade válida na página {}")
  public void requisicaoGetFiltroIdadesValidos(Integer quantidadeAlunos, Integer pagina) {
    Assertions.assertNotNull(quantidadeAlunos, "A quantidade de alunos deve ser informada");
    Assertions.assertNotNull(pagina, "A página deve ser informada");

    PaginaAlunos alunosRetornados =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", pagina)
                        .queryParam("limite", quantidadeAlunos)
                        .queryParam(
                            "IdadeMinima",
                            integrationTestsContext.getAlunoFilters().getIdadeMinima())
                        .queryParam(
                            "IdadeMaxima",
                            integrationTestsContext.getAlunoFilters().getIdadeMaxima())
                        .build())
            .exchange()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();

    integrationTestsContext.setPaginaAlunos(alunosRetornados);

    assertNotNull(alunosRetornados, "A pagina de alunos não deve ser nula");
    assertEquals(
        quantidadeAlunos,
        alunosRetornados.getPage().getSize(),
        "O tamanho da página deve ser igual ao limite");
    assertEquals(
        pagina, alunosRetornados.getPage().getNumber(), "A página retornada deve ser a solicitada");
  }

  @Dado("que o banco de dados possua alunos de diferentes gêneros")
  public void bancoDeDadosPossuaAlunosComGenerosDiferentes() {
    System.out.println("Limpando o banco...");
    alunoRepository.deleteAll();

    if (!alunoRepository.findAll().isEmpty()) {
      throw new IllegalStateException("O banco de dados não foi limpo totalmente");
    }
    System.out.println("Banco limpo!");
    AlunoDto alunoDtoTest1 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.FEMININO,
            LocalDate.now().minusYears(4L),
            integrationTestsContext.getTurmaCriadaBanco().getId());
    AlunoDto alunoDtoTest2 =
        new AlunoDto(
            null,
            gerarNomeAleatorio(),
            gerarNomeAleatorio(),
            Genero.MASCULINO,
            LocalDate.now().minusYears(6L),
            integrationTestsContext.getTurmaCriadaBanco().getId());

    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest1)
            .exchange()
            .expectStatus()
            .isCreated();
    ;
    responseSpec =
        webTestClient
            .post()
            .uri("/alunos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(alunoDtoTest2)
            .exchange()
            .expectStatus()
            .isCreated();
    long count = alunoRepository.count();
    System.out.println("Total de alunos após setup: " + count);
  }

  @E("eu filtro por gênero {string}")
  public void filtroGenero(String genero) {
    var generoFiltro = Genero.valueOf(genero.toUpperCase());

    var alunoFilter = new AlunoFilters();
    alunoFilter.setGenero(generoFiltro);

    integrationTestsContext.setAlunoFilters(alunoFilter);
  }

  @Quando("eu procuro os alunos com o filtro de genero")
  public void requisicaoGetFiltroGenero() {
    var alunoFilter = integrationTestsContext.getAlunoFilters();

    PaginaAlunos paginaAlunos =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/alunos")
                        .queryParam("pagina", 0)
                        .queryParam("limite", 30)
                        .queryParam("genero", alunoFilter.getGenero().name())
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PaginaAlunos.class)
            .returnResult()
            .getResponseBody();

    assertNotNull(paginaAlunos);
    integrationTestsContext.setPaginaAlunos(paginaAlunos);
  }

  @Entao("retorne uma lista contendo apenas alunos do gênero filtrado")
  public void retorneUmaListaDeAlunosDoGeneroFiltrado() {
    var pageAlunos = integrationTestsContext.getPaginaAlunos();
    assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");
    assertNotNull(pageAlunos.getContent(), "A lista de alunos nao deve ser nula");

    var alunoFilter = integrationTestsContext.getAlunoFilters();

    for (AlunoDto alunoRetornado : pageAlunos.getContent()) {
      assertEquals(
          alunoFilter.getGenero(),
          alunoRetornado.getGenero(),
          "O gênero do aluno filtrado deve ser " + alunoFilter.getGenero().name());
    }
  }

  @Entao("retorne uma lista com apenas os alunos com as idades correspondentes")
  public void respostaIdadesCorrespondentes() {
    var pageAlunos = integrationTestsContext.getPaginaAlunos();

    assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");
    assertNotNull(pageAlunos.getPage(), "Os metadados da página não devem ser nulos.");
    assertNotNull(pageAlunos.getContent(), "A lista de alunos nao deve ser nula");

    var filters = integrationTestsContext.getAlunoFilters();
    var dataAtual = LocalDate.now();

    for (AlunoDto alunoRetornado : pageAlunos.getContent()) {
      long idade = ChronoUnit.YEARS.between(alunoRetornado.getDataNascimento(), dataAtual);
      assertTrue(
          idade >= filters.getIdadeMinima(),
          "Aluno com idade menor que a minima solicitada " + idade);
      assertTrue(
          idade <= filters.getIdadeMaxima(),
          "Aluno com idade maior que a maxima solicitada " + idade);
    }
  }

  @Entao("retorne uma lista com apenas os alunos com os nomes completos correspondentes")
  public void respostaNomesCorrespondentes() {
    var pageAlunos = integrationTestsContext.getPaginaAlunos();

    assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");
    assertNotNull(pageAlunos.getPage(), "Os metadados da página não devem ser nulos.");
    assertNotNull(pageAlunos.getContent(), "A lista de alunos nao deve ser nula");

    var filters = integrationTestsContext.getAlunoFilters();

    for (AlunoDto alunoRetornado : pageAlunos.getContent()) {
      var nomeCompleto = alunoRetornado.getNome() + " " + alunoRetornado.getSobrenome();
      assertEquals(
          filters.getNomeCompleto(), nomeCompleto, "Nome do aluno nao corresponde ao solicitado");
    }
  }

  @Entao("retorne uma lista com todos os alunos")
  public void respostaTodosOsAlunos() {
    var pageAlunos = integrationTestsContext.getPaginaAlunos();
    var alunosCriadosNoBanco = integrationTestsContext.getAlunosCriadosNoBanco();

    assertNotNull(pageAlunos, "A lista de alunos não deve ser nula.");
    assertNotNull(pageAlunos.getPage(), "Os metadados da página não devem ser nulos.");
    assertNotNull(pageAlunos.getContent(), "A lista de alunos nao deve ser nula");
    assertEquals(
        pageAlunos.getContent().size(),
        alunosCriadosNoBanco.size(),
        "A quantidade de alunos retornados deve ser igual à quantidade criada no banco");
  }

  @Quando("eu deleto o aluno através do endpoint")
  public void requisicaoDeletarAluno_204() {
    Assertions.assertNotNull(
        integrationTestsContext.getAlunoCriadoBanco(), "Deve existir um aluno no banco de dados");
    Long id = integrationTestsContext.getAlunoCriadoBanco().getId();

    webTestClient.delete().uri("/alunos/{id}", id).exchange().expectStatus().isNoContent();
  }

  @Entao("entao nao devo encontrar o aluno ao realizar o GET")
  public void respostaDeletarAluno_204() {
    Long id = integrationTestsContext.getAlunoCriadoBanco().getId();

    webTestClient.get().uri("/alunos/{id}", id).exchange().expectStatus().isNotFound();
  }

  @Entao("deve retornar erro ao tentar deletar aluno com id inexistente")
  public void requisicaoDeletarAluno_Erro404() {
    Long id = 10L;

    webTestClient.delete().uri("/alunos/{id}", id).exchange().expectStatus().isNotFound();
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
    assertNotNull(alunoDto);
    System.out.println("Aluno do Id: " + alunoDto.getId());

    webTestClient
        .patch()
        .uri("/alunos/{id}", alunoDto.getId())
        .bodyValue(alunoModificado)
        .exchange()
        .expectStatus()
        .isNoContent();

    AlunoDto esperado = new AlunoDto();
    esperado.setId(alunoDto.getId());
    esperado.setNome(novoNome);
    esperado.setSobrenome(novoSobrenome);

    integrationTestsContext.setAlunoRecuperadoDoBanco(esperado);

    assertNotNull(alunoDto);
  }

  @Entao("retonar solicitação com sucesso")
  public void respostaAlterarAluno_Retornar204() {
    AlunoDto alunoComNovosDadosEsperados = integrationTestsContext.getAlunoRecuperadoDoBanco();
    assertNotNull(alunoComNovosDadosEsperados, "Dados esperados não encontrados.");

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

    assertNotNull(alunoDoBancoAposPatch, "Aluno não encontrado após o PATCH.");

    assertEquals(
        alunoComNovosDadosEsperados.getSobrenome(),
        alunoDoBancoAposPatch.getSobrenome(),
        "O sobrenome não foi atualizado corretamente.");
  }

  @Entao("deve ocorrer um erro ao tentar atualizar o aluno com id inexistente")
  public void requisicaoAlterarAluno_Retornar404() {
    AlunoDto alunoDto1 =
        AlunoFixture.buildDto(
            a -> {
              a.setNome(gerarNomeAleatorio());
              a.setSobrenome(gerarNomeAleatorio());
              a.setDataNascimento(null);
              a.setGenero(null);
              a.setId(null);
              a.setTurmaId(null);
            });

    webTestClient
        .patch()
        .uri("/alunos/{id}", 10L)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(alunoDto1)
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
