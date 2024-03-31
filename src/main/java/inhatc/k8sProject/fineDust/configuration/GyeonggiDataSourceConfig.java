package inhatc.k8sProject.fineDust.configuration;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "inhatc.k8sProject.fineDust.repository.gyeonggi",
        entityManagerFactoryRef = "gyeonggiEntityManagerFactory",
        transactionManagerRef = "gyeonggiTransactionManager"
)
public class GyeonggiDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource gyeonggiDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gyeonggiEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean gyeonggiEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gyeonggiDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("inhatc.k8sProject.fineDust.domain.gyeonggi")
                .persistenceUnit("gyeonggi")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "gyeonggiTransactionManager")
    @Primary
    public PlatformTransactionManager gyeonggiTransactionManager(
            @Qualifier("gyeonggiEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update"); // Change to "none", "create", "create-drop" as needed
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect"); // Change to your DB dialect
        return properties;
    }
}
