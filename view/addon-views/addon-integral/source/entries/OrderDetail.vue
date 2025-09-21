<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'

type ActiveName = 'orderInfo' | 'logisticsInfo'

const tabPaneOrderDetails = [
  { label: '订单信息', name: 'orderInfo' },
  { label: '物流信息', name: 'logisticsInfo' },
]
const reactiveAsyncComponent = {
  orderInfo: defineAsyncComponent(() => import('../components/orderDetail/IntegralOrderDetail.vue')),
  logisticsInfo: defineAsyncComponent(() => import('../components/orderDetail/integral-order-logistics.vue')),
}
const activeName = ref<ActiveName>('orderInfo')
</script>

<template>
  <div class="tab_container">
    <el-tabs v-model="activeName">
      <el-tab-pane v-for="tabPaneItem in tabPaneOrderDetails" :key="tabPaneItem.label" :label="tabPaneItem.label" :name="tabPaneItem.name" />
    </el-tabs>
  </div>
  <component :is="reactiveAsyncComponent[activeName]"></component>
</template>
