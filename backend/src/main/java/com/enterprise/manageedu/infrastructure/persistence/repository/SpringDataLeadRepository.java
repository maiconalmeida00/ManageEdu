package com.enterprise.manageedu.infrastructure.persistence.repository;

import com.enterprise.manageedu.infrastructure.persistence.entity.LeadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataLeadRepository extends JpaRepository<LeadJpaEntity, Long> {

    boolean existsByCursoInteresseId(Long cursoId);
}

