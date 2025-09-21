<script setup lang="ts">
import { computed, reactive, ref, PropType, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { doSetPurchase } from '@/apis/good'
import { CommoditySpecTable } from '../types'

interface PurchaseSkuType {
    skuId: string
    limitType: string
    limitNum: number
}
interface PurchaseFormType {
    productId: string
    skuStocks: PurchaseSkuType[]
}

const $props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    sku: {
        type: Array as PropType<CommoditySpecTable[]>,
        default() {
            return []
        },
    },
    productId: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['close', 'update:sku'])
const modelSku = useVModel($props, 'sku', $emit)

const currentRadioChoose = ref('UNLIMITED')
const commodityNum = ref(0)
const submitForm = reactive<PurchaseFormType>({
    productId: '',
    skuStocks: [
        {
            skuId: '',
            limitType: '',
            limitNum: 0,
        },
    ],
})
const dialogVisible = computed(() => {
    return $props.show
})

watch(
    () => dialogVisible.value,
    (val) => {
        if (val) {
            currentRadioChoose.value = $props.sku[0].limitType
            commodityNum.value = $props.sku[0].limitNum
        }
    },
)

const handleCloseDialog = () => {
    submitForm.productId = ''
    submitForm.skuStocks = [
        {
            skuId: '',
            limitType: '',
            limitNum: 0,
        },
    ]
    currentRadioChoose.value = 'UNLIMITED'
    commodityNum.value = 0
    $emit('close')
}
const handleSubmit = async () => {
    submitForm.productId = $props.productId
    submitForm.skuStocks = $props.sku.map((item) => {
        return {
            skuId: item.id,
            limitType: currentRadioChoose.value,
            limitNum: currentRadioChoose.value === 'PRODUCT_LIMITED' ? commodityNum.value : item.limitNum,
        }
    })
    const { code, success } = await doSetPurchase(submitForm.productId, submitForm.skuStocks)
    if (code === 200 && success) {
        ElMessage.success('设置限购成功')
        $emit('close', 'refresh')
    }
}
const handleBatch = (e: any) => {
    if (e) {
        modelSku.value = modelSku.value.map((item) => {
            item.limitNum = e
            return item
        })
    }
}
</script>

<template>
    <el-dialog v-model="dialogVisible" title="限购设置" center @close="handleCloseDialog">
        <el-form>
            <el-row justify="space-between">
                <el-form-item label="限购类型">
                    <el-radio-group v-model="currentRadioChoose">
                        <el-radio label="UNLIMITED">不限购</el-radio>
                        <el-radio label="PRODUCT_LIMITED">商品限购</el-radio>
                        <el-radio label="SKU_LIMITED" :disabled="$props.sku.length === 1">规格限购</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item v-if="currentRadioChoose === 'SKU_LIMITED' && $props.sku.length !== 1" label="批量设置">
                    <el-input-number style="width: 100px" :controls="false" :precision="0" :min="0" :max="9999999" @change="handleBatch" />
                </el-form-item>
            </el-row>
            <el-form-item v-if="currentRadioChoose === 'PRODUCT_LIMITED'" label="数量">
                <el-input-number v-model="commodityNum" :controls="false" style="width: 90vw" :min="0" :precision="0" :max="9999999" />
            </el-form-item>
            <el-form-item v-else-if="currentRadioChoose === 'SKU_LIMITED' && $props.sku.length !== 1">
                <el-table :data="$props.sku" height="50vh">
                    <el-table-column v-if="$props.sku.some((item) => item.image)" label="规格图" align="center">
                        <template #default="{ row }">
                            <el-image v-if="row.image" style="width: 80px; height: 80px" :src="row.image"></el-image>
                        </template>
                    </el-table-column>
                    <el-table-column prop="specs" label="商品规格" align="center" />
                    <el-table-column label="限购数量" align="center">
                        <template #default="{ row }">
                            <el-input-number v-model="row.limitNum" :controls="false" :min="0" :precision="0" :max="9999999" />
                        </template>
                    </el-table-column>
                </el-table>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="$emit('close')">取消</el-button>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>
