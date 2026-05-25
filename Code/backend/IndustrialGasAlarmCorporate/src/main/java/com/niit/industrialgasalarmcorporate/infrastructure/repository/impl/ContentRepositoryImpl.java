package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentRepository;
import com.niit.industrialgasalarmcorporate.domain.content.ContentStatus;
import com.niit.industrialgasalarmcorporate.domain.content.ContentType;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.CategoryMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.ContentMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.ContentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepository {

    private final ContentMapper contentMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Optional<Content> findById(String contentUuid) {
        ContentPO po = contentMapper.selectById(contentUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public void save(Content content) {
        ContentPO po = toPO(content);
        ContentPO existing = contentMapper.selectById(content.getContentUuid());
        if (existing != null) {
            contentMapper.updateById(po);
        } else {
            contentMapper.insert(po);
        }
    }

    @Override
    public void deleteById(String contentUuid) {
        contentMapper.deleteById(contentUuid);
    }

    @Override
    public Page<Content> findByType(ContentType type, int page, int size) {
        LambdaQueryWrapper<ContentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentPO::getType, type.name())
                .eq(ContentPO::getStatus, ContentStatus.PUBLISHED.name())
                .orderByDesc(ContentPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentPO> result =
                contentMapper.selectPage(mpPage, wrapper);
        List<Content> contents = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(contents, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public List<Content> searchByKeyword(String keyword, int limit) {
        LambdaQueryWrapper<ContentPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(ContentPO::getTitle, keyword).or().like(ContentPO::getSummary, keyword))
                .eq(ContentPO::getType, ContentType.SOLUTION.name())
                .eq(ContentPO::getStatus, ContentStatus.PUBLISHED.name())
                .orderByDesc(ContentPO::getCreatedAt)
                .last("LIMIT " + limit);
        List<ContentPO> poList = contentMapper.selectList(wrapper);
        return poList.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Content> findAllWithFilter(String title, ContentType type, String categoryUuid,
                                                  String status, int page, int size) {
        LambdaQueryWrapper<ContentPO> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isBlank()) {
            wrapper.like(ContentPO::getTitle, title);
        }
        if (type != null) {
            wrapper.eq(ContentPO::getType, type.name());
        }
        if (categoryUuid != null && !categoryUuid.isBlank()) {
            wrapper.eq(ContentPO::getCategoryUuid, categoryUuid);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(ContentPO::getStatus, status);
        }
        wrapper.orderByDesc(ContentPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentPO> result =
                contentMapper.selectPage(mpPage, wrapper);
        List<Content> contents = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(contents, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    private Content toDomain(ContentPO po) {
        String categoryName = null;
        if (po.getCategoryUuid() != null) {
            var cat = categoryMapper.selectById(po.getCategoryUuid());
            if (cat != null) {
                categoryName = cat.getName();
            }
        }
        return new Content(
                po.getContentUuid(),
                po.getTitle(),
                po.getSummary(),
                po.getBody(),
                po.getCoverImage(),
                ContentType.valueOf(po.getType()),
                ContentStatus.valueOf(po.getStatus()),
                po.getCategoryUuid(),
                categoryName,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private ContentPO toPO(Content content) {
        ContentPO po = new ContentPO();
        po.setContentUuid(content.getContentUuid());
        po.setTitle(content.getTitle());
        po.setSummary(content.getSummary());
        po.setBody(content.getBody());
        po.setCoverImage(content.getCoverImage());
        po.setType(content.getType().name());
        po.setStatus(content.getStatus().name());
        po.setCategoryUuid(content.getCategoryUuid());
        return po;
    }
}
