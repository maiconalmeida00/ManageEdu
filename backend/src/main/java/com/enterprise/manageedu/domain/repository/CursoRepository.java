package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository {
    Curso salvar(Curso curso);
    Optional<Curso> buscarPorId(Long id);
    List<Curso> listarTodos();
    void remover(Long id);
}
