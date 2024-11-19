package com.badri.dto;

import com.badri.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PaginationRequestDto<T> {

    private List<T> users;
    private int page;
    private int size;
    private int totalPages;
    private Long totalElements;
    private boolean isLast;

}
