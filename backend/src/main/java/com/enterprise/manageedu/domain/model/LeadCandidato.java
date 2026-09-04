package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leads_candidatos")
public class LeadCandidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String origem;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
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
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    public void definirId(Long id) {
        this.id = id;
    }

    public void atualizarDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    private void definirDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do lead e obrigatorio.");
        }
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("O email do lead e obrigatorio.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new RegraDeNegocioException("O email do lead deve ter um formato basico valido.");
        }
        if (telefone == null || telefone.isBlank()) {
            throw new RegraDeNegocioException("O telefone do lead e obrigatorio.");
        }
        if (origem == null || origem.isBlank()) {
            throw new RegraDeNegocioException("A origem do lead e obrigatoria.");
        }
        if (cursoInteresse == null) {
            throw new RegraDeNegocioException("O curso de interesse do lead e obrigatorio.");
        }

        this.nome = nome.trim();
        this.email = email.trim();
        this.telefone = telefone.trim();
        this.origem = origem.trim();
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
}
