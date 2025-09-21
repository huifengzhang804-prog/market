<template>
  <view>
    <view class="goods-card">
      <u-cell-group :border="false">
        <u-cell-item
          :border-bottom="false"
          :value-style="{ color: 'red' }"
          :value="!isMember ? '去开通' : ''"
          :arrow="!isMember"
          @click="jumpToMember(isMember)"
        >
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">会员</text>
              <view class="goods-card--value">
                <text v-if="memberInfo?.memberBenefit?.GOODS_DISCOUNT[0]" class="goods-card__tag">
                  {{ Number(memberInfo?.memberBenefit?.GOODS_DISCOUNT[0]?.extendValue) / 100 }}折 优惠
                  {{ (+comProvide.memberPriceC.value).toFixed(2) }}
                </text>
                <text v-else> 开通会员获得优惠多多 </text>
              </view>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="discountList.length" :border-bottom="false" @click="isPreferentialPopup = true">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">优惠</text>
              <text class="goods-card--value">
                <text v-for="(item, index) in discountList" :key="index" class="goods-card__tag">
                  {{ item }}
                  <text v-if="index !== discountList.length - 1">,</text>
                </text>
              </text>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="bonusPrice !== '0.00' || rebateAmount !== '0.00'" :arrow="false" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">赚钱</text>
              <view class="goods-card--value">
                <ProfitCard v-if="bonusPrice !== '0.00'" :price="bonusPrice" :un-active="true" style="display: inline-flex; margin-right: 8rpx" />
                <ProfitCard v-if="rebateAmount !== '0.00'" :price="rebateAmount" style="display: inline-flex" text="预计返" un-active />
              </view>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="params" :border-bottom="false" @click="paraPop = true">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">参数</text>
              <view class="goods-card--value">
                {{ params }}
              </view>
            </view>
          </template>
        </u-cell-item>
      </u-cell-group>
    </view>
    <view class="goods-card">
      <u-cell-group :border="false">
        <u-cell-item
          :title="`评价  (${Number(evaluateInfo.contentCount) > 100 ? '100+' : evaluateInfo.contentCount})`"
          :value="`好评率${evaluateInfo.praiseRatio ? evaluateInfo.praiseRatio : 0}%`"
          :border-bottom="false"
          use-label-slot
          :value-style="{ color: '#333333', fontSize: '24rpx' }"
          :title-style="{ 'font-weight': 700, color: '#000' }"
          @click="handleGoOrderAssess"
        />
      </u-cell-group>
    </view>
    <u-popup v-model="paraPop" height="500px" mode="bottom" border-radius="20" safe-area-inset-bottom>
      <view class="parameter__header">商品参数</view>
      <view style="height: 30rpx"></view>
      <view v-for="item in props.info.extra?.productParameters" :key="item.id" class="parameter__item">
        <view class="parameter__item--left" style="font-weight: 700; color: #000">{{ item.featureName }}</view>
        <view class="parameter__item--right">{{ item.featureValues.map((item) => item.firstValue).join(' ') }}</view>
      </view>
    </u-popup>
    <preferential-popup v-model:show="isPreferentialPopup" :shop-id="o2oShopId" :sku="currentChoosedSku" />
  </view>
</template>

<script setup lang="ts">
import { computed, inject, type Ref, type PropType, ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import preferentialPopup from '@/pluginPackage/o2o-goods/components/o2o-goods-preferential-popup/o2o-goods-preferential-popup.vue'
import type { CartApiCouponVO } from '@/apis/plugin/coupon/model'
import type { ProductResponse, StorageSku, ApiEvaluation, EvaluationType } from '@/pluginPackage/goods/commodityInfo/types'
import ProfitCard from '@/pluginPackage/goods/commodityInfo/components/profit-card.vue'
import { doGetOrderEvaluateInfo } from '@/apis/good'
import { watch } from 'vue'

const memberInfo = useUserStore().getterMemberInfo

// 是否是付费会员
const isMember = computed(() => memberInfo?.memberType === 'PAID_MEMBER')

const { divTenThousand } = useConvert()

const props = defineProps({
  info: {
    type: Object as PropType<ProductResponse>,
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
  o2oShopId: {
    type: String,
    default() {
      return ''
    },
  },
})
// 参数弹框控制
const paraPop = ref(false)
const bonusPrice = ref('0.00') // 分销预计赚
// 参数数据
const params = computed(() => {
  return props.info.extra?.productParameters?.map((item) => item.featureName).join(' ')
})

const rebateAmount = computed(() => {
  return divTenThousand(props.info?.earningMap?.REBATE || '0').toFixed(2)
})

const comProvide = inject('comProvide') as {
  isPaidMember: () => boolean
  forecastPrice: Ref<string | number | DecimalType>
  memberPriceC: Ref<string | DecimalType>
  couponList: Ref<CartApiCouponVO[]>
}
/**
 * 跳转开通会员
 */
const jumpToMember = (e: boolean) => {
  if (e) return
  uni.navigateTo({
    url: '/pluginPackage/member/views/MemberCenter',
  })
}

const discountList = computed(() => {
  const list = []
  const length = comProvide.couponList.value.length
  if (length) {
    for (let i = 0; i < length; i++) {
      if (comProvide.couponList.value[i]) {
        const discountAmount = comProvide.couponList.value[i].discountAmount
        list.push(`优惠券 优惠${divTenThousand(discountAmount).toFixed(2)}`)
      }
    }
  }
  return list
})
// 优惠券弹框控制
const isPreferentialPopup = ref(false)

const evaluateInfo = ref<ApiEvaluation>({
  contentCount: '',
  evaluate: {
    comment: '',
    createTime: '',
    deleted: false,
    id: '',
    image: '',
    isExcellent: false,
    itemId: '',
    medias: [],
    name: '',
    orderNo: '',
    packageId: '',
    productId: '',
    rate: 5,
    shopId: '',
    skuId: '',
    specs: [],
    updateTime: '',
    userId: '',
    shopReply: '',
    avatar: '',
    nickname: '',
  },
  mediaCount: '',
  praiseCount: '',
  praiseRatio: '',
  totalCount: '',
})

watch(
  () => props.info,
  (val) => {
    if (props.info.productId) initEvaluateInfo()
    if (val.earningMap?.DISTRIBUTE) {
      bonusPrice.value = divTenThousand(val?.earningMap?.DISTRIBUTE || 0).toFixed(2)
    }
  },
)

async function initEvaluateInfo(type?: keyof typeof EvaluationType) {
  const { code, msg, data } = await doGetOrderEvaluateInfo(props.o2oShopId, props.info.productId, type)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取评价概述失败'}`, icon: 'none' })
  evaluateInfo.value = data
}

/**
 * 导航去查看评价
 */
const handleGoOrderAssess = () => {
  const { totalCount, contentCount, mediaCount } = evaluateInfo.value
  const params = `&totalCount=${totalCount}&contentCount=${contentCount}&mediaCount=${mediaCount}`
  uni.navigateTo({
    url: `/basePackage/pages/orderAssess/OrderEvaluate?shopId=${props.o2oShopId}&productId=${props.info.productId}${params}`,
  })
}
</script>

<style scoped lang="scss">
@include b(goods-card) {
  margin: 20rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  @include e(content) {
    font-size: 24rpx;
    display: flex;
  }
  @include e(tag) {
    color: #fa3534;
  }
  @include m(title) {
    min-width: 100rpx;
    color: #999999;
  }
  @include m(text) {
    display: inline-block;
    margin-right: 20rpx;
  }
  @include m(value) {
    color: #333;
  }
}
@include b(u-cell) {
  padding: 0rpx 20rpx;
  margin-top: 5rpx;
  margin-bottom: 5rpx;
}
@include b(cell) {
  @include e(title) {
    font-size: 28rpx;
    font-weight: bold;
    color: #000;
  }
}
@include b(parameter) {
  @include e(header) {
    text-align: center;
    line-height: 100rpx;
    font-size: 30rpx;
    color: #000;
    font-weight: bold;
  }
  @include e(item) {
    padding: 20rpx;
    font-size: 28rpx;
    border-bottom: 1px solid rgb(226, 224, 224);
    @include flex(flex-start);
    @include m(left) {
      color: #ccc;
      margin-right: 40rpx;
    }
  }
}
:deep(.u-cell-item-box) {
  padding: 8rpx 0;
}
</style>
