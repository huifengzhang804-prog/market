<script setup lang="ts">
import { ref, reactive } from 'vue'
import Search from './search.vue'
import RebateList from './rebate-list.vue'
import { doExportRebateOrder } from '../apis'
import { ElMessage } from 'element-plus'

const searchForm = ref<{
  orderNo: string
  productName: string
  shopName: string
  buyerNickname: string
  orderCreateTime: string
  orderEndTime: string
  clinchTime: string[] | '' | null
}>({
  orderNo: '',
  productName: '',
  shopName: '',
  buyerNickname: '',
  orderCreateTime: '',
  orderEndTime: '',
  clinchTime: [],
})
const RebateListRef = ref()
const handleSearchForm = () => {
  RebateListRef.value?.initOrderList()
}
const rebateDetailStatistic = reactive({
  totalExpired: '',
  totalPendingSettlement: '',
  totalRebate: '',
})

// 导出
const handleExport = async () => {
  let params: any = {}
  if (RebateListRef.value?.multiSelect?.length) {
    params.exportOrderNos = RebateListRef.value?.multiSelect?.map((item: any) => item.orderNo) || []
  } else {
    const { orderNo, productName, shopName, buyerNickname, orderCreateTime, orderEndTime, clinchTime } = searchForm.value
    params = { orderNo, productName, shopName, buyerNickname, orderCreateTime, orderEndTime, clinchTime }
  }
  const { code, msg } = await doExportRebateOrder(params)
  if (code === 200) {
    ElMessage.success({ message: msg || '导出成功' })
  } else {
    ElMessage.error({ message: msg || '导出失败' })
  }
}
</script>

<template>
  <Search v-model:searchForm="searchForm" @getSearchParams="handleSearchForm" @handleExport="handleExport"></Search>
  <RebateList ref="RebateListRef" v-model:statistic="rebateDetailStatistic" :searchForm="searchForm" />
</template>

<style scoped lang="scss"></style>
