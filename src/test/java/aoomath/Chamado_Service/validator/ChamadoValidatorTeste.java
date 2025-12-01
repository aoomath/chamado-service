package aoomath.Chamado_Service.validator;

import aoomath.Chamado_Service.exception.AcessoNegadoException;
import aoomath.Chamado_Service.exception.RecursoNaoEncontradoException;
import aoomath.Chamado_Service.exception.RequisicaoInvalidaException;
import aoomath.Chamado_Service.factory.ChamadoFactory;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ChamadoValidatorTeste {

    @InjectMocks
    ChamadoValidator validator;


    @Test
    public void deveLancarExcecaoAoValidarIdCriadorInvalido(){

        Chamado chamado = ChamadoFactory.chamado();
        UUID idCriador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarAcessoAoChamado(chamado,idCriador,true))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessage("Você não tem permissão para acessar este chamado.");
    }


    @Test
    public void deveLancarExcecaoAoValidarChamadoQueNaoEstaEmTratativa(){
        Chamado chamado = ChamadoFactory.chamado().toBuilder().status(Status.CONCLUIDO).build();
        UUID idTecnico = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarAcessoDoTecnico(chamado,idTecnico))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("O chamado não está em tratativa.");
    }

    @Test
    public void deveLancarExcecaoAoValidarTecnicoResponsavelPeloChamado(){
        Chamado chamado = ChamadoFactory.chamado().toBuilder().status(Status.EM_TRATATIVA).build();;
        UUID idTecnico = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarAcessoDoTecnico(chamado,idTecnico))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessage("Você precisa assumir o chamado para comentar ou concluir.");
    }

    @Test
    public void deveLancarExcecaoAoDeletarChamadoQueNaoEstaAberto(){

        Chamado chamado = ChamadoFactory.chamado().toBuilder().status(Status.CONCLUIDO).build();
        UUID idCriador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarDelete(chamado,idCriador))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Chamado não pode ser removido, pois não está aberto.");
    }

    @Test
    public void deveLancarExcecaoAoDeletarChamadoDeOutroUsuario(){

        Chamado chamado = ChamadoFactory.chamado();
        UUID idCriador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarDelete(chamado,idCriador))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessage("Você não tem permissão para acessar este recurso");
    }

}
