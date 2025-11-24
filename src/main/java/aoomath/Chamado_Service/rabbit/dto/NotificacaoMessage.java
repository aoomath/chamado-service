package aoomath.Chamado_Service.rabbit.dto;

import java.util.UUID;

public record NotificacaoMessage(
        UUID usuarioId,
        String chamadoNome,
        String tecnicoNome,
        String tipo
) {
}
