<script setup lang="ts">
import { ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { navItems } from '@/data/navigation'
import type { NavItem } from '@/data/navigation'

const router = useRouter()
const authStore = useAuthStore()
const { isLoggedIn, isAdmin, isStaff } = storeToRefs(authStore)

const mobileMenuOpen = ref(false)
const activeDropdown = ref<string | null>(null)
const expandedMobileGroup = ref<string | null>(null)
let hideTimer: ReturnType<typeof setTimeout> | null = null

function showDropdown(name: string) {
  if (hideTimer) {
    clearTimeout(hideTimer)
    hideTimer = null
  }
  activeDropdown.value = name
}

function hideDropdown() {
  hideTimer = setTimeout(() => {
    activeDropdown.value = null
    hideTimer = null
  }, 250)
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
  expandedMobileGroup.value = null
}

function toggleMobileGroup(name: string) {
  expandedMobileGroup.value = expandedMobileGroup.value === name ? null : name
}

function goLogin() {
  if (isLoggedIn.value) {
    router.push(isAdmin.value ? '/admin' : isStaff.value ? '/staff' : '/user')
  } else {
    router.push('/login')
  }
}

function handleNavClick(item: NavItem) {
  if (item.href.startsWith('/#')) {
    const id = item.href.slice(2)
    if (router.currentRoute.value.path !== '/') {
      router.push({ path: '/', hash: '#' + id })
      return
    }
    const el = document.getElementById(id)
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' })
    }
  } else {
    router.push(item.href)
  }
}
</script>

<template>
  <header class="front-header">
    <div class="container header-inner">
      <router-link to="/" class="logo-wrap">
        <div class="logo-icon">IS</div>
        <div class="logo-text">
          <span class="logo-name">InterSense</span>
          <span class="logo-tagline">智能气体安全专家</span>
        </div>
      </router-link>

      <nav class="desktop-nav">
        <div
          v-for="item in navItems"
          :key="item.name"
          class="nav-item"
          @mouseenter="item.children && showDropdown(item.name)"
          @mouseleave="item.children && hideDropdown()"
        >
          <a
            v-if="item.href.startsWith('/#')"
            href="javascript:void(0)"
            class="nav-link"
            :class="{ active: $route.hash === '#' + item.href.split('#')[1] }"
            @click.prevent="handleNavClick(item)"
          >
            {{ item.name }}
            <span v-if="item.children" class="dropdown-arrow">▾</span>
          </a>
          <router-link
            v-else
            :to="item.href"
            class="nav-link"
            :class="{ active: $route.path === item.href || ($route.path.startsWith(item.href) && item.href !== '/') }"
          >
            {{ item.name }}
            <span v-if="item.children" class="dropdown-arrow">▾</span>
          </router-link>

          <div
            v-if="item.children && activeDropdown === item.name"
            class="dropdown-menu"
            @mouseenter="showDropdown(item.name)"
            @mouseleave="hideDropdown()"
          >
            <router-link
              v-for="child in item.children"
              :key="child.name"
              :to="child.href"
              class="dropdown-item"
            >
              {{ child.name }}
            </router-link>
          </div>
        </div>
      </nav>

      <div class="header-actions">
        <button class="btn-login" @click="goLogin">
          {{ isLoggedIn ? '用户中心' : '登录 / 注册' }}
        </button>
      </div>

      <button class="mobile-toggle" @click="mobileMenuOpen = !mobileMenuOpen">
        <span v-if="!mobileMenuOpen">☰</span>
        <span v-else>✕</span>
      </button>
    </div>

    <div v-if="mobileMenuOpen" class="mobile-menu">
      <div v-for="item in navItems" :key="item.name" class="mobile-nav-group">
        <div
          class="mobile-nav-link"
          @click="item.children ? toggleMobileGroup(item.name) : (closeMobileMenu(), handleNavClick(item))"
        >
          <span>{{ item.name }}</span>
          <span v-if="item.children" class="mobile-arrow">{{ expandedMobileGroup === item.name ? '▴' : '▾' }}</span>
        </div>
        <div v-if="item.children && expandedMobileGroup === item.name" class="mobile-subnav">
          <router-link
            v-for="child in item.children"
            :key="child.name"
            :to="child.href"
            class="mobile-sub-link"
            @click="closeMobileMenu"
          >
            {{ child.name }}
          </router-link>
        </div>
      </div>
      <div class="mobile-login-wrap">
        <button class="btn-login btn-login-mobile" @click="goLogin">
          {{ isLoggedIn ? '用户中心' : '登录 / 注册' }}
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.front-header {
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-inner {
  display: flex;
  align-items: center;
  height: 72px;
}

.container {
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 32px;
}

.logo-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  flex-shrink: 0;
  margin-right: 48px;
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: #3b82f6;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 17px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-name {
  font-size: 19px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.3px;
}

.logo-tagline {
  font-size: 11px;
  color: #9ca3af;
  letter-spacing: 1px;
}

.desktop-nav {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 500;
  color: #1f2937;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.25s ease;
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.active {
  background: var(--color-brand-dark);
  color: #ffffff;
}

.dropdown-arrow {
  font-size: 10px;
  margin-top: 1px;
  transition: transform 0.2s;
}

.nav-link:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  min-width: 180px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 14px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 4px 12px rgba(0, 0, 0, 0.04);
  padding: 10px 8px;
  animation: dropdownFadeIn 0.35s ease;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-12px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.dropdown-item {
  display: block;
  padding: 11px 18px;
  font-size: 14px;
  color: #374151;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.18s ease;
  white-space: nowrap;
}

.dropdown-item:hover {
  background: var(--color-brand-dark);
  color: #ffffff;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 32px;
}

.btn-login {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  background: var(--color-brand-dark);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.btn-login:hover {
  background: var(--color-brand-deeper);
  box-shadow: 0 4px 16px rgba(26, 54, 93, 0.3);
}

.mobile-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 26px;
  cursor: pointer;
  color: #1f2937;
  padding: 8px;
  line-height: 1;
}

.mobile-menu {
  display: none;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  padding: 8px 24px 24px;
  max-height: calc(100vh - 72px);
  overflow-y: auto;
}

.mobile-nav-group {
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 0;
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  cursor: pointer;
}

.mobile-arrow {
  font-size: 12px;
  color: #9ca3af;
}

.mobile-subnav {
  padding: 4px 0 12px 20px;
}

.mobile-sub-link {
  display: block;
  padding: 10px 0;
  font-size: 14px;
  color: #6b7280;
  text-decoration: none;
}

.mobile-login-wrap {
  padding-top: 20px;
}

.btn-login-mobile {
  width: 100%;
}

@media (max-width: 1200px) {
  .desktop-nav {
    gap: 2px;
  }
  .nav-link {
    padding: 10px 13px;
    font-size: 14px;
  }
}

@media (max-width: 1024px) {
  .desktop-nav,
  .header-actions {
    display: none;
  }
  .mobile-toggle {
    display: block;
  }
  .mobile-menu {
    display: block;
  }
  .logo-wrap {
    margin-right: 0;
  }
}

@media (max-width: 768px) {
  .header-inner {
    height: 64px;
  }
  .container {
    padding: 0 20px;
  }
}
</style>
