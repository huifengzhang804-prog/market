<script setup lang="ts">
import type { PropType } from 'vue'
import { doSetInventory } from '@/apis/good'
import { ElMessage } from 'element-plus'
import { CommoditySpecTable } from '../types'

type CusCommoditySpecTable = {
    num: number
} & CommoditySpecTable

const $props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    sku: {
        type: Array as PropType<CusCommoditySpecTable[]>,
        default() {
            return []
        },
    },
    productId: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['close'])
const dialogVisible = computed(() => {
    return $props.show
})

const batchProcessing = ref(0)

const handleCloseDialog = () => {
    batchProcessing.value = 0
    $emit('close')
}
const handleSubmit = async () => {
    const productId = $props.productId
    const skuStocks = $props.sku.map((item) => {
        return {
            skuId: item.id,
            stockType: item.stockType,
            num: item.num || 0,
        }
    })
    const { code, success } = await doSetInventory(productId, skuStocks)
    if (code === 200 && success) {
        ElMessage.success('设置库存成功')
        $emit('close', 'refresh')
    }
}

const handleChangeBatchInventory = (val: any) => {
    if (typeof val !== 'undefined') {
        $props.sku
            .filter((item) => item.stockType !== 'UNLIMITED')
            .forEach((item) => {
                if (val >= 0) {
                    item.num = val
                } else {
                    // 处理 < 0 的情况
                    item.num = val < -Number(item.stock) ? -Number(item.stock) : val
                }
            })
    }
}
</script>

<template>
    <el-dialog v-model="dialogVisible" center title="库存设置" destroy-on-close @close="handleCloseDialog">
        <el-form inline class="batch-form">
            <el-form-item label="批量增减库存">
                <el-input-number
                    v-model="batchProcessing"
                    :controls="false"
                    :precision="0"
                    :max="2000000000"
                    :min="-2000000000"
                    @change="handleChangeBatchInventory"
                />
            </el-form-item>
        </el-form>
        <el-table :data="$props.sku" height="60vh">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column v-if="$props.sku.some((item) => item.image)" label="规格图" align="center">
                <template #default="{ row }">
                    <el-image v-if="row.image" style="width: 80px; height: 80px" :src="row.image" />
                </template>
            </el-table-column>
            <el-table-column prop="specs" label="商品规格" align="center" />
            <el-table-column label="剩余库存" align="center">
                <template #default="{ row }">
                    <el-input v-if="row.stockType !== 'UNLIMITED'" v-model="row.stock" disabled maxlength="20" />
                    <div v-else>无限库存</div>
                </template>
            </el-table-column>
            <el-table-column label="增减库存" align="center">
                <template #default="{ row }">
                    <el-input-number
                        v-if="row.stockType !== 'UNLIMITED'"
                        v-model="row.num"
                        style="width: 100px"
                        :controls="false"
                        :precision="0"
                        :max="2000000000"
                        :min="Number(row.stock) === 0 ? 0 : -row.stock"
                    />
                    <div v-else></div>
                </template>
            </el-table-column>
        </el-table>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="$emit('close')">取消</el-button>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(batch-form) {
    @include flex(flex-end);
}
</style>
