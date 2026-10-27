package com.enterprise.manageedu.application.dto;

public record LeadCandidatoResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String origem,
        String classificacao,
        Long cursoInteresseId,
        String cursoInteresseNome
) {
}
