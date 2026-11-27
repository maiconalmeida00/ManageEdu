# ManageEdu — Frontend

Interface web do ManageEdu feita com **HTML, CSS e JavaScript puros** (sem framework e sem build).

## Estrutura

```text
frontend/
├── index.html            Login
├── painel.html           Resumo (métricas e funil)
├── cursos.html           CRUD de cursos
├── leads.html            CRUD de leads, foto e anexos
├── oportunidades.html    Funil: criação, mudança de status, reabertura e anexos
├── usuarios.html         Gestão de usuários (somente administrador)
├── css/style.css         Estilo único do sistema
└── js/
    ├── api.js            Comunicação com o backend (sessão + CSRF)
    ├── comum.js          Menu, modais, avisos, formatações e rótulos
    └── <página>.js       Lógica de cada tela
```

## Como executar

1. Suba o backend (porta `8080`), conforme o `backend/README.md`.
2. Sirva esta pasta na porta **5173**. O backend só aceita requisições (CORS) vindas de `http://localhost:5173`:

```powershell
python -m http.server 5173 --bind localhost --directory frontend
```

3. Acesse **http://localhost:5173**. Use `localhost`, e não `127.0.0.1`, senão o cookie de sessão não funciona.

## Primeiro acesso

O backend não cria nenhum usuário automaticamente. Para entrar pela primeira vez, insira um administrador direto no MySQL com a senha criptografada em BCrypt:

```sql
INSERT INTO usuarios (nome, email, senha_hash, perfil, ativo)
VALUES ('Administrador', 'admin@manageedu.com', '<hash-bcrypt>', 'ADMINISTRADOR', 1);
```

Depois disso, os outros usuários podem ser cadastrados pela tela **Usuários**.

## Como a integração funciona

- Toda requisição usa `credentials: "include"` para enviar o cookie de sessão `JSESSIONID`.
- Antes de `POST`, `PUT`, `PATCH` e `DELETE`, o frontend lê o cookie `XSRF-TOKEN` e o envia no cabeçalho `X-XSRF-TOKEN`. Se o cookie ainda não existir, ele chama antes `GET /api/csrf-token`.
- Os erros da API (`ErrorResponseDTO`) aparecem como aviso no canto da tela, usando o campo `mensagem`.
- Uma resposta `401` leva de volta para a tela de login.
- O operador de captação não vê o botão de confirmar matrícula nem o de reabrir. O menu Usuários aparece só para administradores.
