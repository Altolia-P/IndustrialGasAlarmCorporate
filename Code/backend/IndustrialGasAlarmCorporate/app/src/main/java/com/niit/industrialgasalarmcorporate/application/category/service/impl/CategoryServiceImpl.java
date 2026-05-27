package com.niit.industrialgasalarmcorporate.application.category.service.impl;

import com.niit.industrialgasalarmcorporate.application.category.dto.CreateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.dto.UpdateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.service.CategoryService;
import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;
import com.niit.industrialgasalarmcorporate.assembler.CategoryAssembler;
import com.niit.industrialgasalarmcorporate.common.exception.CategoryNotFoundException;
import com.niit.industrialgasalarmcorporate.domain.category.Category;
import com.niit.industrialgasalarmcorporate.domain.category.CategoryRepository;
import com.niit.industrialgasalarmcorporate.domain.category.CategoryType;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.CategoryCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCacheRepository cacheRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryVO> getCategoriesByType(String type) {
        List<CategoryVO> cached = cacheRepository.get(type);
        if (cached != null) {
            return cached;
        }
        CategoryType categoryType = CategoryType.valueOf(type);
        List<Category> categories = categoryRepository.findByType(categoryType);
        List<CategoryVO> tree = CategoryAssembler.toTree(categories);
        cacheRepository.put(type, tree);
        return tree;
    }

    @Override
    @Transactional
    public CategoryVO createCategory(CreateCategoryDTO dto) {
        CategoryType categoryType = CategoryType.valueOf(dto.getType());
        Category category = new Category(dto.getName(), categoryType, dto.getParentUuid(), dto.getSortOrder());
        categoryRepository.save(category);
        evictCache(dto.getType());
        log.info("分类已创建: name={}, type={}, uuid={}", category.getName(), category.getType(), category.getCategoryUuid());
        return CategoryAssembler.toVO(category);
    }

    @Override
    @Transactional
    public CategoryVO updateCategory(String categoryUuid, UpdateCategoryDTO dto) {
        Category category = categoryRepository.findById(categoryUuid)
                .orElseThrow(CategoryNotFoundException::new);
        String name = dto.getName() != null ? dto.getName() : category.getName();
        CategoryType type = dto.getType() != null ? CategoryType.valueOf(dto.getType()) : category.getType();
        String parentUuid = dto.getParentUuid() != null ? dto.getParentUuid() : category.getParentUuid();
        int sortOrder = dto.getSortOrder() != null ? dto.getSortOrder() : category.getSortOrder();
        category = new Category(categoryUuid, name, type, parentUuid, sortOrder);
        categoryRepository.save(category);
        evictCache(category.getType().name());
        log.info("分类已更新: uuid={}, name={}", categoryUuid, category.getName());
        return CategoryAssembler.toVO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryUuid) {
        Category category = categoryRepository.findById(categoryUuid)
                .orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(categoryUuid);
        evictCache(category.getType().name());
        log.info("分类已删除: uuid={}, name={}", categoryUuid, category.getName());
    }

    private void evictCache(String type) {
        try {
            cacheRepository.evict(type);
        } catch (Exception e) {
            log.warn("分类缓存清除失败: type={}", type, e);
        }
    }
}
