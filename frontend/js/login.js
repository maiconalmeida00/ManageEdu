const formLogin = document.getElementById("form-login");
const caixaErro = document.getElementById("erro-login");

async function verificarSessaoExistente() {
  try {
    await Api.usuarioAtual({ ignorar401: true });
    window.location.href = "painel.html";
  } catch (erro) {}
}

formLogin.addEventListener("submit", (evento) => {
  evento.preventDefault();
  caixaErro.classList.add("oculto");

  enviarFormulario(formLogin, async () => {
    const { email, senha } = dadosFormulario(formLogin);
    try {
      await Api.login(email, senha);
      await garantirCsrf();
      window.location.href = "painel.html";
    } catch (erro) {
      caixaErro.textContent = erro.message;
      caixaErro.classList.remove("oculto");
    }
  });
});

verificarSessaoExistente();
