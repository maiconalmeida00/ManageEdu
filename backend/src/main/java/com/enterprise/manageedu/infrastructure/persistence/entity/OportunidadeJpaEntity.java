package com.enterprise.manageedu.infrastructure.persistence.entity;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oportunidades")
public class OportunidadeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_candidato_id", nullable = false)
    private LeadJpaEntity leadCandidato;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    private CursoJpaEntity curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOportunidade status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "valor_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorEstimado;

    @Column(name = "data_prevista", nullable = false)
    private LocalDate dataPrevista;

    @Column(length = 500)
    private String observacao;

    @OneToMany(mappedBy = "oportunidade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dataHora ASC")
    private List<ReaberturaJpaEntity> reaberturas = new ArrayList<>();

    public OportunidadeJpaEntity() {
    }

    public OportunidadeJpaEntity(
            Long id,
            LeadJpaEntity leadCandidato,
            CursoJpaEntity curso,
            StatusOportunidade status,
            LocalDateTime dataCriacao,
            BigDecimal valorEstimado,
            LocalDate dataPrevista,
            String observacao
    ) {
        this.id = id;
        this.leadCandidato = leadCandidato;
        this.curso = curso;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.valorEstimado = valorEstimado;
        this.dataPrevista = dataPrevista;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LeadJpaEntity getLeadCandidato() {
        return leadCandidato;
    }

    public void setLeadCandidato(LeadJpaEntity leadCandidato) {
        this.leadCandidato = leadCandidato;
    }

    public CursoJpaEntity getCurso() {
        return curso;
    }

    public void setCurso(CursoJpaEntity curso) {
        this.curso = curso;
    }

    public StatusOportunidade getStatus() {
        return status;
    }

    public void setStatus(StatusOportunidade status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ReaberturaJpaEntity> getReaberturas() {
        return reaberturas;
    }

    public void setReaberturas(List<ReaberturaJpaEntity> reaberturas) {
        this.reaberturas = reaberturas;
    }

    public void adicionarReabertura(ReaberturaJpaEntity reabertura) {
        reaberturas.add(reabertura);
        reabertura.setOportunidade(this);
    }
}

