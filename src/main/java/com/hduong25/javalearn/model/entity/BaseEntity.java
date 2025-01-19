package com.hduong25.javalearn.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: Alphaway
 */

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity<T> {
    private static final long serialVersionUID = 1L;

    @Column(name = "CREATED_BY", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "UPDATED_BY", nullable = false, length = 50, updatable = false)
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    public abstract T getId();

    @PrePersist
    public void prePersist() {
        if (StringUtils.isBlank(createdBy)) {
            this.setCreatedBy("ADMIN");
            this.setUpdatedBy("ADMIN");
        }

        if (this.createdDate == null) {
            this.setCreatedDate(LocalDateTime.now());
            this.setUpdatedDate(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.setUpdatedBy("ADMIN UPDATE");
        this.setUpdatedDate(LocalDateTime.now());
    }
}
