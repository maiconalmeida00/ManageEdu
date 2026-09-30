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
            throw new RegraDeNegocioException("O lead candidato da oportunidade é obrigatório.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade é obrigatório.");
        }
        validarConsistenciaCursoLead(leadCandidato, curso);

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.status = StatusOportunidade.NOVO_LEAD;
        this.dataCriacao = LocalDateTime.now();
        this.observacao = normalizarObservacao(observacao);
    }

    public void definirId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID inválido.");
        }
        if (this.id != null) {
            throw new RegraDeNegocioException("O ID já foi definido.");
        }
        this.id = id;
    }

    public void atualizarDados(LeadCandidato leadCandidato, Curso curso, String observacao) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato da oportunidade é obrigatório.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade é obrigatório.");
        }
        validarConsistenciaCursoLead(leadCandidato, curso);

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

    private void validarConsistenciaCursoLead(LeadCandidato leadCandidato, Curso curso) {
        if (leadCandidato.getCursoInteresse() == null || curso == null) {
            return;
        }
        if (!leadCandidato.getCursoInteresse().getId().equals(curso.getId())) {
            throw new RegraDeNegocioException("O curso da oportunidade deve ser igual ao curso de interesse do lead.");
        }
    }

    private String normalizarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String normalizada = observacao.trim();
        if (normalizada.length() > 500) {
            throw new RegraDeNegocioException("A observação da oportunidade deve ter no máximo 500 caracteres.");
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