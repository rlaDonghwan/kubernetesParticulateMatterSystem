package inhatc.k8sProject.particulateMatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class kubernetesParticulateMatterSystemApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() {
		try {
			assertNotNull(dataSource);
			System.out.println("Datasource is not null");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
