package com.enterprise.manageedu.infrastructure.persistence.entity;

import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;
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
public class CursoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModalidadeCurso modalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TurnoCurso turno;

    public CursoJpaEntity() {
    }

    public CursoJpaEntity(Long id, String nome, ModalidadeCurso modalidade, TurnoCurso turno) {
        this.id = id;
        this.nome = nome;
        this.modalidade = modalidade;
        this.turno = turno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ModalidadeCurso getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeCurso modalidade) {
        this.modalidade = modalidade;
    }

    public TurnoCurso getTurno() {
        return turno;
    }

    public void setTurno(TurnoCurso turno) {
        this.turno = turno;
    }
}

