let oportunidades = [];
let leads = [];
let cursos = [];
let filtroAtual = "";

async function carregarOportunidades() {
  try {
    [oportunidades, leads, cursos] = await Promise.all([Api.listarOportunidades(), Api.listarLeads(), Api.listarCursos()]);
    desenharFiltros();
    desenharTabela();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function desenharFiltros() {
  const contar = (status) => oportunidades.filter((o) => !status || o.status === status).length;
  const filtros = [{ valor: "", rotulo: "Todas" }, ...ORDEM_STATUS.map((s) => ({ valor: s, rotulo: ROTULOS.status[s] }))];
  const area = document.getElementById("filtros-status");

  area.innerHTML = filtros
    .map(
      (f) => `<button class="filtro ${f.valor === filtroAtual ? "ativo" : ""}" data-status="${f.valor}">
        ${f.rotulo}<span class="contagem">${contar(f.valor)}</span>
      </button>`
    )
    .join("");

  area.querySelectorAll(".filtro").forEach((botao) =>
    botao.addEventListener("click", () => {
      filtroAtual = botao.dataset.status;
      desenharFiltros();
      desenharTabela();
    })
  );
}

function podeMoverPara(novoStatus) {
  return novoStatus !== "MATRICULA_CONFIRMADA" || ehAdmin();
}

function botoesDeStatus(oportunidade) {
  const botoes = TRANSICOES[oportunidade.status]
    .filter(podeMoverPara)
    .map((proximo) => {
      const classe = proximo === "DESISTENCIA" ? "btn-perigo" : "btn-primario";
      const texto = proximo === "DESISTENCIA" ? "Desistência" : "→ " + ROTULOS.status[proximo];
      return `<button class="btn btn-pequeno ${classe}" data-mover="${oportunidade.id}" data-para="${proximo}">${texto}</button>`;
    });

  if (oportunidade.status === "DESISTENCIA" && ehAdmin()) {
    botoes.push(`<button class="btn btn-pequeno btn-primario" data-reabrir="${oportunidade.id}">Reabrir</button>`);
  }
  return botoes.join("");
}

function desenharTabela() {
  const corpo = document.getElementById("tabela-oportunidades");
  const filtradas = oportunidades.filter((o) => !filtroAtual || o.status === filtroAtual).sort((a, b) => b.id - a.id);

  if (!filtradas.length) {
    corpo.innerHTML = `<tr><td colspan="6" class="vazio">${oportunidades.length ? "Nenhuma oportunidade nesta etapa." : "Nenhuma oportunidade cadastrada ainda."}</td></tr>`;
    return;
  }

  corpo.innerHTML = filtradas
    .map(
      (o) => `
      <tr>
        <td><strong>${escapar(o.leadNome)}</strong></td>
        <td>${escapar(o.cursoNome)}</td>
        <td>${formatarMoeda(o.valorEstimado)}</td>
        <td>${formatarData(o.dataPrevista)}</td>
        <td>${seloStatus(o.status)}</td>
        <td>
          <div class="acoes">
            ${botoesDeStatus(o)}
            <button class="btn btn-pequeno" data-detalhes="${o.id}">Detalhes</button>
          </div>
        </td>
      </tr>`
    )
    .join("");

  const buscar = (id) => oportunidades.find((o) => o.id == id);
  corpo.querySelectorAll("[data-mover]").forEach((b) => b.addEventListener("click", () => moverStatus(buscar(b.dataset.mover), b.dataset.para)));
  corpo.querySelectorAll("[data-reabrir]").forEach((b) => b.addEventListener("click", () => abrirReabertura(buscar(b.dataset.reabrir))));
  corpo.querySelectorAll("[data-detalhes]").forEach((b) => b.addEventListener("click", () => abrirDetalhes(buscar(b.dataset.detalhes))));
}

async function moverStatus(oportunidade, novoStatus) {
  const mensagem = `Mover a oportunidade de ${oportunidade.leadNome} para "${ROTULOS.status[novoStatus]}"?`;
  if (!(await confirmar(mensagem, "Mover"))) return;
  try {
    await Api.alterarStatus(oportunidade.id, novoStatus);
    mostrarAviso("Status alterado para " + ROTULOS.status[novoStatus] + ".", "sucesso");
    carregarOportunidades();
  } catch (erro) {
    mostrarAviso(erro.message, "erro");
  }
}

function abrirReabertura(oportunidade) {
  const corpo = abrirModal("Reabrir oportunidade", `
    <form id="form-reabrir">
      <p style="margin-top:0">A oportunidade de <strong>${escapar(oportunidade.leadNome)}</strong> voltará para a etapa <strong>Contato</strong>.</p>
      <div class="campo">
        <label for="justificativa">Justificativa</label>
        <textarea id="justificativa" name="justificativa" required placeholder="Candidato retornou o contato pedindo uma nova proposta."></textarea>
      </div>
      <div class="modal-rodape">
        <button type="button" class="btn" data-fechar>Cancelar</button>
        <button type="submit" class="btn btn-primario">Reabrir</button>
      </div>
    </form>`);

  const formulario = corpo.querySelector("#form-reabrir");
  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    enviarFormulario(formulario, async () => {
      await Api.reabrirOportunidade(oportunidade.id, dadosFormulario(formulario).justificativa.trim());
      mostrarAviso("Oportunidade reaberta.", "sucesso");
      fecharModal();
      carregarOportunidades();
    });
  });
}

function abrirFormulario(oportunidade) {
  if (!leads.length || !cursos.length) {
    mostrarAviso("Cadastre pelo menos um curso e um lead antes de criar oportunidades.", "erro");
    return;
  }

  const editando = Boolean(oportunidade);
  const opcoesLeads = leads
    .map((l) => `<option value="${l.id}" data-curso="${l.cursoInteresseId}" ${oportunidade?.leadCandidatoId === l.id ? "selected" : ""}>${escapar(l.nome)}</option>`)
    .join("");
  const opcoesCursos = cursos
    .map((c) => `<option value="${c.id}" ${oportunidade?.cursoId === c.id ? "selected" : ""}>${escapar(c.nome)}</option>`)
    .join("");

  const corpo = abrirModal(editando ? "Editar oportunidade" : "Nova oportunidade", `
    <form id="form-oportunidade">
      <div class="campo">
        <label for="leadCandidatoId">Lead</label>
        <select id="leadCandidatoId" name="leadCandidatoId" required>${opcoesLeads}</select>
      </div>
      <div class="campo">
        <label for="cursoId">Curso</label>
        <select id="cursoId" name="cursoId" required>${opcoesCursos}</select>
      </div>
      <div class="campo-linha">
        <div class="campo">
          <label for="valorEstimado">Valor estimado (R$)</label>
          <input id="valorEstimado" name="valorEstimado" type="number" min="0" step="0.01" value="${oportunidade?.valorEstimado ?? ""}" placeholder="1200,00">
        </div>
        <div class="campo">
          <label for="dataPrevista">Data prevista</label>
          <input id="dataPrevista" name="dataPrevista" type="date" value="${oportunidade?.dataPrevista ?? ""}">
        </div>
      </div>
      <div class="campo">
        <label for="observacao">Observação</label>
        <textarea id="observacao" name="observacao" placeholder="Interessado em bolsa de estudos">${escapar(oportunidade?.observacao)}</textarea>
      </div>
      <div class="modal-rodape">
        <button type="button" class="btn" data-fechar>Cancelar</button>
        <button type="submit" class="btn btn-primario">${editando ? "Salvar alterações" : "Criar oportunidade"}</button>
      </div>
    </form>`);

  const formulario = corpo.querySelector("#form-oportunidade");
  const seletorLead = formulario.querySelector("#leadCandidatoId");
  const seletorCurso = formulario.querySelector("#cursoId");

  if (!editando) {
    const sugerirCurso = () => {
      const curso = seletorLead.selectedOptions[0]?.dataset.curso;
      if (curso) seletorCurso.value = curso;
    };
    seletorLead.addEventListener("change", sugerirCurso);
    sugerirCurso();
  }

  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();
    enviarFormulario(formulario, async () => {
      const dados = dadosFormulario(formulario);
      const corpoRequisicao = {
        leadCandidatoId: Number(dados.leadCandidatoId),
        cursoId: Number(dados.cursoId),
        valorEstimado: dados.valorEstimado === "" ? null : Number(dados.valorEstimado),
        dataPrevista: dados.dataPrevista || null,
        observacao: dados.observacao.trim() || null,
      };
      if (editando) {
        await Api.atualizarOportunidade(oportunidade.id, corpoRequisicao);
        mostrarAviso("Oportunidade atualizada.", "sucesso");
      } else {
        await Api.criarOportunidade(corpoRequisicao);
        mostrarAviso("Oportunidade criada.", "sucesso");
      }
      fecharModal();
      carregarOportunidades();
    });
  });
}

function htmlReaberturas(reaberturas) {
  if (!reaberturas || !reaberturas.length) {
    return `<p class="vazio" style="padding:14px">Esta oportunidade nunca foi reaberta.</p>`;
  }
  return `<ul class="lista-simples">${reaberturas
    .map(
      (r) => `
      <li style="display:block">
        <div>${escapar(r.justificativa)}</div>
        <div class="meta">${formatarDataHora(r.dataHora)} · por ${escapar(r.responsavelNome)} · ${ROTULOS.status[r.statusOrigem] || r.statusOrigem} → ${ROTULOS.status[r.statusDestino] || r.statusDestino}</div>
      </li>`
    )
    .join("")}</ul>`;
}

function abrirDetalhes(oportunidade) {
  const corpo = abrirModal("Oportunidade de " + oportunidade.leadNome, `
    <dl class="detalhes">
      <dt>Lead</dt><dd>${escapar(oportunidade.leadNome)}</dd>
      <dt>Curso</dt><dd>${escapar(oportunidade.cursoNome)}</dd>
      <dt>Status</dt><dd>${seloStatus(oportunidade.status)}</dd>
      <dt>Valor estimado</dt><dd>${formatarMoeda(oportunidade.valorEstimado)}</dd>
      <dt>Data prevista</dt><dd>${formatarData(oportunidade.dataPrevista)}</dd>
      <dt>Criada em</dt><dd>${formatarDataHora(oportunidade.dataCriacao)}</dd>
      <dt>Observação</dt><dd>${escapar(oportunidade.observacao || "—")}</dd>
    </dl>

    <div class="secao">
      <h3>Histórico de reaberturas</h3>
      ${htmlReaberturas(oportunidade.reaberturas)}
    </div>

    <div class="secao">
      <h3>Anexos (PDF, JPG ou PNG até 10 MB)</h3>
      <div id="anexos-oportunidade"></div>
    </div>

    <div class="modal-rodape secao">
      <button class="btn btn-perigo" id="excluir-oportunidade">Excluir</button>
      <button class="btn btn-primario" id="editar-oportunidade">Editar dados</button>
    </div>`, { largo: true });

  montarSecaoAnexos(
    corpo.querySelector("#anexos-oportunidade"),
    () => Api.listarAnexosOportunidade(oportunidade.id),
    (arquivo) => Api.enviarAnexoOportunidade(oportunidade.id, arquivo)
  );

  corpo.querySelector("#editar-oportunidade").addEventListener("click", () => abrirFormulario(oportunidade));
  corpo.querySelector("#excluir-oportunidade").addEventListener("click", async () => {
    if (!(await confirmar(`Excluir a oportunidade de ${oportunidade.leadNome}?`, "Excluir"))) return;
    try {
      await Api.removerOportunidade(oportunidade.id);
      mostrarAviso("Oportunidade excluída.", "sucesso");
      carregarOportunidades();
    } catch (erro) {
      mostrarAviso(erro.message, "erro");
    }
  });
}

document.getElementById("botao-novo").addEventListener("click", () => abrirFormulario(null));

iniciarPagina("oportunidades").then((usuario) => usuario && carregarOportunidades());
