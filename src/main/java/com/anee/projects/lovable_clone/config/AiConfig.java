package com.anee.projects.lovable_clone.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for integrating Spring AI with OpenAI API.
 *
 * This class sets up the necessary beans and configurations for using OpenAI's chat models
 * through Spring AI. It provides a pre-configured {@link ChatClient} bean with logging
 * capabilities via the {@link SimpleLoggerAdvisor}.
 *
 * Configuration properties required:
 * - {@code spring.ai.openai.api-key}: OpenAI API key for authentication
 * - {@code spring.ai.openai.base-url}: Base URL for the OpenAI API endpoint
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

        /**
     * Creates and configures a {@link ChatClient} bean for AI chat operations.
     *
     * This method builds a ChatClient instance with logging capabilities enabled through
     * the {@link SimpleLoggerAdvisor}. The advisor logs all chat interactions for debugging
     * and monitoring purposes.
     *
     * @param builder the {@link ChatClient.Builder} used to construct the ChatClient instance
     * @return a configured {@link ChatClient} bean ready for chat operations with OpenAI
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
