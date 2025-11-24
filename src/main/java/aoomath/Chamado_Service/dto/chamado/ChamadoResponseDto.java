package aoomath.Chamado_Service.dto.chamado;

import aoomath.Chamado_Service.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChamadoResponseDto {

    private UUID id;
    private String titulo;
    private String descricao;
    private Status status;
    private UUID criadorId;
    private String criadorNome;
    private UUID tecnicoId;
    private String tecnicoNome;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime criadoEm;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime atualizadoEm;

}
