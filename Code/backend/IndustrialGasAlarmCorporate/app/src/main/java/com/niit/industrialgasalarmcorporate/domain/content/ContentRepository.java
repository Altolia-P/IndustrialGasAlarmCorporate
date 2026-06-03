package com.niit.industrialgasalarmcorporate.domain.content;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.List;
import java.util.Optional;

public interface ContentRepository {

    Optional<Content> findById(String contentUuid);

    void save(Content content);

    void deleteById(String contentUuid);

    Page<Content> findByType(ContentType type, int page, int size);

    Page<Content> findAllWithFilter(String title, ContentType type, String categoryUuid, String status, int page, int size);

    List<Content> searchByKeyword(String keyword, int limit);

    long countByCategoryUuid(String categoryUuid);
}
