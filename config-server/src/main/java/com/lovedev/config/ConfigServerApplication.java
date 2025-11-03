package com.lovedev.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server for Centralized Configuration Management
 * 
 * This service provides centralized configuration management for all microservices.
 * It serves configuration files from the local file system or Git repository.
 * 
 * @author LoveDev Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
        System.out.println("""
            
            =====================================
            🔧 Config Server Started Successfully
            =====================================
            Port: 8888
            Status: RUNNING
            Config Location: config-repo/
            =====================================
            """);
    }
}
