package com.example.jpa_dynamic_query.mapper;

import com.example.jpa_dynamic_query.dto.request.CarRequestDTO;
import com.example.jpa_dynamic_query.dto.response.CarResponseDTO;
import com.example.jpa_dynamic_query.entity.Car;

public interface CarMapper {
    Car toCarEntity(CarRequestDTO carRequestDTO);
    CarResponseDTO toCarResponseDTO(Car car);
    void updateCar(Car oldCar, CarRequestDTO carRequestDTO);
}
