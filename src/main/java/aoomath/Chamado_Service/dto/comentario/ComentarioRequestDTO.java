package aoomath.Chamado_Service.dto.comentario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Objeto de requisição para criação de um comentário")
public class ComentarioRequestDTO {

    @Schema(description = "Conteúdo do comentário", example = "Teclado foi trocado em breve trago atualização sobre a compra do novo monitor")
    @NotBlank(message = "O comentário da postagem é obrigatório")
    @Size(max = 255, message = "O conteúdo deve ter no máximo 255 caracteres")
    private String conteudo;
}
