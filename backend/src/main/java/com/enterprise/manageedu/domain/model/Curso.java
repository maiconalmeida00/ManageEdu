package com.enterprise.manageedu.domain.model;

public class Curso {
    private Long id;
    private String nome;
    private String modalidade;
    private String turno;

    public Curso(Long id, String nome, String modalidade, String turno) {
        this.id = id;
        this.nome = nome;
        this.modalidade = modalidade;
        this.turno = turno;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getTurno() {
        return turno;
    }
}
