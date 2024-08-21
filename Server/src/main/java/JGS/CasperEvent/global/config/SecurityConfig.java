package JGS.CasperEvent.global.config;

import JGS.CasperEvent.global.util.AESUtils;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.Key;

@Configuration
public class SecurityConfig {

    @Value("${spring.encryption.key}")
    private String encryptionKey;

    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    @Bean
    public SecretKey secretKey()  {
        return AESUtils.stringToKey(encryptionKey);
    }

    @Bean
    public Key jwtKey(){
        byte[] secret = secretKey.getBytes();
        return Keys.hmacShaKeyFor(secret);
    }
}
