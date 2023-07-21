package com.example.myservice.Controller;

import com.example.myservice.DTO.DataRepository;
import com.example.myservice.DTO.TemperatureDataRequest;
import com.example.myservice.DTO.UserService;
import com.example.myservice.Data.TemperatureData;
import com.example.myservice.Member.User;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class GetDataController {

    private final DataRepository dataRepository;
    private int id=1;
    JSONParser parser = new JSONParser();
    @PostMapping("/temp")
    public void postTemp(@RequestBody String req) throws ParseException {
        JSONObject jsonObject = (JSONObject) parser.parse(req);
        Double value = (Double) jsonObject.get("temp");
        dataRepository.save(new TemperatureDataRequest().toEntity(value));
    }
    @PostMapping("/count")
    public void postCount(@RequestBody String req) throws ParseException {
        JSONObject jsonObject = (JSONObject) parser.parse(req);
        Double value = (Double) jsonObject.get("temp");
        dataRepository.save(new TemperatureDataRequest().toEntity(value));
    }
    @GetMapping("/latestTemperatureData")
    public TemperatureData getLatestTemperatureData() {
        TemperatureData temperatureData = dataRepository.findFirstByIdOrderByIdDesc();
        return temperatureData;
    }
}
