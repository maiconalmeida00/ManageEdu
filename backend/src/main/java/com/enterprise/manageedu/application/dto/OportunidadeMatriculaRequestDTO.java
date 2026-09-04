package com.enterprise.manageedu.application.dto;

public record OportunidadeMatriculaRequestDTO(
        Long leadCandidatoId,
        Long cursoId,
        String observacao
) {
}
