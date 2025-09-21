<script setup lang="ts">
import footerInfo from './footer-info'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof footerInfo>,
        default: footerInfo,
    },
})
</script>

<template>
    <div class="footer">
        <div class="main flex footer-info">
            <div class="flex">
                <div class="logo">
                    <img class="logo__img" :src="formData.logo.img" />

                    <div class="logo__title">
                        <div v-for="(item, index) in formData.logo.logoInfo" :key="index" class="logo__title--text">{{ item.title }}</div>
                    </div>
                </div>
                <div class="line"></div>
            </div>

            <div class="module">
                <div v-for="(item, index) in 4" :key="index" class="module__item">
                    <div class="module__item--name">{{ formData.module[index]?.moduleName }}</div>

                    <div v-for="(ite, ind) in formData.module[index]?.moduleTitle" :key="ind" class="module__item--title">{{ ite.title }}</div>
                </div>
            </div>

            <img :class="{ hide: !formData.QRcode }" class="QRcode" :src="formData.QRcode" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(footer) {
    background-color: #fff;
    height: 232px;
    padding: 21px 0 52px;
}

@include b(logo) {
    @include e(img) {
        width: 220px;
        height: 68px;
        margin-bottom: 24px;
    }

    @include e(title) {
        height: 64px;
        display: flex;
        justify-content: space-between;
        flex-direction: column;
        @include m(text) {
            font-size: 14px;
            color: #666666;
            text-align: left;
            width: 269px;
        }
    }
}

@include b(flex) {
    display: flex;
    align-items: center;
}

@include b(footer-info) {
    justify-content: space-between;
}

@include b(line) {
    height: 99px;
    width: 1px;
    background-color: #0000000d;
    margin: 0 60px 0 60px;
}

@include b(module) {
    display: flex;
    justify-content: space-between;
    width: 631px;
    height: 160px;

    @include e(item) {
        min-width: 56px;
        @include m(name) {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 24px;
            color: #333333;
        }

        @include m(title) {
            font-size: 12px;
            font-weight: 300;
            margin-bottom: 16px;
        }
    }
}

@include b(QRcode) {
    margin-left: 94px;
    width: 88px;
    height: 88px;
}

@include b(hide) {
    visibility: hidden;
}
</style>
