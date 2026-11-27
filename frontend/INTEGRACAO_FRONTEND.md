# Guia de Integração Frontend — ManageEdu API

Base URL padrão: `http://localhost:8080`

---

## 1. Autenticação, Sessão e CSRF

A API utiliza **autenticação por sessão HTTP** (`JSESSIONID`) gerenciada pelo Spring Security.

### Regras obrigatórias no Frontend (`fetch` / `axios`)
1. Enviar cookies em **todas** as requisições:
   - `fetch`: `credentials: "include"`
   - `axios`: `withCredentials: true`
2. **CSRF (`XSRF-TOKEN`)**:
   - Antes de fazer requisições `POST`, `PUT`, `PATCH` ou `DELETE` autenticadas, garanta que o cookie `XSRF-TOKEN` foi recebido (ex.: chamando `GET /api/csrf-token` ao iniciar o app ou após o login).
   - Leia o valor do cookie `XSRF-TOKEN` e envie no header **`X-XSRF-TOKEN`** em toda requisição de escrita (`POST`, `PUT`, `PATCH`, `DELETE`).
   - *(Exceção: `POST /api/sessoes` e `DELETE /api/sessoes/atual` não exigem o header CSRF.)*

### Endpoints de Sessão (`/api/sessoes`)

| Método | Rota | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/csrf-token` | Público | Emite o cookie `XSRF-TOKEN` (`204 No Content`) |
| `POST` | `/api/sessoes` | Público | Autentica e cria sessão (`JSESSIONID`) (`201 Created`) |
| `DELETE` | `/api/sessoes/atual` | Público | Encerra a sessão (`204 No Content`) |
| `GET` | `/api/sessoes/atual` | Autenticado | Retorna os dados do usuário logado |

**Exemplo `POST /api/sessoes`:**
```json
{
  "email": "admin@manageedu.com",
  "senha": "sua_senha"
}
```
**Resposta `200 OK`:**
```json
{
  "id": 1,
  "nome": "Ana Administradora",
  "email": "admin@manageedu.com",
  "perfil": "ADMINISTRADOR",
  "ativo": true
}
```

---

## 2. Gestão de Usuários (`/api/usuarios`) — Somente `ADMINISTRADOR`

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/usuarios` | Cadastra usuário (`201 Created`) |
| `GET` | `/api/usuarios` | Lista todos os usuários |
| `GET` | `/api/usuarios/{id}` | Busca usuário por ID |
| `PUT` | `/api/usuarios/{id}` | Atualiza dados (campo `senha` é opcional na edição) |
| `PATCH` | `/api/usuarios/{id}` | Ativa ou desativa usuário (corpo `{ "ativo": false }`) |

**Exemplo `POST /api/usuarios`:**
```json
{
  "nome": "Carlos Captação",
  "email": "carlos@manageedu.com",
  "senha": "SenhaForte123",
  "perfil": "OPERADOR_CAPTACAO",
  "ativo": true
}
```

---

## 3. Cursos (`/api/cursos`) — Autenticado

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/cursos` | Cadastra curso (`201 Created`) |
| `GET` | `/api/cursos` | Lista cursos |
| `GET` | `/api/cursos/{id}` | Busca curso por ID |
| `PUT` | `/api/cursos/{id}` | Atualiza curso |
| `DELETE` | `/api/cursos/{id}` | Remove curso (`204 No Content`) |

**Exemplo `POST /api/cursos`:**
```json
{
  "nome": "Engenharia de Software",
  "modalidade": "PRESENCIAL",
  "turno": "NOTURNO"
}
```

---

## 4. Leads (`/api/leads`) — Autenticado

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/leads` | Cadastra lead (`201 Created`) |
| `GET` | `/api/leads` | Lista leads |
| `GET` | `/api/leads/{id}` | Busca lead por ID |
| `PUT` | `/api/leads/{id}` | Atualiza lead |
| `DELETE` | `/api/leads/{id}` | Remove lead (`204 No Content`) |

**Exemplo `POST /api/leads`:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "11999998888",
  "origem": "Instagram",
  "classificacao": "QUENTE",
  "cursoInteresseId": 1
}
```

---

## 5. Oportunidades (`/api/oportunidades`) — Autenticado

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/oportunidades` | Cria oportunidade (`201 Created`) |
| `GET` | `/api/oportunidades` | Lista todas (ou `?status=CONTATO`) |
| `GET` | `/api/oportunidades/funil` | Retorna mapa agrupado por `StatusOportunidade` |
| `GET` | `/api/oportunidades/{id}` | Busca por ID |
| `PUT` | `/api/oportunidades/{id}` | Atualiza dados básicos |
| `PATCH` / `PUT` | `/api/oportunidades/{id}/status` | Avança/altera status |
| `POST` | `/api/oportunidades/{id}/reaberturas` | Reabre oportunidade em `DESISTENCIA` (somente `ADMINISTRADOR`) |
| `DELETE` | `/api/oportunidades/{id}` | Remove oportunidade (`204 No Content`) |

**Exemplo `POST /api/oportunidades`:**
```json
{
  "leadCandidatoId": 1,
  "cursoId": 1,
  "valorEstimado": 1200.00,
  "dataPrevista": "2026-12-15",
  "observacao": "Interessado em bolsa"
}
```

**Exemplo `PATCH /api/oportunidades/{id}/status`:**
```json
{
  "novoStatus": "CONTATO"
}
```
*(Regra de perfil: `OPERADOR_CAPTACAO` não pode alterar para `MATRICULA_CONFIRMADA`.)*

**Exemplo `POST /api/oportunidades/{id}/reaberturas`:**
```json
{
  "justificativa": "Candidato retornou contato solicitando nova proposta."
}
```

---

## 6. Fotos e Anexos — Autenticado

Envio via `multipart/form-data` com o campo **`arquivo`**.

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/leads/{leadId}/foto` | Envia foto do lead (JPG, PNG, WEBP — máx. 5 MB) |
| `GET` | `/api/leads/{leadId}/foto` | Retorna imagem (`Content-Disposition: inline`) |
| `POST` | `/api/leads/{leadId}/anexos` | Envia anexo do lead (PDF, JPG, PNG — máx. 10 MB) |
| `GET` | `/api/leads/{leadId}/anexos` | Lista anexos do lead |
| `POST` | `/api/oportunidades/{oportunidadeId}/anexos` | Envia anexo da oportunidade (PDF, JPG, PNG — máx. 10 MB) |
| `GET` | `/api/oportunidades/{oportunidadeId}/anexos` | Lista anexos da oportunidade |
| `GET` | `/api/anexos/{id}/arquivo` | Faz download do arquivo (`attachment`) |
| `DELETE` | `/api/anexos/{id}` | Remove anexo/foto (`204 No Content`) |

**Exemplo de resposta de upload (`201 Created`):**
```json
{
  "id": 10,
  "nomeOriginal": "rg-joao.pdf",
  "contentType": "application/pdf",
  "tamanhoBytes": 184320,
  "tipo": "ANEXO_LEAD",
  "leadId": 1,
  "oportunidadeId": null,
  "dataUpload": "2026-11-13T15:20:00",
  "urlDownload": "/api/anexos/10/arquivo"
}
```

---

## 7. Valores dos Enums

- **`PerfilUsuario`**: `ADMINISTRADOR`, `OPERADOR_CAPTACAO`
- **`ModalidadeCurso`**: `PRESENCIAL`, `EAD`, `HIBRIDO`
- **`TurnoCurso`**: `MATUTINO`, `VESPERTINO`, `NOTURNO`, `INTEGRAL`
- **`ClassificacaoLead`**: `FRIO`, `MORNO`, `QUENTE`
- **`StatusOportunidade`**: `NOVO_LEAD`, `CONTATO`, `DOCUMENTACAO`, `MATRICULA_CONFIRMADA`, `DESISTENCIA`
- **`TipoAnexo`**: `FOTO_LEAD`, `ANEXO_LEAD`, `ANEXO_OPORTUNIDADE`

---

## 8. Formato Padrão de Erros (`ErrorResponseDTO`)

Todos os erros (`400`, `401`, `403`, `404`, `500`) retornam JSON neste formato:
```json
{
  "timestamp": "2026-11-13T15:25:00",
  "status": 400,
  "erro": "Regra de negócio violada",
  "mensagem": "Somente administradores podem reabrir oportunidades.",
  "path": "/api/oportunidades/1/reaberturas"
}
```

