package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;
import com.enterprise.manageedu.domain.repository.CursoRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CursoUseCase {

    private final CursoRepository cursoRepository;

    public CursoUseCase(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public CursoResponseDTO cadastrar(CursoRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de curso nao pode ser nula.");
        }

        Curso curso = new Curso(
            request.nome(),
            converterModalidade(request.modalidade()),
            converterTurno(request.turno())
        );
        Curso salvo = cursoRepository.salvar(curso);
        return toResponse(salvo);
    }

    public CursoResponseDTO buscarPorId(Long id) {
        validarId(id);
        Curso curso = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado para o ID: " + id));
        return toResponse(curso);
    }

    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CursoResponseDTO atualizar(Long id, CursoRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de curso nao pode ser nula.");
        }

        Curso existente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado para o ID: " + id));

        existente.atualizarDados(
            request.nome(),
            converterModalidade(request.modalidade()),
            converterTurno(request.turno())
        );
        Curso salvo = cursoRepository.salvar(existente);
        return toResponse(salvo);
    }

    public void remover(Long id) {
        validarId(id);
        cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado para o ID: " + id));
        cursoRepository.remover(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do curso deve ser maior que zero.");
        }
    }

    private ModalidadeCurso converterModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank()) {
            throw new RegraDeNegocioException("A modalidade do curso e obrigatoria.");
        }
        try {
            return ModalidadeCurso.valueOf(modalidade.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Modalidade de curso invalida.");
        }
    }

    private TurnoCurso converterTurno(String turno) {
        if (turno == null || turno.isBlank()) {
            throw new RegraDeNegocioException("O turno do curso e obrigatorio.");
        }
        try {
            return TurnoCurso.valueOf(turno.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Turno de curso inválido.");
        }
    }

    private CursoResponseDTO toResponse(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getNome(),
                curso.getModalidade(),
                curso.getTurno()
        );
    }
}
