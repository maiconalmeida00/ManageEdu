# ManageEdu

Backend de um CRM educacional para gerenciar cursos, leads/candidatos e oportunidades de matrícula.

Atualmente, o sistema possui uma interface de linha de comando (CLI), é executado como uma aplicação Spring Boot e mantém os dados apenas em memória durante a execução.

## Tecnologias

- Java 21
- Spring Boot 3.4.5
- Maven
- JUnit 5 para os testes

## Arquitetura

O projeto é organizado em camadas, seguindo uma abordagem próxima de Clean Architecture/Arquitetura Hexagonal:

```text
adapters/cli       -> entrada e interação com o usuário
application/       -> casos de uso e DTOs
domain/            -> regras de negócio e contratos
infrastructure/   -> configuração e implementações técnicas
```

### `adapters/cli`

Contém a interface de linha de comando. O `CliRunner` inicia o menu depois que o contexto do Spring é carregado. O `MenuPrincipal` encaminha as opções para os menus especializados:

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

É o núcleo do sistema. Contém:

- modelos como `Curso`, `LeadCandidato`, `OportunidadeMatricula` e `Usuario`;
- papéis `Administrador` e `OperadorCaptacao`;
- enums de modalidade, turno e status;
- exceções de negócio e de recurso não encontrado;
- interfaces dos repositórios.

As regras mais importantes ficam no domínio. Por exemplo, uma oportunidade sempre começa em `NOVO_LEAD`, normaliza a observação e valida o limite de 500 caracteres. A enumeração `StatusOportunidade` define as transições permitidas do funil.

### `infrastructure`

`UseCaseConfig` configura os beans do Spring e conecta as interfaces de repositório às implementações em memória:

- `InMemoryCursoRepository`;
- `InMemoryLeadRepository`;
- `InMemoryOportunidadeRepository`.

Os repositórios usam `LinkedHashMap` e geram IDs sequenciais. Como não existe banco de dados configurado, todos os dados são perdidos quando a aplicação é encerrada.

## Fluxo do sistema desde o início

### 1. Inicialização

1. A classe `ManageEduApplication` executa `SpringApplication.run(...)`.
2. O Spring procura componentes e configurações no pacote `com.enterprise.manageedu`.
3. `UseCaseConfig` cria os repositórios em memória, os casos de uso e os usuários padrão:
   - Administrador: `Ana Administradora`;
   - Operador de captação: `Carlos Captacao`.
4. O Spring cria o `CliRunner`. Ele é habilitado por padrão e pode ser desabilitado com `manageedu.cli.enabled=false`.
5. Depois da inicialização do contexto, o Spring chama `CliRunner.run(...)`.
6. O runner cria o `MenuPrincipal`, injeta os casos de uso e inicia o loop interativo.

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
./mvnw spring-boot:run
```

No Windows PowerShell, também é possível usar:

```powershell
.\mvnw.cmd spring-boot:run
```

Para executar os testes:

```powershell
./mvnw test
```

## Testes

Os testes em `src/test/java` verificam, entre outros cenários:

- criação e validação de cursos;
- criação de leads e validação de e-mail;
- rejeição de referências inexistentes;
- criação de oportunidades com status inicial;
- transições válidas e inválidas do funil;
- permissões de administrador e operador;
- operações CRUD dos repositórios em memória.

## Limitações e próximos passos

- Os dados não são persistidos após o encerramento do processo.
- A entrada atual é exclusivamente via CLI; não há controllers REST ou frontend integrado neste módulo.
- Não há autenticação real: a aplicação alterna entre dois usuários padrão.
- Para produção, o próximo passo natural é substituir as implementações `InMemory*Repository` por uma persistência real, mantendo as interfaces do domínio, e adicionar uma camada de API para consumo externo.