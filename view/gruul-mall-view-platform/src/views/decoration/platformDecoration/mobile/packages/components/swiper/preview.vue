<script setup lang="ts">
import swiperFormData from './swiper'
import type { SwiperFormDataType } from './swiper'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<SwiperFormDataType>,
        default() {
            return swiperFormData
        },
    },
})
const current = ref(0)
const getImageClass = computed(() => {
    const { imageStyle, imageAngle } = $props.formData
    // 1常规 2投影
    const styles = ['homeSwiper-swiper__boxNM', 'homeSwiper-swiper__boxCP']
    // 图片倒角 1直角 2圆角
    const angles = ['homeSwiper-swiper__corners', 'homeSwiper-swiper__angle']
    const gs = styles[+imageStyle - 1]
    const as = angles[+imageAngle - 1]
    return `${gs} ${as}`
})

const handleSwiperChange = (e: number) => {
    current.value = e
}
</script>

<template>
    <div class="homeSwiper" :style="{ height: `${$props.formData.height / 2}px` }">
        <van-swipe
            v-if="$props.formData.swiperList.length"
            class="homeSwiper-swiper"
            :show-indicators="false"
            :autoplay="3000"
            @change="handleSwiperChange"
        >
            <van-swipe-item v-for="(item, index) in $props.formData.swiperList" :key="index" class="homeSwiper-swiper__item">
                <div class="homeSwiper-swiper__image" :style="{ padding: `0px ${$props.formData.padding}px` }">
                    <van-image
                        width="100%"
                        height="100%"
                        :src="item.img"
                        :class="['homeSwiper-swiper__imgItem', getImageClass]"
                        fit="cover"
                        :radius="$props.formData.imageAngle === 2 ? '7px' : '0'"
                    />
                </div>
                <div v-if="$props.formData.indicator === 1" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat1">
                    <span
                        v-for="(ix, idx) in $props.formData.swiperList.length"
                        :key="ix"
                        :class="[current === idx ? 'homeSwiper-swiper__icat1--active' : '']"
                    ></span>
                </div>
                <div v-if="$props.formData.indicator === 3" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat3">
                    <span
                        v-for="(ix, idx) in $props.formData.swiperList.length"
                        :key="ix"
                        :class="[current === idx ? 'homeSwiper-swiper__icat1--active' : '']"
                    ></span>
                </div>
            </van-swipe-item>
        </van-swipe>
        <div v-if="!$props.formData.swiperList.length" class="homeSwiper-swiper__nullImg">
            <img src="https://qiniu-app.qtshe.com/u391.png" alt="" />
        </div>
        <div
            v-if="$props.formData.indicator === 2"
            class="homeSwiper-swiper__indicator homeSwiper-swiper__icat2"
            :style="{ right: `${$props.formData.padding + 5}px` }"
        >
            <span>{{ `${current + 1}/${$props.formData.swiperList.length}` }}</span>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(homeSwiper) {
    background-color: #fff;
    position: relative;
}

@include b(homeSwiper-swiper) {
    height: 100%;
    @include e(item) {
        height: 100%;
    }
    @include e(image) {
        width: 100%;
        height: 100%;
        position: relative;
    }
    @include m(custom) {
        border-radius: 0;
    }
    @include m(fillet) {
        border-radius: 10px;
        overflow: hidden;
    }

    @include e(imgItem) {
        box-sizing: border-box;
        padding-top: 2px;

        img {
            height: calc(100% - 2px);
        }
    }
    @include e(boxNM) {
        box-shadow: none;
    }
    @include e(boxCP) {
        box-shadow: rgba(0, 0, 0, 0.6) 0 0 4px;
    }

    @include e(nullImg) {
        box-sizing: border-box;
        overflow: hidden;
        height: 100%;
        background-color: rgba(233, 247, 253, 1);
        display: flex;
        justify-content: center;
        align-items: center;
        img {
            display: inline-block;
            width: 44px;
            height: 46px;
        }
    }

    @include e(indicator) {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
        bottom: 10px;
        // border: 1px solid red;
        height: 20px;
        width: 100%;
        box-sizing: border-box;
    }

    @include e(icat1) {
        span {
            display: inline-block;
            width: 8px;
            height: 8px;
            background-color: #ebedf0;
            opacity: 0.3;
            margin-right: 6px;
            border-radius: 50%;
        }

        @include m(active) {
            opacity: 0.8 !important;
            background-color: #fff !important;
        }
    }

    @include e(icat2) {
        position: absolute;
        right: 0;
        bottom: 10px;
        display: block;

        span {
            float: right;
            box-sizing: border-box;
            padding: 5px 12px;
            background-color: rgba(0, 0, 0, 0.2);
            color: #fff;
            font-size: 13px;
            border-radius: 16px;
        }
    }

    @include e(icat3) {
        span {
            display: inline-block;
            width: 22px;
            height: 3px;
            background-color: #ebedf0;
            opacity: 0.3;
            margin-right: 6px;
            border-radius: 4px;
        }

        @include m(active) {
            opacity: 0.8 !important;
            background-color: #fff !important;
        }
    }
}
</style>
