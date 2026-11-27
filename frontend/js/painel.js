async function carregarPainel() {
  const usuario = await iniciarPagina("painel");
  if (!usuario) return;

  document.getElementById("saudacao").textContent = "Olá, " + usuario.nome.split(" ")[0];

  try {
    const [cursos, leads, funil] = await Promise.all([Api.listarCursos(), Api.listarLeads(), Api.funil()]);

    const contagem = {};
    ORDEM_STATUS.forEach((status) => (contagem[status] = (funil[status] || []).length));
    const emAndamento = contagem.NOVO_LEAD + contagem.CONTATO + contagem.DOCUMENTACAO;

    document.getElementById("total-cursos").textContent = cursos.length;
    document.getElementById("total-leads").textContent = leads.length;
    document.getElementById("total-andamento").textContent = emAndamento;
    document.getElementById("total-matriculas").textContent = contagem.MATRICULA_CONFIRMADA;

    desenharFunil(contagem);
    desenharUltimosLeads(leads);
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function desenharFunil(contagem) {
  const maior = Math.max(1, ...Object.values(contagem));
  document.getElementById("grafico-funil").innerHTML = ORDEM_STATUS.map(
    (status) => `
    <div class="barra-funil">
      <span class="nome">${ROTULOS.status[status]}</span>
      <div class="trilho"><div class="preenchimento" style="width:${(contagem[status] / maior) * 100}%"></div></div>
      <span class="numero">${contagem[status]}</span>
    </div>`
  ).join("") + `<p style="margin:14px 0 0"><a href="oportunidades.html">Ver funil completo →</a></p>`;
}

function desenharUltimosLeads(leads) {
  const area = document.getElementById("ultimos-leads");
  if (!leads.length) {
    area.innerHTML = `<p class="vazio">Nenhum lead cadastrado ainda. <a href="leads.html">Cadastre o primeiro</a>.</p>`;
    return;
  }
  const recentes = [...leads].sort((a, b) => b.id - a.id).slice(0, 5);
  area.innerHTML = `<ul class="lista-simples">${recentes
    .map(
      (lead) => `
      <li>
        <div>
          <div>${escapar(lead.nome)}</div>
          <div class="meta">${escapar(lead.cursoInteresseNome || "—")}</div>
        </div>
        ${selo(ROTULOS.classificacao[lead.classificacao] || lead.classificacao, lead.classificacao)}
      </li>`
    )
    .join("")}</ul>`;
}

carregarPainel();
