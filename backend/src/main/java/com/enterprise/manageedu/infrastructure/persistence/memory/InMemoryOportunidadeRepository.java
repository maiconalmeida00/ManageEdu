package com.enterprise.manageedu.infrastructure.persistence.memory;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class InMemoryOportunidadeRepository implements OportunidadeRepository {

    private final LinkedHashMap<Long, OportunidadeMatricula> oportunidades = new LinkedHashMap<>();
    private long proximoId = 1;

    @Override
    public OportunidadeMatricula salvar(OportunidadeMatricula oportunidade) {
        if (oportunidade.getId() == null) {
            oportunidade.definirId(proximoId++);
        }
        oportunidades.put(oportunidade.getId(), oportunidade);
        return oportunidade;
    }

    @Override
    public Optional<OportunidadeMatricula> buscarPorId(Long id) {
        return Optional.ofNullable(oportunidades.get(id));
    }

    @Override
    public List<OportunidadeMatricula> listarTodos() {
        return new ArrayList<>(oportunidades.values());
    }

    @Override
    public List<OportunidadeMatricula> listarPorStatus(StatusOportunidade status) {
        return oportunidades.values().stream()
                .filter(oportunidade -> oportunidade.getStatus() == status)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public void remover(Long id) {
        oportunidades.remove(id);
    }
}