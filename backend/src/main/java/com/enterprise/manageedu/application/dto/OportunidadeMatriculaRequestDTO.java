package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
import jakarta.validation.constraints.NotNull;

public record OportunidadeMatriculaRequestDTO(
    @NotNull(message = "O ID do lead é obrigatório")
    Long leadId,

    @NotNull(message = "O ID do curso é obrigatório")
    Long cursoId,

    StatusOportunidade status,

    String observacao
) {}
