package com.enterprise.manageedu.application.dto;

public record LeadCandidatoRequestDTO(
        String nome,
        String email,
        String telefone,
        String origem,
        Long cursoInteresseId
) {
}
