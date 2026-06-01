package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.domain.comment.Comment;

import java.time.format.DateTimeFormatter;

public final class CommentAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CommentAssembler() {}

    public static CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setCommentUuid(comment.getCommentUuid());
        vo.setTargetType(comment.getTargetType().name());
        vo.setTargetUuid(comment.getTargetUuid());
        vo.setAuthorType(comment.getAuthorType().name());
        vo.setAuthorUuid(comment.getAuthorUuid());
        vo.setAuthorName(comment.getAuthorName());
        vo.setContent(comment.getContent());
        vo.setCreatedAt(comment.getCreatedAt().format(DTF));
        return vo;
    }
}
