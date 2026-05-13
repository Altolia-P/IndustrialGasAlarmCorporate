package com.niit.industrialgasalarmcorporate.domain.category;

import java.util.List;

public interface CategoryRepository {

    List<Category> findByType(CategoryType type);

    List<Category> findAll();
}
