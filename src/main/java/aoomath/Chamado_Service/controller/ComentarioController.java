package aoomath.Chamado_Service.controller;



import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.security.CustomJwtAuthentication;
import aoomath.Chamado_Service.service.ComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/comentarios", produces = "application/json")
@Tag(name = "2 Comentário", description = "Endpoints de comentários")
public class ComentarioController {

    private final ComentarioService service;

    @PreAuthorize("hasRole('TECNICO')")
    @PostMapping(path = "/chamado/{chamadoId}",consumes = "application/json")
    @Operation(summary = "Criar um novo comentário no chamado pelo ID informado. Necessário Role TECNICO")
    @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso")
    public ResponseEntity<ComentarioResponseDTO> criar(@PathVariable UUID chamadoId, @RequestBody @Valid ComentarioRequestDTO dto, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(chamadoId,dto, auth.getNome(), auth.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um comentário pelo ID informado")
    @ApiResponse(responseCode = "200", description = "Comentário encontrado")
    public ResponseEntity<ComentarioResponseDTO> buscarPorId(@PathVariable UUID id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/chamado/{chamadoId}")
    @Operation(summary = "Retornar todos os comentário de um chamado pelo ID informado")
    @ApiResponse(responseCode = "200", description = "Comentários encontrados")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarPorChamado(@PathVariable UUID chamadoId, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(service.listarPorChamado(chamadoId, page, size));
    }

    @PreAuthorize("hasRole('TECNICO')")
    @GetMapping("/meus-comentarios")
    @Operation(summary = "Retornar todos os comentário do técnico autenticado. Necessário Role TECNICO")
    @ApiResponse(responseCode = "200", description = "Comentários encontrados")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarMeusComentarios( Authentication authentication, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.listarMeusComentarios(auth.getId(), page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tecnico/{id}")
    @Operation(summary = "Retornar todos os comentário do técnico pelo ID informado. Necessário Role ADMIN")
    @ApiResponse(responseCode = "200", description = "Comentários encontrados")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarPeloTecnicoId( @PathVariable UUID id, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(service.listarPeloTecnicoId(id, page, size));
    }

    @PreAuthorize("hasRole('TECNICO')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um comentário pelo ID informado. Necessário Role TECNICO")
    @ApiResponse(responseCode = "204", description = "Comentário removido com sucesso")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        service.deletar(id, auth.getId());
        return ResponseEntity.noContent().build();
    }




}
