package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OportunidadeMatricula {

    private Long id;
    private LeadCandidato leadCandidato;
    private Curso curso;
    private StatusOportunidade status;
    private LocalDateTime dataCriacao;
    private BigDecimal valorEstimado;
    private LocalDate dataPrevista;
    private String observacao;
    private final List<ReaberturaOportunidade> reaberturas = new ArrayList<>();

    public OportunidadeMatricula(
            LeadCandidato leadCandidato,
            Curso curso,
            BigDecimal valorEstimado,
            LocalDate dataPrevista,
            String observacao
    ) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato da oportunidade é obrigatório.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade é obrigatório.");
        }
        validarConsistenciaCursoLead(leadCandidato, curso);
        BigDecimal valorNormalizado = validarValorEstimado(valorEstimado);
        validarDataPrevista(dataPrevista, LocalDate.now(), false);

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.status = StatusOportunidade.NOVO_LEAD;
        this.dataCriacao = LocalDateTime.now();
        this.valorEstimado = valorNormalizado;
        this.dataPrevista = dataPrevista;
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

    public void atualizarDados(
            LeadCandidato leadCandidato,
            Curso curso,
            BigDecimal valorEstimado,
            LocalDate dataPrevista,
            String observacao
    ) {
        if (leadCandidato == null) {
            throw new RegraDeNegocioException("O lead candidato da oportunidade é obrigatório.");
        }
        if (curso == null) {
            throw new RegraDeNegocioException("O curso da oportunidade é obrigatório.");
        }
        validarConsistenciaCursoLead(leadCandidato, curso);
        BigDecimal valorNormalizado = validarValorEstimado(valorEstimado);
        validarDataPrevista(
                dataPrevista,
                LocalDate.now(),
                dataPrevista != null && dataPrevista.equals(this.dataPrevista)
        );
        String observacaoNormalizada = normalizarObservacao(observacao);

        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.valorEstimado = valorNormalizado;
        this.dataPrevista = dataPrevista;
        this.observacao = observacaoNormalizada;
    }

    public void alterarStatus(StatusOportunidade novoStatus) {
        StatusOportunidade.validarTransicao(this.status, novoStatus);
        this.status = novoStatus;
    }

    public void atualizarObservacao(String observacao) {
        this.observacao = normalizarObservacao(observacao);
    }

    public void reabrir(String justificativa, Usuario responsavel) {
        if (this.status != StatusOportunidade.DESISTENCIA) {
            throw new RegraDeNegocioException("Somente oportunidades em desistência podem ser reabertas.");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraDeNegocioException("A justificativa da reabertura é obrigatória.");
        }
        if (responsavel == null) {
            throw new RegraDeNegocioException("Usuário responsável pela reabertura é obrigatório.");
        }

        String justificativaNormalizada = justificativa.trim();
        this.reaberturas.add(new ReaberturaOportunidade(
                LocalDateTime.now(),
                responsavel.getId(),
                responsavel.getNome(),
                justificativaNormalizada,
                this.status,
                StatusOportunidade.CONTATO
        ));
        this.status = StatusOportunidade.CONTATO;
    }

    private BigDecimal validarValorEstimado(BigDecimal valorEstimado) {
        if (valorEstimado == null || valorEstimado.signum() <= 0) {
            throw new RegraDeNegocioException("O valor estimado deve ser maior que zero.");
        }
        if (valorEstimado.stripTrailingZeros().scale() > 2) {
            throw new RegraDeNegocioException("O valor estimado pode ter no máximo duas casas decimais.");
        }
        return valorEstimado.setScale(2, RoundingMode.UNNECESSARY);
    }

    private void validarDataPrevista(LocalDate dataPrevista, LocalDate hoje, boolean permiteDataVencidaMantida) {
        if (dataPrevista == null) {
            throw new RegraDeNegocioException("A data prevista é obrigatória.");
        }
        if (!permiteDataVencidaMantida && dataPrevista.isBefore(hoje)) {
            throw new RegraDeNegocioException("A data prevista não pode estar no passado.");
        }
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

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public List<ReaberturaOportunidade> getReaberturas() {
        return List.copyOf(reaberturas);
    }

    public String getObservacao() {
        return observacao;
    }
}