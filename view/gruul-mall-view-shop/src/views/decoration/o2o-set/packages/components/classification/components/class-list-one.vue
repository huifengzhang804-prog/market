<script setup lang="ts">
import type { PropType } from 'vue'
import ClassListItem from './class-list-item.vue'
import type { ApiCategoryData, CommodityItem } from '../classification'

const $props = defineProps({
    secondCategoryList: {
        type: Array as PropType<ApiCategoryData[]>,
        default() {
            return []
        },
    },
    large: {
        type: Number,
        default: 1,
    },
    list: {
        type: Array as PropType<CommodityItem[]>,
        default() {
            return []
        },
    },
})
</script>

<template>
    <div class="classificate">
        <div v-for="item in $props.secondCategoryList" :key="item.id" class="classificate__unit">
            <div class="classificate__unit-title">{{ item.name }}</div>
            <div class="classificate__unit-content">
                <div v-for="ite in item.children" :key="ite.id" class="classificate__unit-content-item">
                    <img class="classificate__unit-content-item--img" :src="ite.categoryImg" />
                    <div class="classificate__unit-content-item--name">{{ ite.name }}</div>
                </div>
            </div>
        </div>
        <div v-if="$props.large === 2" class="classificate__list">
            <class-list-item v-for="item in $props.list" :key="item.id" :is-large="$props.large" :info="item" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(classificate) {
    width: 285px;
    padding: 14px 10px 0 10px;
    box-sizing: border-box;
    background: #fff;
    height: calc(100vh - 137px);
    overflow-y: auto;
    background: #fff;
    color: #3c3c3c;
    &::-webkit-scrollbar {
        width: 0 !important;
    }
    @include e(unit-title) {
        line-height: 44px;
        font-weight: bold;
        font-size: 14px;
    }
    @include e(unit-content) {
        display: grid;
        grid-template-columns: repeat(3, 65px);
        grid-column-gap: 20px;
        grid-row-gap: 22px;
    }
    @include e(unit-content-item) {
        @include m(img) {
            width: 65px;
            height: 65px;
            border-radius: 5px;
        }
        @include m(name) {
            font-size: 11px;
            width: inherit;
            text-align: center;
        }
    }
    // @include e(list) {
    //     box-sizing: border-box;
    //     height: calc(100vh - 165px);
    //     overflow-y: auto;
    // }
}
</style>
