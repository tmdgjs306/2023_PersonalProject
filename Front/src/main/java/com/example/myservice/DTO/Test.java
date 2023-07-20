package com.example.myservice.DTO;

import com.example.myservice.Data.TemperatureData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class Test {
    WebClient client = WebClient.create("http:165.246.116.83:80");

    Mono<ResponseEntity<TemperatureData>> result = client.get()
            .uri("/getTemp")
            .retrieve()
            .toEntity(TemperatureData.class);
}

