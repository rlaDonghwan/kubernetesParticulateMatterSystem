package inhatc.k8sProject.fineDust.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "inhatc.k8sProject.fineDust.repository.gangwon",
        entityManagerFactoryRef = "gangwonEntityManagerFactory",
        transactionManagerRef = "gangwonTransactionManager"
)
public class GangwonDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.gangwon.datasource")
    public DataSource gangwonDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gangwonEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gangwonEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gangwonDataSource") DataSource gangwonDataSource) {
        return builder
                .dataSource(gangwonDataSource)
                .packages("inhatc.k8sProject.fineDust.domain.gangwon")
                .persistenceUnit("gangwon")
                .properties(hibernateProperties())
                .build();
    }

    @Bean(name = "gangwonTransactionManager")
    public PlatformTransactionManager gangwonTransactionManager(
            @Qualifier("gangwonEntityManagerFactory") EntityManagerFactory gangwonEntityManagerFactory) {
        return new JpaTransactionManager(gangwonEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return properties;
    }
}


