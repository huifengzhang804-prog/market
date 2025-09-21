<script lang="ts" setup>
import { defineProps, defineEmits, PropType, watch, computed } from 'vue'
import type { ApiFreightTemplate, Region } from '@/views/freight/components/types'

const $props = defineProps({ tableData: { type: Array as PropType<ApiFreightTemplate[]>, required: true } })
const $emit = defineEmits(['handleEditTemplate', 'handleDelTemplate'])
/**
 * 地区回显
 */
const getAreaName = (data: Region[]) => {
  if (data && data.length) {
    return data
      .map((item) => {
        if (item.lowerName.length === item.length) {
          return item.upperName
        }
        return `${item.upperName}(${item.lowerName.length}/${item.length || 0})`
      })
      .join(',')
  }
}
</script>

<template>
  <div v-for="item in $props.tableData" :key="item.id" class="Template_container__tab">
    <div class="Template_container__head">
      <div class="Template_container__head--left">{{ item.templateName }}</div>
      <div class="Template_container__head--right">
        <div class="Template_container__head--right_btn">
          <el-button link type="primary" @click="$emit('handleEditTemplate', item.id)">编辑</el-button>
          <el-button link type="danger" @click="$emit('handleDelTemplate', item.id)">删除</el-button>
        </div>
      </div>
    </div>
    <el-table
      :cell-style="{ fontSize: '14px', color: '#333', height: '80px' }"
      :data="item.logisticsBaseModelVos"
      :header-cell-style="{ fontSize: '14px', color: '#515151', height: '80px', fontWeight: 'normal' }"
    >
      <el-table-column label="可配送区域" min-width="300%" prop="da">
        <template #default="{ row }">
          <div>{{ getAreaName(row.regionJson) }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="item.valuationModel === 'PKGS' ? '首件数（件）' : '首重量(kg)'" min-width="80%" prop="date">
        <template #default="{ row }">
          <div>{{ row.firstQuantity }}</div>
        </template>
      </el-table-column>
      <el-table-column label="首费（元）" min-width="70%" prop="name">
        <template #default="{ row }">
          <div>{{ row.firstAmount }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="item.valuationModel === 'PKGS' ? '续件数（件）' : '续重量(kg)'" min-width="70%" prop="address">
        <template #default="{ row }">
          <div>{{ row.secondQuantity }}</div>
        </template>
      </el-table-column>
      <el-table-column label="续费（元）" min-width="70%" prop="addresss">
        <template #default="{ row }">
          <div>{{ row.secondAmount }}</div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
@include b(Template_container) {
  @include e(btn) {
    width: 81px;
    height: 36px;
    font-size: 12px;
    margin-bottom: 10px;
  }
  @include e(tab) {
    border: 1px solid #f2f2f2;
    border-bottom: 0;
    margin-bottom: 20px;
  }
  @include e(head) {
    height: 40px;
    display: flex;
    padding: 0 10px;
    justify-content: space-between;
    align-items: center;
    background: #f6f8fa;
    @include m(left) {
      font-size: 12px;
      font-weight: Bold;
      color: #515151;
    }
    @include m(right_btn) {
      display: flex;
      justify-content: space-evenly;
      margin-right: 15px;
      .el-button:nth-child(1) {
        margin-right: 15px;
      }
    }
  }
}
</style>
