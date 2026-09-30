# ManageEdu

Backend de um CRM educacional para gerenciar cursos, leads/candidatos e oportunidades de matrícula.

A aplicação foi implementada como um sistema Java puro executado no terminal, com persistência em memória e regras de negócio centralizadas no domínio e nos casos de uso.

## Proposta do sistema

O ManageEdu organiza a captação de interessados e acompanha oportunidades de matrícula em instituições de ensino, permitindo que equipes de captação, atendimento e secretaria acadêmica controlem cursos, leads e o funil de vendas.

## Público-alvo

O sistema é voltado para equipes de captação, atendimento, secretaria acadêmica e gestores de instituições de ensino que precisam controlar interessados, cursos e oportunidades de matrícula em um ambiente simples e operacional.

## Tecnologias

- Java 21
- Maven
- Interface de terminal/CLI
- Repositórios em memória

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

- `InMemoryCursoRepository`
- `InMemoryLeadRepository`
- `InMemoryOportunidadeRepository`

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
- Bloqueio de remoção de curso vinculados a leads ou oportunidades;
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

## Como executar

No diretório `backend`:

```powershell
./mvnw compile
java -cp target/classes com.enterprise.manageedu.ManageEduApplication
```

No Windows PowerShell:

```powershell
.\mvnw.cmd compile
java -cp target\classes com.enterprise.manageedu.ManageEduApplication
```

## Parte 1 — Implementado

- Aplicação Java pura executada no terminal
- CRUD em memória de cursos
- CRUD em memória de leads/candidatos
- CRUD em memória de oportunidades de matrícula
- Validação de dados
- Funil de status
- Perfis Administrador e Operador de Captação
- Encapsulamento, herança e polimorfismo

## Parte 2 — Previsto

- Banco de dados
- API REST
- Uso do Spring Boot
- Interface gráfica ou web
- Login funcional
- Upload de documentos e fotos
- Logs em arquivo