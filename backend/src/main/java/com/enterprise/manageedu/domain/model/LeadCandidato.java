package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

public class LeadCandidato {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String origem;
    private Curso cursoInteresse;

    public LeadCandidato(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    public void definirId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID inválido.");
        }

        if (this.id != null) {
            throw new RegraDeNegocioException("O ID já foi definido.");
        }

        this.id = id;
    }

    public void atualizarDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    private void definirDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do lead é obrigatório.");
        }

        if (email == null || email.isBlank()
                || !email.contains("@")
                || !email.contains(".")) {
            throw new RegraDeNegocioException("E-mail inválido.");
        }

        if (telefone == null || telefone.isBlank()) {
            throw new RegraDeNegocioException("Telefone inválido.");
        }

        String numerosTelefone = telefone.replaceAll("\\D", "");

        if (numerosTelefone.length() < 8) {
            throw new RegraDeNegocioException("Telefone inválido.");
        }

        if (origem == null || origem.isBlank()) {
            throw new RegraDeNegocioException("A origem do lead é obrigatória.");
        }

        if (cursoInteresse == null) {
            throw new RegraDeNegocioException(
                    "O curso de interesse do lead é obrigatório."
            );
        }

        this.nome = nome.trim();
        this.email = email.trim();
        this.telefone = telefone.trim();
        this.origem = origem.trim();
        this.cursoInteresse = cursoInteresse;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getOrigem() {
        return origem;
    }

    public Curso getCursoInteresse() {
        return cursoInteresse;
    }
}