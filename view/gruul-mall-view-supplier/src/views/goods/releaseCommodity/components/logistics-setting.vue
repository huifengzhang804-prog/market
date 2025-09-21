<script setup lang="ts">
import { ref, PropType, computed, watch } from 'vue'
import type { ApiFreightTemplate, Region, LogisticsBaseModelVos } from '@/views/freight/components/types'

const $props = defineProps({
    freightTemplateId: { type: String, required: true },
    data: { type: Array as PropType<ApiFreightTemplate[]>, default: () => [] },
})

const currentData = computed(() => {
    if (!$props.data.length) return []
    const res = $props.data.find((item) => item.id === $props.freightTemplateId)
    if (!res) return []
    return res.logisticsBaseModelVos
})
const valuationModel = computed(() => {
    if (!$props.data.length) return 'PKGS'
    const res = $props.data.find((item) => item.id === $props.freightTemplateId)
    if (!res) return 'PKGS'
    return res.valuationModel
})

/**
 * @description:地区回显
 * @param {*} data
 * @returns {*}
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
        :data="currentData"
        border
        style="width: 80%; margin-top: 20px"
        :cell-style="{ fontSize: '14px', color: '#333', height: '80px' }"
        :header-cell-style="{ fontSize: '14px', color: '#515151', height: '80px', fontWeight: 'normal' }"
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
        <el-table-column min-width="70%" :label="valuationModel === 'PKGS' ? '续件数（件）' : '续重量(kg)'">
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

<style scoped lang="scss"></style>
