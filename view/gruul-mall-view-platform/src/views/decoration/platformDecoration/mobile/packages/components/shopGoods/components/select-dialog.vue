<template>
    <div>
        <el-form :inline="true" :model="shopSearchParams" class="demo-form-inline">
            <el-form-item :label="isSelectGoods ? '商品名称' : '店铺ID'">
                <el-input v-if="isSelectGoods" v-model="goodsSearchParams.keyword" placeholder="请填写商品名称" />
                <el-input v-else v-model="shopSearchParams.no" placeholder="请填写店铺ID" />
            </el-form-item>
            <el-form-item v-if="!isSelectGoods" label="店铺名称">
                <el-input v-model="shopSearchParams.name" placeholder="请填写店铺名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
        </el-form>
        <el-table :data="tableData" highlight-current-row style="width: 100%; height: 300px" @current-change="selectRow">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column property="logo" :label="`${isSelectGoods ? '商品' : '店铺'}名称`">
                <template #default="{ row }">
                    <div style="display: flex; align-items: center; justify-content: start">
                        <img
                            style="vertical-align: middle; margin: 0 10px 0 0; width: 40px; height: 40px"
                            :src="isSelectGoods ? row.pic : row.logo"
                        />
                        <div>
                            <q-tooltip :content="isSelectGoods ? row.productName : row.name" width="400px"></q-tooltip>
                            <div v-if="isSelectGoods" style="color: rgba(245, 7, 7, 1); width: 400px">
                                ￥{{ row.salePrices[0] / 10000 }}
                                {{
                                    row.salePrices[0] === row.salePrices[row.salePrices.length - 1]
                                        ? ''
                                        : '-' + row.salePrices[row.salePrices.length - 1] / 10000
                                }}
                            </div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column v-if="isSelectGoods" property="name" label="总库存" align="right">
                <template #default="{ row }"> {{ countPrice(row.stocks) }} </template>
            </el-table-column>
        </el-table>
        <page-manage
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
        <div class="footer">
            <el-button @click="emit('cancel')">取消</el-button>
            <el-button type="primary" @click="emit('confirm', currentRow)">确定</el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { doGetShopList } from '@/apis/shops'
import { ElMessage } from 'element-plus'
const emit = defineEmits(['cancel', 'confirm'])
import { doGetRetrieveProduct } from '@/apis/good'
import { onBeforeUnmount } from 'vue'

const props = defineProps({
    isSelectGoods: {
        type: Boolean,
        default: false,
    },
    shopId: {
        type: String,
        default: '',
    },
    activePagesType: {
        type: String,
        default: '',
    },
})

// 当前选中行
const currentRow = ref()
// 选择当前行
const selectRow = (val: any) => {
    currentRow.value = val
}
const tableData = ref<any>([])

interface searchParamType {
    no: string
    name: string
    status: string
}

const countPrice = (arr: string[]) => {
    return arr.reduce((pre, item) => {
        return (pre += parseInt(item))
    }, 0)
}

// 添加请求控制器
const controller = ref<AbortController | null>(null)

// 组件卸载前取消请求
onBeforeUnmount(() => {
    if (controller.value) {
        controller.value.abort()
    }
})

onBeforeMount(() => {
    if (props.isSelectGoods) {
        if (props.shopId) {
            initGoodsList(goodsSearchParams.value)
        } else {
            ElMessage.warning('请重新选择店铺!')
        }
    } else {
        initShopList(shopSearchParams.value)
    }
})

// 分页数据
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})

// 更改条数
const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    if (props.isSelectGoods) {
        initGoodsList(goodsSearchParams.value)
    } else {
        initShopList(shopSearchParams.value)
    }
}

// 更改当前页
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    if (props.isSelectGoods) {
        initGoodsList(goodsSearchParams.value)
    } else {
        initShopList(shopSearchParams.value)
    }
}

// 搜索
const handleSearch = () => {
    if (props.isSelectGoods) {
        initGoodsList(goodsSearchParams.value)
    } else {
        initShopList(shopSearchParams.value)
    }
}

// 店铺搜索框数据
const shopSearchParams = ref<searchParamType>({
    no: '',
    name: '',
    status: 'NORMAL',
})

// 获取店铺列表
async function initShopList(param: searchParamType) {
    try {
        // 取消之前的请求
        if (controller.value) {
            controller.value.abort()
        }
        // 创建新的请求控制器
        controller.value = new AbortController()

        const params = Object.assign(param, pageConfig)
        const { data } = await doGetShopList({
            ...params,
            shopModes: props.activePagesType === 'SAME_CITY_MALL_HOME_PAGE' ? 'O2O' : 'COMMON',
            forDecoration: true,
        })
        tableData.value = data.records.filter((item: any) => item.hasOnSaleAndStock)
        pageConfig.current = data.current
        pageConfig.total = data.total
        pageConfig.size = data.size
    } catch (error: any) {
        if (error.name === 'CanceledError') {
            return
        }
        ElMessage.error('获取店铺列表失败')
    }
}

// 商品搜索框数据
const goodsSearchParams = ref({
    keyword: '',
    shopId: props.shopId,
    productStatus: 'SELL_ON',
})

// 获取商品列表
const initGoodsList = async (param: any) => {
    try {
        // 取消之前的请求
        if (controller.value) {
            controller.value.abort()
        }
        // 创建新的请求控制器
        controller.value = new AbortController()

        const params = Object.assign(param, pageConfig)
        const { data, code } = await doGetRetrieveProduct({
            ...params,
            searchTotalStockGtZero: true,
        })

        if (code !== 200) {
            return ElMessage.error('获取商品失败')
        }
        tableData.value = data.list
        pageConfig.current = data.pageNum
        pageConfig.total = data.total
        pageConfig.size = data.size
    } catch (error: any) {
        if (error.name === 'CanceledError') {
            return
        }
        ElMessage.error('获取商品列表失败')
    }
}
</script>

<style scoped lang="scss">
.footer {
    width: 180px;
    margin: 20px auto 0;
    display: flex;
    justify-content: space-between;
}
</style>
