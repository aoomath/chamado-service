package aoomath.Chamado_Service.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ComentarioRequestDTO {


    @NotBlank(message = "O comentário da postagem é obrigatório")
    @Size(max = 255, message = "O conteúdo deve ter no máximo 255 caracteres")
    private String conteudo;
}
