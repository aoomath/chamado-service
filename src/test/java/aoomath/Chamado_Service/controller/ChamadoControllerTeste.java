package aoomath.Chamado_Service.controller;

import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.factory.ChamadoFactory;


import aoomath.Chamado_Service.mock.WithMockCustomJwt;
import aoomath.Chamado_Service.service.ChamadoService;
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
public class ChamadoControllerTeste {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-management");

    @Autowired
    private ChamadoService service;

    @Autowired
    private WebApplicationContext webAppContext;

    private ChamadoRequestDto request;

    @BeforeEach
    void setUp() {
        request = ChamadoFactory.request();
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders
                        .webAppContextSetup(webAppContext)
                        .apply(SecurityMockMvcConfigurers.springSecurity()) // Aplica a segurança do Spring
                        .build()
        );

    }

    private ChamadoResponseDto criarChamadoAberto() {
        return service.criar(request,"3fa85f64-5717-4562-b3fc-2c963f66afa6" , "User Teste");
    }

    private ChamadoResponseDto criarChamadoEmTratativa() {
        ChamadoResponseDto response =  criarChamadoAberto();
        return service.assumirChamado(response.getId(), "3fa85f64-5717-4562-b3fc-2c963f66af33", "Tecnico Teste");
    }



    // -------------------------------
    // TESTES POSITIVOS (fluxo feliz)
    // -------------------------------



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveCriarChamadoComSucesso() {

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chamados")
                .then()
                .statusCode(201)
                .body("titulo", equalTo("Reparo técnico"));
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveBuscarChamadoComSucesso() {

        ChamadoResponseDto chamadoCriado = criarChamadoAberto();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", chamadoCriado.getId())
                .when()
                .get("/chamados/{id}")
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Reparo técnico"));
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveBuscarPaginaDeChamados(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .when()
                .get("/chamados")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].titulo", equalTo("Reparo técnico"));

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveBuscarPaginaDeChamadosDoUsuarioAutenticado(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .when()
                .get("/chamados/meus-chamados")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].titulo", equalTo("Reparo técnico"));

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveBuscarPaginaDeChamadosDoTecnicoAutenticado(){

        criarChamadoEmTratativa();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "EM_TRATATIVA")
                .when()
                .get("/chamados/minhas-tratativas")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].titulo", equalTo("Reparo técnico"));

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveBuscarPaginaDeChamadosPeloUsuarioId(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .pathParam("id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .when()
                .get("/chamados/criador/{id}")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].titulo", equalTo("Reparo técnico"));

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af98",
            username = "Admin Teste",
            roles = "ADMIN"
    )
    public void deveBuscarPaginaDeChamadosPeloTecnicoId(){

        criarChamadoEmTratativa();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "EM_TRATATIVA")
                .pathParam("id", "3fa85f64-5717-4562-b3fc-2c963f66af33")
                .when()
                .get("/chamados/tecnico/{id}")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].titulo", equalTo("Reparo técnico"));

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveAssumirChamadoPeloIdExistente(){

       ChamadoResponseDto response = criarChamadoAberto();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/assumir")
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Reparo técnico"))
                .body("tecnicoId", equalTo("3fa85f64-5717-4562-b3fc-2c963f66af33"));

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveConcluirChamadoPeloIdExistente(){

        ChamadoResponseDto response = criarChamadoEmTratativa();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/concluir")
                .then()
                .statusCode(204);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveDeletarChamadoComIdValido(){

        ChamadoResponseDto chamadoCriado = criarChamadoAberto();

        given()
                .pathParam("id", chamadoCriado.getId())
                .when()
                .delete("/chamados/{id}")
                .then()
                .statusCode(204);

        // Verifica que realmente foi deletado

        given()
                .pathParam("id", chamadoCriado.getId())
                .when()
                .get("/chamados/{id}")
                .then()
                .statusCode(404);
    }


    // -------------------------------
    // TESTES DE EXCEÇÃO (fluxos alternativos)
    // -------------------------------


    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    @ParameterizedTest(name = "Deve lançar exceção ao salvar chamado com campo {0} inválido")
    @CsvSource({
            "'titulo', 'erro: O título é obrigatório'",
            "'descricao', 'erro: A descrição é obrigatória'"
    })
    public void deveLancarExcecaoAoCriarChamado(String campo, String mensagemEsperada){

        ChamadoRequestDto dto = ChamadoFactory.request();

        switch (campo) {
            case "titulo" -> dto.setTitulo("");
            case "descricao" -> dto.setDescricao("");
            default -> throw new IllegalArgumentException("Campo desconhecido: " + campo);
        }


        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/chamados")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Erro de validação"))
                .body("mensagem", equalTo("Campos inválidos na requisição"))
                .body("detalhes", hasItem(mensagemEsperada));

    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarChamadoComIdInexistente(){

        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .get("/chamados/{id}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Chamado não encontrado"));
    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66a543",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarChamadoDeOutroUsuario() {

        ChamadoResponseDto chamadoCriado = criarChamadoAberto();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", chamadoCriado.getId())
                .when()
                .get("/chamados/{id}")
                .then()
                .statusCode(403)
                .body("erro", equalTo("Acesso negado"))
                .body("mensagem", equalTo("Você não tem permissão para acessar este chamado."));
    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoAssumirChamadoComIdInexistente(){


        given()
                .pathParam("chamadoId", UUID.randomUUID())
                .when()
                .patch("/chamados/{chamadoId}/assumir")
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
    public void deveLancarExcecaoAoConcluirChamadoComIdInexistente(){


        given()
                .pathParam("chamadoId", UUID.randomUUID())
                .when()
                .patch("/chamados/{chamadoId}/concluir")
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
    public void deveLancarExcecaoAoConcluirChamadoComStatusInvalido(){

        ChamadoResponseDto response = criarChamadoAberto();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/concluir")
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
    public void deveLancarExcecaoAoConcluirChamadoDeOutroTecnico(){

        ChamadoResponseDto response = criarChamadoEmTratativa();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/concluir")
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
    public void deveLancarExcecaoAoDeletarChamadoComIdInexistente(){

        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .delete("/chamados/{id}")
                .then()
                .statusCode(404)
                .body("erro", equalTo("Recurso não encontrado"))
                .body("mensagem", equalTo("Chamado não encontrado"));

    }

    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoDeletarChamadoComStatusInvalido(){

        ChamadoResponseDto response = criarChamadoEmTratativa();

        given()
                .pathParam("id", response.getId())
                .when()
                .delete("/chamados/{id}")
                .then()
                .statusCode(400)
                .body("erro", equalTo("Requisição inválida"))
                .body("mensagem", equalTo("Chamado não pode ser removido, pois não está aberto."));


    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoDeletarChamadoDeOutroUsuario(){

        ChamadoResponseDto chamadoCriado = criarChamadoAberto();

        given()
                .pathParam("id", chamadoCriado.getId())
                .when()
                .delete("/chamados/{id}")
                .then()
                .statusCode(403)
                .body("erro", equalTo("Acesso negado"))
                .body("mensagem", equalTo("Você não tem permissão para acessar este recurso"));

    }


    // -------------------------------
    // TESTES DE SEGURANÇA (role)
    // -------------------------------



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoCriarChamadoComRoleTecnico() {

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chamados")
                .then()
                .statusCode(403);
    }




    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarPaginaDeChamadosComRoleUser(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .when()
                .get("/chamados")
                .then()
                .statusCode(403);

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancarExcecaoAoBuscarChamadosDoUsuarioComRoleTecnico(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .when()
                .get("/chamados/meus-chamados")
                .then()
                .statusCode(403);

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancaExcecaoAoBuscarChamadosDoTecnicoComRoleUser(){

        criarChamadoEmTratativa();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "EM_TRATATIVA")
                .when()
                .get("/chamados/minhas-tratativas")
                .then()
                .statusCode(403);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancarExcecaoAoBuscarChamadosPeloUsuarioIdComRoleUser(){

        criarChamadoAberto();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "ABERTO")
                .pathParam("id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .when()
                .get("/chamados/criador/{id}")
                .then()
                .statusCode(403);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancaExcecaoAoBuscarChamadosPeloTecnicoIdComRoleUser(){

        criarChamadoEmTratativa();

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("status", "EM_TRATATIVA")
                .pathParam("id", "3fa85f64-5717-4562-b3fc-2c963f66af33")
                .when()
                .get("/chamados/tecnico/{id}")
                .then()
                .statusCode(403);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancaExcecaoAoAssumirChamadoComRoleUser(){

        ChamadoResponseDto response = criarChamadoAberto();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/assumir")
                .then()
                .statusCode(403);

    }


    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            username = "User Teste"
    )
    public void deveLancaExcecaoAoConcluirChamadoComRoleUser(){

        ChamadoResponseDto response = criarChamadoEmTratativa();

        given()
                .pathParam("chamadoId", response.getId())
                .when()
                .patch("/chamados/{chamadoId}/concluir")
                .then()
                .statusCode(403);

    }



    @Test
    @WithMockCustomJwt(
            userId = "3fa85f64-5717-4562-b3fc-2c963f66af33",
            username = "Tecnico Teste",
            roles = "TECNICO"
    )
    public void deveLancaExcecaoAoDeletarChamadoComRoleTecnico(){

        ChamadoResponseDto chamadoCriado = criarChamadoAberto();

        given()
                .pathParam("id", chamadoCriado.getId())
                .when()
                .delete("/chamados/{id}")
                .then()
                .statusCode(403);

    }


}
