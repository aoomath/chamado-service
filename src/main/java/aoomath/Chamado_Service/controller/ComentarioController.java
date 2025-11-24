package aoomath.Chamado_Service.controller;



import aoomath.Chamado_Service.dto.comentario.ComentarioRequestDTO;
import aoomath.Chamado_Service.dto.comentario.ComentarioResponseDTO;
import aoomath.Chamado_Service.security.CustomJwtAuthentication;
import aoomath.Chamado_Service.service.ComentarioService;
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
@RequestMapping("/comentario")
public class ComentarioController {

    private final ComentarioService service;

    @PreAuthorize("hasRole('TECNICO')")
    @PostMapping(path = "/chamado/{chamadoId}",consumes = "application/json", produces = "application/json")
    public ResponseEntity<ComentarioResponseDTO> salvar(@PathVariable UUID chamadoId, @RequestBody @Valid ComentarioRequestDTO dto, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(chamadoId,dto, auth.getNome(), auth.getId()));
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<ComentarioResponseDTO> buscarPorId(@PathVariable UUID id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping(path = "/chamado/{chamadoId}", produces = "application/json")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarPorChamado(@PathVariable UUID chamadoId, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(service.listarPorChamado(chamadoId, page, size));
    }

    @PreAuthorize("hasRole('TECNICO')")
    @GetMapping(path = "/meus-comentarios", produces = "application/json")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarMeusComentarios( Authentication authentication, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.listarMeusComentarios(auth.getId(), page, size));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/tecnico/id", produces = "application/json")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarPeloTecnicoId( UUID id, @RequestParam(defaultValue = "0") int page,  @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(service.listarPeloTecnicoId(id, page, size));
    }

    @PreAuthorize("hasRole('TECNICO')")
    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        service.deletar(id, auth.getId());
        return ResponseEntity.noContent().build();
    }




}
