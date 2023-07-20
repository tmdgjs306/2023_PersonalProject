package com.example.myservice.DTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class DataService {
    private final DataRepository dataRepository;


}
