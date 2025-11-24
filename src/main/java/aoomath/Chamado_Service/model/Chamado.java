package aoomath.Chamado_Service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "chamado")
@EntityListeners(AuditingEntityListener.class)
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String titulo;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "criador_id")
    private UUID criadorId;
    @Column(name = "criador_nome")
    private String criadorNome;
    @Column(name = "tecnico_id")
    private UUID tecnicoId;
    @Column(name = "tecnico_nome")
    private String tecnicoNome;
    @Column(name = "criado_em")
    @CreatedDate
    private LocalDateTime criadoEm;
    @Column(name = "atualizado_em")
    @LastModifiedDate
    private LocalDateTime atualizadoEm;
    @Builder.Default
    @OneToMany (mappedBy = "chamado",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();;
}
