package aoomath.Chamado_Service.specification;

import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import aoomath.Chamado_Service.repository.ChamadoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Transactional
@DataJpaTest
public class ChamadoSpecificationTeste {

    @Autowired
    private ChamadoRepository repository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18-alpine");

    private Chamado criarChamadoAberto() {
        return repository.save(new Chamado(null,"Titulo 1", "Descricao 1", Status.ABERTO, UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"),"Matheus",null,null, LocalDateTime.now(), LocalDateTime.now(),null));
    }

    private Chamado criarChamadoEmTratativa() {
        return repository.save(new Chamado(null,"Titulo 2", "Descricao 2", Status.EM_TRATATIVA, UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"),"Matheus",UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"),"Victor", LocalDateTime.now(), LocalDateTime.now(),null));
    }

    private Chamado criarChamadoConcluido() {
        return repository.save(new Chamado(null,"Titulo 3", "Descricao 3", Status.CONCLUIDO, UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"),"Matheus",UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"),"Victor", LocalDateTime.now(), LocalDateTime.now(),null));
    }

    @Test
    void deveFiltrarPorUsuario() {
        criarChamadoAberto();
        criarChamadoConcluido();

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario( UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"), null, null);

        List<Chamado> resultado = repository.findAll(spec);

        assertEquals(2, resultado.size());
        assertEquals("Titulo 1", resultado.getFirst().getTitulo());

    }

    @Test
    void deveFiltrarPorTecnico() {
        criarChamadoEmTratativa();
        criarChamadoConcluido();

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario( null, UUID.fromString("40b01278-f651-4259-b8e8-7edf26eb6fc8"), null);

        List<Chamado> resultado = repository.findAll(spec);

        assertEquals(2, resultado.size());
        assertEquals("Titulo 3", resultado.getLast().getTitulo());

    }

    @Test
    void deveFiltrarPorUsuarioEStatus() {
        criarChamadoAberto();
        criarChamadoEmTratativa();
        criarChamadoConcluido();

        Specification<Chamado> spec = ChamadoSpecification.filtrarPorUsuario(UUID.fromString("c3b49d6c-d7df-49eb-91b5-f9df6e1d3749"), null, "CONCLUIDO");

        List<Chamado> resultado = repository.findAll(spec);

        assertEquals(1, resultado.size());
        assertEquals("Titulo 3", resultado.getFirst().getTitulo());
    }
}
