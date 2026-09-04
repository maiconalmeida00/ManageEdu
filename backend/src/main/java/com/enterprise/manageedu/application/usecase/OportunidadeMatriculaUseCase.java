package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OportunidadeMatriculaUseCase {

    private final OportunidadeRepository oportunidadeRepository;
    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;

    public OportunidadeMatriculaUseCase(
            OportunidadeRepository oportunidadeRepository,
            LeadRepository leadRepository,
            CursoRepository cursoRepository
    ) {
        this.oportunidadeRepository = oportunidadeRepository;
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
    }

    public OportunidadeMatriculaResponseDTO criar(OportunidadeMatriculaRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de oportunidade nao pode ser nula.");
        }

        LeadCandidato lead = buscarLead(request.leadCandidatoId());
        Curso curso = buscarCurso(request.cursoId());

        OportunidadeMatricula oportunidade = new OportunidadeMatricula(lead, curso, request.observacao());
        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);
        return toResponse(salva);
    }

    public OportunidadeMatriculaResponseDTO buscarPorId(Long id) {
        validarId(id);
        OportunidadeMatricula oportunidade = oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade nao encontrada para o ID: " + id));
        return toResponse(oportunidade);
    }

    public List<OportunidadeMatriculaResponseDTO> listarTodos() {
        return oportunidadeRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OportunidadeMatriculaResponseDTO> listarPorStatus(StatusOportunidade status) {
        if (status == null) {
            throw new RegraDeNegocioException("Status da oportunidade nao pode ser nulo.");
        }
        return oportunidadeRepository.listarPorStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> listarFunilPorStatus() {
        Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> funil = new LinkedHashMap<>();
        for (StatusOportunidade status : StatusOportunidade.values()) {
            funil.put(status, listarPorStatus(status));
        }
        return funil;
    }

    public OportunidadeMatriculaResponseDTO atualizarDados(Long id, OportunidadeMatriculaRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de oportunidade nao pode ser nula.");
        }

        OportunidadeMatricula existente = oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade nao encontrada para o ID: " + id));

        LeadCandidato lead = buscarLead(request.leadCandidatoId());
        Curso curso = buscarCurso(request.cursoId());

        existente.atualizarDados(lead, curso, request.observacao());
        OportunidadeMatricula salva = oportunidadeRepository.salvar(existente);
        return toResponse(salva);
    }

    public OportunidadeMatriculaResponseDTO alterarStatus(Long id, AtualizarStatusOportunidadeDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisicao de atualizacao de status nao pode ser nula.");
        }
        if (request.novoStatus() == null) {
            throw new RegraDeNegocioException("O novo status e obrigatorio.");
        }

        OportunidadeMatricula oportunidade = oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade nao encontrada para o ID: " + id));

        oportunidade.alterarStatus(request.novoStatus());
        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);
        return toResponse(salva);
    }

    public void remover(Long id) {
        validarId(id);
        oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade nao encontrada para o ID: " + id));
        oportunidadeRepository.remover(id);
    }

    private LeadCandidato buscarLead(Long leadId) {
        if (leadId == null || leadId <= 0) {
            throw new RegraDeNegocioException("O lead da oportunidade e obrigatorio.");
        }
        return leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead nao encontrado para o ID: " + leadId));
    }

    private Curso buscarCurso(Long cursoId) {
        if (cursoId == null || cursoId <= 0) {
            throw new RegraDeNegocioException("O curso da oportunidade e obrigatorio.");
        }
        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado para o ID: " + cursoId));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID da oportunidade deve ser maior que zero.");
        }
    }

    private OportunidadeMatriculaResponseDTO toResponse(OportunidadeMatricula oportunidade) {
        LeadCandidato lead = oportunidade.getLeadCandidato();
        Curso curso = oportunidade.getCurso();
        return new OportunidadeMatriculaResponseDTO(
                oportunidade.getId(),
                lead != null ? lead.getId() : null,
                lead != null ? lead.getNome() : null,
                curso != null ? curso.getId() : null,
                curso != null ? curso.getNome() : null,
                oportunidade.getStatus(),
                oportunidade.getDataCriacao(),
                oportunidade.getObservacao()
        );
    }
}
