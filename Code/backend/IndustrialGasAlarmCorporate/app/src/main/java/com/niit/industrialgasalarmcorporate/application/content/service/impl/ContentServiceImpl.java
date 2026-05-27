package com.niit.industrialgasalarmcorporate.application.content.service.impl;

import com.niit.industrialgasalarmcorporate.application.content.dto.CreateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.dto.UpdateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.service.ContentService;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentDetailVO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.assembler.ContentAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.exception.ContentNotFoundException;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentRepository;
import com.niit.industrialgasalarmcorporate.domain.content.ContentType;
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ContentVO createContent(CreateContentDTO dto) {
        Content content = ContentAssembler.toEntity(dto);
        contentRepository.save(content);
        return ContentAssembler.toVO(content);
    }

    @Override
    @Transactional
    public ContentVO updateContent(String contentUuid, UpdateContentDTO dto) {
        Content content = contentRepository.findById(contentUuid)
                .orElseThrow(() -> new ContentNotFoundException(contentUuid));
        String oldCoverImage = content.getCoverImage();
        ContentAssembler.updateEntity(content, dto);
        if (dto.getCoverImage() != null && oldCoverImage != null
                && !oldCoverImage.equals(dto.getCoverImage())) {
            fileStorageService.delete(oldCoverImage);
        }
        contentRepository.save(content);
        return ContentAssembler.toVO(content);
    }

    @Override
    @Transactional
    public void publishContent(String contentUuid) {
        Content content = contentRepository.findById(contentUuid)
                .orElseThrow(() -> new ContentNotFoundException(contentUuid));
        content.publish();
        contentRepository.save(content);
    }

    @Override
    @Transactional
    public void unpublishContent(String contentUuid) {
        Content content = contentRepository.findById(contentUuid)
                .orElseThrow(() -> new ContentNotFoundException(contentUuid));
        content.unpublish();
        contentRepository.save(content);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentDetailVO getContent(String contentUuid) {
        Content content = contentRepository.findById(contentUuid)
                .orElseThrow(() -> new ContentNotFoundException(contentUuid));
        return ContentAssembler.toDetailVO(content);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentVO> findPublicContents(String type, String categoryUuid, int page, int size) {
        ContentType contentType = ContentType.valueOf(type);
        Page<Content> domainPage;
        if (categoryUuid != null && !categoryUuid.isBlank()) {
            domainPage = contentRepository.findAllWithFilter(null, contentType, categoryUuid, "PUBLISHED", page, size);
        } else {
            domainPage = contentRepository.findByType(contentType, page, size);
        }
        return new Page<>(
                domainPage.getContent().stream().map(ContentAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentVO> findAdminContents(String title, String type, String categoryUuid,
                                              String status, int page, int size) {
        ContentType contentType = type != null ? ContentType.valueOf(type) : null;
        Page<Content> domainPage = contentRepository.findAllWithFilter(
                title, contentType, categoryUuid, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(ContentAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional
    public void deleteContent(String contentUuid) {
        Content content = contentRepository.findById(contentUuid)
                .orElseThrow(() -> new ContentNotFoundException(contentUuid));
        if (content.getCoverImage() != null) {
            fileStorageService.delete(content.getCoverImage());
        }
        contentRepository.deleteById(contentUuid);
    }
}
