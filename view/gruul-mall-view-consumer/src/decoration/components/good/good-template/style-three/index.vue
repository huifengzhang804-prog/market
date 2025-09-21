<template>
  <view class="style-three">
    <view v-for="(item, idx) in goodsList" :key="idx" class="goods-item" @click="handleNavToDetail(item.productId, item.shopId)">
      <view class="goods-item__image">
        <image :src="cropImg(item.pic, 200, 200)" alt :lazy-load="true" mode="aspectFill" style="height: 100%"></image>
      </view>
      <view class="goods-item__foot">
        <view v-if="formData?.goodsNameConfig?.rows" class="goods-item__name">
          <text
            v-if="formData?.goodsNameConfig.showTag && item?.productLabel?.name"
            class="tag"
            :style="{ color: item?.productLabel?.fontColor || '', backgroundColor: item?.productLabel?.backgroundColor || '' }"
            >{{ item?.productLabel?.name }}</text
          >
          <text>{{ item.productName }}</text>
        </view>
        <view class="goods-item__foot--content">
          <view v-if="formData?.extraConfig?.coupon" class="goods-item__coupon">
            <text v-for="(coupon, index) in item?.couponList" :key="index" class="goods-item__coupon--list" :class="{ ml0: index === 0 }">{{
              coupon.sourceDesc
            }}</text>
          </view>
          <view v-else-if="formData?.extraConfig?.sellPointDesc && item?.saleDescribe" class="goods-item__salepoints">{{ item?.saleDescribe }}</view>
          <view v-else-if="formData?.extraConfig?.historyData && item?.shopOperationHistory" class="goods-item__history">{{
            item?.shopOperationHistory
          }}</view>
          <view v-else-if="formData?.extraConfig?.shopName" class="goods-item__shop" @click.stop="jumpToShop(item?.shopId)">
            <template v-if="formData?.extraConfig?.shopTag">
              <text v-if="item?.shopType === 'SELF_OWNED'" class="label label-red">自营</text>
              <text v-else-if="item.shopType === 'PREFERRED'" class="label label-yellow">优选</text>
            </template>
            <text class="goods-item__shop--name">{{ item.shopName }}</text>
            <q-icon name="icon-youjiantou_huaban1" color="#999" />
          </view>
        </view>
        <view class="goods-item__price">
          <GoodPrice :price="range(item.salePrices[0]).toString()" :member-info="item.memberInfo" />
          <view
            v-if="formData?.extraConfig?.memberTag && contrastMemberLabel(formData?.extraConfig?.memberTag) && item.memberInfo?.memberLabel"
            class="goods-item__price--content"
          >
            <member-label
              :bg-color="item.memberInfo?.memberLabel?.priceLabelColor"
              :font-color="item.memberInfo?.memberLabel?.priceFontColor"
              style="margin-left: 6rpx"
              >{{ item.memberInfo?.memberLabel?.priceLabelName }}</member-label
            >
          </view>
          <text
            v-if="formData?.extraConfig?.freeMail && item?.freightTemplateId && Number(item?.freightTemplateId) === 0"
            class="goods-item__price--free"
            >包邮</text
          >
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { cropImg } from '@/utils'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { GoodItemType } from '../../good'
import type { GoodFormData } from '../../../types'
import type { PropType } from 'vue'
import GoodPrice from '../good-price.vue'
import memberLabel from '../member-label.vue'

const $props = defineProps({
  goodsList: {
    type: Array<GoodItemType>,
    default: () => [],
  },
  formData: {
    type: Object as PropType<GoodFormData>,
    default: () => ({}),
  },
})

const { range } = usePriceRange()
const handleNavToDetail = (id: Long, shopId: Long, instruction?: number) => {
  let url = `/pluginPackage/goods/commodityInfo/InfoEntrance?goodId=${id}&shopId=${shopId}`
  if (instruction) {
    url += `&instruction=${instruction}`
    uni.navigateTo({
      url,
    })
  } else {
    jumpGoods(shopId, id)
  }
}

const jumpToShop = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
enum MemberTagEnum {
  PAID = 'PAID_MEMBER',
  FREE = 'FREE_MEMBER',
}

const contrastMemberLabel = (memberType: string) => {
  const memberTag: any = $props.formData.extraConfig?.memberTag || ''
  if (memberTag === 'ALL') return true
  // @ts-ignore
  if (MemberTagEnum[memberTag] === memberType) return true
  return false
}
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
          const flagHeight = uni.upx2px(40)
          this.showCancel = height <= flagHeight
        })
        .exec()
    },
  },
}
</script>

<style lang="scss" scoped>
@import './index.scss';
</style>
