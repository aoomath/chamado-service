package aoomath.Chamado_Service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
@Configuration
public class JwtConfig {

    private static final String PUBLIC_KEY_BASE64  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjooz3yr729DoJkEDlrCQJpVQwVUqta+o9lE8Yyw+KmurqWIeIoU2plcX+4Vm5iLuOP3nqvdmJKwfLTP/4f2ReE5BORR4HsGUm+6jRH+DwHM4EROGS9MvQ6sojx2t3hcT3LSERw0E/VH97thhuva8ez/Baap388x9kQjvpYl1DNhGKyGOrCDjTwM+jreeOTBxcsjMDBeA6R8LP9HI+9hg6hdkzZIbM4mOfjXN2bmJF34i6cKYNPF1cImPjVXHgEmsax6+yqkUP3a8hndIpx3LNDuRgKwzSUpqJ40etxrfh36MdfKd7VWXPFOIklBDOzuu6Ms55R/+1zcbhPrgMGpRywIDAQAB";

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        byte[] decoded = Base64.getDecoder().decode(PUBLIC_KEY_BASE64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}
