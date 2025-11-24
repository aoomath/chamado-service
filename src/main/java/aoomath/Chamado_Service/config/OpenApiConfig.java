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
                Serviço responsável pelo gerenciamento de chamados no sistema.
            
                Segurança:
                - Todos os endpoints exigem autenticação via token JWT.
                - A autenticação é aplicada globalmente pelo SecurityConfig.
                - O token JWT deve ser obtido no Usuario Service.
                - As anotações @PreAuthorize são utilizadas apenas para definir permissões específicas
                  (roles) por endpoint, como USER, TECNICO e ADMIN.
            
                Regras gerais de negócio:
                1. Qualquer usuário autenticado pode visualizar seus próprios chamados.
                2. Somente usuários com role USER podem criar e excluir chamados.
                3. Somente técnicos podem assumir chamados e registrar comentários.
                4. Técnicos e administradores podem listar chamados e consultar dados de outros usuários.
                """)
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
