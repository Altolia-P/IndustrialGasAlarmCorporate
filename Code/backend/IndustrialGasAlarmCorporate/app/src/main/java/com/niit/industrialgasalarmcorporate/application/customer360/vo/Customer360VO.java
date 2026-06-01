package com.niit.industrialgasalarmcorporate.application.customer360.vo;

import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import lombok.Data;

import java.util.List;

@Data
public class Customer360VO {

    private String phone;
    private String name;
    private String company;
    private boolean registered;

    private int deviceCount;
    private int workOrderCount;
    private int messageCount;
    private int alertCount;

    private List<DeviceVO> devices;
    private List<WorkOrderVO> workOrders;
    private List<MessageVO> messages;
    private List<AlertVO> recentAlerts;
}
