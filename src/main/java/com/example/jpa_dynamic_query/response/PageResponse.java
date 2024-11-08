package com.example.jpa_dynamic_query.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageResponse<T> {
//    private int status;
//    private String message;
    private int pageNo;
    private int pageSize;
    private long totalPages;
    private long totalElements;
    private T items;

    public PageResponse(int pageNo, int pageSize, long totalPages, long totalElements, T items) {
//        this.status = status;
//        this.message = message;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.items = items;
    }


}
