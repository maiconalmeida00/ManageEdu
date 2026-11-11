package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.application.dto.ReaberturaOportunidadeRequestDTO;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.model.Administrador;
import com.enterprise.manageedu.domain.model.OperadorCaptacao;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oportunidades")
@CrossOrigin(origins = "*")
public class OportunidadeController {

    private final OportunidadeMatriculaUseCase oportunidadeUseCase;
    private final Administrador administradorPadrao;
    private final OperadorCaptacao operadorPadrao;

    public OportunidadeController(
            OportunidadeMatriculaUseCase oportunidadeUseCase,
            Administrador administradorPadrao,
            OperadorCaptacao operadorPadrao
    ) {
        this.oportunidadeUseCase = oportunidadeUseCase;
        this.administradorPadrao = administradorPadrao;
        this.operadorPadrao = operadorPadrao;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadeMatriculaResponseDTO criar(@RequestBody OportunidadeMatriculaRequestDTO request) {
        return oportunidadeUseCase.criar(request);
    }

    @GetMapping
    public List<OportunidadeMatriculaResponseDTO> listar(
            @RequestParam(required = false) StatusOportunidade status
    ) {
        if (status != null) {
            return oportunidadeUseCase.listarPorStatus(status);
        }
        return oportunidadeUseCase.listarTodos();
    }

    @GetMapping("/funil")
    public Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> funil() {
        return oportunidadeUseCase.listarFunilPorStatus();
    }

    @GetMapping("/{id}")
    public OportunidadeMatriculaResponseDTO buscarPorId(@PathVariable Long id) {
        return oportunidadeUseCase.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public OportunidadeMatriculaResponseDTO atualizarDados(
            @PathVariable Long id,
            @RequestBody OportunidadeMatriculaRequestDTO request
    ) {
        return oportunidadeUseCase.atualizarDados(id, request);
    }

    @PatchMapping("/{id}/status")
    public OportunidadeMatriculaResponseDTO alterarStatusPatch(
            @PathVariable Long id,
            @RequestBody AtualizarStatusOportunidadeDTO request,
            @RequestHeader(value = "X-User-Role", defaultValue = "ADMIN") String userRole
    ) {
        Usuario usuario = obterUsuarioPorRole(userRole);
        return oportunidadeUseCase.alterarStatus(id, request, usuario);
    }

    @PutMapping("/{id}/status")
    public OportunidadeMatriculaResponseDTO alterarStatusPut(
            @PathVariable Long id,
            @RequestBody AtualizarStatusOportunidadeDTO request,
            @RequestHeader(value = "X-User-Role", defaultValue = "ADMIN") String userRole
    ) {
        Usuario usuario = obterUsuarioPorRole(userRole);
        return oportunidadeUseCase.alterarStatus(id, request, usuario);
    }

    @PostMapping("/{id}/reabrir")
    public OportunidadeMatriculaResponseDTO reabrir(
            @PathVariable Long id,
            @RequestBody ReaberturaOportunidadeRequestDTO request,
            @RequestHeader(value = "X-User-Role", defaultValue = "ADMIN") String userRole
    ) {
        Usuario usuario = obterUsuarioPorRole(userRole);
        String justificativa = request != null ? request.justificativa() : null;
        return oportunidadeUseCase.reabrirOportunidade(id, justificativa, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        oportunidadeUseCase.remover(id);
    }

    private Usuario obterUsuarioPorRole(String role) {
        if (role != null && role.trim().equalsIgnoreCase("OPERADOR")) {
            return operadorPadrao;
        }
        return administradorPadrao;
    }
}

