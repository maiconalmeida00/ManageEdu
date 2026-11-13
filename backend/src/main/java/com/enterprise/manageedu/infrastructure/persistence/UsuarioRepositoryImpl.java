package com.enterprise.manageedu.infrastructure.persistence;

import com.enterprise.manageedu.domain.model.Administrador;
import com.enterprise.manageedu.domain.model.OperadorCaptacao;
import com.enterprise.manageedu.domain.model.PerfilUsuario;
import com.enterprise.manageedu.domain.model.Usuario;
import com.enterprise.manageedu.domain.repository.UsuarioRepository;
import com.enterprise.manageedu.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataUsuarioRepository;

    public UsuarioRepositoryImpl(SpringDataUsuarioRepository springDataUsuarioRepository) {
        this.springDataUsuarioRepository = springDataUsuarioRepository;
    }

    @Override
    @Transactional
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity;
        if (usuario.getId() == null) {
            entity = new UsuarioJpaEntity(
                    null,
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getSenhaHash(),
                    usuario.getPerfil(),
                    usuario.isAtivo()
            );
            entity = springDataUsuarioRepository.save(entity);
            usuario.definirId(entity.getId());
        } else {
            entity = springDataUsuarioRepository.findById(usuario.getId())
                    .orElseGet(() -> new UsuarioJpaEntity(
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getEmail(),
                            usuario.getSenhaHash(),
                            usuario.getPerfil(),
                            usuario.isAtivo()
                    ));
            entity.setNome(usuario.getNome());
            entity.setEmail(usuario.getEmail());
            entity.setSenhaHash(usuario.getSenhaHash());
            entity.setPerfil(usuario.getPerfil());
            entity.setAtivo(usuario.isAtivo());
            springDataUsuarioRepository.save(entity);
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return springDataUsuarioRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return springDataUsuarioRepository.findByEmailIgnoreCase(email.trim()).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return springDataUsuarioRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        if (email == null) {
            return false;
        }
        return springDataUsuarioRepository.existsByEmailIgnoreCase(email.trim());
    }

    private Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            return new Administrador(
                    entity.getId(),
                    entity.getNome(),
                    entity.getEmail(),
                    entity.getSenhaHash(),
                    entity.isAtivo()
            );
        }
        return new OperadorCaptacao(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getSenhaHash(),
                entity.isAtivo()
        );
    }
}

