<script lang="ts" setup>
import { computed, ref, type PropType } from 'vue'
import Countdown from './countdown.vue'
import QPrice from '@/components/q-price/index.vue'
import DateUtils from '@/utils/date'
import { doGetSeckillProductsOfRound, doGetSeckillRounds } from '@/apis/plugin/secKill'
import { type SeckillDataType, SeckillQueryStatus, SeckillStatus, type SeckillRoundProductVO, type SeckillRoundVO } from '@/apis/plugin/secKill/model'
import { jumpGoods } from '@/utils/navigateToShopInfo'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<SeckillDataType>,
    default: () => {
      return {}
    },
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})

//场次信息
const round = ref<SeckillRoundVO>({
  time: {
    start: '',
    end: '',
  },
  status: SeckillQueryStatus.NOT_START,
})
//场次商品列表
const products = ref<SeckillRoundProductVO[]>([])
const dateTool = new DateUtils()
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
    cornerColClass: cornerColClass[$props.decorationProperties.tagStyle - 1],
    cornerRowClass: cornerRowClass[$props.decorationProperties.tagStyle - 1],
    url: srcs[$props.decorationProperties.tagStyle - 1],
  }
})
const getGoodStyle = computed(() => {
  const classs = ['good-one', 'good-two', 'good-three']
  return classs[$props.decorationProperties.goodStyle - 1]
})

initSeckill()

const handleNavToMore = () => {
  let url = '/pluginPackage/scondsKill/views/SecondsKill'
  if ($props.shopId) {
    url += `?shopId=${$props.shopId}`
  }
  uni.navigateTo({
    url: url,
  })
}
const handleNavToGood = (productId: Long, shopId: Long) => {
  jumpGoods(shopId, productId)
}

async function initSeckill() {
  const shopId = $props.shopId || undefined
  const { code: roundCode, data: roundData } = await doGetSeckillRounds({
    shopId,
    current: 1,
    size: 1,
  })
  if (roundCode !== 200 || !roundData?.records.length) {
    return
  }
  const curRound = roundData.records[0]
  round.value = curRound
  const { code, data } = await doGetSeckillProductsOfRound({
    shopId,
    start: curRound.time.start,
    size: 4,
    current: 1,
  })
  if (code !== 200 || !data) {
    round.value.time.start = ''
    return
  }
  products.value = data.records
}
</script>

<template>
  <!-- 横向滑动 -->
  <view v-if="round.time.start" :class="[getGoodStyle, 'seckill']">
    <view :style="{ padding: `0 ${$props.decorationProperties.padding * 2}rpx` }" class="seckill__title" @click="handleNavToMore">
      <view class="seckill__title--left">
        <text class="f20 fb">限时秒杀</text>
        <text class="seckill__title--m">{{ SeckillStatus[round.status].timerDesc }}</text>
        <Countdown :is-start="false" :start-time="SeckillStatus[round.status].timePick(round)" @end="initSeckill" />
      </view>
      <view class="seckill__title--more"></view>
    </view>
    <!-- 横向展示 -->
    <view
      v-if="$props.decorationProperties.display === 1"
      :class="['seckill__row', $props.decorationProperties.goodStyle === 2 ? 'boxShadow' : '']"
      :style="{ padding: `0 ${$props.decorationProperties.padding * 2}rpx 40rpx ${$props.decorationProperties.padding * 2}rpx` }"
    >
      <view
        v-for="item in products"
        :key="item.shopId + '-' + item.productId"
        :style="{ marginRight: $props.decorationProperties.marginBottom * 2 + 'rpx' }"
        class="seckill__row-item"
        @click="handleNavToGood(item.productId, item.shopId)"
      >
        <img :src="item.productImage" alt="" class="seckill__row-item--img" />
        <view class="seckill__row-item--price">
          <q-price :price="item.minPrice" font-size="32rpx" unit="¥" />
        </view>
        <!-- 商品角标 -->
        <img v-if="$props.decorationProperties.showtag" :class="getCornerInfo.cornerRowClass" :src="getCornerInfo.url" />
      </view>
    </view>
    <!-- 详情展示 -->
    <view
      v-else
      :class="['seckill__col', $props.decorationProperties.goodStyle === 2 ? 'boxShadow' : '']"
      :style="{ padding: `0 ${$props.decorationProperties.padding * 2}rpx` }"
    >
      <view
        v-for="item in products"
        :key="item.shopId + '-' + item.productId"
        :style="{ marginBottom: $props.decorationProperties.marginBottom * 2 + 'rpx' }"
        class="seckill__col-item"
        @click="handleNavToGood(item.productId, item.shopId)"
      >
        <img :src="item.productImage" alt="" class="seckill__col-item--img" />
        <view class="seckill__col-item--right">
          <view>
            <view :class="['seckill__col-item--head', $props.decorationProperties.textStyle === 2 ? 'fb' : '']">{{ item.productName }} </view>
            <view class="seckill__col-item--subhead">&nbsp;</view>
          </view>
          <view class="seckill__col-item--bottom">
            <view class="seckill__col-item--price">
              <q-price :price="item.minPrice" font-size="32rpx" unit="¥" />
            </view>
            <view class="seckill__col-item--btn">{{ SeckillStatus[round.status].buyButtonText }}</view>
          </view>
        </view>
        <!-- 商品角标 -->
        <img v-if="$props.decorationProperties.showtag" :class="getCornerInfo.cornerColClass" :src="getCornerInfo.url" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(seckill) {
  box-sizing: border-box;
  color: #333;
  @include e(title) {
    height: 100rpx;
    margin-bottom: 12rpx;
    background: #fff;
    @include flex(space-between);
    @include m(left) {
      font-size: 28rpx;
      @include flex(flex-start);
    }
    @include m(circle) {
      display: inline-block;
      width: 40rpx;
      height: 40rpx;
      text-align: center;
      line-height: 40rpx;
      background: #000;
      border-radius: 50%;
      color: #fff;
    }
    @include m(m) {
      margin: 0 20rpx;
    }
    @include m(more) {
      color: #838383;
      &::after {
        content: '>';
        display: inline-block;
        margin-left: 8rpx;
        font-size: 32rpx;
      }
    }
  }
  // 横向展示样式
  @include e(row) {
    display: flex;
    width: 100%;
    overflow: auto hidden;
    // & > .seckill__row-item:last-child {
    //     margin-right: 0 !important;
    // }
  }
  @include e(row-item) {
    flex-shrink: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 20rpx;
    position: relative;
    @include m(img) {
      width: 144rpx;
      height: 144rpx;
      margin-bottom: 8rpx;
    }
    @include m(price) {
      width: 110rpx;
      height: 36rpx;
      text-align: center;
      line-height: 36rpx;
      font-size: 28rpx;
      background: url('https://obs.xiaoqa.cn/gruul/20221126/441a63322cca4a02bc1e4d4f5d4b54b9.png') no-repeat 100% 100%;
    }
  }
  @include e(col) {
    overflow: hidden;
    // & > .seckill__col-item:last-child {
    //     margin-bottom: 0 !important;
    // }
  }
  // 详情
  @include e(col-item) {
    @include flex;
    position: relative;
    @include m(img) {
      width: 184rpx;
      height: 184rpx;
      margin-right: 16rpx;
    }
    @include m(right) {
      flex: 1;
      display: flex;
      width: 0;
      flex-direction: column;
      justify-content: space-around;
      height: 184rpx;
    }
    @include m(head) {
      font-size: 28rpx;
      width: 100%;
      @include utils-ellipsis;
    }
    @include m(subhead) {
      font-size: 24rpx;
      color: #525252;
      margin-top: 4rpx;
    }
    @include m(bottom) {
      @include flex(space-between);
    }
    @include m(price) {
      font-size: 32rpx;
      color: #dd3324;
      font-weight: bold;
    }
    @include m(btn) {
      width: 132rpx;
      height: 66rpx;
      text-align: center;
      line-height: 66rpx;
      background: #e94927;
      color: #fff;
      border-radius: 34rpx;
      font-size: 24rpx;
      font-weight: bold;
    }
  }
}

.f20 {
  font-size: 40rpx;
}

.fb {
  font-weight: bold;
}

@include b(seckill-row) {
  @include e(corner) {
    @include m(new) {
      width: 64rpx;
      height: 32rpx;
      position: absolute;
      top: 8rpx;
      left: 0;
    }
    @include m(hot) {
      width: 64rpx;
      height: 70rpx;
      position: absolute;
      left: 0;
      top: 0;
    }
    @include m(purchase) {
      width: 68rpx;
      height: 40rpx;
      position: absolute;
      left: 12rpx;
      top: 12rpx;
    }
  }
}

@include b(seckill-col) {
  @include e(corner) {
    @include m(purchase) {
      width: 68rpx;
      height: 40rpx;
      position: absolute;
      left: 12rpx;
      top: 12rpx;
    }
    @include m(hot) {
      width: 64rpx;
      height: 70rpx;
      position: absolute;
      left: 0;
      top: 0;
    }
    @include m(new) {
      width: 64rpx;
      height: 32rpx;
      position: absolute;
      top: 8rpx;
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
    border-radius: 20rpx;
    padding: 20rpx;
    box-shadow: 0 2rpx 112rpx 12rpx rgb(109 109 109 / 10%);
  }
}

.good-three {
  background: #fff;

  & .seckill__row-item,
  .seckill__col-item {
    border-radius: 20rpx;
    padding: 20rpx;
    border: 2rpx solid #eee;
  }
}
</style>
