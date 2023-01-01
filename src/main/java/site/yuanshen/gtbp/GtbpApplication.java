package site.yuanshen.gtbp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
public class GtbpApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtbpApplication.class, args);
	}

}
