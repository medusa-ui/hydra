package com.sample.hydra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource postgresDatasource(@Value("${postgres.jdbcurl}") String jdbcUrl,
                                         @Value("${postgres.username}") String username,
                                         @Value("${postgres.password}") String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        return new HikariDataSource(config);
    }

}
