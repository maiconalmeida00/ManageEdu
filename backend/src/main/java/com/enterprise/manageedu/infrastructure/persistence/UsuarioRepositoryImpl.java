package com.enterprise.manageedu.infrastructure.persistence;

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
        UsuarioJpaEntity entity = usuario.getId() != null
                ? springDataUsuarioRepository.findById(usuario.getId())
                        .orElseGet(() -> new UsuarioJpaEntity(usuario.getId(), null, null, null, null, false))
                : new UsuarioJpaEntity();

        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setSenhaHash(usuario.getSenhaHash());
        entity.setPerfil(usuario.getPerfil());
        entity.setAtivo(usuario.isAtivo());

        UsuarioJpaEntity salvo = springDataUsuarioRepository.save(entity);
        if (usuario.getId() == null) {
            usuario.definirId(salvo.getId());
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
        return Usuario.criar(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getSenhaHash(),
                entity.getPerfil(),
                entity.isAtivo()
        );
    }
}

