package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

public class Curso {

    private Long id;
    private String nome;
    private ModalidadeCurso modalidade;
    private TurnoCurso turno;

    public Curso(String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        definirDados(nome, modalidade, turno);
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

    public void atualizarDados(String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        definirDados(nome, modalidade, turno);
    }

    private void definirDados(String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do curso é obrigatório.");
        }
        if (modalidade == null) {
            throw new RegraDeNegocioException("A modalidade do curso é obrigatória.");
        }
        if (turno == null) {
            throw new RegraDeNegocioException("O turno do curso é obrigatório.");
        }

        this.nome = nome.trim();
        this.modalidade = modalidade;
        this.turno = turno;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public ModalidadeCurso getModalidade() {
        return modalidade;
    }

    public TurnoCurso getTurno() {
        return turno;
    }
}