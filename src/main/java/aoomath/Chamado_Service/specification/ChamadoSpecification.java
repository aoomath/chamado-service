package aoomath.Chamado_Service.specification;


import aoomath.Chamado_Service.model.Chamado;
import aoomath.Chamado_Service.model.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ChamadoSpecification {

    public static Specification<Chamado> filtrarPorUsuario(UUID criadorId, UUID tecnicoId, String status) {
        return (root, query, builder) -> {

            Predicate predicate = builder.conjunction();

            if (criadorId != null) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("criadorId"), criadorId));
            }

            if (tecnicoId != null) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("tecnicoId"), tecnicoId));
            }

            if (status != null && !status.isBlank()) {
                Predicate statusPred = builder.equal(root.get("status"), Status.valueOf(status));
                predicate = builder.and(predicate, statusPred);
            }

            return predicate;
        };
    }
}
