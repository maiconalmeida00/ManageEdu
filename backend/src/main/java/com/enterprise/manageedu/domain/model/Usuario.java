package com.enterprise.manageedu.domain.model;

public abstract class Usuario {

    private Long id;
    private String nome;

    protected Usuario(Long id, String nome) {
        if (id == null || id <= 0 || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Usuário deve possuir ID e nome válidos.");
        }
        this.id = id;
        this.nome = nome.trim();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public abstract boolean podeAlterarStatus(
            StatusOportunidade statusAtual,
            StatusOportunidade novoStatus
    );
}