package com.example.myservice.DTO;

import com.example.myservice.Data.CountData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountDataRequest {
    private String sensor;
    private Long value;

    public CountData toEntity(Long val){
        return CountData.builder()
                .sensor("Ultrasonic sensor")
                .value(val)
                .build();
    }
}
