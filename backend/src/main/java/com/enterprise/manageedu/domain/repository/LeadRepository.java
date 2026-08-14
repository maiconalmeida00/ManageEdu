package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.LeadCandidato;

import java.util.List;
import java.util.Optional;

public interface LeadRepository {
    LeadCandidato salvar(LeadCandidato lead);
    Optional<LeadCandidato> buscarPorId(Long id);
    List<LeadCandidato> listarTodos();
    void remover(Long id);
}
