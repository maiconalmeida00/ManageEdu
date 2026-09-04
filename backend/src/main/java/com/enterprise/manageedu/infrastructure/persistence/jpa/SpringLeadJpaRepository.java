package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.model.LeadCandidato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringLeadJpaRepository extends JpaRepository<LeadCandidato, Long> {
}
