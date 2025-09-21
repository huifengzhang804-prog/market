<script setup lang="ts">
import { ref, shallowReactive, reactive, onMounted, defineExpose, PropType } from 'vue'
import PageManage from '@/components/PageManage/index.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetHighestCategoryLevel } from '@/apis/good'
import { doGetRetrieveProduct } from '@/apis/good'
import { Search, Check } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import { CheckboxValueType } from 'element-plus'
import type { RetrieveParam } from '@/apis/good/model'
type categoryItme = { id: string; name: string; productNum: string }

const $props = defineProps({
    pointGoodsList: {
        type: Array as PropType<ApiRetrieveComItemType[]>,
        default() {
            return []
        },
    },
    goodsVisible: {
        type: Boolean,
        default: false,
    },
    modelValue: {
        type: Boolean,
        default: false,
    },
    searchParam: {
        type: Object as PropType<Partial<RetrieveParam>>,
        default() {
            return {}
        },
    },
    quota: {
        type: Number,
        default: 0,
    },
})

const emit = defineEmits(['update:modelValue', 'on-confirm', 'update:searchParam'])
const _isShow = useVModel($props, 'modelValue', emit)

const { divTenThousand, mulTenThousand } = useConvert()
const pageConfig = ref({
    size: 10,
    current: 1,
    total: 0,
})
const search = useVModel($props, 'searchParam', emit)
// 弹框商品
const goodsList = ref<ApiRetrieveComItemType[]>([])
// 选择的商品列表
const tempGoods = ref<ApiRetrieveComItemType[]>([])
// 分类数组
const categoryList = ref<categoryItme[]>([])
// 分类选中值
const categoryVal = ref('')
const allChecked = ref(false)
defineExpose({
    tempGoods,
    goodsList,
    resetSearch,
    retrieveCommodity,
    pageConfig: pageConfig.value,
})
// 样式选择
const borderStyle = {
    borderGet: '2px solid #2D8CF0',
    borderNoGet: '2px solid #f2f2f2',
}

onMounted(() => {
    initCategoryList()
})

/**
 * @description: 选择分类
 */
const handleSelectCateItem = (item: categoryItme) => {
    search.value.categoryFirstId = item.id
    retrieveCommodity()
}
const handleSearchByInput = () => {
    retrieveCommodity()
}
/**
 * @description: 选择商品
 */
// 保存选中的id 翻页复用
// bug第一页已选择的商品自动取消选择 解决方案
// let checkedGoods: ApiRetrieveComItemType[] = []
const handleChooseGood = (item: ApiRetrieveComItemType) => {
    item.isCheck = !item.isCheck
    const isAll = goodsList.value.every((item) => item.isCheck)
    allChecked.value = isAll
    // const includ = checkedGoods.some((goods) => item.id === goods.id)
    // if (!item.isCheck && includ) {
    //     checkedGoods = checkedGoods.filter((goods) => goods.id !== item.id)
    // } else if (item.isCheck && !includ) {
    //     checkedGoods.push(item)
    // }
}
/**
 * @description: 全选
 * @returns {*}
 */
const handleGetAll = (e: CheckboxValueType) => {
    goodsList.value.forEach((item) => {
        item.isCheck = e as boolean
    })
}
const handleSizeChange = (val: number) => {
    pageConfig.value.size = val
    retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
    pageConfig.value.current = val
    allChecked.value = false
    retrieveCommodity()
}
async function resetSearch() {
    search.value.categoryFirstId = ''
    search.value.categoryFirstId = ''
    search.value.keyword = ''
    search.value.maxPrice = ''
    search.value.minPrice = ''
    pageConfig.value.current = 1
    pageConfig.value.size = 10
    pageConfig.value.total = 0
    allChecked.value = false
}
/**
 * @description: 初始化分类列表
 */
async function initCategoryList() {
    const { code, data } = await doGetHighestCategoryLevel({ size: 1000 })
    if (code === 200) {
        categoryList.value = data.records
    } else {
        ElMessage.error('获取分类列表失败')
    }
}
/**
 * @description: 检索商品列表
 */
async function retrieveCommodity() {
    const shopId = useShopInfoStore().shopInfo.id
    const { keyword, maxPrice, minPrice, categoryFirstId } = search.value
    const filterSearch: Partial<ApiRetrieveComItemType> = {}
    // 筛选空值
    Object.keys(search.value).forEach((item) => {
        if (search.value[item]) {
            filterSearch[item] = search.value[item]
        }
    })
    const { code, data } = await doGetRetrieveProduct({
        ...filterSearch,
        size: pageConfig.value.size,
        current: pageConfig.value.current,
        shopId,
    })
    if (code === 200) {
        let checkAll = true
        let pointGoodsListArr: string[] = []
        if ($props.pointGoodsList.length) {
            // 编辑前选中的商品 取出productId去重
            const set = new Set($props.pointGoodsList.map((item) => item.productId))
            pointGoodsListArr = Array.from(set)
        }
        data.list.forEach((item) => {
            // 检测当前商品是否已经在选择过的列表中 或者编辑前已选中
            const flag = checkIsSelected(item.productId) || pointGoodsListArr.includes(item.productId)
            if (flag) {
                // 如果此商品默认选中 或 已选中 添加到选中列表
                handleChooseGood(item)
            }
            item.isCheck = flag
            checkAll = checkAll ? flag : checkAll
        })
        allChecked.value = checkAll
        goodsList.value = data.list
        pageConfig.value.total = data.total
    } else {
        ElMessage.error('获取商品失败')
    }
}

/**
 * @description: 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string) {
    return tempGoods.value.findIndex((i) => i.productId === id) !== -1
}
const handleClose = () => {
    _isShow.value = false
}
const handleConfirm = () => {
    tempGoods.value = goodsList.value.filter((item) => item.isCheck)
    if (!!$props.quota && tempGoods.value.length > $props.quota) {
        return ElMessage.error(`最多选择${$props.quota}种商品`)
    }
    // bug第一页已选择的商品自动取消选择 解决方案
    // tempGoods.value = checkedGoods
    // console.log('tempGoods.value', tempGoods.value)
    _isShow.value = false
    emit('on-confirm', { tempGoods: tempGoods.value, goodsList: goodsList.value, pageConfig: pageConfig.value })
}
</script>

<template>
    <el-dialog v-model="_isShow" width="800px" :before-close="handleClose">
        <div>
            <div class="title">选择商品</div>
            <div class="digGoods">
                <div class="digGoods__box">
                    <div class="digGoods__box--top">
                        <el-select v-model="categoryVal" style="width: 120px" placeholder="全部分类">
                            <el-option-group v-for="group in categoryList" :key="group.id">
                                <el-option :label="group.name" :value="group.name" @click="handleSelectCateItem(group)" />
                            </el-option-group>
                        </el-select>
                        <div>
                            <span style="margin: 0px 10px 0px 25px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
                            <el-input v-model="search.minPrice" style="width: 60px" maxlength="20"></el-input>
                            <span style="margin: 0px 5px; line-height: 32px">-</span>
                            <el-input v-model="search.maxPrice" style="width: 60px" maxlength="20"></el-input>
                        </div>
                        <el-input
                            v-model="search.keyword"
                            placeholder="请输入关键词"
                            class="input-with-select"
                            maxlength="20"
                            style="width: 200px; margin-left: 10px"
                        >
                            <template #append>
                                <el-button :icon="Search" @click="handleSearchByInput" />
                            </template>
                        </el-input>
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
                            <!-- bug第一页已选择的商品自动取消选择 解决方案 -->
                            <!--  checkedGoods.some((goods) => goods.id === item.id) -->
                            <img :src="item.pic" class="digGoods__box--content--good--img" />
                            <!-- bug第一页已选择的商品自动取消选择 解决方案 -->
                            <!-- || checkedGoods.some((goods) => goods.id === item.id) -->
                            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
                                <el-icon color="#fff" size="40px"><Check /></el-icon>
                            </div>
                            <div class="digGoods__box--content--good--shopName">
                                <div class="digGoods__box--content--good--shopName--name">{{ item.productName }}</div>
                                <div v-if="item.salePrices.length > 1" class="digGoods__box--content--good--shopName--price">
                                    <span>￥{{ divTenThousand(item.salePrices[0]) }}</span>
                                    ~
                                    <span>￥{{ divTenThousand(item.salePrices[item.salePrices.length - 1]) }}</span>
                                </div>
                                <div v-else class="digGoods__box--content--good--shopName--price">￥{{ divTenThousand(item.salePrices[0]) }}</div>
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
                    <div class="bottom">
                        <el-checkbox v-model="allChecked" style="margin-top: 40px" @change="handleGetAll">全选</el-checkbox>
                        <PageManage
                            :page-size="pageConfig.size"
                            :page-num="pageConfig.current"
                            :total="pageConfig.total"
                            @handle-size-change="handleSizeChange"
                            @handle-current-change="handleCurrentChange"
                        />
                    </div>
                </div>
            </div>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="_isShow = false">取消</el-button>
                <el-button type="primary" @click="handleConfirm"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.title {
    font-size: 15px;
    font-weight: bold;
    display: flex;
    margin-bottom: 20px;
    margin-top: -40px;
}

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
                    @include flex;
                    flex-direction: column;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-left: 10px;
                    padding: 5px;
                    font-size: 12px;
                    @include m(name) {
                        @include utils-ellipsis(2);
                    }
                    @include m(price) {
                        @include utils-ellipsis;
                    }
                }
            }
        }
    }
}
@include b(bottom) {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-right: 20px;
}
@include b(bottom-container) {
    @include flex;
    flex-direction: column;
}
</style>
