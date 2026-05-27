package com.niit.industrialgasalarmcorporate.application.message.service;

import com.niit.industrialgasalarmcorporate.application.message.dto.AssignMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.BatchProcessDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface MessageService {

    String submitMessage(SubmitMessageDTO dto, String ip);

    void assignMessage(String messageUuid, AssignMessageDTO dto);

    void markProcessed(String messageUuid, ProcessMessageDTO dto, String processor);

    void batchProcess(BatchProcessDTO dto, String processor);

    MessageVO getMessage(String messageUuid);

    Page<MessageVO> findMessages(String name, String phone, String status, int page, int size);

    Page<MessageVO> findUserMessages(String name, int page, int size);

    Page<MessageVO> findStaffMessages(String staffUuid, int page, int size);
}
