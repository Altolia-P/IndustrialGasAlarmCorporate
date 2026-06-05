package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;
import com.niit.industrialgasalarmcorporate.domain.category.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CategoryAssembler {

    private CategoryAssembler() {
    }

    public static CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setCategoryUuid(category.getCategoryUuid());
        vo.setName(category.getName());
        vo.setType(category.getType().name());
        vo.setParentUuid(category.getParentUuid());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }

    public static List<CategoryVO> toTree(List<Category> categories) {
        List<CategoryVO> all = categories.stream()
                .map(CategoryAssembler::toVO)
                .map(CategoryAssembler::normalizeParentUuid)
                .collect(Collectors.toList());

        Map<String, List<CategoryVO>> childrenMap = all.stream()
                .filter(c -> c.getParentUuid() != null)
                .collect(Collectors.groupingBy(CategoryVO::getParentUuid));

        List<CategoryVO> roots = new ArrayList<>();
        for (CategoryVO vo : all) {
            vo.setChildren(childrenMap.getOrDefault(vo.getCategoryUuid(), new ArrayList<>()));
            if (vo.getParentUuid() == null) {
                roots.add(vo);
            }
        }
        return roots;
    }

    private static CategoryVO normalizeParentUuid(CategoryVO vo) {
        if (vo.getParentUuid() != null && vo.getParentUuid().isBlank()) {
            vo.setParentUuid(null);
        }
        return vo;
    }
}
