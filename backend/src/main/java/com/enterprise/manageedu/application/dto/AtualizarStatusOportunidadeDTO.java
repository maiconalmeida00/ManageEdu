package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusOportunidadeDTO(
    @NotNull(message = "O novo status é obrigatório")
    StatusOportunidade status,

    String observacao
) {}
