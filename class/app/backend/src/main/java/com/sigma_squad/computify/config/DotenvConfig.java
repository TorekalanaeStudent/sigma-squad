package com.sigma_squad.computify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * DotenvConfig - Loads environment variables from .env file
 * The spring-dotenv library automatically loads the .env file
 */
@Configuration
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class DotenvConfig {
    // spring-dotenv auto-configures when the dependency is present
}
