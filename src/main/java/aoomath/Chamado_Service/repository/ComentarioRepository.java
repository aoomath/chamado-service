package aoomath.Chamado_Service.repository;


import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    Page<ComentarioResponseDTO> findByChamadoId(UUID chamadoId, Pageable pageable);

    Page<ComentarioResponseDTO> findByTecnicoId(UUID tecnicoId, Pageable pageable);
}
