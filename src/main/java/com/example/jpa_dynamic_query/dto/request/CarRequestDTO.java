package com.example.jpa_dynamic_query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class CarRequestDTO {
    private Integer id;
    private String name;
    private String brand;
    private String color;
    private Date dateBooking;
//    private String productionTime;
}
