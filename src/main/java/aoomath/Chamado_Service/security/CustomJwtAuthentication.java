package aoomath.Chamado_Service.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class CustomJwtAuthentication extends AbstractAuthenticationToken {
    @Getter
    private final String id;
    @Getter
    private final String nome;
    private final String username;
    private final Jwt jwt;


    public CustomJwtAuthentication(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.username = jwt.getSubject();
        this.id = jwt.getClaim("id");
        this.nome = jwt.getClaim("nome");
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return jwt.getTokenValue();
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    public boolean hasRole(String roleName) {
        String expected = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(expected));
    }

}
