package com.enterprise.manageedu.domain.repository;

import com.enterprise.manageedu.domain.model.Anexo;
import com.enterprise.manageedu.domain.model.TipoAnexo;

import java.util.List;
import java.util.Optional;

public interface AnexoRepository {

    Anexo salvar(Anexo anexo);

    Optional<Anexo> buscarPorId(Long id);

    List<Anexo> listarPorLeadIdETipo(Long leadId, TipoAnexo tipo);

    List<Anexo> listarPorOportunidadeId(Long oportunidadeId);

    void remover(Long id);
}

