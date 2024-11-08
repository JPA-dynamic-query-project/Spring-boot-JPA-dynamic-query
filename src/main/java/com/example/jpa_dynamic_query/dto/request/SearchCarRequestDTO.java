package com.example.jpa_dynamic_query.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class SearchCarRequestDTO {
    private String name;
    private String brand;
    private String color;
    private Date startDate;
    private Date endDate;
    private int pageNo = 1;
    private int pageSize = 10;
    private String field = "name";
    private String direction = "asc";
}
