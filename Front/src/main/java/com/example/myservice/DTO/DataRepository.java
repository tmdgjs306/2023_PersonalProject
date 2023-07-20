package com.example.myservice.DTO;

import com.example.myservice.Data.TemperatureData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataRepository extends JpaRepository<TemperatureData, Long> {
    Optional<TemperatureData> findById(Long id);
}
