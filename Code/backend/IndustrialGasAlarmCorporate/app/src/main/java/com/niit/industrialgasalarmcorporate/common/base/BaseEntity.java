package com.niit.industrialgasalarmcorporate.common.base;

import java.time.LocalDateTime;

/**
 * Abstract base for entities that carry audit timestamps.
 * POs extend this to inherit createdAt / updatedAt.
 * Aggregate roots may optionally extend it.
 */
public abstract class BaseEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
