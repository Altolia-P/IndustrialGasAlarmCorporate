package com.niit.industrialgasalarmcorporate.domain.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findByType(CategoryType type);

    List<Category> findAll();

    Optional<Category> findById(String categoryUuid);

    void save(Category category);

    void delete(String categoryUuid);

    List<Category> findByParentUuid(String parentUuid);
}
