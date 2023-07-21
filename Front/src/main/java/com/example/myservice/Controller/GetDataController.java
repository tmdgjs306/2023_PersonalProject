package com.example.myservice.Controller;

import com.example.myservice.DTO.*;
import com.example.myservice.Data.PhotoData;
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

    private final TemperatureDataRepository temperatureDataRepository;
    private final PhotoDataRepository photoDataRepository;

    JSONParser parser = new JSONParser();
    @PostMapping("/addData")
    public void postTemp(@RequestBody String req) throws ParseException {
        JSONObject jsonObject = (JSONObject) parser.parse(req);
        Double tempValue = (Double) jsonObject.get("temp");
        Long photoValue = (Long) jsonObject.get("photo");
        temperatureDataRepository.save(new TemperatureDataRequest().toEntity(tempValue));
        photoDataRepository.save(new PhotoDataRequest().toEntity(photoValue));
    }
    @PostMapping("/count")
    public void postCount(@RequestBody String req) throws ParseException {
        JSONObject jsonObject = (JSONObject) parser.parse(req);
        Double value = (Double) jsonObject.get("temp");
    }
    @GetMapping("/latestTemperatureData")
    public TemperatureData getLatestTemperatureData() {
        TemperatureData temperatureData = temperatureDataRepository.findFirstByIdOrderByIdDesc();
        return temperatureData;
    }

    @GetMapping("/latestPhotoData")
    public PhotoData getLatestPhotoData(){
        PhotoData photoData = photoDataRepository.findFirstByIdOrderByIdDesc();
        return photoData;
    }
}
