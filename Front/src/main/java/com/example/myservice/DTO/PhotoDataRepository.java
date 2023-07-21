package com.example.myservice.DTO;

import com.example.myservice.Data.PhotoData;
import com.example.myservice.Data.TemperatureData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PhotoDataRepository extends JpaRepository<PhotoData, Long> {
    @Override
    Optional<PhotoData> findById(Long id);

    @Query("SELECT u FROM PhotoData u WHERE u.id = (SELECT MAX(u2.id) FROM PhotoData u2)")
    PhotoData findFirstByIdOrderByIdDesc();
}
