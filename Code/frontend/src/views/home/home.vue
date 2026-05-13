<script setup lang="ts">
import HeroSection from './components/hero-section.vue'
import ProductPreview from './components/product-preview.vue'
import SolutionTabs from './components/solution-tabs.vue'
import AdvantageCards from './components/advantage-cards.vue'
import ClientCases from './components/client-cases.vue'
import NewsScroll from './components/news-scroll.vue'
import CtaSection from './components/cta-section.vue'
</script>

<template>
  <div class="home-page">
    <HeroSection />
    <div class="section-bridge bridge-hero-to-products"></div>
    <ProductPreview />
    <SolutionTabs />

    <div class="section-bridge bridge-to-dark"></div>

    <AdvantageCards />

    <div class="section-bridge bridge-to-light"></div>

    <ClientCases />

    <div class="section-bridge bridge-to-dark"></div>

    <NewsScroll />

    <div class="section-bridge bridge-to-cta"></div>

    <CtaSection />
  </div>
</template>

<style scoped>
.section-bridge {
  height: 64px;
  position: relative;
  z-index: 1;
}

.bridge-hero-to-products {
  background: linear-gradient(180deg, #1e293b 0%, #f0f4fa 40%, #ffffff 100%);
}

.bridge-to-dark {
  background: linear-gradient(180deg, #f0f4fa 0%, var(--color-brand-deeper) 100%);
}

.bridge-to-light {
  background: linear-gradient(180deg, var(--color-brand-dark) 0%, var(--color-gray-50) 100%);
}

.bridge-to-cta {
  background: linear-gradient(180deg, var(--color-brand-hero) 0%, var(--color-brand-dark) 100%);
}

@media (max-width: 768px) {
  .section-bridge {
    height: 48px;
  }
}
</style>
