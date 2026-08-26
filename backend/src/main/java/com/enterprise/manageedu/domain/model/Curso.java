package com.enterprise.manageedu.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade", nullable = false, length = 20)
    private ModalidadeCurso modalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 20)
    private TurnoCurso turno;

    protected Curso() {
    }

    public Curso(
            String nome,
            ModalidadeCurso modalidade,
            TurnoCurso turno
    ) {
        this.nome = nome;
        this.modalidade = modalidade;
        this.turno = turno;
    }

    public void atualizarDados(
            String nome,
            ModalidadeCurso modalidade,
            TurnoCurso turno
    ) {
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

    public ModalidadeCurso getModalidade() {
        return modalidade;
    }

    public TurnoCurso getTurno() {
        return turno;
    }
}