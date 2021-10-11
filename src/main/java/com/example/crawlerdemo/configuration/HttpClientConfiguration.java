package com.example.crawlerdemo.configuration;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfiguration {
    @Value("${http.client.max-total-connections}")
    private int maxTotalConnections;

    @Value("${http.client.default-max-alive-time-sec}")
    private int defaultKeepAliveTime;

    @Value("${http.client.connection-timeout-sec}")
    private int connectionTimeout;

    @Value("${http.client.socket-timeout-sec}")
    private int socketTimeout;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(socketTimeout))
                .retryOnConnectionFailure(true)
                .connectTimeout(Duration.ofSeconds(connectionTimeout))
                .connectionPool(new ConnectionPool(maxTotalConnections, defaultKeepAliveTime, TimeUnit.SECONDS))
                .build();
    }
}
