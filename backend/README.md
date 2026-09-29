# ManageEdu

Backend de um CRM educacional para gerenciar cursos, leads/candidatos e oportunidades de matrícula.

Atualmente, o sistema possui uma interface de linha de comando (CLI), é executado como uma aplicação Java convencional e mantém os dados apenas em memória durante a execução.

## Tecnologias

- Java 21
- Maven

## Arquitetura

O projeto é organizado em camadas, seguindo uma abordagem próxima de Clean Architecture/Arquitetura Hexagonal:

```text
adapters/cli       -> entrada e interação com o usuário
application/       -> casos de uso e DTOs
domain/            -> regras de negócio e contratos
infrastructure/   -> configuração e implementações técnicas
```

### `adapters/cli`

Contém a interface de linha de comando. O `CliRunner` inicia o menu depois que as dependências são montadas pela aplicação. O `MenuPrincipal` encaminha as opções para os menus especializados:

- `CursoMenu`: cadastro, consulta, atualização e remoção de cursos;
- `LeadMenu`: gerenciamento de leads/candidatos;
- `OportunidadeMenu`: gerenciamento e alteração de status das oportunidades;
- `MenuPrincipal`: alternância de usuário, exibição do funil e encerramento.

Essa camada lê as entradas do teclado, chama os casos de uso e apresenta os resultados ou mensagens de erro. Ela não deve conter as regras centrais do negócio.

### `application`

Representa os casos de uso da aplicação:

- `CursoUseCase`: operações de CRUD de cursos;
- `LeadCandidatoUseCase`: operações de CRUD de leads, validando o curso de interesse;
- `OportunidadeMatriculaUseCase`: criação, consulta, atualização, remoção, filtragem por status e alteração do status das oportunidades.

Os DTOs separam os dados recebidos e devolvidos pela aplicação dos objetos do domínio. Os casos de uso também coordenam as dependências entre entidades: um lead precisa referenciar um curso existente, e uma oportunidade precisa referenciar um lead e um curso existentes.

### `domain`

É o núcleo do sistema. Contém:# ManageEdu

Backend de um CRM educacional para gerenciar cursos, leads/candidatos e oportunidades de matrícula.

Atualmente, o sistema possui uma interface de linha de comando (CLI), é executado como uma aplicação Java convencional e mantém os dados apenas em memória durante a execução.


### `infrastructure`

`UseCaseConfig` realiza a composição manual das dependências e conecta as interfaces de repositório às implementações em memória:

- `InMemoryCursoRepository`;
- `InMemoryLeadRepository`;
- `InMemoryOportunidadeRepository`.

Os repositórios usam `LinkedHashMap` e geram IDs sequenciais. Como não existe banco de dados configurado, todos os dados são perdidos quando a aplicação é encerrada.

## Fluxo do sistema desde o início

### 1. Inicialização

1. A classe `ManageEduApplication` cria uma instância de `UseCaseConfig`.
2. `UseCaseConfig` cria os repositórios em memória, os casos de uso e os usuários padrão:
   - Administrador: `Ana Administradora`;
   - Operador de captação: `Carlos Captacao`.
3. `ManageEduApplication` cria o `CliRunner` e fornece os casos de uso e usuários por construtor.
4. O runner cria o `MenuPrincipal` e inicia o loop interativo.

### 2. Interação com o menu

O menu principal permanece em um loop até o usuário escolher `0`:

```text
MenuPrincipal
  ├─ 1 -> CursoMenu
  ├─ 2 -> LeadMenu
  ├─ 3 -> OportunidadeMenu
  ├─ 4 -> funil agrupado por status
  ├─ 5 -> alterna administrador/operador
  └─ 0 -> encerra a aplicação
```

Cada menu coleta os dados, monta um DTO de requisição e chama o caso de uso correspondente. O caso de uso valida os dados, cria ou altera entidades do domínio, chama um repositório e converte o resultado em DTO de resposta. O menu então imprime o resultado.

### 3. Exemplo: cadastro de curso

```text
usuário
  -> CursoMenu
  -> CursoUseCase.cadastrar
  -> validação de nome, modalidade e turno
  -> criação de Curso
  -> CursoRepository.salvar
  -> InMemoryCursoRepository
  -> CursoResponseDTO
  -> exibição no terminal
```

### 4. Exemplo: criação de lead

1. O usuário informa os dados do lead e o ID do curso de interesse.
2. `LeadMenu` cria um `LeadCandidatoRequestDTO`.
3. `LeadCandidatoUseCase` valida a requisição e procura o curso no `CursoRepository`.
4. Se o curso não existir, é lançada `ResourceNotFoundException`.
5. O caso de uso cria o `LeadCandidato`, salva-o no `LeadRepository` e devolve um `LeadCandidatoResponseDTO`.

### 5. Exemplo: criação e avanço de oportunidade

1. O usuário informa o ID do lead, o ID do curso e uma observação opcional.
2. `OportunidadeMatriculaUseCase` confirma que o lead e o curso existem.
3. `OportunidadeMatricula` é criada com status inicial `NOVO_LEAD` e data de criação.
4. A oportunidade é salva no `OportunidadeRepository`.
5. Para mudar o status, o caso de uso valida o usuário, a permissão do papel e a transição definida no domínio.
6. A oportunidade é salva novamente e o menu exibe o novo estado.

## Funil de matrícula

As transições permitidas são:

```text
NOVO_LEAD -> CONTATO -> DOCUMENTACAO -> MATRICULA_CONFIRMADA
     |            |             |
     +----------> DESISTENCIA <-+
```

Na prática, qualquer estado não final pode avançar para `DESISTENCIA`. `MATRICULA_CONFIRMADA` e `DESISTENCIA` são estados finais e não permitem novas transições.

O operador de captação pode avançar pelas etapas operacionais, mas não pode confirmar a matrícula. O administrador pode executar qualquer transição válida do funil, inclusive `DOCUMENTACAO -> MATRICULA_CONFIRMADA`.

A opção de funil consulta todos os valores de `StatusOportunidade`, busca as oportunidades por status e apresenta a quantidade e os registros de cada grupo.

## Tratamento de erros

- `RegraDeNegocioException`: dados inválidos, IDs inválidos, transições não permitidas ou falta de permissão;
- `ResourceNotFoundException`: curso, lead ou oportunidade não encontrado.

Os menus capturam essas exceções e mostram a mensagem no terminal, mantendo a aplicação em execução para que o usuário possa tentar novamente.

## Como executar

No diretório `backend`:

```powershell
./mvnw compile
java -cp target/classes com.enterprise.manageedu.ManageEduApplication
```

No Windows PowerShell, também é possível usar:

```powershell
.\mvnw.cmd compile
java -cp target\classes com.enterprise.manageedu.ManageEduApplication
```

É necessário utilizar Java 21 para executar a aplicação. Quando o `java` padrão do terminal apontar para outra versão, use o executável do JDK 21 diretamente:

```powershell
& "$env:JAVA_HOME\bin\java.exe" -cp target\classes com.enterprise.manageedu.ManageEduApplication
```

## Limitações e próximos passos

O backend está preparado para ser o núcleo da aplicação, mas ainda não expõe uma API HTTP. A CLI é atualmente o único adaptador de entrada e os dados são mantidos apenas em memória.

Para a Parte 2, o próximo objetivo é disponibilizar este backend para consumo pelo frontend por meio de uma API REST. A evolução será:

1. Criar um adaptador HTTP em `adapters/rest`, mantendo os casos de uso e o domínio independentes da tecnologia web.
2. Definir endpoints para cursos, leads e oportunidades, cobrindo cadastro, consulta, atualização, remoção, filtros por status e alteração de status.
3. Mapear os DTOs de aplicação para contratos JSON estáveis, documentando campos obrigatórios, formatos, enums e códigos HTTP.
4. Padronizar respostas de erro para validações, recursos inexistentes, transições inválidas e falta de permissão.
5. Substituir a composição exclusiva da CLI por uma composição que permita iniciar a API e, se necessário, manter a CLI como outro adaptador.
6. Substituir os repositórios `InMemory*Repository` por uma persistência durável, mantendo as interfaces dos repositórios no domínio.
7. Implementar autenticação e autorização reais antes de expor operações protegidas, especialmente a alteração de status das oportunidades.
8. Adicionar documentação da API, para orientar a integração do frontend.

O frontend da Parte 2 poderá consumir esses endpoints sem acessar diretamente as entidades ou os repositórios. Assim, a arquitetura atual é preservada: o novo adaptador HTTP traduzirá as requisições e respostas, enquanto as regras continuam em `application` e `domain`.