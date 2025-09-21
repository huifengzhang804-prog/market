<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doGetStorageOrderDetail } from '@/apis/inventory'
import type { ApiCommodityType } from '@/views/goods/types'

interface ManagementOrderItemsType {
    productId: string
    productName: string
    pic: string
    skuStockItems: {
        productId: string
        skuId: string
        num: number
        stockType: 'UNLIMITED' | 'LIMITED'
    }[]
}

const $route = useRoute()
const countForm = ref({
    evidence: [] as string[],
    remark: '',
    storageManagementOrderItems: [] as ManagementOrderItemsType[],
    storageManagementOrderType: 'INVENTORY',

    no: '',
    inventoryArea: '',
    status: '',
    operationPhone: '',
    operationAccomplishTime: '',
})

//表格数据
const countTableData = ref<ApiCommodityType[]>([])
const statuslist = {
    WAIT_COMPLETION: '盘点中',
    COMPLETION: '已完成',
    CANCEL: '已取消',
}

initcountForm()

/**
 * 获取添加商品列表
 */
async function initcountForm() {
    const id = $route.query.id as string
    if (!id) return
    const { code, data, msg } = await doGetStorageOrderDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取数据失败')
    countForm.value = data
    organizedata(data.storageManagementOrderItems)
}
const organizedata = (storageManagementOrderItems: any) => {
    countTableData.value = storageManagementOrderItems.map((item: typeof storageManagementOrderItems) => {
        const storageSkus = item.skuStockItems.map((sku: typeof item.skuStockItems) => {
            return {
                productId: sku.productId,
                id: sku.skuId,
                countNum: Math.abs(sku.num),
                stockType: sku.stockType,
                stock: sku.stock,
                specs: sku.specs,
            }
        })
        return {
            id: item.productId,
            productName: item.productName,
            albumPics: item.pic,
            totalCount: storageSkus.reduce((pre: number, item: typeof storageSkus) => {
                return pre + item.countNum
            }, 0),
            storageSkus,
        }
    })
}
const computedSuplier = computed(() => (storageSkus?: any[]) => {
    let show = ''
    if (
        storageSkus?.every((item) => {
            return item.stockType === 'UNLIMITED'
        })
    ) {
        show = '无限库存'
    } else {
        show =
            storageSkus?.reduce((pre, cur) => {
                return pre + Number(cur.stock)
            }, 0) + ''
    }
    return show
})
</script>

<template>
    <div style="padding: 16px">
        <el-form ref="form" :model="countForm" label-width="70px" style="margin-bottom: 20px">
            <el-row>
                <el-col :span="7">
                    <el-form-item label="订单号">
                        {{ countForm.no }}
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="盘点区域">
                        {{ countForm.inventoryArea }}
                    </el-form-item>
                </el-col>
                <el-col :span="5">
                    <el-form-item label="状态">
                        {{ statuslist[countForm.status as keyof typeof statuslist] }}
                    </el-form-item>
                </el-col>

                <el-col :span="6">
                    <el-form-item label="盘点时间">
                        {{ countForm.operationAccomplishTime }}
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="相关凭证">
                        <el-image
                            v-for="item in countForm.evidence"
                            :key="item"
                            :src="item"
                            style="width: 80px; height: 80px; margin-right: 10px"
                        ></el-image>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="备注">
                        {{ countForm.remark }}
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <el-table
            :data="countTableData"
            row-key="id"
            :header-cell-style="{
                'background-color': '#F0F8FA',
                'font-weight': 'normal',
                color: '#515151',
            }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="expand">
                <template #default="{ row }">
                    <div class="batch__expand">
                        <el-table
                            :data="row.storageSkus"
                            row-key="id"
                            border
                            style="width: 800px; margin: 0 auto"
                            :header-cell-style="{
                                'background-color': '#F6F8FA',
                                'font-weight': 'normal',
                                color: '#515151',
                            }"
                        >
                            <template #empty> <ElTableEmpty /> </template>
                            <el-table-column label="商品规格">
                                <template #default="{ row: childRow }">{{ childRow?.specs?.join(',') || '单规格' }}</template>
                            </el-table-column>
                            <el-table-column label="账面库存" prop="stock" width="100">
                                <template #default="{ row: childRow }">
                                    {{ childRow?.stockType === 'UNLIMITED' ? '无限库存' : childRow.stock }}
                                </template>
                            </el-table-column>
                            <el-table-column label="实盘库存" prop="countNum" align="center">
                                <template #default="{ row: childRow }">
                                    <el-input-number v-model="childRow.countNum" disabled :min="0" :precision="0" />
                                </template>
                            </el-table-column>
                            <el-table-column label="盈亏数量" width="100">
                                <template #default="{ row: childRow }">
                                    <div v-if="childRow.countNum">
                                        {{ childRow.countNum - childRow.stock }}
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column label="盘点结果" width="100">
                                <template #default="{ row: childRow }">
                                    <div v-if="childRow.countNum">
                                        {{
                                            childRow.countNum - childRow.stock === 0
                                                ? '盘平'
                                                : childRow.countNum - childRow.stock > 0
                                                ? '盘盈'
                                                : '盘亏'
                                        }}
                                    </div>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="商品名称" width="350">
                <template #default="{ row }">
                    <div class="batch__commodity">
                        <img :src="row.albumPics.split(',')[0]" />
                        <span class="batch__commodity--name">{{ row.productName }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="账面库存">
                <template #default="{ row }">{{ computedSuplier(row?.storageSkus) }}</template>
            </el-table-column>
            <el-table-column label="实盘库存(正整数)" width="150">
                <template #default="{ row }">
                    <div v-if="row.totalCount">
                        {{ row.totalCount }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="盈亏数量" width="100">
                <template #default="{ row }">
                    <div v-if="row.totalCount">
                        {{ row.totalCount - Number(computedSuplier(row?.storageSkus)) }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="盘点结果" width="100">
                <template #default="{ row }">
                    <div v-if="row.totalCount">
                        {{
                            row.totalCount - Number(computedSuplier(row?.storageSkus)) === 0
                                ? '盘平'
                                : row.totalCount - Number(computedSuplier(row?.storageSkus)) > 0
                                ? '盘盈'
                                : '盘亏'
                        }}
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<style lang="scss" scoped>
@include b(batch) {
    @include e(commodity) {
        display: flex;
        align-items: center;
        img {
            width: 60px;
            height: 60px;
            flex-shrink: 0;
        }
        span {
            overflow: hidden;
            text-overflow: ellipsis;
            display: box;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-left: 10px;
        }
    }
    @include e(expand) {
        margin-left: 70px;
        margin-right: 70px;
        padding: 10px;
        border: 1px solid #efefef;
    }
    @include e(form) {
        display: flex;
        justify-content: flex-end;
        height: 40px;
        margin-right: 170px;
    }
}
</style>
