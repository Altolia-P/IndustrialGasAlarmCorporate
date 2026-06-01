package com.niit.industrialgasalarmcorporate.application.download.service.impl;

import com.niit.industrialgasalarmcorporate.application.download.service.DownloadFileService;
import com.niit.industrialgasalarmcorporate.application.download.vo.DownloadFileVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.download.DownloadFile;
import com.niit.industrialgasalarmcorporate.domain.download.DownloadFileRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DownloadFileServiceImpl implements DownloadFileService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DownloadFileRepository downloadFileRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public DownloadFileVO uploadFile(MultipartFile file, String displayName) {
        String storedPath = fileStorageService.storeDocument(file);
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : displayName;
        DownloadFile downloadFile = new DownloadFile(
                displayName != null && !displayName.isBlank() ? displayName : originalName,
                originalName,
                file.getSize(),
                file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                storedPath
        );
        downloadFileRepository.save(downloadFile);
        return toVO(downloadFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DownloadFileVO> listFiles(int page, int size) {
        Page<DownloadFile> domainPage = downloadFileRepository.findAll(page, size);
        return new Page<>(
                domainPage.getContent().stream().map(this::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadFileVO getFile(String downloadUuid) {
        DownloadFile file = downloadFileRepository.findById(downloadUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "文件不存在"));
        return toVO(file);
    }

    @Override
    @Transactional
    public void deleteFile(String downloadUuid) {
        DownloadFile file = downloadFileRepository.findById(downloadUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "文件不存在"));
        fileStorageService.delete(file.getStoredPath());
        downloadFileRepository.deleteById(downloadUuid);
    }

    private DownloadFileVO toVO(DownloadFile file) {
        DownloadFileVO vo = new DownloadFileVO();
        vo.setDownloadUuid(file.getDownloadUuid());
        vo.setDisplayName(file.getDisplayName());
        vo.setOriginalName(file.getOriginalName());
        vo.setFileSize(file.getFileSize());
        vo.setContentType(file.getContentType());
        vo.setStoredPath(file.getStoredPath());
        vo.setCreatedAt(file.getCreatedAt().format(FORMATTER));
        return vo;
    }
}
