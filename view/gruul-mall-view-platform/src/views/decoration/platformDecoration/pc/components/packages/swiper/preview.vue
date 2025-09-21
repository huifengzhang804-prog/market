<script setup lang="ts">
import swiper from './swiper'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof swiper>,
        default: swiper,
    },
})
</script>

<template>
    <el-carousel height="520px" indicator-position="none" arrow="never">
        <el-carousel-item v-for="item in formData" :key="item.id">
            <img :src="item.img" :alt="item.title" style="width: 100%; height: 100%" />
        </el-carousel-item>
    </el-carousel>
</template>

<style lang="scss" scoped>
@include b(el-carousel__item) {
    background-color: #f6f6f6;
}
</style>
