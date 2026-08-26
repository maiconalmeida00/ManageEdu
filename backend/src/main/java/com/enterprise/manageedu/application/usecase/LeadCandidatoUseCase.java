package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeadCandidatoUseCase {

    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;

    public LeadCandidatoUseCase(
            LeadRepository leadRepository,
            CursoRepository cursoRepository
    ) {
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public LeadCandidatoResponseDTO criar(LeadCandidatoRequestDTO dto) {
        Curso curso = buscarCursoPorId(dto.cursoInteresseId());

        LeadCandidato lead = new LeadCandidato(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.origem(),
                curso
        );

        LeadCandidato salvo = leadRepository.salvar(lead);
        return LeadCandidatoResponseDTO.fromDomain(salvo);
    }

    @Transactional(readOnly = true)
    public LeadCandidatoResponseDTO buscarPorId(Long id) {
        return LeadCandidatoResponseDTO.fromDomain(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<LeadCandidatoResponseDTO> listarTodos() {
        return leadRepository.listarTodos()
                .stream()
                .map(LeadCandidatoResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public LeadCandidatoResponseDTO atualizar(Long id, LeadCandidatoRequestDTO dto) {
        LeadCandidato lead = buscarEntidadePorId(id);
        Curso curso = buscarCursoPorId(dto.cursoInteresseId());

        lead.atualizarDados(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.origem(),
                curso
        );

        LeadCandidato salvo = leadRepository.salvar(lead);
        return LeadCandidatoResponseDTO.fromDomain(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        LeadCandidato lead = buscarEntidadePorId(id);
        leadRepository.remover(lead.getId());
    }

    private LeadCandidato buscarEntidadePorId(Long id) {
        return leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead não encontrado com o ID: " + id
                ));
    }

    private Curso buscarCursoPorId(Long cursoId) {
        if (cursoId == null) {
            throw new RegraDeNegocioException(
                    "O curso de interesse é obrigatório."
            );
        }

        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso de interesse não encontrado com o ID: " + cursoId
                ));
    }
}