package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.AlterarAtivoUsuarioDTO;
import com.enterprise.manageedu.application.dto.UsuarioRequestDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.application.dto.UsuarioResponseDTO;
import com.enterprise.manageedu.application.usecase.UsuarioUseCase;
import com.enterprise.manageedu.domain.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public UsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(
            @RequestBody UsuarioRequestDTO request,
            Authentication authentication
    ) {
        return usuarioUseCase.cadastrar(request, obterUsuarioAutenticado(authentication));
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos(Authentication authentication) {
        return usuarioUseCase.listarTodos(obterUsuarioAutenticado(authentication));
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id, Authentication authentication) {
        return usuarioUseCase.buscarPorId(id, obterUsuarioAutenticado(authentication));
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO request,
            Authentication authentication
    ) {
        return usuarioUseCase.atualizar(id, request, obterUsuarioAutenticado(authentication));
    }

    @PatchMapping("/{id}")
    public UsuarioResponseDTO alterarAtivo(
            @PathVariable Long id,
            @RequestBody AlterarAtivoUsuarioDTO request,
            Authentication authentication
    ) {
        if (request == null || request.ativo() == null) {
            throw new RegraDeNegocioException("O campo ativo é obrigatório.");
        }
        return usuarioUseCase.alterarAtivo(id, request.ativo(), obterUsuarioAutenticado(authentication));
    }

    private Usuario obterUsuarioAutenticado(Authentication authentication) {
        return usuarioUseCase.buscarDominioPorEmail(authentication.getName());
    }
}

