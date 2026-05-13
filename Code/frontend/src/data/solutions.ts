export interface SolutionData {
  id: string
  name: string
  icon: string
  description: string
  detail: {
    title: string
    description: string
    features: string[]
    stats: { projects: string; clients: string }
  }
}

export const solutions: SolutionData[] = [
  {
    id: 'petrochemical',
    name: '石油化工',
    icon: '⛽',
    detail: {
      title: '石油化工行业解决方案',
      description: '针对炼油厂、化工厂等高危场所，提供可燃气体、有毒气体综合监测系统。包括储罐区气体监测、装置区密闭监测、装卸区安全监测、中控室集中管理等完整方案。',
      features: [
        '全厂区气体监测覆盖，支持1000+检测点位',
        '防爆设计符合GB/T 3836标准',
        '智能联动排风与切断阀系统',
        '实时数据上传云端管理平台',
        '多级报警联动，支持声光报警+短信+APP推送',
        '7x24小时在线监测与运维服务'
      ],
      stats: { projects: '500+', clients: '200+' }
    },
    description: '针对炼油厂、化工厂等高危场所，提供可燃气体、有毒气体综合监测系统，确保生产安全。'
  },
  {
    id: 'metallurgy',
    name: '冶金钢铁',
    icon: '🏭',
    detail: {
      title: '冶金钢铁行业解决方案',
      description: '为高炉、转炉、焦化、烧结等工艺环节提供全面的气体安全监测方案。采用耐高温传感器和抗干扰设计，适应冶金行业高温、多尘的恶劣环境。',
      features: [
        '高温环境传感器，最高可适应120℃环境',
        'CO、煤气泄漏精准检测与定位',
        '多级联动报警与应急机制',
        '移动端APP实时监控与巡检管理',
        '粉尘环境下高可靠性传感器设计',
        '对接企业MES/ERP系统'
      ],
      stats: { projects: '300+', clients: '150+' }
    },
    description: '为高炉、转炉、焦化等工艺环节提供CO、煤气等有毒可燃气体检测方案。'
  },
  {
    id: 'energy',
    name: '电力能源',
    icon: '⚡',
    detail: {
      title: '电力能源行业解决方案',
      description: '为发电厂、变电站、储能电站提供SF6、氢气、甲烷等气体泄漏监测方案。结合物联网技术，实现电力场景的无人值守与远程运维。',
      features: [
        'SF6在线监测与泄漏报警系统',
        '储能电站氢气/可燃气体安全方案',
        '电缆沟与电缆层气体监测',
        '远程运维管理与智能巡检',
        '对接SCADA与集中监控系统',
        '防雷击与电磁兼容设计'
      ],
      stats: { projects: '400+', clients: '180+' }
    },
    description: '为发电厂、变电站、储能电站提供SF6、氢气等气体泄漏监测解决方案。'
  },
  {
    id: 'gas',
    name: '市政燃气',
    icon: '🔥',
    detail: {
      title: '市政燃气行业解决方案',
      description: '为城市燃气管网、调压站、阀室及居民用户端提供全链路的燃气泄漏监测预警系统。支持NB-IoT无线通讯，实现大规模远程部署与集中管控。',
      features: [
        '管网巡检与地面浓度检测方案',
        '调压站与阀室在线监测系统',
        '居民/商业用户燃气报警器',
        '智慧燃气综合管理云平台',
        'NB-IoT/4G无线通讯方案',
        '管网风险评估与预警分析'
      ],
      stats: { projects: '600+', clients: '100+' }
    },
    description: '为燃气管网、调压站、用户端提供全链路燃气泄漏监测预警系统。'
  }
]

export interface SolutionPageData {
  title: string
  icon: string
  description: string
  challenges: { title: string; desc: string }[]
  solution: { title: string; desc: string }[]
  benefits: { title: string; desc: string; icon: string }[]
  cases: { name: string; desc: string; result: string }[]
}

export const solutionDetailMap: Record<string, SolutionPageData> = {
  petrochemical: {
    title: '石油化工行业解决方案',
    icon: '⛽',
    description: '石油化工行业是高风险行业，涉及大量易燃易爆、有毒有害气体。公司为石油化工企业提供覆盖全厂区的气体安全监测系统，从储罐区到装置区、从装卸区到中控室，实现无死角的安全防护。',
    challenges: [
      { title: '高风险环境', desc: '炼油、化工装置区域存在大量可燃气体和有毒气体泄漏风险，需要高可靠性的检测设备' },
      { title: '大面积覆盖', desc: '化工厂区面积大，需要在数千个点位部署检测设备，对系统容量和组网能力要求极高' },
      { title: '严苛环境', desc: '部分区域存在高温、高湿、腐蚀性环境，对传感器的环境适应性要求严格' },
      { title: '实时响应', desc: '气体泄漏必须秒级响应，联动消防、排风、切断等安全设施，最大限度降低事故风险' }
    ],
    solution: [
      { title: '分区分级监测', desc: '根据HAZOP分析结果，对全厂进行分区管理，不同风险等级配置不同的检测密度和响应策略' },
      { title: '防爆传感网络', desc: '全线采用本安或隔爆型传感器，支持HART/RS485/4-20mA等多种信号输出，灵活组网' },
      { title: '智能控制平台', desc: '集中管理所有探测器和报警控制器，实现统一监控、数据分析、报表生成和远程运维' },
      { title: '多级联动机制', desc: '一级报警触发声光提示，二级报警联动排风/切断/消防系统，三级报警启动全厂应急响应' }
    ],
    benefits: [
      { title: '安全合规', desc: '方案符合AQ标准及石化行业安全规范', icon: '✅' },
      { title: '降本增效', desc: '减少人工巡检成本60%，提升安全巡检效率', icon: '💰' },
      { title: '智能运维', desc: '远程诊断与预维护，降低设备故障率40%', icon: '🔧' },
      { title: '决策支持', desc: '大数据分析辅助安全管理决策', icon: '📊' }
    ],
    cases: [
      { name: '某千万吨炼化一体化项目', desc: '为新建炼化一体化项目提供全厂可燃/有毒气体监测系统', result: '覆盖2000+点位，稳定运行超5年' },
      { name: '某大型化工厂安全升级', desc: '对老旧厂区的气体监测系统进行数字化升级改造', result: '巡检效率提升3倍，误报率降低90%' }
    ]
  },
  metallurgy: {
    title: '冶金钢铁行业解决方案',
    icon: '🏭',
    description: '冶金钢铁行业生产过程中产生大量CO、煤气等有毒可燃气体。公司针对高炉、转炉、焦化等不同工艺环节的特定风险，提供定制化的气体安全监测方案。',
    challenges: [
      { title: '高温环境', desc: '高炉、热风炉等区域环境温度极高，普通传感器无法正常工作' },
      { title: '高粉尘', desc: '烧结、炼铁等工序粉尘浓度高，传感器容易被污染导致误报或失效' },
      { title: '多点位监控', desc: '冶金厂区面积大，CO和煤气管道分布广，监测点位多' }
    ],
    solution: [
      { title: '耐高温传感器', desc: '采用特殊散热结构和耐高温材料，传感器可承受最高120℃环境温度' },
      { title: '防尘设计', desc: '传感器配备防尘过滤装置和自动吹扫功能，降低粉尘对检测精度的影响' },
      { title: '煤气管网监测', desc: '沿煤气管网进行分段监测，实时检测管道泄漏点，精准定位' }
    ],
    benefits: [
      { title: '适应性强', desc: '专门针对冶金行业特殊工况设计', icon: '🏭' },
      { title: '可靠性高', desc: '抗干扰能力强，误报率行业领先', icon: '🛡️' },
      { title: '部署灵活', desc: '支持有线/无线多种组网方式', icon: '📡' }
    ],
    cases: [
      { name: '某大型钢铁集团', desc: '对全厂CO监测系统进行升级改造', result: '安全事故降低80%' }
    ]
  },
  energy: {
    title: '电力能源行业解决方案',
    icon: '⚡',
    description: '电力能源行业的智能化转型对安全监测提出了更高要求。公司为发电、输电、变电、储能等环节提供专业的气体安全监测方案。',
    challenges: [
      { title: 'SF6泄漏', desc: 'GIS设备SF6气体泄漏会导致绝缘性能下降和环境污染' },
      { title: '储能安全', desc: '锂电池储能电站存在热失控和可燃气体释放风险' },
      { title: '远程管理', desc: '变电站多为无人值守，需要远程监控和运维能力' }
    ],
    solution: [
      { title: 'SF6在线监测', desc: '高精度SF6传感器实时监测GIS设备气体状态，支持ppm级检测' },
      { title: '储能站方案', desc: '针对储能电站配置H₂、CO、VOC等多参数气体检测系统' },
      { title: '远程运维', desc: '基于物联网的远程监控平台，实现变电站无人值守管理' }
    ],
    benefits: [
      { title: '精准监测', desc: 'SF6检测精度达ppm级', icon: '🎯' },
      { title: '远程管理', desc: '支持无人值守远程运维', icon: '🌐' },
      { title: '早期预警', desc: '提前发现潜在风险隐患', icon: '⚠️' }
    ],
    cases: [
      { name: '某大型水电站', desc: '部署全站SF6在线监测系统', result: '实现GIS室无人值守，年巡检成本降低70%' }
    ]
  },
  gas: {
    title: '市政燃气行业解决方案',
    icon: '🔥',
    description: '城市燃气安全事关千家万户。公司为燃气公司提供从气源到用户终端全链路的安全监测解决方案，构建城市级的智慧燃气安全防护网。',
    challenges: [
      { title: '覆盖范围广', desc: '城市燃气管网绵延数百公里，需要大规模分布式监测' },
      { title: '环境复杂', desc: '监测点分布在城市各个角落，供电和通信条件参差不齐' },
      { title: '实时性要求高', desc: '燃气泄漏需要立即发现和处理，秒级响应至关重要' }
    ],
    solution: [
      { title: 'NB-IoT无线方案', desc: '采用NB-IoT无线通讯技术，支持大规模低功耗部署，无需布线' },
      { title: '智慧燃气平台', desc: '统一管理管网、调压站、阀室和用户端设备，实现数据可视化' },
      { title: '分级预警', desc: '根据泄漏浓度和位置进行分级预警，精准调度巡检人员' }
    ],
    benefits: [
      { title: '覆盖广', desc: '支持城市级大规模部署', icon: '🏙️' },
      { title: '功耗低', desc: '电池供电可工作3年以上', icon: '🔋' },
      { title: '易部署', desc: '即装即用，15分钟上线', icon: '⚡' }
    ],
    cases: [
      { name: '某省会城市燃气管网', desc: '建设城市级燃气管网智能监测预警平台', result: '覆盖1500km管网，上线后及时发现隐患23起' }
    ]
  }
}
