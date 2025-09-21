<script setup lang="ts">
import type { PropType } from 'vue'
import type { DeCategoryType, ApiCategoryData } from '../classification'
import { useDecorationStore } from '@/store/modules/decoration'

const $props = defineProps({
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
    totalHeight: {
        type: Number,
        default: 0,
    },
})
const $decorationStore = useDecorationStore()
const $emit = defineEmits(['changeTab'])

const handleChangeTab = (index: number, firstLevelId: string) => {
    $emit('changeTab', { index, firstLevelId })
}
</script>

<template>
    <div class="slide" :style="{}">
        <div
            v-for="(item, index) in $props.config.categoryList"
            :key="item.platformCategoryFirstId"
            :style="{
                color: item.platformCategoryFirstId === $props.currentChooseId ? '#FA3534' : '#000000',
                fontWeight: item.platformCategoryFirstId === $props.currentChooseId ? '700' : 'normal',
                fontSize: item.platformCategoryFirstId === $props.currentChooseId ? '15px' : '12px',
            }"
            class="slide__unit"
            @click="handleChangeTab(index, item.platformCategoryFirstId)"
        >
            <view class="word">
                <template v-if="item && item.platformCategoryFirstName!.length > 5">
                    <text>{{ item.platformCategoryFirstName?.slice(0, 5) }}</text>
                    <text>{{ item.platformCategoryFirstName?.slice(5) }}</text>
                </template>
                <text v-else>{{ item.platformCategoryFirstName }}</text>
            </view>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(slide) {
    width: 106px;
    background: #f4f4f4;
    @include e(unit) {
        height: 50px;
        font-size: 14px;
        color: #333;
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        cursor: pointer;
    }
    .word {
        width: 106px;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        text-align: center;
    }
}
</style>
