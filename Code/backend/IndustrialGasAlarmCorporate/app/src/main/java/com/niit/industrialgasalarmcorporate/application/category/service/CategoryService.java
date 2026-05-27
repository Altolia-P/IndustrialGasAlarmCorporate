package com.niit.industrialgasalarmcorporate.application.category.service;

import com.niit.industrialgasalarmcorporate.application.category.dto.CreateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.dto.UpdateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> getCategoriesByType(String type);

    CategoryVO createCategory(CreateCategoryDTO dto);

    CategoryVO updateCategory(String categoryUuid, UpdateCategoryDTO dto);

    void deleteCategory(String categoryUuid);
}
