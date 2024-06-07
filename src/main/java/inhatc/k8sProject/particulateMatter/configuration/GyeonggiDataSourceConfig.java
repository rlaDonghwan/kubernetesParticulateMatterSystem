package inhatc.k8sProject.particulateMatter.configuration;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "inhatc.k8sProject.particulateMatter.repository.gyeonggi", // 경기도 리포지토리의 기본 패키지
        entityManagerFactoryRef = "gyeonggiEntityManagerFactory", // 엔터티 매니저 팩토리 참조
        transactionManagerRef = "gyeonggiTransactionManager" // 트랜잭션 매니저 참조
)
public class GyeonggiDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.gyeonggi.datasource") // spring.datasource가 아닌 spring.gyeonggi.datasource로 변경
    public DataSource gyeonggiDataSource() {
        return DataSourceBuilder.create().build(); // 경기도용 데이터 소스 빌드 및 생성
    }

    @Bean(name = "gyeonggiEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean gyeonggiEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gyeonggiDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("inhatc.k8sProject.particulateMatter.domain.gyeonggi") // 경기도 도메인 패키지 지정
                .persistenceUnit("gyeonggi") // 영속성 유닛 이름 설정
                .properties(hibernateProperties()) // 하이버네이트 설정 속성 지정
                .build();
    }

    @Bean(name = "gyeonggiTransactionManager")
    @Primary
    public PlatformTransactionManager gyeonggiTransactionManager(
            @Qualifier("gyeonggiEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory); // 경기도용 트랜잭션 매니저 생성
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return properties;
    }
}
