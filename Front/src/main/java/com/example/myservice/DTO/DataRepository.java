package com.example.myservice.DTO;

import com.example.myservice.Data.TemperatureData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DataRepository extends JpaRepository<TemperatureData, Long> {
    Optional<TemperatureData> findById(Long id);

    @Query("SELECT u FROM TemperatureData u WHERE u.id = (SELECT MAX(u2.id) FROM TemperatureData u2)")
    TemperatureData findFirstByIdOrderByIdDesc();
}
