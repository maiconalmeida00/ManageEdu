package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;

import java.util.List;
import java.util.Optional;

public interface OportunidadeRepository {
    OportunidadeMatricula salvar(OportunidadeMatricula lead);
    Optional<OportunidadeMatricula> buscarPorId(Long id);
    List<OportunidadeMatricula> listarTodos();
    void remover(Long id);
}
