export interface StatItem {
  value: string
  label: string
}

export interface ProductPreviewItem {
  id: number
  name: string
  category: string
  description: string
  features: string[]
  icon: string
}

export interface SolutionTabItem {
  id: string
  name: string
  icon: string
  title: string
  description: string
  features: string[]
  projects: string
  clients: string
}

export interface AdvantageItem {
  icon: string
  title: string
  description: string
}

export interface CaseItem {
  title: string
  industry: string
  description: string
  result: string
}


export const stats: StatItem[] = [
  { value: '20+', label: '年行业经验' },
  { value: '5000+', label: '项目案例' },
  { value: '99.9%', label: '系统可靠性' }
]

export const products: ProductPreviewItem[] = [
  {
    id: 1,
    name: '便携式气体检测仪',
    category: '气体检测仪',
    description: '适用于各类工业场所的便携式多合一气体检测设备',
    features: ['四合一检测', 'IP67防护', '本安防爆'],
    icon: '🔍'
  },
  {
    id: 2,
    name: '固定式气体探测器',
    category: '气体探测器',
    description: '工业级固定安装气体探测器，7x24小时持续监测',
    features: ['红外检测', '高精度', '低功耗'],
    icon: '📡'
  },
  {
    id: 3,
    name: '气体报警控制器',
    category: '控制系统',
    description: '多通道气体报警控制主机，支持多种通讯协议',
    features: ['多通道', '联动控制', '远程监控'],
    icon: '🖥️'
  },
  {
    id: 4,
    name: '智能传感器模组',
    category: '传感器',
    description: '高性能气体传感器模组，适配各类检测场景',
    features: ['快速响应', '长寿命', '高稳定'],
    icon: '⚙️'
  }
]

export const solutionTabs: SolutionTabItem[] = [
  {
    id: 'petrochemical',
    name: '石油化工',
    icon: '⛽',
    title: '石油化工行业解决方案',
    description: '针对炼油厂、化工厂等高危场所，提供可燃气体、有毒气体综合监测系统，确保生产安全。',
    features: ['全厂区气体监测覆盖', '防爆设计符合国标', '智能联动排风系统', '实时数据上传平台'],
    projects: '500+',
    clients: '200+'
  },
  {
    id: 'metallurgy',
    name: '冶金钢铁',
    icon: '🏭',
    title: '冶金钢铁行业解决方案',
    description: '为高炉、转炉、焦化等工艺环节提供CO、煤气等有毒可燃气体检测方案。',
    features: ['高温环境适应设计', '煤气泄漏精准检测', '多级联动报警机制', '移动端实时监控'],
    projects: '300+',
    clients: '150+'
  },
  {
    id: 'energy',
    name: '电力能源',
    icon: '⚡',
    title: '电力能源行业解决方案',
    description: '为发电厂、变电站、储能电站提供SF6、氢气等气体泄漏监测解决方案。',
    features: ['SF6在线监测系统', '储能电站安全方案', '电缆沟气体检测', '远程运维管理'],
    projects: '400+',
    clients: '180+'
  },
  {
    id: 'gas',
    name: '市政燃气',
    icon: '🔥',
    title: '市政燃气行业解决方案',
    description: '为燃气管网、调压站、用户端提供全链路燃气泄漏监测预警系统。',
    features: ['管网巡检检测方案', '调压站在线监测', '居民用户报警器', '智慧燃气平台'],
    projects: '600+',
    clients: '100+'
  }
]

export const advantages: AdvantageItem[] = [
  { icon: '🏅', title: '资质认证齐全', description: '通过ISO9001、防爆认证、消防认证等多项国家级资质认证' },
  { icon: '👥', title: '专业技术团队', description: '拥有50+研发工程师，持续创新，为客户提供最优解决方案' },
  { icon: '🎧', title: '全天候服务', description: '7x24小时技术支持，全国200+服务网点，快速响应' },
  { icon: '🛡️', title: '品质保障', description: '严格的质量管控体系，产品出厂合格率99.9%以上' },
  { icon: '🔧', title: '定制化服务', description: '根据客户需求定制专属解决方案，满足特殊场景需求' },
  { icon: '📊', title: '数据驱动', description: '智能物联网平台，实现数据可视化分析与智能决策' }
]

export const clientLogos: string[] = [
  '中国石油', '中国石化', '国家电网', '宝武钢铁', '华能集团', '华电集团', '中国燃气', '新奥能源'
]

export const cases: CaseItem[] = [
  {
    title: '某大型炼化项目',
    industry: '石油化工',
    description: '为年产1000万吨炼化一体化项目提供全厂气体监测系统',
    result: '覆盖2000+检测点位'
  },
  {
    title: '某钢铁集团安全升级',
    industry: '冶金钢铁',
    description: '对老旧厂区进行智能化安全监测系统改造升级',
    result: '安全事故降低80%'
  },
  {
    title: '某城市燃气管网',
    industry: '市政燃气',
    description: '建设城市级燃气管网智能监测预警平台',
    result: '覆盖1500km管网'
  }
]
