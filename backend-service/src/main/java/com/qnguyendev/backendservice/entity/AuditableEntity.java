package com.qnguyendev.backendservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;

@MappedSuperclass
public abstract class AuditableEntity<T extends Serializable> extends BaseEntity<T> {

    @CreatedBy
    @Column(name = "created_by")
    private T createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private T updatedBy;
}
