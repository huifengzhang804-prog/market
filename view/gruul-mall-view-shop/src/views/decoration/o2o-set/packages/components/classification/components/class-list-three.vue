<script setup lang="ts">
import ClassListItem from './class-list-item.vue'
import type { ApiCategoryData, DeCategoryType, CommodityItem } from '../classification'
import type { PropType } from 'vue'

const $props = defineProps({
    secondCategoryList: {
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
    // large: {
    //     type: Number,
    //     default: 1,
    // },
    currentChooseId: {
        type: String,
        default: '',
    },
    list: {
        type: Array as PropType<CommodityItem[]>,
        default() {
            return []
        },
    },
})
const $emit = defineEmits(['changeNav'])

const handleChooseSec = (index: number, id: string) => {
    $emit('changeNav', { index, id })
}
</script>

<template>
    <div class="classificate">
        <div class="classificate__sort">
            <div
                v-for="(item, index) in $props.secondCategoryList"
                :key="item.id"
                :style="{
                    borderBottom: $props.currentChooseId === item.id ? '2px solid #FA3534' : '',
                    // color: $props.currentChooseId === item.id ?'#FA3534' : '#000000',
                    // background: $props.currentChooseId === item.id ? '#FFFFFF' : '#F2F2F2',
                }"
                class="classificate__sort-item"
                @click="handleChooseSec(index, item.id)"
            >
                {{ item.name }}
            </div>
        </div>
        <div class="classificate__arrow">
            <van-icon name="arrow-down" />
        </div>
        <div class="classificate__list">
            <class-list-item v-for="item in $props.list" :key="item.id" :is-large="$props.config.style" :info="item" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(classificate) {
    width: 250px;
    height: 26px;
    position: relative;
    @include e(sort) {
        padding: 0 8px;
        box-sizing: border-box;
        @include flex(flex-start);
        overflow-x: auto;
        &::-webkit-scrollbar {
            display: none;
            width: 0 !important;
        }
    }
    @include e(sort-item) {
        flex-shrink: 0;
        font-size: 14px;
        padding: 4px 6px;
        background: #fff;
        margin-right: 10px;
        cursor: pointer;
    }
    @include e(arrow) {
        width: 26px;
        height: 26px;
        background: #f2f2f2;
        opacity: 0.8;
        position: absolute;
        z-index: 9;
        right: 0;
        top: 0;
        @include flex();
    }
    @include e(list) {
        padding: 10px 13px 0 25px;
        box-sizing: border-box;
        height: calc(100vh - 165px);
        overflow-y: auto;
    }
}
</style>
