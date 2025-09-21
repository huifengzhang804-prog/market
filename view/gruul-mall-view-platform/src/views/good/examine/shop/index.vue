<template>
    <Search @onSearchParams="getSearch" />
    <el-tabs v-model="currentTab" class="tab_container" @tab-change="handleTabClick">
        <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
    </el-tabs>
    <div class="handle_container">
        <el-button v-if="currentTab === 'UNDER_REVIEW'" type="primary" @click="handleBatchExamine">批量审核</el-button>
    </div>
    <q-table
        v-model:checked-item="selectItems"
        :data="tableList.goods"
        class="table"
        no-border
        :selection="currentTab === 'UNDER_REVIEW' ? true : false"
        @change-sort="sortTableList"
    >
        <template #noData>
            <div class="no_data">
                <img src="@/assets/image/no_shop_examine.png" alt="" />
                <p class="cont">暂无数据</p>
            </div>
        </template>
        <q-table-column label="商品" align="left" width="300">
            <template #default="{ row }">
                <div class="commodity-info">
                    <el-image :src="row?.pic" :alt="row?.name" style="width: 68px; height: 68px; flex-shrink: 0" />
                    <div class="commodity-info__main">
                        <el-tooltip v-if="row.name?.length >= 30" effect="dark" :content="row.name" placement="top-start">
                            <span class="commodity-info__main--name">
                                <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ row?.name }}</span
                            ></el-tooltip
                        >
                        <span v-else><span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ row?.name }}</span>
                        <span class="commodity-info__main--price">￥{{ salePriceRange(row?.salePrices) }}</span>
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="所属店铺" align="left" :width="220" prop="shopName">
            <template #default="{ row }">
                <el-tooltip v-if="row.shopName.length >= 20" effect="dark" :content="row.shopName" placement="top-start" class="transparent-tooltip"
                    ><span :key="row.id" class="shop-name">{{ row.shopName }}</span></el-tooltip
                >
                <span v-if="row.shopName.length < 20" :key="row.id" class="shop-name">{{ row.shopName }}</span>
            </template>
        </q-table-column>

        <q-table-column label="状态" align="left" width="100">
            <template #default="{ row }">
                <span
                    :style="{ color: row?.auditStatus === 'UNDER_REVIEW' ? '#FD9224' : row?.auditStatus === 'ALREADY_PASSED' ? '#00BB2C' : '#999' }"
                >
                    {{ ExamineGoodsEnum[row?.auditStatus as keyof typeof ExamineGoodsEnum] }}
                </span>
            </template>
        </q-table-column>
        <q-table-column label="提交时间" align="left" prop="submitTime" width="130" />
        <q-table-column v-if="currentTab !== 'UNDER_REVIEW'" label="审核时间" align="left" prop="auditTime" width="130" />
        <q-table-column label="操作" align="right" width="140" fixed="right">
            <template #default="{ row }">
                <el-link type="primary" @click="handlePreviewExamineDetails(row)">查看</el-link>
                <el-link v-if="row?.auditStatus === 'UNDER_REVIEW'" type="primary" @click="handleAuditGoods([row], true)">通过</el-link>
                <el-link v-if="row?.auditStatus === 'UNDER_REVIEW'" type="danger" @click="handleAuditGoods([row], false)">拒绝</el-link>
                <el-link v-if="row?.auditStatus === 'REFUSE'" type="primary" @click="reasonRefusal(row)">拒绝原因</el-link>
            </template>
        </q-table-column>
    </q-table>
    <BetterPageManage
        :page-num="tableList.page.current"
        :page-size="tableList.page.size"
        :total="tableList.total"
        @handle-current-change="handleChangeCurrentChange"
        @handle-size-change="handleChangeSize"
        @reload="initList"
    />
    <el-dialog v-model="previewVisible" title="商品详情" center width="60%" top="5vh" destroy-on-close>
        <CommodityDetails :commodity-id="commodityInfo.id" :shop-id="commodityInfo.shopId" />
    </el-dialog>
    <el-dialog v-model="showAuditDialog" title="商品审核" destroy-on-close @close="handleCloseAuditDialog">
        <AuditGoods ref="auditGoodsRefs" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showAuditDialog = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmAudit('')">确认</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="visible" :show-close="false" width="500">
        <template #header="{ close, titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass" style="display: flex; align-items: center">
                    <QIcon name="icon-tishi1-copy" svg size="24" /><span style="margin-left: 5px; font-size: 16px">商品审核</span>
                </h4>
                <QIcon size="18" name="icon-guanbi1" @click="close" />
            </div>
        </template>
        <div style="width: 100%; display: flex; justify-content: center; padding: 10px 0">
            <span style="width: 80%; color: #4e5969">商品审核通过后商品将处于上架状态，确定通过审核吗？</span>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmAudit('SELL_ON')"> 确认 </el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="refuseVisible" title="商品审核" width="690">
        <el-form ref="formRef" :model="formModel">
            <el-form-item label="拒绝原因" prop="explain">
                <el-input v-model="formModel.explain" placeholder="请输入拒绝原因" :maxlength="20" show-word-limit />
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="refuseVisible = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmAudit('REFUSE', formModel.explain)"> 确认 </el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="reasonRefusalDialog" title="拒绝原因" center destroy-on-close @close="reasonRefusal">
        <div>拒绝原因：{{ reason }}</div>
    </el-dialog>
</template>

<script lang="ts" setup>
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import Search from './components/search.vue'
import useExamineListHooks from './hooks/useExamineListHooks'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import CommodityDetails from '../../components/shop/commodityDetail.vue'
import usePreviewExamineDetails from './hooks/usePreviewExamineDetails'
import AuditGoods from './components/audit-goods.vue'

const {
    getSearch,
    currentTab,
    goodsStatus,
    handleTabClick,
    tableList,
    salePriceRange,
    ExamineGoodsEnum,
    initList,
    selectItems,
    handleAuditGoods,
    showAuditDialog,
    auditGoodsRefs,
    handleConfirmAudit,
    handleCloseAuditDialog,
    handleChangeCurrentChange,
    handleChangeSize,
    reasonRefusalDialog,
    reasonRefusal,
    reason,
    sortTableList,
    handleBatchExamine,
    formModel,
    visible,
    refuseVisible,
} = useExamineListHooks()

const { commodityInfo, previewVisible, handlePreviewExamineDetails } = usePreviewExamineDetails()
</script>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}
@include b(commodity-info) {
    display: flex;
    @include e(main) {
        display: flex;
        flex-direction: column;
        margin-left: 12px;
        height: 68px;
        justify-content: space-around;
        @include m(name) {
            @include utils-ellipsis(2);
            width: 217px;
            line-height: 18px;
            color: #333;
        }
        @include m(price) {
            color: #ff7417;
        }
    }
}
@include b(el-link) {
    margin-right: 8px;
}
@include b(btns) {
    padding-bottom: 8px;
}
.my-header {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    gap: 16px;
}
</style>
<style scoped lang="scss">
.is-dark {
    background: #00000080 !important;
    border: none !important;
    z-index: 99;
}
.el-popper.is-dark .el-popper__arrow:before {
    background: linear-gradient(315deg, #00000080, #00000080 50%, transparent 50%, transparent 100%);
    border: none;
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
