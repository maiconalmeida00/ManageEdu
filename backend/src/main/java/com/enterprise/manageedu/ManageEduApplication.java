/*
 * DECLARAÇÃO DE AUTORIA
 *
 * Autores:
 * 1. Maicon da Silva Almeida
 * 2. Pedro Lucas Nascimento Pimenta 
 * 3. Jaime Luiz Khoury Nicoletti
 * 4. Pedro Paulo Dantas Torres
 *
 * Declaramos que o código-fonte deste projeto foi desenvolvido
 * pelos autores identificados acima. Ferramentas de Inteligência
 * Artificial foram utilizadas exclusivamente para consultas sobre o conteúdo abordado e 
 * esclarecimento de dúvidas.
 *
 * Declaramos, ainda, que a Inteligência Artificial não foi utilizada
 * para gerar integral ou parcialmente o código-fonte entregue neste
 * projeto.
 */

package com.enterprise.manageedu;

import com.enterprise.manageedu.adapters.cli.CliRunner;
import com.enterprise.manageedu.infrastructure.config.UseCaseConfig;

public class ManageEduApplication {

    public static void main(String[] args) {
        UseCaseConfig config = new UseCaseConfig();
        CliRunner cliRunner = new CliRunner(
                config.cursoUseCase(),
                config.leadCandidatoUseCase(),
                config.oportunidadeMatriculaUseCase(),
                config.administradorPadrao(),
                config.operadorPadrao()
        );
        cliRunner.run(args);
    }
}
