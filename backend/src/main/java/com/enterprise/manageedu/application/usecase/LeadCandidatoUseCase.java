package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.List;
import java.util.stream.Collectors;

public class LeadCandidatoUseCase {

    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;
    private final OportunidadeRepository oportunidadeRepository;

    public LeadCandidatoUseCase(
            LeadRepository leadRepository,
            CursoRepository cursoRepository,
            OportunidadeRepository oportunidadeRepository
    ) {
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
        this.oportunidadeRepository = oportunidadeRepository;
    }

    public LeadCandidatoResponseDTO cadastrar(LeadCandidatoRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de lead não pode ser nula.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));
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
            throw new RegraDeNegocioException("Requisição de lead não pode ser nula.");
        }

        LeadCandidato existente = leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));

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
        LeadCandidato lead = leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));

        boolean possuiOportunidade = oportunidadeRepository.listarTodos().stream()
                .anyMatch(oportunidade -> oportunidade.getLeadCandidato() != null
                        && oportunidade.getLeadCandidato().getId() != null
                        && oportunidade.getLeadCandidato().getId().equals(lead.getId()));

        if (possuiOportunidade) {
            throw new RegraDeNegocioException("Não é possível excluir o lead porque existem oportunidades vinculadas.");
        }

        leadRepository.remover(id);
    }

    private Curso buscarCurso(Long cursoId) {
        if (cursoId == null || cursoId <= 0) {
            throw new RegraDeNegocioException("O curso de interesse do lead é obrigatório.");
        }
        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do lead inválido.");
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
