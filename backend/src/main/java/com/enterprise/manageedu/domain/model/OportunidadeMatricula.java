package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "oportunidade_matricula")
public class OportunidadeMatricula {

    private static final int TAMANHO_MAXIMO_OBSERVACAO = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_candidato_id", nullable = false)
    private LeadCandidato leadCandidato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusOportunidade status;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "observacao", length = TAMANHO_MAXIMO_OBSERVACAO)
    private String observacao;

    protected OportunidadeMatricula() {
    }

    public OportunidadeMatricula(
            LeadCandidato leadCandidato,
            Curso curso,
            String observacao
    ) {
        validarLead(leadCandidato);
        validarCurso(curso);
        validarObservacao(observacao);

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.status = StatusOportunidade.NOVO_LEAD;
        this.dataCriacao = LocalDateTime.now();
        this.observacao = observacao;
    }

    @PrePersist
    private void definirDataCriacao() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }

    public void alterarStatus(StatusOportunidade novoStatus) {
        if (novoStatus == null) {
            throw new RegraDeNegocioException("O novo status é obrigatório.");
        }

        if (status == StatusOportunidade.MATRICULA_CONFIRMADA
                || status == StatusOportunidade.DESISTENCIA) {
            throw new RegraDeNegocioException(
                    "Não é possível alterar o status de uma oportunidade encerrada."
            );
        }

        boolean transicaoPermitida = switch (status) {
            case NOVO_LEAD ->
                    novoStatus == StatusOportunidade.CONTATO;

            case CONTATO ->
                    novoStatus == StatusOportunidade.DOCUMENTACAO
                            || novoStatus == StatusOportunidade.DESISTENCIA;

            case DOCUMENTACAO ->
                    novoStatus == StatusOportunidade.MATRICULA_CONFIRMADA
                            || novoStatus == StatusOportunidade.DESISTENCIA;

            case MATRICULA_CONFIRMADA, DESISTENCIA -> false;
        };

        if (!transicaoPermitida) {
            throw new RegraDeNegocioException(
                    "Transição inválida: " + status + " -> " + novoStatus
            );
        }

        this.status = novoStatus;
    }

    public void atualizarDados(
            LeadCandidato leadCandidato,
            Curso curso,
            String observacao
    ) {
        validarLead(leadCandidato);
        validarCurso(curso);
        validarObservacao(observacao);

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.observacao = observacao;
    }

    public void atualizarObservacao(String observacao) {
        validarObservacao(observacao);
        this.observacao = observacao;
    }

    private void validarLead(LeadCandidato leadCandidato) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato é obrigatório.");
        }
    }

    private void validarCurso(Curso curso) {
        if (curso == null) {
            throw new RegraDeNegocioException("O curso é obrigatório.");
        }
    }

    private void validarObservacao(String observacao) {
        if (observacao != null
                && observacao.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new RegraDeNegocioException(
                    "A observação deve ter no máximo "
                            + TAMANHO_MAXIMO_OBSERVACAO + " caracteres."
            );
        }
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