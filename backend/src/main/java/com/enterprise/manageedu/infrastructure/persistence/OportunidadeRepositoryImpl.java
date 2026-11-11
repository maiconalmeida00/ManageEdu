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

        OportunidadeJpaEntity entity;
        if (oportunidade.getId() == null) {
            entity = new OportunidadeJpaEntity(
                    null,
                    leadEntity,
                    cursoEntity,
                    oportunidade.getStatus(),
                    oportunidade.getDataCriacao(),
                    oportunidade.getValorEstimado(),
                    oportunidade.getDataPrevista(),
                    oportunidade.getObservacao()
            );
            entity = springDataOportunidadeRepository.save(entity);
            oportunidade.definirId(entity.getId());
        } else {
            entity = springDataOportunidadeRepository.findById(oportunidade.getId())
                    .orElseGet(() -> new OportunidadeJpaEntity(
                            oportunidade.getId(),
                            leadEntity,
                            cursoEntity,
                            oportunidade.getStatus(),
                            oportunidade.getDataCriacao(),
                            oportunidade.getValorEstimado(),
                            oportunidade.getDataPrevista(),
                            oportunidade.getObservacao()
                    ));
            entity.setLeadCandidato(leadEntity);
            entity.setCurso(cursoEntity);
            entity.setStatus(oportunidade.getStatus());
            entity.setValorEstimado(oportunidade.getValorEstimado());
            entity.setDataPrevista(oportunidade.getDataPrevista());
            entity.setObservacao(oportunidade.getObservacao());

            int persistidas = entity.getReaberturas().size();
            List<ReaberturaOportunidade> domainReaberturas = oportunidade.getReaberturas();
            for (int i = persistidas; i < domainReaberturas.size(); i++) {
                ReaberturaOportunidade r = domainReaberturas.get(i);
                ReaberturaJpaEntity reabEntity = new ReaberturaJpaEntity(
                        entity,
                        r.dataHora(),
                        r.responsavelId(),
                        r.responsavelNome(),
                        r.justificativa(),
                        r.statusOrigem(),
                        r.statusDestino()
                );
                entity.adicionarReabertura(reabEntity);
            }
            springDataOportunidadeRepository.save(entity);
        }
        return oportunidade;
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

