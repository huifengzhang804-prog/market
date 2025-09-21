<script setup lang="ts">
const $props = defineProps({
    imgUrl: {
        type: String,
        default: '',
    },
    // rightIcon: {
    //     type: String,
    //     default: 'el-icon-arrow-right',
    // },
})
</script>

<template>
    <div class="item">
        <div class="item__left">
            <img v-if="$props.imgUrl !== ''" :src="$props.imgUrl" class="item__img" />
            <span class="item__text">
                <slot></slot>
            </span>
        </div>
        <!-- <i :class="'el-collapse-item__arrow ' + rightIcon"></i> -->
        <!-- <el-icon><i-eq-arrowRight /></el-icon> -->
        <el-icon><i-ep-arrowRight /></el-icon>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/menuItem.scss';
</style>
