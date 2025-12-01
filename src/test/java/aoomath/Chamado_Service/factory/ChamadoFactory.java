package aoomath.Chamado_Service.factory;

import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChamadoFactory {

    public static Chamado chamado(){
        return Chamado.builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .titulo("Reparo técnico")
                .descricao("Teclado parou de funcionar e monitor não liga")
                .status(Status.ABERTO)
                .criadorId(UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"))
                .criadorNome("Matheus")
                .tecnicoId(UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"))
                .tecnicoNome("Victor")
                .criadoEm(LocalDateTime.of(2024,5,5,10,12,17,25))
                .atualizadoEm(LocalDateTime.of(2024,5,6,10,12,17,25))
                .comentarios(null)
                .build();
    }

    public static ChamadoRequestDto request(){
        return ChamadoRequestDto.builder()
                .titulo("Reparo técnico")
                .descricao("Teclado parou de funcionar e monitor não liga")
                .build();
    }

    public static ChamadoResponseDto response(){
        return ChamadoResponseDto.builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .titulo("Reparo técnico")
                .descricao("Teclado parou de funcionar e monitor não liga")
                .status(Status.ABERTO)
                .criadorId(UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"))
                .criadorNome("Matheus")
                .tecnicoId(UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"))
                .tecnicoNome("Victor")
                .criadoEm(LocalDateTime.of(2024,5,5,10,12,17,25))
                .atualizadoEm(LocalDateTime.of(2024,5,6,10,12,17,25))
                .build();
    }

    public static List<Chamado> listaChamado(){
        return List.of(ChamadoFactory.chamado());
    }

    public static List<ChamadoResponseDto> listaResponse(){
        return List.of(ChamadoFactory.response());
    }
}
