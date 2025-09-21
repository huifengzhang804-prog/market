<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { doGetPlatformLevelAndComNum } from '@/apis/decoration'
import { doGetRetrieveProduct } from '@/apis/good'

import type { CategoryItem, ApiGoodItemType } from '../goods/goods'
// TODO: 待完善选择商品回显和 刷新后无选中
type SearchType = {
    maxPrice: number | null
    minPrice: number | null
    keyword: string
    current: number
    size: number
    total: number
    platformCategoryFirstId: string | null
}

const $props = defineProps({
    pointGoodsList: {
        type: Array,
        default() {
            return []
        },
    },
    goodsVisible: {
        type: Boolean,
        default: false,
    },
})
const { mulHundred, divTenThousand } = useConvert()

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
// 弹框商品
const goodsList = ref<ApiGoodItemType[]>([])
// 选择的商品列表
const tempGoods = ref<ApiGoodItemType[]>([])
// 分类数组
const categoryList = ref<CategoryItem[]>([])
// 分类选中值
const categoryVal = ref('')
const allChecked = ref(false)
const search = reactive<SearchType>({
    maxPrice: null,
    minPrice: null,
    keyword: '',
    current: 1,
    size: 10,
    total: 0,
    platformCategoryFirstId: '',
})
watch($props, (newval) => {
    if (newval.goodsVisible) {
        resetDate()
        tempGoods.value = JSON.parse(JSON.stringify(newval.pointGoodsList))
        dealPointList()
        retrieveCommodity()
    }
})
defineExpose({
    tempGoods,
    search,
    goodsList,
})
// 样式选择
const borderStyle = {
    borderGet: '2px solid #2D8CF0',
    borderNoGet: '2px solid #f2f2f2',
}

onMounted(() => {
    retrieveCommodity()
    initCategoryList()
})

/**
 * 选择分类
 */
const handleSelectCateItem = (item: CategoryItem) => {
    search.platformCategoryFirstId = item.platformCategoryFirstId
    retrieveCommodity()
}
const handleSearchByInput = () => {
    retrieveCommodity()
}
/**
 * 选择商品
 */
const handleChooseGood = (item: ApiGoodItemType) => {
    item.isCheck = !item.isCheck
    const tempGoodsVal = tempGoods.value
    if (item.isCheck) {
        tempGoodsVal.push(item)
    } else {
        const idx = tempGoodsVal.findIndex((i) => i.id === item.id)
        if (idx !== -1) {
            tempGoodsVal.splice(idx, 1)
        }
    }
}
/**
 * 全选
 */
const handleGetAll = () => {
    const goodsListval = goodsList.value
    const tempGoodsval = tempGoods.value
    const allCheckedval = allChecked.value
    goodsListval.map((item) => {
        if (allCheckedval) {
            if (!tempGoodsval.find((t) => t.id === item.id)) {
                tempGoodsval.push(item)
            }
        }
        return (item.isCheck = allCheckedval)
    })
    if (!allCheckedval) {
        goodsListval.forEach((t) => {
            const idx = tempGoodsval.findIndex((i) => i.id === t.id)
            if (idx !== -1) {
                tempGoodsval.splice(idx, 1)
            }
        })
    }
}
const handleSizeChange = (val: number) => {
    search.current = 1
    search.size = val
    retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
    search.current = val
    allChecked.value = false
    retrieveCommodity()
}
async function resetDate() {
    search.platformCategoryFirstId = null
    search.keyword = ''
    search.maxPrice = null
    search.minPrice = null
    search.current = 1
    search.size = 10
    allChecked.value = false
}
/**
 * 编辑是获取已选择过的数据
 */
async function dealPointList() {
    let checkAll = true
    goodsList.value.forEach((item) => {
        const flag = checkIsSelected(item.id)
        item.isCheck = flag
        checkAll = checkAll ? flag : checkAll
    })
    allChecked.value = checkAll
}
/**
 * 初始化分类列表
 */
async function initCategoryList() {
    const { code, data } = await doGetPlatformLevelAndComNum({ size: 1000 })
    if (code === 200) {
        console.log(data)
        categoryList.value = data.records
    } else {
        ElMessage.error('获取分类列表失败')
    }
}
/**
 * 检索商品列表
 */
async function retrieveCommodity() {
    const cloneSearch = JSON.parse(JSON.stringify(search))
    // null乘10000位0 检索默认值为null
    cloneSearch.minPrice = Number(mulHundred(cloneSearch.minPrice)) || null
    cloneSearch.maxPrice = Number(mulHundred(cloneSearch.maxPrice)) || null
    const { code, data } = await doGetRetrieveProduct({ ...cloneSearch, searchTotalStockGtZero: true })
    if (code === 200) {
        let checkAll = true
        data.list.forEach((item: ApiGoodItemType) => {
            const flag = checkIsSelected(item.id)
            item.isCheck = flag
            checkAll = checkAll ? flag : checkAll
        })
        allChecked.value = checkAll
        goodsList.value = data.list
        search.total = data.total
    } else {
        ElMessage.error('获取商品失败')
    }
}
/**
 * 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string) {
    return tempGoods.value.findIndex((i) => i.id === id) !== -1
}
</script>

<template>
    <div class="digGoods">
        <div class="digGoods__box">
            <div class="digGoods__box--top">
                <el-select v-model="categoryVal" style="width: 120px" placeholder="全部分类">
                    <el-option-group v-for="group in categoryList" :key="group.platformCategoryFirstId">
                        <el-option
                            :label="group.platformCategoryFirstName"
                            :value="group.platformCategoryFirstName"
                            @click="handleSelectCateItem(group)"
                        ></el-option>
                    </el-option-group>
                </el-select>
                <span style="margin: 0px 0px 0px 65px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
                <el-input-number v-model="search.minPrice as number" style="width: 60px" :min="0.01" :controls="false" />
                <span style="margin: 0px 5px; line-height: 32px">-</span>
                <el-input-number v-model="search.maxPrice as number" style="width: 60px" :min="0.01" :controls="false" />
                <el-input v-model="search.keyword" style="width: 140px; margin-left: 10px" placeholder="请输入关键词" maxlength="20"></el-input>
                <span class="serachBtn">
                    <el-icon @click="handleSearchByInput"><i-ep-search /></el-icon>
                </span>
            </div>
            <div v-if="goodsList.length > 0" class="digGoods__box--content">
                <div
                    v-for="(item, index) in goodsList"
                    :key="index"
                    class="digGoods__box--content--good"
                    :style="{
                        border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
                    }"
                    @click="handleChooseGood(item)"
                >
                    <img :src="item.pic" class="digGoods__box--content--good--img" />
                    <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
                        <el-icon color="#fff" size="40px"><i-ep-check /></el-icon>
                    </div>
                    <div class="digGoods__box--content--good--shopName">
                        <div class="ellipsi">{{ item.productName }}</div>
                        <div>￥{{ range(item.salePrices) }}</div>
                    </div>
                </div>
            </div>
            <div
                v-if="goodsList.length === 0"
                class="digGoods__box--content"
                style="display: flex; justify-content: center; align-items: center; height: 250px"
            >
                暂无相关商品信息，请选择其他分类
            </div>
            <div class="digGoods__box--bottom">
                <el-checkbox v-model="allChecked" style="margin-top: 40px" @change="handleGetAll">全选</el-checkbox>
                <PageManage
                    :page-size="search.size"
                    :page-num="search.current"
                    :total="search.total"
                    @handle-size-change="handleSizeChange"
                    @handle-current-change="handleCurrentChange"
                ></PageManage>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(digGoods) {
    border-top: 1px solid #d7d7d7;
    padding-top: 10px;
    @include e(box) {
        background-color: #f2f2f2;
        padding: 10px;
        @include m(top) {
            display: flex;
            justify-content: space-between;
        }
        @include m(content) {
            margin-top: 10px;
            background-color: white;
            border-radius: 5px;
            display: flex;
            flex-wrap: wrap;
            padding: 5px;
            @include m(good) {
                width: 33%;
                margin-left: 2px;
                margin-bottom: 4px;
                height: 80px;

                border-radius: 5px;
                padding: 5px;
                display: flex;
                @include m(img) {
                    width: 65px;
                    height: 65px;
                    position: relative;
                }
                @include m(imgShadow) {
                    width: 65px;
                    height: 65px;
                    position: absolute;
                    background-color: rgba(0, 0, 0, 0.6);
                    @include flex(center, center);
                }
                @include m(shopName) {
                    margin-left: 10px;
                    font-size: 12px;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    flex: 1;
                }
            }
        }
        @include m(bottom) {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            :deep(.pagination) {
                width: calc(100% - 50px);
            }
        }
    }
}

.serachBtn {
    width: 32px;
    height: 32px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid #dcdfe6;
    background-color: white;
    cursor: pointer;
    border-left: none;
    border-radius: 4px;
}
.ellipsi {
    width: 150px;
    @include utils-ellipsis(2);
}
</style>
