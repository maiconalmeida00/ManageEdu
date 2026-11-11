package com.enterprise.manageedu.infrastructure.persistence.entity;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
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
@Table(name = "reaberturas_oportunidade")
public class ReaberturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidade_id", nullable = false)
    private OportunidadeJpaEntity oportunidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "responsavel_id", nullable = false)
    private Long responsavelId;

    @Column(name = "responsavel_nome", nullable = false, length = 150)
    private String responsavelNome;

    @Column(nullable = false, length = 500)
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_origem", nullable = false, length = 30)
    private StatusOportunidade statusOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_destino", nullable = false, length = 30)
    private StatusOportunidade statusDestino;

    public ReaberturaJpaEntity() {
    }

    public ReaberturaJpaEntity(
            OportunidadeJpaEntity oportunidade,
            LocalDateTime dataHora,
            Long responsavelId,
            String responsavelNome,
            String justificativa,
            StatusOportunidade statusOrigem,
            StatusOportunidade statusDestino
    ) {
        this.oportunidade = oportunidade;
        this.dataHora = dataHora;
        this.responsavelId = responsavelId;
        this.responsavelNome = responsavelNome;
        this.justificativa = justificativa;
        this.statusOrigem = statusOrigem;
        this.statusDestino = statusDestino;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OportunidadeJpaEntity getOportunidade() {
        return oportunidade;
    }

    public void setOportunidade(OportunidadeJpaEntity oportunidade) {
        this.oportunidade = oportunidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public StatusOportunidade getStatusOrigem() {
        return statusOrigem;
    }

    public void setStatusOrigem(StatusOportunidade statusOrigem) {
        this.statusOrigem = statusOrigem;
    }

    public StatusOportunidade getStatusDestino() {
        return statusDestino;
    }

    public void setStatusDestino(StatusOportunidade statusDestino) {
        this.statusDestino = statusDestino;
    }
}

