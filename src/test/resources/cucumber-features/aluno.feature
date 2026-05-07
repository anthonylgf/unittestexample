# language: pt
Funcionalidade: CRUD de Alunos

  Cenario: Procurar aluno pelo id
    Dado que o banco de dados possua um aluno
    Quando eu tendo procurar um aluno atraves do id
    Entao retorne o aluno procurado

  Cenario: Procurar aluno pelo id retornar erro 404
    Dado que o banco de dados nao contenha nenhum aluno
    Entao deve dar erro ao procurar aluno com id inexistente

  Cenário: Filtro de idade
    Dado que o banco de dados possua diversos alunos
    E eu configuro o filtro de idade mínima e máxima
    Quando eu procurar 10 alunos com os parametros de idade válida na página 0
    Então retorne uma lista com apenas os alunos com as idades correspondentes

  Cenário: Teste rapido do filtro por genero FEMININO
    Dado que o banco de dados possua alunos de diferentes gêneros
    E eu filtro por gênero "FEMININO"
    Quando eu procuro os alunos com o filtro de genero
    Então retorne uma lista contendo apenas alunos do gênero filtrado

  Cenário: Filtro por nome completo
    Dado que o banco de dados possua diversos alunos
    E eu configuro o filtro de de nome completo
    Quando eu procurar os alunos utilizando o filtro de nome completo
    Então retorne uma lista com apenas os alunos com os nomes completos correspondentes

  Cenario: Listar alunos sem filtros
    Dado que o banco de dados possua diversos alunos
    Quando eu procurar os alunos sem utilizar filtros
    Entao retorne uma lista com todos os alunos

  Cenario: Deletar aluno pelo id
    Dado que o banco de dados possua um aluno
    Quando eu deleto o aluno através do endpoint
    Entao entao nao devo encontrar o aluno ao realizar o GET

  Cenario: Deletar aluno pelo id Inexistente
    Dado que o banco de dados nao contenha nenhum aluno
    Entao deve retornar erro ao tentar deletar aluno com id inexistente

  Cenario: Alterar aluno pelo id
    Dado que o banco de dados possua um aluno
    Quando eu tento atualizar o aluno passado
    Entao retonar solicitação com sucesso

  Cenario: Alterar aluno pelo id inexistente
    Dado que o banco de dados nao contenha nenhum aluno
    Entao deve ocorrer um erro ao tentar atualizar o aluno com id inexistente
