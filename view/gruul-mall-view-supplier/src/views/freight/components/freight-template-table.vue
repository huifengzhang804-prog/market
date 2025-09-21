<script setup lang="ts">
import { PropType, watch, computed } from 'vue'
import type { ApiFreightTemplate, Region } from '@/views/freight/components/types'

const $props = defineProps({ tableData: { type: Array as PropType<ApiFreightTemplate[]>, required: true } })
const $emit = defineEmits(['handleEditTemplate', 'handleDelTemplate'])

/**
 * @description:地区回显
 * @param {*} data
 * @returns {*}
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
                    <el-button type="primary" size="small" link @click="$emit('handleEditTemplate', item.id)">编辑</el-button>
                    <el-button type="danger" size="small" link @click="$emit('handleDelTemplate', item.id)">删除</el-button>
                </div>
            </div>
        </div>
        <el-table
            :data="item.logisticsBaseModelVos"
            :cell-style="{ fontSize: '14px', color: '#333', height: '80px' }"
            :header-cell-style="{ fontSize: '14px', color: '#515151', height: '80px', fontWeight: 'normal' }"
        >
            <el-table-column label="可配送区域" prop="da" min-width="300%">
                <template #default="{ row }">
                    <div>{{ getAreaName(row.regionJson) }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="date" :label="item.valuationModel === 'PKGS' ? '首件数（件）' : '首重量(kg)'" min-width="80%">
                <template #default="{ row }">
                    <div>{{ row.firstQuantity }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="首费（元）" min-width="70%">
                <template #default="{ row }">
                    <div>{{ row.firstAmount }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="address" min-width="70%" :label="item.valuationModel === 'PKGS' ? '续件数（件）' : '续重量(kg)'">
                <template #default="{ row }">
                    <div>{{ row.secondQuantity }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="addresss" label="续费（元）" min-width="70%">
                <template #default="{ row }">
                    <div>{{ row.secondAmount }}</div>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<style scoped lang="scss">
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
        height: 48px;
        display: flex;
        padding: 0 10px;
        justify-content: space-between;
        align-items: center;
        background: #f6f8fa;
        @include m(left) {
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
