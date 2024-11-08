package com.example.jpa_dynamic_query.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseErrorEx {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private String message;
}
