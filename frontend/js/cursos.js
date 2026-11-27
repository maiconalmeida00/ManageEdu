let cursos = [];

async function carregarCursos() {
  try {
    cursos = await Api.listarCursos();
    desenharTabela();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function desenharTabela() {
  const termo = document.getElementById("busca").value.trim().toLowerCase();
  const filtrados = cursos.filter((curso) => curso.nome.toLowerCase().includes(termo));
  const corpo = document.getElementById("tabela-cursos");

  if (!filtrados.length) {
    corpo.innerHTML = `<tr><td colspan="4" class="vazio">${cursos.length ? "Nenhum curso encontrado." : "Nenhum curso cadastrado ainda."}</td></tr>`;
    return;
  }

  corpo.innerHTML = filtrados
    .map(
      (curso) => `
      <tr>
        <td><strong>${escapar(curso.nome)}</strong></td>
        <td>${selo(ROTULOS.modalidade[curso.modalidade] || curso.modalidade, "neutro")}</td>
        <td>${escapar(ROTULOS.turno[curso.turno] || curso.turno)}</td>
        <td>
          <div class="acoes">
            <button class="btn btn-pequeno" data-editar="${curso.id}">Editar</button>
            <button class="btn btn-pequeno btn-perigo" data-remover="${curso.id}">Excluir</button>
          </div>
        </td>
      </tr>`
    )
    .join("");

  corpo.querySelectorAll("[data-editar]").forEach((botao) =>
    botao.addEventListener("click", () => abrirFormulario(cursos.find((c) => c.id == botao.dataset.editar)))
  );
  corpo.querySelectorAll("[data-remover]").forEach((botao) =>
    botao.addEventListener("click", () => removerCurso(cursos.find((c) => c.id == botao.dataset.remover)))
  );
}

function abrirFormulario(curso) {
  const editando = Boolean(curso);
  const corpo = abrirModal(editando ? "Editar curso" : "Novo curso", `
    <form id="form-curso">
      <div class="campo">
        <label for="nome">Nome do curso</label>
        <input id="nome" name="nome" required maxlength="150" value="${escapar(curso?.nome)}" placeholder="Engenharia de Software">
      </div>
      <div class="campo-linha">
        <div class="campo">
          <label for="modalidade">Modalidade</label>
          <select id="modalidade" name="modalidade" required>${opcoesSelect(ROTULOS.modalidade, curso?.modalidade)}</select>
        </div>
        <div class="campo">
          <label for="turno">Turno</label>
          <select id="turno" name="turno" required>${opcoesSelect(ROTULOS.turno, curso?.turno)}</select>
        </div>
      </div>
      <div class="modal-rodape">
        <button type="button" class="btn" data-fechar>Cancelar</button>
        <button type="submit" class="btn btn-primario">${editando ? "Salvar alterações" : "Cadastrar curso"}</button>
      </div>
    </form>`);

  const formulario = corpo.querySelector("#form-curso");
  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    enviarFormulario(formulario, async () => {
      const dados = dadosFormulario(formulario);
      if (editando) {
        await Api.atualizarCurso(curso.id, dados);
        mostrarAviso("Curso atualizado.", "sucesso");
      } else {
        await Api.criarCurso(dados);
        mostrarAviso("Curso cadastrado.", "sucesso");
      }
      fecharModal();
      carregarCursos();
    });
  });
}

async function removerCurso(curso) {
  if (!(await confirmar(`Excluir o curso "${curso.nome}"?`, "Excluir"))) return;
  try {
    await Api.removerCurso(curso.id);
    mostrarAviso("Curso excluído.", "sucesso");
    carregarCursos();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

document.getElementById("busca").addEventListener("input", desenharTabela);
document.getElementById("botao-novo").addEventListener("click", () => abrirFormulario(null));

iniciarPagina("cursos").then((usuario) => usuario && carregarCursos());
