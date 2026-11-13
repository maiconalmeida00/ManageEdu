package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.application.dto.ReaberturaOportunidadeRequestDTO;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.application.usecase.UsuarioUseCase;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeController {

    private static final Logger log = LoggerFactory.getLogger(OportunidadeController.class);

    private final OportunidadeMatriculaUseCase oportunidadeUseCase;
    private final UsuarioUseCase usuarioUseCase;

    public OportunidadeController(
            OportunidadeMatriculaUseCase oportunidadeUseCase,
            UsuarioUseCase usuarioUseCase
    ) {
        this.oportunidadeUseCase = oportunidadeUseCase;
        this.usuarioUseCase = usuarioUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadeMatriculaResponseDTO criar(
            @RequestBody OportunidadeMatriculaRequestDTO request,
            Authentication authentication
    ) {
        OportunidadeMatriculaResponseDTO criada = oportunidadeUseCase.criar(request);
        log.info("Ação=CADASTRO_OPORTUNIDADE | Responsavel={} | OportunidadeId={}",
                obterEmailResponsavel(authentication), criada.id());
        return criada;
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
            @RequestBody OportunidadeMatriculaRequestDTO request,
            Authentication authentication
    ) {
        OportunidadeMatriculaResponseDTO atualizada = oportunidadeUseCase.atualizarDados(id, request);
        log.info("Ação=ATUALIZACAO_OPORTUNIDADE | Responsavel={} | OportunidadeId={}",
                obterEmailResponsavel(authentication), atualizada.id());
        return atualizada;
    }

    @PatchMapping("/{id}/status")
    public OportunidadeMatriculaResponseDTO alterarStatusPatch(
            @PathVariable Long id,
            @RequestBody AtualizarStatusOportunidadeDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(obterEmailResponsavel(authentication));
        OportunidadeMatriculaResponseDTO atualizada = oportunidadeUseCase.alterarStatus(id, request, usuario);
        log.info("Ação=MUDANCA_STATUS_OPORTUNIDADE | Responsavel={} | OportunidadeId={} | NovoStatus={}",
                usuario.getEmail(), atualizada.id(), atualizada.status());
        return atualizada;
    }

    @PutMapping("/{id}/status")
    public OportunidadeMatriculaResponseDTO alterarStatusPut(
            @PathVariable Long id,
            @RequestBody AtualizarStatusOportunidadeDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(obterEmailResponsavel(authentication));
        OportunidadeMatriculaResponseDTO atualizada = oportunidadeUseCase.alterarStatus(id, request, usuario);
        log.info("Ação=MUDANCA_STATUS_OPORTUNIDADE | Responsavel={} | OportunidadeId={} | NovoStatus={}",
                usuario.getEmail(), atualizada.id(), atualizada.status());
        return atualizada;
    }

    @PostMapping("/{id}/reabrir")
    public OportunidadeMatriculaResponseDTO reabrir(
            @PathVariable Long id,
            @RequestBody ReaberturaOportunidadeRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(obterEmailResponsavel(authentication));
        String justificativa = request != null ? request.justificativa() : null;
        OportunidadeMatriculaResponseDTO reaberta = oportunidadeUseCase.reabrirOportunidade(id, justificativa, usuario);
        log.info("Ação=REABERTURA_OPORTUNIDADE | Responsavel={} | OportunidadeId={}",
                usuario.getEmail(), reaberta.id());
        return reaberta;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication authentication) {
        oportunidadeUseCase.remover(id);
        log.info("Ação=REMOCAO_OPORTUNIDADE | Responsavel={} | OportunidadeId={}",
                obterEmailResponsavel(authentication), id);
    }

    private String obterEmailResponsavel(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}
