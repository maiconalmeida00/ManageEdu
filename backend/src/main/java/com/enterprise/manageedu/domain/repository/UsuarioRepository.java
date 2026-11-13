package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();

    boolean existePorEmail(String email);
}

