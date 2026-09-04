package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.StatusOportunidade;

public record AtualizarStatusOportunidadeDTO(
        StatusOportunidade novoStatus
) {
}
