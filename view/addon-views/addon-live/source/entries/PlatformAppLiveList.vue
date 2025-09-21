<template>
  <div class="q_plugin_container">
    <ChromeTab :tab-list="tabList" :value="activeName" @handle-tabs="changeTab" />
    <component :is="defineAsyncComponentReactive[activeName]"></component>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineAsyncComponent } from 'vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

const activeName = ref<'room' | 'anchor'>('room')
const defineAsyncComponentReactive = {
  room: defineAsyncComponent(() => import('../components/platform/room/index.vue')),
  anchor: defineAsyncComponent(() => import('../components/platform/anchor/index.vue')),
}
const tabList = [
  { label: '直播间', name: 'room' },
  { label: '主播管理', name: 'anchor' },
]

const changeTab = (value: any) => {
  if (value) activeName.value = value
}
</script>
