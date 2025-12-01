package aoomath.Chamado_Service.controller;


import aoomath.Chamado_Service.dto.chamado.ChamadoRequestDto;
import aoomath.Chamado_Service.dto.chamado.ChamadoResponseDto;
import aoomath.Chamado_Service.security.CustomJwtAuthentication;
import aoomath.Chamado_Service.service.ChamadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(path = "/chamados", produces = "application/json")
@Tag(name = "1 Chamado", description = "Endpoints de chamados")
public class ChamadoController {

     private final ChamadoService service;

     @PreAuthorize("hasRole('USER')")
     @PostMapping(consumes = "application/json")
     @Operation(summary = "Criar um novo chamado no sistema. Necessário Role USER")
     @ApiResponse(responseCode = "201", description = "Chamado criado com sucesso")
     public ResponseEntity<ChamadoResponseDto> criar (@RequestBody @Valid ChamadoRequestDto request, Authentication authentication){
         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request,auth.getId(), auth.getNome()));
     }

     @GetMapping("/{id}")
     @Operation(summary = "Buscar um chamado pelo ID informado")
     @ApiResponse(responseCode = "200", description = "Chamado encontrado")
     public ResponseEntity<ChamadoResponseDto> buscarPorId (@PathVariable UUID id, Authentication authentication){
         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         boolean isUser = auth.hasRole("USER");
         return ResponseEntity.ok(service.buscarPorId(id, auth.getId(), isUser));
     }

     @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
     @GetMapping
     @Operation(summary = "Retornar todos os chamados cadastrados de forma paginada. Necessário Role ADMIN ou TECNICO")
     @ApiResponse(responseCode = "200", description = "Chamados encontrados")
     public ResponseEntity<Page<ChamadoResponseDto>> listarChamados(
             @Parameter(description = "Número da página (inicia em 0)")
             @RequestParam(defaultValue = "0") int page,
             @Parameter(description = "Tamanho da página")
             @RequestParam(defaultValue = "10") int size,
             @Parameter(description = "Status do chamado", example= "ABERTO")
             @RequestParam(required = false) String status){

        return ResponseEntity.ok(service.listarChamados(page, size, status));
     }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/meus-chamados")
    @Operation(summary = "Retornar todos os chamados criados pelo usuário autenticado. Necessário Role USER")
    @ApiResponse(responseCode = "200", description = "Chamados encontrados")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosDoUsuario (
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Status do chamado", example= "ABERTO")
            @RequestParam(required = false) String status,
             Authentication authentication){

         CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         return ResponseEntity.ok(service.listarChamadosDoUsuario(page, size, auth.getId(), status));
     }

    @PreAuthorize("hasRole('TECNICO')")
    @GetMapping("/minhas-tratativas")
    @Operation(summary = "Retornar todos os chamados que o técnico assumiu a tratativa. Necessário Role TECNICO")
    @ApiResponse(responseCode = "200", description = "Chamados encontrados")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosDoTecnico (
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Status do chamado", example= "EM_TRATATIVA")
            @RequestParam(required = false) String status,
            Authentication authentication){

        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.listarChamadosDoTecnico(page, size, auth.getId(), status));
    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @GetMapping("/criador/{id}")
    @Operation(summary = "Retornar todos os chamados de um usuário pelo ID informado. Necessário Role USER")
    @ApiResponse(responseCode = "200", description = "Chamados encontrados")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosPeloCriadorId (
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Status do chamado", example= "ABERTO")
            @RequestParam(required = false) String status,
            @PathVariable UUID id ){

        return ResponseEntity.ok(service.listarChamadosPeloCriadorId(page, size, id, status));

    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @GetMapping("/tecnico/{id}")
    @Operation(summary = "Retornar todos os chamados tratado por um ténico pelo ID informado. Necessário Role TECNICO ou ADMIN")
    @ApiResponse(responseCode = "200", description = "Chamados encontrados")
    public ResponseEntity<Page<ChamadoResponseDto>> listarChamadosPeloTecnicoId (
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Status do chamado", example= "EM_TRATATIVA")
            @RequestParam(required = false) String status,
            @PathVariable UUID id ){

        return ResponseEntity.ok(service.listarChamadosPeloTecnicoId(page, size, id, status));

    }

    @PreAuthorize("hasRole('TECNICO')")
    @PatchMapping(path = "/{chamadoId}/assumir")
    @Operation(summary = "Assumir a tratativa do chamado. Necessário Role TECNICO")
    @ApiResponse(responseCode = "200", description = "Chamado assumido com sucesso")
    public ResponseEntity<ChamadoResponseDto> assumirChamado (@PathVariable UUID chamadoId, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        return ResponseEntity.ok(service.assumirChamado(chamadoId, auth.getId(), auth.getNome()));

    }

    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @PatchMapping(path = "/{chamadoId}/concluir")
    @Operation(summary = "Concluir a tratativa do chamado. Necessário Role TECNICO ou ADMIN")
    @ApiResponse(responseCode = "200", description = "Chamado concluído com sucesso")
    public ResponseEntity<Void> concluirChamado (@PathVariable UUID chamadoId, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
        service.concluirChamado(chamadoId, auth.getId());
        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um chamado pelo ID informado. Necessário Role USER e o chamado está com status ABERTO")
    @ApiResponse(responseCode = "204", description = "Chamado removido com sucesso")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, Authentication authentication){
        CustomJwtAuthentication auth = (CustomJwtAuthentication) authentication;
         service.deletar(id, auth.getId());
         return ResponseEntity.noContent().build();
     }
}
