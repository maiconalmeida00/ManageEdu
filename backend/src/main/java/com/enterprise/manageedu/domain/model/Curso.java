package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeCurso modalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurnoCurso turno;

    protected Curso() {
    }

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
