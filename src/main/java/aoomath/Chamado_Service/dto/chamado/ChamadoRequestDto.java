package aoomath.Chamado_Service.dto.chamado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChamadoRequestDto {

    private String titulo;
    private String descricao;
}
