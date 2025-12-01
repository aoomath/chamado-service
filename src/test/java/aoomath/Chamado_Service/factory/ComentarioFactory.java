package aoomath.Chamado_Service.factory;

import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.model.Comentario;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ComentarioFactory {

    public static Comentario comentario() {
        return Comentario.builder()
                .id(UUID.fromString("7d51cf0e-2b7b-4a87-b0c7-3f0dbfe2b4fb"))
                .conteudo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor")
                .criadoEm(LocalDateTime.of(2024,5,5,10,12,17,25))
                .tecnicoId(UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"))
                .tecnicoNome("Victor")
                .chamado(ChamadoFactory.chamado())
                .build();
    }

    public static ComentarioRequestDTO request(){
        return ComentarioRequestDTO.builder()
                .conteudo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor")
                .build();
    }

    public static ComentarioResponseDTO response(){
        return ComentarioResponseDTO.builder()
                .id(UUID.fromString("7d51cf0e-2b7b-4a87-b0c7-3f0dbfe2b4fb"))
                .conteudo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor")
                .criadoEm(LocalDateTime.of(2024,5,5,10,12,17,25))
                .tecnicoId(UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"))
                .tecnicoNome("Victor")
                .build();
    }

    public static List<Comentario> listaComentario(){
        return List.of(ComentarioFactory.comentario());
    }

    public static List<ComentarioResponseDTO> listaResponse(){
        return List.of(ComentarioFactory.response());
    }
}
