<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search @on-search-params="getSearch" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>
    <QTable :data="tableList.goods" :selection="false" class="table" no-border @change-sort="sortTableList">
        <template #noData>
            <div class="no_data">
                <img src="@/assets/images/no_shop_examine.png" alt="" />
                <p class="cont">暂无商品</p>
            </div>
        </template>
        <QTableColumn label="商品" align="left" width="320">
            <template #default="{ row }">
                <div class="commodity-info">
                    <el-image :src="row?.pic" :alt="row?.name" style="width: 68px; height: 68px; flex-shrink: 0" />
                    <div class="commodity-info__main">
                        <el-tooltip v-if="row?.name?.length >= 30" effect="dark" :content="row?.name" placement="top">
                            <span class="commodity-info__main--name cup">
                                <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                {{ row?.name }}</span
                            >
                        </el-tooltip>
                        <span v-else class="commodity-info__main--name">
                            <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ row?.name }}</span
                        >
                    </div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="销售价" width="160">
            <template #default="{ row }">
                <span>￥{{ salePriceRange(row?.salePrices) }}</span>
            </template>
        </QTableColumn>
        <QTableColumn label="状态" width="120">
            <template #default="{ row }">
                <span
                    :style="{
                        color:
                            row.auditStatus === 'ALREADY_PASSED'
                                ? '#00BB2C'
                                : row.auditStatus === 'REFUSE'
                                ? '#999'
                                : row.auditStatus === 'UNDER_REVIEW'
                                ? '#FD9224'
                                : '',
                    }"
                >
                    {{ ExamineGoodsEnum[row?.auditStatus as keyof typeof ExamineGoodsEnum] }}
                </span>
            </template>
        </QTableColumn>
        <QTableColumn label="提交时间" prop="submitTime" width="150" />
        <QTableColumn label="审核时间" prop="auditTime" width="150" />
        <QTableColumn label="操作" align="right" width="150">
            <template #default="{ row }">
                <el-link type="primary" @click="handlePreviewExamineDetails(row?.id)">查看</el-link>
                <el-link v-if="row?.auditStatus === 'REFUSE'" type="primary" @click="handleEditExamineGoods(row?.id)">编辑</el-link>
                <el-dropdown v-if="row?.auditStatus === 'REFUSE'" @command="handleCommand($event, row)">
                    <el-link type="primary">
                        <span style="flex-shrink: 0">更多</span>
                    </el-link>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="commit">提交审核</el-dropdown-item>
                            <el-dropdown-item command="reason" @click.stop>
                                <el-tooltip class="box-item" effect="dark" :content="row.explain" placement="bottom-end">
                                    <span>拒绝原因</span>
                                </el-tooltip>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </template>
        </QTableColumn>
    </QTable>
    <div v-if="tableList.total" class="pagination">
        <PageManage
            :page-size="tableList.page.size"
            :page-num="tableList.page.current"
            :total="tableList.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <el-dialog v-model="previewVisible" title="商品详情" width="900px" destroy-on-close top="5vh" center>
        <CommodityDetails :commodity-id="commodityId" />
    </el-dialog>
</template>

<script lang="ts" setup>
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import Search from '@/views/goods/examine/components/search.vue'
import useExamineListHooks from '@/views/goods/examine/hooks/useExamineListHooks'
import PageManage from '@/components/PageManage.vue'
import CommodityDetails from '@/views/goods/details/index.vue'
import usePreviewExamineDetails from './hooks/usePreviewExamineDetails'

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
    sortTableList,
    handleSizeChange,
    handleCurrentChange,
} = useExamineListHooks()

const { commodityId, previewVisible, handlePreviewExamineDetails } = usePreviewExamineDetails()
</script>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(commodity-info) {
    display: flex;
    height: 83px;
    @include e(main) {
        display: flex;
        flex-direction: column;
        justify-content: center;
        margin-left: 8px;
        @include m(name) {
            word-break: break-all;
            overflow: hidden;
            width: 217px;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }
        @include m(price) {
            color: #ff7417;
            margin: 10px 0;
        }
    }
}
@include b(el-link) {
    margin-right: 8px;
}
@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}
.no_data {
    margin-top: 150px;
    img {
        width: 220px;
        height: 150px;
    }
    .cont {
        color: #737b80;
        text-align: center;
        margin-top: 50px;
    }
}
</style>
