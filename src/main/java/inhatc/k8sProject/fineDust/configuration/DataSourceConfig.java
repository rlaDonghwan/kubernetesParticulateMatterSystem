package inhatc.k8sProject.fineDust.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "gyeonggiDataSource")
    public DataSource gyeonggiDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/gyeonggi?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }

    @Bean(name = "gangwonDataSource")
    public DataSource gangwonDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3307/gangwon?useUnicode=true&characterEncoding=UTF&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }

    @Bean(name = "chungcheongDataSource")
    public DataSource chungcheongDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3308/chungcheong?useUnicode=true&characterEncoding=UTF&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }

    @Bean(name = "gyeongsangDataSource")
    public DataSource gyeongsangDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3309/gyeongsang?useUnicode=true&characterEncoding=UTF&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }

    @Bean(name = "jeollaDataSource")
    public DataSource jeollaDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3310/jeolla?useUnicode=true&characterEncoding=UTF&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }

    @Bean(name = "jejuDataSource")
    public DataSource jejuDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3311/jeju?useUnicode=true&characterEncoding=UTF&serverTimezone=UTC")
                .username("root")
                .password("password")
                .build();
    }
}
