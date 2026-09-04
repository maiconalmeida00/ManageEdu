package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;

public record CursoResponseDTO(
        Long id,
        String nome,
        ModalidadeCurso modalidade,
        TurnoCurso turno
) {
}
