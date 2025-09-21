<script lang="ts" setup>
import { defineProps, PropType, computed } from 'vue'
import type { ApiFreightTemplate, Region } from '@/views/freight/components/types'

const props = defineProps({
  data: { type: Object as PropType<ApiFreightTemplate>, default: () => null },
})

const currentData = computed(() => {
  return (props.data && props.data.logisticsBaseModelVos) || []
})
const valuationModel = computed(() => {
  return (props.data && props.data.valuationModel) || 'PKGS'
})

/**
 * 地区回显
 */
const getAreaName = computed(() => (data: Region[]) => {
  if (data && data.length) {
    return data
      .map((item) => {
        if (item.lowerCode.length === item.length) {
          return item.upperName
        }
        return `${item.upperName}(${item.lowerCode.length}/${item.length})`
      })
      .join(',')
  }
})
</script>
<template>
  <el-table
    v-show="data"
    :cell-style="{ fontSize: '14px', color: '#333', height: '80px' }"
    :data="currentData"
    :header-cell-style="{ fontSize: '14px', color: '#515151', height: '80px', fontWeight: 'normal' }"
    border
    style="width: 80%; margin-top: 20px"
  >
    <el-table-column label="可配送区域">
      <template #default="{ row }">
        <div>{{ getAreaName(row.regionJson) }}</div>
      </template>
    </el-table-column>
    <el-table-column :label="valuationModel === 'PKGS' ? '首件数（件）' : '首重量(kg)'" min-width="80%">
      <template #default="{ row }">
        <div>{{ row.firstQuantity }}{{}}</div>
      </template>
    </el-table-column>
    <el-table-column label="首费（元）" min-width="70%">
      <template #default="{ row }">
        <div>{{ row.firstAmount }}</div>
      </template>
    </el-table-column>
    <el-table-column :label="valuationModel === 'PKGS' ? '续件数（件）' : '续重量(kg)'" min-width="70%">
      <template #default="{ row }">
        <div>{{ row.secondQuantity }}</div>
      </template>
    </el-table-column>
    <el-table-column label="续费（元）" min-width="70%">
      <template #default="{ row }">
        <div>{{ row.secondAmount }}</div>
      </template>
    </el-table-column>
  </el-table>
</template>

<style lang="scss" scoped></style>
