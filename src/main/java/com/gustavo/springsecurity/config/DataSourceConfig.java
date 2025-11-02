package com.gustavo.springsecurity.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile(value = "dev")
@Configuration
public class DataSourceConfig {

    private final Dotenv dotenv = Dotenv.load();

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dotenv.get("SPRING_DATASOURCE_URL"));
        dataSource.setUsername(dotenv.get("SPRING_DATASOURCE_USERNAME"));
        dataSource.setPassword(dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        return dataSource;
    }
}