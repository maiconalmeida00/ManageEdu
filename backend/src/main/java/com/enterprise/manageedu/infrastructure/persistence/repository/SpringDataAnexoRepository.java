package com.enterprise.manageedu.infrastructure.persistence.repository;

import com.enterprise.manageedu.domain.model.TipoAnexo;
import com.enterprise.manageedu.infrastructure.persistence.entity.AnexoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAnexoRepository extends JpaRepository<AnexoJpaEntity, Long> {

    List<AnexoJpaEntity> findByLeadIdAndTipoOrderByDataUploadDesc(Long leadId, TipoAnexo tipo);

    List<AnexoJpaEntity> findByOportunidadeIdOrderByDataUploadDesc(Long oportunidadeId);
}

