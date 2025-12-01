package aoomath.Chamado_Service.service;

import aoomath.Chamado_Service.dto.comentario.ComentarioMapper;
import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.exception.RecursoNaoEncontradoException;
import aoomath.Chamado_Service.factory.ChamadoFactory;
import aoomath.Chamado_Service.factory.ComentarioFactory;
import aoomath.Chamado_Service.factory.PageFactory;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Comentario;
import aoomath.Chamado_Service.rabbit.dto.NotificacaoMessage;
import aoomath.Chamado_Service.rabbit.service.NotificacaoProducer;
import aoomath.Chamado_Service.repository.ChamadoRepository;
import aoomath.Chamado_Service.repository.ComentarioRepository;
import aoomath.Chamado_Service.validator.ChamadoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTeste {

    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private ComentarioMapper mapper;
    @Mock
    private NotificacaoProducer notificacaoProducer;
    @Mock
    private ChamadoValidator validator;

    @InjectMocks
    private ComentarioService service;

    private UUID idChamado;
    private Chamado chamado;
    private UUID idComentario;
    private UUID idTecnico;
    private Comentario comentario;
    private ComentarioResponseDTO response;
    private ComentarioRequestDTO request;
    private Pageable pageable;
    private Page<Comentario> page;
    private Page<ComentarioResponseDTO> pageResponse;


    @BeforeEach
    void init(){
        idChamado = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        idComentario = UUID.fromString("7d51cf0e-2b7b-4a87-b0c7-3f0dbfe2b4fb");
        idTecnico = UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8");
        chamado = ChamadoFactory.chamado();
        comentario = ComentarioFactory.comentario();
        response = ComentarioFactory.response();
        request = ComentarioFactory.request();
        pageable = PageRequest.of(0,10, Sort.by("criadoEm").descending());
        page= PageFactory.listaToPage(ComentarioFactory.listaComentario());
        pageResponse= PageFactory.listaToPage(ComentarioFactory.listaResponse());
    }

    // -------------------------------
    // TESTES POSITIVOS (fluxo feliz)
    // -------------------------------



    @Test
    public void deveCriarComentarioComSucesso(){
        Comentario comentarioSemId= ComentarioFactory.comentario().toBuilder().id(null).tecnicoNome(null).tecnicoId(null).build();

        when(chamadoRepository.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(mapper.toEntity(request)).thenReturn(comentarioSemId);
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        when(mapper.toResponse(comentario)).thenReturn(response);

        ComentarioResponseDTO resultado = service.criar(idChamado, request, "Victor", String.valueOf(idTecnico));

        assertNotNull(resultado.getId());
        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getConteudo(), resultado.getConteudo());

        verify(validator).validarAcessoDoTecnico(chamado, idTecnico);
        verify(notificacaoProducer).enviarNotificacao(any(NotificacaoMessage.class));

    }



    @Test
    public void deveBuscarComentarioQuandoIdExistir(){
        when(comentarioRepository.findById(idComentario)).thenReturn(Optional.of(comentario));
        when(mapper.toResponse(comentario)).thenReturn(response);

        ComentarioResponseDTO resultado = service.buscarPorId(idComentario);

        assertNotNull(resultado.getId());
        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getConteudo(), resultado.getConteudo());

    }


    @Test
    public void deveBuscarListaDeComentarioPeloChamadoId(){
        when(chamadoRepository.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(comentarioRepository.findByChamadoId(chamado.getId(),pageable)).thenReturn(pageResponse);

        Page<ComentarioResponseDTO> resultado = service.listarPorChamado(idChamado, 0 , 10);

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }

    @Test
    public void deveBuscarListaDeComentarioDoTecnicoAutenticado(){
        when(comentarioRepository.findByTecnicoId(idTecnico,pageable)).thenReturn(pageResponse);

        Page<ComentarioResponseDTO> resultado = service.listarMeusComentarios(String.valueOf(idTecnico), 0 , 10);

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());

    }

    @Test
    public void deveBuscarListaDeComentarioPeloTecnicoId(){
        when(comentarioRepository.findByTecnicoId(idTecnico,pageable)).thenReturn(pageResponse);

        Page<ComentarioResponseDTO> resultado = service.listarPeloTecnicoId(idTecnico, 0 , 10);

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());

    }

    @Test
    public void deveDeletarComentarioQuandoIdExistir(){

        when(comentarioRepository.findById(idComentario)).thenReturn(Optional.of(comentario));
        doNothing().when(comentarioRepository).delete(comentario);

        service.deletar(idComentario, String.valueOf(idTecnico));

        verify(comentarioRepository).findById(idComentario);
        verify(validator).validarAcessoDoTecnico(chamado, idTecnico);
        verify(comentarioRepository).delete(comentario);
    }


    // -------------------------------
    // TESTES DE EXCEÇÃO (fluxos alternativos)
    // -------------------------------


    @Test
    public void deveLancarExcecaoQuandoCriarComIdChamadoInexistente(){

        UUID id = UUID.randomUUID();

        when(chamadoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(id, request, "Victor", String.valueOf(idTecnico)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");

    }

    @Test
    public void deveLancarExcecaoQuandoBuscarComIdInexistente(){

        UUID id = UUID.randomUUID();

        when(comentarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Comentário não encontrado");

    }


    @Test
    public void deveLancarExcecaoQuandoLiscarComChamadoIdInexistente(){
        UUID id = UUID.randomUUID();

        when(chamadoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarPorChamado(id, 0, 10))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");

    }

    @Test
    public void deveLancarExcecaoQuandoDeletarComIdInexistente(){

        UUID id = UUID.randomUUID();

        when(comentarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(id, String.valueOf(idTecnico)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Comentário não encontrado");

    }

}
