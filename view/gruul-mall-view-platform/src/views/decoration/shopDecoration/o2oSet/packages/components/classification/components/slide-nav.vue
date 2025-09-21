<script setup lang="ts">
import type { PropType } from 'vue'
import type { DeCategoryType, ApiCategoryData } from '../classification'

const $props = defineProps({
    categoryList: {
        type: Array as PropType<ApiCategoryData[]>,
        default() {
            return []
        },
    },
    config: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return {}
        },
    },
    currentChooseId: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['changeTab'])

const handleChangeTab = (index: number, firstLevelId: string) => {
    $emit('changeTab', { index, firstLevelId })
}
</script>

<template>
    <div class="slide">
        <div
            v-for="(item, index) in $props.categoryList"
            :key="item.id"
            :style="{
                color: item.id === $props.currentChooseId ? '#FA3534' : '#000000',
                background: item.id === $props.currentChooseId ? '#FFFFFF' : '#F2F2F2',
                fontWeight: item.id === $props.currentChooseId ? 'bold' : 'normal',
            }"
            class="slide__unit"
            @click="handleChangeTab(index, item.id)"
        >
            <div
                v-if="item.id === $props.currentChooseId"
                :style="{ background: item.id === $props.currentChooseId ? '#FA3534' : '#000000' }"
                class="slide__col"
            ></div>
            {{ item.name }}
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(slide) {
    width: 106px;
    height: calc(100vh - 137px);
    overflow-y: auto;
    background: #f8f8f8;
    scrollbar-width: none;
    -ms-overflow-style: none;
    @include e(unit) {
        width: 106px;
        height: 40px;
        @include utils-ellipsis;
        text-align: center;
        line-height: 40px;
        font-size: 14px;
        color: #333;
        position: relative;
    }
    @include e(col) {
        width: 7px;
        height: 22px;
        position: absolute;
        left: 2px;
        top: 50%;
        margin-top: -12px;
        border-radius: 0px 30px 30px 0px;
    }
    &::-webkit-scrollbar {
        display: none;
    }
}
</style>
