package com.enterprise.manageedu.infrastructure.persistence.memory;

import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.repository.CursoRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class InMemoryCursoRepository implements CursoRepository {

    private final LinkedHashMap<Long, Curso> cursos = new LinkedHashMap<>();
    private long proximoId = 1;

    @Override
    public Curso salvar(Curso curso) {
        if (curso.getId() == null) {
            curso.definirId(proximoId++);
        }
        cursos.put(curso.getId(), curso);
        return curso;
    }

    @Override
    public Optional<Curso> buscarPorId(Long id) {
        return Optional.ofNullable(cursos.get(id));
    }

    @Override
    public List<Curso> listarTodos() {
        return new ArrayList<>(cursos.values());
    }

    @Override
    public void remover(Long id) {
        cursos.remove(id);
    }
}