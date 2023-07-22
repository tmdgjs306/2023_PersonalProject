package com.example.myservice.DTO;

import com.example.myservice.Data.TemperatureData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemperatureDataRequest {
    private String sensor;
    private Double value;

    public TemperatureData toEntity(Double val){
        return TemperatureData.builder()
                .sensor("Temperature Sensor")
                .value(val)
                .build();
    }
}
