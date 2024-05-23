package inhatc.k8sProject.fineDust.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "inhatc.k8sProject.fineDust.repository.gyeongsang",
        entityManagerFactoryRef = "gyeongsangEntityManagerFactory",
        transactionManagerRef = "gyeongsangTransactionManager"
)
public class GyeongsangDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.gyeongsang.datasource")
    public DataSource gyeongsangDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gyeongsangEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gyeongsangEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gyeongsangDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("inhatc.k8sProject.fineDust.domain.gyeongsang")
                .persistenceUnit("gyeongsang")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "gyeongsangTransactionManager")
    public PlatformTransactionManager gyeongsangTransactionManager(
            @Qualifier("gyeongsangEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
        return properties;
    }
}
