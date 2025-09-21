<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabsList" :value="activeName" @handle-tabs="changeTab" />
    <component :is="defineAsyncComponentReactive[activeName]"></component>
  </div>
</template>

<script lang="ts" setup>
import ChromeTabs from '@/components/ChromeTab.vue'
import { ref, defineAsyncComponent } from 'vue'

const activeName = ref<'room' | 'anchor'>('room')
const tabsList = [
  { name: 'room', label: '直播间' },
  { name: 'anchor', label: '主播管理' },
]
const changeTab = (value: any) => {
  if (value) activeName.value = value
}
const defineAsyncComponentReactive = {
  room: defineAsyncComponent(() => import('../components/room-list.vue')),
  anchor: defineAsyncComponent(() => import('../components/anchor-list.vue')),
}
</script>
