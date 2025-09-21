<template>
  <view class="goods-item__foot">
    <view v-if="formData?.goodsNameConfig?.rows" class="goods-item__name">
      <text
        v-if="formData?.goodsNameConfig.showTag && goodsInfo?.productLabel?.name"
        class="tag"
        :style="{ color: goodsInfo?.productLabel?.fontColor || '', backgroundColor: goodsInfo?.productLabel?.backgroundColor || '' }"
        >{{ goodsInfo?.productLabel?.name }}</text
      >
      <text class="goods-item__name--main">{{ goodsInfo.productName }}</text>
    </view>
    <view v-if="formData?.extraConfig?.sellPointDesc && goodsInfo?.saleDescribe" class="goods-item__salepoints">{{ goodsInfo?.saleDescribe }}</view>
    <view class="goods-item__price">
      <GoodPrice :price="range(goodsInfo.salePrices[0]).toString()" :member-info="goodsInfo?.memberInfo" />
      <view
        v-if="formData?.extraConfig?.memberTag && contrastMemberLabel(formData?.extraConfig?.memberTag) && goodsInfo.memberInfo?.memberLabel"
        class="goods-item__price--content"
      >
        <MemberLabel
          :bg-color="goodsInfo.memberInfo?.memberLabel?.priceLabelColor"
          :font-color="goodsInfo.memberInfo?.memberLabel?.priceFontColor"
          style="margin-left: 6rpx"
          >{{ goodsInfo.memberInfo?.memberLabel?.priceLabelName }}</MemberLabel
        >
      </view>
    </view>
    <scroll-view
      v-if="formData?.extraConfig?.coupon && goodsInfo?.couponList && goodsInfo?.couponList?.length > 0"
      scroll-x
      enhanced
      :show-scrollbar="false"
      class="goods-item__coupon"
    >
      <view class="goods-item__coupon_container">
        <view v-for="(coupon, index) in goodsInfo?.couponList" :key="index" class="goods-item__coupon--list" :class="{ ml0: index === 0 }">{{
          coupon.sourceDesc
        }}</view>
      </view>
    </scroll-view>
    <view v-if="formData?.extraConfig?.historyData && goodsInfo?.shopOperationHistory" class="goods-item__history">{{
      goodsInfo?.shopOperationHistory
    }}</view>
    <view v-if="formData?.extraConfig?.shopName" class="goods-item__shop" @click.stop="jumpToShop(goodsInfo?.shopId)">
      <template v-if="formData?.extraConfig?.shopTag">
        <text v-if="goodsInfo?.shopType === 'SELF_OWNED'" class="label label-red">自营</text>
        <text v-else-if="goodsInfo.shopType === 'PREFERRED'" class="label label-yellow">优选</text>
      </template>
      <text class="goods-item__shop--name">{{ goodsInfo.shopName }}</text>
      <q-icon name="icon-youjiantou_huaban1" color="#999" />
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import type { GoodFormData } from '../../types'
import type { GoodItemType } from '../good'
import GoodPrice from './good-price.vue'
import MemberLabel from './member-label.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { computed } from 'vue'

const { range } = usePriceRange()
const $props = defineProps({
  formData: {
    type: Object as PropType<GoodFormData>,
    default: () => ({}),
  },
  goodsInfo: {
    type: Object as PropType<GoodItemType>,
    default: () => {},
  },
})

const jumpToShop = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
enum MemberTagEnum {
  PAID = 'PAID_MEMBER',
  FREE = 'FREE_MEMBER',
}
const contrastMemberLabel = computed(() => (memberType: string) => {
  const memberTag: any = $props.formData.extraConfig?.memberTag || ''
  if (memberTag === 'ALL') return true
  // @ts-ignore
  if (MemberTagEnum[memberTag] === memberType) return true
  return false
})
</script>

<script lang="ts">
export default {
  data() {
    return {
      showCancel: false,
    }
  },
  mounted() {
    this.doGetInfo()
  },
  methods: {
    doGetInfo() {
      uni
        .createSelectorQuery()
        .in(this)
        .select('.goods-item__name--main')
        .boundingClientRect((result) => {
          // @ts-ignore
          const height = result?.height || 0
          const flagHeight = uni.upx2px(44)
          this.showCancel = height <= flagHeight
        })
        .exec()
    },
  },
}
</script>

<style lang="scss" scoped>
@import './good-item-foot.scss';
</style>
