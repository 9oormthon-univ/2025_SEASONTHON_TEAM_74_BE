//package com.example.demo.common.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    public WebClient pythonAiWebClient(@Value("${python.ai.base-url}") String baseUrl) {
//        HttpClient httpClient = HttpClient.create()
//                .responseTimeout(Duration.ofMinutes(15))
//                .keepAlive(true);
//        ExchangeStrategies strategies = ExchangeStrategies.builder()
//                .codecs(c -> c.defaultCodecs().maxInMemorySize(1 * 1024 * 1024))
//                .build();
//        return WebClient.builder()
//                .baseUrl(baseUrl)
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .exchangeStrategies(strategies)
//                .defaultHeader(HttpHeaders.ACCEPT, "video/mp4,application/octet-stream")
//                .build();
//    }
//}
