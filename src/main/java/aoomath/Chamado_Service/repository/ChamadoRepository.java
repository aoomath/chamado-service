package aoomath.Chamado_Service.repository;

import aoomath.Chamado_Service.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.UUID;


public interface ChamadoRepository extends JpaRepository<Chamado, UUID>, JpaSpecificationExecutor<Chamado>{

}
