<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { doGetPlatformLevelAndComNum } from '@/apis/decoration'
import { doGetRetrieveProduct } from '@/apis/good'
import type { ClassItemType, RetrieveType, itemType } from './type.ts'
import type { PropType } from 'vue'
import type { ApiRetrieveComItemType } from '@/apis/good/model/index'

const props = defineProps({
    selectedGoods: {
        type: Object as PropType<itemType[]>,
        default: () => {},
    },

    goodsMax: {
        type: Number,
        default: 0,
    },

    classMax: {
        type: Number,
        default: 0,
    },
})

const emit = defineEmits(['update:selectedGoods'])
const selectGoods = useVModel(props, 'selectedGoods', emit)

const { mulTenThousand, divTenThousand } = useConvert()

function range(salePrices: string[] | string) {
    if (Array.isArray(salePrices)) {
        if (!salePrices.length) return '0'
        let priceArr: string[] = []
        salePrices.forEach((item) => {
            priceArr.push(String(divTenThousand(item)))
        })
        const delRepetPriceArr = Array.from(new Set(priceArr))
        return delRepetPriceArr.length > 1
            ? `${parseFloat(delRepetPriceArr[0]).toFixed(2)}-${parseFloat(delRepetPriceArr[delRepetPriceArr.length - 1]).toFixed(2)}`
            : parseFloat(delRepetPriceArr[0]).toFixed(2)
    } else {
        return divTenThousand(salePrices).toFixed(2)
    }
}
const loading = ref(false)

// 分页配置
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})

// 搜索配置
const retrieveConfig = reactive<RetrieveType>({
    maxPrice: undefined,
    minPrice: undefined,
    platformCategoryFirstId: undefined,
    keyword: undefined,
})

// 商品列表数据
const tableData = ref<itemType[]>([])
// 展示分类数据
const classList = ref<ClassItemType[]>([])

onBeforeMount(() => {
    getClassList()
    getGoodList()
})

const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    getGoodList()
}

const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getGoodList()
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
    retrieveConfig.platformCategoryFirstId = item.platformCategoryFirstId
    getGoodList()
}

const onClear = () => {
    retrieveConfig.platformCategoryFirstId = undefined
    retrieveConfig.keyword = undefined
    retrieveConfig.maxPrice = undefined
    retrieveConfig.minPrice = undefined
    getGoodList()
}

/**
 * 获取分类数据
 */
async function getClassList() {
    const { data, code } = await doGetPlatformLevelAndComNum({
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
    const { data, code } = await doGetRetrieveProduct({
        ...pageConfig,
        ...cloneRetrieveConfig,
        searchTotalStockGtZero: true,
        minPrice: Number(mulTenThousand(cloneRetrieveConfig.minPrice)) || undefined,
        maxPrice: Number(mulTenThousand(cloneRetrieveConfig.maxPrice)) || undefined,
    })
    if (code !== 200) {
        return ElMessage.error('获取商品失败')
    }
    const temp = data.list.map((item: ApiRetrieveComItemType) => {
        const { productId, shopId, salePrices, productName, platformCategoryFirstId, id, pic, prices } = item
        return {
            id,
            productId,
            shopId,
            pic,
            prices,
            salePrices,
            productName,
            platformCategoryFirstId,
        }
    })

    tableData.value = temp
    pageConfig.total = data.total
    pageConfig.size = data.pageSize
    pageConfig.current = data.pageNum
    loading.value = false
}

/**
 * 选择商品
 */
const select = (goods: itemType) => {
    const index = checkInclude(goods)

    if (index === -1) {
        handleSelect(goods)
    } else {
        handleSelect(index, false)
    }
}

/**
 * 处理是否勾选
 */
const handleSelect = (item: any, isSelect = true) => {
    if (isSelect) {
        const { goodsMax, classMax } = props
        if (goodsMax <= 1) return (selectGoods.value = [item])

        if (goodsMax && selectGoods.value.length >= goodsMax) return message()

        const classArr = goodsClass()
        if (classMax && classArr.length >= classMax && !classArr.includes(item.platformCategoryFirstId)) return message(false)
        selectGoods.value.push(item)
    } else {
        selectGoods.value.splice(item, 1)
    }
    return true
}

/**
 * 是否已经选中
 */
const checkInclude = (goods: itemType) => {
    return selectGoods.value.findIndex((item) => goods.id === item.id) ?? -1
}

const message = (isGoods = true) => {
    const message = isGoods ? `商品最多选择${props.goodsMax}个!` : `分类最多选择${props.classMax}个!`
    ElMessage.error(message)
    return false
}

/**
 * 全选
 */
const change = (select: any) => {
    for (let i = 0; i < tableData.value.length; i++) {
        const item = tableData.value[i]
        const index = checkInclude(item)
        const flag = index === -1

        if (select && flag) {
            if (!handleSelect(item)) return
        } else if (!select && !flag) {
            if (!handleSelect(index, false)) return
        }
    }
}

/**
 * 是否全选
 */
const checkAll = computed(() => tableData.value.length && tableData.value.every((item) => checkInclude(item) !== -1))

/**
 * 已选分类数量
 */
const goodsClass = () => {
    const obj: { [key: string]: itemType } = {}

    selectGoods.value.forEach((item) => {
        const key = item.platformCategoryFirstId
        obj[key] = item
    })
    return Object.keys(obj)
}

/**
 * 获取已选商品与类型
 */
const getCheckedGoodsClass = () => {
    const obj: { [key: string]: itemType[] } = {}

    selectGoods.value.forEach((item) => {
        const classObj = classList.value.find((ele) => ele.platformCategoryFirstId === item.platformCategoryFirstId)
        if (classObj) {
            const key = classObj.platformCategoryFirstName
            if (!obj[key]) obj[key] = []
            obj[key].push(item)
        }
    })

    return obj
}

// hover Id
const activeId = ref('0')

defineExpose({
    getCheckedGoodsClass,
})
</script>

<template>
    <div>
        <!-- 搜索 -->
        <div class="search-wrap">
            <div>
                选择分类
                <el-select v-model="retrieveConfig.platformCategoryFirstId" class="m-l-6" placeholder="请选择分类" clearable style="width: 146px">
                    <el-option
                        v-for="item in classList"
                        :key="item.platformCategoryFirstId"
                        :label="item.platformCategoryFirstName"
                        :value="item.platformCategoryFirstId"
                        @click="handleSelectGood(item)"
                    />
                </el-select>
            </div>
            <div class="demo-input-suffix">
                价格
                <el-input-number
                    v-model="retrieveConfig.minPrice"
                    style="width: 80px"
                    :controls="false"
                    placeholder="最小值"
                    class="mr8 m-l-6"
                    :max="999999999"
                />
                <span>-</span>
                <el-input-number
                    v-model="retrieveConfig.maxPrice"
                    style="width: 80px"
                    :controls="false"
                    placeholder="最大值"
                    class="ml8"
                    :max="999999999"
                />
            </div>
            <div>
                商品名称
                <el-input
                    v-model="retrieveConfig.keyword"
                    maxlength="40"
                    placeholder="请输入商品名称"
                    class="search-wrap-input m-l-6"
                    style="width: 146px"
                    clearable
                    @click="onClear"
                >
                </el-input>
            </div>

            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        </div>

        <!-- 商品 -->
        <el-scrollbar height="394px" always class="dialog-scroll m-t-16">
            <div class="main">
                <div
                    v-for="item in tableData"
                    :key="item.id"
                    class="main__item"
                    :class="{ checked: checkInclude(item) > -1 || activeId === item.id }"
                    @mouseover="activeId = item.id"
                    @mouseleave="activeId = '0'"
                    @click="select(item)"
                >
                    <div class="main__item-img">
                        <img :src="item.pic" />

                        <div v-show="checkInclude(item) > -1" class="main__item-img--modal">
                            <q-icon name="icon-duigou-copy" size="40px" color="#fff" />
                        </div>
                    </div>
                    <div class="m-l-6">
                        <div class="main__item--text">{{ item.productName }}</div>
                        <div>￥ {{ range(item.salePrices) }}</div>
                    </div>
                </div>
            </div>
        </el-scrollbar>

        <!-- 分页 -->
        <div class="footer">
            <div class="footer__left">
                <template v-if="goodsMax > 1">
                    <el-checkbox :model-value="checkAll" label="全选" @change="change" />
                    <div class="m-l-20">已选： {{ selectGoods.length }}</div>
                </template>
            </div>
            <page-manage
                :page-size="pageConfig.size"
                :page-num="pageConfig.current"
                :total="pageConfig.total"
                @handle-size-change="handleSizeChange"
                @handle-current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(main) {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 16px 20px;
    @include e(item) {
        width: 372px;
        height: 99px;
        border-radius: 2px;
        border: 2px solid #e9e8ed;
        cursor: pointer;
        padding: 10px;
        font-size: 13px;
        color: #333;
        display: flex;

        @include m(text) {
            width: 262px;
            height: 40px;
            margin-bottom: 19px;
            @include utils-ellipsis(2);
        }
    }

    @include e(item-img) {
        position: relative;
        overflow: hidden;
        width: 79px;
        height: 79px;
        border-radius: 2px;
        img {
            width: 79px;
            height: 79px;
        }

        @include m(modal) {
            inset: 0;
            position: absolute;
            background: #00000080;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    }
}

@include b(checked) {
    border: 2px solid #409eff;
}

@include b(footer) {
    display: flex;
    justify-content: space-between;
    @include e(left) {
        display: flex;
        align-items: center;
        padding: 12.23px 0;
        min-width: 140px;
    }
}

.demo-input-suffix {
    margin: 0 18px;
    .el-input {
        width: 98px !important;
    }
}

.ml8 {
    margin-left: 8px;
}
.mr8 {
    margin-right: 8px;
}

:deep(.el-input) {
    --el-input-height: 28px;
}

.el-input-number.is-without-controls {
    :deep(.el-input__wrapper) {
        padding-left: 6px;
        font-size: 12px;
        padding-right: 6px;
    }
}

.search-wrap {
    display: flex;
    justify-content: space-between;
    justify-items: center;
    font-size: 12px;
    .el-button {
        margin-left: 30px;
        height: 28px;
        padding: 8px 10px;
    }
}
.search-wrap-input {
    width: 180px;
}
</style>
