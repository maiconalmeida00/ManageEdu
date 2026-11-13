package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.UsuarioRequestDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        Usuario autenticado = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.cadastrar(request, autenticado);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos(Authentication authentication) {
        Usuario autenticado = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.listarTodos(autenticado);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario autenticado = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.buscarPorId(id, autenticado);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO request,
            Authentication authentication
    ) {
        Usuario autenticado = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.atualizar(id, request, autenticado);
    }

    @PatchMapping("/{id}/ativo")
    public UsuarioResponseDTO alterarAtivo(
            @PathVariable Long id,
            @RequestParam boolean ativo,
            Authentication authentication
    ) {
        Usuario autenticado = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.alterarAtivo(id, ativo, autenticado);
    }
}

