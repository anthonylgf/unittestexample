# language: pt
@spring.profiles.active=test
Funcionalidade: Testes de Cadastro com kafka
  Cenario: Cadastrar aluno e retornar sucesso
    Dado que o banco de dados está vazio
    Quando eu tento criar um aluno
    Entao o aluno tem que ser Criado no banco
    E o aluno deve existir no banco de dados

  Cenario: Cadastrar aluno retornar erro por causa do nome invalido
    Dado que o banco de dados está vazio
    Quando eu tento criar um aluno com nome invalido
    Entao a resposta deve ser um erro de formatação

  Cenario: Cadastrar aluno retornar erro por causa de nome repetido
    Dado que o banco de dados esteja com um aluno salvo
    Quando  eu tento criar um aluno com o mesmo nome
    Entao a resposta deve ser um erro de formatação
