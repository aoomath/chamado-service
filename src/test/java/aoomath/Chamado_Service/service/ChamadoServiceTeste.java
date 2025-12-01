package aoomath.Chamado_Service.service;

import aoomath.Chamado_Service.dto.chamado.ChamadoMapper;
import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.exception.RecursoNaoEncontradoException;
import aoomath.Chamado_Service.factory.ChamadoFactory;
import aoomath.Chamado_Service.factory.PageFactory;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import aoomath.Chamado_Service.rabbit.dto.NotificacaoMessage;
import aoomath.Chamado_Service.rabbit.service.NotificacaoProducer;
import aoomath.Chamado_Service.repository.ChamadoRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChamadoServiceTeste {

    @Mock
    private  ChamadoRepository repository;
    @Mock
    private  ChamadoMapper mapper;
    @Mock
    private  NotificacaoProducer notificacaoProducer;
    @Mock
    private  ChamadoValidator validator;

    @InjectMocks
    private ChamadoService service;

    private UUID idChamado;
    private Chamado chamado;
    private String idCriador;
    private String idTecnico;
    private ChamadoResponseDto response;
    private ChamadoRequestDto request;
    private Pageable pageable;
    private Page<Chamado> page;
    private Page<ChamadoResponseDto> pageResponse;

    @BeforeEach
    void init(){
        idChamado = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        idCriador = "c3b49d6c-d7df-49eb-91b5-f9df6e1d3749";
        idTecnico = "40b01278-f651-4259-b8e8-7edf26eb6fc8";
        chamado = ChamadoFactory.chamado();
        response = ChamadoFactory.response();
        request = ChamadoFactory.request();
        pageable = PageRequest.of(0,10, Sort.by("criadoEm").descending());
        page= PageFactory.listaToPage(ChamadoFactory.listaChamado());
        pageResponse= PageFactory.listaToPage(ChamadoFactory.listaResponse());
    }

    // -------------------------------
    // TESTES POSITIVOS (fluxo feliz)
    // -------------------------------

    @Test
    public void deveCriarChamadoComSucesso() {
        Chamado chamadoSemId = ChamadoFactory.chamado().toBuilder().id(null).criadorId(null).criadorNome(null).tecnicoId(null).tecnicoNome(null).build();

        when(mapper.toEntity(request)).thenReturn(chamadoSemId);
        when(repository.save(any(Chamado.class))).thenReturn(chamado);
        when(mapper.toResponse(chamado)).thenReturn(response);

        ChamadoResponseDto resultado = service.criar(request, String.valueOf(chamado.getCriadorId()), chamado.getCriadorNome());

        assertNotNull(resultado.getId());
        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getTitulo(), resultado.getTitulo());
        assertEquals(chamado.getCriadorId(), resultado.getCriadorId());
        assertEquals(chamado.getCriadorNome(), resultado.getCriadorNome());
    }

    @Test
    public void deveBuscarChamadoQuandoIdExistir() {

        when(repository.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(mapper.toResponse(chamado)).thenReturn(response);

        ChamadoResponseDto resultado = service.buscarPorId(idChamado, idCriador,true);

        assertNotNull(resultado.getId());
        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getTitulo(), resultado.getTitulo());

        verify(validator).validarAcessoAoChamado(chamado, UUID.fromString(idCriador), true);

    }


    @Test
    public void deveRetornarListaDeChamado() {

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toPageResponse(page)).thenReturn(pageResponse);

        Page<ChamadoResponseDto> resultado = service.listarChamados(0,10, "ABERTO");

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }


    @Test
    public void deveRetornarListaDeChamadoDoUsuarioAutenticado() {
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toPageResponse(page)).thenReturn(pageResponse);

        Page<ChamadoResponseDto> resultado = service.listarChamadosDoUsuario(0,10,idCriador, "ABERTO");

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }


    @Test
    public void deveRetornarListaDeChamadoDoTecnicoAutenticado() {
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toPageResponse(page)).thenReturn(pageResponse);

        Page<ChamadoResponseDto> resultado = service.listarChamadosDoTecnico(0,10,idTecnico, "ABERTO");

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }

    @Test
    public void deveRetornarListaDeChamadoPeloCriadorId() {
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toPageResponse(page)).thenReturn(pageResponse);

        Page<ChamadoResponseDto> resultado = service.listarChamadosPeloCriadorId(0,10, UUID.fromString(idCriador), "ABERTO");

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }


    @Test
    public void deveRetornarListaDeChamadoPeloTecnicoId() {
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toPageResponse(page)).thenReturn(pageResponse);

        Page<ChamadoResponseDto> resultado = service.listarChamadosPeloTecnicoId(0,10, UUID.fromString(idTecnico), "ABERTO");

        assertNotNull(resultado);
        assertEquals(1,resultado.getContent().size());
    }


    @Test
    public void deveAssumirChamadoComSucesso() {
        Chamado chamadoEmTratativa = ChamadoFactory.chamado().toBuilder().status(Status.EM_TRATATIVA).build();
        ChamadoResponseDto responseEmTratativa = ChamadoFactory.response().toBuilder().status(Status.EM_TRATATIVA).build();

        when(repository.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(mapper.toResponse(chamadoEmTratativa)).thenReturn(responseEmTratativa);

        ChamadoResponseDto resultado = service.assumirChamado(idChamado,idTecnico, "Victor");

        assertEquals(Status.EM_TRATATIVA, resultado.getStatus());
        assertEquals(UUID.fromString(idTecnico), resultado.getTecnicoId());
        assertEquals("Victor", resultado.getTecnicoNome());

        verify(repository).save(chamadoEmTratativa);
        verify(notificacaoProducer).enviarNotificacao(any(NotificacaoMessage.class));
    }


    @Test
    public void deveConcluirChamadoComSucesso() {
        Chamado chamadoConcluido = ChamadoFactory.chamado().toBuilder().status(Status.CONCLUIDO).build();
        when(repository.findById(idChamado)).thenReturn(Optional.of(chamado));

        service.concluirChamado(idChamado,idTecnico);

        verify(validator).validarAcessoDoTecnico(chamado, UUID.fromString(idTecnico));
        verify(repository).save(chamadoConcluido);
        verify(notificacaoProducer).enviarNotificacao(any(NotificacaoMessage.class));
    }


    @Test
    public void deveDeletarChamadoQuandoIdExistir(){

        when(repository.findById(idChamado)).thenReturn(Optional.ofNullable(chamado));
        doNothing().when(repository).delete(chamado);

        service.deletar(idChamado,idCriador);

        verify(repository).findById(idChamado);
        verify(validator).validarDelete(chamado, UUID.fromString(idCriador));
        verify(repository).delete(chamado);
    }


    // -------------------------------
    // TESTES DE EXCEÇÃO (fluxos alternativos)
    // -------------------------------


    @Test
    public void deveLancarExcecaoQuandoBuscarComIdInexistente(){

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id, idCriador,true))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");
    }

    @Test
    public void deveLancarExcecaoAoAssumirChamadoComIdInexistente(){
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assumirChamado(id, idTecnico,"Paulo"))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");
    }

    @Test
    public void deveLancarExcecaoAoConcluirChamadoComIdInexistente(){
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.concluirChamado(id, idTecnico))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");
    }

    @Test
    public void deveLancarExcecaoAoDeletarrChamadoComIdInexistente(){
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(id, idCriador))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Chamado não encontrado");
    }


}
