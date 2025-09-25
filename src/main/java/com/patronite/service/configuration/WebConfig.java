package com.patronite.service.configuration;

import com.patronite.service.field.Field;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;

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
        registry.addMapping("/npc/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "PUT");
        registry.addMapping("/item/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "PUT");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login")
                .setViewName("forward:/login.html");
        Arrays.stream(Field.values())
                .map(field -> field.name().toLowerCase())
                .forEach(fieldName -> registry.addViewController(String.format("/field/%s", fieldName))
                        .setViewName(String.format("forward:/field/%s.html", fieldName)));
        registry.addViewController("/battle")
                .setViewName("forward:/battle.html");
        registry.addViewController("/error/404")
                .setViewName("forward:/error/404.html");
        registry.addViewController("/error/uh-oh")
                .setViewName("forward:/error/uh-oh.html");
    }
}
