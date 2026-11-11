package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

    private final LeadCandidatoUseCase leadCandidatoUseCase;

    public LeadController(LeadCandidatoUseCase leadCandidatoUseCase) {
        this.leadCandidatoUseCase = leadCandidatoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadCandidatoResponseDTO cadastrar(@RequestBody LeadCandidatoRequestDTO request) {
        return leadCandidatoUseCase.cadastrar(request);
    }

    @GetMapping
    public List<LeadCandidatoResponseDTO> listarTodos() {
        return leadCandidatoUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    public LeadCandidatoResponseDTO buscarPorId(@PathVariable Long id) {
        return leadCandidatoUseCase.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LeadCandidatoResponseDTO atualizar(@PathVariable Long id, @RequestBody LeadCandidatoRequestDTO request) {
        return leadCandidatoUseCase.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        leadCandidatoUseCase.remover(id);
    }
}

