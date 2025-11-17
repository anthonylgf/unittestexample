# language: pt

Funcionalidade: Requisicoes do aluno

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

 Cenario: Procurar aluno pelo id
     Dado que o banco de dados não está vazio
     Quando eu tendo procurar um aluno atraves do id
     Entao retorne o aluno procurado

 Cenario: Procurar aluno pelo id retornar erro 404
     Dado que eu passo um id inexistente
     Quando eu tendo procurar um aluno com id inexistente
     Entao retorna que nao conseguiu encontrar o recurso solicitado

  Cenário: Filtro de idade
    Dado que o banco de dados possua alunos
    E eu configuro o filtro de idade mínima e máxima
    Quando eu procurar os alunos
    Então retorne uma lista somente com os alunos que estao dentro do que pediram

  Cenário: Filtro por idade mínima maior que a máxima retornar erro
    Dado que o banco de dados possua alunos
    Quando eu procurar os alunos com parametros Idade maxima e minima
    Então a resposta deve ser um erro de formatação

  Cenário: Filtro de idade inválido (idade máxima menor que a mínima)
    Dado que o banco de dados possua alunos com idades abaixo do minimo e acima do maximo
    Quando eu procurar os alunos com parametros de Idade
    Então deve retornar nenhum aluno

  Cenário: Teste rapido do filtro por genero FEMININO
    Dado que o banco de dados possua alunos de diferentes gêneros
    E eu filtro por gênero "FEMININO"
    Quando eu procuro os alunos com o filtro de genero
    Então retorne uma lista contendo apenas alunos do gênero filtrado

  Cenário: Filtro por nome completo
    Dado que o banco de dados possua alunos
    Quando eu procurar os alunos
    Então retorne uma lista somente com os alunos que estao dentro do que pediram

 Cenario: Listar alunos
     Dado que o banco de dados possua alunos e passo o limite e a paginacao
     Quando eu procurar os alunos
     Entao retorne uma lista somente com os alunos que estao dentro do que pediram

 Cenario: Listar alunos com parametros de listagem invalidos
     Dado que eu passe alunos no banco
     Quando eu procuro os alunos com parametros de listagem invalidos
     Entao retorne erro de formatação

 Cenario: Deletar aluno pelo id
     Dado que eu passe o id do aluno
     Quando eu tento deletar o aluno do id passado
     E quando eu tentar procurar o aluno atraves do id novamente
     Entao como ele não deve mais existir no banco tem que retornar que nao conseguiu encontrar o recurso solicitado

 Cenario: Deletar aluno pelo id Inexistente
     Dado que eu passe um id inexistente do aluno
     Quando eu tento deletar o aluno com o id inexistente
     Entao retorna que nao conseguiu encontrar o recurso solicitado

  Cenario: Alterar aluno pelo id
     Dado que o aluno esteja no banco de dados e eu passe o id do aluno e novos dados
     Quando eu tento atualizar o aluno passado
     Entao retonar solicitação com sucesso

 Cenario: Alterar aluno pelo id inexistente
     Dado que eu passe um id inexistente do aluno e novos dados
     Quando eu tento atualizar o aluno com id inexistente
     Entao retorna que nao conseguiu encontrar o recurso solicitado
