package com.niit.industrialgasalarmcorporate.application.content.service;

import com.niit.industrialgasalarmcorporate.application.content.dto.CreateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.dto.UpdateContentDTO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentDetailVO;
import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface ContentService {

    ContentVO createContent(CreateContentDTO dto);

    ContentVO updateContent(String contentUuid, UpdateContentDTO dto);

    void publishContent(String contentUuid);

    ContentDetailVO getContent(String contentUuid);

    Page<ContentVO> findPublicContents(String type, String categoryUuid, int page, int size);

    Page<ContentVO> findAdminContents(String title, String type, String categoryUuid, String status, int page, int size);

    void deleteContent(String contentUuid);
}
