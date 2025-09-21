<template>
    <div class="commodity">
        <div class="commodity__left">
            <el-image :src="pic" fit="fill" style="width: 68px; height: 68px"></el-image>
        </div>
        <div class="commodity__right">
            <div class="productName" style="position: relative">
                <span v-if="flag">
                    <el-tooltip v-if="$props.info.productName.length > 30" :content="$props.info.productName" effect="dark" placement="top-start">
                        <span class="commodity__right--name">
                            <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ $props.info.productName }}
                        </span>
                    </el-tooltip>
                    <span v-else class="commodity__right--name">
                        <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                        {{ $props.info.productName }}
                    </span>
                </span>
                <el-input v-else ref="inputRef" v-model="name" :maxlength="35" style="width: 260px" type="textarea" @blur="handleBlur" />
                <div v-if="flag" class="edit" @click="handleName">
                    <q-icon color="#333" name="icon-bianji_o edit"></q-icon>
                </div>
            </div>
            <div v-if="priceFlag" class="price" style="position: relative; width: fit-content">
                <div class="commodity__right--price">￥{{ price }}</div>
                <div class="edit" @click="openEditPrice">
                    <q-icon color="#333" name="icon-bianji_o edit"></q-icon>
                </div>
            </div>
            <el-tooltip v-if="$props.info.providerName" :content="$props.info.providerName" :teleported="false" effect="dark">
                <div class="commodity__right--sup">供应商:{{ $props.info.providerName }}</div>
            </el-tooltip>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetCommoditySku, doNameUpdate } from '@/apis/good'
import qIcon from '@/components/q-icon/q-icon.vue'
import type { PropType } from 'vue'
import { computed, nextTick, ref } from 'vue'
import { ApiCommodityType } from '../types'

const $props = defineProps({
    info: {
        type: Object as PropType<ApiCommodityType>,
        default: null,
    },
    priceFlag: {
        type: Boolean,
        default: true,
    },
})
const { divTenThousand, mulTenThousand } = useConvert()
const emit = defineEmits(['update-price', 'update-name', 'open', 'get-sku'])
const price = computed(() => {
    const tempArr = $props.info.storageSkus.map((item) => {
        return +item.salePrice
    })
    const min = Math.min(...tempArr) / 10000
    const max = Math.max(...tempArr) / 10000
    if (max === min) {
        return max
    } else {
        return `${min}-${max}`
    }
})

watch(
    () => $props.info.productName,
    (val) => {
        name.value = val
    },
)

// 获取当前商品sku
const getCommoditySku = async () => {
    const { code, data } = await doGetCommoditySku(useShopInfoStore().shopInfo.id, $props.info.id)
    if (code !== 200) {
        ElMessage.error('获取商品信息失败')
        return
    }
    data.skus.forEach((item: any) => {
        item.initStock = 0
        item.price = divTenThousand(item.price)
        item.salePrice = divTenThousand(item.salePrice)
    })
    return data
}

// 修改名称
const flag = ref(true)

const name = ref($props.info.productName)
const inputRef = ref()

const handleName = () => {
    name.value = $props.info.productName
    flag.value = false
    nextTick(() => {
        inputRef.value.focus()
    })
}

const handleBlur = async () => {
    try {
        const curName = name.value
        if (!curName || curName === $props.info.productName) {
            flag.value = true
            return
        }
        const { code } = await doNameUpdate({ id: $props.info.id, name: curName })
        if (code === 200) {
            flag.value = true
            emit('update-name', curName)
        }
    } catch (error) {
        return
    }
}

const openEditPrice = async () => {
    try {
        const data = await getCommoditySku()
        emit('get-sku', data.skus)
        emit('open', true)
    } catch (error) {
        ElMessage.error('获取商品信息失败')
    }
}

const pic = computed(() => {
    return $props.info.albumPics.split(',')[0]
})
</script>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins';

@include b(commodity) {
    height: 82px;
    @include flex(flex-start);
    text-align: left;
    width: 100%;
    @include e(left) {
        width: 68px;
        height: 68px;
        margin-right: 10px;
    }
    @include e(right) {
        margin-right: 10px;
        height: 68px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        @include m(name) {
            max-width: 217px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-right: 20px;
        }
        @include m(price) {
            color: #ff7417;
            // width: 80px;
            margin: 10px 0;
            margin-right: 25px;
        }
        @include m(sup) {
            width: 120px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}

.edit {
    display: none;
    position: absolute;
    right: 0;
    top: 0;
    height: 20px;
    width: 20px;
    cursor: pointer;
}

.productName:hover {
    .edit {
        display: block;
    }
}

.price:hover {
    .edit {
        display: block;
    }
}
</style>
