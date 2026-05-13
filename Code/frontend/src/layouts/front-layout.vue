<script setup lang="ts">
import HeaderSection from './components/header-section.vue'
import FooterSection from './components/footer-section.vue'
</script>

<template>
  <div class="front-layout">
    <HeaderSection />

    <main class="front-main">
      <router-view />
    </main>

    <FooterSection />
  </div>
</template>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.front-main {
  flex: 1;
  margin-top: 72px;
}

@media (max-width: 768px) {
  .front-main {
    margin-top: 64px;
  }
}
</style>
