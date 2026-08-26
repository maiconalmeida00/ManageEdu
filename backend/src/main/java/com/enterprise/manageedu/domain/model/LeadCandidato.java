package com.enterprise.manageedu.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lead_candidato")
public class LeadCandidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "origem", nullable = false, length = 50)
    private String origem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_interesse_id", nullable = false)
    private Curso cursoInteresse;

    protected LeadCandidato() {
    }

    public LeadCandidato(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.origem = origem;
        this.cursoInteresse = cursoInteresse;
    }

    public void atualizarDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.origem = origem;
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

    public Long getCursoInteresseId() {
        return cursoInteresse != null ? cursoInteresse.getId() : null;
    }
}

