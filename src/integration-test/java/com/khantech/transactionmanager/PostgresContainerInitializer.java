package com.khantech.transactionmanager;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;

public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    static {
        Startables.deepStart(postgres).join();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        MapPropertySource testcontainers = new MapPropertySource("testcontainers", Map.of(
            "spring.datasource.url", postgres.getJdbcUrl(),
            "spring.datasource.username", postgres.getUsername(),
            "spring.datasource.password", postgres.getPassword(),
            "spring.datasource.driver-class-name", "org.postgresql.Driver"
        ));

        environment.getPropertySources().addFirst(testcontainers);
    }
}
