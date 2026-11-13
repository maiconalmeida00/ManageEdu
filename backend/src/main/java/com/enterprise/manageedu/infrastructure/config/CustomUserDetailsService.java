package com.enterprise.manageedu.infrastructure.config;

import com.enterprise.manageedu.domain.model.Usuario;
import com.enterprise.manageedu.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPerfil().name())
                .disabled(!usuario.isAtivo())
                .build();
    }
}

