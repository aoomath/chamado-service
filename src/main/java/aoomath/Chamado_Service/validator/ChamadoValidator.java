package aoomath.Chamado_Service.validator;

import aoomath.Chamado_Service.exception.AcessoNegadoException;
import aoomath.Chamado_Service.exception.RequisicaoInvalidaException;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChamadoValidator {

    public void validarAcessoAoChamado(Chamado chamado, UUID usuarioId, boolean isUser) {

        if (!isUser) {
            return;
        }

        if (!chamado.getCriadorId().equals(usuarioId)) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este chamado.");
        }
    }


    public void validarAcessoDoTecnico(Chamado chamado, UUID tecnicoId) {

        if (!chamado.getStatus().equals(Status.EM_TRATATIVA)) {
            throw new RequisicaoInvalidaException("O chamado não está em tratativa.");
        }


        if (!tecnicoId.equals(chamado.getTecnicoId())) {
            throw new AcessoNegadoException("Você precisa assumir o chamado para comentar ou concluir.");
        }

    }

    public void validarDelete(Chamado chamado, UUID criadorId) {

        if (!chamado.getStatus().equals(Status.ABERTO)) {
            throw new RequisicaoInvalidaException("Chamado não pode ser removido, pois não está aberto.");
        }

        if (!chamado.getCriadorId().equals(criadorId)) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este recurso");
        }
    }
}
