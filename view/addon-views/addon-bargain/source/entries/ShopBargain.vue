<script setup lang="ts">
import { reactive, defineAsyncComponent, toRefs } from 'vue'
import type { TabPaneName } from 'element-plus'
import ChromeTabs from '@/components/ChromeTab.vue'

const pageData = reactive({
  activeName: 'bargainList' as 'bargainList' | 'bargainOrder',
  tabsList: [
    { label: '砍价列表', name: 'bargainList' },
    { label: '砍价订单', name: 'bargainOrder' },
  ],
})
const { activeName, tabsList } = toRefs(pageData)
const defineAsyncComponentReactive = {
  bargainList: defineAsyncComponent(() => import('../components/bargainList/BargainList.vue')),
  bargainOrder: defineAsyncComponent(() => import('../components/bargainOrder/BargainOrder.vue')),
}

const handleTabChange = (tabsListName: TabPaneName) => {
  activeName.value = tabsListName as 'bargainList' | 'bargainOrder'
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabsList" :value="activeName" @handle-tabs="handleTabChange" />
    <div class="fdc1 overh">
      <component :is="defineAsyncComponentReactive[activeName]"></component>
    </div>
  </div>
</template>

<style scoped></style>
