package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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

    @Transactional
    public OportunidadeMatriculaResponseDTO criar(
            OportunidadeMatriculaRequestDTO dto
    ) {
        LeadCandidato lead = buscarLeadPorId(dto.leadId());
        Curso curso = buscarCursoPorId(dto.cursoId());

        OportunidadeMatricula oportunidade = new OportunidadeMatricula(
                lead,
                curso,
                dto.observacao()
        );

        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);

        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    @Transactional(readOnly = true)
    public OportunidadeMatriculaResponseDTO buscarPorId(Long id) {
        OportunidadeMatricula oportunidade = buscarEntidadePorId(id);

        return OportunidadeMatriculaResponseDTO.fromDomain(oportunidade);
    }

    @Transactional(readOnly = true)
    public List<OportunidadeMatriculaResponseDTO> listarTodos() {
        return oportunidadeRepository.listarTodos()
                .stream()
                .map(OportunidadeMatriculaResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public OportunidadeMatriculaResponseDTO atualizar(
            Long id,
            OportunidadeMatriculaRequestDTO dto
    ) {
        OportunidadeMatricula oportunidade = buscarEntidadePorId(id);

        LeadCandidato lead = buscarLeadPorId(dto.leadId());
        Curso curso = buscarCursoPorId(dto.cursoId());

        oportunidade.atualizarDados(
                lead,
                curso,
                dto.observacao()
        );

        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);

        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    @Transactional
    public OportunidadeMatriculaResponseDTO atualizarStatus(
            Long id,
            AtualizarStatusOportunidadeDTO dto
    ) {
        OportunidadeMatricula oportunidade = buscarEntidadePorId(id);

        oportunidade.alterarStatus(dto.status());

        if (dto.observacao() != null && !dto.observacao().isBlank()) {
            oportunidade.atualizarObservacao(dto.observacao());
        }

        OportunidadeMatricula salva = oportunidadeRepository.salvar(oportunidade);

        return OportunidadeMatriculaResponseDTO.fromDomain(salva);
    }

    @Transactional
    public void deletar(Long id) {
        OportunidadeMatricula oportunidade = buscarEntidadePorId(id);

        oportunidadeRepository.remover(oportunidade.getId());
    }

    private OportunidadeMatricula buscarEntidadePorId(Long id) {
        return oportunidadeRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Oportunidade de matrícula não encontrada com o ID: " + id
                ));
    }

    private LeadCandidato buscarLeadPorId(Long leadId) {
        if (leadId == null) {
            throw new RegraDeNegocioException("O lead candidato é obrigatório.");
        }

        return leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead não encontrado com o ID: " + leadId
                ));
    }

    private Curso buscarCursoPorId(Long cursoId) {
        if (cursoId == null) {
            throw new RegraDeNegocioException("O curso é obrigatório.");
        }

        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso não encontrado com o ID: " + cursoId
                ));
    }
}