package com.example.myservice.Controller;

import com.example.myservice.DTO.*;
import com.example.myservice.Data.CountData;
import com.example.myservice.Data.PhotoData;
import com.example.myservice.Data.TemperatureData;
import com.example.myservice.DTO.getWeather;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class GetDataController {

    private final TemperatureDataRepository temperatureDataRepository;
    private final PhotoDataRepository photoDataRepository;
    private final CountDataRepository countDataRepository;
    JSONParser parser = new JSONParser();
    @PostMapping("/addData")
    public void postTemp(@RequestBody String req) throws ParseException {
        JSONObject jsonObject = (JSONObject) parser.parse(req);
        Double tempValue = (Double) jsonObject.get("temp");
        Long photoValue = (Long) jsonObject.get("photo");
        Long countValue = (Long) jsonObject.get("count");
        temperatureDataRepository.save(new TemperatureDataRequest().toEntity(tempValue));
        photoDataRepository.save(new PhotoDataRequest().toEntity(photoValue));
        countDataRepository.save(new CountDataRequest().toEntity(countValue));
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
    @GetMapping("/latestCountData")
    public CountData getLatestCountData(){
        CountData countData = countDataRepository.findFirstByIdOrderByIdDesc();
        return countData;
    }

    @GetMapping("/getWeather")
    public String getWeatherData() throws IOException {
        getWeather a1 = new getWeather();
        String result = a1.get();
        System.out.println(result);
        return result;
    }
}
