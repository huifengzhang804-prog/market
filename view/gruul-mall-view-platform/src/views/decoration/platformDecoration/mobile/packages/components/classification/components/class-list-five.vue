<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import type { CommodityItem, DeCategoryItem, DeCategoryType } from '../classification'
import type { PropType } from 'vue'
import { Picture as IconPicture } from '@element-plus/icons-vue'

const $props = defineProps({
    config: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return {}
        },
    },
    categoryList: {
        type: Array as PropType<DeCategoryItem[]>,
        default() {
            return []
        },
    },
    secondCategoryList: {
        type: Array as PropType<DeCategoryItem[]>,
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
    goodsShow: {
        type: Array as PropType<number[]>,
        default() {
            return []
        },
    },
})

const currentChooseIndex = ref(0)

// 使用响应式数据来管理商品列表的状态
const goodsListState = ref<Map<string, { activeImg: number; currentPic: string }>>(new Map())

const goodsList = computed(() => {
    return $props.list.map((item) => {
        // 确保每个商品都有状态
        if (!goodsListState.value.has(item.id)) {
            goodsListState.value.set(item.id, {
                activeImg: 0,
                currentPic: item.pic,
            })
        }

        const state = goodsListState.value.get(item.id)!
        return {
            ...item,
            activeImg: state.activeImg,
            pic: state.currentPic || item.pic, // 使用状态中的图片，如果没有则使用原始图片
        }
    })
})

const $emit = defineEmits(['changeTab', 'changeNav'])
const { divTenThousand } = useConvert()

const handleChangeTab = (index: number, firstLevelId: string) => {
    $emit('changeTab', { index, firstLevelId })
}

const handleChangeSec = (index: number, id: string) => {
    currentChooseIndex.value = index
    $emit('changeNav', { id })
}

const handleChangeSpecImg = (index: number, specImg: string, item: CommodityItem) => {
    // 更新状态
    if (goodsListState.value.has(item.id)) {
        const state = goodsListState.value.get(item.id)!
        state.activeImg = index
        state.currentPic = specImg
    } else {
        goodsListState.value.set(item.id, {
            activeImg: index,
            currentPic: specImg,
        })
    }
}
</script>

<template>
    <div class="classificate">
        <div class="classificate__sort">
            <div
                v-for="(item, index) in $props.categoryList"
                :key="item.platformCategoryFirstId"
                :class="{ 'classificate__sort-item_active': $props.currentChooseId === item.platformCategoryFirstId }"
                class="classificate__sort-item"
                @click="handleChangeTab(index, item.platformCategoryFirstId)"
            >
                {{ item.platformCategoryFirstName }}
            </div>
        </div>

        <div class="classificate__sec">
            <div
                v-for="(item, index) in $props.secondCategoryList"
                :key="item.platformCategorySecondId"
                class="classificate__sec-item"
                @click="handleChangeSec(index, item.platformCategorySecondId)"
            >
                <el-image :src="item.platformCategorySecondImg" class="classificate__sec-item--pic">
                    <template #error>
                        <div class="image-slot">
                            <el-icon><icon-picture /></el-icon>
                        </div>
                    </template>
                </el-image>
                <div class="classificate__sec-item--name" :style="{ color: currentChooseIndex === index ? '#FA3534' : '#666' }">
                    {{ item.platformCategorySecondName }}
                </div>
            </div>
        </div>
        <div v-if="$props.config.commodityShow === 2" class="classificate__list">
            <div v-for="item in goodsList" :key="item.id" class="classificate__list-item">
                <el-image :src="item.pic" class="classificate__list-item--pic"></el-image>
                <div class="classificate__list-item-info">
                    <div class="classificate__list-item-info--name">
                        <text
                            v-if="item.shopType !== 'ORDINARY' && $props.goodsShow.includes(1)"
                            class="classificate__list-item-info--tag"
                            :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                        >
                            {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                        </text>
                        {{ item.name }}
                    </div>

                    <div v-if="item.specImages && $props.goodsShow.includes(2) && item.specImages.length > 1" class="horizontal-scroll">
                        <div class="classificate__list-item-info--specImg">
                            <div
                                v-for="(specImg, index) in item.specImages"
                                :key="index"
                                class="classificate__list-item-info--specImg-item"
                                :style="{ border: item.activeImg === index ? '2px solid #fb232f' : 'none' }"
                                @click="handleChangeSpecImg(index, specImg, item)"
                            >
                                <van-image :src="specImg" />
                            </div>
                        </div>
                    </div>
                    <div v-if="item.salesCount && $props.goodsShow.includes(3)" class="classificate__list-item-info--sale">
                        已售 <text>{{ item.salesCount > 10000 ? divTenThousand(item.salesCount) : item.salesCount }}</text
                        ><text v-if="item.salesCount > 10000">万</text>
                    </div>
                    <div v-if="$props.goodsShow.includes(4)" class="classificate__list-item-info--discount">
                        <div>优惠券折扣</div>
                        <div v-if="item.freeShipping">包邮</div>
                    </div>
                    <div class="classificate__list-item-info-price">
                        <div class="classificate__list-item-info-price--num">
                            <text style="font-size: 16px; font-weight: 500; color: #fb232f">
                                {{ String(divTenThousand(item.salePrices[0])).split('.')[0] }}
                            </text>
                            <text v-if="String(divTenThousand(item.salePrices[0])).split('.')[1]" style="font-size: 12px">
                                .{{ String(divTenThousand(item.salePrices[0])).split('.')[1] }}
                            </text>
                        </div>
                        <QIcon
                            v-if="$props.goodsShow.includes(5)"
                            name="icon-gouwuche5"
                            class="classificate__list-item-info-price--shopCar"
                            size="18px"
                        />
                    </div>
                </div>
            </div>
        </div>
        <div v-else class="classificate__list2" style="background-color: #fff">
            <div v-for="item in goodsList" :key="item.id" class="classificate__list2__item">
                <el-image :src="item.pic" class="classificate__list2__item--pic"> </el-image>
                <view class="classificate__list2__item--info">
                    <div class="classificate__list2__item--info--name">
                        <text
                            v-if="item.shopType !== 'ORDINARY' && $props.goodsShow.includes(1)"
                            class="classificate__list2__item--info--tag"
                            :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                        >
                            {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }} </text
                        >{{ item.name }}
                    </div>
                    <div
                        v-if="item.specImages && $props.goodsShow.includes(2) && item.specImages.length > 1"
                        class="horizontal-scroll"
                        style="display: flex; align-items: center; gap: 6px; width: calc(100% - 16px); overflow-x: auto"
                    >
                        <div v-for="(specImg, index) in item.specImages" :key="index">
                            <van-image :src="specImg" style="width: 30px; height: 30px"></van-image>
                        </div>
                    </div>
                    <div v-if="item.salesCount && $props.goodsShow.includes(3)" class="classificate__list2__item--info--sale">
                        已售 <text>{{ item.salesCount > 10000 ? divTenThousand(item.salesCount) : item.salesCount }}</text
                        ><text v-if="item.salesCount > 10000">万</text>
                    </div>
                    <div v-if="$props.goodsShow.includes(4)" class="classificate__list2__item--info--discount">
                        <div>优惠券折扣</div>
                        <div v-if="item.freeShipping">包邮</div>
                    </div>
                    <div class="classificate__list2__item--info--price">
                        <div class="classificate__list2__item--info--price--num">
                            <text style="font-size: 16px; font-weight: 500; color: #fb232f">
                                {{ String(divTenThousand(item.salePrices[0])).split('.')[0] }}
                            </text>
                            <text v-if="String(divTenThousand(item.salePrices[0])).split('.')[1]" style="font-size: 12px">
                                .{{ String(divTenThousand(item.salePrices[0])).split('.')[1] }}
                            </text>
                        </div>
                        <QIcon
                            v-if="$props.goodsShow.includes(5)"
                            name="icon-gouwuche5"
                            class="classificate__list2__item--info--price--shopCar"
                            size="18px"
                        />
                    </div>
                </view>
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
        padding: 0 12px;
        box-sizing: border-box;
        @include flex(flex-start);
        overflow-x: auto;
        background: #ffffff;
        &::-webkit-scrollbar {
            display: none;
            width: 0 !important;
        }
    }
    @include e(sort-item) {
        flex-shrink: 0;
        padding: 8px 0;
        margin-right: 29px;
        font-size: 15px;
        cursor: pointer;
        font-family: Microsoft YaHei;
        font-weight: 400;
        color: #000;
    }

    @include e(sort-item_active) {
        font-weight: 600;
        color: #fa3534;
        font-family: Microsoft YaHei;
        position: relative;
        &::after {
            content: '';
            position: absolute;
            left: 50%;
            bottom: 0px;
            transform: translateX(-50%);
            width: 15px;
            height: 5px;
            background: #fa3534;
            border-radius: 1px 1px 5px 5px;
        }
    }

    @include e(sec) {
        padding: 15px 12px;
        background: #ffffff;
        // 向下50px白色阴影渐渐变淡
        box-shadow: 0px 158px 120px 140px rgb(255, 255, 255);
        display: flex;
        gap: 19px;
        overflow-x: auto;
        &::-webkit-scrollbar {
            display: none;
        }
    }
    @include e(sec-item) {
        width: 55px;
        cursor: pointer;
        @include m(pic) {
            width: 55px;
            height: 55px;
            margin-bottom: 5px;
            border-radius: 5px;
        }
        @include m(name) {
            width: 55px;
            color: #666;
            font-size: 12px;
            text-align: center;
            padding: 0 3px;
        }
    }
    @include e(list) {
        padding: 0 12px;
        column-count: 2;
        column-gap: 10px;
        padding-top: 10px;
    }
    @include e(list-item) {
        width: 100%;
        margin-bottom: 10px;
        background-color: #fff;
        overflow: hidden;
        -webkit-column-break-inside: avoid;
        page-break-inside: avoid;
        break-inside: avoid;
        border-radius: 5px;
        background-color: #fff;
        @include m(pic) {
            display: block;
            width: 100%;
            height: auto;
            border-radius: 5px;
        }
    }
    @include e(list-item-info) {
        padding: 8px 5px;
        font-size: 11px;

        @include m(tag) {
            font-size: 10px;
            color: #fff;
            padding: 1px 2px;
            border-radius: 2px;
            margin-right: 2px;
            position: relative;
            top: -1px;
        }

        @include m(name) {
            font-size: 13px;
            color: #333;
            line-height: 1.4;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }

        @include m(specImg) {
            @include flex(flex-start);
            margin: 5px 0;
            overflow-x: auto;
            align-items: center;
            width: max-content;
            min-width: 100%;
            overflow-y: hidden;
            scroll-behavior: smooth;
            -webkit-overflow-scrolling: touch;
            scrollbar-width: none;
            -ms-overflow-style: none;

            &::-webkit-scrollbar {
                display: none;
            }
        }
        @include m(specImg-item) {
            width: 33px;
            height: 33px;
            margin-right: 7px;
            flex-shrink: 0;
            cursor: pointer;
            overflow: hidden;

            &:last-child {
                margin-right: 0;
            }

            .van-image {
                width: 100%;
                height: 100%;
                border-radius: 4px;
            }
        }

        @include m(sale) {
            font-size: 12px;
            color: #a6a6a6;
            margin-top: 2px;
        }

        @include m(discount) {
            @include flex(flex-start);
            gap: 6px;
            font-size: 10px;
            margin-top: 7px;
            color: #fa3534;
            & > div {
                padding: 0px 3px;
                border-radius: 2px;
                border: 1px solid #fa3534;
            }
            & > div:last-child {
                color: #e3600a;
                border: 1px solid #e3600a;
            }
        }
    }

    @include e(list-item-info-price) {
        margin-top: 7px;
        @include flex(space-between, center);
        font-family: Kingsoft_Cloud_Font;
        @include m(num) {
            color: #fb232f;
            font-size: 16px;
            font-weight: 500;
            &::before {
                content: '￥';
                font-size: 12px;
            }
        }
        @include m(shopCar) {
            color: #fb232f;
            padding-right: 3px;
        }
    }
}
@include b(classificate__list2) {
    display: flex;
    flex-direction: column;
    @include e(item) {
        padding: 10px;
        width: 100%;
        height: 100%;
        @include flex(flex-start);
        @include m(pic) {
            width: 115px;
            height: 115px;
            border-radius: 5px;
        }

        @include m(info) {
            height: 115px;
            padding-left: 10px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            width: calc(100% - 115px);

            @include m(name) {
                font-size: 13px;
                font-family: LXGW Neo XiHei;
                color: #232323;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
            }

            @include m(tag) {
                font-size: 10px;
                color: #fff;
                padding: 1px 2px;
                border-radius: 2px;
            }

            @include m(sale) {
                font-size: 12px;
                color: #a6a6a6;
            }

            @include m(specImg) {
                @include flex(flex-start);
                overflow-x: auto;
                align-items: center;
                width: max-content;
                min-width: 100%;
                overflow-y: hidden;
                scroll-behavior: smooth;
                -webkit-overflow-scrolling: touch;
                scrollbar-width: none;
                -ms-overflow-style: none;

                &::-webkit-scrollbar {
                    display: none;
                }
            }
            @include m(specImg-item) {
                width: 33px;
                height: 33px;
                margin-right: 7px;
                flex-shrink: 0;
                cursor: pointer;
                overflow: hidden;

                &:last-child {
                    margin-right: 0;
                }

                .van-image {
                    width: 100%;
                    height: 100%;
                    border-radius: 4px;
                }
            }

            @include m(discount) {
                @include flex(flex-start);
                gap: 6px;
                font-size: 10px;
                color: #fa3534;
                & > div {
                    padding: 0px 3px;
                    border-radius: 2px;
                    border: 1px solid #fa3534;
                }
                & > div:last-child {
                    color: #e3600a;
                    border: 1px solid #e3600a;
                }
            }

            @include m(price) {
                @include flex(space-between, center);
                font-family: Kingsoft_Cloud_Font;
                @include m(num) {
                    color: #fb232f;
                    font-size: 16px;
                    font-weight: 500;
                    &::before {
                        content: '￥';
                        font-size: 12px;
                    }
                }
                @include m(shopCar) {
                    color: #fb232f;
                    padding-right: 3px;
                }
            }
        }
    }
}
.horizontal-scroll {
    overflow-x: auto;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
    &::-webkit-scrollbar {
        display: none;
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
