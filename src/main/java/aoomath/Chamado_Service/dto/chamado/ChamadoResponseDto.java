package aoomath.Chamado_Service.dto.chamado;

import aoomath.Chamado_Service.model.Status;
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
@Schema(description = "Representa os dados de retorno de um chamado cadastrado no sistema")
public class ChamadoResponseDto {

    @Schema(description = "Id do usuário no formato UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;
    @Schema(description = "Título do chamado", example = "Reparo técnico")
    private String titulo;
    @Schema(description = "Descrição do chamado", example = "Teclado parou de funcionar e monitor não liga")
    private String descricao;
    @Schema(description = "Status do chamado (Pode ser um dos três: ABERTO, EM_TRATATIVA, CONCLUIDO)", example = "ABERTO")
    private Status status;
    @Schema(description = "Id do criador do chamado no formato UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID criadorId;
    @Schema(description = "Nome do criador do chamado", example = "Victor")
    private String criadorNome;
    @Schema(description = "Id do técnico que assumiu o chamado no formato UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID tecnicoId;
    @Schema(description = "Nome do técnico assumiu o chamado", example = "Paulo")
    private String tecnicoNome;
    @Schema(description = "Data e hora de criação do chamado", example = "23/03/2024 13:30")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime criadoEm;
    @Schema(description = "Data e hora da ultima atualização do chamado", example = "25/03/2024 12:15")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime atualizadoEm;

}
