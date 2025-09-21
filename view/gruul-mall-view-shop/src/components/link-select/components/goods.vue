<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { doGetRetrieveCommodity } from '@/apis/decoration'
import { doGetHighestCategoryLevel } from '@/apis/good'
import type { LinkSelectItem } from '../linkSelectItem'
import type { PropType } from 'vue'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
type RetrieveType = {
    maxPrice: null | string
    minPrice: null | string
    categoryFirstId: string
    keyword: null | string
}
type ClassItemType = {
    id: string
    name: string
    productNum: number
}

const $props = defineProps({
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
    visible: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:link'])
const { mulTenThousand } = useConvert()
const { range } = usePriceRange()
const linkSelectItem = useVModel($props, 'link', $emit)
const name = 'Goods'
const selectId = ref()
const loading = ref(false)
// 分页配置
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})
// 搜索配置
const retrieveConfig = reactive<RetrieveType>({
    maxPrice: null,
    minPrice: null,
    categoryFirstId: '',
    keyword: null,
})
// 商品列表数据
const tableData = ref<ApiRetrieveComItemType[]>([])
// 展示分类数据
const classList = ref<ClassItemType[]>([])
// defineExpose({
//     selectId,
// })
watch(
    linkSelectItem,
    (newVal) => {
        selectId.value = newVal.id
    },
    {
        immediate: true,
    },
)

getClassList()
getGoodList()

const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    getGoodList()
}

const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getGoodList()
}
const handleSelect = () => {
    const copyTableData = JSON.parse(JSON.stringify(tableData.value)) as ApiRetrieveComItemType[]
    const currentChooseItem = copyTableData.find((item) => item.id === selectId.value)
    if (currentChooseItem) {
        const { id, shopId, productName } = currentChooseItem
        Object.assign(linkSelectItem.value, {
            id,
            type: 1,
            name: productName,
            url: '/pluginPackage/goods/commodityInfo/InfoEntrance',
            append: '',
            shopId,
        })
    }
}
/**
 * 检索商品
 */
const handleSearch = () => {
    getGoodList()
}
/**
 * 分类检索商品
 */
const handleSelectGood = (item: ClassItemType) => {
    retrieveConfig.categoryFirstId = item.id
    getGoodList()
}
const onClear = () => {
    retrieveConfig.categoryFirstId = ''
    retrieveConfig.keyword = null
    retrieveConfig.maxPrice = null
    retrieveConfig.minPrice = null
    getGoodList()
}

/**
 * 获取分类数据
 */
async function getClassList() {
    const { data, code } = await doGetHighestCategoryLevel({
        size: 1000,
    })
    if (code !== 200) {
        return ElMessage.error('分类数据获取失败')
    }
    classList.value = data.records
}
/**
 * 获取商品数据
 */
async function getGoodList() {
    const cloneRetrieveConfig = JSON.parse(JSON.stringify(retrieveConfig))
    if (Number(cloneRetrieveConfig.maxPrice) < Number(cloneRetrieveConfig.minPrice)) {
        ElMessage.warning('请填写正确价格区间')
        return
    }
    loading.value = true
    const { data, code } = await doGetRetrieveCommodity({
        ...pageConfig,
        ...cloneRetrieveConfig,
        searchTotalStockGtZero: true,
        minPrice: Number(mulTenThousand(cloneRetrieveConfig.minPrice)) || null,
        maxPrice: Number(mulTenThousand(cloneRetrieveConfig.maxPrice)) || null,
    })
    if (code !== 200) {
        return ElMessage.error('获取商品失败')
    }
    const temp = data.list.map((item: ApiRetrieveComItemType) => {
        const { productId, shopId, salePrices, productName } = item
        return {
            id: productId,
            shopId,
            salePrices,
            productName,
        }
    })
    tableData.value = temp
    pageConfig.total = data.total
    loading.value = false
}
</script>

<template>
    <!-- 商超商品 -->
    <div>
        <div class="search-wrap">
            <el-button @click="onClear">刷新</el-button>
            <el-select v-model="retrieveConfig.categoryFirstId" placeholder="请选择分类" style="width: 120px">
                <el-option
                    v-for="item in classList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                    style="margin-left: 20px"
                    @click="handleSelectGood(item)"
                ></el-option>
            </el-select>
            <div class="demo-input-suffix">
                价格：
                <el-input v-model="retrieveConfig.minPrice" placeholder="最小值" class="mr10" type="number" maxlength="20"></el-input>
                <span>-</span>
                <el-input v-model="retrieveConfig.maxPrice" placeholder="最大值" class="ml10" type="number" maxlength="20"></el-input>
            </div>
            <el-input v-model="retrieveConfig.keyword" maxlength="40" placeholder="商品名称查询" class="search-wrap-input" clearable @click="onClear">
                <template #append>
                    <el-button :icon="Search" @click="handleSearch" />
                </template>
            </el-input>
        </div>
        <el-table v-loading="loading" :data="tableData" height="369">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="商品名称">
                <template #default="{ row }">
                    <div>{{ row.productName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="价格" width="150px">
                <template #default="scope">
                    <div>￥{{ range(scope.row.salePrices) }}</div>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template #default="scope">
                    <el-radio v-model="selectId" :label="scope.row.id" @change="handleSelect">
                        <div></div>
                    </el-radio>
                </template>
            </el-table-column>
        </el-table>
        <page-manage
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
</template>

<style lang="scss" scoped>
.demo-input-suffix .el-input {
    width: 98px !important;
}
.ml10 {
    margin-left: 10px;
}
.mr10 {
    margin-right: 10px;
}
.search-wrap {
    display: flex;
    justify-content: space-between;
    justify-items: center;
}
.search-wrap-input {
    width: 180px;
}
</style>
