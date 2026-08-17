package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadCandidatoUseCase {

    private final LeadRepository leadRepository;
    private final CursoRepository cursoRepository;

    public LeadCandidatoUseCase(LeadRepository leadRepository, CursoRepository cursoRepository) {
        this.leadRepository = leadRepository;
        this.cursoRepository = cursoRepository;
    }

    public LeadCandidatoResponseDTO criar(LeadCandidatoRequestDTO dto) {
        validarCursoExistente(dto.cursoInteresseId());

        LeadCandidato lead = new LeadCandidato(
                null,
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.origem(),
                dto.cursoInteresseId()
        );
        LeadCandidato salvo = leadRepository.salvar(lead);
        return LeadCandidatoResponseDTO.fromDomain(salvo);
    }

    public LeadCandidatoResponseDTO buscarPorId(Long id) {
        LeadCandidato lead = leadRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado com o ID: " + id));
        return LeadCandidatoResponseDTO.fromDomain(lead);
    }

    public List<LeadCandidatoResponseDTO> listarTodos() {
        return leadRepository.listarTodos()
                .stream()
                .map(LeadCandidatoResponseDTO::fromDomain)
                .toList();
    }

    public LeadCandidatoResponseDTO atualizar(Long id, LeadCandidatoRequestDTO dto) {
        buscarPorId(id);
        validarCursoExistente(dto.cursoInteresseId());

        LeadCandidato leadAtualizado = new LeadCandidato(
                id,
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.origem(),
                dto.cursoInteresseId()
        );
        LeadCandidato salvo = leadRepository.salvar(leadAtualizado);
        return LeadCandidatoResponseDTO.fromDomain(salvo);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        leadRepository.remover(id);
    }

    private void validarCursoExistente(Long cursoId) {
        if (cursoId != null && cursoRepository.buscarPorId(cursoId).isEmpty()) {
            throw new ResourceNotFoundException("Curso de interesse não encontrado com o ID: " + cursoId);
        }
    }
}
