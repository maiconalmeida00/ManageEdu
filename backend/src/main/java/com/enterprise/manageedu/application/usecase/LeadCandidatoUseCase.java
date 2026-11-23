package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.ClassificacaoLead;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.List;
import java.util.Locale;

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
                converterClassificacao(request.classificacao()),
                cursoInteresse
        );
        return toResponse(leadRepository.salvar(lead));
    }

    public LeadCandidatoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarLeadOuFalhar(id));
    }

    public List<LeadCandidatoResponseDTO> listarTodos() {
        return leadRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    public LeadCandidatoResponseDTO atualizar(Long id, LeadCandidatoRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de lead não pode ser nula.");
        }

        LeadCandidato existente = buscarLeadOuFalhar(id);
        Curso cursoInteresse = buscarCurso(request.cursoInteresseId());

        existente.atualizarDados(
                request.nome(),
                request.email(),
                request.telefone(),
                request.origem(),
                converterClassificacao(request.classificacao()),
                cursoInteresse
        );
        return toResponse(leadRepository.salvar(existente));
    }

    public void remover(Long id) {
        LeadCandidato lead = buscarLeadOuFalhar(id);

        if (oportunidadeRepository.existePorLeadId(lead.getId())) {
            throw new RegraDeNegocioException("Não é possível excluir o lead porque existem oportunidades vinculadas.");
        }

        leadRepository.remover(id);
    }

    private LeadCandidato buscarLeadOuFalhar(Long id) {
        validarId(id);
        return leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));
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
                lead.getClassificacao().name(),
                curso != null ? curso.getId() : null,
                curso != null ? curso.getNome() : null
        );
    }

    private ClassificacaoLead converterClassificacao(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("A classificação do lead é obrigatória.");
        }
        try {
            return ClassificacaoLead.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Classificação inválida. Use FRIO, MORNO ou QUENTE.");
        }
    }
}
