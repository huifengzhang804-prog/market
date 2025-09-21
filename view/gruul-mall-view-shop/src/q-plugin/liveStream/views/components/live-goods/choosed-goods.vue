<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import { doGetCommodity } from '@/apis/good'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import type { CommoditySpecTable, SubmitCommodityType } from '@/views/goods/types'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default() {
            return false
        },
    },
})
const emit = defineEmits(['update:modelValue', 'chooseGoods'])
const { divTenThousand } = useConvert()
const _isShow = useVModel(props, 'modelValue', emit)
const keyword = ref('')
const tabLoading = ref(false)
const goodsList = ref<SubmitCommodityType[]>([])
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})

watch(
    () => props.modelValue,
    (val) => {
        if (val) {
            initGoodsList()
        }
    },
)

async function initGoodsList() {
    tabLoading.value = true
    const { code, data } = await doGetCommodity({ ...pageConfig, status: 'SELL_ON', name: keyword.value, shopId: useShopInfoStore().shopInfo.id })
    if (code !== 200) {
        ElMessage.error('获取商品失败')
        tabLoading.value = false
        return
    }
    goodsList.value = data.records
    pageConfig.total = data.total
    tabLoading.value = false
}
const handleSearch = () => {
    initGoodsList()
}
const goodsId = ref('')
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initGoodsList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initGoodsList()
}
const handleCancel = () => {
    reset()
}
/**
 * 价格处理展示
 * @param {*} storageSkus
 */
const formatPrice = (storageSkus: CommoditySpecTable[]) => {
    const tempArr = storageSkus.map((item) => divTenThousand(item.salePrice).toNumber())
    const min = Math.min(...tempArr)
    const max = Math.max(...tempArr)
    return max === min ? max : `${min}-${max}`
}
/**
 * 列表库存计算
 */
const handleComputeStocks = (storageSkus: CommoditySpecTable[]) => storageSkus.reduce((pre, cur) => pre + Number(cur.stock), 0)
const handleConfirm = () => {
    const goods = goodsList.value.find((item) => item.id === goodsId.value)
    emit('chooseGoods', goods ? [goods] : [])
    reset()
}
const reset = () => {
    keyword.value = ''
    pageConfig.current = 1
    pageConfig.size = 10
    pageConfig.total = 0
    goodsId.value = ''
    _isShow.value = false
}
</script>

<template>
    <el-radio-group v-model="goodsId">
        <el-dialog v-model="_isShow">
            <template #header="{ titleId, titleClass }">
                <h4 :id="titleId" :class="titleClass">选择商品</h4>
            </template>
            <div class="head-search">
                <el-input v-model="keyword" placeholder="输入关键词" style="width: 200px">
                    <template #append>
                        <el-button :icon="Search" @click="handleSearch" />
                    </template>
                </el-input>
            </div>
            <el-table v-loading="tabLoading" height="300" :data="goodsList" style="width: 100%">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column label="商品信息">
                    <template #default="{ row }">
                        <div class="goods">
                            <el-radio :value="row.id">{{ '' }}</el-radio>
                            <el-image style="width: 40px; height: 40px" :src="row.pic" />
                            <div class="goods__info">
                                <div class="goods__info--name">{{ row.name }}</div>
                                <div class="goods__info--price">{{ formatPrice(row.storageSkus) }}</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="商品库存" width="130">
                    <template #default="{ row }">
                        <div v-if="row.storageSkus.length">{{ handleComputeStocks(row.storageSkus) }}</div>
                        <!-- <div class="warning">{{ handleStockLess(row.storageSkus) }}</div> -->
                    </template>
                </el-table-column>
            </el-table>
            <!-- </el-radio-group> -->

            <el-row justify="end" align="middle">
                <!-- 好用的分页器 -->
                <page-manage
                    v-model="pageConfig"
                    :page-size="pageConfig.size"
                    :page-num="pageConfig.current"
                    :total="pageConfig.total"
                    @handle-size-change="handleSizeChange"
                    @handle-current-change="handleCurrentChange"
                />
            </el-row>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="handleCancel">取消</el-button>
                    <el-button type="primary" @click="handleConfirm"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
    </el-radio-group>
</template>

<style scoped lang="scss">
@include b(head-search) {
    text-align: right;
}
@include b(goods) {
    width: 310px;
    display: flex;
    justify-content: flex-start;
    @include e(info) {
        padding: 0 0 0 10px;
        width: 150px;
        line-height: 20px;
        color: #515151;
        font-size: 12px;
        @include m(name) {
            @include utils-ellipsis(1);
        }
        @include m(price) {
            &::before {
                content: '￥';
            }
        }
    }
}
</style>
