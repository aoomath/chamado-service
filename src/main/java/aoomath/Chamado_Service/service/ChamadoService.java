package aoomath.Chamado_Service.service;


import aoomath.Chamado_Service.dto.chamado.ChamadoMapper;
import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.exception.AcessoNegadoException;
import aoomath.Chamado_Service.exception.RecursoNaoEncontradoException;
import aoomath.Chamado_Service.exception.RequisicaoInvalidaException;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import aoomath.Chamado_Service.rabbit.dto.NotificacaoMessage;
import aoomath.Chamado_Service.rabbit.service.NotificacaoProducer;
import aoomath.Chamado_Service.repository.ChamadoRepository;
import aoomath.Chamado_Service.specification.ChamadoSpecification;
import aoomath.Chamado_Service.validator.ChamadoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository repository;
    private final ChamadoMapper mapper;
    private final NotificacaoProducer notificacaoProducer;
    private final ChamadoValidator validator;

    public ChamadoResponseDto criar (ChamadoRequestDto request, String id, String nome){
        Chamado novo = mapper.toEntity(request);
        novo.setStatus(Status.ABERTO);
        novo.setCriadorId(UUID.fromString(id));
        novo.setCriadorNome(nome);

        return mapper.toResponse(repository.save(novo));
    }


    public ChamadoResponseDto buscarPorId (UUID id,  String usuarioId, boolean isUser){

        Chamado chamado = repository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        validator.validarAcessoAoChamado(chamado, UUID.fromString(usuarioId), isUser);

        return mapper.toResponse(chamado);
    }

    public Page<ChamadoResponseDto> listarChamados (int pagina, int tamanho, String status){
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());
        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(null, null, status);
        return mapper.toPageResponse(repository.findAll(spec, pageable));
    }

    public Page<ChamadoResponseDto> listarChamadosDoUsuario (int pagina, int tamanho, String id, String status){
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(UUID.fromString(id), null, status);
        return mapper.toPageResponse(repository.findAll(spec,pageable));
    }

    public Page<ChamadoResponseDto> listarChamadosDoTecnico (int pagina, int tamanho, String id, String status){
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(null, UUID.fromString(id), status);
        return mapper.toPageResponse(repository.findAll(spec,pageable));
    }

    public Page<ChamadoResponseDto> listarChamadosPeloCriadorId (int pagina, int tamanho, UUID id, String status){
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(id, null, status);
        return mapper.toPageResponse(repository.findAll(spec,pageable));
    }

    public Page<ChamadoResponseDto> listarChamadosPeloTecnicoId (int pagina, int tamanho, UUID id, String status){
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(null, id, status);
        return mapper.toPageResponse(repository.findAll(spec,pageable));
    }

    public ChamadoResponseDto assumirChamado (UUID chamadoId, String id, String nome){
        Chamado chamado = repository.findById(chamadoId)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        chamado.setTecnicoId(UUID.fromString(id));
        chamado.setTecnicoNome(nome);
        chamado.setStatus(Status.EM_TRATATIVA);
        repository.save(chamado);

        notificacaoProducer.enviarNotificacao(
                new NotificacaoMessage(chamado.getCriadorId(), chamado.getTitulo(), nome,"TRATATIVA")
        );

        return mapper.toResponse(chamado);
    }

    public void concluirChamado (UUID chamadoId, String tecnicoId){
        Chamado chamado = repository.findById(chamadoId)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        validator.validarAcessoDoTecnico(chamado, UUID.fromString(tecnicoId));

        chamado.setStatus(Status.CONCLUIDO);
        repository.save(chamado);

        notificacaoProducer.enviarNotificacao(
                new NotificacaoMessage(chamado.getCriadorId(), chamado.getTitulo(), chamado.getTecnicoNome(),"CONCLUIDO")
        );

    }


    public void deletar(UUID id, String criadorId){
        Chamado chamado = repository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        validator.validarDelete(chamado, UUID.fromString(criadorId));

        repository.delete(chamado);
    }
}
