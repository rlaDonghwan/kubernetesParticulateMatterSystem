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
        basePackages = "inhatc.k8sProject.fineDust.repository.jeolla",
        entityManagerFactoryRef = "jeollaEntityManagerFactory",
        transactionManagerRef = "jeollaTransactionManager"
)
public class JeollaDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.jeolla.datasource")
    public DataSource jeollaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jeollaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jeollaEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("jeollaDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("inhatc.k8sProject.fineDust.domain.jeolla")
                .persistenceUnit("jeolla")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "jeollaTransactionManager")
    public PlatformTransactionManager jeollaTransactionManager(
            @Qualifier("jeollaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
        return properties;
    }
}
