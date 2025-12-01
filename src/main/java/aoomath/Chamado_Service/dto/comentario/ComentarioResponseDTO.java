package aoomath.Chamado_Service.dto.comentario;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Representa os dados de retorno de um comentário cadastrado no sistema")
public class ComentarioResponseDTO {

    @Schema(description = "Id do comentário no formato UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;
    @Schema(description = "Conteúdo do comentário", example = "Teclado foi trocado em breve trago atualização sobre a compra do novo monitor")
    private String conteudo;
    @Schema(description = "Data e hora de criação do comentário", example = "27/03/2024 11:25")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime criadoEm;
    @Schema(description = "Id do técnico que comentou no chamado", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID tecnicoId;
    @Schema(description = "Nome do técnico comentou no chamado", example = "Paulo")
    private String tecnicoNome;

}
