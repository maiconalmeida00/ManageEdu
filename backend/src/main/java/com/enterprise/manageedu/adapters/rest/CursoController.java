package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.application.usecase.CursoUseCase;
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
@RequestMapping("/api/cursos")
public class CursoController {

    private static final Logger log = LoggerFactory.getLogger(CursoController.class);

    private final CursoUseCase cursoUseCase;

    public CursoController(CursoUseCase cursoUseCase) {
        this.cursoUseCase = cursoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponseDTO cadastrar(@RequestBody CursoRequestDTO request, Authentication authentication) {
        CursoResponseDTO salvo = cursoUseCase.cadastrar(request);
        log.info("Ação=CADASTRO_CURSO | Responsavel={} | CursoId={}", obterResponsavel(authentication), salvo.id());
        return salvo;
    }

    @GetMapping
    public List<CursoResponseDTO> listarTodos() {
        return cursoUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    public CursoResponseDTO buscarPorId(@PathVariable Long id) {
        return cursoUseCase.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CursoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody CursoRequestDTO request,
            Authentication authentication
    ) {
        CursoResponseDTO atualizado = cursoUseCase.atualizar(id, request);
        log.info("Ação=ATUALIZACAO_CURSO | Responsavel={} | CursoId={}", obterResponsavel(authentication), atualizado.id());
        return atualizado;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication authentication) {
        cursoUseCase.remover(id);
        log.info("Ação=REMOCAO_CURSO | Responsavel={} | CursoId={}", obterResponsavel(authentication), id);
    }

    private String obterResponsavel(Authentication authentication) {
        return authentication != null ? authentication.getName() : "desconhecido";
    }
}
