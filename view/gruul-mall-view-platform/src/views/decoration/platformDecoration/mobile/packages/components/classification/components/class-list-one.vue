<script setup lang="ts">
import type { PropType } from 'vue'
import ClassListItem from './class-list-item.vue'
import type { ApiCategoryData, CommodityItem, DeCategoryItem, DeCategoryType } from '../classification'
import { useDecorationStore } from '@/store/modules/decoration'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import { LinkSelectItem } from '../../../../types'

const $props = defineProps({
    config: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return {}
        },
    },
    secondCategoryList: {
        type: Array as PropType<DeCategoryItem[]>,
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
    totalHeight: {
        type: Number,
        default: 0,
    },
    categoryAd: {
        type: Array as PropType<{ img: string; link: LinkSelectItem }[]>,
        default() {
            return []
        },
    },
})
const $decorationStore = useDecorationStore()
const $emit = defineEmits(['changeNav'])
const currentChooseIndex = ref(0)
const scrollContainer = ref<HTMLElement | null>(null)

const handleChooseUnit = (index: number, item: DeCategoryItem) => {
    currentChooseIndex.value = index
    if ($props.large === 2) {
        $emit('changeNav', { index, id: item.platformCategorySecondId })
        return
    }
    // 滚动到对应的分类位置
    nextTick(() => {
        if (scrollContainer.value) {
            const targetElement = scrollContainer.value.querySelector(`[data-category-id="${item.platformCategorySecondId}"]`)
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start',
                })
            }
        }
    })
}
</script>

<template>
    <div class="classificate">
        <!-- 铺满整个广告位 -->
        <van-swipe
            v-if="$props.config.categoryShow && $props.categoryAd && $props.categoryAd.length > 0"
            class="classificate__advertisement"
            :show-indicators="false"
            :autoplay="3000"
        >
            <van-swipe-item v-for="(item, index) in $props.categoryAd" :key="index" class="homeSwiper-swiper__item">
                <van-image :src="item.img" alt="" fit="fill" width="100%" height="100%" radius="5px"> </van-image>
            </van-swipe-item>
        </van-swipe>
        <!-- </div> -->
        <div v-if="$props.config.navShow" class="classificate__header-container">
            <div class="classificate__header">
                <div
                    v-for="(item, index) in $props.secondCategoryList || []"
                    :key="item.platformCategorySecondId"
                    class="classificate__header-title"
                    :style="{
                        backgroundColor: currentChooseIndex === index ? '#FFECEC' : '#fff',
                        color: currentChooseIndex === index ? '#FA3534' : '#232323',
                        borderColor: currentChooseIndex === index ? '#FA3534' : '#fff',
                    }"
                    @click="handleChooseUnit(index, item)"
                >
                    {{ item.platformCategorySecondName }}
                </div>
            </div>
            <div
                style="border-radius: 50%; padding: 16rpx 22rpx; text-align: center; background-color: #f4f4f4; font-size: 20rpx; color: #979797"
            ></div>
        </div>
        <div :style="{ overflow: 'scroll' }" class="classificate__list-container">
            <template v-if="$props.large === 2">
                <div v-if="$props.list.length > 0" class="classificate__list">
                    <class-list-item v-for="item in $props.list" :key="item.id" :is-large="$props.large" :config="config" :info="item" />
                </div>
            </template>
            <div v-else ref="scrollContainer">
                <div
                    v-for="item in $props.secondCategoryList"
                    :key="item.platformCategorySecondId"
                    class="classificate__unit"
                    :data-category-id="item.platformCategorySecondId"
                >
                    <div class="classificate__unit-title">{{ item.platformCategorySecondName }}</div>
                    <div class="classificate__unit-content">
                        <div v-for="ite in item.children" :key="ite.platformCategoryThirdId" class="classificate__unit-content-item">
                            <el-image class="classificate__unit-content-item--img" :src="ite.platformCategoryThirdImg">
                                <template #error>
                                    <div class="image-slot">
                                        <el-icon><icon-picture /></el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div class="classificate__unit-content-item--name">{{ ite.platformCategoryThirdName }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(classificate) {
    width: calc(100% - 106px);
    box-sizing: border-box;
    background: #f4f4f4;
    color: #3c3c3c;
    &::-webkit-scrollbar {
        width: 0 !important;
    }

    @include e(advertisement) {
        height: 100px;
        background: #fff;
        border-radius: 5px;
        margin-top: 10px;
        margin-right: 10px;
    }
    @include e(unit) {
        background: #fff;
        margin-bottom: 10px;
        padding: 10px;
        border-radius: 10px;
        margin-right: 10px;
    }
    @include e(unit-title) {
        font-weight: bold;
        font-size: 15px;
        padding-bottom: 10px;
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
            color: #979797;
            text-align: center;
            margin-top: 6px;
            text-align: left;
        }
    }
    @include e(header-container) {
        overflow-x: auto;
        scroll-behavior: smooth;
        -webkit-overflow-scrolling: touch;
        scrollbar-width: none;
        &::-webkit-scrollbar {
            display: none;
        }
    }
    @include e(header) {
        display: flex;
        align-items: center;
        padding: 10px 10px 0 0;
        width: max-content;
        min-width: 100%;

        @include e(header-title) {
            margin-right: 10px;
            padding: 4px 12px;
            white-space: nowrap;
            flex-shrink: 0;
            cursor: pointer;
            border-radius: 15px;
        }
    }

    .classificate__list {
        background-color: #fff;
        padding: 10px 10px 0 10px;
        border-radius: 5px;
        margin-right: 10px;
    }
    @include e(list-container) {
        margin-top: 10px;
        &::-webkit-scrollbar {
            display: none;
        }
    }
}

:deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-secondary);
    font-size: 30px;
}
:deep(.image-slot .el-icon) {
    font-size: 30px;
}
</style>
