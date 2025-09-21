<template>
    <div v-show="$props.formData.search.show" class="search-wrapper">
        <compose-search :form-data="$props.formData.search" />
    </div>
    <compose-position-style v-show="$props.formData.positionStyle.show" :form-data="$props.formData.positionStyle" />
    <compose-swiper :form-data="$props.formData.swiper" />
</template>

<script lang="ts" setup>
import composeData, { ComposeDataType } from './compose'
import type { PropType } from 'vue'
import ComposeSearch from './compose/search/preview.vue'
import ComposePositionStyle from './compose/positioningStyle/preview.vue'
import ComposeSwiper from './compose/swiper/preview.vue'
const $props = defineProps({
    formData: {
        type: Object as PropType<ComposeDataType>,
        default() {
            return composeData
        },
    },
})
</script>

<style lang="scss" scoped>
.search-wrapper {
    margin: 0 20px;
    vertical-align: middle;
    transition: all 0.3s;
    .search {
        padding: 0 22px;
    }
}
</style>
