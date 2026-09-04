package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@ConditionalOnProperty(name = "manageedu.cli.enabled", havingValue = "true", matchIfMissing = true)
public class CliRunner implements CommandLineRunner {

    private final CursoUseCase cursoUseCase;
    private final LeadCandidatoUseCase leadUseCase;
    private final OportunidadeMatriculaUseCase oportunidadeUseCase;

    public CliRunner(
            CursoUseCase cursoUseCase,
            LeadCandidatoUseCase leadUseCase,
            OportunidadeMatriculaUseCase oportunidadeUseCase
    ) {
        this.cursoUseCase = cursoUseCase;
        this.leadUseCase = leadUseCase;
        this.oportunidadeUseCase = oportunidadeUseCase;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        MenuPrincipal menuPrincipal = new MenuPrincipal(
                scanner,
                cursoUseCase,
                leadUseCase,
                oportunidadeUseCase
        );
        menuPrincipal.iniciar();
    }
}
