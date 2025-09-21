<script setup lang="ts">
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetGetGoods } from '@/q-plugin/liveStream/apis/goods'
import { ElMessage } from 'element-plus'
import { Search, Check } from '@element-plus/icons-vue'
import type { ChooseGoodsPopup } from '@/q-plugin/liveStream/views/components/live-goods/types'

defineProps({
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
const { divTenThousand, mulTenThousand } = useConvert()
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
// 弹框商品
const goodsList = ref<ChooseGoodsPopup[]>([])
// 选择的商品列表
const tempGoods = ref<ChooseGoodsPopup[]>([])
const allChecked = ref(false)
const total = ref(0)
const search = shallowReactive({
    productName: '',
    maxPrice: '',
    minPrice: '',
    salePrice: '',
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

const handleSearchByInput = () => {
    retrieveCommodity()
}
/**
 * 选择商品
 */
const handleChooseGood = (item: ChooseGoodsPopup) => {
    item.isCheck = !item.isCheck
    const tempGoodsVal = tempGoods.value
    // ElMessage.error('最多选择16个商品')
    // return false
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
    pageConfig.size = val
    retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    allChecked.value = false
    retrieveCommodity()
}
async function resetDate() {
    search.productName = ''
    search.maxPrice = ''
    search.minPrice = ''
    pageConfig.current = 1
    pageConfig.size = 10
    pageConfig.total = 0
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
 * 检索商品列表
 */
async function retrieveCommodity() {
    if (search.minPrice && search.maxPrice) {
        search.salePrice = `${mulTenThousand(search.minPrice).toString()}_${mulTenThousand(search.maxPrice).toString()}`
    }
    if (!search.minPrice && search.maxPrice) {
        search.salePrice = `_${mulTenThousand(search.maxPrice).toString()}`
    }
    if (!search.maxPrice && search.minPrice) {
        search.salePrice = `${mulTenThousand(search.minPrice).toString()}_`
    }
    if (!search.maxPrice && !search.minPrice) {
        search.salePrice = ''
    }
    const { productName, salePrice } = search
    const { code, data } = await doGetGetGoods({ productName, salePrice, size: pageConfig.size, current: pageConfig.current })
    if (code === 200) {
        let checkAll = true
        data.records.forEach((item: ChooseGoodsPopup) => {
            const flag = checkIsSelected(item.id)
            item.isCheck = flag
            checkAll = checkAll ? flag : checkAll
        })
        allChecked.value = checkAll
        goodsList.value = data.records
        pageConfig.total = data.total
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
    <div>
        <div class="title">选择商品</div>
        <div class="digGoods">
            <div class="digGoods__box">
                <div class="digGoods__box--top">
                    <div>
                        <span style="margin: 0px 10px 0px 25px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
                        <el-input v-model.number="search.minPrice" style="width: 60px" onkeyup="value=value.replace(/[^\d]/g,'')" maxlength="20" />
                        <span style="margin: 0px 5px; line-height: 32px">-</span>
                        <el-input v-model.number="search.maxPrice" style="width: 60px" onkeyup="value=value.replace(/[^\d]/g,'')" maxlength="20" />
                    </div>
                    <el-input
                        v-model="search.productName"
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
                        <img :src="item.ossImgUrl" class="digGoods__box--content--good--img" />
                        <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
                            <el-icon color="#fff" size="40px"><Check /></el-icon>
                        </div>
                        <div class="digGoods__box--content--good--shopName">
                            <div class="digGoods__box--content--good--productName">
                                {{ item.productName }}
                            </div>
                            <div v-if="item.priceType === 1" class="digGoods__box--content--good--price">
                                {{ item.price && divTenThousand(item.price) }}
                            </div>
                            <div v-else-if="item.priceType === 2" class="digGoods__box--content--good--price">
                                {{ item.price && divTenThousand(item.price) }}
                                ~ ￥{{ item.price2 && divTenThousand(item.price2) }}
                            </div>
                            <div v-else class="digGoods__box--content--good--price">
                                市场价：{{ item.price && divTenThousand(item.price) }} 现价：￥{{ item.price2 && divTenThousand(item.price2) }}
                            </div>
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
                    <el-checkbox v-model="allChecked" style="margin-top: 40px" :disabled="!goodsList.length" @change="handleGetAll">全选</el-checkbox>
                    <BetterPageManage
                        v-model="pageConfig"
                        :load-init="true"
                        background
                        :total="pageConfig.total"
                        @reload="retrieveCommodity"
                        @handle-size-change="handleSizeChange"
                        @handle-current-change="handleCurrentChange"
                    />
                </div>
            </div>
        </div>
    </div>
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
                    width: 60%;
                    @include flex;
                    flex-direction: column;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-left: 10px;
                    padding: 5px;
                    font-size: 12px;
                }
                @include m(productName) {
                    width: 100%;
                    @include utils-ellipsis(2);
                }
                @include m(price) {
                    &::before {
                        content: '￥';
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
</style>
