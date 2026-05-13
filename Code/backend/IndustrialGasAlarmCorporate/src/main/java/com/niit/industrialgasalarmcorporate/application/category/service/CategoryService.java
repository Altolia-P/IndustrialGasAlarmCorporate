package com.niit.industrialgasalarmcorporate.application.category.service;

import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> getCategoriesByType(String type);
}
