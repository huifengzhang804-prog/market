<script setup lang="ts">
import { ref, PropType, computed } from 'vue'
import AreaChoose from '@/components/q-area-choose/area-choose.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import uuid from '@/utils/uuid'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const GD_regionData = useGDRegionDataStore().getterGDRegionData

interface ChooseAreaItem {
    upperCode: string
    upperName: string
    length: number
    lowerName: string[]
    lowerCode: string[]
}

const $props = defineProps({
    tableData: {
        type: Array as PropType<
            {
                id: string
                firstAmount: number
                firstQuantity: string
                logisticsId: number
                regionJson: any[]
                secondAmount: number
                secondQuantity: string
                valuationModel: string
            }[]
        >,
        // eslint-disable-next-line vue/require-valid-default-prop
        default: [
            {
                id: uuid(10),
                firstAmount: 0, //第一个金额
                firstQuantity: '0', //第一数量
                logisticsId: 0, //物流编号
                regionJson: [], // 地区
                secondAmount: 0, // 第二个金额
                secondQuantity: '0', // 第二个数量
                valuationModel: 'PKGS', // 计价模式
            },
        ],
    },
    isPKGS: { type: Boolean, required: true },
})
const emit = defineEmits(['update:tableData'])
const _tableData = useVModel($props, 'tableData', emit)
const freeDialogTag = ref(false)
// 记录选择区域操作下标
const currentId = ref('0')

const handleAddRegion = (id: string) => {
    freeDialogTag.value = true
    currentId.value = id
}

/**
 * 区域选择回调
 */
const handleChangeArea = (e: ChooseAreaItem[]) => {
    _tableData.value.find((item) => item.id === currentId.value).regionJson = e
    // _tableData.value[currentId.value].regionJson = e
}
/**
 * 获取选中类名
 */
const getAreaName = (data: ChooseAreaItem[]) => {
    return data
        .map((item) => {
            if (item.lowerName.length === item.length) {
                return item.upperName
            }
            return `${item.upperName}(${item.lowerName.length}/${item.length})`
        })
        .join(',')
}
/**
 * @description:增加 / 删除 某一项基本物流基本信息
 * @param {*} index
 * @returns {*}
 */
const HandleFirstTabItem = (index: number, row) => {
    if (index === -1) {
        const item = {
            id: uuid(10),
            firstAmount: 0, //第一个金额
            firstQuantity: 0, //第一数量
            logisticsId: 0, //物流编号
            regionJson: [], // 地区
            secondAmount: 0, // 第二个金额
            secondQuantity: 0, // 第二个数量
            valuationModel: 'PKGS', // 计价模式
        }
        _tableData.value.push(item)
    } else {
        if (index === 0) return ElMessage.error('至少保留一个配送区域')
        _tableData.value = _tableData.value.filter((item) => item.id !== row.id)
    }
}

// 在计算属性之前添加类型定义
interface RegionData {
    label: string
    value: string
    children?: RegionData[]
    isCheck?: boolean
    isDisable?: boolean
}

const checkboxData = computed(() => {
    const currentItem = _tableData.value.find((item) => item.id === currentId.value)
    if (!currentItem) {
        return GD_regionData
    } else {
        console.time('处理选中区域')

        // 使用Map存储区域信息，避免重复查找
        const upperNameMap = new Map<string, boolean>()
        const lowerNameMap = new Map<string, boolean>()

        // 首先收集所有非当前行选中的区域
        const otherRegionJson = _tableData.value
            .filter((item) => item.id !== currentId.value)
            .map((item) => item.regionJson)
            .flat(1)

        // 预处理其他行选中的区域信息，只遍历一次
        otherRegionJson.forEach((region) => {
            upperNameMap.set(region.upperName, true)
            region.lowerName.forEach((name: string) => lowerNameMap.set(name, true))
        })

        // 预处理当前行选中的区域信息
        const currentUpperMap = new Map<string, boolean>()
        const currentLowerMap = new Map<string, boolean>()
        currentItem.regionJson.forEach((region) => {
            currentUpperMap.set(region.upperName, true)
            region.lowerName.forEach((name: string) => currentLowerMap.set(name, true))
        })

        // 使用结构化克隆创建GD_regionData的深拷贝，避免修改原始数据
        const regionDataCopy = JSON.parse(JSON.stringify(GD_regionData)) as RegionData[]

        // 一次遍历处理所有区域，避免多层嵌套循环
        regionDataCopy.forEach((item: RegionData) => {
            // 使用Map查找，性能更好
            item.isCheck = currentUpperMap.has(item.label)
            item.isDisable = upperNameMap.has(item.label)

            if (item.children) {
                item.children.forEach((i: RegionData) => {
                    i.isCheck = currentLowerMap.has(i.label)
                    i.isDisable = lowerNameMap.has(i.label)
                })
            }
        })

        console.timeEnd('处理选中区域')
        return regionDataCopy
    }
})
</script>

<template>
    <el-table :cell-style="{ padding: '15px 0' }" :data="_tableData" :header-cell-style="{ fontSize: '14px', color: '#333' }">
        <el-table-column prop="regionJson" label="选择区域" width="300">
            <template #default="{ row }">
                <div v-if="row.regionJson.length" @click="handleAddRegion(row.id)">{{ getAreaName(row.regionJson) }}</div>
                <el-button type="primary" link @click="handleAddRegion(row.id)">
                    {{ row.regionJson.length ? '编辑' : '添加区域' }}
                </el-button>
            </template>
        </el-table-column>
        <el-table-column prop="firstQuantity" :label="$props.isPKGS ? '首件数（件）' : '首重（kg）'" align="center" width="120">
            <template #default="{ row }">
                <el-input-number
                    v-model="row.firstQuantity"
                    class="number-input"
                    :controls="false"
                    :precision="$props.isPKGS ? 0 : 1"
                    :step="0.1"
                    :min="0"
                    :input-style="{ textAlign: 'center' }"
                />
            </template>
        </el-table-column>
        <el-table-column prop="firstAmount" label="首费（元）" align="center" width="120">
            <template #default="{ row }">
                <el-input-number
                    v-model="row.firstAmount"
                    class="number-input"
                    :controls="false"
                    :precision="2"
                    :step="0.1"
                    :min="0"
                    :input-style="{ textAlign: 'center' }"
                />
            </template>
        </el-table-column>
        <el-table-column prop="secondQuantity" :label="$props.isPKGS ? '续件数（件）' : '续重（kg）'" align="center" width="120">
            <template #default="{ row }">
                <el-input-number
                    v-model="row.secondQuantity"
                    class="number-input"
                    :controls="false"
                    :precision="$props.isPKGS ? 0 : 1"
                    :step="0.1"
                    :min="0"
                    :input-style="{ textAlign: 'center' }"
                />
            </template>
        </el-table-column>
        <el-table-column prop="secondAmount" label="续费（元）" align="center" width="120">
            <template #default="{ row }">
                <el-input-number
                    v-model="row.secondAmount"
                    class="number-input"
                    :controls="false"
                    :precision="2"
                    :step="0.1"
                    :min="0"
                    :input-style="{ textAlign: 'center' }"
                />
            </template>
        </el-table-column>
        <el-table-column prop="name" label="操作" align="center" width="120" fixed="right">
            <template #default="{ row, $index }">
                <div class="dialogTab_container__right_btn">
                    <el-button v-if="_tableData.length - 1 === $index" type="primary" link @click="HandleFirstTabItem(-1, row)">添加</el-button>
                    <el-button type="danger" :disabled="_tableData.length === 1" link @click="HandleFirstTabItem(_tableData.length - 1, row)"
                        >删除</el-button
                    >
                </div>
            </template>
        </el-table-column>
    </el-table>
    <AreaChoose v-model:show="freeDialogTag" :checkboxData="checkboxData" @change="handleChangeArea" />
</template>

<style scoped lang="scss">
@include b(number-input) {
    width: 80px;
}
@include b(dialogTab_container) {
    @include e(input) {
        text-align: center;
    }
    @include e(right_btn) {
        display: flex;
        justify-content: space-evenly;
    }
}
</style>
