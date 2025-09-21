<script setup lang="ts">
import type { CommodityItem, DeCategoryType } from '../classification'
import qIcon from '@/components/q-icon/q-icon.vue'
import type { PropType } from 'vue'

const $props = defineProps({
    width: {
        type: String,
        default: '246px',
    },
    height: {
        type: String,
        default: '50px',
    },
    info: {
        type: Object as PropType<CommodityItem>,
        default() {
            return {}
        },
    },
    config: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return {}
        },
    },
})
const { divTenThousand } = useConvert()

const $emit = defineEmits(['changeSpecImg'])
const handleChangeSpecImg = (index: number, specImg: string, info: CommodityItem) => {
    $emit('changeSpecImg', index, specImg, info)
}
</script>

<template>
    <div class="classification" :style="{ height: $props.height }">
        <div class="classification__title">
            <text
                v-if="$props.info.shopType !== 'ORDINARY' && $props.config.goodsShow.includes(1)"
                class="classification__tag"
                :style="{ backgroundColor: $props.info.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
            >
                {{ $props.info.shopType === 'PREFERRED' ? '优选' : '自营' }} </text
            >{{ $props.info.name }}
        </div>
        <div v-if="$props.info.specImages && $props.info.specImages.length > 1 && $props.config.goodsShow.includes(2)" class="horizontal-scroll">
            <div class="classification__specImg">
                <van-image
                    v-for="(specImg, index) in $props.info.specImages"
                    :key="index"
                    :src="specImg"
                    class="classification__specImg-item"
                    :style="{ border: $props.info.activeImg === index ? '2px solid #fb232f' : 'none' }"
                    @click="handleChangeSpecImg(index, specImg, $props.info)"
                />
            </div>
        </div>
        <div v-if="$props.config.goodsShow.includes(3) && $props.info.salesCount > 0" class="classification__sales-count">
            已售 {{ $props.info.salesCount > 10000 ? $props.info.salesCount / 10000 + '万' : $props.info.salesCount }}
        </div>
        <div v-if="$props.config.goodsShow.includes(4)" class="classification__discount">
            <div>优惠券折扣</div>
            <div v-if="$props.info.freeShipping">包邮</div>
        </div>
        <div class="classification__bottom">
            <div class="classification__bottom--price">
                {{ String(divTenThousand($props.info.salePrices[0])).split('.')[0]
                }}<text v-if="String(divTenThousand($props.info.salePrices[0])).split('.')[1]" style="font-size: 11px"
                    >.{{ String(divTenThousand($props.info.salePrices[0])).split('.')[1] }}</text
                >
            </div>
            <q-icon v-if="$props.config.goodsShow.includes(5)" name="icon-gouwuche5" size="35" color="#FA3534" />
        </div>
    </div>
</template>
<style lang="scss" scoped>
@include b(classification) {
    padding-left: 10px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    @include e(title) {
        font-size: 14px;
        color: #323232;
        @include utils-ellipsis();
        width: 100%;
    }
    @include e(tag) {
        font-size: 10px;
        color: #fff;
        padding: 1px 2px;
        border-radius: 2px;
        position: relative;
        top: -1px;
        margin-right: 3px;
    }
    @include e(sales-count) {
        font-size: 11px;
        color: #999;
    }
    @include e(discount) {
        @include flex(flex-start);
        gap: 6px;
        font-size: 10px;
        color: #fa3534;
        & > div {
            padding: 0px 3px;
            border-radius: 2px;
            border: 1px solid #fa3534;
        }
        & > div:last-child {
            color: #e3600a;
            border: 1px solid #e3600a;
        }
    }
    @include e(bottom) {
        @include flex(space-between);
        @include m(price) {
            font-size: 16px;
            color: #dd3324;
            font-weight: 500;
            font-family: Kingsoft_Cloud_Font;
            &::before {
                content: '￥';
                display: inline-block;
                font-size: 12px;
                color: #dd3324;
            }
        }
        @include m(img) {
            width: 22px;
            height: 22px;
        }
    }
    @include e(specImg) {
        @include flex(flex-start);
        margin: 5px 0;
        overflow-x: auto;
        align-items: center;
        width: max-content;
        min-width: 100%;
        overflow-y: hidden;
        scroll-behavior: smooth;
        -webkit-overflow-scrolling: touch;
        scrollbar-width: none;
        -ms-overflow-style: none;
        &::-webkit-scrollbar {
            display: none;
        }
        @include e(specImg-item) {
            width: 25px;
            height: 25px;
            flex-shrink: 0;
            cursor: pointer;
            overflow: hidden;
            border-radius: 4px;
            margin-right: 7px;
            &:last-child {
                margin-right: 0;
            }
        }
    }
}
@include b(redBtn) {
    width: 25px;
    height: 25px;
    background: linear-gradient(164deg, #f3f3f3, #e5382e, #fd4e26);
    box-shadow: 0px 2px 7px 0px rgb(255 14 0 / 27%);
    border-radius: 50%;
    @include flex();
    @include e(img) {
        width: 16px;
        height: 16px;
    }
}

.horizontal-scroll {
    overflow-x: auto;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
    &::-webkit-scrollbar {
        display: none;
    }
}
</style>
