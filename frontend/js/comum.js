const ROTULOS = {
  modalidade: { PRESENCIAL: "Presencial", EAD: "EAD", HIBRIDO: "Híbrido" },
  turno: { MATUTINO: "Matutino", VESPERTINO: "Vespertino", NOTURNO: "Noturno", INTEGRAL: "Integral" },
  classificacao: { FRIO: "Frio", MORNO: "Morno", QUENTE: "Quente" },
  perfil: { ADMINISTRADOR: "Administrador", OPERADOR_CAPTACAO: "Operador de captação" },
  status: {
    NOVO_LEAD: "Novo lead",
    CONTATO: "Contato",
    DOCUMENTACAO: "Documentação",
    MATRICULA_CONFIRMADA: "Matrícula confirmada",
    DESISTENCIA: "Desistência",
  },
};

const ORDEM_STATUS = ["NOVO_LEAD", "CONTATO", "DOCUMENTACAO", "MATRICULA_CONFIRMADA", "DESISTENCIA"];

const TRANSICOES = {
  NOVO_LEAD: ["CONTATO", "DESISTENCIA"],
  CONTATO: ["DOCUMENTACAO", "DESISTENCIA"],
  DOCUMENTACAO: ["MATRICULA_CONFIRMADA", "DESISTENCIA"],
  MATRICULA_CONFIRMADA: [],
  DESISTENCIA: [],
};

const MENU = [
  { pagina: "painel", titulo: "Painel", href: "painel.html" },
  { pagina: "cursos", titulo: "Cursos", href: "cursos.html" },
  { pagina: "leads", titulo: "Leads", href: "leads.html" },
  { pagina: "oportunidades", titulo: "Funil", href: "oportunidades.html" },
  { pagina: "usuarios", titulo: "Usuários", href: "usuarios.html", somenteAdmin: true },
];

let usuarioLogado = null;

function ehAdmin() {
  return usuarioLogado && usuarioLogado.perfil === "ADMINISTRADOR";
}

async function iniciarPagina(paginaAtiva, opcoes = {}) {
  try {
    usuarioLogado = await Api.usuarioAtual();
  } catch (erro) {
    if (!String(erro.message).includes("sessão")) {
      mostrarAviso(erro.message, "erro");
    }
    return null;
  }

  if (opcoes.somenteAdmin && !ehAdmin()) {
    window.location.href = "painel.html";
    return null;
  }

  montarTopo(paginaAtiva);
  return usuarioLogado;
}

function montarTopo(paginaAtiva) {
  const links = MENU.filter((item) => !item.somenteAdmin || ehAdmin())
    .map((item) => `<a href="${item.href}" class="${item.pagina === paginaAtiva ? "ativo" : ""}">${item.titulo}</a>`)
    .join("");

  const topo = document.createElement("header");
  topo.className = "topo";
  topo.innerHTML = `
    <div class="topo-conteudo">
      <a class="logo" href="painel.html"><span class="logo-icone">ME</span>ManageEdu</a>
      <nav class="menu">${links}</nav>
      <div class="usuario-logado">
        <div class="textos">
          <div class="nome">${escapar(usuarioLogado.nome)}</div>
          <div class="perfil">${ROTULOS.perfil[usuarioLogado.perfil] || usuarioLogado.perfil}</div>
        </div>
        <button class="btn btn-pequeno" id="botao-sair">Sair</button>
      </div>
    </div>`;
  document.body.prepend(topo);

  document.getElementById("botao-sair").addEventListener("click", async () => {
    try {
      await Api.logout();
    } finally {
      window.location.href = "index.html";
    }
  });
}

function escapar(texto) {
  if (texto === null || texto === undefined) return "";
  return String(texto)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function iniciais(nome) {
  return (nome || "?")
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((parte) => parte[0].toUpperCase())
    .join("");
}

function formatarMoeda(valor) {
  if (valor === null || valor === undefined || valor === "") return "—";
  return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarData(data) {
  if (!data) return "—";
  const [ano, mes, dia] = String(data).substring(0, 10).split("-");
  return `${dia}/${mes}/${ano}`;
}

function formatarDataHora(data) {
  if (!data) return "—";
  const d = new Date(data);
  return d.toLocaleDateString("pt-BR") + " " + d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
}

function formatarTamanho(bytes) {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / (1024 * 1024)).toFixed(1) + " MB";
}

function formatarTelefone(telefone) {
  const n = String(telefone || "").replace(/\D/g, "");
  if (n.length === 11) return `(${n.slice(0, 2)}) ${n.slice(2, 7)}-${n.slice(7)}`;
  if (n.length === 10) return `(${n.slice(0, 2)}) ${n.slice(2, 6)}-${n.slice(6)}`;
  return telefone || "—";
}

function selo(texto, classe) {
  return `<span class="selo selo-${String(classe).toLowerCase()}">${escapar(texto)}</span>`;
}

function seloStatus(status) {
  return selo(ROTULOS.status[status] || status, status);
}

function opcoesSelect(mapa, selecionado) {
  return Object.entries(mapa)
    .map(([valor, rotulo]) => `<option value="${valor}" ${valor === selecionado ? "selected" : ""}>${escapar(rotulo)}</option>`)
    .join("");
}

function mostrarAviso(mensagem, tipo = "info") {
  let caixa = document.querySelector(".avisos");
  if (!caixa) {
    caixa = document.createElement("div");
    caixa.className = "avisos";
    document.body.appendChild(caixa);
  }
  const aviso = document.createElement("div");
  aviso.className = "aviso aviso-" + tipo;
  aviso.textContent = mensagem;
  caixa.appendChild(aviso);
  setTimeout(() => aviso.remove(), 4500);
}

function abrirModal(titulo, html, opcoes = {}) {
  fecharModal();
  const fundo = document.createElement("div");
  fundo.className = "modal-fundo";
  fundo.id = "modal-ativo";
  fundo.innerHTML = `
    <div class="modal ${opcoes.largo ? "modal-largo" : ""}" role="dialog" aria-modal="true">
      <div class="modal-cabecalho">
        <h2>${escapar(titulo)}</h2>
        <button class="modal-fechar" aria-label="Fechar">&times;</button>
      </div>
      <div class="modal-corpo">${html}</div>
    </div>`;
  document.body.appendChild(fundo);

  fundo.querySelector(".modal-fechar").addEventListener("click", fecharModal);
  fundo.addEventListener("mousedown", (evento) => {
    if (evento.target === fundo) fecharModal();
  });
  fundo.querySelectorAll("[data-fechar]").forEach((botao) => botao.addEventListener("click", fecharModal));

  const primeiroCampo = fundo.querySelector("input, select, textarea");
  if (primeiroCampo) primeiroCampo.focus();

  return fundo.querySelector(".modal-corpo");
}

function fecharModal() {
  const modal = document.getElementById("modal-ativo");
  if (modal) modal.remove();
}

document.addEventListener("keydown", (evento) => {
  if (evento.key === "Escape") fecharModal();
});

function confirmar(mensagem, textoBotao = "Confirmar") {
  return new Promise((resolver) => {
    const corpo = abrirModal("Confirmação", `
      <p style="margin-top:0">${escapar(mensagem)}</p>
      <div class="modal-rodape">
        <button class="btn" id="confirmar-nao">Cancelar</button>
        <button class="btn btn-primario" id="confirmar-sim">${escapar(textoBotao)}</button>
      </div>`);
    corpo.querySelector("#confirmar-nao").addEventListener("click", () => {
      fecharModal();
      resolver(false);
    });
    corpo.querySelector("#confirmar-sim").addEventListener("click", () => {
      fecharModal();
      resolver(true);
    });
  });
}

async function enviarFormulario(formulario, acao) {
  const botao = formulario.querySelector("button[type=submit]");
  if (botao) botao.disabled = true;
  try {
    await acao();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  } finally {
    if (botao) botao.disabled = false;
  }
}

function dadosFormulario(formulario) {
  return Object.fromEntries(new FormData(formulario).entries());
}

async function baixarArquivo(anexo) {
  try {
    const blob = await Api.baixarAnexo(anexo.id);
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = anexo.nomeOriginal;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function htmlListaAnexos(anexos) {
  if (!anexos.length) {
    return `<p class="vazio" style="padding:14px">Nenhum anexo enviado.</p>`;
  }
  return `<ul class="lista-simples">${anexos
    .map(
      (anexo) => `
      <li>
        <div>
          <div>${escapar(anexo.nomeOriginal)}</div>
          <div class="meta">${formatarTamanho(anexo.tamanhoBytes)} · ${formatarDataHora(anexo.dataUpload)}</div>
        </div>
        <div class="acoes">
          <button class="btn btn-pequeno" data-baixar="${anexo.id}">Baixar</button>
          <button class="btn btn-pequeno btn-perigo" data-remover-anexo="${anexo.id}">Remover</button>
        </div>
      </li>`
    )
    .join("")}</ul>`;
}

async function montarSecaoAnexos(container, carregar, enviar) {
  async function atualizar() {
    const lista = container.querySelector(".lista-anexos");
    lista.innerHTML = `<p class="carregando">Carregando anexos...</p>`;
    try {
      const anexos = await carregar();
      lista.innerHTML = htmlListaAnexos(anexos);
      lista.querySelectorAll("[data-baixar]").forEach((botao) => {
        const anexo = anexos.find((a) => a.id == botao.dataset.baixar);
        botao.addEventListener("click", () => baixarArquivo(anexo));
      });
      lista.querySelectorAll("[data-remover-anexo]").forEach((botao) => {
        botao.addEventListener("click", async () => {
          try {
            await Api.removerAnexo(botao.dataset.removerAnexo);
            mostrarAviso("Anexo removido.", "sucesso");
            atualizar();
          } catch (erro) {
            mostrarAviso(erro.message, "erro");
          }
        });
      });
    } catch (erro) {
      lista.innerHTML = `<p class="vazio">${escapar(erro.message)}</p>`;
    }
  }

  container.innerHTML = `
    <div class="lista-anexos"></div>
    <div class="upload">
      <input type="file" accept=".pdf,.jpg,.jpeg,.png">
      <button class="btn btn-pequeno btn-primario">Enviar</button>
    </div>`;

  const campoArquivo = container.querySelector("input[type=file]");
  const botaoEnviar = container.querySelector(".upload .btn");
  botaoEnviar.addEventListener("click", async () => {
    if (!campoArquivo.files.length) {
      mostrarAviso("Escolha um arquivo para enviar.", "erro");
      return;
    }
    botaoEnviar.disabled = true;
    try {
      await enviar(campoArquivo.files[0]);
      campoArquivo.value = "";
      mostrarAviso("Anexo enviado.", "sucesso");
      atualizar();
    } catch (erro) {
      mostrarAviso(erro.message, "erro");
    } finally {
      botaoEnviar.disabled = false;
    }
  });

  atualizar();
}
