<script setup lang="ts">
import type { PropType } from 'vue'
import ClassListItem from './class-list-item.vue'
import type { ApiCategoryData } from '../classification'

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
    btnStyle: {
        type: Number,
        default: 1,
    },
})
</script>

<template>
    <div class="classificate">
        <div v-for="item in $props.secondCategoryList" :key="item.id" class="classificate__unit">
            <div class="classificate__unit-title">{{ item.name }}</div>
            <class-list-item
                v-for="ite in item.children"
                :key="ite.id"
                :btn-style="$props.btnStyle"
                :third-category-info="ite"
                :is-large="$props.large"
            />
        </div>
    </div>
</template>
<style lang="scss" scoped>
@include b(classificate) {
    width: 285px;
    padding: 14px 14px 0 25px;
    box-sizing: border-box;
    background: #fff;
    height: calc(708px - 54px);
    overflow-y: auto;
    color: #3c3c3c;
    &::-webkit-scrollbar {
        width: 0 !important;
    }
    @include e(unit-title) {
        line-height: 44px;
        font-weight: bold;
        font-size: 14px;
    }
}
</style>
