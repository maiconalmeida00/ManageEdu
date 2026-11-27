let usuarios = [];

async function carregarUsuarios() {
  try {
    usuarios = await Api.listarUsuarios();
    desenharTabela();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function desenharTabela() {
  const corpo = document.getElementById("tabela-usuarios");

  if (!usuarios.length) {
    corpo.innerHTML = `<tr><td colspan="5" class="vazio">Nenhum usuário cadastrado.</td></tr>`;
    return;
  }

  corpo.innerHTML = usuarios
    .map((u) => {
      const ehVoce = u.id === usuarioLogado.id;
      return `
      <tr>
        <td><strong>${escapar(u.nome)}</strong>${ehVoce ? ' <span class="selo selo-neutro">você</span>' : ""}</td>
        <td>${escapar(u.email)}</td>
        <td>${escapar(ROTULOS.perfil[u.perfil] || u.perfil)}</td>
        <td>${u.ativo ? selo("Ativo", "ativo") : selo("Inativo", "inativo")}</td>
        <td>
          <div class="acoes">
            <button class="btn btn-pequeno" data-editar="${u.id}">Editar</button>
            ${ehVoce ? "" : `<button class="btn btn-pequeno ${u.ativo ? "btn-perigo" : ""}" data-ativo="${u.id}">${u.ativo ? "Desativar" : "Ativar"}</button>`}
          </div>
        </td>
      </tr>`;
    })
    .join("");

  const buscar = (id) => usuarios.find((u) => u.id == id);
  corpo.querySelectorAll("[data-editar]").forEach((b) => b.addEventListener("click", () => abrirFormulario(buscar(b.dataset.editar))));
  corpo.querySelectorAll("[data-ativo]").forEach((b) => b.addEventListener("click", () => alternarAtivo(buscar(b.dataset.ativo))));
}

function abrirFormulario(usuario) {
  const editando = Boolean(usuario);
  const corpo = abrirModal(editando ? "Editar usuário" : "Novo usuário", `
    <form id="form-usuario">
      <div class="campo">
        <label for="nome">Nome</label>
        <input id="nome" name="nome" required value="${escapar(usuario?.nome)}" placeholder="Carlos Captação">
      </div>
      <div class="campo">
        <label for="email">E-mail</label>
        <input id="email" name="email" type="email" required value="${escapar(usuario?.email)}" placeholder="carlos@manageedu.com">
      </div>
      <div class="campo-linha">
        <div class="campo">
          <label for="senha">${editando ? "Nova senha (opcional)" : "Senha"}</label>
          <input id="senha" name="senha" type="password" minlength="6" ${editando ? "" : "required"} autocomplete="new-password" placeholder="Mínimo 6 caracteres">
        </div>
        <div class="campo">
          <label for="perfil">Perfil</label>
          <select id="perfil" name="perfil" required>${opcoesSelect(ROTULOS.perfil, usuario?.perfil || "OPERADOR_CAPTACAO")}</select>
        </div>
      </div>
      <label class="campo-check">
        <input type="checkbox" name="ativo" ${!editando || usuario.ativo ? "checked" : ""}>
        Usuário ativo
      </label>
      <div class="modal-rodape">
        <button type="button" class="btn" data-fechar>Cancelar</button>
        <button type="submit" class="btn btn-primario">${editando ? "Salvar alterações" : "Cadastrar usuário"}</button>
      </div>
    </form>`);

  const formulario = corpo.querySelector("#form-usuario");
  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    enviarFormulario(formulario, async () => {
      const dados = dadosFormulario(formulario);
      const corpoRequisicao = {
        nome: dados.nome.trim(),
        email: dados.email.trim(),
        senha: dados.senha || null,
        perfil: dados.perfil,
        ativo: formulario.ativo.checked,
      };
      if (editando) {
        await Api.atualizarUsuario(usuario.id, corpoRequisicao);
        mostrarAviso("Usuário atualizado.", "sucesso");
      } else {
        await Api.criarUsuario(corpoRequisicao);
        mostrarAviso("Usuário cadastrado.", "sucesso");
      }
      fecharModal();
      carregarUsuarios();
    });
  });
}

async function alternarAtivo(usuario) {
  const acao = usuario.ativo ? "Desativar" : "Ativar";
  if (!(await confirmar(`${acao} o usuário ${usuario.nome}?`, acao))) return;
  try {
    await Api.alterarAtivo(usuario.id, !usuario.ativo);
    mostrarAviso("Usuário " + (usuario.ativo ? "desativado." : "ativado."), "sucesso");
    carregarUsuarios();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

document.getElementById("botao-novo").addEventListener("click", () => abrirFormulario(null));

iniciarPagina("usuarios", { somenteAdmin: true }).then((usuario) => usuario && carregarUsuarios());
