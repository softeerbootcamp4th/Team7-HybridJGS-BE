package JGS.CasperEvent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class CasperEventApplication {

	public static void main(String[] args) {
		SpringApplication.run(CasperEventApplication.class, args);
	}

}
