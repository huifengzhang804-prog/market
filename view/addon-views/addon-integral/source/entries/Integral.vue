<script setup lang="ts">
import { ref, defineAsyncComponent, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import ChromeTab from '@/components/chrome-tab/index.vue'

const activeName = ref<'goods' | 'order' | 'rules'>('goods')
const $route = useRoute()
const tabPanes = [
  { label: '积分商品', name: 'goods' },
  { label: '积分订单', name: 'order' },
  { label: '积分规则', name: 'rules' },
]
const reactiveAsyncComponent = {
  goods: defineAsyncComponent(() => import('../components/integral-goods.vue')),
  order: defineAsyncComponent(() => import('../components/integral-order.vue')),
  rules: defineAsyncComponent(() => import('./IntegralRules.vue')),
}

watchEffect(() => {
  // 批量发货返回时却换到积分订单
  if ($route.query.type && $route.query.type === 'order') {
    activeName.value = $route.query.type
  }
})

const changeTab = (value: any) => {
  if (value) activeName.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTab :tab-list="tabPanes" :value="activeName" @handle-tabs="changeTab" />
    <component :is="reactiveAsyncComponent[activeName]"></component>
  </div>
</template>

<style lang="scss" scoped></style>
