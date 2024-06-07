package inhatc.k8sProject.particulateMatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class kubernetesParticulateMatterSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(kubernetesParticulateMatterSystemApplication.class, args);
	}

}
