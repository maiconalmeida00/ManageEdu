package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringOportunidadeJpaRepository extends JpaRepository<OportunidadeMatricula, Long> {

    List<OportunidadeMatricula> findByStatus(StatusOportunidade status);
}
