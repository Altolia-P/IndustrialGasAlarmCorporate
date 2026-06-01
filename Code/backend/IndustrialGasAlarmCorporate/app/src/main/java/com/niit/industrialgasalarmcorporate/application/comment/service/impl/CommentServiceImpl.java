package com.niit.industrialgasalarmcorporate.application.comment.service.impl;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.assembler.CommentAssembler;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.comment.Comment;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentRepository;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final WorkOrderRepository workOrderRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentVO> findByTarget(CommentTargetType targetType, String targetUuid) {
        return commentRepository.findByTarget(targetType, targetUuid).stream()
                .map(CommentAssembler::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentVO addComment(CommentTargetType targetType, String targetUuid,
                                CommentAuthorType authorType, String authorUuid, String authorName,
                                CreateCommentDTO dto) {
        if (targetType == CommentTargetType.WORK_ORDER) {
            if (workOrderRepository.findById(targetUuid).isEmpty()) {
                throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND);
            }
        } else if (targetType == CommentTargetType.MESSAGE) {
            if (messageRepository.findById(targetUuid).isEmpty()) {
                throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
            }
        }
        Comment comment = new Comment(targetType, targetUuid, authorType, authorUuid, authorName, dto.getContent());
        commentRepository.save(comment);
        return CommentAssembler.toVO(comment);
    }
}
