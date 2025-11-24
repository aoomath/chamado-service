package aoomath.Chamado_Service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "comentario")
@EntityListeners(AuditingEntityListener.class)
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "tecnico_id")
    private UUID tecnicoId;
    @Column(name = "tecnico_nome")
    private String tecnicoNome;
    private String conteudo;
    @Column(name = "criado_em")
    @CreatedDate
    private LocalDateTime criadoEm;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;
}
