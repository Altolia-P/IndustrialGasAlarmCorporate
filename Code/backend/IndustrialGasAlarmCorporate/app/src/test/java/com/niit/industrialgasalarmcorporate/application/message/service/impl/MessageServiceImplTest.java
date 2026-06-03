package com.niit.industrialgasalarmcorporate.application.message.service.impl;

import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.message.ContactMessage;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.domain.staff.Staff;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRepository;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRole;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffStatus;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.MessageRateLimitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageServiceImpl 留言服务")
class MessageServiceImplTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageRateLimitRepository rateLimitRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private WorkOrderRepository workOrderRepository;
    @Mock private DashboardCacheRepository dashboardCacheRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private static final String IP = "127.0.0.1";
    private static final String PHONE = "13812345678";
    private static final String MSG_UUID = "msg-001";
    private static final String STAFF_UUID = "staff-001";

    @Test
    @DisplayName("提交留言成功，返回 messageUuid")
    void shouldSubmitMessageSuccessfully() {
        SubmitMessageDTO dto = new SubmitMessageDTO();
        dto.setPhone(PHONE);
        dto.setName("张三");
        dto.setContent("产品咨询");

        when(rateLimitRepository.tryAcquirePhone(PHONE)).thenReturn(true);
        when(rateLimitRepository.tryAcquireIp(IP)).thenReturn(true);

        String uuid = messageService.submitMessage(dto, IP);

        assertNotNull(uuid);
        verify(messageRepository).save(any(ContactMessage.class));
        verify(dashboardCacheRepository).evict("dashboard:stats");
    }

    @Test
    @DisplayName("频繁提交被电话限流时抛出异常")
    void shouldThrowWhenPhoneRateLimited() {
        SubmitMessageDTO dto = new SubmitMessageDTO();
        dto.setPhone(PHONE);
        dto.setName("张三");
        dto.setContent("产品咨询");

        when(rateLimitRepository.tryAcquirePhone(PHONE)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> messageService.submitMessage(dto, IP));
        assertEquals(ErrorCode.VALIDATION_ERROR.getCode(), ex.getCode());
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("处理留言后无剩余任务时员工变为 STANDBY")
    void shouldSetStaffToStandbyWhenNoRemainingTasks() {
        ContactMessage message = new ContactMessage(
                MSG_UUID, "张三", PHONE, "产品咨询",
                "127.0.0.1", MessageStatus.IN_PROGRESS, null, null,
                STAFF_UUID, "李四",
                LocalDateTime.now(), null, 0
        );
        Staff staff = new Staff(STAFF_UUID, "user-s1", "李四", "13900000000",
                "li@test.com", StaffRole.TECH_SUPPORT,
                StaffStatus.WORKING, LocalDateTime.now());

        ProcessMessageDTO dto = new ProcessMessageDTO();
        dto.setRemark("已沟通解决");

        when(messageRepository.findById(MSG_UUID)).thenReturn(Optional.of(message));
        when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                .thenReturn(0L);
        when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                .thenReturn(0L);
        when(staffRepository.findById(STAFF_UUID)).thenReturn(Optional.of(staff));

        messageService.markProcessed(MSG_UUID, dto, "李四");

        assertEquals(MessageStatus.PROCESSED, message.getStatus());
        verify(staffRepository).save(staff);
        assertEquals(StaffStatus.STANDBY, staff.getStatus());
        verify(dashboardCacheRepository).evict("dashboard:stats");
    }

    @Test
    @DisplayName("仍有剩余工单时员工状态保持 WORKING")
    void shouldKeepStaffWorkingWhenRemainingWorkOrders() {
        ContactMessage message = new ContactMessage(
                MSG_UUID, "张三", PHONE, "产品咨询",
                "127.0.0.1", MessageStatus.IN_PROGRESS, null, null,
                STAFF_UUID, "李四",
                LocalDateTime.now(), null, 0
        );

        ProcessMessageDTO dto = new ProcessMessageDTO();
        dto.setRemark("已沟通解决");

        when(messageRepository.findById(MSG_UUID)).thenReturn(Optional.of(message));
        when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                .thenReturn(0L);
        when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                .thenReturn(1L);

        messageService.markProcessed(MSG_UUID, dto, "李四");

        assertEquals(MessageStatus.PROCESSED, message.getStatus());
        verify(staffRepository, never()).save(any());
    }
}
