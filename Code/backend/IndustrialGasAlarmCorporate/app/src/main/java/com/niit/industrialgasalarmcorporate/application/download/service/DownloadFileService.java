package com.niit.industrialgasalarmcorporate.application.download.service;

import com.niit.industrialgasalarmcorporate.application.download.vo.DownloadFileVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DownloadFileService {

    DownloadFileVO uploadFile(MultipartFile file, String displayName);

    Page<DownloadFileVO> listFiles(int page, int size);

    void deleteFile(String downloadUuid);

    DownloadFileVO getFile(String downloadUuid);
}
