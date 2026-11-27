const API_URL = "http://localhost:8080";

function lerCookie(nome) {
  const item = document.cookie.split("; ").find((c) => c.startsWith(nome + "="));
  return item ? decodeURIComponent(item.substring(nome.length + 1)) : null;
}

async function garantirCsrf() {
  if (!lerCookie("XSRF-TOKEN")) {
    await fetch(API_URL + "/api/csrf-token", { credentials: "include" });
  }
}

async function api(caminho, opcoes = {}) {
  const metodo = (opcoes.method || "GET").toUpperCase();
  const headers = {};
  let corpo = opcoes.body;

  if (corpo !== undefined && !(corpo instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    corpo = JSON.stringify(corpo);
  }

  if (metodo !== "GET") {
    await garantirCsrf();
    const token = lerCookie("XSRF-TOKEN");
    if (token) {
      headers["X-XSRF-TOKEN"] = token;
    }
  }

  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, {
      method: metodo,
      headers,
      body: corpo,
      credentials: "include",
    });
  } catch (erro) {
    throw new Error("Não foi possível conectar ao servidor. Verifique se o backend está rodando em " + API_URL + ".");
  }

  if (resposta.status === 401 && !opcoes.ignorar401) {
    window.location.href = "index.html";
    throw new Error("Sua sessão expirou. Faça login novamente.");
  }

  if (!resposta.ok) {
    let mensagem = "Erro " + resposta.status;
    try {
      const erro = await resposta.json();
      mensagem = erro.mensagem || erro.erro || mensagem;
    } catch (e) {}
    throw new Error(mensagem);
  }

  if (resposta.status === 204) {
    return null;
  }

  const tipo = resposta.headers.get("Content-Type") || "";
  if (tipo.includes("application/json")) {
    return resposta.json();
  }
  return resposta.blob();
}

const Api = {
  login: (email, senha) => api("/api/sessoes", { method: "POST", body: { email, senha }, ignorar401: true }),
  logout: () => api("/api/sessoes/atual", { method: "DELETE", ignorar401: true }),
  usuarioAtual: (opcoes = {}) => api("/api/sessoes/atual", opcoes),

  listarCursos: () => api("/api/cursos"),
  criarCurso: (dados) => api("/api/cursos", { method: "POST", body: dados }),
  atualizarCurso: (id, dados) => api("/api/cursos/" + id, { method: "PUT", body: dados }),
  removerCurso: (id) => api("/api/cursos/" + id, { method: "DELETE" }),

  listarLeads: () => api("/api/leads"),
  criarLead: (dados) => api("/api/leads", { method: "POST", body: dados }),
  atualizarLead: (id, dados) => api("/api/leads/" + id, { method: "PUT", body: dados }),
  removerLead: (id) => api("/api/leads/" + id, { method: "DELETE" }),

  listarOportunidades: () => api("/api/oportunidades"),
  funil: () => api("/api/oportunidades/funil"),
  buscarOportunidade: (id) => api("/api/oportunidades/" + id),
  criarOportunidade: (dados) => api("/api/oportunidades", { method: "POST", body: dados }),
  atualizarOportunidade: (id, dados) => api("/api/oportunidades/" + id, { method: "PUT", body: dados }),
  alterarStatus: (id, novoStatus) => api("/api/oportunidades/" + id + "/status", { method: "PATCH", body: { novoStatus } }),
  reabrirOportunidade: (id, justificativa) => api("/api/oportunidades/" + id + "/reaberturas", { method: "POST", body: { justificativa } }),
  removerOportunidade: (id) => api("/api/oportunidades/" + id, { method: "DELETE" }),

  listarUsuarios: () => api("/api/usuarios"),
  criarUsuario: (dados) => api("/api/usuarios", { method: "POST", body: dados }),
  atualizarUsuario: (id, dados) => api("/api/usuarios/" + id, { method: "PUT", body: dados }),
  alterarAtivo: (id, ativo) => api("/api/usuarios/" + id, { method: "PATCH", body: { ativo } }),

  urlFotoLead: (leadId) => API_URL + "/api/leads/" + leadId + "/foto",
  enviarFotoLead: (leadId, arquivo) => api("/api/leads/" + leadId + "/foto", { method: "POST", body: criarFormulario(arquivo) }),
  listarAnexosLead: (leadId) => api("/api/leads/" + leadId + "/anexos"),
  enviarAnexoLead: (leadId, arquivo) => api("/api/leads/" + leadId + "/anexos", { method: "POST", body: criarFormulario(arquivo) }),
  listarAnexosOportunidade: (id) => api("/api/oportunidades/" + id + "/anexos"),
  enviarAnexoOportunidade: (id, arquivo) => api("/api/oportunidades/" + id + "/anexos", { method: "POST", body: criarFormulario(arquivo) }),
  baixarAnexo: (id) => api("/api/anexos/" + id + "/arquivo"),
  removerAnexo: (id) => api("/api/anexos/" + id, { method: "DELETE" }),
};

function criarFormulario(arquivo) {
  const formulario = new FormData();
  formulario.append("arquivo", arquivo);
  return formulario;
}
