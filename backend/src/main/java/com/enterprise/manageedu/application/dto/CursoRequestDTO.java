package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;

public record CursoRequestDTO(
        String nome,
        ModalidadeCurso modalidade,
        TurnoCurso turno
) {
}
