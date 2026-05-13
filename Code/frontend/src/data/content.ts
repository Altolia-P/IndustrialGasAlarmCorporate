import { reactive } from 'vue'
import { ContentType, ContentStatus } from '@/types/content'
import type { ContentVO } from '@/types/content'
import { ProductStatus } from '@/types/product'
import type { ProductVO } from '@/types/product'

type ContentData = ContentVO & { body?: string }

export const contents = reactive<ContentData[]>([
  { contentUuid: '1', title: '石油化工行业气体监测方案', summary: '针对石油化工场景的完整气体安全监测解决方案', coverImage: '', type: ContentType.SOLUTION, categoryUuid: '1', categoryName: '解决方案', status: ContentStatus.PUBLISHED, createdAt: '2024-01-10' },
  { contentUuid: '2', title: '冶金钢铁行业安全方案', summary: '面向冶金钢铁行业的气体安全监测方案', coverImage: '', type: ContentType.SOLUTION, categoryUuid: '1', categoryName: '解决方案', status: ContentStatus.PUBLISHED, createdAt: '2024-01-12' },
  { contentUuid: 'n1', title: '公司荣获2024年度工业安全技术创新奖', summary: 'InterSense荣获2024年度工业安全技术创新奖', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n1', categoryName: '公司新闻', status: ContentStatus.PUBLISHED, createdAt: '2024-03-15' },
  { contentUuid: 'n2', title: '新版《工业气体检测报警系统》国标发布', summary: '新版国标正式发布实施', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n1', categoryName: '行业动态', status: ContentStatus.PUBLISHED, createdAt: '2024-03-10' },
  { contentUuid: 'n3', title: '新一代智能气体探测器正式发布', summary: '新一代产品正式亮相', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n2', categoryName: '产品发布', status: ContentStatus.PUBLISHED, createdAt: '2024-03-05' },
  { contentUuid: 'n4', title: 'InterSense与中石化签署战略合作协议', summary: '双方达成战略合作', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n1', categoryName: '公司新闻', status: ContentStatus.PUBLISHED, createdAt: '2024-02-28' },
  { contentUuid: 'n5', title: '工业气体安全监测市场规模持续增长', summary: '市场规模持续扩大', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n1', categoryName: '行业动态', status: ContentStatus.PUBLISHED, createdAt: '2024-02-20' },
  { contentUuid: 'n6', title: 'IS-500系列固定式多气体探测器上市', summary: '新品上市预告', coverImage: '', type: ContentType.NEWS, categoryUuid: 'n2', categoryName: '产品发布', status: ContentStatus.DRAFT, createdAt: '2024-02-10' }
])

export const adminProducts = reactive<ProductVO[]>([
  { productUuid: '1', name: 'IS-100 便携式气体检测仪', description: '单一气体检测', coverImage: '', categoryUuid: '1', categoryName: '气体检测仪', status: ProductStatus.PUBLISHED, createdAt: '2024-01-15' },
  { productUuid: '2', name: 'IS-200 复合气体检测仪', description: '四合一复合气体检测仪', coverImage: '', categoryUuid: '1', categoryName: '气体检测仪', status: ProductStatus.PUBLISHED, createdAt: '2024-01-20' },
  { productUuid: '3', name: 'IS-300 固定式气体检测器', description: '固定式在线监测', coverImage: '', categoryUuid: '1', categoryName: '气体检测仪', status: ProductStatus.DRAFT, createdAt: '2024-02-10' },
  { productUuid: '4', name: 'ISC-8 八通道控制器', description: '八通道报警控制器', coverImage: '', categoryUuid: '2', categoryName: '控制系统', status: ProductStatus.PUBLISHED, createdAt: '2024-02-15' },
  { productUuid: '5', name: 'ISS-EC 电化学传感器', description: '电化学传感器探头', coverImage: '', categoryUuid: '3', categoryName: '传感器', status: ProductStatus.PUBLISHED, createdAt: '2024-03-01' },
  { productUuid: '6', name: 'ISF-S 点型感烟探测器', description: '火灾烟雾探测', coverImage: '', categoryUuid: '4', categoryName: '火灾报警', status: ProductStatus.UNPUBLISHED, createdAt: '2024-03-05' }
])
