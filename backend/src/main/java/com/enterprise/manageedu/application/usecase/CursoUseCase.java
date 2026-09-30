package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CursoUseCase {

    private final CursoRepository cursoRepository;
    private final LeadRepository leadRepository;
    private final OportunidadeRepository oportunidadeRepository;

    public CursoUseCase(
            CursoRepository cursoRepository,
            LeadRepository leadRepository,
            OportunidadeRepository oportunidadeRepository
    ) {
        this.cursoRepository = cursoRepository;
        this.leadRepository = leadRepository;
        this.oportunidadeRepository = oportunidadeRepository;
    }

    public CursoResponseDTO cadastrar(CursoRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de curso não pode ser nula.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));
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
            throw new RegraDeNegocioException("Requisição de curso não pode ser nula.");
        }

        Curso existente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));

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
        Curso curso = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));

        boolean vinculadoLead = leadRepository.listarTodos().stream()
                .anyMatch(lead -> lead.getCursoInteresse() != null
                        && lead.getCursoInteresse().getId() != null
                        && lead.getCursoInteresse().getId().equals(curso.getId()));

        boolean vinculadoOportunidade = oportunidadeRepository.listarTodos().stream()
                .anyMatch(oportunidade -> oportunidade.getCurso() != null
                        && oportunidade.getCurso().getId() != null
                        && oportunidade.getCurso().getId().equals(curso.getId()));

        if (vinculadoLead || vinculadoOportunidade) {
            throw new RegraDeNegocioException("Não é possível excluir o curso porque existem leads ou oportunidades vinculados.");
        }

        cursoRepository.remover(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do curso inválido.");
        }
    }

    private ModalidadeCurso converterModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank()) {
            throw new RegraDeNegocioException("A modalidade do curso é obrigatória.");
        }
        try {
            return ModalidadeCurso.valueOf(modalidade.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Modalidade de curso inválida.");
        }
    }

    private TurnoCurso converterTurno(String turno) {
        if (turno == null || turno.isBlank()) {
            throw new RegraDeNegocioException("O turno do curso é obrigatório.");
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
