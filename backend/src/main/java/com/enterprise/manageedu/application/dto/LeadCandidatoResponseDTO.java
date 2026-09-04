package com.enterprise.manageedu.application.dto;

public record LeadCandidatoResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String origem,
        Long cursoInteresseId,
        String cursoInteresseNome
) {
}
