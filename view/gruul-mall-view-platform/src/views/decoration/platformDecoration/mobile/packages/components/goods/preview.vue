<script setup lang="ts">
import defaultGoodData from './goods'
import useConvert from '@/AutoImportCustomUse/useConvert'
import ShoppingButton from './shoppingButton.vue'
import Coner from './coner.vue'
import type { PropType } from 'vue'
import { ApiGoodItemType, ListStyle } from './goods'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultGoodData>,
        default() {
            return defaultGoodData
        },
    },
})
const { divTenThousand } = useConvert()

function range(salePrices: string[] | string) {
    if (Array.isArray(salePrices)) {
        if (!salePrices.length) return '0'
        let priceArr: string[] = []
        salePrices.forEach((item) => {
            priceArr.push(String(divTenThousand(item)))
        })
        const delRepetPriceArr = Array.from(new Set(priceArr))
        return delRepetPriceArr.length > 1
            ? `${parseFloat(delRepetPriceArr[0]).toFixed(2)}-${parseFloat(delRepetPriceArr[delRepetPriceArr.length - 1]).toFixed(2)}`
            : parseFloat(delRepetPriceArr[0]).toFixed(2)
    } else {
        return divTenThousand(salePrices).toFixed(2)
    }
}
const goodsList = ref<ApiGoodItemType[] | undefined[]>([])
const pageStyle = computed(() => {
    return {
        padding: `0px 10px`,
        minHeight: '261px',
        fontSize: '15px',
    }
})
/**
 * 动态显示商品边框样式
 */
const goodStyle = computed(() => {
    let margin = ''
    let width = ''
    const { listStyle } = $props.formData
    switch (listStyle) {
        case 'goods-style--four':
            margin = `0px 10px 0px 0px`
            break
        case 'goods-style--two':
            margin = `0px 0px ${10}px 0px`
            width = `calc(50% - ${10 / 2}px)`
            break
        case 'goods-style--five':
            margin = `0px 0px ${10}px 0px`
            width = `calc(50% - ${10 / 2}px)`
            break
        default:
            margin = `10px 0`
            break
    }
    return { margin, width }
})
/**
 * 模式2调整边距
 */
const goodStyle2 = computed(() => {
    const margin = `0px 0px 10px 0px`
    return { margin }
})
watch(
    () => $props.formData,
    () => {
        getGoodsList()
    },
    {
        immediate: true,
        deep: true,
    },
)

const activeClass = (id: string) => {
    Object.assign($props.formData, { currentCategoryId: id })
}
function getGoodsList() {
    const { ponentType = 1, currentCategoryId = '', firstCatList = [], goods = [] } = $props.formData
    // 选择商品数量时对应显示预览效果
    if (ponentType === 1) {
        const item = firstCatList.find((i) => i.platformCategoryFirstId === currentCategoryId)
        if (item && item.productNum) {
            goodsList.value = Array.from({ length: item.productNum }, () => undefined)
        }
    } else if (ponentType === 2) {
        goodsList.value = goods
    }
}

const waterfallHeight = (i: number) => {
    if ($props.formData.listStyle === 'goods-style--five') {
        return { height: [5, -5][i % 2] + 240 + 'px' }
    }
    return {}
}
</script>

<template>
    <div class="goods__ponent-page">
        <div
            v-if="
                ($props.formData.ponentType === 1 && !($props.formData.firstCatList && $props.formData.firstCatList.length)) ||
                ($props.formData.ponentType === 2 && !$props.formData.goods.length)
            "
            class="no__goods-item"
        >
            <img src="https://qiniu-app.qtshe.com/u391.png" />
        </div>
        <!-- 头部分类 s -->
        <div v-if="$props.formData.ponentType === 1 && $props.formData.firstCatList && $props.formData.firstCatList.length" class="tab__bar-box">
            <div class="con">
                <div
                    v-for="(item, idx) in $props.formData.firstCatList"
                    :key="idx"
                    :class="['class__item', item.platformCategoryFirstId === $props.formData.currentCategoryId ? ['', '', 'class__new-active'] : '']"
                    @click="activeClass(item.platformCategoryFirstId)"
                >
                    <span class="item__name">{{ item.platformCategoryFirstName }}</span>
                </div>
            </div>
        </div>
        <!-- 头部分类 e -->
        <!-- 商品主体展示 s -->
        <!-- $props.formData.listStyle === ListStyle['goods-style--two'] && goodsList.length === 1 ? '' : $props.formData.listStyle -->
        <div
            v-if="
                ($props.formData.ponentType === 1 && $props.formData.firstCatList && $props.formData.firstCatList.length) ||
                ($props.formData.ponentType === 2 && $props.formData.goods.length)
            "
            class="goods"
            :class="$props.formData.listStyle === ListStyle['goods-style--two'] && goodsList.length === 1 ? '' : $props.formData.listStyle"
            :style="pageStyle"
        >
            <!-- 大图模式 -->
            <template v-if="$props.formData.listStyle === ListStyle['goods-style--one']">
                <div v-for="(item, idx) in goodsList" :key="idx" :class="['goods-item']">
                    <!-- tag -->
                    <coner :tag-show="$props.formData.showContent.tagShow" :tag-style="$props.formData.showContent.tagStyle"></coner>
                    <!-- tag -->
                    <!-- 大图模式 s -->
                    <div class="goods-item__large_box">
                        <div class="goods-item__large">
                            <img
                                :class="[$props.formData.ponentType === 1 ? 'show__mall' : 'show__big']"
                                src="https://qiniu-app.qtshe.com/u391.png"
                            />
                        </div>
                        <div class="goods-item__large_foot" style="padding: 0 7px">
                            <div class="goods-item__large_foot_left">
                                <div v-show="!!$props.formData?.goodsNameConfig?.rows" class="goods-item__name">
                                    {{ item ? item.productName : '商品名称' }}
                                </div>
                                <div>
                                    <span class="goods-item__price">{{ item ? range(item.salePrices) : '99.00' }}</span>
                                </div>
                            </div>
                            <div class="goods-item__large_foot_right">
                                <shopping-button :button-style="$props.formData.showContent.buttonStyle"></shopping-button>
                            </div>
                        </div>
                        <div class="goods-item__large_foot--placeholder-node"></div>
                    </div>
                </div>
            </template>
            <!-- 大图模式 -->
            <!-- 一行两个 goodStyle2 goodStyle-->
            <template v-else-if="$props.formData.listStyle === ListStyle['goods-style--two']">
                <div
                    v-for="(item, idx) in goodsList"
                    :key="idx"
                    :class="['goods-item']"
                    :style="{ ...(idx === $props.formData.goods.length - 1 ? goodStyle2 : goodStyle) }"
                >
                    <coner :tag-show="$props.formData.showContent.tagShow" :tag-style="$props.formData.showContent.tagStyle"></coner>
                    <div class="goods-item__icon">
                        <div class="ipic" style="border-radius: 10px 10px 0 0">
                            <img
                                :class="[$props.formData.ponentType === 1 ? 'show__mall' : 'show__big']"
                                src="https://qiniu-app.qtshe.com/u391.png"
                            />
                        </div>
                    </div>

                    <div class="goods-item__foot" style="padding: 0 7px">
                        <div class="goods-item__name">
                            {{ item ? item.productName : '商品名称' }}
                        </div>
                        <div class="goods-item__bottom">
                            <div>
                                <span class="goods-item__price">{{ item ? range(item.salePrices) : '99.00' }}</span>
                            </div>
                            <shopping-button :button-style="$props.formData.showContent.buttonStyle"></shopping-button>
                        </div>
                    </div>
                </div>
            </template>
            <!-- 一行两个 -->
            <!-- 详细列表 -->
            <template v-else-if="$props.formData.listStyle === ListStyle['goods-style--three']">
                <div
                    v-for="(item, idx) in goodsList"
                    :key="idx"
                    :class="['goods-item', 'goods-item-three_shadow']"
                    :style="{ ...(idx === $props.formData.goods.length - 1 ? goodStyle2 : goodStyle) }"
                >
                    <coner :tag-show="$props.formData.showContent.tagShow" :tag-style="$props.formData.showContent.tagStyle"></coner>
                    <div class="goods-item__three">
                        <div class="goods-item__three_img">
                            <img src="https://qiniu-app.qtshe.com/u391.png" />
                        </div>
                    </div>

                    <div class="goods-item__foot" style="padding: 0 7px">
                        <div class="goods-item__name">
                            {{ item ? item.productName : '商品名称' }}
                        </div>
                        <div class="goods-item__bottom">
                            <div>
                                <span class="goods-item__price">{{ item ? range(item.salePrices) : '99.00' }}</span>
                            </div>
                            <shopping-button :button-style="$props.formData.showContent.buttonStyle"></shopping-button>
                        </div>
                    </div>
                </div>
            </template>
            <!-- 详细列表 -->
            <!-- 横向滑动 -->
            <template v-else-if="$props.formData.listStyle === ListStyle['goods-style--four']">
                <div
                    v-for="(item, idx) in goodsList"
                    :key="idx"
                    :class="['goods-item', 'goods-item-three_shadow']"
                    :style="{ ...(idx === $props.formData.goods.length - 1 ? goodStyle2 : goodStyle) }"
                >
                    <coner :tag-show="$props.formData.showContent.tagShow" :tag-style="$props.formData.showContent.tagStyle"></coner>
                    <div class="goods-item__four">
                        <div class="goods-item__four_img">
                            <img src="https://qiniu-app.qtshe.com/u391.png" />
                        </div>
                    </div>

                    <div class="goods-item__foot" style="padding: 0 7px">
                        <div class="goods-item__name" style="font-weight: 600; font-size: 11px">
                            {{ item ? item.productName : '商品名称' }}
                        </div>
                        <div class="goods-item__bottom">
                            <div>
                                <span class="goods-item__price">{{ item ? range(item.salePrices) : '99.00' }}</span>
                            </div>
                            <shopping-button :button-style="$props.formData.showContent.buttonStyle"></shopping-button>
                        </div>
                    </div>
                </div>
            </template>
            <!-- 横向滑动 -->
            <!-- 瀑布 -->
            <template v-else>
                <div
                    v-for="(item, idx) in goodsList"
                    :key="idx"
                    :class="['goods-item']"
                    :style="{ ...waterfallHeight(idx), ...(idx === $props.formData.goods.length - 1 ? goodStyle2 : goodStyle) }"
                >
                    <coner :tag-show="$props.formData.showContent.tagShow" :tag-style="$props.formData.showContent.tagStyle"></coner>
                    <div class="goods-item__icon">
                        <div class="ipic" style="border-radius: 10px 10px 0 0">
                            <img
                                :class="[$props.formData.ponentType === 1 ? 'show__mall' : 'show__big']"
                                src="https://qiniu-app.qtshe.com/u391.png"
                            />
                        </div>
                    </div>

                    <div class="goods-item__foot" style="padding: 0 7px">
                        <div class="goods-item__name">
                            {{ item ? item.productName : '商品名称' }}
                        </div>
                        <div class="goods-item__bottom" style="height: 20px; margin-top: 10px">
                            <div>
                                <span class="goods-item__price">{{ item ? range(item.salePrices) : '99.00' }}</span>
                            </div>
                            <shopping-button :button-style="$props.formData.showContent.buttonStyle"></shopping-button>
                        </div>
                    </div>
                </div>
            </template>
            <!-- 瀑布 -->
        </div>
        <!-- 商品主体展示 e -->
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/css/decoration/goods.scss';
$color-red: #fa3534;
.goods__ponent-page {
    .tab__bar-box {
        height: 50px;
        width: 100%;
        overflow: hidden;

        .con {
            width: auto;
            height: 60px;
        }

        .class__item {
            display: inline-flex;
            align-items: center;
            position: relative;
            color: #6b6b6b;
            font-size: 12px;
            padding: 0px 12px;
            padding-top: 15px;
            cursor: pointer;
            padding-bottom: 4px;
            span {
                display: inline-block;
                border-bottom: 3px solid transparent;
                border-radius: 1px;
            }
            &::before {
                content: '';
                position: absolute;
                right: 0;
                width: 1px;
                height: 14px;
                background: #ccc;
            }
        }

        .class__new-active {
            color: $color-red;
            font-size: 12px;
            font-weight: 600;
            span {
                border-color: $color-red;
            }
        }

        .first__class {
            padding-left: 0;
            background-size: calc(100% - 11px) 8px;
            background-position: 0px 1.16em;
        }
    }

    .goods-item__icon {
        width: 100%;
        height: 160px;
        .ipic {
            display: inline-block;
            width: 100%;
            height: 100%;
            background-color: rgba(233, 247, 253, 1);
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 10px;
            .show__mall {
                display: inline-block;
                width: 44px;
                height: 46px;
            }

            .show__big {
                display: inline-block;
                width: 100%;
                height: 100%;
            }
        }
    }

    .goods-style--three {
        .goods-item__icon {
            height: 128px;
            width: 128px;
            margin-right: 10px;
            flex: none;

            .ipic {
                height: 128px;
            }
        }
    }

    .spellpre__goods--delivery {
        color: #a3a3a3;
        font-size: 12px;
        font-weight: 400;
        padding-top: 10px;
    }
}
</style>
