package com.enterprise.manageedu.infrastructure.config;

import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.repository.CursoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import com.enterprise.manageedu.domain.model.Administrador;
import com.enterprise.manageedu.domain.model.OperadorCaptacao;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryCursoRepository;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryLeadRepository;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryOportunidadeRepository;

public class UseCaseConfig {

    private final CursoRepository cursoRepository = new InMemoryCursoRepository();
    private final LeadRepository leadRepository = new InMemoryLeadRepository();
    private final OportunidadeRepository oportunidadeRepository = new InMemoryOportunidadeRepository();

    public CursoUseCase cursoUseCase() {
        return new CursoUseCase(cursoRepository);
    }

    public LeadCandidatoUseCase leadCandidatoUseCase() {
        return new LeadCandidatoUseCase(leadRepository, cursoRepository);
    }

    public OportunidadeMatriculaUseCase oportunidadeMatriculaUseCase() {
        return new OportunidadeMatriculaUseCase(oportunidadeRepository, leadRepository, cursoRepository);
    }

    public Administrador administradorPadrao() {
        return new Administrador(1L, "Ana Administradora");
    }

    public OperadorCaptacao operadorPadrao() {
        return new OperadorCaptacao(2L, "Carlos Captacao");
    }
}
