package com.niit.industrialgasalarmcorporate.application.customer360.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.application.customer360.service.Customer360Service;
import com.niit.industrialgasalarmcorporate.application.customer360.vo.Customer360VO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.assembler.AlertAssembler;
import com.niit.industrialgasalarmcorporate.assembler.DeviceAssembler;
import com.niit.industrialgasalarmcorporate.assembler.MessageAssembler;
import com.niit.industrialgasalarmcorporate.assembler.WorkOrderAssembler;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrder;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Customer360ServiceImpl implements Customer360Service {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final MessageRepository messageRepository;
    private final AlertRepository alertRepository;

    @Override
    @Transactional(readOnly = true)
    public Customer360VO getCustomer360(String phone) {
        Customer360VO vo = new Customer360VO();
        vo.setPhone(phone);

        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            vo.setName(user.getUsername());
            vo.setCompany(user.getCompany());
            vo.setRegistered(true);

            List<Device> devices = deviceRepository.findByCustomerUuid(user.getUserUuid());
            List<DeviceVO> deviceVOs = devices.stream().map(DeviceAssembler::toVO).collect(Collectors.toList());
            vo.setDevices(deviceVOs);
            vo.setDeviceCount(devices.size());

            if (!devices.isEmpty()) {
                List<String> deviceUuids = devices.stream().map(Device::getDeviceUuid).collect(Collectors.toList());
                List<Alert> recentAlerts = alertRepository.findByDeviceUuids(deviceUuids, 20);
                List<AlertVO> alertVOs = recentAlerts.stream().map(AlertAssembler::toVO).collect(Collectors.toList());
                vo.setRecentAlerts(alertVOs);
                vo.setAlertCount(recentAlerts.size());
            } else {
                vo.setRecentAlerts(Collections.emptyList());
                vo.setAlertCount(0);
            }
        } else {
            vo.setName(phone);
            vo.setCompany(null);
            vo.setRegistered(false);
            vo.setDevices(Collections.emptyList());
            vo.setDeviceCount(0);
            vo.setRecentAlerts(Collections.emptyList());
            vo.setAlertCount(0);
        }

        List<WorkOrder> workOrders = workOrderRepository.findByCustomerPhone(phone);
        List<WorkOrderVO> woVOs = workOrders.stream().map(WorkOrderAssembler::toVO).collect(Collectors.toList());
        vo.setWorkOrders(woVOs);
        vo.setWorkOrderCount(woVOs.size());

        var msgPage = messageRepository.findAllWithFilter(null, phone, null, 1, 100);
        List<MessageVO> msgVOs = msgPage.getContent().stream().map(MessageAssembler::toVO).collect(Collectors.toList());
        vo.setMessages(msgVOs);
        vo.setMessageCount((int) msgPage.getTotalElements());

        return vo;
    }
}
