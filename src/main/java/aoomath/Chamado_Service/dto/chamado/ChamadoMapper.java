package aoomath.Chamado_Service.dto.chamado;

import aoomath.Chamado_Service.model.Chamado;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ChamadoMapper {

    public ChamadoResponseDto toResponse (Chamado chamado){
       return new ChamadoResponseDto(chamado.getId(), chamado.getTitulo(),
                chamado.getDescricao(), chamado.getStatus(),chamado.getCriadorId(),
                chamado.getCriadorNome(), chamado.getTecnicoId(), chamado.getTecnicoNome(),
                chamado.getCriadoEm(),chamado.getAtualizadoEm());
    }

    public Chamado toEntity(ChamadoRequestDto request){
        Chamado chamado = new Chamado();
        chamado.setTitulo(request.getTitulo());
        chamado.setDescricao(request.getDescricao());

        return chamado;
    }

    public Page<ChamadoResponseDto> toPageResponse (Page<Chamado> chamados){
        return chamados.map(this::toResponse);
    }
}
