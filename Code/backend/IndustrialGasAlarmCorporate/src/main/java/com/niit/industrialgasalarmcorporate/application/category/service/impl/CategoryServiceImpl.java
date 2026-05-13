package com.niit.industrialgasalarmcorporate.application.category.service.impl;

import com.niit.industrialgasalarmcorporate.application.category.service.CategoryService;
import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;
import com.niit.industrialgasalarmcorporate.assembler.CategoryAssembler;
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
        List<Category> cached = cacheRepository.get(type);
        if (cached != null) {
            return CategoryAssembler.toTree(cached);
        }
        CategoryType categoryType = CategoryType.valueOf(type);
        List<Category> categories = categoryRepository.findByType(categoryType);
        cacheRepository.put(type, categories);
        return CategoryAssembler.toTree(categories);
    }
}
