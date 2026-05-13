package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.domain.category.Category;
import com.niit.industrialgasalarmcorporate.domain.category.CategoryRepository;
import com.niit.industrialgasalarmcorporate.domain.category.CategoryType;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.CategoryMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.CategoryPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> findByType(CategoryType type) {
        LambdaQueryWrapper<CategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryPO::getType, type.name())
                .orderByAsc(CategoryPO::getSortOrder);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findAll() {
        LambdaQueryWrapper<CategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CategoryPO::getSortOrder);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Category toDomain(CategoryPO po) {
        return new Category(
                po.getCategoryUuid(),
                po.getName(),
                CategoryType.valueOf(po.getType()),
                po.getParentUuid(),
                po.getSortOrder()
        );
    }
}
