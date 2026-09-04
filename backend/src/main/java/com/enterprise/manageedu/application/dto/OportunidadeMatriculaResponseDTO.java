package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.StatusOportunidade;

import java.time.LocalDateTime;

public record OportunidadeMatriculaResponseDTO(
        Long id,
        Long leadCandidatoId,
        String leadNome,
        Long cursoId,
        String cursoNome,
        StatusOportunidade status,
        LocalDateTime dataCriacao,
        String observacao
) {
}
