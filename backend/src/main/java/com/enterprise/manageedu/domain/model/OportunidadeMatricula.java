package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "oportunidades_matricula")
public class OportunidadeMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_candidato_id", nullable = false)
    private LeadCandidato leadCandidato;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOportunidade status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(length = 500)
    private String observacao;

    protected OportunidadeMatricula() {
    }

    public OportunidadeMatricula(LeadCandidato leadCandidato, Curso curso, String observacao) {
        definirDados(leadCandidato, curso, observacao);
    }

    public void definirId(Long id) {
        this.id = id;
    }

    public void atualizarDados(LeadCandidato leadCandidato, Curso curso, String observacao) {
        definirDados(leadCandidato, curso, observacao);
    }

    public void atualizarObservacao(String observacao) {
        this.observacao = normalizarObservacao(observacao);
    }

    private void definirDados(LeadCandidato leadCandidato, Curso curso, String observacao) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead da oportunidade e obrigatorio.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade e obrigatorio.");
        }

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.observacao = normalizarObservacao(observacao);

        if (this.status == null) {
            this.status = StatusOportunidade.NOVO_LEAD;
        }
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }

    public void alterarStatus(StatusOportunidade novoStatus) {
        if (novoStatus == null) {
            throw new RegraDeNegocioException("O novo status e obrigatorio.");
        }

        if (this.status == StatusOportunidade.MATRICULA_CONFIRMADA) {
            throw new RegraDeNegocioException(
                    "Oportunidade ja esta com matricula confirmada. Status final nao pode ser alterado."
            );
        }

        if (this.status == StatusOportunidade.DESISTENCIA) {
            throw new RegraDeNegocioException(
                    "Oportunidade ja esta com desistencia. Status final nao pode ser alterado."
            );
        }

        if (!transicaoPermitida(this.status, novoStatus)) {
            throw new RegraDeNegocioException(
                    "Transicao de status nao permitida: " + this.status + " -> " + novoStatus
            );
        }

        this.status = novoStatus;
    }

    private static boolean transicaoPermitida(StatusOportunidade atual, StatusOportunidade novo) {
        return switch (atual) {
            case NOVO_LEAD -> novo == StatusOportunidade.CONTATO
                    || novo == StatusOportunidade.DESISTENCIA;
            case CONTATO -> novo == StatusOportunidade.DOCUMENTACAO
                    || novo == StatusOportunidade.DESISTENCIA;
            case DOCUMENTACAO -> novo == StatusOportunidade.MATRICULA_CONFIRMADA
                    || novo == StatusOportunidade.DESISTENCIA;
            case MATRICULA_CONFIRMADA, DESISTENCIA -> false;
        };
    }

    private static String normalizarObservacao(String observacao) {
        if (observacao == null) {
            return null;
        }
        String normalizada = observacao.trim();
        if (normalizada.length() > 500) {
            throw new RegraDeNegocioException("A observacao nao pode ter mais de 500 caracteres.");
        }
        return normalizada.isEmpty() ? null : normalizada;
    }

    public Long getId() {
        return id;
    }

    public LeadCandidato getLeadCandidato() {
        return leadCandidato;
    }

    public Curso getCurso() {
        return curso;
    }

    public StatusOportunidade getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getObservacao() {
        return observacao;
    }
}
