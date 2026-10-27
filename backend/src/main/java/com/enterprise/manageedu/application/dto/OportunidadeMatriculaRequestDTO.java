package com.enterprise.manageedu.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OportunidadeMatriculaRequestDTO(
        Long leadCandidatoId,
        Long cursoId,
        BigDecimal valorEstimado,
        LocalDate dataPrevista,
        String observacao
) {
}
