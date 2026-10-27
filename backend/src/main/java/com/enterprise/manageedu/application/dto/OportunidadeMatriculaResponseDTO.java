package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.model.ReaberturaOportunidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OportunidadeMatriculaResponseDTO(
        Long id,
        Long leadCandidatoId,
        String leadNome,
        Long cursoId,
        String cursoNome,
        StatusOportunidade status,
        LocalDateTime dataCriacao,
        BigDecimal valorEstimado,
        LocalDate dataPrevista,
        List<ReaberturaOportunidade> reaberturas,
        String observacao
) {
}
