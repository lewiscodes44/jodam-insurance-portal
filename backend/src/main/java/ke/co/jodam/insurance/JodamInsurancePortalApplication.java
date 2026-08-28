package ke.co.jodam.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JodamInsurancePortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				JodamInsurancePortalApplication.class,
				args
		);
	}
}