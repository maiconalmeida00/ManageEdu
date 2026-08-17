package com.enterprise.manageedu.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CursoRequestDTO(
    @NotBlank(message = "O nome do curso é obrigatório")
    String nome,

    @NotBlank(message = "A modalidade é obrigatória")
    String modalidade,

    @NotBlank(message = "O turno é obrigatório")
    String turno
) {}
