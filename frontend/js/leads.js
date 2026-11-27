let leads = [];
let cursos = [];
let versaoFoto = Date.now();

async function carregarLeads() {
  try {
    [leads, cursos] = await Promise.all([Api.listarLeads(), Api.listarCursos()]);
    desenharLeads();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function htmlAvatar(lead, classeExtra = "") {
  return `
    <div class="avatar ${classeExtra}" data-iniciais="${escapar(iniciais(lead.nome))}">
      <img src="${Api.urlFotoLead(lead.id)}?v=${versaoFoto}" alt="" onerror="this.parentElement.textContent=this.parentElement.dataset.iniciais">
    </div>`;
}

function desenharLeads() {
  const termo = document.getElementById("busca").value.trim().toLowerCase();
  const classificacao = document.getElementById("filtro-classificacao").value;
  const area = document.getElementById("lista-leads");

  const filtrados = leads.filter((lead) => {
    const texto = [lead.nome, lead.email, lead.cursoInteresseNome].join(" ").toLowerCase();
    return texto.includes(termo) && (!classificacao || lead.classificacao === classificacao);
  });

  if (!filtrados.length) {
    area.innerHTML = `<div class="cartao vazio" style="grid-column:1/-1">${leads.length ? "Nenhum lead encontrado com esses filtros." : "Nenhum lead cadastrado ainda."}</div>`;
    return;
  }

  area.innerHTML = filtrados
    .map(
      (lead) => `
      <article class="cartao cartao-lead">
        <div class="cartao-lead-topo">
          ${htmlAvatar(lead)}
          <div>
            <div class="nome">${escapar(lead.nome)}</div>
            <div class="subtitulo">${escapar(lead.cursoInteresseNome || "Sem curso")}</div>
          </div>
        </div>
        <div class="info">
          <span>${escapar(lead.email)}</span>
          <span>${formatarTelefone(lead.telefone)}</span>
          <span>Origem: ${escapar(lead.origem || "—")}</span>
        </div>
        <div class="rodape">
          ${selo(ROTULOS.classificacao[lead.classificacao] || lead.classificacao, lead.classificacao)}
          <div class="acoes">
            <button class="btn btn-pequeno" data-detalhes="${lead.id}">Detalhes</button>
            <button class="btn btn-pequeno" data-editar="${lead.id}">Editar</button>
            <button class="btn btn-pequeno btn-perigo" data-remover="${lead.id}" aria-label="Excluir">Excluir</button>
          </div>
        </div>
      </article>`
    )
    .join("");

  const buscar = (id) => leads.find((l) => l.id == id);
  area.querySelectorAll("[data-detalhes]").forEach((b) => b.addEventListener("click", () => abrirDetalhes(buscar(b.dataset.detalhes))));
  area.querySelectorAll("[data-editar]").forEach((b) => b.addEventListener("click", () => abrirFormulario(buscar(b.dataset.editar))));
  area.querySelectorAll("[data-remover]").forEach((b) => b.addEventListener("click", () => removerLead(buscar(b.dataset.remover))));
}

function abrirFormulario(lead) {
  if (!cursos.length) {
    mostrarAviso("Cadastre um curso antes de cadastrar leads.", "erro");
    return;
  }

  const editando = Boolean(lead);
  const opcoesCursos = cursos
    .map((c) => `<option value="${c.id}" ${lead?.cursoInteresseId === c.id ? "selected" : ""}>${escapar(c.nome)}</option>`)
    .join("");

  const corpo = abrirModal(editando ? "Editar lead" : "Novo lead", `
    <form id="form-lead">
      <div class="campo">
        <label for="nome">Nome completo</label>
        <input id="nome" name="nome" required value="${escapar(lead?.nome)}" placeholder="João da Silva">
      </div>
      <div class="campo-linha">
        <div class="campo">
          <label for="email">E-mail</label>
          <input id="email" name="email" type="email" required value="${escapar(lead?.email)}" placeholder="joao@email.com">
        </div>
        <div class="campo">
          <label for="telefone">Telefone (com DDD)</label>
          <input id="telefone" name="telefone" required value="${escapar(lead?.telefone)}" placeholder="11999998888" inputmode="numeric">
        </div>
      </div>
      <div class="campo-linha">
        <div class="campo">
          <label for="origem">Origem</label>
          <input id="origem" name="origem" value="${escapar(lead?.origem)}" placeholder="Instagram, indicação, site...">
        </div>
        <div class="campo">
          <label for="classificacao">Classificação</label>
          <select id="classificacao" name="classificacao" required>${opcoesSelect(ROTULOS.classificacao, lead?.classificacao || "MORNO")}</select>
        </div>
      </div>
      <div class="campo">
        <label for="cursoInteresseId">Curso de interesse</label>
        <select id="cursoInteresseId" name="cursoInteresseId" required>${opcoesCursos}</select>
      </div>
      <div class="modal-rodape">
        <button type="button" class="btn" data-fechar>Cancelar</button>
        <button type="submit" class="btn btn-primario">${editando ? "Salvar alterações" : "Cadastrar lead"}</button>
      </div>
    </form>`);

  const formulario = corpo.querySelector("#form-lead");
  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    enviarFormulario(formulario, async () => {
      const dados = dadosFormulario(formulario);
      dados.telefone = dados.telefone.replace(/\D/g, "");
      dados.cursoInteresseId = Number(dados.cursoInteresseId);
      if (editando) {
        await Api.atualizarLead(lead.id, dados);
        mostrarAviso("Lead atualizado.", "sucesso");
      } else {
        await Api.criarLead(dados);
        mostrarAviso("Lead cadastrado.", "sucesso");
      }
      fecharModal();
      carregarLeads();
    });
  });
}

function abrirDetalhes(lead) {
  const corpo = abrirModal(lead.nome, `
    <div style="display:flex;gap:18px;align-items:center;flex-wrap:wrap">
      <div id="foto-detalhe">${htmlAvatar(lead, "avatar-grande")}</div>
      <div>
        <div class="upload" style="margin-top:0">
          <input type="file" id="arquivo-foto" accept=".jpg,.jpeg,.png,.webp">
          <button class="btn btn-pequeno btn-primario" id="enviar-foto">Enviar foto</button>
        </div>
        <p class="meta" style="font-size:12px;color:var(--texto-suave);margin:6px 0 0">JPG, PNG ou WEBP até 5 MB</p>
      </div>
    </div>

    <dl class="detalhes secao">
      <dt>E-mail</dt><dd>${escapar(lead.email)}</dd>
      <dt>Telefone</dt><dd>${formatarTelefone(lead.telefone)}</dd>
      <dt>Origem</dt><dd>${escapar(lead.origem || "—")}</dd>
      <dt>Classificação</dt><dd>${selo(ROTULOS.classificacao[lead.classificacao] || lead.classificacao, lead.classificacao)}</dd>
      <dt>Curso de interesse</dt><dd>${escapar(lead.cursoInteresseNome || "—")}</dd>
    </dl>

    <div class="secao">
      <h3>Anexos (PDF, JPG ou PNG até 10 MB)</h3>
      <div id="anexos-lead"></div>
    </div>`, { largo: true });

  const botaoFoto = corpo.querySelector("#enviar-foto");
  botaoFoto.addEventListener("click", async () => {
    const campo = corpo.querySelector("#arquivo-foto");
    if (!campo.files.length) {
      mostrarAviso("Escolha uma imagem para enviar.", "erro");
      return;
    }
    botaoFoto.disabled = true;
    try {
      await Api.enviarFotoLead(lead.id, campo.files[0]);
      versaoFoto = Date.now();
      corpo.querySelector("#foto-detalhe").innerHTML = htmlAvatar(lead, "avatar-grande");
      campo.value = "";
      mostrarAviso("Foto atualizada.", "sucesso");
      desenharLeads();
    } catch (erro) {
      mostrarAviso(erro.message, "erro");
    } finally {
      botaoFoto.disabled = false;
    }
  });

  montarSecaoAnexos(
    corpo.querySelector("#anexos-lead"),
    async () => (await Api.listarAnexosLead(lead.id)).filter((a) => a.tipo !== "FOTO_LEAD"),
    (arquivo) => Api.enviarAnexoLead(lead.id, arquivo)
  );
}

async function removerLead(lead) {
  if (!(await confirmar(`Excluir o lead "${lead.nome}"?`, "Excluir"))) return;
  try {
    await Api.removerLead(lead.id);
    mostrarAviso("Lead excluído.", "sucesso");
    carregarLeads();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

document.getElementById("busca").addEventListener("input", desenharLeads);
document.getElementById("filtro-classificacao").addEventListener("change", desenharLeads);
document.getElementById("botao-novo").addEventListener("click", () => abrirFormulario(null));

iniciarPagina("leads").then((usuario) => usuario && carregarLeads());
