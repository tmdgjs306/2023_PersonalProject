package com.example.myservice.DTO;

import com.example.myservice.Data.PhotoData;
import com.example.myservice.Data.TemperatureData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhotoDataRequest {
    private String sensor;
    private Long value;

    public PhotoData toEntity(Long val){
        return PhotoData.builder()
                .sensor("PhotoresistorSensor")
                .value(val)
                .build();
    }
}
