package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OportunidadeMatriculaUseCase {

    private final OportunidadeRepository oportunidadeRepository;
    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;

    public OportunidadeMatriculaUseCase(OportunidadeRepository oportunidadeRepository,
                                        LeadRepository leadRepository,
                                        CursoRepository cursoRepository) {
        this.oportunidadeRepository = oportunidadeRepository;
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
    }

    public OportunidadeMatriculaResponseDTO criar(OportunidadeMatriculaRequestDTO dto) {
        validarLeadECurso(dto.leadId(), dto.cursoId());

        StatusOportunidade status = (dto.status() != null) ? dto.status() : StatusOportunidade.NOVO_LEAD;

        OportunidadeMatricula oportunidade = new OportunidadeMatricula(
                null,
                dto.leadId(),
                dto.cursoId(),
                status,
                LocalDateTime.now(),
                dto.observacao()
        );

        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);
        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    public OportunidadeMatriculaResponseDTO buscarPorId(Long id) {
        OportunidadeMatricula oportunidade = oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade de matrícula não encontrada com o ID: " + id));
        return OportunidadeMatriculaResponseDTO.fromDomain(oportunidade);
    }

    public List<OportunidadeMatriculaResponseDTO> listarTodos() {
        return oportunidadeRepository.listarTodos()
                .stream()
                .map(OportunidadeMatriculaResponseDTO::fromDomain)
                .toList();
    }

    public OportunidadeMatriculaResponseDTO atualizar(Long id, OportunidadeMatriculaRequestDTO dto) {
        OportunidadeMatricula existente = buscarOportunidadeEntidade(id);
        validarLeadECurso(dto.leadId(), dto.cursoId());

        StatusOportunidade novoStatus = (dto.status() != null) ? dto.status() : existente.getStatus();

        OportunidadeMatricula atualizada = new OportunidadeMatricula(
                id,
                dto.leadId(),
                dto.cursoId(),
                novoStatus,
                existente.getDataCriacao(),
                dto.observacao()
        );

        OportunidadeMatricula salva = oportunidadeRepository.salvar(atualizada);
        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    public OportunidadeMatriculaResponseDTO atualizarStatus(Long id, AtualizarStatusOportunidadeDTO dto) {
        OportunidadeMatricula existente = buscarOportunidadeEntidade(id);

        OportunidadeMatricula atualizada = new OportunidadeMatricula(
                existente.getId(),
                existente.getLeadId(),
                existente.getCursoId(),
                dto.status(),
                existente.getDataCriacao(),
                dto.observacao() != null ? dto.observacao() : existente.getObservacao()
        );

        OportunidadeMatricula salva = oportunidadeRepository.salvar(atualizada);
        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        oportunidadeRepository.remover(id);
    }

    private OportunidadeMatricula buscarOportunidadeEntidade(Long id) {
        return oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade de matrícula não encontrada com o ID: " + id));
    }

    private void validarLeadECurso(Long leadId, Long cursoId) {
        if (leadId != null && leadRepository.buscarPorId(leadId).isEmpty()) {
            throw new ResourceNotFoundException("Lead não encontrado com o ID: " + leadId);
        }
        if (cursoId != null && cursoRepository.buscarPorId(cursoId).isEmpty()) {
            throw new ResourceNotFoundException("Curso não encontrado com o ID: " + cursoId);
        }
    }
}
