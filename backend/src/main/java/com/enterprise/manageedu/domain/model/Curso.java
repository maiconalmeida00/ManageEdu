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
        this.id = id;
    }

    public void atualizarDados(String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        definirDados(nome, modalidade, turno);
    }

    private void definirDados(String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do curso e obrigatorio.");
        }
        if (modalidade == null) {
            throw new RegraDeNegocioException("A modalidade do curso e obrigatoria.");
        }
        if (turno == null) {
            throw new RegraDeNegocioException("O turno do curso e obrigatorio.");
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