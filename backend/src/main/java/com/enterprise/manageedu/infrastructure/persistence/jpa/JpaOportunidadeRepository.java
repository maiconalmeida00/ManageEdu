package com.enterprise.manageedu.infrastructure.persistence.jpa;

import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaOportunidadeRepository implements OportunidadeRepository {

    private final SpringOportunidadeJpaRepository springRepository;

    public JpaOportunidadeRepository(SpringOportunidadeJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    @Transactional
    public OportunidadeMatricula salvar(OportunidadeMatricula oportunidade) {
        return springRepository.save(oportunidade);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OportunidadeMatricula> buscarPorId(Long id) {
        return springRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadeMatricula> listarTodos() {
        return new ArrayList<>(springRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadeMatricula> listarPorStatus(StatusOportunidade status) {
        return new ArrayList<>(springRepository.findByStatus(status));
    }

    @Override
    @Transactional
    public void remover(Long id) {
        springRepository.deleteById(id);
    }
}
