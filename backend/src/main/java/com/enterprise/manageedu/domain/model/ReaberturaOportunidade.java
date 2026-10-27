package com.enterprise.manageedu.domain.model;

import java.time.LocalDateTime;

public record ReaberturaOportunidade(
        LocalDateTime dataHora,
        Long responsavelId,
        String responsavelNome,
        String justificativa,
        StatusOportunidade statusOrigem,
        StatusOportunidade statusDestino
) {
}
