<script setup lang="ts">
</script>

<template>
  <div class="front-layout">
    <header class="front-header">
      <div class="container">
        <router-link to="/" class="logo">工业气体报警</router-link>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/products">产品中心</router-link>
          <router-link to="/solutions">解决方案</router-link>
          <router-link to="/contact">联系我们</router-link>
        </nav>
      </div>
    </header>
    <main class="front-main">
      <router-view />
    </main>
    <footer class="front-footer">
      <div class="container">
        <p>© 2026 工业气体报警企业官网</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.front-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.front-header .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  text-decoration: none;
}
.nav {
  display: flex;
  gap: 32px;
}
.nav a {
  color: #333;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.nav a:hover,
.nav a.router-link-active {
  color: #409eff;
}
.front-main {
  flex: 1;
}
.front-footer {
  background: #1a1a2e;
  color: #999;
  text-align: center;
  padding: 32px 0;
}
</style>
