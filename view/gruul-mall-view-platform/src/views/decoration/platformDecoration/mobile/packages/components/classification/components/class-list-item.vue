<script setup lang="ts">
import ClassListItemInfo from './class-list-item-info.vue'
import type { ApiCategoryData, CommodityItem, DeCategoryType } from '../classification'
import type { PropType } from 'vue'

const $props = defineProps({
    isLarge: {
        type: Number,
        default: 1,
    },
    info: {
        type: Object as PropType<CommodityItem>,
        default() {
            return {}
        },
    },
    config: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return {}
        },
    },
})
</script>

<template>
    <div class="small">
        <img class="small__img" :src="$props.info.pic" />
        <class-list-item-info :info="$props.info" :config="$props.config" height="100px" style="width: calc(100% - 100px)" />
    </div>
</template>
<style lang="scss" scoped>
@include b(large) {
    @include e(img) {
        display: block;
        width: 246px;
        height: 98px;
        border-radius: 6px;
        margin-bottom: 7px;
    }
}
@include b(small) {
    width: calc(100%);
    padding-bottom: 15px;
    display: flex;

    @include e(img) {
        width: 100px;
        height: 100px;
        border-radius: 6px;
    }
}
</style>
