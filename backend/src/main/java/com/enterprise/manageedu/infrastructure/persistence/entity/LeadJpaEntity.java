package com.enterprise.manageedu.infrastructure.persistence.entity;

import com.enterprise.manageedu.domain.model.ClassificacaoLead;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leads")
public class LeadJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(nullable = false, length = 100)
    private String origem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClassificacaoLead classificacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_interesse_id", nullable = false)
    private CursoJpaEntity cursoInteresse;

    public LeadJpaEntity() {
    }

    public LeadJpaEntity(
            Long id,
            String nome,
            String email,
            String telefone,
            String origem,
            ClassificacaoLead classificacao,
            CursoJpaEntity cursoInteresse
    ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.origem = origem;
        this.classificacao = classificacao;
        this.cursoInteresse = cursoInteresse;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public ClassificacaoLead getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoLead classificacao) {
        this.classificacao = classificacao;
    }

    public CursoJpaEntity getCursoInteresse() {
        return cursoInteresse;
    }

    public void setCursoInteresse(CursoJpaEntity cursoInteresse) {
        this.cursoInteresse = cursoInteresse;
    }
}

