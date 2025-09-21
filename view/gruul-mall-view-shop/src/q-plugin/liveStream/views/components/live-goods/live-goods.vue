<script setup lang="ts">
import HeadSearch from '@/q-plugin/liveStream/views/components/live-goods/head-search.vue'
import AddGoods from '@/q-plugin/liveStream/views/components/live-goods/AddGoods.vue'
import PageManage from '@/components/PageManage.vue'
import { doGetGoodsAdd, doPutResubmitAudit, doGetGoodsInfo, doPutGoodsResetAudit, doDeldeleteGoodsInfos } from '@/q-plugin/liveStream/apis/goods'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GOODS_TYPE_CN } from '@/q-plugin/liveStream/views/components/live-goods'
import type { ApiGoods } from '@/q-plugin/liveStream/views/components/live-goods/types'

const { divTenThousand } = useConvert()
const searchParams = ref({
    keywords: '',
})
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
// 可编辑
const goodsList = ref<ApiGoods[]>([])
const chooseList = ref<ApiGoods[]>([])
const addGoodsShow = ref(false)
// 查看商品
const currentGoodsInfo = ref<ApiGoods>({
    auditId: '',
    auditStatus: 'APPROVED',
    createTime: '',
    coverImgUrl: '',
    goodsId: '',
    id: '',
    ossImgUrl: '',
    price: '',
    priceType: 1,
    productId: '',
    productName: '',
    shopId: '',
    updateTime: '',
    price2: '',
    url: '',
    verifyTime: '',
})

const goodsStatusHandler = {
    UNAPPROVED: {
        name: '提交审核',
        title: '查看商品',
        handler: async (row: ApiGoods, type: string) => {
            if (type === '查看商品') {
                const res = await lookGoodsInfo(row.goodsId)
                if (res) {
                    currentGoodsInfo.value = res as ApiGoods
                    addGoodsShow.value = true
                }
            }
            if (type === '提交审核') {
                const { code, data } = await doPutResubmitAudit(row.goodsId)
                if (code !== 200) {
                    ElMessage.error('提交失败')
                    return
                }
                ElMessage.success('提交成功')
                const currentGoods = goodsList.value.find((item) => item.id === row.id)
                if (currentGoods) {
                    currentGoods.auditStatus = 'UNDER_REVIEW'
                }
                return
            }
        },
    },
    UNDER_REVIEW: {
        name: '撤销审核',
        title: '查看商品',
        handler: async (row: ApiGoods, type: string) => {
            if (type === '查看商品') {
                const res = await lookGoodsInfo(row.goodsId)
                if (res) {
                    currentGoodsInfo.value = res as ApiGoods
                    addGoodsShow.value = true
                }
            }
            if (type === '撤销审核') {
                console.log('撤销审核')
                const { code, data, msg } = await doPutGoodsResetAudit(row.goodsId, row.auditId)
                if (code !== 200) {
                    ElMessage.error(msg || '撤销审核失败')
                    return
                }
                ElMessage.success('撤销成功')
                const currentGoods = goodsList.value.find((item) => item.id === row.id)
                if (currentGoods) {
                    currentGoods.auditStatus = 'UNAPPROVED'
                }
            }
        },
    },
    APPROVED: {
        name: '查看商品',
        title: '查看商品',
        handler: async (row: ApiGoods, type: string) => {
            if (type === '查看商品') {
                const res = await lookGoodsInfo(row.goodsId)
                if (res) {
                    currentGoodsInfo.value = res as ApiGoods
                    addGoodsShow.value = true
                }
            }
        },
    },
    FAILED_APPROVED: {
        name: '提交审核',
        title: '修改',
        handler: async (row: ApiGoods, type: string) => {
            if (type === '提交审核') {
                const { code, data } = await doPutResubmitAudit(row.goodsId)
                if (code !== 200) {
                    ElMessage.error('提交失败')
                    return
                }
                ElMessage.success('提交成功')
                const currentGoods = goodsList.value.find((item) => item.id === row.id)
                if (currentGoods) {
                    currentGoods.auditStatus = 'UNDER_REVIEW'
                }
                return
            }
            if (type === '修改') {
                // 修改
                const res = await lookGoodsInfo(row.goodsId)
                if (res) {
                    currentGoodsInfo.value = res as ApiGoods
                    addGoodsShow.value = true
                }
                return
            }
        },
    },
    VIOLATION__OFF_SHELF: {
        name: '查看商品',
        title: '',
        handler: async (row: ApiGoods, type: string) => {
            if (type === '查看商品') {
                const res = await lookGoodsInfo(row.goodsId)
                if (res) {
                    currentGoodsInfo.value = res as ApiGoods
                    addGoodsShow.value = true
                }
            }
        },
    },
}

// 处理选择变化
function handleSelectionChange(selection: ApiGoods[]) {
    chooseList.value = selection
}

async function lookGoodsInfo(goodsId: string) {
    const { code, data } = await doGetGoodsInfo(goodsId)
    if (code !== 200) {
        ElMessage.error('获取商品信息失败')
        return false
    }
    return data
}
const handleUpdateList = () => {}
const handleBatchDel = async () => {
    if (!chooseList.value.length) {
        ElMessage.info('请选择需要删除的商品')
        return
    }
    const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    if (!isValidate) return
    const ids = chooseList.value.map((item) => item.goodsId)
    const { code, data } = await doDeldeleteGoodsInfos(ids)
    if (code !== 200) {
        ElMessage.error('删除失败')
        return
    }
    ElMessage.success('删除成功')
    initGoodsList()
}
async function initGoodsList() {
    const params = { ...pageConfig, productName: searchParams.value.keywords }
    const { code, data } = await doGetGoodsAdd(params)
    if (code !== 200) return ElMessage.error('获取商品列表失败')
    goodsList.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
initGoodsList()

const handleDelCurrentGoods = () => {
    currentGoodsInfo.value = {
        auditId: '',
        coverImgUrl: '',
        auditStatus: 'APPROVED',
        createTime: '',
        goodsId: '',
        id: '',
        ossImgUrl: '',
        price: '',
        priceType: 1,
        productId: '',
        productName: '',
        shopId: '',
        updateTime: '',
        price2: '',
        url: '',
        verifyTime: '',
    }
}

const handleDelClick = async (row: ApiGoods) => {
    try {
        const isValidate = await ElMessageBox.confirm('确定删除该商品?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        if (!isValidate) return
        const { code, data } = await doDeldeleteGoodsInfos([row.goodsId])
        if (code !== 200) {
            ElMessage.error('删除失败')
            return
        }
        ElMessage.success('删除成功')
        goodsList.value = goodsList.value.filter((item) => item.id !== row.id)
        pageConfig.total--
    } catch (error) {
        console.error(error)
    }
}

const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initGoodsList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initGoodsList()
}
</script>

<template>
    <HeadSearch v-model="searchParams" @search="initGoodsList" />
    <div class="handle_container">
        <el-button round type="primary" @click="addGoodsShow = true">新增商品</el-button>
        <el-button plain @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="goodsList"
            :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="30" />
            <el-table-column label="商品信息" width="360">
                <template #default="{ row }: { row: ApiGoods }">
                    <div class="goods">
                        <el-image style="width: 68px; height: 68px" :src="row.ossImgUrl" />
                        <div class="goods__info">
                            <div class="goods__info--name">{{ row.productName }}</div>
                            <div v-if="row.priceType === 1" class="goods__info--price">{{ row.price && divTenThousand(row.price) }}</div>
                            <div v-else-if="row.priceType === 2" class="goods__info--price">
                                {{ row.price && divTenThousand(row.price) }}
                                ~ ￥{{ row.price2 && divTenThousand(row.price2) }}
                            </div>
                            <div v-else class="goods__info--price">
                                市场价：{{ row.price && divTenThousand(row.price) }} 现价：￥{{ row.price2 && divTenThousand(row.price2) }}
                            </div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="审核状态">
                <template #default="{ row }: { row: ApiGoods }">
                    <span
                        :style="{
                            color:
                                row.auditStatus === 'FAILED_APPROVED'
                                    ? '#999999'
                                    : row.auditStatus === 'APPROVED'
                                    ? '#00BB2C'
                                    : row.auditStatus === 'VIOLATION__OFF_SHELF'
                                    ? '#F54319'
                                    : '',
                        }"
                        >{{ GOODS_TYPE_CN[row.auditStatus] }}</span
                    >
                </template>
            </el-table-column>
            <el-table-column label="添加时间" align="center">
                <template #default="{ row }">
                    <div>{{ row.createTime }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="date" label="提交审核时间" align="center">
                <template #default="{ row }">
                    <span>{{ row.verifyTime }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="操作" fixed="right" width="250" align="center">
                <template #default="{ row }: { row: ApiGoods }">
                    <el-row justify="end" style="width: 100%">
                        <el-link
                            v-if="['FAILED_APPROVED', 'UNAPPROVED', 'UNDER_REVIEW', 'VIOLATION__OFF_SHELF'].includes(row.auditStatus)"
                            style="padding: 0 5px"
                            :underline="false"
                            type="primary"
                            size="small"
                            @click="goodsStatusHandler[row.auditStatus].handler(row, goodsStatusHandler[row.auditStatus].name)"
                        >
                            {{ goodsStatusHandler[row.auditStatus].name }}
                        </el-link>
                        <el-link
                            v-if="!['VIOLATION__OFF_SHELF', 'UNDER_REVIEW'].includes(row.auditStatus)"
                            style="padding: 0 5px"
                            :underline="false"
                            type="primary"
                            size="small"
                            @click="goodsStatusHandler[row.auditStatus].handler(row, goodsStatusHandler[row.auditStatus].title)"
                        >
                            {{ goodsStatusHandler[row.auditStatus].title }}
                        </el-link>
                        <el-link
                            style="padding: 0 5px"
                            :underline="false"
                            type="primary"
                            :disabled="['UNDER_REVIEW'].includes(row.auditStatus)"
                            size="small"
                            @click="handleDelClick(row)"
                        >
                            删除
                        </el-link>
                    </el-row>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <!-- 好用的分页器 -->
    <PageManage
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        :page-num="pageConfig.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <AddGoods
        v-model="addGoodsShow"
        :current-goods-value="currentGoodsInfo"
        @update-list="initGoodsList"
        @submit="handleUpdateList"
        @del-current-goods="handleDelCurrentGoods"
    />
</template>

<style scoped lang="scss">
* {
    font-size: 14px;
}
@include b(goods) {
    width: 310px;
    display: flex;
    justify-content: flex-start;
    padding: 2px 0;
    @include e(info) {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding: 3px 0;
        margin-left: 5px;
        width: 150px;
        font-size: 12px;
        @include m(name) {
            width: 217px;
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
