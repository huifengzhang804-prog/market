<script setup lang="ts">
import { computed, type PropType } from 'vue'
import GroupCardCount from './group-card-count.vue'
import type { ApiGroupInfo } from '@/apis/plugin/group/model'
import type { StorageSku, comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'

const $props = defineProps({
  groupInfo: {
    type: Object as PropType<ApiGroupInfo>,
    default() {
      return {}
    },
  },
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  groupExist: {
    type: Boolean,
    default: false,
  },
  groupJoin: {
    type: Boolean,
    default: false,
  },
  // true 活动进行时 / false 预热
  activitiesBegan: {
    type: Boolean,
    default: true,
  },
  activityInfo: {
    type: Object,
    default: () => {},
  },
  // 总商品详情
  goodsInfo: {
    type: Object,
    default: () => {},
  },
})
const { divTenThousand } = useConvert()

const info = computed(() => {
  const tempObj = {
    groupPrice: '0',
    price: '0',
    stock: '0',
  }
  const currentSkuArr = $props.groupInfo.skus.filter((item) => {
    return item.skuId === $props.currentChoosedSku.id
  })
  // 当前sku未参加拼团 且还显示拼团card 替换成原商品信息
  if (currentSkuArr.length) {
    tempObj.groupPrice = divTenThousand(currentSkuArr[0].prices[0]).toString()
    tempObj.stock = `限${currentSkuArr[0].stock}件`
  } else {
    tempObj.groupPrice = divTenThousand($props.currentChoosedSku.salePrice).toString()
    if ($props.currentChoosedSku.stockType === 'LIMITED') {
      tempObj.stock = `限${$props.currentChoosedSku.stock}件`
    } else {
      tempObj.stock = '不限购'
    }
  }
  tempObj.price = divTenThousand($props.currentChoosedSku.salePrice).toString()
  return tempObj
})
// 倒计时时间
const getTime = computed(() => {
  const {
    time: { start: startTime, end: endTime },
  } = $props.activityInfo
  return $props.activitiesBegan ? endTime : startTime
})
</script>

<template>
  <view class="group">
    <view class="group__left">
      <view>
        <text v-if="$props.groupJoin" class="group__left-name">拼团价</text>
        <text class="group__left-price">{{ info.groupPrice || '???' }}</text>
      </view>
      <view class="group__left-initial">
        <text class="group__left-initial--price">{{ info.price }}</text>
        <slot name="card" />
      </view>
    </view>
    <view class="group__right">
      <view>拼团活动</view>
      <!-- 倒计时 -->
      <group-card-count
        :get-tip="`距${$props.activitiesBegan ? '结束' : '开始'}`"
        :current-choosed-sku="$props.currentChoosedSku"
        :status="$props.groupInfo.teamStatus"
        :time="getTime"
      />
      <view>{{ info.stock }}</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(group) {
  width: 100%;
  height: 232rpx;
  background: linear-gradient(95.47deg, #ff0844 0%, #ffb199 100%);
  color: #fff;
  padding: 30rpx 35rpx 33rpx;
  font-size: 32rpx;
  @include flex(space-between);

  @include e(left-price) {
    font-size: 45rpx;
    font-weight: 700;
    &::before {
      content: '￥';
      display: inline-block;
      vertical-align: baseline;
      font-size: 32rpx;
    }
  }
  @include e(left-initial) {
    height: 48rpx;
    @include flex(flex-start);
    margin-top: 60rpx;
    @include m(price) {
      color: #fff;
      margin-right: 20rpx;
      text-decoration-line: line-through;
      &::before {
        content: '￥';
        display: inline-block;
        vertical-align: baseline;
        transform: scale(0.7);
      }
    }
  }

  @include e(right) {
    height: 168rpx;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: space-between;
  }
}
</style>
