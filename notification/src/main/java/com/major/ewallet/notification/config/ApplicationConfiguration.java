package com.major.ewallet.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
