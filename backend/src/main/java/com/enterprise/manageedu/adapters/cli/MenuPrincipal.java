package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.StatusOportunidade;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner sc;
    private final CursoMenu cursoMenu;
    private final LeadMenu leadMenu;
    private final OportunidadeMenu oportunidadeMenu;
    private final OportunidadeMatriculaUseCase oportunidadeUseCase;

    public MenuPrincipal(
            Scanner sc,
            CursoUseCase cursoUseCase,
            LeadCandidatoUseCase leadUseCase,
            OportunidadeMatriculaUseCase oportunidadeUseCase
    ) {
        this.sc = sc;
        this.oportunidadeUseCase = oportunidadeUseCase;
        this.cursoMenu = new CursoMenu(sc, cursoUseCase);
        this.leadMenu = new LeadMenu(sc, leadUseCase);
        this.oportunidadeMenu = new OportunidadeMenu(sc, oportunidadeUseCase);
    }

    public void iniciar() {
        boolean executar = true;
        while (executar) {
            exibirMenu();
            String opcao = sc.nextLine().trim();
            switch (opcao) {
                case "1" -> cursoMenu.exibir();
                case "2" -> leadMenu.exibir();
                case "3" -> oportunidadeMenu.exibir();
                case "4" -> exibirFunil();
                case "0" -> {
                    System.out.println("Encerrando o ManageEdu. Ate logo!");
                    executar = false;
                }
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
        }
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== ManageEdu - CRM Educacional =====");
        System.out.println("1. Gerenciar cursos");
        System.out.println("2. Gerenciar leads/candidatos");
        System.out.println("3. Gerenciar oportunidades de matricula");
        System.out.println("4. Exibir funil de matricula por status");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opcao: ");
    }

    private void exibirFunil() {
        try {
            Map<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> funil =
                    oportunidadeUseCase.listarFunilPorStatus();

            System.out.println();
            System.out.println("--- Funil de matricula por status ---");
            for (Map.Entry<StatusOportunidade, List<OportunidadeMatriculaResponseDTO>> entrada : funil.entrySet()) {
                List<OportunidadeMatriculaResponseDTO> oportunidades = entrada.getValue();
                System.out.println();
                System.out.println(entrada.getKey() + " (" + oportunidades.size() + ")");
                if (oportunidades.isEmpty()) {
                    System.out.println("  Nenhuma oportunidade neste status.");
                    continue;
                }
                for (OportunidadeMatriculaResponseDTO oportunidade : oportunidades) {
                    oportunidadeMenu.imprimirOportunidade(oportunidade);
                }
            }
        } catch (ResourceNotFoundException | RegraDeNegocioException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
