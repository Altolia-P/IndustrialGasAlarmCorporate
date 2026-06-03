package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.content.dto.CreateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.dto.UpdateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentDetailVO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentStatus;
import com.niit.industrialgasalarmcorporate.domain.content.ContentType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ContentAssembler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ContentAssembler() {
    }

    public static Content toEntity(CreateContentDTO dto) {
        ContentStatus status = dto.getStatus() != null
                ? ContentStatus.valueOf(dto.getStatus())
                : ContentStatus.PUBLISHED;
        return new Content(
                dto.getTitle(),
                dto.getSummary(),
                dto.getBody(),
                dto.getCoverImage(),
                ContentType.valueOf(dto.getType()),
                status,
                dto.getCategoryUuid()
        );
    }

    public static void updateEntity(Content content, UpdateContentDTO dto) {
        content.update(
                dto.getTitle() != null ? dto.getTitle() : content.getTitle(),
                dto.getSummary() != null ? dto.getSummary() : content.getSummary(),
                dto.getBody() != null ? dto.getBody() : content.getBody(),
                dto.getCoverImage() != null ? dto.getCoverImage() : content.getCoverImage(),
                dto.getCategoryUuid() != null ? dto.getCategoryUuid() : content.getCategoryUuid()
        );
        if (dto.getStatus() != null) {
            content.setStatus(ContentStatus.valueOf(dto.getStatus()));
        }
    }

    public static ContentVO toVO(Content content) {
        ContentVO vo = new ContentVO();
        vo.setContentUuid(content.getContentUuid());
        vo.setTitle(content.getTitle());
        vo.setSummary(content.getSummary());
        vo.setCoverImage(content.getCoverImage());
        vo.setType(content.getType().name());
        vo.setCategoryUuid(content.getCategoryUuid());
        vo.setCategoryName(content.getCategoryName());
        vo.setStatus(content.getStatus().name());
        if (content.getCreatedAt() != null) {
            vo.setCreatedAt(content.getCreatedAt().format(FORMATTER));
        }
        if (content.getUpdatedAt() != null) {
            vo.setUpdatedAt(content.getUpdatedAt().format(FORMATTER));
        }
        return vo;
    }

    public static ContentDetailVO toDetailVO(Content content) {
        ContentDetailVO vo = new ContentDetailVO();
        vo.setContentUuid(content.getContentUuid());
        vo.setTitle(content.getTitle());
        vo.setBody(content.getBody());
        vo.setCoverImage(content.getCoverImage());
        vo.setType(content.getType().name());
        vo.setCategoryUuid(content.getCategoryUuid());
        vo.setCategoryName(content.getCategoryName());
        vo.setStatus(content.getStatus().name());
        if (content.getCreatedAt() != null) {
            vo.setCreatedAt(content.getCreatedAt().format(FORMATTER));
        }
        if (content.getUpdatedAt() != null) {
            vo.setUpdatedAt(content.getUpdatedAt().format(FORMATTER));
        }
        return vo;
    }
}
