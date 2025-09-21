<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { doGetLiveGoodsList, doDelDeleteGoods } from '@/q-plugin/liveStream/apis/goods'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GOODS_TYPE_CN } from '@/q-plugin/liveStream/views/components/live-goods'
import type { ApiGoodsItem } from '@/q-plugin/liveStream/views/components/live-goods/types'

const { divTenThousand } = useConvert()
const searchParams = ref({
    type: '',
    keywords: '',
    status: '',
})
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const goodsList = ref<ApiGoodsItem[]>([])
const chooseList = ref<ApiGoodsItem[]>([])
const columns = [
    {
        label: '商品审核状态',
        prop: 'status',
        valueType: 'select',
        labelWidth: 105,
        options: [
            {
                value: '',
                label: '全部状态',
            },
            {
                value: 'UNAPPROVED',
                label: '未审核',
            },
            {
                value: 'UNDER_REVIEW',
                label: '审核中',
            },
            {
                value: 'APPROVED',
                label: '审核通过',
            },
            {
                value: 'FAILED_APPROVED',
                label: '未通过',
            },
            {
                value: 'VIOLATION__OFF_SHELF',
                label: '违规下架',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '关键字',
        prop: 'keywords',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关键字',
        },
    },
]

const handleReset = () => {
    searchParams.value = {
        type: '',
        keywords: '',
        status: '',
    }
    handleSearch()
}

watch(
    () => searchParams.value.status,
    () => {
        // 搜索数据
        initGoodsList()
    },
)

// 处理选择变化
function handleSelectionChange(selection: ApiGoodsItem[]) {
    chooseList.value = selection
}
const handleBatchDel = async () => {
    if (!chooseList.value.length) {
        ElMessage.info('请选择需要删除的直播商品')
        return
    }
    const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    if (!isValidate) return
    const ids = chooseList.value.map((item) => item.goodsId)
    const { code, data } = await doDelDeleteGoods(ids)
    if (code !== 200) {
        ElMessage.error('删除失败')
        return
    }
    ElMessage.success('删除成功')
    initGoodsList()
}
const handleSearch = () => {
    initGoodsList()
}
async function initGoodsList() {
    const { keywords, status: auditStatus } = searchParams.value
    const params = { ...pageConfig, keywords, auditStatus }
    const { code, data } = await doGetLiveGoodsList(params)
    if (code !== 200) return ElMessage.error('获取商品列表失败')
    goodsList.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
initGoodsList()
/**
 * 删除
 */
const handleDelClick = async (row: ApiGoodsItem) => {
    try {
        const isValidate = await ElMessageBox.confirm('确定删除该商品?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        if (!isValidate) return
        console.log('row.goodsId', row)
        const { code, data } = await doDelDeleteGoods([row.goodsId])
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
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initGoodsList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initGoodsList()
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchParams" :columns="columns" label-width="80" @searchHandle="handleSearch" @handleReset="handleReset"> </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <el-button @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="goodsList"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '50px' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="35" />
            <el-table-column label="店铺名称" width="150">
                <template #default="{ row }: { row: ApiGoodsItem }">
                    <span class="shop-name" :title="row.shopName">{{ row.shopName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="商品信息" width="370">
                <template #default="{ row }: { row: ApiGoodsItem }">
                    <div class="goods">
                        <el-image style="width: 68px; height: 68px" :src="row.ossImgUrl" />
                        <div class="goods__info">
                            <el-tooltip v-if="row.productName.length >= 30" class="box-item" effect="dark" :content="row.productName" placement="top">
                                <div class="goods__info--name" :title="row.productName">
                                    {{ row.productName }}
                                </div>
                            </el-tooltip>
                            <div v-else class="goods__info--name" :title="row.productName">
                                {{ row.productName }}
                            </div>
                            <div v-if="row.priceType === 1" class="goods__info--price">{{ row.price && divTenThousand(row.price).toFixed(2) }}</div>
                            <div v-else-if="row.priceType === 2" class="goods__info--price">
                                {{ row.price && divTenThousand(row.price).toFixed(2) }}
                                ~ ￥{{ row.price2 && divTenThousand(row.price2).toFixed(2) }}
                            </div>
                            <div v-else class="goods__info--price">
                                市场价：{{ row.price && divTenThousand(row.price).toFixed(2) }} ￥现价：{{
                                    row.price2 && divTenThousand(row.price2).toFixed(2)
                                }}
                            </div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="审核状态">
                <template #default="{ row }: { row: ApiGoodsItem }">
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
            <el-table-column label="添加时间" width="200px">
                <template #default="{ row }: { row: ApiGoodsItem }">
                    <div>{{ row.createTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="提交审核时间" width="200px">
                <template #default="{ row }: { row: ApiGoodsItem }">
                    <span>{{ row.verifyTime }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="center">
                <template #default="{ row }: { row: ApiGoodsItem }">
                    <el-link
                        style="padding: 0 5px"
                        :underline="false"
                        type="danger"
                        :disabled="row.auditStatus === 'UNDER_REVIEW'"
                        size="small"
                        @click="handleDelClick(row)"
                    >
                        删除
                    </el-link>
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
    <!-- <AddGoods v-model="addGoodsShow" /> -->
</template>

<style scoped lang="scss">
@include b(shop-name) {
    display: inline-block;
    width: 100px;
    @include utils-ellipsis(1);
}
* {
    font-size: 14px;
}
@include b(goods) {
    width: 310px;
    display: flex;
    justify-content: flex-start;
    @include e(info) {
        padding-left: 10px;
        width: 250px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        @include m(name) {
            width: 200px;
            @include utils-ellipsis(2);
        }
        @include m(price) {
            &::before {
                content: '￥';
            }
        }
    }
    @include e(img) {
        width: 100px;
        height: 100px;
        line-height: 20px;
        color: #515151;
        font-size: 12px;
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        @include m(name) {
            width: 65%;
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
