export interface NavChild {
  name: string
  href: string
}

export interface NavItem {
  name: string
  href: string
  children?: NavChild[]
}

export interface FooterLinkItem {
  name: string
  href: string
}

export interface FooterSection {
  title: string
  href?: string
  links?: FooterLinkItem[]
}

export const navItems: NavItem[] = [
  { name: '首页', href: '/' },
  { name: '新闻中心', href: '/#news-section' },
  {
    name: '产品中心',
    href: '/products'
  },
  {
    name: '解决方案',
    href: '/solutions',
    children: [
      { name: '石油化工', href: '/solutions/petrochemical' },
      { name: '冶金钢铁', href: '/solutions/metallurgy' },
      { name: '电力能源', href: '/solutions/energy' },
      { name: '市政燃气', href: '/solutions/gas' }
    ]
  },
  {
    name: '服务支持',
    href: '/support',
    children: [
      { name: '技术支持', href: '/support' },
      { name: '下载中心', href: '/support#downloads' },
      { name: '售后服务', href: '/contact#after-sales' }
    ]
  },
  {
    name: '关于我们',
    href: '/about',
    children: [
      { name: '公司简介', href: '/about' },
      { name: '企业文化', href: '/about#values' },
      { name: '资质荣誉', href: '/about#honors' }
    ]
  },
  { name: '联系我们', href: '/contact' }
]

export const footerLinks: Record<string, FooterSection> = {
  products: {
    title: '产品中心',
    href: '/products'
  },
  solutions: {
    title: '解决方案',
    links: [
      { name: '石油化工', href: '/solutions/petrochemical' },
      { name: '冶金钢铁', href: '/solutions/metallurgy' },
      { name: '电力能源', href: '/solutions/energy' },
      { name: '市政燃气', href: '/solutions/gas' }
    ]
  },
  support: {
    title: '服务支持',
    links: [
      { name: '技术支持', href: '/support' },
      { name: '下载中心', href: '/support#downloads' },
      { name: '售后服务', href: '/contact#after-sales' }
    ]
  },
  about: {
    title: '关于我们',
    links: [
      { name: '公司简介', href: '/about' },
      { name: '服务支持', href: '/support' },
      { name: '联系我们', href: '/contact' }
    ]
  }
}
