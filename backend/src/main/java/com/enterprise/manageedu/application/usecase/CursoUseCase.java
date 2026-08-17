package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoUseCase {

    private final CursoRepository cursoRepository;

    public CursoUseCase(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public CursoResponseDTO criar(CursoRequestDTO dto) {
        Curso curso = new Curso(null, dto.nome(), dto.modalidade(), dto.turno());
        Curso salvo = cursoRepository.salvar(curso);
        return CursoResponseDTO.fromDomain(salvo);
    }

    public CursoResponseDTO buscarPorId(Long id) {
        Curso curso = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + id));
        return CursoResponseDTO.fromDomain(curso);
    }

    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.listarTodos()
                .stream()
                .map(CursoResponseDTO::fromDomain)
                .toList();
    }

    public CursoResponseDTO atualizar(Long id, CursoRequestDTO dto) {
        buscarPorId(id);
        Curso cursoAtualizado = new Curso(id, dto.nome(), dto.modalidade(), dto.turno());
        Curso salvo = cursoRepository.salvar(cursoAtualizado);
        return CursoResponseDTO.fromDomain(salvo);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        cursoRepository.remover(id);
    }
}
