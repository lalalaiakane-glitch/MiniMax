package com.rainforest.minimax.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(FalProperties.class)
public class AppConfig {

    /**
     * 專門用來呼叫 fal.ai 的 WebClient。
     * Authorization header 用 "Key <FAL_KEY>"，這是 fal.ai 的格式（不是 Bearer）。
     */
    @Bean
    public WebClient falWebClient(FalProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Key " + properties.getApiKey())
                .build();
    }
}
