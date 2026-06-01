package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.download.DownloadFile;
import com.niit.industrialgasalarmcorporate.domain.download.DownloadFileRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.DownloadFileMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.DownloadFilePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DownloadFileRepositoryImpl implements DownloadFileRepository {

    private final DownloadFileMapper downloadFileMapper;

    @Override
    public void save(DownloadFile file) {
        DownloadFilePO po = toPO(file);
        DownloadFilePO existing = downloadFileMapper.selectById(file.getDownloadUuid());
        if (existing != null) {
            downloadFileMapper.updateById(po);
        } else {
            downloadFileMapper.insert(po);
        }
    }

    @Override
    public Optional<DownloadFile> findById(String downloadUuid) {
        DownloadFilePO po = downloadFileMapper.selectById(downloadUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<DownloadFile> findAll(int page, int size) {
        LambdaQueryWrapper<DownloadFilePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DownloadFilePO::getCreatedAt);
        Page<DownloadFilePO> mpPage = new Page<>(page, size);
        Page<DownloadFilePO> result = downloadFileMapper.selectPage(mpPage, wrapper);
        List<DownloadFile> files = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                files, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public void deleteById(String downloadUuid) {
        downloadFileMapper.deleteById(downloadUuid);
    }

    private DownloadFile toDomain(DownloadFilePO po) {
        return new DownloadFile(
                po.getDownloadUuid(),
                po.getDisplayName(),
                po.getOriginalName(),
                po.getFileSize() != null ? po.getFileSize() : 0L,
                po.getContentType(),
                po.getStoredPath(),
                po.getCreatedAt()
        );
    }

    private DownloadFilePO toPO(DownloadFile file) {
        DownloadFilePO po = new DownloadFilePO();
        po.setDownloadUuid(file.getDownloadUuid());
        po.setDisplayName(file.getDisplayName());
        po.setOriginalName(file.getOriginalName());
        po.setFileSize(file.getFileSize());
        po.setContentType(file.getContentType());
        po.setStoredPath(file.getStoredPath());
        return po;
    }
}
