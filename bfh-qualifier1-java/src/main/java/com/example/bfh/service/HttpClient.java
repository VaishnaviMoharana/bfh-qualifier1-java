package com.example.bfh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class HttpClient {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    private final WebClient webClient;

    public HttpClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public <T> Mono<T> postJson(String url, Object body, Class<T> responseType) {
        log.debug("POST {}", url);
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> postJsonWithAuth(String url, String token, Object body, Class<T> responseType) {
        log.debug("POST {} (authorized)", url);
        return webClient.post()
                .uri(url)
                .headers(h -> h.add("Authorization", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType);
    }
}
