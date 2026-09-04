package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringCursoJpaRepository extends JpaRepository<Curso, Long> {
}
