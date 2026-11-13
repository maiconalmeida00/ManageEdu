package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
public class LeadController {

    private static final Logger log = LoggerFactory.getLogger(LeadController.class);

    private final LeadCandidatoUseCase leadCandidatoUseCase;

    public LeadController(LeadCandidatoUseCase leadCandidatoUseCase) {
        this.leadCandidatoUseCase = leadCandidatoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadCandidatoResponseDTO cadastrar(
            @RequestBody LeadCandidatoRequestDTO request,
            Authentication authentication
    ) {
        LeadCandidatoResponseDTO salvo = leadCandidatoUseCase.cadastrar(request);
        log.info("Ação=CADASTRO_LEAD | Responsavel={} | LeadId={}", obterResponsavel(authentication), salvo.id());
        return salvo;
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
    public LeadCandidatoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody LeadCandidatoRequestDTO request,
            Authentication authentication
    ) {
        LeadCandidatoResponseDTO atualizado = leadCandidatoUseCase.atualizar(id, request);
        log.info("Ação=ATUALIZACAO_LEAD | Responsavel={} | LeadId={}", obterResponsavel(authentication), atualizado.id());
        return atualizado;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication authentication) {
        leadCandidatoUseCase.remover(id);
        log.info("Ação=REMOCAO_LEAD | Responsavel={} | LeadId={}", obterResponsavel(authentication), id);
    }

    private String obterResponsavel(Authentication authentication) {
        return authentication != null ? authentication.getName() : "desconhecido";
    }
}
