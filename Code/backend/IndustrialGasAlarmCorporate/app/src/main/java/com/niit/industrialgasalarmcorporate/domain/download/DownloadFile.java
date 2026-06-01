package com.niit.industrialgasalarmcorporate.domain.download;

import java.time.LocalDateTime;
import java.util.UUID;

public class DownloadFile {

    private final String downloadUuid;
    private String displayName;
    private String originalName;
    private long fileSize;
    private String contentType;
    private String storedPath;
    private final LocalDateTime createdAt;

    public DownloadFile(String displayName, String originalName, long fileSize,
                        String contentType, String storedPath) {
        this.downloadUuid = UUID.randomUUID().toString();
        this.displayName = displayName;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storedPath = storedPath;
        this.createdAt = LocalDateTime.now();
    }

    public DownloadFile(String downloadUuid, String displayName, String originalName,
                        long fileSize, String contentType, String storedPath,
                        LocalDateTime createdAt) {
        this.downloadUuid = downloadUuid;
        this.displayName = displayName;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storedPath = storedPath;
        this.createdAt = createdAt;
    }

    public String getDownloadUuid() { return downloadUuid; }
    public String getDisplayName() { return displayName; }
    public String getOriginalName() { return originalName; }
    public long getFileSize() { return fileSize; }
    public String getContentType() { return contentType; }
    public String getStoredPath() { return storedPath; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateDisplayName(String displayName) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
    }
}
