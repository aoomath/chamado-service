package aoomath.Chamado_Service.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Chamado Service", version = "v1",
                description = """
        Serviço responsável pelo gerenciamento de chamados no sistema.<br><br>

        <b>Segurança:</b><br>
        - Todos os endpoints exigem autenticação via token JWT.<br>
        - A autenticação é aplicada globalmente pelo SecurityConfig.<br>
        - O token JWT deve ser obtido no Usuario Service.<br>
        - As anotações @PreAuthorize são utilizadas apenas para definir permissões específicas
          (roles) por endpoint, como USER, TECNICO e ADMIN.<br><br>

        <b>Papéis de Acesso (Roles)</b><br><br>

        <b>USER</b><br>
        - Cria chamados.<br>
        - Exclui seus próprios chamados se estiverem ABERTOS.<br>
        - Lista e consulta apenas seus chamados.<br><br>

        <b>TECNICO</b><br><br>
        <u>Chamados:</u><br>
        - Lista todos os chamados.<br>
        - Assume a tratativa.<br>
        - Conclui chamados.<br>
        - Lista chamados que assumiu.<br>
        - Consulta chamados de qualquer usuário.<br><br>

        <u>Comentários:</u><br>
        - Cria comentários em chamados.<br>
        - Lista e exclui seus próprios comentários.<br><br>

        <b>ADMIN</b><br>
        - Lista todos os chamados.<br>
        - Conclui chamados.<br>
        - Consulta chamados por criador ou técnico.<br>
        - Lista comentários de qualquer técnico.<br><br>

        <b>Observação:</b><br><br>
        ADMIN não cria, assume, comenta ou exclui chamados. Seu papel é supervisão e auditoria.
        """
        )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

}
