package com.enterprise.manageedu.infrastructure.persistence;

import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Curso;
import com.enterprise.manageedu.domain.model.LeadCandidato;
import com.enterprise.manageedu.domain.model.OportunidadeMatricula;
import com.enterprise.manageedu.domain.model.ReaberturaOportunidade;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import com.enterprise.manageedu.infrastructure.persistence.entity.CursoJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.entity.LeadJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.entity.OportunidadeJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.entity.ReaberturaJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataCursoRepository;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataLeadRepository;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataOportunidadeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class OportunidadeRepositoryImpl implements OportunidadeRepository {

    private final SpringDataOportunidadeRepository springDataOportunidadeRepository;
    private final SpringDataLeadRepository springDataLeadRepository;
    private final SpringDataCursoRepository springDataCursoRepository;
    private final LeadRepositoryImpl leadRepositoryImpl;
    private final CursoRepositoryImpl cursoRepositoryImpl;

    public OportunidadeRepositoryImpl(
            SpringDataOportunidadeRepository springDataOportunidadeRepository,
            SpringDataLeadRepository springDataLeadRepository,
            SpringDataCursoRepository springDataCursoRepository,
            LeadRepositoryImpl leadRepositoryImpl,
            CursoRepositoryImpl cursoRepositoryImpl
    ) {
        this.springDataOportunidadeRepository = springDataOportunidadeRepository;
        this.springDataLeadRepository = springDataLeadRepository;
        this.springDataCursoRepository = springDataCursoRepository;
        this.leadRepositoryImpl = leadRepositoryImpl;
        this.cursoRepositoryImpl = cursoRepositoryImpl;
    }

    @Override
    @Transactional
    public OportunidadeMatricula salvar(OportunidadeMatricula oportunidade) {
        LeadJpaEntity leadEntity = springDataLeadRepository.findById(oportunidade.getLeadCandidato().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead da oportunidade não encontrado no banco."));

        CursoJpaEntity cursoEntity = springDataCursoRepository.findById(oportunidade.getCurso().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso da oportunidade não encontrado no banco."));

        boolean novaOportunidade = oportunidade.getId() == null;
        OportunidadeJpaEntity entity = novaOportunidade
                ? new OportunidadeJpaEntity()
                : springDataOportunidadeRepository.findById(oportunidade.getId())
                        .orElseGet(() -> {
                            OportunidadeJpaEntity fallback = new OportunidadeJpaEntity();
                            fallback.setId(oportunidade.getId());
                            fallback.setDataCriacao(oportunidade.getDataCriacao());
                            return fallback;
                        });

        entity.setLeadCandidato(leadEntity);
        entity.setCurso(cursoEntity);
        entity.setStatus(oportunidade.getStatus());
        if (novaOportunidade) {
            entity.setDataCriacao(oportunidade.getDataCriacao());
        }
        entity.setValorEstimado(oportunidade.getValorEstimado());
        entity.setDataPrevista(oportunidade.getDataPrevista());
        entity.setObservacao(oportunidade.getObservacao());

        if (!novaOportunidade) {
            sincronizarReaberturas(entity, oportunidade.getReaberturas());
        }

        OportunidadeJpaEntity salva = springDataOportunidadeRepository.save(entity);
        if (novaOportunidade) {
            oportunidade.definirId(salva.getId());
        }
        return oportunidade;
    }

    private void sincronizarReaberturas(
            OportunidadeJpaEntity entity,
            List<ReaberturaOportunidade> domainReaberturas
    ) {
        int persistidas = entity.getReaberturas().size();
        for (int i = persistidas; i < domainReaberturas.size(); i++) {
            ReaberturaOportunidade r = domainReaberturas.get(i);
            entity.adicionarReabertura(new ReaberturaJpaEntity(
                    entity,
                    r.dataHora(),
                    r.responsavelId(),
                    r.responsavelNome(),
                    r.justificativa(),
                    r.statusOrigem(),
                    r.statusDestino()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OportunidadeMatricula> buscarPorId(Long id) {
        return springDataOportunidadeRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadeMatricula> listarTodos() {
        return springDataOportunidadeRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OportunidadeMatricula> listarPorStatus(StatusOportunidade status) {
        return springDataOportunidadeRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        springDataOportunidadeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCursoId(Long cursoId) {
        return springDataOportunidadeRepository.existsByCursoId(cursoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorLeadId(Long leadId) {
        return springDataOportunidadeRepository.existsByLeadCandidatoId(leadId);
    }

    private OportunidadeMatricula toDomain(OportunidadeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        LeadCandidato lead = leadRepositoryImpl.toDomain(entity.getLeadCandidato());
        Curso curso = cursoRepositoryImpl.toDomain(entity.getCurso());
        List<ReaberturaOportunidade> reaberturas = entity.getReaberturas().stream()
                .map(r -> new ReaberturaOportunidade(
                        r.getDataHora(),
                        r.getResponsavelId(),
                        r.getResponsavelNome(),
                        r.getJustificativa(),
                        r.getStatusOrigem(),
                        r.getStatusDestino()
                ))
                .toList();

        return OportunidadeMatricula.restaurar(
                entity.getId(),
                lead,
                curso,
                entity.getStatus(),
                entity.getDataCriacao(),
                entity.getValorEstimado(),
                entity.getDataPrevista(),
                entity.getObservacao(),
                reaberturas
        );
    }
}

