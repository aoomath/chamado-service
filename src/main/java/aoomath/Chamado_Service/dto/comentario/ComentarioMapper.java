package aoomath.Chamado_Service.dto.comentario;

import aoomath.Chamado_Service.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {

    public ComentarioResponseDTO toResponse (Comentario comentario){
        return new ComentarioResponseDTO(comentario.getId(),
                comentario.getConteudo(),
                comentario.getCriadoEm(),
                comentario.getTecnicoId(),
                comentario.getTecnicoNome());
    }

    public Comentario toEntity(ComentarioRequestDTO request){
        Comentario comentario = new Comentario();
        comentario.setConteudo(request.getConteudo());

        return comentario;
    }

    public Page<ComentarioResponseDTO> toPageResponse(Page<Comentario> comentarios){
        return comentarios.map(this::toResponse);
    }
}
