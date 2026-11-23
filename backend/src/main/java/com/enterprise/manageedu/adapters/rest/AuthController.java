package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.LoginRequestDTO;
import com.enterprise.manageedu.application.dto.UsuarioResponseDTO;
import com.enterprise.manageedu.application.usecase.UsuarioUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UsuarioUseCase usuarioUseCase;
    private final HttpSessionSecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, UsuarioUseCase usuarioUseCase) {
        this.authenticationManager = authenticationManager;
        this.usuarioUseCase = usuarioUseCase;
    }

    @PostMapping("/login")
    public UsuarioResponseDTO login(
            @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        if (request == null || request.email() == null || request.email().isBlank()
                || request.senha() == null || request.senha().isBlank()) {
            throw new RegraDeNegocioException("E-mail e senha são obrigatórios.");
        }

        String emailNormalizado = request.email().trim().toLowerCase();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNormalizado, request.senha())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Persiste a sessão autenticada no cookie JSESSIONID.
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
            log.info("Ação=LOGIN | Responsavel={} | Perfil={}", usuario.getEmail(), usuario.getPerfil());
            return usuarioUseCase.toResponse(usuario);
        } catch (AuthenticationException ex) {
            log.warn("Ação=LOGIN_FALHA | Email={}", emailNormalizado);
            throw ex;
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "desconhecido";
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        log.info("Ação=LOGOUT | Responsavel={}", email);
    }

    @GetMapping("/me")
    public UsuarioResponseDTO usuarioAtual(Authentication authentication) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return usuarioUseCase.toResponse(usuario);
    }

    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void csrf() {
        // Endpoint auxiliar para o frontend receber o cookie XSRF-TOKEN.
    }
}

