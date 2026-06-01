package com.niit.industrialgasalarmcorporate.domain.comment;

import java.util.List;

public interface CommentRepository {

    void save(Comment comment);

    List<Comment> findByTarget(CommentTargetType targetType, String targetUuid);
}
