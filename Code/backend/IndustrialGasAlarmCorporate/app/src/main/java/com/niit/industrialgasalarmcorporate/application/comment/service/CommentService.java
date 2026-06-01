package com.niit.industrialgasalarmcorporate.application.comment.service;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;

import java.util.List;

public interface CommentService {

    List<CommentVO> findByTarget(CommentTargetType targetType, String targetUuid);

    CommentVO addComment(CommentTargetType targetType, String targetUuid,
                         CommentAuthorType authorType, String authorUuid, String authorName,
                         CreateCommentDTO dto);
}
