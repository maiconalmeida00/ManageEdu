package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaLeadRepository implements LeadRepository {

    private final SpringLeadJpaRepository springRepository;

    public JpaLeadRepository(SpringLeadJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    @Transactional
    public LeadCandidato salvar(LeadCandidato lead) {
        return springRepository.save(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LeadCandidato> buscarPorId(Long id) {
        return springRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadCandidato> listarTodos() {
        return new ArrayList<>(springRepository.findAll());
    }

    @Override
    @Transactional
    public void remover(Long id) {
        try {
            springRepository.deleteById(id);
            springRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o lead porque existem oportunidades vinculadas."
            );
        }
    }
}
