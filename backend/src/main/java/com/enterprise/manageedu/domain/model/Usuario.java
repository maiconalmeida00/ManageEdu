package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

public abstract class Usuario {

    private Long id;
    private String nome;

    protected Usuario(Long id, String nome) {
        if (id == null || id <= 0 || nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("Usuário deve possuir ID e nome válidos.");
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