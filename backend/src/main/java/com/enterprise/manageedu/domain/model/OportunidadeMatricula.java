package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.time.LocalDateTime;

public class OportunidadeMatricula {

    private Long id;
    private LeadCandidato leadCandidato;
    private Curso curso;
    private StatusOportunidade status;
    private LocalDateTime dataCriacao;
    private String observacao;

    public OportunidadeMatricula(
            LeadCandidato leadCandidato,
            Curso curso,
            String observacao
    ) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato da oportunidade e obrigatorio.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade e obrigatorio.");
        }

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.status = StatusOportunidade.NOVO_LEAD;
        this.dataCriacao = LocalDateTime.now();
        this.observacao = normalizarObservacao(observacao);
    }

    public void definirId(Long id) {
        this.id = id;
    }

    public void atualizarDados(LeadCandidato leadCandidato, Curso curso, String observacao) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato da oportunidade e obrigatorio.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade e obrigatorio.");
        }

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.observacao = normalizarObservacao(observacao);
    }

    public void alterarStatus(StatusOportunidade novoStatus) {
        StatusOportunidade.validarTransicao(this.status, novoStatus);
        this.status = novoStatus;
    }

    public void atualizarObservacao(String observacao) {
        this.observacao = normalizarObservacao(observacao);
    }

    private String normalizarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String normalizada = observacao.trim();
        if (normalizada.length() > 500) {
            throw new RegraDeNegocioException("A observacao da oportunidade deve ter no maximo 500 caracteres.");
        }
        return normalizada;
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