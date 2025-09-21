<script setup lang="ts">
import type { PropType } from 'vue'
import ClassListItemInfo from './class-list-item-info.vue'
import type { CommodityItem } from '../classification'

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
})
</script>

<template>
    <div v-if="$props.isLarge === 3" class="large" :style="{ marginBottom: `20px` }">
        <img class="large__img" :src="$props.info.pic" />
        <class-list-item-info :info="$props.info" />
    </div>
    <div v-else class="small" :style="{ marginBottom: 20 + 'px' }">
        <img class="small__img" :src="$props.info.pic" />
        <class-list-item-info :info="$props.info" width="147px" height="80px" />
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
    &:last-child {
        margin-bottom: 0px;
    }
}
@include b(small) {
    margin-bottom: 15px;
    @include flex(space-between);
    @include e(img) {
        width: 92px;
        height: 92px;
        border-radius: 6px;
    }
    &:last-child {
        margin-bottom: 0px;
    }
}
</style>
