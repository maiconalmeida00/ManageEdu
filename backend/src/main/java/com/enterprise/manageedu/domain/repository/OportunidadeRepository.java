package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;

import java.util.List;
import java.util.Optional;

public interface OportunidadeRepository {

    OportunidadeMatricula salvar(OportunidadeMatricula oportunidade);

    Optional<OportunidadeMatricula> buscarPorId(Long id);

    List<OportunidadeMatricula> listarTodos();

    List<OportunidadeMatricula> listarPorStatus(StatusOportunidade status);

    void remover(Long id);
}
