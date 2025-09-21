<script setup lang="ts">
import { reactive, ref } from 'vue'
import Search, { SearchType } from './Search.vue'
import { ElMessage, TabPaneName } from 'element-plus'
import PageManage from '@/components/PageManage/index.vue'
import { doGetStorageFlow, doPostExportStorageFlow } from '@/apis/inventory'
import DescriptionDialog from './description-dialog.vue'

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
    isOutbound: '' as string | TabPaneName,
})
const stockChangeTypelist = {
    PUBLISHED_INBOUND: '发布入库',
    EDITED_INBOUND: '编辑入库',
    OVERAGE_INBOUND: '盘盈入库',
    RETURNED_INBOUND: '退货入库',
    ORDER_CANCELLED_INBOUND: '订单取消入库',
    ALLOCATION_INBOUND: '调拨入库',
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
    if (!data) return
    tableList.total = +data.total
    tableList.list = data.records
}
initList()
//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    searchParams.value.isOutbound = status
    initList()
}

/**
 * 处理选择事件的函数。
 */
const handleSelectionChange = (val: any) => {
    multipleSelection.value = val
}
const getSearch = (e: SearchType) => {
    tableList.page.current = 1
    if (e.date.length > 0) {
        searchParams.value.startTime = e.date[0]
        searchParams.value.endTime = e.date[1]
    }
    e.date = ''
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}

/**
 * 处理数据导出的函数
 */
const handleExportData = async (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e }
    let params: any = {}
    if (multipleSelection.value?.length) {
        params.exportIds = multipleSelection.value?.map((item: any) => item.id) || []
    } else {
        params = searchParams.value
    }
    const { code, msg } = await doPostExportStorageFlow(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}

// 分页器
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
    <div class="search_container">
        <Search @on-search-params="getSearch" @on-export-data="handleExportData" />
    </div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" @tab-change="handleTabClick">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="入库明细" name="false"></el-tab-pane>
            <el-tab-pane label="出库明细" name="true"></el-tab-pane>
        </el-tabs>
        <QIcon name="icon-wenhao" class="explain" siez="20px" color="#333" style="cursor: pointer" @click="illustrateType = true" />
    </div>
    <div class="table_container">
        <el-table
            empty-text="暂无数据~"
            :data="tableList.list"
            style="width: 100%"
            :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
            :header-row-style="{ fontSize: '14', color: '#333' }"
            :cell-style="{ fontSize: '14', color: '#333', height: '70px' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="50" />
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
            <el-table-column label="规格" width="240">
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
            <el-table-column label="销售方式" width="120">
                <template #default="{ row }">
                    <div>{{ sellTypeList[row.sellType as keyof typeof sellTypeList] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="商品类型" width="120">
                <template #default="{ row }">
                    <div>{{ productTypeList[row.product.productType as keyof typeof productTypeList] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="关联订单号" width="230">
                <template #default="{ row }">
                    <div>{{ row.orderNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="SPUID" width="190">
                <template #default="{ row }">
                    {{ row.productId }}
                </template>
            </el-table-column>
            <el-table-column label="SKUID" width="190">
                <template #default="{ row }">
                    {{ row.skuId }}
                </template>
            </el-table-column>
            <el-table-column label="出入库时间" width="160" fixed="right">
                <template #default="{ row }">
                    <div>{{ row.updateTime }}</div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        :page-size="tableList.page.size"
        :total="tableList.total"
        :page-num="tableList.page.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="illustrateType" width="1000px" title="库存明细说明" center top="5vh">
        <DescriptionDialog />
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(ellipsi) {
    width: 240px;
    @include utils-ellipsis(2);
}
.tab_container {
    position: relative;
    .explain {
        position: absolute;
        right: 24px;
        top: 30%;
        z-index: 6;
    }
}
</style>
