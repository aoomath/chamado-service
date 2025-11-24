package aoomath.Chamado_Service.controller;


import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.security.CustomJwtAuthentication;
import aoomath.Chamado_Service.service.ChamadoService;
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
@RequestMapping("/chamado")
public class ChamadoController {

     private final ChamadoService service;

     @PreAuthorize("hasRole('USER')")
     @PostMapping
     public ResponseEntity<ChamadoResponseDto> criar (@RequestBody ChamadoRequestDto request, Authentication authentication){
         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request,auth.getId(), auth.getNome()));
     }

     @GetMapping("/{id}")
     public ResponseEntity<ChamadoResponseDto> buscarPorId (@PathVariable UUID id, Authentication authentication){
         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         return ResponseEntity.ok(service.buscarPorId(id, auth));
     }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
     @GetMapping
     public ResponseEntity<Page<ChamadoResponseDto>> listarChamados(
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(required = false) String status){

        return ResponseEntity.ok(service.listarChamados(page, size, status));
     }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/meus-chamados")
     public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosDoUsuario (
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(required = false) String status,
             Authentication authentication){

         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         return ResponseEntity.ok(service.listarChamadosDoUsuario(page, size, auth.getId(), status));
     }

    @PreAuthorize("hasRole('TECNICO')")
    @GetMapping("/minhas-tratativas")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosDoTecnico (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication){

        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.listarChamadosDoTecnico(page, size, auth.getId(), status));
    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @GetMapping("/criador/{id}")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosPeloCriadorId (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @PathVariable UUID id ){

        return ResponseEntity.ok(service.listarChamadosPeloCriadorId(page, size, id, status));

    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @GetMapping("/tecnico/{id}")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosPeloTecnicoId (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @PathVariable UUID id ){

        return ResponseEntity.ok(service.listarChamadosPeloTecnicoId(page, size, id, status));

    }

    @PreAuthorize("hasRole('TECNICO')")
    @PatchMapping("/{chamadoId}/assumir")
    public ResponseEntity<ChamadoResponseDto> assumirChamado (@PathVariable UUID chamadoId, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.assumirChamado(chamadoId, auth.getId(), auth.getNome()));

    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @PatchMapping("/{chamadoId}/concluir")
    public ResponseEntity<Void> concluirChamado (@PathVariable UUID chamadoId, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        service.concluirChamado(chamadoId, auth.getId());
        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         service.deletar(id, auth.getId());
         return ResponseEntity.noContent().build();
     }
}
