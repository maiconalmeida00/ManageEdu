package com.enterprise.manageedu.infrastructure.persistence;

import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.infrastructure.persistence.entity.CursoJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataCursoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CursoRepositoryImpl implements CursoRepository {

    private final SpringDataCursoRepository springDataCursoRepository;

    public CursoRepositoryImpl(SpringDataCursoRepository springDataCursoRepository) {
        this.springDataCursoRepository = springDataCursoRepository;
    }

    @Override
    @Transactional
    public Curso salvar(Curso curso) {
        CursoJpaEntity entity;
        if (curso.getId() == null) {
            entity = new CursoJpaEntity(
                    null,
                    curso.getNome(),
                    curso.getModalidade(),
                    curso.getTurno()
            );
            entity = springDataCursoRepository.save(entity);
            curso.definirId(entity.getId());
        } else {
            entity = springDataCursoRepository.findById(curso.getId())
                    .orElseGet(() -> new CursoJpaEntity(curso.getId(), curso.getNome(), curso.getModalidade(), curso.getTurno()));
            entity.setNome(curso.getNome());
            entity.setModalidade(curso.getModalidade());
            entity.setTurno(curso.getTurno());
            springDataCursoRepository.save(entity);
        }
        return curso;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorId(Long id) {
        return springDataCursoRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return springDataCursoRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        springDataCursoRepository.deleteById(id);
    }

    public Curso toDomain(CursoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Curso curso = new Curso(entity.getNome(), entity.getModalidade(), entity.getTurno());
        curso.definirId(entity.getId());
        return curso;
    }

    public CursoJpaEntity toEntity(Curso curso) {
        if (curso == null) {
            return null;
        }
        return new CursoJpaEntity(
                curso.getId(),
                curso.getNome(),
                curso.getModalidade(),
                curso.getTurno()
        );
    }
}

