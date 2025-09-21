<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import defaultCouponData from './coupon'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultCouponData>,
        default() {
            return defaultCouponData
        },
    },
})
const backgroundList = [
    'linear-gradient(270deg,#ffbfb3 0%, #ee514f 0%, #ff7f66 52%)',
    'linear-gradient(90deg,#ffa037, #ff7a1e)',
    'linear-gradient(90deg,#9769e6, #6c3dc8)',
    'linear-gradient(90deg,#51545a, #3a3c42)',
]
const $emit = defineEmits(['update:formData'])
const formModel = useVModel($props, 'formData', $emit)

const handleChangeBg = (idx: number) => {
    formModel.value.bgIndex = idx
    formModel.value.bg = backgroundList[idx]
}
</script>

<template>
    <div class="coupon">
        <div class="coupon-form">
            <el-form label-position="left" :model="formModel" label-width="85px">
                <el-form-item label="优惠券样式">
                    <el-radio-group v-model="formModel.style">
                        <el-radio value="style1">样式一</el-radio>
                        <el-radio value="style2">样式二</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="优惠券底色">
                    <div
                        v-for="(item, index) in backgroundList"
                        :key="item"
                        :class="[formModel.bgIndex === index ? 'choosed' : 'circle']"
                        :style="{ background: item }"
                        @click="handleChangeBg(index)"
                    ></div>
                </el-form-item>
                <el-form-item label="更多设置">
                    <el-checkbox v-model="formModel.hideCoupon">隐藏已抢完的券</el-checkbox>
                    <div class="coupon_tip">页面无可显示的优惠券时，优惠券模块将隐藏</div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.coupon_tip {
    color: #8c939d;
}
@include b(homeSwiperForm-item) {
    display: flex;
    border: 1px solid #e4e4e4;
    padding: 10px;
    margin-bottom: 10px;
    @include e(right) {
        flex: 1;
        padding-left: 10px;
    }
    @include e(uploader) {
        width: 80px;
        height: 80px;
        display: block;
        .el-upload {
            border: 1px dashed #d9d9d9;
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }
        .el-upload:hover {
            border-color: #409eff;
        }
    }

    @include e(img) {
        width: 80px;
        height: 80px;
        display: block;
    }

    @include e(plus) {
        font-size: 28px;
        color: #8c939d;
        width: 80px;
        height: 80px;
        line-height: 80px;
        text-align: center;
    }
}

@include b(homeSwiperForm-add) {
    width: 100%;
    height: 100px;
    border: 1px solid #e4e4e4;
    margin-bottom: 10px;
    .el-upload {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
    }
    span {
        color: #3088f0;
        cursor: pointer;
    }
    p {
        font-size: 12px;
        color: #a7a7a7;
    }
}

@include b(homeSwiperForm-form) {
    border: 1px solid #e4e4e4;
    padding: 10px;
}
@include b(circle) {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    margin-right: 15px;
}
@include b(choosed) {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    transition: all 0.3;
    margin-right: 10px;
}
</style>
