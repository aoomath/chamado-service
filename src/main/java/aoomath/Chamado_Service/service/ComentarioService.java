package aoomath.Chamado_Service.service;



import aoomath.Chamado_Service.dto.comentario.ComentarioMapper;
import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.exception.RecursoNaoEncontradoException;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Comentario;
import aoomath.Chamado_Service.rabbit.dto.NotificacaoMessage;
import aoomath.Chamado_Service.rabbit.service.NotificacaoProducer;
import aoomath.Chamado_Service.repository.ChamadoRepository;
import aoomath.Chamado_Service.repository.ComentarioRepository;
import aoomath.Chamado_Service.validator.ChamadoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ChamadoRepository chamadoRepository;
    private final ComentarioMapper mapper;
    private final NotificacaoProducer notificacaoProducer;
    private final ChamadoValidator validator;




    public ComentarioResponseDTO criar(UUID chamadoId, ComentarioRequestDTO dto, String nome, String id){

        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        validator.validarAcessoDoTecnico(chamado, UUID.fromString(id));


        Comentario comentario = mapper.toEntity(dto);
        comentario.setChamado(chamado);
        comentario.setTecnicoId(UUID.fromString(id));
        comentario.setTecnicoNome(nome);

        Comentario salvo = comentarioRepository.save(comentario);

        notificacaoProducer.enviarNotificacao(
                new NotificacaoMessage(chamado.getCriadorId(), chamado.getTitulo(), chamado.getTecnicoNome(),"COMENTARIO")
        );


        return mapper.toResponse(salvo);

    }

    public ComentarioResponseDTO buscarPorId(UUID id){
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Comentário não encontrado"));
        return mapper.toResponse(comentario);
    }


    public Page<ComentarioResponseDTO> listarPorChamado(UUID chamadoId, int pagina, int tamanho){
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Chamado não encontrado"));

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("criadoEm").descending());
        return comentarioRepository.findByChamadoId(chamado.getId(),pageable);
    }


    public Page<ComentarioResponseDTO> listarMeusComentarios(String tecnicoId, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina,tamanho, Sort.by("criadoEm").descending());
        return comentarioRepository.findByTecnicoId(UUID.fromString(tecnicoId),pageable);
    }

    public Page<ComentarioResponseDTO> listarPeloTecnicoId(UUID tecnicoId, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina,tamanho, Sort.by("criadoEm").descending());
        return comentarioRepository.findByTecnicoId(tecnicoId,pageable);
    }



    public void deletar(UUID comentarioId, String tecnicoId){
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Comentário não encontrado"));

        validator.validarAcessoDoTecnico(comentario.getChamado(), UUID.fromString(tecnicoId));


        comentarioRepository.delete(comentario);
    }

}
