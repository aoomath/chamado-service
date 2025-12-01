package aoomath.Chamado_Service.dto.chamado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Objeto de requisição para criação de um chamado")
public class ChamadoRequestDto {

    @Schema(description = "Título do chamado", example = "Reparo técnico")
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    @Schema(description = "Descrição do chamado", example = "Teclado parou de funcionar e monitor não liga")
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
}
