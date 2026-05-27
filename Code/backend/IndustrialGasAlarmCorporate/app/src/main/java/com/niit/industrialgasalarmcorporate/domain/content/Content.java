package com.niit.industrialgasalarmcorporate.domain.content;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Content {

    private final String contentUuid;
    private String title;
    private String summary;
    private String body;
    private String coverImage;
    private ContentType type;
    private ContentStatus status;
    private String categoryUuid;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Content(String title, String summary, String body, String coverImage,
                   ContentType type, ContentStatus status, String categoryUuid) {
        this.contentUuid = UUID.randomUUID().toString();
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.coverImage = coverImage;
        this.type = type;
        this.status = status;
        this.categoryUuid = categoryUuid;
    }

    public Content(String contentUuid, String title, String summary, String body,
                   String coverImage, ContentType type, ContentStatus status, String categoryUuid,
                   String categoryName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.contentUuid = contentUuid;
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.coverImage = coverImage;
        this.type = type;
        this.status = status;
        this.categoryUuid = categoryUuid;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void publish() {
        if (this.status == ContentStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "内容已是发布状态");
        }
        this.status = ContentStatus.PUBLISHED;
    }

    public void unpublish() {
        if (this.status != ContentStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅已发布的内容可以取消发布");
        }
        this.status = ContentStatus.DRAFT;
    }

    public void update(String title, String summary, String body, String coverImage,
                       String categoryUuid) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.coverImage = coverImage;
        this.categoryUuid = categoryUuid;
    }

    public String getContentUuid() {
        return contentUuid;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public ContentType getType() {
        return type;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }
}
