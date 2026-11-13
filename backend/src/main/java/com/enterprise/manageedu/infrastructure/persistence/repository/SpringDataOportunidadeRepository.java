package com.enterprise.manageedu.infrastructure.persistence.repository;

import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.infrastructure.persistence.entity.OportunidadeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataOportunidadeRepository extends JpaRepository<OportunidadeJpaEntity, Long> {

    List<OportunidadeJpaEntity> findByStatus(StatusOportunidade status);

    boolean existsByCursoId(Long cursoId);

    boolean existsByLeadCandidatoId(Long leadId);
}

