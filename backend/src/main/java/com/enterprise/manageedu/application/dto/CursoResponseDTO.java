package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.Curso;

public record CursoResponseDTO(
    Long id,
    String nome,
    String modalidade,
    String turno
) {
    public static CursoResponseDTO fromDomain(Curso curso) {
        if (curso == null) return null;
        return new CursoResponseDTO(
            curso.getId(),
            curso.getNome(),
            curso.getModalidade(),
            curso.getTurno()
        );
    }
}
