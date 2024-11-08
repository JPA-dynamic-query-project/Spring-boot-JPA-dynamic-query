package com.example.jpa_dynamic_query.response;

public class ErrorResponse extends DataResponse{
    public ErrorResponse(int status, String message) {
        super(status, message);
    }
}
