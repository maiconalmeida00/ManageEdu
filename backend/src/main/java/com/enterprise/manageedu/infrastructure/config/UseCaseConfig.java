package com.enterprise.manageedu.infrastructure.config;

import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CursoUseCase cursoUseCase(CursoRepository cursoRepository) {
        return new CursoUseCase(cursoRepository);
    }

    @Bean
    public LeadCandidatoUseCase leadCandidatoUseCase(
            LeadRepository leadRepository,
            CursoRepository cursoRepository
    ) {
        return new LeadCandidatoUseCase(leadRepository, cursoRepository);
    }

    @Bean
    public OportunidadeMatriculaUseCase oportunidadeMatriculaUseCase(
            OportunidadeRepository oportunidadeRepository,
            LeadRepository leadRepository,
            CursoRepository cursoRepository
    ) {
        return new OportunidadeMatriculaUseCase(oportunidadeRepository, leadRepository, cursoRepository);
    }
}
