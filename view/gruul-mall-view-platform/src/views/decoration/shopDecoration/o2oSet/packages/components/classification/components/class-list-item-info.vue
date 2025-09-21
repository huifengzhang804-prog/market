<script setup lang="ts">
import type { CommodityItem } from '../classification'
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
})
const { divTenThousand } = useConvert()
</script>

<template>
    <div class="classification" :style="{ height: $props.height, width: $props.width }">
        <div class="classification__title">{{ $props.info.name }}</div>
        <div class="classification__bottom">
            <div class="classification__bottom--price">{{ divTenThousand($props.info.salePrices[0]) }}</div>
            <q-icon name="icon-gouwuche5" size="35" color="#FA3534" />
        </div>
    </div>
</template>
<style lang="scss" scoped>
@include b(classification) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    @include e(title) {
        width: inherit;
        font-size: 14px;
        color: #323232;
        @include utils-ellipsis;
    }
    @include e(bottom) {
        width: inherit;
        @include flex(space-between);
        @include m(price) {
            font-size: 16px;
            color: #dd3324;
            &::before {
                content: '￥';
                display: inline-block;
                font-size: 14px;
                color: #dd3324;
            }
        }
        @include m(img) {
            width: 22px;
            height: 22px;
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
</style>
