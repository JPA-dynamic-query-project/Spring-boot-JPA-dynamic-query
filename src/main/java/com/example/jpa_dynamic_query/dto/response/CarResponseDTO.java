package com.example.jpa_dynamic_query.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class CarResponseDTO {
    private Integer id;
    private String name;
    private String brand;
    private String color;
    private String dateBooking;


    public CarResponseDTO(Integer id, String name, String brand, String color) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.color = color;
    }

    public CarResponseDTO(Integer id, String name, String brand, String color, String dateBooking) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.dateBooking = dateBooking;
    }

    public CarResponseDTO() {
    }
}
