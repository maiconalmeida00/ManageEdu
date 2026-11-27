const http = require("http");
const fs = require("fs");
const path = require("path");

const PORTA = 5173;
const HOST = "localhost";
const PASTA = __dirname;

const TIPOS = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".svg": "image/svg+xml",
  ".ico": "image/x-icon",
  ".webp": "image/webp",
};

const servidor = http.createServer((requisicao, resposta) => {
  const caminhoUrl = decodeURIComponent(new URL(requisicao.url, `http://${HOST}`).pathname);
  const relativo = caminhoUrl === "/" ? "index.html" : caminhoUrl;
  const arquivo = path.normalize(path.join(PASTA, relativo));

  if (!arquivo.startsWith(PASTA)) {
    resposta.writeHead(403);
    resposta.end("Acesso negado");
    return;
  }

  fs.readFile(arquivo, (erro, conteudo) => {
    if (erro) {
      resposta.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
      resposta.end("Página não encontrada");
      return;
    }
    const tipo = TIPOS[path.extname(arquivo).toLowerCase()] || "application/octet-stream";
    resposta.writeHead(200, { "Content-Type": tipo, "Cache-Control": "no-cache" });
    resposta.end(conteudo);
  });
});

servidor.listen(PORTA, HOST, () => {
  console.log(`Frontend do ManageEdu rodando em http://${HOST}:${PORTA}`);
  console.log("Pressione Ctrl+C para parar.");
});

servidor.on("error", (erro) => {
  if (erro.code === "EADDRINUSE") {
    console.error(`A porta ${PORTA} já está em uso. Feche o outro servidor e tente de novo.`);
  } else {
    console.error(erro.message);
  }
  process.exit(1);
});
