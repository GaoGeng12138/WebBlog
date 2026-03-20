<template>
  <div class="app-shell" :data-season="season.key">
    <div v-if="seasonPreviewLabel" class="season-preview-badge">
      {{ seasonPreviewLabel }}
    </div>

    <div class="atmosphere-layer" aria-hidden="true">
      <div class="atmosphere-layer__mist atmosphere-layer__mist--left"></div>
      <div class="atmosphere-layer__mist atmosphere-layer__mist--right"></div>
      <div class="atmosphere-layer__veil"></div>
      <span
        v-for="orb in season.orbs"
        :key="`orb-${season.key}-${orb.id}`"
        class="atmosphere-layer__orb"
        :style="orb.style"
      ></span>
    </div>

    <div class="season-layer" aria-hidden="true">
      <span
        v-for="particle in season.particles"
        :key="`${season.key}-${particle.id}`"
        class="season-layer__particle"
        :class="`season-layer__particle--${season.key}`"
        :style="particle.style"
      ></span>
    </div>

    <div class="app-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const siteConfig = useSiteConfigStore()
const userStore = useUserStore()

const makeStyles = (items) => items.map((item) => ({ ...item, style: Object.entries(item.styleMap).map(([key, value]) => `${key}: ${value}`).join('; ') + ';' }))

const springParticles = makeStyles([
  { id: 1, styleMap: { '--left': '4%', '--size': '24px', '--duration': '27s', '--delay': '-4s', '--drift': '18px', '--opacity': '0.62', '--rotate-start': '-18deg', '--rotate-end': '190deg' } },
  { id: 2, styleMap: { '--left': '11%', '--size': '19px', '--duration': '24s', '--delay': '-11s', '--drift': '-16px', '--opacity': '0.58', '--rotate-start': '12deg', '--rotate-end': '210deg' } },
  { id: 3, styleMap: { '--left': '19%', '--size': '22px', '--duration': '29s', '--delay': '-8s', '--drift': '20px', '--opacity': '0.64', '--rotate-start': '-22deg', '--rotate-end': '220deg' } },
  { id: 4, styleMap: { '--left': '27%', '--size': '18px', '--duration': '23s', '--delay': '-13s', '--drift': '-14px', '--opacity': '0.54', '--rotate-start': '16deg', '--rotate-end': '180deg' } },
  { id: 5, styleMap: { '--left': '36%', '--size': '23px', '--duration': '31s', '--delay': '-6s', '--drift': '24px', '--opacity': '0.61', '--rotate-start': '-12deg', '--rotate-end': '240deg' } },
  { id: 6, styleMap: { '--left': '45%', '--size': '17px', '--duration': '22s', '--delay': '-15s', '--drift': '-12px', '--opacity': '0.52', '--rotate-start': '8deg', '--rotate-end': '200deg' } },
  { id: 7, styleMap: { '--left': '54%', '--size': '24px', '--duration': '28s', '--delay': '-18s', '--drift': '14px', '--opacity': '0.66', '--rotate-start': '-26deg', '--rotate-end': '260deg' } },
  { id: 8, styleMap: { '--left': '63%', '--size': '20px', '--duration': '26s', '--delay': '-9s', '--drift': '-20px', '--opacity': '0.55', '--rotate-start': '14deg', '--rotate-end': '190deg' } },
  { id: 9, styleMap: { '--left': '72%', '--size': '21px', '--duration': '30s', '--delay': '-2s', '--drift': '22px', '--opacity': '0.6', '--rotate-start': '-10deg', '--rotate-end': '230deg' } },
  { id: 10, styleMap: { '--left': '82%', '--size': '19px', '--duration': '25s', '--delay': '-17s', '--drift': '-15px', '--opacity': '0.57', '--rotate-start': '20deg', '--rotate-end': '210deg' } },
  { id: 11, styleMap: { '--left': '89%', '--size': '23px', '--duration': '28s', '--delay': '-12s', '--drift': '18px', '--opacity': '0.63', '--rotate-start': '-16deg', '--rotate-end': '235deg' } },
  { id: 12, styleMap: { '--left': '95%', '--size': '17px', '--duration': '21s', '--delay': '-7s', '--drift': '-10px', '--opacity': '0.5', '--rotate-start': '10deg', '--rotate-end': '180deg' } }
])

const summerParticles = makeStyles([
  { id: 1, styleMap: { '--left': '5%', '--top': '18%', '--size': '15px', '--duration': '11s', '--delay': '-2s', '--rise': '14px', '--opacity': '0.84' } },
  { id: 2, styleMap: { '--left': '13%', '--top': '42%', '--size': '12px', '--duration': '9.5s', '--delay': '-5s', '--rise': '10px', '--opacity': '0.75' } },
  { id: 3, styleMap: { '--left': '23%', '--top': '24%', '--size': '16px', '--duration': '12s', '--delay': '-4s', '--rise': '12px', '--opacity': '0.88' } },
  { id: 4, styleMap: { '--left': '33%', '--top': '57%', '--size': '13px', '--duration': '10.5s', '--delay': '-8s', '--rise': '9px', '--opacity': '0.8' } },
  { id: 5, styleMap: { '--left': '44%', '--top': '16%', '--size': '17px', '--duration': '11.5s', '--delay': '-1s', '--rise': '13px', '--opacity': '0.9' } },
  { id: 6, styleMap: { '--left': '55%', '--top': '47%', '--size': '12px', '--duration': '8.8s', '--delay': '-6s', '--rise': '8px', '--opacity': '0.72' } },
  { id: 7, styleMap: { '--left': '64%', '--top': '29%', '--size': '15px', '--duration': '10.2s', '--delay': '-9s', '--rise': '11px', '--opacity': '0.79' } },
  { id: 8, styleMap: { '--left': '73%', '--top': '62%', '--size': '13px', '--duration': '9.8s', '--delay': '-3s', '--rise': '9px', '--opacity': '0.75' } },
  { id: 9, styleMap: { '--left': '83%', '--top': '21%', '--size': '16px', '--duration': '12.4s', '--delay': '-10s', '--rise': '12px', '--opacity': '0.87' } },
  { id: 10, styleMap: { '--left': '91%', '--top': '53%', '--size': '12px', '--duration': '9.2s', '--delay': '-7s', '--rise': '8px', '--opacity': '0.71' } },
  { id: 11, styleMap: { '--left': '95%', '--top': '34%', '--size': '14px', '--duration': '10.8s', '--delay': '-11s', '--rise': '11px', '--opacity': '0.77' } },
  { id: 12, styleMap: { '--left': '28%', '--top': '73%', '--size': '12px', '--duration': '11.2s', '--delay': '-6.5s', '--rise': '10px', '--opacity': '0.73' } }
])

const autumnParticles = makeStyles([
  { id: 1, styleMap: { '--left': '5%', '--size': '30px', '--duration': '25s', '--delay': '-3s', '--drift': '22px', '--opacity': '0.72', '--rotate-start': '-12deg', '--rotate-end': '170deg' } },
  { id: 2, styleMap: { '--left': '13%', '--size': '24px', '--duration': '23s', '--delay': '-9s', '--drift': '-18px', '--opacity': '0.66', '--rotate-start': '16deg', '--rotate-end': '200deg' } },
  { id: 3, styleMap: { '--left': '22%', '--size': '32px', '--duration': '29s', '--delay': '-12s', '--drift': '26px', '--opacity': '0.76', '--rotate-start': '-20deg', '--rotate-end': '240deg' } },
  { id: 4, styleMap: { '--left': '31%', '--size': '22px', '--duration': '21s', '--delay': '-6s', '--drift': '-12px', '--opacity': '0.62', '--rotate-start': '18deg', '--rotate-end': '190deg' } },
  { id: 5, styleMap: { '--left': '40%', '--size': '27px', '--duration': '27s', '--delay': '-14s', '--drift': '19px', '--opacity': '0.68', '--rotate-start': '-15deg', '--rotate-end': '215deg' } },
  { id: 6, styleMap: { '--left': '51%', '--size': '21px', '--duration': '22s', '--delay': '-2s', '--drift': '-14px', '--opacity': '0.58', '--rotate-start': '9deg', '--rotate-end': '180deg' } },
  { id: 7, styleMap: { '--left': '61%', '--size': '30px', '--duration': '28s', '--delay': '-16s', '--drift': '24px', '--opacity': '0.73', '--rotate-start': '-17deg', '--rotate-end': '245deg' } },
  { id: 8, styleMap: { '--left': '70%', '--size': '24px', '--duration': '24s', '--delay': '-7s', '--drift': '-16px', '--opacity': '0.65', '--rotate-start': '12deg', '--rotate-end': '205deg' } },
  { id: 9, styleMap: { '--left': '80%', '--size': '26px', '--duration': '26s', '--delay': '-11s', '--drift': '21px', '--opacity': '0.69', '--rotate-start': '-10deg', '--rotate-end': '228deg' } },
  { id: 10, styleMap: { '--left': '89%', '--size': '22px', '--duration': '22.5s', '--delay': '-5s', '--drift': '-13px', '--opacity': '0.61', '--rotate-start': '14deg', '--rotate-end': '194deg' } },
  { id: 11, styleMap: { '--left': '95%', '--size': '29px', '--duration': '30s', '--delay': '-18s', '--drift': '25px', '--opacity': '0.72', '--rotate-start': '-19deg', '--rotate-end': '250deg' } }
])

const winterParticles = makeStyles([
  { id: 1, styleMap: { '--left': '6%', '--size': '24px', '--duration': '28s', '--delay': '-5s', '--drift': '-18px', '--opacity': '0.66', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 2, styleMap: { '--left': '15%', '--size': '30px', '--duration': '34s', '--delay': '-18s', '--drift': '22px', '--opacity': '0.6', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 3, styleMap: { '--left': '21%', '--size': '19px', '--duration': '24s', '--delay': '-7s', '--drift': '12px', '--opacity': '0.55', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 4, styleMap: { '--left': '27%', '--size': '21px', '--duration': '26s', '--delay': '-10s', '--drift': '-16px', '--opacity': '0.61', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 5, styleMap: { '--left': '33%', '--size': '27px', '--duration': '29s', '--delay': '-15s', '--drift': '-22px', '--opacity': '0.6', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 6, styleMap: { '--left': '39%', '--size': '28px', '--duration': '31s', '--delay': '-13s', '--drift': '19px', '--opacity': '0.56', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 7, styleMap: { '--left': '45%', '--size': '18px', '--duration': '23s', '--delay': '-9s', '--drift': '-10px', '--opacity': '0.53', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 8, styleMap: { '--left': '52%', '--size': '24px', '--duration': '27s', '--delay': '-3s', '--drift': '-14px', '--opacity': '0.64', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 9, styleMap: { '--left': '58%', '--size': '20px', '--duration': '25s', '--delay': '-17s', '--drift': '11px', '--opacity': '0.55', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 10, styleMap: { '--left': '64%', '--size': '29px', '--duration': '33s', '--delay': '-20s', '--drift': '20px', '--opacity': '0.58', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 11, styleMap: { '--left': '70%', '--size': '19px', '--duration': '24s', '--delay': '-6s', '--drift': '-15px', '--opacity': '0.58', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 12, styleMap: { '--left': '76%', '--size': '20px', '--duration': '25s', '--delay': '-8s', '--drift': '-12px', '--opacity': '0.66', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 13, styleMap: { '--left': '81%', '--size': '24px', '--duration': '28s', '--delay': '-19s', '--drift': '14px', '--opacity': '0.52', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 14, styleMap: { '--left': '86%', '--size': '27px', '--duration': '30s', '--delay': '-16s', '--drift': '16px', '--opacity': '0.61', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 15, styleMap: { '--left': '90%', '--size': '18px', '--duration': '22s', '--delay': '-12s', '--drift': '-9px', '--opacity': '0.5', '--rotate-start': '0deg', '--rotate-end': '240deg' } },
  { id: 16, styleMap: { '--left': '94%', '--size': '21px', '--duration': '29s', '--delay': '-11s', '--drift': '-10px', '--opacity': '0.63', '--rotate-start': '0deg', '--rotate-end': '240deg' } }
])

const orbsBySeason = {
  spring: [
    { id: 1, style: '--left: 9%; --top: 14%; --size: 12px; --delay: 0s; --duration: 13s;' },
    { id: 2, style: '--left: 26%; --top: 20%; --size: 8px; --delay: 1.1s; --duration: 10.6s;' },
    { id: 3, style: '--left: 43%; --top: 12%; --size: 9px; --delay: 2.2s; --duration: 12.8s;' },
    { id: 4, style: '--left: 67%; --top: 17%; --size: 10px; --delay: 0.8s; --duration: 11.8s;' },
    { id: 5, style: '--left: 83%; --top: 26%; --size: 7px; --delay: 2.9s; --duration: 13.2s;' },
    { id: 6, style: '--left: 18%; --top: 62%; --size: 8px; --delay: 2.1s; --duration: 12.4s;' },
    { id: 7, style: '--left: 54%; --top: 71%; --size: 10px; --delay: 1.5s; --duration: 11.3s;' }
  ],
  summer: [
    { id: 1, style: '--left: 7%; --top: 18%; --size: 8px; --delay: 0.3s; --duration: 9.6s;' },
    { id: 2, style: '--left: 22%; --top: 31%; --size: 7px; --delay: 1.7s; --duration: 8.8s;' },
    { id: 3, style: '--left: 39%; --top: 15%; --size: 9px; --delay: 2.6s; --duration: 10.1s;' },
    { id: 4, style: '--left: 58%; --top: 27%; --size: 7px; --delay: 0.9s; --duration: 8.6s;' },
    { id: 5, style: '--left: 73%; --top: 20%; --size: 9px; --delay: 2.2s; --duration: 9.9s;' },
    { id: 6, style: '--left: 88%; --top: 34%; --size: 8px; --delay: 1.2s; --duration: 9.1s;' },
    { id: 7, style: '--left: 47%; --top: 66%; --size: 10px; --delay: 2.8s; --duration: 10.8s;' }
  ],
  autumn: [
    { id: 1, style: '--left: 8%; --top: 10%; --size: 10px; --delay: 0.2s; --duration: 11.8s;' },
    { id: 2, style: '--left: 24%; --top: 18%; --size: 7px; --delay: 1.4s; --duration: 10.4s;' },
    { id: 3, style: '--left: 41%; --top: 13%; --size: 9px; --delay: 2.3s; --duration: 12.1s;' },
    { id: 4, style: '--left: 59%; --top: 20%; --size: 8px; --delay: 0.6s; --duration: 10.9s;' },
    { id: 5, style: '--left: 76%; --top: 15%; --size: 10px; --delay: 2.5s; --duration: 12.7s;' },
    { id: 6, style: '--left: 90%; --top: 24%; --size: 7px; --delay: 1.7s; --duration: 11.3s;' },
    { id: 7, style: '--left: 33%; --top: 69%; --size: 9px; --delay: 2.1s; --duration: 12.2s;' }
  ],
  winter: [
    { id: 1, style: '--left: 7%; --top: 12%; --size: 10px; --delay: 0s; --duration: 12s;' },
    { id: 2, style: '--left: 18%; --top: 22%; --size: 6px; --delay: 1.5s; --duration: 10s;' },
    { id: 3, style: '--left: 31%; --top: 10%; --size: 8px; --delay: 2.1s; --duration: 13s;' },
    { id: 4, style: '--left: 46%; --top: 18%; --size: 5px; --delay: 0.7s; --duration: 11s;' },
    { id: 5, style: '--left: 63%; --top: 14%; --size: 9px; --delay: 2.8s; --duration: 12.5s;' },
    { id: 6, style: '--left: 79%; --top: 9%; --size: 6px; --delay: 1.2s; --duration: 10.8s;' },
    { id: 7, style: '--left: 91%; --top: 20%; --size: 8px; --delay: 3.1s; --duration: 13.5s;' },
    { id: 8, style: '--left: 12%; --top: 56%; --size: 7px; --delay: 2.5s; --duration: 12.2s;' },
    { id: 9, style: '--left: 37%; --top: 68%; --size: 9px; --delay: 0.8s; --duration: 11.6s;' },
    { id: 10, style: '--left: 72%; --top: 63%; --size: 7px; --delay: 1.9s; --duration: 12.8s;' }
  ]
}

const seasonalEffects = {
  spring: {
    key: 'spring',
    particles: springParticles,
    orbs: orbsBySeason.spring
  },
  summer: {
    key: 'summer',
    particles: summerParticles,
    orbs: orbsBySeason.summer
  },
  autumn: {
    key: 'autumn',
    particles: autumnParticles,
    orbs: orbsBySeason.autumn
  },
  winter: {
    key: 'winter',
    particles: winterParticles,
    orbs: orbsBySeason.winter
  }
}

const resolveSeasonKey = () => {
  const seasonQuery = typeof route.query.season === 'string'
    ? route.query.season.toLowerCase()
    : ''

  if (seasonQuery && seasonalEffects[seasonQuery]) {
    return seasonQuery
  }

  const month = new Date().getMonth() + 1

  if (month >= 3 && month <= 5) {
    return 'spring'
  }

  if (month >= 6 && month <= 8) {
    return 'summer'
  }

  if (month >= 9 && month <= 11) {
    return 'autumn'
  }

  return 'winter'
}

const season = computed(() => seasonalEffects[resolveSeasonKey()])
const seasonPreviewLabel = computed(() => {
  const seasonQuery = typeof route.query.season === 'string'
    ? route.query.season.toLowerCase()
    : ''

  if (!seasonQuery || !seasonalEffects[seasonQuery]) {
    return ''
  }

  const labelMap = {
    spring: '春季预览',
    summer: '夏季预览',
    autumn: '秋季预览',
    winter: '冬季预览'
  }

  return labelMap[seasonQuery]
})

watch(
  () => route.meta.title,
  (newTitle) => {
    if (newTitle) {
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - ThoughtFlow 管理`
      } else {
        document.title = `${newTitle} - ThoughtFlow`
      }
    }
  },
  { immediate: true }
)

watch(
  () => route.fullPath,
  () => {
    if (route.meta.title) {
      const newTitle = route.meta.title
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - ThoughtFlow 管理`
      } else {
        document.title = `${newTitle} - ThoughtFlow`
      }
    }
  }
)
</script>

<style scoped>
.app-shell {
  --mist-left: radial-gradient(circle, rgba(144, 179, 241, 0.24) 0%, rgba(190, 214, 245, 0.1) 48%, transparent 74%);
  --mist-right: radial-gradient(circle, rgba(177, 205, 245, 0.22) 0%, rgba(224, 234, 248, 0.1) 44%, transparent 76%);
  --veil: radial-gradient(circle, rgba(245, 238, 226, 0.32) 0%, rgba(236, 242, 251, 0.16) 38%, transparent 68%);
  --orb-color: rgba(255, 255, 255, 0.72);
  --orb-shadow: rgba(149, 184, 236, 0.2);
  --particle-color: rgba(255, 255, 255, 0.85);
  --particle-shadow-main: rgba(255, 255, 255, 0.75);
  --particle-shadow-sub: rgba(149, 184, 236, 0.24);
  --particle-layer-opacity: 0.88;
  position: relative;
  min-height: 100vh;
}

.app-shell[data-season='spring'] {
  --mist-left: radial-gradient(circle, rgba(242, 170, 197, 0.22) 0%, rgba(252, 219, 230, 0.12) 46%, transparent 74%);
  --mist-right: radial-gradient(circle, rgba(184, 226, 196, 0.22) 0%, rgba(229, 244, 210, 0.1) 44%, transparent 76%);
  --veil: radial-gradient(circle, rgba(255, 240, 226, 0.28) 0%, rgba(250, 233, 239, 0.16) 42%, transparent 70%);
  --orb-color: rgba(255, 236, 241, 0.72);
  --orb-shadow: rgba(229, 170, 190, 0.22);
  --particle-color: rgba(239, 129, 170, 0.98);
  --particle-shadow-main: rgba(255, 243, 247, 0.92);
  --particle-shadow-sub: rgba(232, 144, 179, 0.44);
  --particle-layer-opacity: 0.96;
}

.app-shell[data-season='summer'] {
  --mist-left: radial-gradient(circle, rgba(113, 196, 182, 0.2) 0%, rgba(180, 234, 216, 0.1) 46%, transparent 74%);
  --mist-right: radial-gradient(circle, rgba(250, 210, 114, 0.18) 0%, rgba(255, 236, 183, 0.08) 44%, transparent 78%);
  --veil: radial-gradient(circle, rgba(191, 242, 224, 0.22) 0%, rgba(222, 252, 245, 0.12) 40%, transparent 72%);
  --orb-color: rgba(245, 255, 208, 0.68);
  --orb-shadow: rgba(222, 199, 88, 0.26);
  --particle-color: rgba(245, 249, 171, 0.95);
  --particle-shadow-main: rgba(255, 249, 192, 0.88);
  --particle-shadow-sub: rgba(216, 191, 84, 0.34);
  --particle-layer-opacity: 0.92;
}

.app-shell[data-season='autumn'] {
  --mist-left: radial-gradient(circle, rgba(214, 127, 63, 0.22) 0%, rgba(241, 186, 138, 0.11) 46%, transparent 74%);
  --mist-right: radial-gradient(circle, rgba(194, 137, 67, 0.18) 0%, rgba(255, 221, 166, 0.1) 44%, transparent 76%);
  --veil: radial-gradient(circle, rgba(238, 201, 142, 0.24) 0%, rgba(244, 227, 197, 0.12) 42%, transparent 70%);
  --orb-color: rgba(255, 228, 186, 0.72);
  --orb-shadow: rgba(183, 103, 36, 0.24);
  --particle-color: rgba(223, 116, 42, 0.98);
  --particle-shadow-main: rgba(255, 228, 190, 0.88);
  --particle-shadow-sub: rgba(179, 86, 26, 0.42);
  --particle-layer-opacity: 0.94;
}

.app-shell[data-season='winter'] {
  --mist-left: radial-gradient(circle, rgba(144, 179, 241, 0.24) 0%, rgba(190, 214, 245, 0.1) 48%, transparent 74%);
  --mist-right: radial-gradient(circle, rgba(177, 205, 245, 0.22) 0%, rgba(224, 234, 248, 0.1) 44%, transparent 76%);
  --veil: radial-gradient(circle, rgba(245, 238, 226, 0.32) 0%, rgba(236, 242, 251, 0.16) 38%, transparent 68%);
  --orb-color: rgba(255, 255, 255, 0.72);
  --orb-shadow: rgba(149, 184, 236, 0.2);
  --particle-color: rgba(255, 255, 255, 0.9);
  --particle-shadow-main: rgba(255, 255, 255, 0.85);
  --particle-shadow-sub: rgba(149, 184, 236, 0.24);
  --particle-layer-opacity: 0.9;
}

.app-content {
  position: relative;
  z-index: 1;
}

.season-preview-badge {
  position: fixed;
  top: 5.5rem;
  right: 1.5rem;
  z-index: 5;
  padding: 0.5rem 0.9rem;
  border: 1px solid rgba(255, 255, 255, 0.56);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(14px);
  color: rgba(39, 64, 109, 0.82);
  font-size: 0.75rem;
  letter-spacing: 0.18em;
  box-shadow: 0 12px 30px rgba(128, 157, 207, 0.16);
  pointer-events: none;
}

.atmosphere-layer,
.season-layer {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.atmosphere-layer {
  z-index: 0;
}

.season-layer {
  z-index: 3;
  opacity: var(--particle-layer-opacity);
}

.atmosphere-layer__mist,
.atmosphere-layer__veil {
  position: absolute;
  border-radius: 9999px;
  filter: blur(80px);
}

.atmosphere-layer__mist--left {
  left: -10%;
  top: -4%;
  width: 44rem;
  height: 26rem;
  background: var(--mist-left);
}

.atmosphere-layer__mist--right {
  right: -8%;
  top: 20%;
  width: 36rem;
  height: 30rem;
  background: var(--mist-right);
}

.atmosphere-layer__veil {
  left: 18%;
  bottom: -12%;
  width: 42rem;
  height: 20rem;
  background: var(--veil);
}

.atmosphere-layer__orb {
  position: absolute;
  left: var(--left);
  top: var(--top);
  width: var(--size);
  height: var(--size);
  border-radius: 999px;
  background: var(--orb-color);
  box-shadow:
    0 0 10px rgba(255, 255, 255, 0.28),
    0 0 18px var(--orb-shadow);
  animation: floatPulse var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

.season-layer__particle {
  position: absolute;
  user-select: none;
  transform-origin: center;
  filter: saturate(1.08);
  will-change: transform;
}

.season-layer__particle--spring,
.season-layer__particle--autumn,
.season-layer__particle--winter {
  top: -12%;
  left: var(--left);
  width: var(--size);
  height: calc(var(--size) * 0.78);
  opacity: var(--opacity);
}

.season-layer__particle--spring {
  border-radius: 68% 32% 62% 38% / 42% 58% 42% 58%;
  background:
    radial-gradient(circle at 28% 26%, rgba(255, 244, 248, 0.96) 0 16%, rgba(255, 219, 232, 0.92) 17%, rgba(242, 157, 191, 0.92) 62%, rgba(228, 126, 171, 0.84) 100%);
  box-shadow:
    0 0 10px rgba(255, 240, 246, 0.78),
    0 0 20px rgba(232, 144, 179, 0.32);
  animation: driftFall var(--duration) linear infinite;
  animation-delay: var(--delay);
}

.season-layer__particle--summer {
  left: var(--left);
  top: var(--top);
  width: var(--size);
  height: var(--size);
  border-radius: 999px;
  color: transparent;
  background:
    radial-gradient(circle at 35% 35%, rgba(255, 255, 222, 0.98) 0 24%, rgba(255, 244, 173, 0.86) 25%, rgba(255, 237, 129, 0.36) 56%, transparent 76%);
  box-shadow:
    0 0 12px rgba(255, 250, 196, 0.86),
    0 0 28px rgba(232, 210, 95, 0.42),
    0 0 44px rgba(250, 227, 133, 0.28);
  opacity: var(--opacity);
  animation: fireflyPulse var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

.season-layer__particle--autumn {
  width: calc(var(--size) * 0.9);
  height: calc(var(--size) * 1.15);
  border-radius: 10% 70% 16% 74% / 16% 62% 38% 84%;
  background:
    radial-gradient(circle at 35% 26%, rgba(255, 232, 181, 0.96) 0 12%, rgba(241, 173, 82, 0.92) 20%, rgba(216, 112, 41, 0.94) 62%, rgba(154, 69, 23, 0.88) 100%);
  box-shadow:
    0 0 12px rgba(255, 206, 146, 0.42),
    0 0 22px rgba(179, 86, 26, 0.28);
  clip-path: polygon(50% 0%, 62% 12%, 82% 16%, 72% 36%, 100% 48%, 74% 58%, 82% 82%, 56% 72%, 48% 100%, 38% 74%, 14% 84%, 24% 60%, 0% 48%, 24% 34%, 14% 14%, 38% 18%);
  animation: driftFall var(--duration) linear infinite;
  animation-delay: var(--delay);
}

.season-layer__particle--winter {
  width: var(--size);
  height: var(--size);
  border-radius: 999px;
  background:
    radial-gradient(circle, rgba(255, 255, 255, 0.98) 0 26%, rgba(233, 244, 255, 0.92) 27%, rgba(194, 223, 250, 0.7) 58%, transparent 74%);
  box-shadow:
    0 0 10px rgba(255, 255, 255, 0.9),
    0 0 24px rgba(176, 210, 245, 0.34);
  animation: snowfallFall var(--duration) linear infinite;
  animation-delay: var(--delay);
}

.season-layer__particle--spring::before,
.season-layer__particle--autumn::before,
.season-layer__particle--winter::before,
.season-layer__particle--winter::after,
.season-layer__particle--summer::before,
.season-layer__particle--summer::after {
  content: '';
  position: absolute;
  inset: 0;
}

.season-layer__particle--spring::before {
  inset: 14% 18% 24% 22%;
  border-radius: 999px;
  background: linear-gradient(180deg, rgba(255, 247, 250, 0.94) 0%, rgba(255, 204, 225, 0.12) 100%);
  transform: rotate(18deg);
}

.season-layer__particle--autumn::before {
  inset: 10% 46% 2% 44%;
  border-radius: 999px;
  background: linear-gradient(180deg, rgba(126, 70, 31, 0.72) 0%, rgba(101, 53, 19, 0.92) 100%);
  transform: rotate(-8deg);
  transform-origin: top center;
}

.season-layer__particle--winter::before {
  inset: 46% 12% 46% 12%;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow:
    0 -8px 0 rgba(255, 255, 255, 0.72),
    0 8px 0 rgba(255, 255, 255, 0.72);
}

.season-layer__particle--winter::after {
  inset: 12% 46% 12% 46%;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow:
    6px 6px 0 rgba(255, 255, 255, 0.56),
    -6px -6px 0 rgba(255, 255, 255, 0.56),
    6px -6px 0 rgba(255, 255, 255, 0.56),
    -6px 6px 0 rgba(255, 255, 255, 0.56);
}

.season-layer__particle--summer::before {
  inset: 18% 18% 18% 18%;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(255, 248, 183, 1) 0 34%, rgba(255, 226, 95, 0.92) 35%, rgba(255, 214, 67, 0.48) 62%, transparent 78%);
  box-shadow:
    0 0 10px rgba(255, 241, 150, 0.88),
    0 0 22px rgba(255, 226, 96, 0.54);
}

.season-layer__particle--summer::after {
  inset: 34% auto auto -140%;
  width: 180%;
  height: 34%;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(255, 229, 112, 0.36) 0%, rgba(255, 229, 112, 0.16) 38%, transparent 100%);
  filter: blur(1.2px);
  transform: rotate(-12deg);
}

@keyframes floatPulse {
  0%, 100% {
    opacity: 0.38;
    transform: translateY(0) scale(0.92);
  }

  50% {
    opacity: 0.88;
    transform: translateY(-10px) scale(1.08);
  }
}

@keyframes driftFall {
  0% {
    transform: translate3d(0, -12vh, 0) rotate(var(--rotate-start)) scale(0.92);
  }

  35% {
    transform: translate3d(calc(var(--drift) * 0.45), 30vh, 0) rotate(calc((var(--rotate-start) + var(--rotate-end)) / 2)) scale(1);
  }

  65% {
    transform: translate3d(calc(var(--drift) * -0.3), 68vh, 0) rotate(calc(var(--rotate-end) * 0.82)) scale(0.97);
  }

  100% {
    transform: translate3d(var(--drift), 112vh, 0) rotate(var(--rotate-end)) scale(0.94);
  }
}

@keyframes snowfallFall {
  0% {
    transform: translate3d(0, -12vh, 0) rotate(var(--rotate-start)) scale(0.92);
  }

  50% {
    transform: translate3d(calc(var(--drift) * 0.55), 54vh, 0) rotate(calc(var(--rotate-end) / 2)) scale(1);
  }

  100% {
    transform: translate3d(var(--drift), 112vh, 0) rotate(var(--rotate-end)) scale(0.96);
  }
}

@keyframes fireflyPulse {
  0%, 100% {
    opacity: 0.18;
    transform: translate3d(0, 0, 0) scale(0.72) rotate(0deg);
  }

  25% {
    opacity: var(--opacity);
    transform: translate3d(8px, calc(var(--rise) * -0.55), 0) scale(1.02) rotate(4deg);
  }

  50% {
    opacity: 0.3;
    transform: translate3d(-6px, calc(var(--rise) * -1), 0) scale(0.82) rotate(-5deg);
  }

  75% {
    opacity: calc(var(--opacity) - 0.12);
    transform: translate3d(7px, calc(var(--rise) * -0.42), 0) scale(0.94) rotate(3deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .atmosphere-layer__orb,
  .season-layer__particle {
    animation: none;
  }

  .season-layer__particle--summer {
    opacity: 0.35;
  }

  .season-layer__particle--spring,
  .season-layer__particle--autumn,
  .season-layer__particle--winter,
  .atmosphere-layer__orb {
    opacity: 0.55;
  }

  .season-preview-badge {
    display: none;
  }
}
</style>

<style>
#nprogress .bar {
   background: linear-gradient(90deg, #7a9de6 0%, #9cb9ef 48%, #c4d8f8 100%) !important;
   box-shadow: 0 0 12px rgba(122, 157, 230, 0.24);
}
</style>
