package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.LeadCandidato;

public record LeadCandidatoResponseDTO(
    Long id,
    String nome,
    String email,
    String telefone,
    String origem,
    Long cursoInteresseId
) {
    public static LeadCandidatoResponseDTO fromDomain(LeadCandidato lead) {
        if (lead == null) return null;
        return new LeadCandidatoResponseDTO(
            lead.getId(),
            lead.getNome(),
            lead.getEmail(),
            lead.getTelefone(),
            lead.getOrigem(),
            lead.getCursoInteresseId()
        );
    }
}
