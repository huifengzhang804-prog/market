<script setup lang="ts">
import type { PropType } from 'vue'
import defaultCouponData from './coupon'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultCouponData>,
        default() {
            return defaultCouponData
        },
    },
})
const couponList = ['1', '2', '3']
</script>

<template>
    <div class="coupon">
        <div v-for="item in couponList" :key="item" class="coupon__item" :style="{ background: $props.formData.bg }">
            <div class="coupon__item-left">
                <div class="coupon__item-left--price">99</div>
                <div class="coupon__item-left--row"></div>
            </div>
            <!-- 样式一 -->
            <div v-if="$props.formData.style === 'style1'" class="coupon__styleOne" :style="{ background: $props.formData.bg }">立即领取</div>
            <!-- 样式二 -->
            <div v-else class="coupon__styleTwo">立即领取</div>
        </div>
    </div>
</template>
<style lang="scss" scoped>
div::-webkit-scrollbar {
    width: 0px;
    height: 0px;
}
@include b(coupon) {
    width: 100%;
    height: 110px;
    overflow-x: auto;
    white-space: nowrap;
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #fff;
    @include e(item) {
        position: relative;
        flex-shrink: 0;
        background: red;
        width: 140px;
        height: 80px;
        border-radius: 10px;
        margin-left: 15px;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    @include e(item-left) {
        margin-left: 21px;
        @include m(price) {
            font-size: 24px;
            color: #fff;
            text-align: center;
            margin-bottom: 10px;
            &::after {
                content: '元';
                display: inline-block;
                vertical-align: baseline;
                font-size: 12px;
                margin-left: 3px;
            }
        }
        @include m(row) {
            width: 60px;
            height: 10px;
            border-radius: 4px;
            background: #fff;
            opacity: 0.6;
        }
    }
    @include e(styleOne) {
        position: absolute;
        width: 48px;
        height: 80px;
        right: 0;
        top: 0;
        background: green;
        writing-mode: vertical-lr;
        text-align: center;
        font-weight: bold;
        line-height: 48px;
        border-radius: 0 10px 10px 0;
        z-index: 1;
        clip-path: ellipse(48px 70% at right center);
    }
    @include e(styleOne-border) {
        content: '';
        width: 30px;
        height: 80px;
        border-radius: 50% 0 0 50%;
        background: green;
        position: absolute;
        top: 0;
        right: 18px;
    }
    @include e(styleTwo) {
        width: 40px;
        height: 80px;
        line-height: 40px;
        text-align: center;
        writing-mode: vertical-lr;
        border-radius: 0 10px 10px 0;
        border-left: 1px dashed #cdcdcd;
        position: relative;
        &::after {
            position: absolute;
            content: '';
            width: 16px;
            height: 16px;
            background: #fff;
            border-radius: 50%;
            left: -8px;
            bottom: -8px;
        }
        &::before {
            position: absolute;
            content: '';
            width: 16px;
            height: 16px;
            background: #fff;
            border-radius: 50%;
            left: -8px;
            top: -8px;
        }
    }
}
</style>
