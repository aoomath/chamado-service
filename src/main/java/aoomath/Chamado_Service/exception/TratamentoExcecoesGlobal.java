package aoomath.Chamado_Service.exception;

import aoomath.Chamado_Service.exception.dto.ErroRespostaDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class TratamentoExcecoesGlobal {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRequisicaoInvalida(RequisicaoInvalidaException ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> erros = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> "erro: " + e.getDefaultMessage())
                .collect(Collectors.toList());

        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Campos inválidos na requisição",
                request.getRequestURI(),
                erros
        );

        return ResponseEntity.badRequest().body(dto);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAcessoNegado(AcessoNegadoException ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErroRespostaDTO> tratarAccessDenied(Exception ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                403,
                "Acesso negado",
                "Você não tem permissão para acessar este recurso",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(403).body(dto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Não autenticado",
                "Token inválido ou ausente",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }

    // Outras exceções genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarOutrosErros(Exception ex, HttpServletRequest request) {
        ErroRespostaDTO dto = new ErroRespostaDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

}
