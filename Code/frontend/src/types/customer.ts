export interface Customer360VO {
  phone: string
  name: string
  company: string | null
  registered: boolean
  deviceCount: number
  workOrderCount: number
  messageCount: number
  alertCount: number
  devices: import('./device').DeviceVO[]
  workOrders: import('./workorder').WorkOrderVO[]
  messages: import('./message').MessageVO[]
  recentAlerts: import('./device').AlertVO[]
}
