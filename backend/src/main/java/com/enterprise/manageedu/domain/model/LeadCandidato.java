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
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("O e-mail do lead é obrigatório.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new RegraDeNegocioException("O e-mail do lead deve ter um formato básico válido.");
        }
        if (telefone == null || telefone.isBlank()) {
            throw new RegraDeNegocioException("O telefone do lead é obrigatório.");
        }
        if (origem == null || origem.isBlank()) {
            throw new RegraDeNegocioException("A origem do lead é obrigatória.");
        }
        if (cursoInteresse == null) {
            throw new RegraDeNegocioException("O curso de interesse do lead é obrigatório.");
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