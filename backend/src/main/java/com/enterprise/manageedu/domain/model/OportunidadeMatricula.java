package com.enterprise.manageedu.domain.model;

import java.time.LocalDateTime;

public class OportunidadeMatricula {
    private Long id;
    private Long leadId;
    private Long cursoId;
    private StatusOportunidade status;
    private LocalDateTime dataCriacao;
    private String observacao;

    public OportunidadeMatricula(Long id, Long leadId, Long cursoId,
                                 StatusOportunidade status,
                                 LocalDateTime dataCriacao,
                                 String observacao) {
        this.id = id;
        this.leadId = leadId;
        this.cursoId = cursoId;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public Long getLeadId() {
        return leadId;
    }

    public Long getCursoId() {
        return cursoId;
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
