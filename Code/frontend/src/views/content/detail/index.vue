<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

interface SolutionPageData {
  title: string
  icon: string
  description: string
  challenges: { title: string; desc: string }[]
  solution: { title: string; desc: string }[]
  benefits: { title: string; desc: string; icon: string }[]
  cases: { name: string; desc: string; result: string }[]
}

const solutionMap: Record<string, SolutionPageData> = {
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

const solutionId = (route.params.uuid as string) || 'petrochemical'
const data = solutionMap[solutionId] || solutionMap.petrochemical

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="solution-detail-page">
    <section class="hero-section">
      <div class="container">
        <div class="hero-badge">
          <span class="badge-icon">{{ data.icon }}</span>
          行业解决方案
        </div>
        <h1 class="page-title">{{ data.title }}</h1>
        <p class="page-desc">{{ data.description }}</p>
      </div>
    </section>

    <section class="challenges-section">
      <div class="container">
        <h2 class="section-title">行业挑战</h2>
        <div class="challenges-grid">
          <div v-for="c in data.challenges" :key="c.title" class="challenge-card">
            <h3 class="challenge-name">{{ c.title }}</h3>
            <p class="challenge-desc">{{ c.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="solution-section section-gray">
      <div class="container">
        <h2 class="section-title">解决方案</h2>
        <div class="solution-steps">
          <div v-for="(s, i) in data.solution" :key="s.title" class="solution-step">
            <div class="step-number">{{ i + 1 }}</div>
            <div class="step-content">
              <h3 class="step-title">{{ s.title }}</h3>
              <p class="step-desc">{{ s.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="benefits-section">
      <div class="container">
        <h2 class="section-title">方案优势</h2>
        <div class="benefits-grid">
          <div v-for="b in data.benefits" :key="b.title" class="benefit-card">
            <span class="benefit-icon">{{ b.icon }}</span>
            <h3 class="benefit-name">{{ b.title }}</h3>
            <p class="benefit-desc">{{ b.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section v-if="data.cases.length" class="section-gray">
      <div class="container">
        <h2 class="section-title">典型案例</h2>
        <div class="cases-grid">
          <div v-for="c in data.cases" :key="c.name" class="case-card">
            <h3 class="case-name">{{ c.name }}</h3>
            <p class="case-desc">{{ c.desc }}</p>
            <div class="case-result">
              <span class="result-label">项目成果</span>
              <span class="result-value">{{ c.result }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-inner">
          <h2 class="cta-title">获取定制化行业方案</h2>
          <p class="cta-desc">留下您的需求，我们的行业专家将为您量身定制专属方案</p>
          <div class="cta-actions">
            <el-button size="large" round class="btn-white" @click="goContact">在线咨询</el-button>
            <el-button size="large" round class="btn-outline-white" @click="goContact">预约演示</el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-section {
  padding: 120px 0 64px;
  background: linear-gradient(to bottom, #f9fafb, #ffffff);
  text-align: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 50px;
  color: #3b82f6;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 24px;
}

.badge-icon {
  font-size: 18px;
}

.page-title {
  font-size: 42px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 20px;
}

.page-desc {
  font-size: 18px;
  color: #6b7280;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.8;
}

.section-gray {
  background: #f9fafb;
}

.challenges-section,
.solution-section,
.benefits-section,
.section-gray {
  padding: 80px 0;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 48px;
  text-align: center;
}

.challenges-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.challenge-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 32px;
  transition: box-shadow 0.3s;
}

.challenge-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.challenge-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
}

.challenge-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.7;
}

.solution-steps {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.solution-step {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.step-number {
  width: 48px;
  height: 48px;
  background: #3b82f6;
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}

.step-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px;
}

.step-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.7;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.benefit-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.benefit-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

.benefit-icon {
  font-size: 36px;
  display: block;
  margin-bottom: 16px;
}

.benefit-name {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
}

.benefit-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.case-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s;
}

.case-card:hover {
  background: #3b82f6;
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.15);
}

.case-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
  transition: color 0.3s;
}

.case-card:hover .case-name {
  color: #ffffff;
}

.case-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 20px;
  line-height: 1.6;
  transition: color 0.3s;
}

.case-card:hover .case-desc {
  color: rgba(255, 255, 255, 0.8);
}

.case-result {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  transition: border-color 0.3s;
}

.case-card:hover .case-result {
  border-color: rgba(255, 255, 255, 0.2);
}

.result-label {
  display: block;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 4px;
  transition: color 0.3s;
}

.case-card:hover .result-label {
  color: rgba(255, 255, 255, 0.6);
}

.result-value {
  font-size: 18px;
  font-weight: 600;
  color: #3b82f6;
  transition: color 0.3s;
}

.case-card:hover .result-value {
  color: #ffffff;
}

.cta-section {
  padding: 80px 0;
}

.cta-inner {
  background: linear-gradient(135deg, #111827, #1e293b);
  border-radius: 24px;
  padding: 64px;
  text-align: center;
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 16px;
}

.cta-desc {
  font-size: 18px;
  color: #9ca3af;
  margin: 0 0 32px;
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.btn-white {
  background: #ffffff;
  color: #111827;
}

.btn-white:hover {
  background: #f0f0f0;
}

.btn-outline-white {
  border-color: rgba(255, 255, 255, 0.5);
  color: #ffffff;
  background: transparent;
}

.btn-outline-white:hover {
  border-color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 40px;
  }
  .page-title {
    font-size: 32px;
  }
  .challenges-grid,
  .benefits-grid,
  .cases-grid {
    grid-template-columns: 1fr;
  }
  .section-title {
    font-size: 26px;
  }
  .cta-inner {
    padding: 40px 24px;
  }
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
