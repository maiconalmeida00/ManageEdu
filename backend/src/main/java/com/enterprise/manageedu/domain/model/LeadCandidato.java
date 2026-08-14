package com.enterprise.manageedu.domain.model;

public class LeadCandidato {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String origem;
    private Long cursoInteresseId;

    public LeadCandidato(Long id, String nome, String email, String telefone,
                         String origem, Long cursoInteresseId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.origem = origem;
        this.cursoInteresseId = cursoInteresseId;
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

    public Long getCursoInteresseId() {
        return cursoInteresseId;
    }
}
