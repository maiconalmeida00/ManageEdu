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
                converterEnum(request.modalidade(), ModalidadeCurso.class,
                        "A modalidade do curso é obrigatória.", "Modalidade de curso inválida."),
                converterEnum(request.turno(), TurnoCurso.class,
                        "O turno do curso é obrigatório.", "Turno de curso inválido.")
        );
        return toResponse(cursoRepository.salvar(curso));
    }

    public CursoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarCursoOuFalhar(id));
    }

    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    public CursoResponseDTO atualizar(Long id, CursoRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de curso não pode ser nula.");
        }

        Curso existente = buscarCursoOuFalhar(id);
        existente.atualizarDados(
                request.nome(),
                converterEnum(request.modalidade(), ModalidadeCurso.class,
                        "A modalidade do curso é obrigatória.", "Modalidade de curso inválida."),
                converterEnum(request.turno(), TurnoCurso.class,
                        "O turno do curso é obrigatório.", "Turno de curso inválido.")
        );
        return toResponse(cursoRepository.salvar(existente));
    }

    public void remover(Long id) {
        Curso curso = buscarCursoOuFalhar(id);

        boolean vinculado = leadRepository.existePorCursoId(curso.getId())
                || oportunidadeRepository.existePorCursoId(curso.getId());
        if (vinculado) {
            throw new RegraDeNegocioException("Não é possível excluir o curso porque existem leads ou oportunidades vinculados.");
        }

        cursoRepository.remover(id);
    }

    private Curso buscarCursoOuFalhar(Long id) {
        validarId(id);
        return cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do curso inválido.");
        }
    }

    private <E extends Enum<E>> E converterEnum(
            String valor,
            Class<E> enumType,
            String mensagemObrigatorio,
            String mensagemInvalido
    ) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(mensagemObrigatorio);
        }
        try {
            return Enum.valueOf(enumType, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException(mensagemInvalido);
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
