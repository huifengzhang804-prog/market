<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

const pages = [
  {
    label: '消费返利订单',
    name: 'REBATE_ORDER',
  },
  {
    label: '消费返利设置',
    name: 'REBATE_SET',
  },
]
const currentComponent = ref<keyof typeof defineAsyncComponentActive>('REBATE_ORDER')

const defineAsyncComponentActive = {
  REBATE_ORDER: defineAsyncComponent(() => import('../components/rebate-order.vue')),
  REBATE_SET: defineAsyncComponent(() => import('../components/rebate-set.vue')),
}

const changeTab = (value: any) => {
  if (value) currentComponent.value = value
}
</script>

<template>
  <div class="fdc1 overh">
    <ChromeTab :tab-list="pages" :value="currentComponent" @handle-tabs="changeTab" />
    <component :is="defineAsyncComponentActive[currentComponent]"></component>
  </div>
</template>
