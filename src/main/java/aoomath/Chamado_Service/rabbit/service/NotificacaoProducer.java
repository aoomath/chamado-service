package aoomath.Chamado_Service.rabbit.service;


import aoomath.Chamado_Service.config.RabbitConfig;
import aoomath.Chamado_Service.rabbit.dto.NotificacaoMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarNotificacao(NotificacaoMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                message
        );
    }
}
