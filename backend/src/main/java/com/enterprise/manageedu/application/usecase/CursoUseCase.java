package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CursoUseCase {

    private final CursoRepository cursoRepository;

    public CursoUseCase(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public CursoResponseDTO criar(CursoRequestDTO dto) {
        ModalidadeCurso modalidade = converterModalidade(dto.modalidade());
        TurnoCurso turno = converterTurno(dto.turno());

        Curso curso = new Curso(
                dto.nome(),
                modalidade,
                turno
        );

        Curso salvo = cursoRepository.salvar(curso);

        return CursoResponseDTO.fromDomain(salvo);
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO buscarPorId(Long id) {
        Curso curso = buscarEntidadePorId(id);

        return CursoResponseDTO.fromDomain(curso);
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.listarTodos()
                .stream()
                .map(CursoResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public CursoResponseDTO atualizar(
            Long id,
            CursoRequestDTO dto
    ) {
        Curso curso = buscarEntidadePorId(id);

        ModalidadeCurso modalidade = converterModalidade(dto.modalidade());
        TurnoCurso turno = converterTurno(dto.turno());

        curso.atualizarDados(
                dto.nome(),
                modalidade,
                turno
        );

        Curso salvo = cursoRepository.salvar(curso);

        return CursoResponseDTO.fromDomain(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        Curso curso = buscarEntidadePorId(id);

        cursoRepository.remover(curso.getId());
    }

    private Curso buscarEntidadePorId(Long id) {
        return cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso não encontrado com o ID: " + id
                ));
    }

    private ModalidadeCurso converterModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank()) {
            throw new RegraDeNegocioException(
                    "A modalidade do curso é obrigatória."
            );
        }

        try {
            return ModalidadeCurso.valueOf(
                    modalidade.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new RegraDeNegocioException(
                    "Modalidade inválida: " + modalidade
                            + ". Valores permitidos: PRESENCIAL, EAD, HIBRIDO."
            );
        }
    }

    private TurnoCurso converterTurno(String turno) {
        if (turno == null || turno.isBlank()) {
            throw new RegraDeNegocioException(
                    "O turno do curso é obrigatório."
            );
        }

        try {
            return TurnoCurso.valueOf(
                    turno.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new RegraDeNegocioException(
                    "Turno inválido: " + turno
                            + ". Valores permitidos: MATUTINO, VESPERTINO, "
                            + "NOTURNO, INTEGRAL."
            );
        }
    }
}