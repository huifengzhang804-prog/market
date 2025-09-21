<template>
    <div class="commodity">
        <div v-if="showPic" class="commodity__left">
            <el-image style="width: 68px; height: 68px" :src="pic" fit="fill"></el-image>
        </div>
        <div class="commodity__right">
            <div v-if="isPrice" style="display: flex; align-items: center" class="price">
                <div class="commodity__right--price">￥{{ price }}</div>
                <div v-if="$props.info?.sellType !== 'CONSIGNMENT'" class="edit-icon" @click="openEditPrice">
                    <q-icon name="icon-bianji_o" color="#333"></q-icon>
                </div>
            </div>
            <div v-else class="info" style="position: relative; display: flex; flex-direction: column; justify-content: space-between; height: 68px">
                <div style="display: flex; align-items: center; width: 230px">
                    <div v-if="flag">
                        <el-tooltip v-if="$props.info.name.length >= 35" placement="top">
                            <template #content>
                                <p style="max-width: 440px">
                                    <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                    {{ $props.info.name }}
                                </p>
                            </template>
                            <div class="commodity__right--name">
                                {{ name }}
                            </div>
                        </el-tooltip>
                        <div v-else class="commodity__right--name">
                            <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ name }}
                        </div>
                    </div>
                    <el-input v-else ref="inputRef" v-model="name" autosize type="textarea" :maxlength="35" @blur.capture="handleBlur" />
                    <div v-if="flag" style="margin-left: 10px" @click="handleName">
                        <q-icon name="icon-bianji_o edit-icon1" color="#333"></q-icon>
                    </div>
                </div>
                <div style="display: flex; align-items: center">
                    <div
                        v-if="$props.info?.productLabel"
                        :style="{ color: $props.info?.productLabel?.fontColor, backgroundColor: $props.info?.productLabel?.backgroundColor }"
                        class="commodity__right--label"
                    >
                        {{ $props.info?.productLabel?.name }}
                    </div>
                    <el-popover :visible="visible" placement="right" title="设置标签" :width="480" trigger="click">
                        <template #reference>
                            <q-icon v-if="$props.info.labelIcon" name="icon-tag" size="14px" color="#999" @click="getProduct"></q-icon>
                            <span v-else></span>
                        </template>
                        <div class="productLabel">
                            <span
                                v-for="(item, index) in productLabel"
                                :key="item.id"
                                :style="{
                                    color: item.fontColor,
                                    backgroundColor: item.backgroundColor,
                                    border: ind === index ? '2px solid #555CFD' : 'none',
                                }"
                                @click="() => (ind = ind === index ? -1 : index)"
                                >{{ item.name }}</span
                            >
                        </div>

                        <div style="display: flex; align-items: center">
                            <el-button @click="visible = false">取消</el-button>
                            <el-button type="primary" @click="handleLabel"> 确认 </el-button>
                        </div>
                    </el-popover>
                </div>
            </div>

            <el-tooltip v-if="$props.info.providerName" :teleported="false" :content="$props.info.providerName" effect="light">
                <div class="commodity__right--sup">供应商:{{ $props.info.providerName }}</div>
            </el-tooltip>
        </div>

        <el-dialog v-model="editPriceVisible" title="价格设置" center width="900px" destroy-on-close @close="handleClose">
            <el-row :gutter="20" align="middle">
                <el-col :span="6" style="text-align: center;bagc"> 批量设置 </el-col>
                <el-col :span="8"
                    >划线价：
                    <el-input-number
                        v-model="allPrice"
                        :max="999999"
                        :min="0.01"
                        :precision="2"
                        :controls="false"
                        @blur="priceSkuItemRef?.setALLskyPrice('price', allPrice)"
                /></el-col>
                <el-col :span="7"
                    >销售价：
                    <el-input-number
                        v-model="allSalePrice"
                        :max="999999"
                        :min="0.01"
                        :precision="2"
                        :controls="false"
                        @blur="priceSkuItemRef?.setALLskyPrice('salePrice', allSalePrice)"
                /></el-col>
            </el-row>
            <PriceSkuItem ref="priceSkuItemRef" :skus="skus" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editPriceVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleEditPrice"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>
<script lang="ts" setup>
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetCommoditySku, doNameUpdate, doPriceUpdate, doGetProductLabel, updateProductLabel } from '@/apis/good'
import PriceSkuItem from './priceSkuItem.vue'
import type { PropType } from 'vue'
import { ApiCommodityType, CommoditySaleType, CommoditySpecTable } from '../types'

const $props = defineProps({
    info: {
        type: Object as PropType<ApiCommodityType>,
        default: null,
    },
    isPrice: {
        type: Boolean,
        default: true,
    },
    showPic: {
        type: Boolean,
        default: true,
    },
    curRowKey: {
        type: String as PropType<Long>,
        default: '',
    },
})
const { divTenThousand, mulTenThousand } = useConvert()
const emit = defineEmits(['updatePrice', 'initList', 'openTagsPopover'])
const price = computed(() => {
    const tempArr =
        $props.info.storageSkus?.map((item) => {
            return +item.salePrice
        }) || []
    if (tempArr.length === 0) return 0
    const min = Math.min(...tempArr) / 10000
    const max = Math.max(...tempArr) / 10000
    if (max === min) {
        return max?.toFixed(2)
    } else {
        return `${min?.toFixed(2)}-${max?.toFixed(2)}`
    }
})
// 全部划线价
const allPrice = ref(0)
// 全部销售价
const allSalePrice = ref(0)

const priceSkuItemRef = ref<InstanceType<typeof PriceSkuItem>>()
// 商品标签弹窗
const visible = ref(false)
// 商品标签数据
const productLabel = ref<any>([])
// 选中标签下标
const ind = ref(-1)
// 获取当前商品sku
const getCommoditySku = async (): Promise<CommoditySaleType> => {
    const { code, data } = await doGetCommoditySku(useShopInfoStore().shopInfo.id, $props.info.id)
    if (code !== 200) {
        ElMessage.error('获取商品信息失败')
        return data
    }
    data.skus.forEach((item: any) => {
        item.initStock = 0
        item.price = divTenThousand(item.price).toNumber()
        item.salePrice = divTenThousand(item.salePrice).toNumber()
    })
    return data
}

// 获取商品标签
const getProduct = async () => {
    emit('openTagsPopover', $props.info)
    const { code, data } = await doGetProductLabel()
    if (code === 200) {
        visible.value = true
        ind.value = -1
        productLabel.value = data.filter((item, index) => {
            if (item.name === $props.info?.productLabel?.name) {
                console.log(item.name, $props.info?.productLabel?.name, index)
                ind.value = index
            }
            return item.shopType.includes(useShopInfoStore().shopInfo.shopType || '')
        })
    }
}

// 修改标签
const handleLabel = async () => {
    const row = productLabel.value[ind.value]
    const { code } = await updateProductLabel(row ? row.id : '', $props.info.id)
    if (code === 200) {
        visible.value = false
        ElMessage.success('修改成功')
        emit('initList')
    }
}

// 修改名称
const flag = ref(true)

const name = ref($props.info.name)
const handleClose = () => {
    allPrice.value = 0
    allSalePrice.value = 0
}
watch(
    () => $props.info.name,
    (val) => {
        name.value = val
    },
)
watch(
    () => $props.curRowKey,
    (val) => {
        if (val !== $props.info.id) {
            visible.value = false
        }
    },
)
const inputRef = ref()

const handleName = () => {
    flag.value = false
    nextTick(() => {
        inputRef.value.focus()
    })
}

const handleBlur = async () => {
    try {
        if (!name.value) {
            name.value = $props.info.name
            return
        }
        const { code, msg } = await doNameUpdate({ id: $props.info.id, name: name.value })
        if (code === 200) {
            ElMessage.success('修改成功')
            emit('initList')
        } else {
            name.value = $props.info.name
            ElMessage.error(msg)
        }
    } catch (error) {
        name.value = $props.info.name
        console.log(error)
    } finally {
        flag.value = true
    }
}

const skus = ref<CommoditySpecTable[]>([])

const openEditPrice = async () => {
    try {
        const data = await getCommoditySku()
        skus.value = data?.skus
        editPriceVisible.value = true
    } catch (error) {
        ElMessage.error('获取商品信息失败')
    }
}

const editPriceVisible = ref(false)
const handleEditPrice = async () => {
    try {
        const skus = priceSkuItemRef.value?.itemSku
        if (!skus?.length) return
        for (const item of skus) {
            if (item.price < item.salePrice) {
                return ElMessage.error('划线价应大于等于销售价')
            }
        }
        const data = skus?.map((item) => ({
            skuId: item.id,
            price: mulTenThousand(item.price).toNumber(),
            salePrice: mulTenThousand(item.salePrice).toNumber(),
        }))
        const { code, msg } = await doPriceUpdate(data, skus[0]?.productId)
        if (code !== 200) return ElMessage.error(msg || '更新商品失败')
        ElMessage.success('更新成功')
        editPriceVisible.value = false
        emit('updatePrice', data, skus[0]?.productId)
    } catch (error) {
        ElMessage.error('更新失败')
    }
}

const pic = computed(() => {
    return $props.info.albumPics.split(',')[0]
})
</script>
<style lang="scss">
@import '@/assets/css/mixins/mixins';
@include b(commodity) {
    @include flex(flex-start);
    font-size: 12px;
    text-align: left;
    width: 100%;
    @include e(left) {
        width: 68px;
        height: 68px;
        margin-right: 10px;
    }
    @include e(right) {
        margin-right: 10px;
        width: 217px;
        cursor: pointer;
        @include m(name) {
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            max-width: 200px;
        }
        @include m(label) {
            line-height: 20px;
            padding: 0 5px;
            width: fit-content;
            font-size: 12px;
            margin-right: 10px;
        }
        @include m(price) {
            color: #ff7417;
            margin: 10px 0;
            margin-right: 20px;
        }
        @include m(sup) {
            width: 120px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}
.price:hover .edit-icon {
    display: block;
}
.edit-icon1,
.edit-icon,
.edit-icon2 {
    display: none;
    cursor: pointer;
}
.info:hover .edit-icon1 {
    display: block;
}
.info:hover .edit-icon2 {
    display: block;
}
.productLabel {
    display: flex;
    flex-wrap: wrap;
    span {
        margin-right: 10px;
        line-height: 30px;
        padding: 0 5px;
        margin-bottom: 12px;
        cursor: pointer;
        box-sizing: border-box;
    }
}
</style>
