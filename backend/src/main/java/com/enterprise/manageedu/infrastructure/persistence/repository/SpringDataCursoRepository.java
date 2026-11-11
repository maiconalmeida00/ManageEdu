package com.enterprise.manageedu.infrastructure.persistence.repository;

import com.enterprise.manageedu.infrastructure.persistence.entity.CursoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCursoRepository extends JpaRepository<CursoJpaEntity, Long> {
}

