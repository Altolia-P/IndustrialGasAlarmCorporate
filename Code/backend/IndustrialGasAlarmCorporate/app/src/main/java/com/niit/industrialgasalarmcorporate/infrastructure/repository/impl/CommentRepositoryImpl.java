package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.domain.comment.Comment;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentRepository;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.CommentMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.CommentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentMapper commentMapper;

    @Override
    public void save(Comment comment) {
        CommentPO po = toPO(comment);
        CommentPO existing = commentMapper.selectById(comment.getCommentUuid());
        if (existing != null) {
            commentMapper.updateById(po);
        } else {
            commentMapper.insert(po);
        }
    }

    @Override
    public List<Comment> findByTarget(CommentTargetType targetType, String targetUuid) {
        LambdaQueryWrapper<CommentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentPO::getTargetType, targetType.name())
                .eq(CommentPO::getTargetUuid, targetUuid)
                .orderByAsc(CommentPO::getCreatedAt);
        return commentMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Comment toDomain(CommentPO po) {
        return new Comment(
                po.getCommentUuid(),
                CommentTargetType.valueOf(po.getTargetType()),
                po.getTargetUuid(),
                CommentAuthorType.valueOf(po.getAuthorType()),
                po.getAuthorUuid(),
                po.getAuthorName(),
                po.getContent(),
                po.getCreatedAt()
        );
    }

    private CommentPO toPO(Comment comment) {
        CommentPO po = new CommentPO();
        po.setCommentUuid(comment.getCommentUuid());
        po.setTargetType(comment.getTargetType().name());
        po.setTargetUuid(comment.getTargetUuid());
        po.setAuthorType(comment.getAuthorType().name());
        po.setAuthorUuid(comment.getAuthorUuid());
        po.setAuthorName(comment.getAuthorName());
        po.setContent(comment.getContent());
        return po;
    }
}
