package com.example.myservice.DTO;

import com.example.myservice.Data.CountData;
import com.example.myservice.Data.PhotoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountDataRepository extends JpaRepository<CountData, Long> {

    @Override
    Optional<CountData> findById(Long id);

    @Query("SELECT u FROM CountData u WHERE u.id = (SELECT MAX(u2.id) FROM CountData u2)")
    CountData findFirstByIdOrderByIdDesc();
}
