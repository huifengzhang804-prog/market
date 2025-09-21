<script lang="ts" setup>
import defaultSeckillData, { seckillGoodType } from './sec-kill'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSeckillData>,
        default: defaultSeckillData,
    },
})
const { divTenThousand } = useConvert()
// 商品角标 1新品 2热卖 3抢购
const srcs = [
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/37a28e49acb448fc8618d5e72851b027.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/1622a28ef72040f9a2f367a2efa7c32d.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/0f33444422b14a8e980cc50d0011e2d0.png',
]
const cornerRowClass = ['seckill-row__corner--new', 'seckill-row__corner--hot', 'seckill-row__corner--purchase']
const cornerColClass = ['seckill-col__corner--new', 'seckill-col__corner--hot', 'seckill-col__corner--purchase']
const getCornerInfo = computed(() => {
    return {
        cornerColClass: cornerColClass[$props.formData.tagStyle - 1],
        cornerRowClass: cornerRowClass[$props.formData.tagStyle - 1],
        url: srcs[$props.formData.tagStyle - 1],
    }
})
const getGoodStyle = computed(() => {
    const classs = ['good-one', 'good-two', 'good-three']
    return classs[$props.formData.goodStyle - 1]
})
const goodList: seckillGoodType[] = []

initGoodList()

function initGoodList() {
    for (let i = 0; i < 3; i++) {
        goodList.push({
            maxPrice: '?.??',
            minPrice: '?.??',
            productId: '',
            productName: `商品-${i}`,
            productPic: 'https://qiniu-app.qtshe.com/u391.png',
        })
    }
}
</script>

<template>
    <!-- 横向滑动 -->
    <div :class="[getGoodStyle, 'seckill']">
        <div :style="{ padding: `0 ${$props.formData.padding}px` }" class="seckill__title">
            <div class="seckill__title--left">
                <span class="f20 fb">限时秒杀</span>
            </div>
            <div class="seckill__title--more">查看更多</div>
        </div>
        <!-- 横向展示 -->
        <div
            v-if="$props.formData.display === 1"
            :class="['seckill__row', 'item', $props.formData.goodStyle === 2 ? 'boxShadow' : '']"
            :style="{ padding: `0 ${$props.formData.padding}px` }"
        >
            <div
                v-for="(item, index) in goodList"
                :key="index"
                :style="{ marginRight: $props.formData.marginBottom + 'px' }"
                class="seckill__row-item"
            >
                <img :src="item.productPic" class="seckill__row-item--img" />
                <div class="seckill__row-item--price">￥{{ item.minPrice }}</div>
                <!-- 商品角标 -->
                <img v-if="$props.formData.showtag" :class="getCornerInfo.cornerRowClass" :src="getCornerInfo.url" />
            </div>
        </div>
        <!-- 详情展示 -->
        <div
            v-else
            :class="['seckill__col', 'item', $props.formData.goodStyle === 2 ? 'boxShadow' : '']"
            :style="{ padding: `0 ${$props.formData.padding}px` }"
        >
            <div
                v-for="(item, index) in goodList"
                :key="index"
                :style="{ marginBottom: $props.formData.marginBottom + 'px' }"
                class="seckill__col-item"
            >
                <img :src="item.productPic" class="seckill__col-item--img" />
                <div class="seckill__col-item--right">
                    <div>
                        <div :class="['seckill__col-item--head', $props.formData.textStyle === 2 ? 'fb' : '']">{{ item.productName }}</div>
                        <!--                        <div class="seckill__col-item&#45;&#45;subhead">asdas</div>-->
                    </div>
                    <div class="seckill__col-item--bottom">
                        <div class="seckill__col-item--price">￥{{ item.minPrice }}</div>
                        <div class="seckill__col-item--btn">马上抢购</div>
                    </div>
                </div>
                <!-- 商品角标 -->
                <img v-if="$props.formData.showtag" :class="getCornerInfo.cornerColClass" :src="getCornerInfo.url" />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(seckill) {
    box-sizing: border-box;
    color: #333;
    @include e(title) {
        height: 50px;
        margin-bottom: 6px;
        background: #fff;
        @include flex(space-between);
        @include m(left) {
            font-size: 14px;
            @include flex(flex-start);
        }
        @include m(circle) {
            display: inline-block;
            width: 20px;
            height: 20px;
            text-align: center;
            line-height: 20px;
            background: #000;
            border-radius: 50%;
            color: #fff;
        }
        @include m(m) {
            margin: 0 10px;
        }
        @include m(more) {
            color: #838383;
            &::after {
                content: '>';
                display: inline-block;
                margin-left: 4px;
                font-size: 16px;
            }
        }
    }
    // 横向展示样式
    @include e(row) {
        display: flex;
        width: 100%;
        overflow-x: auto;
        & > .seckill__row-item:last-child {
            margin-right: 0 !important;
        }
    }
    @include e(row-item) {
        flex-shrink: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-right: 10px;
        position: relative;
        @include m(img) {
            width: 72px;
            height: 72px;
            margin-bottom: 4px;
        }
        @include m(price) {
            width: 55px;
            height: 18px;
            text-align: center;
            line-height: 18px;
            color: #fff;
            font-size: 14px;
            background: url('https://obs.xiaoqa.cn/gruul/20221126/441a63322cca4a02bc1e4d4f5d4b54b9.png') no-repeat 100% 100%;
        }
    }
    @include e(col) {
        & > .seckill__col-item:last-child {
            margin-bottom: 0 !important;
        }
    }
    // 详情
    @include e(col-item) {
        @include flex;
        position: relative;
        @include m(img) {
            width: 92px;
            height: 92px;
            margin-right: 8px;
        }
        @include m(right) {
            flex: 1;
            display: flex;
            width: 0;
            flex-direction: column;
            justify-content: space-around;
            height: 92px;
        }
        @include m(head) {
            font-size: 14px;
            width: 100%;
            @include utils-ellipsis;
        }
        @include m(subhead) {
            font-size: 12px;
            color: #525252;
            margin-top: 2px;
        }
        @include m(bottom) {
            @include flex(space-between);
        }
        @include m(price) {
            font-size: 16px;
            color: #dd3324;
            font-weight: bold;
        }
        @include m(btn) {
            width: 66px;
            height: 33px;
            text-align: center;
            line-height: 33px;
            background: #e94927;
            color: #fff;
            border-radius: 17px;
            font-size: 12px;
            font-weight: bold;
        }
    }
}

.f20 {
    font-size: 20px;
}

.fb {
    font-weight: bold;
}

@include b(seckill-row) {
    @include e(corner) {
        @include m(new) {
            width: 32px;
            height: 16px;
            position: absolute;
            top: 4px;
            left: 0;
        }
        @include m(hot) {
            width: 32px;
            height: 35px;
            position: absolute;
            left: 0;
            top: 0;
        }
        @include m(purchase) {
            width: 34px;
            height: 20px;
            position: absolute;
            left: 6px;
            top: 6px;
        }
    }
}

@include b(seckill-col) {
    @include e(corner) {
        @include m(purchase) {
            width: 34px;
            height: 20px;
            position: absolute;
            left: 6px;
            top: 6px;
        }
        @include m(hot) {
            width: 32px;
            height: 35px;
            position: absolute;
            left: 0;
            top: 0;
        }
        @include m(new) {
            width: 32px;
            height: 16px;
            position: absolute;
            top: 4px;
            left: 0;
        }
    }
}

.good-one {
    background: #fff;
}

.good-two {
    background: #f8f8f8;

    & .seckill__row-item,
    .seckill__col-item {
        box-shadow: 0px 1px 56px 6px rgb(109 109 109 / 10%);
    }
}

.good-three {
    background: #fff;

    & .seckill__row-item,
    .seckill__col-item {
        border: 1px solid #eee;
    }
}
</style>
