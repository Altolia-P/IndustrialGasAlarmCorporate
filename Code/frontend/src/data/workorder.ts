import { reactive } from 'vue'
import { StaffStatus, StaffRole } from '@/types/staff'
import { WorkOrderType, WorkOrderStatus, WorkOrderPriority } from '@/types/workorder'
import { MessageStatus } from '@/types/message'
import type { StaffVO } from '@/types/staff'
import type { WorkOrderVO } from '@/types/workorder'
import type { MessageVO } from '@/types/message'

export const staffList = reactive<StaffVO[]>([
  { staffUuid: '1', name: '张工', phone: '13800010001', email: 'zhang@intersense.cn', role: StaffRole.FIELD_TECH, status: StaffStatus.WORKING, createdAt: '2024-01-10' },
  { staffUuid: '2', name: '李工', phone: '13800010002', email: 'li@intersense.cn', role: StaffRole.FIELD_TECH, status: StaffStatus.BUSINESS_TRIP, createdAt: '2024-01-15' },
  { staffUuid: '3', name: '王工', phone: '13800010003', email: 'wang@intersense.cn', role: StaffRole.TECH_SUPPORT, status: StaffStatus.WORKING, createdAt: '2024-02-01' },
  { staffUuid: '4', name: '赵工', phone: '13800010004', email: 'zhao@intersense.cn', role: StaffRole.AFTER_SALES, status: StaffStatus.STANDBY, createdAt: '2024-02-10' },
  { staffUuid: '5', name: '小刘', phone: '13800010005', email: 'liu@intersense.cn', role: StaffRole.CUSTOMER_SERVICE, status: StaffStatus.WORKING, createdAt: '2024-03-01' },
  { staffUuid: '6', name: '小陈', phone: '13800010006', email: 'chen@intersense.cn', role: StaffRole.CUSTOMER_SERVICE, status: StaffStatus.VACATION, createdAt: '2024-03-05' },
  { staffUuid: '7', name: '周工', phone: '13800010007', email: 'zhou@intersense.cn', role: StaffRole.AFTER_SALES, status: StaffStatus.WORKING, createdAt: '2024-03-10' }
])

export const messages = reactive<MessageVO[]>([
  { messageUuid: '1', name: '张某', phone: '138****8888', content: '想了解贵公司的气体检测仪产品，能否发一份产品手册？', status: MessageStatus.PENDING, assignedStaffUuid: '', assignedStaffName: '', submittedAt: '2024-03-15 14:30', remark: '' },
  { messageUuid: '2', name: '李某', phone: '139****9999', content: '我们需要一套冶金行业的气体监测方案，请联系我', status: MessageStatus.PENDING, assignedStaffUuid: '', assignedStaffName: '', submittedAt: '2024-03-15 11:20', remark: '' },
  { messageUuid: '3', name: '王某', phone: '137****7777', content: '咨询贵公司的SF6在线监测系统，用于变电站', status: MessageStatus.PENDING, assignedStaffUuid: '', assignedStaffName: '', submittedAt: '2024-03-14 16:45', remark: '' },
  { messageUuid: '4', name: '赵某', phone: '136****6666', content: '想预约一个产品演示', status: MessageStatus.PROCESSED, assignedStaffUuid: '4', assignedStaffName: '赵工', submittedAt: '2024-03-14 10:00', remark: '已安排3月18日下午演示' }
])

export const workOrders = reactive<WorkOrderVO[]>([
  { workOrderUuid: '1', title: '某石化厂气体检测仪故障排查', type: WorkOrderType.TECH_SUPPORT, description: '客户反映IS-300固定式检测器数据漂移，需现场排查', status: WorkOrderStatus.PENDING, priority: WorkOrderPriority.HIGH, customerName: '某石化公司', customerPhone: '139****1234', assignedStaffUuid: '', assignedStaffName: '', resolution: '', createdAt: '2024-03-15 09:30', updatedAt: '2024-03-15 09:30' },
  { workOrderUuid: '2', title: '冶金厂报警控制器联网配置', type: WorkOrderType.TECH_SUPPORT, description: 'ISC-8控制器与上位机通讯异常，需要远程协助', status: WorkOrderStatus.IN_PROGRESS, priority: WorkOrderPriority.MEDIUM, customerName: '某钢铁集团', customerPhone: '138****5678', assignedStaffUuid: '3', assignedStaffName: '王工', resolution: '', createdAt: '2024-03-14 14:00', updatedAt: '2024-03-15 10:00' },
  { workOrderUuid: '3', title: '传感器年度校准服务', type: WorkOrderType.AFTER_SALES, description: '客户购买了50支ISS-EC传感器，需要年度校准', status: WorkOrderStatus.PENDING, priority: WorkOrderPriority.MEDIUM, customerName: '某能源公司', customerPhone: '137****9012', assignedStaffUuid: '', assignedStaffName: '', resolution: '', createdAt: '2024-03-13 11:20', updatedAt: '2024-03-13 11:20' },
  { workOrderUuid: '4', title: '火灾报警系统联动测试', type: WorkOrderType.AFTER_SALES, description: '新安装的ISF-S探测器需要做联动测试验收', status: WorkOrderStatus.COMPLETED, priority: WorkOrderPriority.LOW, customerName: '某化工园区', customerPhone: '136****3456', assignedStaffUuid: '4', assignedStaffName: '赵工', resolution: '已完成联动测试，所有点位正常，客户确认签字', createdAt: '2024-03-10 08:00', updatedAt: '2024-03-12 16:30' },
  { workOrderUuid: '5', title: '便携式检测仪电池更换', type: WorkOrderType.AFTER_SALES, description: 'IS-100便携式检测仪电池续航下降，需更换电池组', status: WorkOrderStatus.PENDING, priority: WorkOrderPriority.LOW, customerName: '某天然气公司', customerPhone: '135****7890', assignedStaffUuid: '', assignedStaffName: '', resolution: '', createdAt: '2024-03-12 15:45', updatedAt: '2024-03-12 15:45' }
])

export function assignStaffToWorkOrder(workOrderUuid: string, staffUuid: string, staffName: string) {
  const wo = workOrders.find((w) => w.workOrderUuid === workOrderUuid)
  const staff = staffList.find((s) => s.staffUuid === staffUuid)
  if (wo && staff) {
    wo.assignedStaffUuid = staffUuid
    wo.assignedStaffName = staffName
    wo.status = WorkOrderStatus.IN_PROGRESS
    wo.updatedAt = new Date().toLocaleString()
    staff.status = StaffStatus.WORKING
  }
}

export function assignStaffToMessage(messageUuid: string, staffUuid: string, staffName: string) {
  const msg = messages.find((m) => m.messageUuid === messageUuid)
  const staff = staffList.find((s) => s.staffUuid === staffUuid)
  if (msg && staff) {
    msg.assignedStaffUuid = staffUuid
    msg.assignedStaffName = staffName
    msg.status = MessageStatus.IN_PROGRESS
    staff.status = StaffStatus.WORKING
  }
}
