package com.anee.projects.lovable_clone.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel() {
        // 1. Build a custom WebClient that injects OpenRouter's mandatory headers
        WebClient.Builder webClientBuilder = WebClient.builder()
                .defaultHeader("HTTP-Referer", "http://localhost:8080") // Matches your client.url
                .defaultHeader("X-Title", "lovable-clone");             // Matches your application name

        // 2. Initialize OpenAiApi with the custom base URL and key
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .webClientBuilder(webClientBuilder)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("qwen/qwen3-coder:free")
                .temperature(0.0)
                .build();

        // 3. Return the ChatModel bean which Spring AI will use to build your ChatClient
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }
}
