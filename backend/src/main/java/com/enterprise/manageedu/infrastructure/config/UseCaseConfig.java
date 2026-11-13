package com.enterprise.manageedu.infrastructure.config;

import com.enterprise.manageedu.application.usecase.AnexoUseCase;
import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.application.usecase.UsuarioUseCase;
import com.enterprise.manageedu.domain.repository.AnexoRepository;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import com.enterprise.manageedu.domain.repository.UsuarioRepository;
import com.enterprise.manageedu.infrastructure.storage.ArmazenamentoLocalArquivo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {

    @Bean
    public CursoUseCase cursoUseCase(
            CursoRepository cursoRepository,
            LeadRepository leadRepository,
            OportunidadeRepository oportunidadeRepository
    ) {
        return new CursoUseCase(cursoRepository, leadRepository, oportunidadeRepository);
    }

    @Bean
    public LeadCandidatoUseCase leadCandidatoUseCase(
            LeadRepository leadRepository,
            CursoRepository cursoRepository,
            OportunidadeRepository oportunidadeRepository
    ) {
        return new LeadCandidatoUseCase(leadRepository, cursoRepository, oportunidadeRepository);
    }

    @Bean
    public OportunidadeMatriculaUseCase oportunidadeMatriculaUseCase(
            OportunidadeRepository oportunidadeRepository,
            LeadRepository leadRepository,
            CursoRepository cursoRepository
    ) {
        return new OportunidadeMatriculaUseCase(oportunidadeRepository, leadRepository, cursoRepository);
    }

    @Bean
    public UsuarioUseCase usuarioUseCase(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new UsuarioUseCase(usuarioRepository, passwordEncoder);
    }

    @Bean
    public AnexoUseCase anexoUseCase(
            AnexoRepository anexoRepository,
            LeadRepository leadRepository,
            OportunidadeRepository oportunidadeRepository,
            ArmazenamentoLocalArquivo armazenamentoLocalArquivo
    ) {
        return new AnexoUseCase(
                anexoRepository,
                leadRepository,
                oportunidadeRepository,
                armazenamentoLocalArquivo
        );
    }
}
