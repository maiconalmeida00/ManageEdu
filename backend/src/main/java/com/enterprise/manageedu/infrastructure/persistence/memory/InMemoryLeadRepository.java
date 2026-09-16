package com.enterprise.manageedu.infrastructure.persistence.memory;

import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.LeadRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class InMemoryLeadRepository implements LeadRepository {

    private final LinkedHashMap<Long, LeadCandidato> leads = new LinkedHashMap<>();
    private long proximoId = 1;

    @Override
    public LeadCandidato salvar(LeadCandidato lead) {
        if (lead.getId() == null) {
            lead.definirId(proximoId++);
        }
        leads.put(lead.getId(), lead);
        return lead;
    }

    @Override
    public Optional<LeadCandidato> buscarPorId(Long id) {
        return Optional.ofNullable(leads.get(id));
    }

    @Override
    public List<LeadCandidato> listarTodos() {
        return new ArrayList<>(leads.values());
    }

    @Override
    public void remover(Long id) {
        leads.remove(id);
    }
}