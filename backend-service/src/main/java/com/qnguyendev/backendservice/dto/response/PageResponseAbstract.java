package com.qnguyendev.backendservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public abstract class PageResponseAbstract implements Serializable {
    int pageNumber;
    int pageSize;
    long totalPages;
    long totalElements;
}
