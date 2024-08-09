package JGS.CasperEvent.global.config;

import JGS.CasperEvent.global.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfig {

    @Value("${spring.encryption.key}")
    private String encryptionKey;

    @Bean
    public SecretKey secretKey()  {
        return AESUtils.stringToKey(encryptionKey);
    }
}
