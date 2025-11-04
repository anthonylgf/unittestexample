# language: pt

Funcionalidade: Requisicoes do aluno

  Cenario: Cadastrar aluno retornae sucesso
     Dado que o banco de dados está vazio
     Quando eu faco uma requisicao POST para aluno com dados validos
     Entao o codigo de resposta deve ser 201
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

 Cenario: Listar alunos com parametros de listagem invalidos
     Dado que eu passe 2 alunos no banco
     Quando eu faco uma requisicao GET para alunos com parametros de listagem invalidos
     Entao retorne erro 400

 Cenario: Deletar aluno pelo id
     Dado que eu passe o id do aluno
     Quando eu faco uma requisicao DEL para o id do aluno
     E eu faco uma requisicao GET para aluno atraves do id novamente
     Entao e tem que retornar 404

 Cenario: Deletar aluno pelo id Inexistente
     Dado que eu passe um id inexistente do aluno
     Quando eu faco uma requisicao DEL para o id inexistente do aluno
     E eu faco uma nova requisicao GET para aluno atraves do id inexistente novamente
     Entao e tem que retornar 404

 Cenario: Alterar aluno pelo id
     Dado que o aluno esteja no banco de dados
     E que eu passe o id do aluno e novos dados
     Quando eu faco uma requisicao PATCH para o id do aluno
     Entao e tem que retornar 204

 Cenario: Alterar aluno pelo id inexistente
     Dado que eu passe um id inexistente do aluno e novos dados
     Quando eu faco  uma requisicao PATCH para o id do aluno inexistente
     Entao e tem que retornar 404






