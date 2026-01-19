# language: pt
Funcionalidade: Testes de Cadastro com kafka
  @kafka
  Cenario: Cadastrar aluno e retornar sucesso
    Dado que o banco de dados está vazio
    Quando eu tento criar um aluno
    E a mensagem foi enviada para o tópico com as informações dos alunos
    Entao o aluno tem que ser Criado no banco

  @kafka
  Cenario: Cadastrar aluno retornar erro por causa do nome invalido
     Dado que o banco de dados está vazio
    Quando eu tento criar um aluno com nome invalido
    E a mensagem não foi enviada para o tópico com as informações dos alunos
    Entao a resposta deve ser um erro de formatação

  @kafka
  Cenario: Cadastrar aluno retornar erro por causa de nome repetido
    Dado que o banco de dados esteja com um aluno salvo
    Quando  eu tento criar um aluno com o mesmo nome
    E a mensagem não foi enviada para o tópico com as informações dos alunos
    Entao a resposta deve ser um erro de formatação
