package com.niit.industrialgasalarmcorporate.application.workorder.service.impl;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.CompleteWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.domain.staff.Staff;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRepository;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRole;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffStatus;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrder;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderPriority;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderStatus;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderType;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkOrderServiceImpl 工单服务")
class WorkOrderServiceImplTest {

    @Mock private WorkOrderRepository workOrderRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private AlertRepository alertRepository;
    @Mock private DashboardCacheRepository dashboardCacheRepository;

    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    private WorkOrder workOrder;
    private Staff staff;
    private static final String WORK_ORDER_UUID = "wo-001";
    private static final String STAFF_UUID = "staff-001";

    @BeforeEach
    void setUp() {
        workOrder = new WorkOrder(
                WORK_ORDER_UUID,
                "测试工单",
                WorkOrderType.TECH_SUPPORT,
                "测试描述",
                WorkOrderStatus.IN_PROGRESS,
                WorkOrderPriority.HIGH,
                "测试客户",
                "13800000000",
                STAFF_UUID,
                "张三",
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );

        staff = new Staff(
                STAFF_UUID,
                "user-001",
                "张三",
                "13800000000",
                "zhang@test.com",
                StaffRole.TECH_SUPPORT,
                StaffStatus.WORKING,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("doComplete — 反向联动（Staff 状态变更）")
    class ReverseLinkageWithStaff {

        @Test
        @DisplayName("剩余工单和消息均为 0 时，员工状态变为 STANDBY")
        void shouldSetStaffToStandbyWhenNoRemainingWork() {
            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理完成");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(staffRepository.findById(STAFF_UUID)).thenReturn(Optional.of(staff));

            workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto);

            verify(workOrderRepository).save(any(WorkOrder.class));
            verify(staffRepository).save(staff);
            assertEquals(StaffStatus.STANDBY, staff.getStatus(),
                    "无剩余任务时员工应进入待命状态");
        }

        @Test
        @DisplayName("仍有剩余工单时，员工状态保持不变（WORKING）")
        void shouldKeepStaffWorkingWhenRemainingWorkOrders() {
            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理完成");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                    .thenReturn(1L);

            workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto);

            verify(workOrderRepository).save(any(WorkOrder.class));
            verify(staffRepository, never()).save(any());
            assertEquals(StaffStatus.WORKING, staff.getStatus(),
                    "仍有剩余工单时员工状态应保持 WORKING");
        }

        @Test
        @DisplayName("工单未分配员工时，拒绝完成")
        void shouldRejectCompleteWhenNotAssigned() {
            WorkOrder unassignedWo = new WorkOrder(
                    WORK_ORDER_UUID,
                    "未分配工单",
                    WorkOrderType.TECH_SUPPORT,
                    "描述",
                    WorkOrderStatus.IN_PROGRESS,
                    WorkOrderPriority.HIGH,
                    "客户",
                    "13900000000",
                    null,   // no assigned staff
                    null,
                    null, null,
                    LocalDateTime.now(), LocalDateTime.now(),
                    0
            );

            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(unassignedWo));

            assertThrows(IllegalStateException.class,
                    () -> workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto));

            verify(workOrderRepository, never()).save(any(WorkOrder.class));
            verify(staffRepository, never()).findById(anyString());
            verify(staffRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("resolveByWorkOrder — 工单完成时自动解决关联告警")
    class ResolveAlertByWorkOrder {

        @Test
        @DisplayName("关联 CONFIRMED 告警时，工单完成自动解决告警")
        void shouldResolveLinkedConfirmedAlert() {
            Alert linkedAlert = new Alert(
                    "alert-001",
                    "device-001",
                    "rule-001",
                    AlertRuleType.THRESHOLD,
                    AlertSeverity.CRITICAL,
                    new BigDecimal("2.0"),
                    new BigDecimal("1.0"),
                    "测试告警",
                    AlertStatus.CONFIRMED,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    "admin",
                    null,
                    null,
                    WORK_ORDER_UUID,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            );

            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理完成");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(workOrder));
            when(alertRepository.findByWorkOrderUuid(WORK_ORDER_UUID)).thenReturn(Optional.of(linkedAlert));
            when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(staffRepository.findById(STAFF_UUID)).thenReturn(Optional.of(staff));

            workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto);

            verify(alertRepository).save(linkedAlert);
            assertEquals(AlertStatus.RESOLVED, linkedAlert.getStatus(),
                    "关联告警应被自动解决为 RESOLVED 状态");
            assertEquals("system", linkedAlert.getResolvedBy(),
                    "关联告警的解决人应为 system");
            assertNotNull(linkedAlert.getResolvedAt(),
                    "关联告警的解决时间不应为空");
        }

        @Test
        @DisplayName("关联 PENDING 告警时，工单完成不解决告警（仅 CONFIRMED 可解决）")
        void shouldNotResolvePendingAlert() {
            Alert pendingAlert = new Alert(
                    "alert-002",
                    "device-001",
                    "rule-001",
                    AlertRuleType.THRESHOLD,
                    AlertSeverity.WARNING,
                    new BigDecimal("1.5"),
                    new BigDecimal("1.0"),
                    "待确认告警",
                    AlertStatus.PENDING,
                    LocalDateTime.now(),
                    null,
                    null,
                    null,
                    null,
                    WORK_ORDER_UUID,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            );

            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理完成");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(workOrder));
            when(alertRepository.findByWorkOrderUuid(WORK_ORDER_UUID)).thenReturn(Optional.of(pendingAlert));
            when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(staffRepository.findById(STAFF_UUID)).thenReturn(Optional.of(staff));

            workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto);

            verify(alertRepository, never()).save(pendingAlert);
            assertEquals(AlertStatus.PENDING, pendingAlert.getStatus(),
                    "PENDING 状态的告警不应被自动解决");
        }

        @Test
        @DisplayName("无关联告警时，工作正常完成")
        void shouldCompleteNormallyWhenNoLinkedAlert() {
            CompleteWorkOrderDTO dto = new CompleteWorkOrderDTO();
            dto.setResolution("已处理完成");

            when(workOrderRepository.findById(WORK_ORDER_UUID)).thenReturn(Optional.of(workOrder));
            when(alertRepository.findByWorkOrderUuid(WORK_ORDER_UUID)).thenReturn(Optional.empty());
            when(workOrderRepository.countByStaffAndStatus(STAFF_UUID, WorkOrderStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(messageRepository.countByStaffAndStatus(STAFF_UUID, MessageStatus.IN_PROGRESS))
                    .thenReturn(0L);
            when(staffRepository.findById(STAFF_UUID)).thenReturn(Optional.of(staff));

            workOrderService.completeWorkOrder(WORK_ORDER_UUID, dto);

            verify(workOrderRepository).save(any(WorkOrder.class));
            verify(staffRepository).save(staff);
            verify(alertRepository).findByWorkOrderUuid(WORK_ORDER_UUID);
            verify(alertRepository, never()).save(any());
        }
    }
}
