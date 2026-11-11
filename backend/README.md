# ManageEdu — Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36)

Backend do CRM educacional ManageEdu para gerenciamento de cursos, leads/candidatos e oportunidades de matrícula, implementado como uma **API REST com Spring Boot** e **persistência relacional em banco de dados MySQL**.

## Visão geral

O ManageEdu organiza a captação de interessados e acompanha oportunidades de matrícula em instituições de ensino. A aplicação permite que equipes de captação, atendimento e secretaria acadêmica controlem cursos, leads e o funil de vendas por meio de endpoints HTTP simples, funcionais e consistentes.

## Funcionalidades

- **Cursos**: Cadastro, listagem, consulta por ID, atualização e remoção de cursos (com proteção de exclusão para cursos vinculados a leads ou oportunidades);
- **Leads/Candidatos**: Cadastro, listagem, consulta por ID, atualização e remoção de leads (com validação estrita de e-mail, telefone de 10 a 11 dígitos, classificação `FRIO`, `MORNO` ou `QUENTE` e curso de interesse obrigatório);
- **Oportunidades de Matrícula**: Criação, listagem geral, filtro por status, visão completa do funil de matrícula por etapa, atualização de dados cadastrais, alteração orientada de status e reabertura;
- **Funil de Matrícula e Validação de Transições**:
  ```text
  NOVO_LEAD -> CONTATO -> DOCUMENTACAO -> MATRICULA_CONFIRMADA
       |            |             |
       +----------> DESISTENCIA <-+
                           |
                reabertura (Administrador)
                           v
                        CONTATO
  ```
- **Controle de Acesso por Perfil (`Administrador` e `OperadorCaptacao`)**:
  - `OperadorCaptacao`: não pode aprovar diretamente para `MATRICULA_CONFIRMADA` nem reabrir oportunidades em `DESISTENCIA`.
  - `Administrador`: permissão total de avanço no funil e realização de reaberturas com justificativa obrigatória e registro de auditoria.
- **Persistência Relacional em MySQL**: Tabelas relacionais `cursos`, `leads`, `oportunidades` e `reaberturas_oportunidade`.

## Tecnologias

- **Java 21**
- **Spring Boot 3** (Spring Web, Spring Data JPA, Spring Security, Spring Validation)
- **MySQL 8** (Hibernate / JPA com schema gerenciado via DDL auto)
- **Springdoc OpenAPI / Swagger UI**
- **Maven Wrapper**

## Pré-requisitos

- JDK 21 ou superior;
- Servidor MySQL 8 em execução;
- Porta 8080 liberada para a API.

## Configuração do Banco de Dados

A aplicação conecta-se ao banco de dados MySQL utilizando as variáveis de ambiente ou os valores padrão definidos em `src/main/resources/application.properties`:

- `DB_URL`: URL JDBC do MySQL (padrão: `jdbc:mysql://localhost:3306/manageedu?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`)
- `DB_USERNAME`: Usuário do banco (padrão: `root`)
- `DB_PASSWORD`: Senha do banco (padrão: `123456789`)

Caso o banco `manageedu` ainda não exista, ele será criado automaticamente na primeira execução com suporte a `createDatabaseIfNotExist=true`.

## Como executar

Abra um terminal no diretório `backend`:

### Compilar e rodar com o Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Ou empacotando e executando o JAR:

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target/ManageEdu-0.0.1-SNAPSHOT.jar
```

A API estará disponível em `http://localhost:8080`.
A documentação interativa Swagger UI estará acessível em `http://localhost:8080/swagger-ui.html`.

---

## Endpoints da API

### 1. Cursos (`/api/cursos`)

| Método | Endpoint | Descrição | Status de Sucesso |
|---|---|---|---|
| `POST` | `/api/cursos` | Cadastrar novo curso | `201 Created` |
| `GET` | `/api/cursos` | Listar todos os cursos | `200 OK` |
| `GET` | `/api/cursos/{id}` | Buscar curso por ID | `200 OK` |
| `PUT` | `/api/cursos/{id}` | Atualizar dados do curso | `200 OK` |
| `DELETE` | `/api/cursos/{id}` | Excluir curso (bloqueado se houver vínculo) | `204 No Content` |

#### Exemplo de requisição (POST /api/cursos):
```json
{
  "nome": "Engenharia de Software",
  "modalidade": "PRESENCIAL",
  "turno": "NOTURNO"
}
```
*Modalidades aceitas:* `PRESENCIAL`, `EAD`, `SEMIPRESENCIAL`.  
*Turnos aceitos:* `MATUTINO`, `VESPERTINO`, `NOTURNO`, `INTEGRAL`.

---

### 2. Leads / Candidatos (`/api/leads`)

| Método | Endpoint | Descrição | Status de Sucesso |
|---|---|---|---|
| `POST` | `/api/leads` | Cadastrar lead | `201 Created` |
| `GET` | `/api/leads` | Listar todos os leads | `200 OK` |
| `GET` | `/api/leads/{id}` | Buscar lead por ID | `200 OK` |
| `PUT` | `/api/leads/{id}` | Atualizar lead | `200 OK` |
| `DELETE` | `/api/leads/{id}` | Excluir lead (bloqueado se houver oportunidades) | `204 No Content` |

#### Exemplo de requisição (POST /api/leads):
```json
{
  "nome": "Lucas Silva",
  "email": "lucas@email.com",
  "telefone": "11987654321",
  "origem": "Instagram",
  "classificacao": "QUENTE",
  "cursoInteresseId": 1
}
```
*Classificações aceitas:* `FRIO`, `MORNO`, `QUENTE`.

---

### 3. Oportunidades de Matrícula (`/api/oportunidades`)

| Método | Endpoint | Descrição | Status de Sucesso |
|---|---|---|---|
| `POST` | `/api/oportunidades` | Criar oportunidade (status inicial `NOVO_LEAD`) | `201 Created` |
| `GET` | `/api/oportunidades` | Listar oportunidades (parâmetro opcional `?status=...`) | `200 OK` |
| `GET` | `/api/oportunidades/funil` | Obter visão agrupada do funil de vendas por status | `200 OK` |
| `GET` | `/api/oportunidades/{id}` | Buscar oportunidade por ID | `200 OK` |
| `PUT` | `/api/oportunidades/{id}` | Atualizar dados cadastrais da oportunidade | `200 OK` |
| `PATCH` ou `PUT` | `/api/oportunidades/{id}/status` | Alterar etapa/status no funil | `200 OK` |
| `POST` | `/api/oportunidades/{id}/reabrir` | Reabrir oportunidade em desistência | `200 OK` |
| `DELETE` | `/api/oportunidades/{id}` | Excluir oportunidade | `204 No Content` |

#### Exemplo de criação de oportunidade (`POST /api/oportunidades`):
```json
{
  "leadCandidatoId": 1,
  "cursoId": 1,
  "valorEstimado": 1200.50,
  "dataPrevista": "2026-12-15",
  "observacao": "Interesse no turno noturno"
}
```

#### Alteração de status (`PATCH /api/oportunidades/{id}/status`):
Header opcional: `X-User-Role: ADMIN` (padrão) ou `X-User-Role: OPERADOR`.
```json
{
  "novoStatus": "CONTATO"
}
```

#### Reabertura de oportunidade (`POST /api/oportunidades/{id}/reabrir`):
Header opcional: `X-User-Role: ADMIN` (apenas Administradores podem reabrir).
```json
{
  "justificativa": "Candidato retomou o contato após resolução financeira."
}
```

---

## Arquitetura do Sistema

A arquitetura hexagonal / limpa foi integralmente preservada:

```text
src/main/java/com/enterprise/manageedu/
├── ManageEduApplication.java                 -> Ponto de entrada Spring Boot
├── domain/                                    -> Núcleo de domínio (regras puras)
│   ├── exception/                            -> RegraDeNegocioException, ResourceNotFoundException
│   ├── model/                                -> Curso, LeadCandidato, OportunidadeMatricula, Usuario...
│   └── repository/                           -> Interfaces de contrato dos repositórios
├── application/                               -> Casos de uso e contratos de transporte
│   ├── dto/                                  -> Request e Response DTOs
│   └── usecase/                              -> CursoUseCase, LeadCandidatoUseCase, OportunidadeMatriculaUseCase
├── infrastructure/                            -> Implementações técnicas e persistência real
│   ├── config/                               -> UseCaseConfig (@Configuration), SecurityConfig
│   └── persistence/                          -> Repositórios Spring Data JPA e implementações de persistência
│       ├── entity/                           -> Entidades JPA relacionais para o MySQL
│       └── repository/                       -> Interfaces Spring Data JPA
└── adapters/
    └── rest/                                 -> Controladores REST (endpoints da API) e Exception Handler
```
