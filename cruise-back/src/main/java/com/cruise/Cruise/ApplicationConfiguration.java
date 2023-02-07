package com.cruise.Cruise;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
//@EnableWebMvc
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOriginPatterns("*").allowedMethods("*");
//        registry.addMapping("/api/**").allowedOrigins("http://localhost:4200/").allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE");
    }

}
