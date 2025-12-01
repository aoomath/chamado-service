package aoomath.Chamado_Service.controller;

import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.factory.ChamadoFactory;
import aoomath.Chamado_Service.factory.ComentarioFactory;
import aoomath.Chamado_Service.mock.WithMockCustomJwt;
import aoomath.Chamado_Service.service.ChamadoService;
import aoomath.Chamado_Service.service.ComentarioService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComentarioControlerTeste {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-management");

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private WebApplicationContext webAppContext;

    private ChamadoRequestDto requestChamado;
    private ComentarioRequestDTO requestComentario;
    private UUID chamadoId;
    private UUID comentarioId;

    @BeforeEach
    void setUp() {
        requestChamado = ChamadoFactory.request();
        requestComentario = ComentarioFactory.request();

        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders
                        .webAppContextSetup(webAppContext)
                        .apply(SecurityMockMvcConfigurers.springSecurity()) // Aplica a segurança do Spring
                        .build());

    }

    private ChamadoResponseDto criarChamadoAberto() {
        return chamadoService.criar(requestChamado,"3fa85f64-5717-4562-b3fc-2c963f66afa6" , "User Teste");
    }

    private ChamadoResponseDto criarChamadoEmTratativa() {
        ChamadoResponseDto response =  criarChamadoAberto();
        return chamadoService.assumirChamado(response.getId(), "3fa85f64-5717-4562-b3fc-2c963f66af33", "Tecnico Teste");
    }

    private ComentarioResponseDTO criarComentario(){
        ChamadoResponseDto response =  criarChamadoEmTratativa();
        chamadoId = response.getId();
        return comentarioService.criar(response.getId(), requestComentario, "Tecnico Teste", "3fa85f64-5717-4562-b3fc-2c963f66af33");
    }

    private void concluirChamado(){
        ComentarioResponseDTO response = criarComentario();
        comentarioId = response.getId();
        chamadoService.concluirChamado(chamadoId, "3fa85f64-5717-4562-b3fc-2c963f66af33");
    }



    // -------------------------------
    // TESTES POSITIVOS (fluxo feliz)
    // -------------------------------



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveCriarComentarioComSucesso() {
        ChamadoResponseDto chamado = criarChamadoEmTratativa();

        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", chamado.getId())
                .body(requestComentario)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(201)
                .body("conteudo", equalTo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor"));
    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveBuscarComentarioComSucesso() {

        ComentarioResponseDTO comentario = criarComentario();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", comentario.getId())
                .when()
                .get("/comentarios/{id}")
                .then()
                .statusCode(200)
                .body("conteudo", equalTo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor"));
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveBuscarPaginaDeComentariosPeloChamadoId() {
        criarComentario();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .pathParam("chamadoId", chamadoId)
                .when()
                .get("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].conteudo", equalTo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor"));
    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveBuscarPaginaDeComentariosDoTecnicoAutenticado() {
        criarComentario();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/comentarios//meus-comentarios")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].conteudo", equalTo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor"));
    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af98",
            username = "Admin Teste",
            roles = "ADMIN"
    )
    public void deveBuscarPaginaDeComentariosPeloTecnicoId() {
        ComentarioResponseDTO comentario = criarComentario();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .pathParam("id", comentario.getTecnicoId())
                .when()
                .get("/comentarios/tecnico/{id}")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].conteudo", equalTo("Teclado foi trocado em breve trago atualização sobre a compra do novo monitor"));
    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveDeletarComentarioComIdValido(){

        ComentarioResponseDTO comentario = criarComentario();

        given()
                .pathParam("id", comentario.getId())
                .when()
                .delete("/comentarios/{id}")
                .then()
                .statusCode(204);

        // Verifica que realmente foi deletado

        given()
                .pathParam("id", comentario.getId())
                .when()
                .get("/comentarios/{id}")
                .then()
                .statusCode(404);
    }

    // -------------------------------
    // TESTES DE EXCEÇÃO (fluxos alternativos)
    // -------------------------------



    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    @ParameterizedTest(name = "Deve lançar exceção ao salvar chamado com conteúdo inválido")
    @CsvSource({
            "'conteudoVazio', 'erro: O comentário do chamado é obrigatório'",
            "'conteudoGrande', 'erro: O conteúdo deve ter no máximo 255 caracteres'"
    })
    public void deveLancarExcecaoAoCriarChamado(String campo, String mensagemEsperada){
        ChamadoResponseDto chamado = criarChamadoEmTratativa();

        ComentarioRequestDTO dto = ComentarioFactory.request();

        switch (campo) {
            case "conteudoVazio" -> dto.setConteudo("");
            case "conteudoGrande" -> dto.setConteudo("a".repeat(256));
            default -> throw new IllegalArgumentException("Campo desconhecido: " + campo);
        }


        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", chamado.getId())
                .body(dto)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Erro de validação"))
                .body("mensagem", equalTo("Campos inválidos na requisição"))
                .body("detalhes", hasItem(mensagemEsperada));

    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancaExcecaoAoCriarComentarioComChamadoIdInexistente() {

        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", UUID.randomUUID())
                .body(requestComentario)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Chamado não encontrado"));
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoDeletarComentarioDoChamadosComStatusInvalido(){
        ChamadoResponseDto chamado = criarChamadoAberto();

        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", chamado.getId())
                .body(requestComentario)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Requisição inválida"))
                .body("mensagem", equalTo("O chamado não está em tratativa."));


    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af01",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoComentarChamadoDeOutroTecnico(){

        ChamadoResponseDto chamado = criarChamadoEmTratativa();

        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", chamado.getId())
                .body(requestComentario)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(403)
                .body("erro", equalTo("Acesso negado"))
                .body("mensagem", equalTo("Você precisa assumir o chamado para comentar ou concluir."));


    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarComentarioComIdInexistente() {


        given()
                .contentType(ContentType.JSON)
                .pathParam("id", UUID.randomUUID())
                .when()
                .get("/comentarios/{id}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Comentário não encontrado"));
    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarComentariosPeloChamadoIdInexistente() {

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .pathParam("chamadoId", UUID.randomUUID())
                .when()
                .get("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Chamado não encontrado"));

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoDeletarComentarioComIdInexistente(){

        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .delete("/comentarios/{id}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Comentário não encontrado"));

    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoDeletarComentarioDoChamadoComStatusInvalido(){

        concluirChamado();

        given()
                .pathParam("id", comentarioId)
                .when()
                .delete("/comentarios/{id}")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Requisição inválida"))
                .body("mensagem", equalTo("O chamado não está em tratativa."));


    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af22",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoDeletarComentarioDeOutroTecnico(){

        ComentarioResponseDTO comentario = criarComentario();

        given()
                .pathParam("id", comentario.getId())
                .when()
                .delete("/comentarios/{id}")
                .then()
                .statusCode(403)
                .body("erro", equalTo("Acesso negado"))
                .body("mensagem", equalTo("Você precisa assumir o chamado para comentar ou concluir."));

    }

    // -------------------------------
    // TESTES DE SEGURANÇA (role)
    // -------------------------------



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoCriarComentarioComRoleUser() {
        ChamadoResponseDto chamado = criarChamadoEmTratativa();

        given()
                .contentType(ContentType.JSON)
                .pathParam("chamadoId", chamado.getId())
                .body(requestComentario)
                .when()
                .post("/comentarios/chamado/{chamadoId}")
                .then()
                .statusCode(403);

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarComentariosDoTecnicoAutenticadoComRoleUser() {
        criarComentario();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/comentarios//meus-comentarios")
                .then()
                .statusCode(403);
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af22",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoBuscarPaginaDeComentariosPeloTecnicoIdComRoleTecnico() {
        ComentarioResponseDTO comentario = criarComentario();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .pathParam("id", comentario.getTecnicoId())
                .when()
                .get("/comentarios/tecnico/{id}")
                .then()
                .statusCode(403);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoDeletarComentarioComRoleUser(){

        ComentarioResponseDTO comentario = criarComentario();

        given()
                .pathParam("id", comentario.getId())
                .when()
                .delete("/comentarios/{id}")
                .then()
                .statusCode(403);

    }


}
