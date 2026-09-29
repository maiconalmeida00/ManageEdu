package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.model.Administrador;
import com.enterprise.manageedu.domain.model.OperadorCaptacao;

import java.util.Scanner;

public class CliRunner {

    private final CursoUseCase cursoUseCase;
    private final LeadCandidatoUseCase leadUseCase;
    private final OportunidadeMatriculaUseCase oportunidadeUseCase;
    private final Administrador administradorPadrao;
    private final OperadorCaptacao operadorPadrao;

    public CliRunner(
            CursoUseCase cursoUseCase,
            LeadCandidatoUseCase leadUseCase,
            OportunidadeMatriculaUseCase oportunidadeUseCase,
            Administrador administradorPadrao,
            OperadorCaptacao operadorPadrao
    ) {
        this.cursoUseCase = cursoUseCase;
        this.leadUseCase = leadUseCase;
        this.oportunidadeUseCase = oportunidadeUseCase;
        this.administradorPadrao = administradorPadrao;
        this.operadorPadrao = operadorPadrao;
    }

    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        MenuPrincipal menuPrincipal = new MenuPrincipal(
                scanner,
                cursoUseCase,
                leadUseCase,
                oportunidadeUseCase,
                administradorPadrao,
                operadorPadrao
        );
        menuPrincipal.iniciar();
    }
}
