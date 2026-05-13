package com.niit.industrialgasalarmcorporate.application.message.service;

import com.niit.industrialgasalarmcorporate.application.message.dto.BatchProcessDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface MessageService {

    void submitMessage(SubmitMessageDTO dto, String ip);

    void markProcessed(String messageUuid, ProcessMessageDTO dto, String processor);

    void batchProcess(BatchProcessDTO dto, String processor);

    MessageVO getMessage(String messageUuid);

    Page<MessageVO> findMessages(String name, String phone, String status, int page, int size);
}
