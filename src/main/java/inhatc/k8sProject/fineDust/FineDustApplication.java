package inhatc.k8sProject.fineDust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FineDustApplication {

	public static void main(String[] args) {
		SpringApplication.run(FineDustApplication.class, args);
	}

}
