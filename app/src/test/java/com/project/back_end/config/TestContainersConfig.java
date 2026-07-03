package com.project.back_end.config;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;

public class TestContainersConfig {

    public static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("cms")
                    .withUsername("root")
                    .withPassword("password");

    public static final MongoDBContainer MONGO =
            new MongoDBContainer("mongo:7.0");

    static {
        MYSQL.start();
        MONGO.start();
    }

    private TestContainersConfig() {}
}
