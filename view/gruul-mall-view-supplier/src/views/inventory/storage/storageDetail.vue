<script setup lang="ts">
import { reactive, ref } from 'vue'
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
        stock: string
        specs: string
    }[]
}

const $route = useRoute()
const storageForm = ref({
    stockChangeType: 'ALLOCATION_INBOUND',
    evidence: [] as string[],
    remark: '',
    storageManagementOrderItems: [] as ManagementOrderItemsType[],
    storageManagementOrderType: 'IN_OUT',

    no: '',
    status: '',
    operationPhone: '',
    operationAccomplishTime: '',
    changeStockTotal: '',
})

//表格数据
const storageTableData = ref<any>([])
const statuslist = {
    WAIT_COMPLETION: '出入库中',
    COMPLETION: '已完成',
    CANCEL: '已取消',
}
const stockChangeTypelist = {
    ALLOCATION_INBOUND: '调拨入库',
    OTHER_INBOUND: '其它入库',
    ALLOCATION_OUTBOUND: '调拨出库',
    OTHER_OUTBOUND: '其它出库',
}

initstorageForm()

async function initstorageForm() {
    const id = $route.query.id as string
    if (!id) return
    const { code, data, msg } = await doGetStorageOrderDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取数据失败')
    storageForm.value = data
    organizedata(data.storageManagementOrderItems)
}
const organizedata = (storageManagementOrderItems: ManagementOrderItemsType[]) => {
    storageTableData.value = storageManagementOrderItems.map((item) => {
        const storageSkus = item.skuStockItems.map((sku) => {
            return {
                productId: sku.productId,
                id: sku.skuId,
                StorageNum: Math.abs(sku.num) || 0,
                stockType: sku.stockType,
                stock: sku.stock,
                specs: sku.specs,
            }
        })
        return {
            id: item.productId,
            productName: item.productName,
            albumPics: item.pic,
            totalStorage: storageSkus.reduce((pre: number, item) => {
                return pre + item.StorageNum
            }, 0),
            storageSkus,
        }
    })
}
const expandableTable = ref()
const handleRowClick = (row: ApiCommodityType) => {
    expandableTable.value.toggleRowExpansion(row)
}
</script>

<template>
    <div class="content_container">
        <el-form ref="form" :model="storageForm" label-width="90px" style="margin-bottom: 20px">
            <el-row>
                <el-col :span="7">
                    <el-form-item label="订单号">
                        {{ storageForm.no }}
                    </el-form-item>
                </el-col>
                <el-col :span="5">
                    <el-form-item label="状态">
                        {{ statuslist[storageForm.status as keyof typeof statuslist] }}
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="出入库时间">
                        {{ storageForm.operationAccomplishTime }}
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="总出入库数">
                        {{ storageForm.changeStockTotal }}
                    </el-form-item>
                </el-col>
                <el-col :span="6">
                    <el-form-item label="类型">
                        {{ stockChangeTypelist[storageForm.stockChangeType as keyof typeof stockChangeTypelist] }}
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="相关凭证">
                        <el-image
                            v-for="item in storageForm.evidence"
                            :key="item"
                            :src="item"
                            style="width: 80px; height: 80px; margin-right: 10px"
                        ></el-image>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="备注">
                        {{ storageForm.remark }}
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <el-table
            ref="expandableTable"
            :data="storageTableData"
            row-key="id"
            :header-cell-style="{
                'background-color': '#F0F8FA',
                'font-weight': 'normal',
                color: '#515151',
            }"
            @row-click="handleRowClick"
        >
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
                            <el-table-column label="商品规格">
                                <template #default="{ row: childRow }">{{ childRow?.specs?.join(',') || '单规格' }}</template>
                            </el-table-column>
                            <el-table-column label="出入库数" prop="StorageNum" align="center">
                                <template #default="{ row: childRow }">
                                    <el-input-number
                                        v-model="childRow.StorageNum"
                                        disabled
                                        :controls="false"
                                        :min="0"
                                        :max="
                                            storageForm.stockChangeType === 'ALLOCATION_OUTBOUND' || storageForm.stockChangeType === 'OTHER_OUTBOUND'
                                                ? Number(childRow.stock)
                                                : 99999
                                        "
                                        :precision="0"
                                    />
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="商品名称" width="350">
                <template #default="{ row }">
                    <div class="batch__commodity">
                        <img :src="row.albumPics" />
                        <span class="batch__commodity--name">{{ row.productName }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="出入库数(正整数)" prop="totalStorage" />
        </el-table>
    </div>
</template>

<style lang="scss" scoped>
@include b(content_container) {
    overflow-y: scroll;
}
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
