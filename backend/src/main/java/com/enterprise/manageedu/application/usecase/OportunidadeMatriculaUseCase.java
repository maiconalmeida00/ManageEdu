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
import com.enterprise.manageedu.domain.model.Usuario;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            throw new RegraDeNegocioException("Requisição de oportunidade não pode ser nula.");
        }

        LeadCandidato lead = buscarLead(request.leadCandidatoId());
        Curso curso = buscarCurso(request.cursoId());

        OportunidadeMatricula oportunidade = new OportunidadeMatricula(
                lead, curso, request.valorEstimado(), request.dataPrevista(), request.observacao()
        );
        return toResponse(oportunidadeRepository.salvar(oportunidade));
    }

    public OportunidadeMatriculaResponseDTO buscarPorId(Long id) {
        return toResponse(buscarOportunidadeOuFalhar(id));
    }

    public List<OportunidadeMatriculaResponseDTO> listarTodos() {
        return oportunidadeRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OportunidadeMatriculaResponseDTO> listarPorStatus(StatusOportunidade status) {
        if (status == null) {
            throw new RegraDeNegocioException("Status da oportunidade não pode ser nulo.");
        }
        return oportunidadeRepository.listarPorStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    public Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> listarFunilPorStatus() {
        Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> funil = new LinkedHashMap<>();
        for (StatusOportunidade status : StatusOportunidade.values()) {
            funil.put(status, new ArrayList<>());
        }
        for (OportunidadeMatricula oportunidade : oportunidadeRepository.listarTodos()) {
            funil.get(oportunidade.getStatus()).add(toResponse(oportunidade));
        }
        return funil;
    }

    public OportunidadeMatriculaResponseDTO atualizarDados(Long id, OportunidadeMatriculaRequestDTO request) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de oportunidade não pode ser nula.");
        }

        OportunidadeMatricula existente = buscarOportunidadeOuFalhar(id);
        LeadCandidato lead = buscarLead(request.leadCandidatoId());
        Curso curso = buscarCurso(request.cursoId());

        existente.atualizarDados(
                lead, curso, request.valorEstimado(), request.dataPrevista(), request.observacao()
        );
        return toResponse(oportunidadeRepository.salvar(existente));
    }

    // Reabertura é um fluxo separado da alteração comum de status.
    public OportunidadeMatriculaResponseDTO reabrirOportunidade(
            Long id,
            String justificativa,
            Usuario usuario
    ) {
        validarId(id);
        if (usuario == null) {
            throw new RegraDeNegocioException("Usuário responsável pela reabertura é obrigatório.");
        }
        if (!usuario.podeReabrirOportunidade()) {
            throw new RegraDeNegocioException("Somente administradores podem reabrir oportunidades.");
        }

        OportunidadeMatricula oportunidade = buscarOportunidadeOuFalhar(id);
        oportunidade.reabrir(justificativa, usuario);
        return toResponse(oportunidadeRepository.salvar(oportunidade));
    }

    public OportunidadeMatriculaResponseDTO alterarStatus(
            Long id,
            AtualizarStatusOportunidadeDTO request,
            Usuario usuario
    ) {
        validarId(id);
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de atualização de status não pode ser nula.");
        }
        if (request.novoStatus() == null) {
            throw new RegraDeNegocioException("O novo status é obrigatório.");
        }
        if (usuario == null) {
            throw new RegraDeNegocioException("Usuário responsável pela alteração é obrigatório.");
        }

        OportunidadeMatricula oportunidade = buscarOportunidadeOuFalhar(id);
        if (!usuario.podeAlterarStatus(oportunidade.getStatus(), request.novoStatus())) {
            throw new RegraDeNegocioException("Transição de status não permitida.");
        }

        oportunidade.alterarStatus(request.novoStatus());
        return toResponse(oportunidadeRepository.salvar(oportunidade));
    }

    public void remover(Long id) {
        buscarOportunidadeOuFalhar(id);
        oportunidadeRepository.remover(id);
    }

    private OportunidadeMatricula buscarOportunidadeOuFalhar(Long id) {
        validarId(id);
        return oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade não encontrada."));
    }

    private LeadCandidato buscarLead(Long leadId) {
        if (leadId == null || leadId <= 0) {
            throw new RegraDeNegocioException("O lead da oportunidade é obrigatório.");
        }
        return leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));
    }

    private Curso buscarCurso(Long cursoId) {
        if (cursoId == null || cursoId <= 0) {
            throw new RegraDeNegocioException("O curso da oportunidade é obrigatório.");
        }
        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID da oportunidade inválido.");
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
                oportunidade.getValorEstimado(),
                oportunidade.getDataPrevista(),
                oportunidade.getReaberturas(),
                oportunidade.getObservacao()
        );
    }
}
