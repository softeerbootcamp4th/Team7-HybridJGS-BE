package JGS.CasperEvent.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class S3Config {
    @Bean
    public AmazonS3 amazonS3(){
        return AmazonS3ClientBuilder.defaultClient();
    }
}