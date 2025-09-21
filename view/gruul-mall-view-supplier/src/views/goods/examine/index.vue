<template>
    <div class="search_container">
        <Search @on-search-params="getSearch" />
    </div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" style="margin-top: 15px" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>
    <q-table :data="tableList.goods" :selection="false" no-border class="table">
        <q-table-column label="商品" align="left" width="290" fixed="left">
            <template #default="{ row }">
                <div class="commodity-info">
                    <el-image :src="row?.pic" :alt="row?.name" style="width: 68px; height: 68px; flex-shrink: 0" />
                    <div class="commodity-info__main">
                        <span class="commodity-info__main--name">
                            <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ row?.name }}</span
                        >
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="供货价" width="140">
            <template #default="{ row }">
                <span>￥{{ salePriceRange(row?.salePrices) }}</span>
            </template>
        </q-table-column>
        <q-table-column label="状态" width="100">
            <template #default="{ row }">
                <span
                    :style="{
                        color: row?.auditStatus === 'UNDER_REVIEW' ? '#FD9224' : row?.auditStatus === 'ALREADY_PASSED' ? '#00BB2C' : '#999999',
                    }"
                >
                    {{ ExamineGoodsEnum[row?.auditStatus as keyof typeof ExamineGoodsEnum] }}
                </span>
            </template>
        </q-table-column>
        <q-table-column label="提交时间" prop="submitTime" width="150" />
        <q-table-column label="审核时间" prop="auditTime" width="150" />
        <q-table-column label="操作" width="120" align="right" fixed="right">
            <template #default="{ row }">
                <el-link type="primary" @click="handlePreviewExamineDetails(row)">查看</el-link>
                <el-link v-if="row?.auditStatus === 'REFUSE'" type="primary" @click="handleEditExamineGoods(row?.id)">编辑</el-link>
                <el-link v-else type="primary" @click="handleCopyExamineGoods(row?.id)">复制</el-link>
                <el-dropdown v-if="row?.auditStatus === 'REFUSE'" @command="handleCommand($event, row)">
                    <el-link type="primary">
                        <span style="flex-shrink: 0">更多</span>
                    </el-link>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="commit">提交审核</el-dropdown-item>
                            <el-dropdown-item command="reason">拒绝原因</el-dropdown-item>
                            <el-dropdown-item command="copy">复制</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </template>
        </q-table-column>
    </q-table>
    <PageManage
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="previewVisible" title="商品详情" center width="900px" top="6vh" destroy-on-close>
        <CommodityDetails :commodity-id="commodityInfo.id" :shop-id="commodityInfo.shopId" />
    </el-dialog>
    <el-dialog v-model="showReasonDialog" title="拒绝理由" center destroy-on-close>
        <span>原因：{{ rejectReson }}</span>
    </el-dialog>
</template>

<script lang="ts" setup>
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import Search from './components/search.vue'
import useExamineListHooks from './hooks/useExamineListHooks'
import CommodityDetails from '../details/index.vue'
import usePreviewExamineDetails from './hooks/usePreviewExamineDetails'
import PageManage from '@/components/PageManage/index.vue'

const {
    getSearch,
    currentTab,
    goodsStatus,
    handleTabClick,
    tableList,
    salePriceRange,
    ExamineGoodsEnum,
    initList,
    handleCopyExamineGoods,
    handleEditExamineGoods,
    handleCommand,
    showReasonDialog,
    rejectReson,
    handleSizeChange,
    handleCurrentChange,
} = useExamineListHooks()

const { commodityInfo, previewVisible, handlePreviewExamineDetails } = usePreviewExamineDetails()
</script>

<style lang="scss" scoped>
@include b(commodity-info) {
    display: flex;
    height: 82px;
    align-items: center;
    @include e(main) {
        height: 68px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        margin-left: 8px;
        @include m(name) {
            max-width: 217px;
            word-break: break-all;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }
        @include m(price) {
            color: #ff7417;
        }
    }
}
@include b(el-link) {
    margin-right: 8px;
}
@include b(table) {
    overflow: auto;
    transition: height 0.5s;
    margin-top: -16px;
}
</style>
