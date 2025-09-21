<template>
    <div class="commodity">
        <div class="commodity__left">
            <el-image style="width: 68px; height: 68px" :src="$props.info.pic" fit="fill"></el-image>
        </div>
        <div class="commodity__right">
            <el-tooltip v-if="$props.info.name.length >= 30" class="box-item" effect="dark" :content="$props.info.name" placement="top-start">
                <div class="commodity__right--name">
                    <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ $props.info.name }}
                </div>
            </el-tooltip>
            <div v-else class="commodity__right--name">
                <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ $props.info.name }}
            </div>

            <div
                v-if="$props.info?.productLabel"
                :style="{ color: $props.info?.productLabel?.fontColor, backgroundColor: $props.info?.productLabel?.backgroundColor }"
                class="commodity__right--label"
            >
                {{ $props.info?.productLabel?.name }}
            </div>
            <div v-if="$props.info.providerName && $props.provider" class="commodity__right--sup" :title="$props.info.providerName">
                供应商:{{ $props.info.providerName }}
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type { ApiGoodType } from '@/views/good/types'
const $props = defineProps({
    info: {
        type: Object as PropType<ApiGoodType>,
        default() {
            return {}
        },
    },
    provider: {
        type: Boolean,
        default() {
            return true
        },
    },
})

const { divTenThousand } = useConvert()
const price = computed(() => {
    const priceArr =
        $props.info.storageSkus?.map((item) => {
            return Number(divTenThousand(item.salePrice))
        }) || []
    return priceArr.sort((a, b) => a - b)[0]
})
</script>
<style lang="scss">
@include b(commodity) {
    @include flex();
    font-size: 12px;
    text-align: left;
    justify-content: flex-start;
    @include e(left) {
        width: 68px;
        height: 68px;
        margin-right: 12px;
    }
    @include e(right) {
        height: 68px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        @include m(name) {
            @include utils-ellipsis(2);
            width: 217px;
            color: #333;
            line-height: 18px;
        }
        @include m(label) {
            line-height: 20px;
            padding: 0 5px;
            width: fit-content;
            font-size: 12px;
        }

        @include m(price) {
            color: #ff7417;
            margin: 4px 0;
        }
        @include m(sup) {
            width: 120px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}
</style>
