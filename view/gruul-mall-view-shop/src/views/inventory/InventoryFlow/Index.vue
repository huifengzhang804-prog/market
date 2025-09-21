<script setup lang="ts">
import Search, { SearchType } from './Search.vue'
import { ElMessage, TabPaneName } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import { doGetStorageFlow, doPostExportStorageFlow } from '@/apis/inventory'
import useClipboard from 'vue-clipboard3'
import inventoryDetailDescription from '@/assets/images/inventory_detail_description.png'
import InventoryDetails from './Inventory-details.vue'
const { toClipboard } = useClipboard()
const tableList = reactive({
    page: { size: 10, current: 1 },
    list: [],
    total: 0,
})
const multipleSelection = ref([])
const currentTab = ref<'' | 'WAIT_COMPLETION' | 'COMPLETION' | 'CANCEL'>('')
const searchParams = ref({
    productName: '',
    productId: '',
    Id: '',
    stockChangeType: '',
    sellType: '',
    orderNo: '',
    date: '',
    startTime: '',
    endTime: '',
    isOutbound: '' as string | boolean,
})
const stockChangeTypelist = {
    PUBLISHED_INBOUND: '发布入库',
    EDITED_INBOUND: '编辑入库',
    OVERAGE_INBOUND: '盘盈入库',
    RETURNED_INBOUND: '退货入库',
    ORDER_CANCELLED_INBOUND: '订单取消入库',
    ALLOCATION_INBOUND: '调拨入库',
    PURCHASE_INBOUND: '采购入库',
    OTHER_INBOUND: '其它入库',
    SOLD_OUTBOUND: '销售出库',
    EDITED_OUTBOUND: '编辑出库',
    SHORTAGE_OUTBOUND: '盘亏出库',
    ALLOCATION_OUTBOUND: '调拨出库',
    OTHER_OUTBOUND: '其它出库',
}
const sellTypeList = {
    PURCHASE: '采购商品',
    CONSIGNMENT: '代销商品',
    OWN: '自有商品',
}
const productTypeList = {
    REAL_PRODUCT: '实物商品',
    VIRTUAL_PRODUCT: '虚拟商品',
}

const illustrateType = ref(false)

const initList = async () => {
    const { data } = await doGetStorageFlow({
        ...searchParams.value,
        ...tableList.page,
    })
    tableList.total = +data.total
    tableList.list = data.records
}
initList()

/**
 * 处理选择变化的回调函数
 */
const handleSelectionChange = (val: any) => {
    multipleSelection.value = val
}

/**
 * 表格排序
 */
const sortTableList = () => {
    initList()
}
//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    searchParams.value.isOutbound = status as string
    initList()
}
const getSearch = (e: SearchType) => {
    tableList.page.current = 1
    if (e.date.length > 0) {
        searchParams.value.startTime = e.date[0]
        searchParams.value.endTime = e.date[1]
    }
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}

/**
 * 处理导出操作的函数。
 */
const handleExport = (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e }
    let params: any = {}
    if (multipleSelection.value?.length) {
        params.exportIds = multipleSelection.value?.map((item: any) => item.id) || []
    } else if (searchParams.value) {
        params = searchParams.value
    }
    doPostExportStorageFlow(params).then(({ code, msg }) => {
        if (code === 200) {
            ElMessage.success({ message: msg || '导出成功' })
        } else {
            ElMessage.error({ message: msg || '导出失败' })
        }
    })
}
// const copyOrderNo = async (data: string) => {
//     try {
//         await toClipboard(data)
//         ElMessage.success('复制成功')
//     } catch (e) {
//         ElMessage.error('复制失败')
//     }
// }
const handleSizeChange = (value: number) => {
    tableList.page.size = value
    initList()
}
const handleCurrentChange = (value: number) => {
    tableList.page.current = value
    initList()
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search @on-search-params="getSearch" @on-export="handleExport" />
    </el-config-provider>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="currentTab" @tab-change="handleTabClick">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="入库明细" name="false"></el-tab-pane>
            <el-tab-pane label="出库明细" name="true"></el-tab-pane>
        </el-tabs>
        <QIcon name="icon-wenhao" style="cursor: pointer" size="18px" class="last" @click="illustrateType = true" />
    </div>
    <div class="table_container">
        <el-table
            empty-text="暂无数据~"
            :data="tableList.list"
            :header-cell-style="{
                'background-color': '#F7F8FA',
                color: '#333',
                height: '48px',
            }"
            :cell-style="{
                height: '60px',
                color: '#333',
            }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="55" fixed="left" />
            <el-table-column label="单据编号" width="220">
                <template #default="{ row }">
                    <div>{{ row.id }}</div>
                </template>
            </el-table-column>
            <el-table-column label="商品" width="310">
                <template #default="{ row }">
                    <el-tooltip v-if="row.productName.length > 20" :content="row.productName" placement="top">
                        <div class="ellipsi">{{ row.productName }}</div>
                    </el-tooltip>
                    <div v-else class="ellipsi">{{ row.productName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="规格" width="310">
                <template #default="{ row }">
                    <el-tooltip v-if="row.specName?.join('、').length > 20" :content="row.specName?.join('、')" placement="top">
                        <div class="ellipsi" style="color: #666">{{ row.specName?.join('、') }}</div>
                    </el-tooltip>
                    <div v-else class="ellipsi" style="color: #666">{{ row.specName?.join('、') }}</div>
                </template>
            </el-table-column>
            <el-table-column label="出入库数" width="110">
                <template #header>
                    <div>
                        <span style="margin-right: 5px">出入库数</span>
                        <!-- <i style="font-size: 12px" class="iconfont icon-path" @click="sortTableList"></i> -->
                    </div>
                </template>
                <template #default="{ row }">
                    <div v-if="row.skuStockType === 'LIMITED'">无限库存</div>
                    <div v-else>{{ row.stockChangeNum }}</div>
                </template>
            </el-table-column>
            <el-table-column label="出入库类型" width="120">
                <template #default="{ row }">
                    <div>{{ stockChangeTypelist[row.stockChangeType as keyof typeof stockChangeTypelist] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="商品来源" width="120">
                <template #default="{ row }">
                    <div>{{ sellTypeList[row.sellType as keyof typeof sellTypeList] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="商品类型" width="120">
                <template #default="{ row }">
                    <div>{{ productTypeList[row.product?.productType as keyof typeof productTypeList] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="关联订单号" width="230">
                <template #default="{ row }">
                    <div>{{ row.orderNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="SPUID" width="180">
                <template #default="{ row }">
                    <span> {{ row.productId }}</span>
                </template>
            </el-table-column>
            <el-table-column label="SKUID" width="180">
                <template #default="{ row }">
                    <span> {{ row.skuId }}</span>
                </template>
            </el-table-column>
            <el-table-column label="出入库时间" width="160" fixed="right">
                <template #default="{ row }">
                    <div>{{ row.updateTime }}</div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <!-- 好用的分页器 -->
    <PageManage
        :page-size="tableList.page.size"
        :total="tableList.total"
        :page-num="tableList.page.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="illustrateType" title="库存明细说明" width="1000px" top="5vh" center>
        <InventoryDetails />
    </el-dialog>
</template>

<style lang="scss" scoped>
* {
    font-size: 14px;
    color: #666;
}
@include b(ellipsi) {
    width: 240px;
    @include utils-ellipsis(2);
}
.last {
    position: absolute;
    right: 24px;
    top: 25%;
    z-index: 6;
}
</style>
