package aoomath.Chamado_Service.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockCustomJwtFactory.class)
public @interface WithMockCustomJwt {

    String userId() default "mock-user-id";

    String username() default "Mock User";

    String[] roles() default {"USER"};
}
