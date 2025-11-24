package aoomath.Chamado_Service.dto.comentario;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ComentarioResponseDTO {

    private UUID id;
    private String conteudo;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime criacao;
    private UUID tecnicoId;
    private String tecnicoNome;

}
