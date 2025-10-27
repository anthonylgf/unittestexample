# language: pt

Funcionalidade: Requisicoes do aluno

  Cenario: Cadastrar aluno retornae sucesso
     Dado que o banco de dados está vazio
     Quando eu faco uma requisicao POST para aluno com dados validos
     Entao o codigo de resposta deve ser 204
     E o aluno deve existir no banco de dados

 Cenario: Cadastrar aluno retornar erro 400 (dados invalidos)
     Dado que o banco de dados está vazio
     Quando eu faco uma requisicao POST para aluno com dados invalidos
     Entao o codigo de resposta deve ser 400

 Cenario: Procurar aluno pelo id
     Dado que o banco de dados não está vazio
     Quando eu faco uma requisicao GET para aluno atraves do id
     Entao retorna os dados do aluno


 Cenario: Procurar aluno pelo id retornar erro 404
     Dado que eu passo um id inexistente
     Quando eu faco uma requisicao GET para aluno com id inexistente
     Entao retorna erro 404


 Cenario: Listar alunos
     Dado que eu passe 2 alunos
     Quando eu faco uma requisicao GET para alunos
     Entao retorne uma lista de alunos




