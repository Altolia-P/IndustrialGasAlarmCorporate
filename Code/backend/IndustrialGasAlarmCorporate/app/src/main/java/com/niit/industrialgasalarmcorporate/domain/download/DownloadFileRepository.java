package com.niit.industrialgasalarmcorporate.domain.download;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface DownloadFileRepository {

    void save(DownloadFile file);

    Optional<DownloadFile> findById(String downloadUuid);

    Page<DownloadFile> findAll(int page, int size);

    void deleteById(String downloadUuid);
}
