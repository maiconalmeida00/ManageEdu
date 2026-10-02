# ManageEdu — Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36)

Backend de um CRM educacional para gerenciar cursos, leads/candidatos e oportunidades de matrícula por meio de uma interface de terminal (CLI).

> **Status:** protótipo acadêmico funcional. Os dados são mantidos em memória e são perdidos quando a aplicação é encerrada.

## Visão geral

O ManageEdu organiza a captação de interessados e acompanha oportunidades de matrícula em instituições de ensino. A aplicação permite que equipes de captação, atendimento e secretaria acadêmica controlem cursos, leads e o funil de vendas em um ambiente simples e operacional.

## Público-alvo

Equipes de captação, atendimento, secretaria acadêmica e gestores de instituições de ensino que precisam controlar interessados, cursos e oportunidades de matrícula.

## Funcionalidades

- Cadastro, listagem, consulta, atualização e remoção de cursos;
- Cadastro, listagem, consulta, atualização e remoção de leads/candidatos;
- Criação, listagem, consulta, atualização e remoção de oportunidades de matrícula;
- Organização das oportunidades por etapa do funil;
- Alteração de status com validação de transições;
- Perfis de acesso `Administrador` e `OperadorCaptacao`;
- Validação de e-mail, telefone, IDs e relacionamentos entre entidades;
- Bloqueio de exclusões que quebrariam relacionamentos existentes.

## Tecnologias

- Java 21
- Maven
- Interface de terminal/CLI
- Repositórios em memória

## Pré-requisitos

- JDK 21 ou superior;
- PowerShell, Prompt de Comando ou outro terminal compatível;
- Nenhuma instalação global do Maven é necessária: o projeto inclui o Maven Wrapper.

## Como executar

Abra um terminal no diretório `backend`:

### Windows

```powershell
.\mvnw.cmd clean compile
java -cp target\classes com.enterprise.manageedu.ManageEduApplication
```

### Linux/macOS

```bash
./mvnw clean compile
java -cp target/classes com.enterprise.manageedu.ManageEduApplication
```

O comando `compile` gera as classes em `target`. Depois da compilação, a aplicação pode ser iniciada diretamente pelo comando `java` mostrado acima.

## Como usar

Ao iniciar, o sistema abre o menu principal com as opções:

```text
1. Gerenciar cursos
2. Gerenciar leads/candidatos
3. Gerenciar oportunidades de matrícula
4. Exibir funil de matrícula por status
5. Alternar usuário ativo
0. Sair
```

Fluxo sugerido para uma demonstração:

1. Cadastre um curso e anote o ID exibido;
2. Cadastre um lead usando o ID do curso de interesse;
3. Crie uma oportunidade usando os IDs do lead e do curso;
4. Consulte o funil e avance a oportunidade pelas etapas permitidas;
5. Use a opção de alternância para validar as permissões de cada perfil.

Os valores aceitos para os principais campos são exibidos pelo próprio menu. Por exemplo, os status são `NOVO_LEAD`, `CONTATO`, `DOCUMENTACAO`, `MATRICULA_CONFIRMADA` e `DESISTENCIA`.

## Arquitetura

O projeto é organizado em camadas, com a lógica central de negócio separada da interface de usuário:

```text
adapters/cli       -> interação com o usuário via terminal
application/       -> casos de uso e DTOs
infrastructure/    -> composição manual e persistência em memória
domain/            -> entidades, regras de negócio e enumerações
```

### `adapters/cli`

Contém os menus interativos e a leitura de entrada do usuário:

- `CursoMenu`: cadastro, listagem, busca, atualização e remoção de cursos;
- `LeadMenu`: cadastro, listagem, busca, atualização e remoção de leads/candidatos;
- `OportunidadeMenu`: criação, listagem, busca, atualização e alteração de status;
- `MenuPrincipal`: coordenador do menu principal, alternância de usuário e exibição do funil.

### `application`

Responsável pelos casos de uso da aplicação:

- `CursoUseCase`: operações de CRUD com validação de relacionamento;
- `LeadCandidatoUseCase`: cadastro e validação de lead, incluindo curso de interesse;
- `OportunidadeMatriculaUseCase`: criação, atualização, consulta, remoção e transição de status.

### `domain`

Contém o núcleo do sistema:

- `Curso`
- `LeadCandidato`
- `OportunidadeMatricula`
- `StatusOportunidade`
- `Usuario`
- `Administrador`
- `OperadorCaptacao`
- exceções de negócio e regras de transição de status

### `infrastructure`

A composição manual das dependências é feita pela classe `UseCaseConfig`, que conecta os repositórios em memória aos casos de uso:

- `InMemoryCursoRepository`;
- `InMemoryLeadRepository`;
- `InMemoryOportunidadeRepository`.

Os dados são mantidos apenas em memória durante a execução da aplicação.

## Funil de matrícula

As transições permitidas seguem o fluxo do projeto em Parte 1:

```text
NOVO_LEAD -> CONTATO -> DOCUMENTACAO -> MATRICULA_CONFIRMADA
     |            |             |
     +----------> DESISTENCIA <-+
```

Regra de negócio aplicada:

- `NOVO_LEAD` pode ir para `CONTATO` ou `DESISTENCIA`;
- `CONTATO` pode ir para `DOCUMENTACAO` ou `DESISTENCIA`;
- `DOCUMENTACAO` pode ir para `MATRICULA_CONFIRMADA` ou `DESISTENCIA`;
- `MATRICULA_CONFIRMADA` e `DESISTENCIA` são finais.

O perfil `Administrador` pode executar qualquer transição válida. O perfil `OperadorCaptacao` pode executar transições operacionais válidas, mas não pode confirmar matrícula.

## Regras de negócio atuais

- Validação de e-mail com padrão básico e rejeição de entradas inválidas;
- Validação de telefone com remoção de caracteres não numéricos e exigência de 10 ou 11 dígitos;
- Consistência entre o curso de interesse do lead e o curso da oportunidade;
- Bloqueio de remoção de lead com oportunidades vinculadas;
- Bloqueio de remoção de cursos vinculados a leads ou oportunidades;
- Proteção contra `id` nulo, zero, negativo ou redefinido após atribuição.

## Fluxo do sistema desde o início

### 1. Inicialização

A aplicação começa na classe `ManageEduApplication`, que monta a configuração manual dos casos de uso e instância o `CliRunner`:

1. `UseCaseConfig` cria os repositórios em memória;
2. `UseCaseConfig` cria os casos de uso;
3. `UseCaseConfig` define os usuários padrão:
   - `Administrador` com ID 1;
   - `OperadorCaptacao` com ID 2;
4. `ManageEduApplication` inicia o runner da CLI.

### 2. Interação com o menu

O `CliRunner` inicia o `MenuPrincipal`, que permanece em um loop até o usuário sair. A partir daí, as opções levam para os menus específicos:

```text
MenuPrincipal
  ├─ 1 -> CursoMenu
  ├─ 2 -> LeadMenu
  ├─ 3 -> OportunidadeMenu
  ├─ 4 -> Funil de matrícula
  ├─ 5 -> Alternar usuário ativo
  └─ 0 -> Encerrar aplicação
```

### 3. Cadastro de curso

Quando o usuário escolhe a opção de cursos, o `CursoMenu` coleta os dados e chama `CursoUseCase.cadastrar`. O caso de uso valida nome, modalidade e turno e salva o objeto `Curso` no repositório em memória.

### 4. Cadastro de lead

Ao cadastrar um lead, o `LeadMenu` coleta nome, e-mail, telefone, origem e curso de interesse. O `LeadCandidatoUseCase` valida os dados e confirma que o curso informado existe antes de criar o `LeadCandidato`.

### 5. Criação de oportunidade

A criação de oportunidade ocorre no `OportunidadeMenu`, que solicita ID do lead e ID do curso. O `OportunidadeMatriculaUseCase` valida:

- existência do lead;
- existência do curso;
- consistência entre o curso do lead e o curso da oportunidade.

Em seguida, a oportunidade é criada com status inicial `NOVO_LEAD`.

### 6. Alteração de status

A alteração de status é feita por `OportunidadeMatriculaUseCase.alterarStatus`, que:

- valida o ID da oportunidade;
- valida a transição usando `StatusOportunidade`;
- valida a permissão do usuário ativo;
- atualiza o status da oportunidade.

O `Supplier<Usuario>` presente no `OportunidadeMenu` garante que o menu consulte o usuário ativo no momento da operação, evitando referência desatualizada.

## Limitações atuais e próximos passos

O protótipo ainda não possui persistência definitiva, API HTTP, autenticação real ou interface web. A CLI atual serve como interface temporária para validar as regras de negócio antes da evolução do sistema.

### Evolução para uma API consumida pelo frontend

Em uma etapa futura, o backend será transformado em uma API REST para disponibilizar as funcionalidades do ManageEdu ao frontend que será desenvolvido posteriormente. A intenção é preservar o domínio e os casos de uso existentes, substituindo gradualmente a interface de terminal por endpoints HTTP.

Essa evolução deverá contemplar:

- Integração com Spring Boot ou tecnologia equivalente;
- Endpoints REST para cursos, leads/candidatos e oportunidades de matrícula;
- DTOs de requisição e resposta para comunicação com o frontend;
- Persistência dos dados em banco de dados;
- Autenticação e autorização por perfil de usuário;
- Validação e padronização das respostas de erro;
- Documentação dos endpoints, preferencialmente com OpenAPI/Swagger;
- Configuração de CORS para permitir a comunicação segura com o frontend;
- Manutenção das regras do funil e das validações no domínio, evitando duplicação de lógica na interface.

O frontend futuro consumirá essa API para realizar cadastros, consultas, atualizações, exclusões e acompanhamento visual do funil de matrícula. A separação entre `domain`, `application` e `infrastructure` foi mantida para facilitar essa migração e permitir que a nova camada HTTP reutilize os casos de uso atuais.

### Roadmap

- Persistência em banco de dados;
- Transformação do backend em uma API REST para o frontend;
- Migração ou integração com Spring Boot;
- Login e autorização persistidos;
- Desenvolvimento do frontend web consumidor da API;
- Upload de documentos e fotos;
- Logs em arquivo.

## Estrutura do projeto

```text
backend/
├── pom.xml
├── mvnw / mvnw.cmd
└── src/main/java/com/enterprise/manageedu/
    ├── adapters/cli/
    ├── application/
    │   ├── dto/
    │   └── usecase/
    ├── domain/
    │   ├── exception/
    │   ├── model/
    │   └── repository/
    └── infrastructure/
        ├── config/
        └── persistence/memory/
```