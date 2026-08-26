package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;

import java.time.LocalDateTime;

public record OportunidadeMatriculaResponseDTO(
    Long id,
    Long leadId,
    Long cursoId,
    StatusOportunidade status,
    LocalDateTime dataCriacao,
    String observacao
) {
    public static OportunidadeMatriculaResponseDTO fromDomain(OportunidadeMatricula oportunidade) {
        if (oportunidade == null) return null;
        return new OportunidadeMatriculaResponseDTO(
            oportunidade.getId(),
            oportunidade.getLeadCandidato() != null ? oportunidade.getLeadCandidato().getId() : null,
            oportunidade.getCurso() != null ? oportunidade.getCurso().getId() : null,
            oportunidade.getStatus(),
            oportunidade.getDataCriacao(),
            oportunidade.getObservacao()
        );
    }
}
