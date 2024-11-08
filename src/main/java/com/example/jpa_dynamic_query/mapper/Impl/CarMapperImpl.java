package com.example.jpa_dynamic_query.mapper.Impl;

import com.example.jpa_dynamic_query.dto.request.CarRequestDTO;
import com.example.jpa_dynamic_query.dto.response.CarResponseDTO;
import com.example.jpa_dynamic_query.entity.Car;
import com.example.jpa_dynamic_query.mapper.CarMapper;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class CarMapperImpl implements CarMapper {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public Car toCarEntity(CarRequestDTO carRequestDTO) {
        return Car.builder()
                .name(carRequestDTO.getName())
                .brand(carRequestDTO.getBrand())
                .color(carRequestDTO.getColor())
                .dateBooking(carRequestDTO.getDateBooking())
               // .productionTime(dateFormat.parse(carRequestDTO.getProductionTime()))
                .build();
    }

    @Override
    public CarResponseDTO toCarResponseDTO(Car car) {
        return CarResponseDTO.builder()
                .id(car.getId())
                .name(car.getName())
                .brand(car.getBrand())
                .color(car.getColor())
                .dateBooking(dateFormat.format(car.getDateBooking()))
                //.productionTime(dateFormat.format(car.getProductionTime()))
                .build();
    }

    @Override
    public void updateCar(Car oldCar, CarRequestDTO carRequestDTO) {
        oldCar.setName(carRequestDTO.getName());
        oldCar.setBrand(carRequestDTO.getBrand());
        oldCar.setColor(carRequestDTO.getColor());
        oldCar.setDateBooking(carRequestDTO.getDateBooking());
    }


}
