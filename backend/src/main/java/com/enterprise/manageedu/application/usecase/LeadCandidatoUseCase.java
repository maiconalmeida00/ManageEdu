package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;

import java.util.List;
import java.util.stream.Collectors;

public class LeadCandidatoUseCase {

    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;

    public LeadCandidatoUseCase(LeadRepository leadRepository, CursoRepository cursoRepository) {
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
    }

    public LeadCandidatoResponseDTO cadastrar(LeadCandidatoRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de lead nao pode ser nula.");
        }

        Curso cursoInteresse = buscarCurso(request.cursoInteresseId());

        LeadCandidato lead = new LeadCandidato(
                request.nome(),
                request.email(),
                request.telefone(),
                request.origem(),
                cursoInteresse
        );
        LeadCandidato salvo = leadRepository.salvar(lead);
        return toResponse(salvo);
    }

    public LeadCandidatoResponseDTO buscarPorId(Long id) {
        validarId(id);
        LeadCandidato lead = leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead nao encontrado para o ID: " + id));
        return toResponse(lead);
    }

    public List<LeadCandidatoResponseDTO> listarTodos() {
        return leadRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LeadCandidatoResponseDTO atualizar(Long id, LeadCandidatoRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de lead nao pode ser nula.");
        }

        LeadCandidato existente = leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead nao encontrado para o ID: " + id));

        Curso cursoInteresse = buscarCurso(request.cursoInteresseId());

        existente.atualizarDados(
                request.nome(),
                request.email(),
                request.telefone(),
                request.origem(),
                cursoInteresse
        );
        LeadCandidato salvo = leadRepository.salvar(existente);
        return toResponse(salvo);
    }

    public void remover(Long id) {
        validarId(id);
        leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead nao encontrado para o ID: " + id));
        leadRepository.remover(id);
    }

    private Curso buscarCurso(Long cursoId) {
        if (cursoId == null || cursoId <= 0) {
            throw new RegraDeNegocioException("O curso de interesse do lead e obrigatorio.");
        }
        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado para o ID: " + cursoId));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do lead deve ser maior que zero.");
        }
    }

    private LeadCandidatoResponseDTO toResponse(LeadCandidato lead) {
        Curso curso = lead.getCursoInteresse();
        return new LeadCandidatoResponseDTO(
                lead.getId(),
                lead.getNome(),
                lead.getEmail(),
                lead.getTelefone(),
                lead.getOrigem(),
                curso != null ? curso.getId() : null,
                curso != null ? curso.getNome() : null
        );
    }
}
