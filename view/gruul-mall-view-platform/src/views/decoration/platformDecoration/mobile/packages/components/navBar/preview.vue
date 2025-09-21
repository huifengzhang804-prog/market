<script setup lang="ts">
import defaultNavBarData from './nav-bar'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultNavBarData>,
        default() {
            return defaultNavBarData
        },
    },
})
</script>

<template>
    <div
        class="bottom_bar"
        :style="{
            backgroundColor: $props.formData.underfillColor,
        }"
    >
        <div
            v-for="(item, index) in $props.formData.menuList"
            :key="index"
            :class="
                $props.formData.codeStyle === 2 &&
                ($props.formData.menuList.length === 1 ||
                    ($props.formData.menuList.length === 3 && index === 1) ||
                    ($props.formData.menuList.length === 5 && index === 2))
                    ? 'bottom_bar_item_big'
                    : 'bottom_bar_item'
            "
        >
            <img v-if="item.iconType === 2" class="bottom_bar_item_icon" :src="item.iconPath" />
            <span class="bottom_bar_item_text">{{ item.text }}</span>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/navBar.scss';
</style>
