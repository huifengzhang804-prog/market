<script setup lang="ts">
import type { ApiCategoryData, DeCategoryType, CommodityItem } from '../classification'
import type { PropType } from 'vue'

const $props = defineProps({
    categoryList: {
        type: Array as PropType<ApiCategoryData[]>,
        default() {
            return []
        },
    },
    secondCategoryList: {
        type: Array as PropType<ApiCategoryData[]>,
        default() {
            return []
        },
    },
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
const $emit = defineEmits(['changeTab'])
const { divTenThousand } = useConvert()

const handleChangeTab = (index: number, firstLevelId: string) => {
    $emit('changeTab', { index, firstLevelId })
}
</script>

<template>
    <div class="classificate">
        <div class="classificate__sort">
            <div
                v-for="(item, index) in $props.categoryList"
                :key="item.id"
                :style="{
                    borderBottom: $props.currentChooseId === item.id ? '2px solid #FA3534' : '',
                }"
                class="classificate__sort-item"
                @click="handleChangeTab(index, item.id)"
            >
                {{ item.name }}
            </div>
        </div>

        <div class="classificate__sec">
            <div v-for="item in $props.secondCategoryList.slice(0, 10)" :key="item.id" class="classificate__sec-item">
                <el-image :src="item.categoryImg" class="classificate__sec-item--pic"></el-image>
                <div class="classificate__sec-item--name">{{ item.name }}</div>
            </div>
        </div>
        <div class="classificate__list--title">热门商品</div>
        <div class="classificate__list">
            <div v-for="item in $props.list" :key="item.id" class="classificate__list-item">
                <el-image :src="item.pic" class="classificate__list-item--pic"></el-image>
                <div class="classificate__list-item-info">
                    <div class="classificate__list-item-info--name">{{ item.name }}</div>
                    <div class="classificate__list-item-info--price">￥{{ divTenThousand(item.salePrices[0]) }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(classificate) {
    // width: 285px;
    // height: 26px;
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
    @include e(sec) {
        width: 355px;
        // height: 172px;
        background: #ffffff;
        border-radius: 10px;
        margin: 0 auto;
        display: flex;
        flex-wrap: wrap;
        padding: 10px 14px;
        gap: 30px;
    }
    @include e(sec-item) {
        width: 40px;
        @include m(pic) {
            width: 40px;
            height: 40px;
            margin-bottom: 10px;
            border-radius: 50%;
        }
        @include m(name) {
            color: #333333;
            font-size: 12px;
            text-align: center;
        }
    }
    @include e(list) {
        width: 355px;
        margin: 15px auto 0;
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        @include m(title) {
            margin-left: 10px;
            margin-bottom: 7px;
            width: 56px;
            font-size: 14px;
            border-bottom: 2px solid #fa3534;
        }
    }
    @include e(list-item) {
        width: 172px;
        height: 219px;
        margin-bottom: 10px;
        @include m(pic) {
            width: 172px;
            height: 163px;
        }
    }
    @include e(list-item-info) {
        height: 59px;
        padding: 10px;
        font-size: 11px;
        @include m(name) {
            width: 155px;
            @include utils-ellipsis();
        }
        @include m(price) {
            color: #fa3534;
        }
    }
}
</style>
