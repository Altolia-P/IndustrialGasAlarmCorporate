package com.niit.industrialgasalarmcorporate.domain.comment;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {

    private final String commentUuid;
    private final CommentTargetType targetType;
    private final String targetUuid;
    private final CommentAuthorType authorType;
    private final String authorUuid;
    private final String authorName;
    private final String content;
    private final LocalDateTime createdAt;

    public Comment(CommentTargetType targetType, String targetUuid,
                   CommentAuthorType authorType, String authorUuid, String authorName,
                   String content) {
        this.commentUuid = UUID.randomUUID().toString();
        this.targetType = targetType;
        this.targetUuid = targetUuid;
        this.authorType = authorType;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Comment(String commentUuid, CommentTargetType targetType, String targetUuid,
                   CommentAuthorType authorType, String authorUuid, String authorName,
                   String content, LocalDateTime createdAt) {
        this.commentUuid = commentUuid;
        this.targetType = targetType;
        this.targetUuid = targetUuid;
        this.authorType = authorType;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getCommentUuid() { return commentUuid; }
    public CommentTargetType getTargetType() { return targetType; }
    public String getTargetUuid() { return targetUuid; }
    public CommentAuthorType getAuthorType() { return authorType; }
    public String getAuthorUuid() { return authorUuid; }
    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
