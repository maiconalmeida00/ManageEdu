package com.enterprise.manageedu;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Administrador;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.OperadorCaptacao;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.model.TurnoCurso;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryCursoRepository;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryLeadRepository;
import com.enterprise.manageedu.infrastructure.persistence.memory.InMemoryOportunidadeRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManageEduTdeTests {

    @Test
    void deveCriarCursoValido() {
        CursoUseCase useCase = new CursoUseCase(new InMemoryCursoRepository());

        var curso = useCase.cadastrar(new CursoRequestDTO("Java", "EAD", "NOTURNO"));

        assertNotNull(curso.id());
        assertEquals("Java", curso.nome());
        assertEquals(ModalidadeCurso.EAD, curso.modalidade());
        assertEquals(TurnoCurso.NOTURNO, curso.turno());
    }

    @Test
    void deveBloquearCursoSemNome() {
        CursoUseCase useCase = new CursoUseCase(new InMemoryCursoRepository());

        assertThrows(RegraDeNegocioException.class,
                () -> useCase.cadastrar(new CursoRequestDTO(" ", "EAD", "NOTURNO")));
    }

    @Test
    void deveCriarLeadValido() {
        InMemoryCursoRepository cursoRepository = new InMemoryCursoRepository();
        InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();
        Curso curso = cursoRepository.salvar(new Curso("Java", ModalidadeCurso.EAD, TurnoCurso.NOTURNO));
        LeadCandidatoUseCase useCase = new LeadCandidatoUseCase(leadRepository, cursoRepository);

        var lead = useCase.cadastrar(new LeadCandidatoRequestDTO(
                "Maria", "maria@email.com", "11999999999", "Site", curso.getId()));

        assertNotNull(lead.id());
        assertEquals(curso.getId(), lead.cursoInteresseId());
    }

    @Test
    void deveBloquearLeadComEmailinválido() {
        InMemoryCursoRepository cursoRepository = new InMemoryCursoRepository();
        Curso curso = cursoRepository.salvar(new Curso("Java", ModalidadeCurso.EAD, TurnoCurso.NOTURNO));
        LeadCandidatoUseCase useCase = new LeadCandidatoUseCase(new InMemoryLeadRepository(), cursoRepository);

        assertThrows(RegraDeNegocioException.class, () -> useCase.cadastrar(new LeadCandidatoRequestDTO(
                "Maria", "email-inválido", "11999999999", "Site", curso.getId())));
    }

    @Test
    void deveBloquearLeadComCursoInexistente() {
        LeadCandidatoUseCase useCase = new LeadCandidatoUseCase(
                new InMemoryLeadRepository(), new InMemoryCursoRepository());

        assertThrows(ResourceNotFoundException.class, () -> useCase.cadastrar(new LeadCandidatoRequestDTO(
                "Maria", "maria@email.com", "11999999999", "Site", 99L)));
    }

    @Test
    void deveCriarOportunidadeComStatusInicialNovoLead() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();

        var oportunidade = useCase.criar(new OportunidadeMatriculaRequestDTO(1L, 1L, "Primeiro contato"));

        assertEquals(StatusOportunidade.NOVO_LEAD, oportunidade.status());
        assertNotNull(oportunidade.dataCriacao());
    }

    @Test
    void devePermitirNovoLeadParaContato() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();

        var oportunidade = useCase.alterarStatus(
                1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.CONTATO),
                new OperadorCaptacao(2L, "Operador"));

        assertEquals(StatusOportunidade.CONTATO, oportunidade.status());
    }

    @Test
    void deveBloquearNovoLeadParaMatriculaConfirmada() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();

        assertThrows(RegraDeNegocioException.class, () -> useCase.alterarStatus(
                1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.MATRICULA_CONFIRMADA),
                new Administrador(1L, "Administrador")));
    }

    @Test
    void devePermitirContatoParaDocumentacao() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.CONTATO),
                new OperadorCaptacao(2L, "Operador"));

        var oportunidade = useCase.alterarStatus(1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.DOCUMENTACAO),
                new OperadorCaptacao(2L, "Operador"));

        assertEquals(StatusOportunidade.DOCUMENTACAO, oportunidade.status());
    }

    @Test
    void deveBloquearOperadorAoConfirmarMatricula() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();
        OperadorCaptacao operador = new OperadorCaptacao(2L, "Operador");
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.CONTATO), operador);
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.DOCUMENTACAO), operador);

        assertThrows(RegraDeNegocioException.class, () -> useCase.alterarStatus(1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.MATRICULA_CONFIRMADA), operador));
    }

    @Test
    void devePermitirAdministradorConfirmarMatricula() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();
        OperadorCaptacao operador = new OperadorCaptacao(2L, "Operador");
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.CONTATO), operador);
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.DOCUMENTACAO), operador);

        var oportunidade = useCase.alterarStatus(1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.MATRICULA_CONFIRMADA),
                new Administrador(1L, "Administrador"));

        assertEquals(StatusOportunidade.MATRICULA_CONFIRMADA, oportunidade.status());
    }

    @Test
    void deveBloquearAlteracaoDepoisDeEstadoFinal() {
        OportunidadeMatriculaUseCase useCase = novoUseCaseComDados();
        OperadorCaptacao operador = new OperadorCaptacao(2L, "Operador");
        useCase.alterarStatus(1L, new AtualizarStatusOportunidadeDTO(StatusOportunidade.DESISTENCIA), operador);

        assertThrows(RegraDeNegocioException.class, () -> useCase.alterarStatus(1L,
                new AtualizarStatusOportunidadeDTO(StatusOportunidade.CONTATO),
                new Administrador(1L, "Administrador")));
    }

    @Test
    void deveExecutarCrudDosTresRepositoriosEmMemoria() {
        InMemoryCursoRepository cursos = new InMemoryCursoRepository();
        Curso curso = cursos.salvar(new Curso("Java", ModalidadeCurso.EAD, TurnoCurso.NOTURNO));
        assertEquals(1L, curso.getId());
        assertEquals(1, cursos.listarTodos().size());
        cursos.remover(curso.getId());
        assertTrue(cursos.listarTodos().isEmpty());

        InMemoryLeadRepository leads = new InMemoryLeadRepository();
        LeadCandidato lead = leads.salvar(new LeadCandidato(
                "Maria", "maria@email.com", "11999999999", "Site", curso));
        assertEquals(1L, lead.getId());
        assertTrue(leads.buscarPorId(lead.getId()).isPresent());
        leads.remover(lead.getId());
        assertFalse(leads.buscarPorId(lead.getId()).isPresent());

        InMemoryOportunidadeRepository oportunidades = new InMemoryOportunidadeRepository();
        OportunidadeMatricula oportunidade = oportunidades.salvar(
                new OportunidadeMatricula(lead, curso, null));
        assertEquals(1L, oportunidade.getId());
        assertEquals(1, oportunidades.listarPorStatus(StatusOportunidade.NOVO_LEAD).size());
        oportunidades.remover(oportunidade.getId());
        assertTrue(oportunidades.listarTodos().isEmpty());
    }

    private OportunidadeMatriculaUseCase novoUseCaseComDados() {
        InMemoryCursoRepository cursoRepository = new InMemoryCursoRepository();
        InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();
        InMemoryOportunidadeRepository oportunidadeRepository = new InMemoryOportunidadeRepository();
        Curso curso = cursoRepository.salvar(new Curso("Java", ModalidadeCurso.EAD, TurnoCurso.NOTURNO));
        leadRepository.salvar(new LeadCandidato(
                "Maria", "maria@email.com", "11999999999", "Site", curso));
        OportunidadeMatriculaUseCase useCase = new OportunidadeMatriculaUseCase(
                oportunidadeRepository, leadRepository, cursoRepository);
        useCase.criar(new OportunidadeMatriculaRequestDTO(1L, 1L, null));
        return useCase;
    }
}
