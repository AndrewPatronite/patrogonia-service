package com.patronite.service.configuration;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcAutoConfiguration implements WebMvcConfigurer {
    private final Environment environment;

    public WebConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = environment.getProperty("patrogonia-service.crossorigin", String[].class);
        registry.addMapping("/player/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT");
        registry.addMapping("/battle/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST");
    }
}
