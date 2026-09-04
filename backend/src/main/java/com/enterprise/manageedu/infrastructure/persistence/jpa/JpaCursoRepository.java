package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaCursoRepository implements CursoRepository {

    private final SpringCursoJpaRepository springRepository;

    public JpaCursoRepository(SpringCursoJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    @Transactional
    public Curso salvar(Curso curso) {
        return springRepository.save(curso);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorId(Long id) {
        return springRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return new ArrayList<>(springRepository.findAll());
    }

    @Override
    @Transactional
    public void remover(Long id) {
        try {
            springRepository.deleteById(id);
            springRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o curso porque existem leads ou oportunidades vinculados."
            );
        }
    }
}
