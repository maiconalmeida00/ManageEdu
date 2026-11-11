package com.enterprise.manageedu.infrastructure.persistence;

import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.infrastructure.persistence.entity.CursoJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.entity.LeadJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataCursoRepository;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataLeadRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class LeadRepositoryImpl implements LeadRepository {

    private final SpringDataLeadRepository springDataLeadRepository;
    private final SpringDataCursoRepository springDataCursoRepository;
    private final CursoRepositoryImpl cursoRepositoryImpl;

    public LeadRepositoryImpl(
            SpringDataLeadRepository springDataLeadRepository,
            SpringDataCursoRepository springDataCursoRepository,
            CursoRepositoryImpl cursoRepositoryImpl
    ) {
        this.springDataLeadRepository = springDataLeadRepository;
        this.springDataCursoRepository = springDataCursoRepository;
        this.cursoRepositoryImpl = cursoRepositoryImpl;
    }

    @Override
    @Transactional
    public LeadCandidato salvar(LeadCandidato lead) {
        CursoJpaEntity cursoEntity = springDataCursoRepository.findById(lead.getCursoInteresse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso de interesse não encontrado no banco."));

        LeadJpaEntity entity;
        if (lead.getId() == null) {
            entity = new LeadJpaEntity(
                    null,
                    lead.getNome(),
                    lead.getEmail(),
                    lead.getTelefone(),
                    lead.getOrigem(),
                    lead.getClassificacao(),
                    cursoEntity
            );
            entity = springDataLeadRepository.save(entity);
            lead.definirId(entity.getId());
        } else {
            entity = springDataLeadRepository.findById(lead.getId())
                    .orElseGet(() -> new LeadJpaEntity(
                            lead.getId(),
                            lead.getNome(),
                            lead.getEmail(),
                            lead.getTelefone(),
                            lead.getOrigem(),
                            lead.getClassificacao(),
                            cursoEntity
                    ));
            entity.setNome(lead.getNome());
            entity.setEmail(lead.getEmail());
            entity.setTelefone(lead.getTelefone());
            entity.setOrigem(lead.getOrigem());
            entity.setClassificacao(lead.getClassificacao());
            entity.setCursoInteresse(cursoEntity);
            springDataLeadRepository.save(entity);
        }
        return lead;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LeadCandidato> buscarPorId(Long id) {
        return springDataLeadRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadCandidato> listarTodos() {
        return springDataLeadRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        springDataLeadRepository.deleteById(id);
    }

    public LeadCandidato toDomain(LeadJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Curso curso = cursoRepositoryImpl.toDomain(entity.getCursoInteresse());
        LeadCandidato lead = new LeadCandidato(
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getOrigem(),
                entity.getClassificacao(),
                curso
        );
        lead.definirId(entity.getId());
        return lead;
    }
}

