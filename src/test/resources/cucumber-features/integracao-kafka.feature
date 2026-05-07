# language: pt
Funcionalidade: Teste de Integracao com Kafka
  @kafka
  Cenario: Cadastrar aluno e retornar sucesso
    Dado que o banco de dados nao contenha nenhum aluno
    Quando eu tento criar um aluno
    E a mensagem foi enviada para o tópico com as informações dos alunos
    Entao o aluno tem que ser Criado no banco

  @kafka
  Cenario: Cadastrar aluno retornar erro por causa do nome invalido
     Dado que o banco de dados nao contenha nenhum aluno
    Quando eu tento criar um aluno com nome invalido
    Entao a mensagem não foi enviada para o tópico com as informações dos alunos

  @kafka
  Cenario: Cadastrar aluno retornar erro por causa de nome repetido
    Dado que o banco de dados possua um aluno
    E a mensagem foi enviada para o tópico com as informações dos alunos
    Quando  eu tento criar um aluno com o mesmo nome
    Entao a mensagem não foi enviada para o tópico com as informações dos alunos
