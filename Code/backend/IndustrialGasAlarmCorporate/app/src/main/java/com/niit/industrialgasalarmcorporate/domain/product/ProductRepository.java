package com.niit.industrialgasalarmcorporate.domain.product;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findById(String productUuid);

    void save(Product product);

    void deleteById(String productUuid);

    Page<Product> findByCategory(String categoryUuid, int page, int size);

    Page<Product> findAll(int page, int size);

    Page<Product> findAllWithFilter(String name, String categoryUuid, String status, int page, int size);

    List<Product> searchByKeyword(String keyword, int limit);
}
