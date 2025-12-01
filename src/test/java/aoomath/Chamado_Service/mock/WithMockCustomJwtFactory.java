package aoomath.Chamado_Service.mock;

import aoomath.Chamado_Service.security.CustomJwtAuthentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithMockCustomJwtFactory implements WithSecurityContextFactory<WithMockCustomJwt> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomJwt customJwt) {

        // 1. Criar as Authorities (Roles)
        List<SimpleGrantedAuthority> authorities = Arrays.stream(customJwt.roles())
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 2. Criar o objeto JWT com as Claims
        Jwt jwt = Jwt.withTokenValue("mock-jwt-token-valido")
                .header("alg", "RS256")
                .claim("sub", customJwt.userId())
                .claim("id", customJwt.userId())
                .claim("nome", customJwt.username())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // 3. Criar a instância da sua classe CustomJwtAuthentication
        CustomJwtAuthentication token = new CustomJwtAuthentication(jwt, authorities);

        // 4. Injetar o Token no SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);

        return context;
    }
}
