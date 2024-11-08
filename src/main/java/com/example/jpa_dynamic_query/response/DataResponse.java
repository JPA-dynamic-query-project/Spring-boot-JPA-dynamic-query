package com.example.jpa_dynamic_query.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataResponse<T>{
    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public DataResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public DataResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
