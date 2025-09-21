<script lang="ts" setup>
import type { PropType } from 'vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetHighestCategoryLevel } from '@/apis/good'
import { doGetRetrieveProduct } from '@/apis/good'
import { Check } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import type { RetrieveParam } from '@/apis/good/model'

type categoryItme = { id: string; name: string; productNum: string }

const $props = defineProps({
    pointGoodsList: {
        type: Array as PropType<ApiRetrieveComItemType[]>,
        default: () => [],
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
    searchConsignmentProduct: {
        type: Boolean,
        default: false,
    },
    distributeProduct: {
        type: Boolean,
        default: false,
    },
    distributionMode: {
        type: String,
        default: '',
    },
})
//是否已初始化过分类
let categoryInited = false

const emit = defineEmits(['update:modelValue', 'onConfirm', 'update:searchParam'])
const _isShow = useVModel($props, 'modelValue', emit)

watch(
    () => $props.modelValue,
    (val) => {
        if (!val) {
            return
        }
        if (!categoryInited) {
            initCategoryList()
            categoryInited = true
        }
        retrieveCommodity()
    },
)

const { divTenThousand } = useConvert()
const pageConfig = reactive({
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
const keyword = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const allChecked = ref(false)

// 样式选择
const borderStyle = {
    borderGet: '2px solid #2D8CF0',
    borderNoGet: '2px solid #f2f2f2',
}

/**
 * 选择分类
 */
const handleSelectCateItem = (item: categoryItme) => {
    search.value.categoryFirstId = item.id
    retrieveCommodity()
}
const handlemaxPrice = () => {
    search.value.maxPrice = maxPrice.value
    retrieveCommodity()
}
const handleminPrice = () => {
    search.value.minPrice = minPrice.value
    retrieveCommodity()
}
const handlekeyword = () => {
    search.value.keyword = keyword.value
}
const handleSearchByInput = () => {
    retrieveCommodity()
}
/**
 * 选择商品
 */
// 保存选中的id 翻页复用
// bug第一页已选择的商品自动取消选择 解决方案
// let checkedGoods: ApiRetrieveComItemType[] = []
const handleChooseGood = (item: ApiRetrieveComItemType) => {
    item.isCheck = !item.isCheck
    if (item.isCheck) {
        if (!tempGoods.value.find((i) => i.productId === item.productId)) {
            tempGoods.value.push(item)
        }
    } else {
        const idx = tempGoods.value.findIndex((i) => i.productId === item.productId)
        if (idx !== -1) {
            tempGoods.value.splice(idx, 1)
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
            if (!tempGoodsval.find((t) => t.productId === item.productId)) {
                tempGoodsval.push(item)
            }
        }
        return (item.isCheck = allCheckedval)
    })
    if (!allCheckedval) {
        goodsListval.forEach((t) => {
            const idx = tempGoodsval.findIndex((i) => i.productId === t.productId)
            if (idx !== -1) {
                tempGoodsval.splice(idx, 1)
            }
        })
    }
}
const handleSizeChange = (val: number) => {
    pageConfig.size = val
    retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    allChecked.value = false
    retrieveCommodity()
}

async function resetSearch() {
    search.value.categoryFirstId = ''
    search.value.categoryFirstId = ''
    search.value.keyword = ''
    search.value.maxPrice = ''
    search.value.minPrice = ''
    pageConfig.current = 1
    pageConfig.size = 10
    pageConfig.total = 0
    allChecked.value = false
}

/**
 * 初始化分类列表
 */
async function initCategoryList() {
    console.log('初始化分类列表')
    const { code, data } = await doGetHighestCategoryLevel({ size: 1000 })
    if (code === 200) {
        //todo 需要加上类型
        //@ts-ignore
        categoryList.value = data.records
    } else {
        ElMessage.error('获取分类列表失败')
    }
}

/**
 * 检索商品列表
 */
async function retrieveCommodity() {
    const shopId = useShopInfoStore().shopInfo.id
    // 优化筛选空值并修正类型
    const filterSearch = Object.fromEntries(Object.entries(search.value).filter(([_, v]) => v)) as Partial<RetrieveParam>
    const { code, data } = await doGetRetrieveProduct({
        ...filterSearch,
        size: pageConfig.size,
        current: pageConfig.current,
        shopId,
        searchTotalStockGtZero: true,
        searchConsignmentProduct: $props.searchConsignmentProduct,
        distributeProduct: $props.distributeProduct,
        distributionMode: $props.distributionMode,
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
        console.log(data.list)
        allChecked.value = checkAll
        goodsList.value = data.list
        pageConfig.total = data.total
        if (_isShow.value && !$props.pointGoodsList.length) {
            goodsList.value.forEach((element) => {
                element.isCheck = false
            })
        }
    } else {
        ElMessage.error('获取商品失败')
    }
}

/**
 * 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string) {
    return tempGoods.value.findIndex((i) => i.productId === id) !== -1
}

const handleConfirm = () => {
    if (!!$props.quota && tempGoods.value.length > $props.quota) {
        return ElMessage.error(`只能选择${$props.quota}种商品`)
    }
    emit('onConfirm', { tempGoods: tempGoods.value, goodsList: goodsList.value, pageConfig })
    _isShow.value = false
}
defineExpose({
    handleGetAll,
    tempGoods,
    goodsList,
    resetSearch,
    retrieveCommodity,
    pageConfig,
    allChecked,
})
</script>

<template>
    <el-dialog v-model="_isShow" width="800px">
        <div>
            <div class="title">选择商品</div>
            <div class="digGoods">
                <div class="digGoods__box">
                    <div class="digGoods__box--top">
                        <el-select v-model="categoryVal" placeholder="全部分类" style="width: 120px">
                            <el-option-group v-for="group in categoryList" :key="group.id">
                                <el-option :label="group.name" :value="group.name" @click="handleSelectCateItem(group)" />
                            </el-option-group>
                        </el-select>
                        <div>
                            <span style="margin: 0px 10px 0px 25px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
                            <el-input v-model="minPrice" maxlength="20" style="width: 60px" @change="handleminPrice()"></el-input>
                            <span style="margin: 0px 5px; line-height: 32px">-</span>
                            <el-input v-model="maxPrice" maxlength="20" style="width: 60px" @change="handlemaxPrice()"></el-input>
                        </div>
                        <el-input
                            v-model="keyword"
                            class="input-with-select"
                            maxlength="20"
                            placeholder="请输入关键词"
                            style="width: 200px; margin-left: 10px"
                            @change="handlekeyword()"
                        >
                            <template #append>
                                <QIcon name="icon-sousuo1" @click="handleSearchByInput" />
                            </template>
                        </el-input>
                    </div>
                    <div v-if="goodsList.length > 0" class="digGoods__box--content">
                        <div
                            v-for="(item, index) in goodsList"
                            :key="index"
                            :style="{
                                border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
                            }"
                            class="digGoods__box--content--good"
                            @click="handleChooseGood(item)"
                        >
                            <!-- bug第一页已选择的商品自动取消选择 解决方案 -->
                            <!--  checkedGoods.some((goods) => goods.id === item.id) -->
                            <img :src="item.pic" class="digGoods__box--content--good--img" />
                            <!-- bug第一页已选择的商品自动取消选择 解决方案 -->
                            <!-- || checkedGoods.some((goods) => goods.id === item.id) -->
                            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
                                <el-icon color="#fff" size="40px">
                                    <Check />
                                </el-icon>
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
                        <el-checkbox v-model="allChecked" style="height: 48px; padding-left: 5px; background-color: #fff" @change="handleGetAll"
                            >全选</el-checkbox
                        >
                        <BetterPageManage
                            v-model="pageConfig"
                            :load-init="false"
                            :total="pageConfig.total"
                            background
                            @reload="retrieveCommodity"
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
    margin-top: -20px;
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
}

@include b(bottom-container) {
    @include flex;
    flex-direction: column;
}
:deep(.pagination) {
    padding: 8px;
}
</style>
