package com.enterprise.manageedu.infrastructure.persistence.repository;

import com.enterprise.manageedu.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}

