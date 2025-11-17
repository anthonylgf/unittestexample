# language: pt

Funcionalidade: Requisicoes do aluno

  Cenario: Cadastrar aluno retornae sucesso
     Dado que o banco de dados está vazio
     Quando eu faco uma requisicao POST para aluno com dados validos
     Entao o codigo de resposta deve ser 201
     E o aluno deve existir no banco de dados

 Cenario: Cadastrar aluno retornar erro 400 (nome invalido)
     Dado que o banco de dados está vazio
     Quando eu faco uma requisicao POST para aluno com nome invalido
     Entao o codigo de resposta deve ser 400

  Cenario: Cadastrar aluno retornar erro 400 (nome repetido)
     Dado que o banco de dados esteja com um aluno salvo
     Quando eu faco uma requisicao POST para aluno com o mesmo nome
     Entao o codigo de resposta deve ser 400

 Cenario: Procurar aluno pelo id
     Dado que o banco de dados não está vazio
     Quando eu faco uma requisicao GET para aluno atraves do id
     Entao retorna sucesso

 Cenario: Procurar aluno pelo id retornar erro 404
     Dado que eu passo um id inexistente
     Quando eu faco uma requisicao GET para aluno com id inexistente
     Entao retorna erro 404

  Cenário: Filtro de idade
    Dado que o banco de dados possua alunos
    E eu configuro o filtro de idade mínima e máxima
    Quando eu faco uma nova requisicao GET para alunos
    Então retorne uma lista de alunos

  Cenário: Filtro por idade mínima maior que a máxima retornar erro
    Dado que o banco de dados possua alunos
    Quando eu faco uma nova requisicao GET para alunos com parametros Idade maxima e minima
    Então retorna erro 400

  Cenário: Filtro de idade inválido (idade máxima menor que a mínima)
    Dado que o banco de dados possua alunos com idades abaixo do minimo e acima do maximo
    Quando eu faco uma nova requisicao GET para alunos com parametros de Idade
    Então deve retornar nenhum aluno

  Cenário: Teste rapido do filtro por genero FEMININO
    Dado que o banco de dados possua alunos de diferentes gêneros
    E eu filtro por gênero "FEMININO"
    Quando eu faco uma requisicao GET para alunos com o filtro de genero
    Então retorne uma lista contendo apenas alunos do gênero filtrado

  Cenário: Filtro por nome completo
    Dado que o banco de dados possua alunos
    Quando eu faco uma nova requisicao GET para alunos
    Então retorne uma lista de alunos

 Cenario: Listar alunos
     Dado que o banco de dados possua alunos e passo o limite e a paginacao
     Quando eu faco uma requisicao GET para alunos
     Entao retorne uma lista de alunos

 Cenario: Listar alunos com parametros de listagem invalidos
     Dado que eu passe alunos no banco
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
     Dado que o aluno esteja no banco de dados e eu passe o id do aluno e novos dados
     Quando eu faco uma requisicao PATCH para o id do aluno
     Entao e tem que retornar 204

 Cenario: Alterar aluno pelo id inexistente
     Dado que eu passe um id inexistente do aluno e novos dados
     Quando eu faco  uma requisicao PATCH para o id do aluno inexistente
     Entao e tem que retornar 404
