<script setup lang="ts">
import { ref, watch, type PropType } from 'vue'
import ClassItemInfo from './class-item-info.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { RetrieveCommodity } from '@/apis/good/model'
import QIcon from '@/components/q-icon/q-icon.vue'
import { throttle } from 'lodash'

const { divTenThousand } = useConvert()

const $props = defineProps({
  isLarge: {
    type: Number,
    default: 1,
  },
  info: {
    type: Object as PropType<RetrieveCommodity>,
    default() {
      return {}
    },
  },
  goodsShow: {
    type: Array as PropType<number[]>,
    default() {
      return []
    },
  },
})

const shopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}

const skuId = ref('')

watch(
  () => $props.info,
  (newVal) => {
    console.log(newVal)
  },
  {
    immediate: true,
  },
)

const handleNavTodetail = () => {
  const { shopId, productId } = $props.info
  jumpGoods(shopId, productId)
}

const handleChangeSpecImg = (index: number, specImg: { image: string; skuId: string }, item: RetrieveCommodity) => {
  item.activeImg = index
  item.pic = specImg.image
  skuId.value = specImg.skuId
}

const handleAddCart = throttle(async (val: RetrieveCommodity) => {
  jumpGoods(val.shopId, val.productId, true, skuId.value)
}, 500)
</script>

<template>
  <view class="classificate__shop-list-item" @click="handleNavTodetail">
    <img :src="info.pic" style="border-radius: 10rpx; width: 160rpx; height: 160rpx" />
    <view
      style="
        width: calc(100% - 160rpx);
        flex: 1;
        min-height: 160rpx;
        padding-left: 15rpx;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
      "
    >
      <view class="classificate__shop-list-item-name">
        <template
          v-if="info.skuVOS && info.skuVOS.length > 1 && Array.isArray(goodsShow) && goodsShow.includes(2) && info.skuVOS.some((item) => item.image)"
        >
          <text class="classificate__shop-list-item-name-text">
            <text
              v-if="info.shopType === 'SELF_OWNED' || info.shopType === 'PREFERRED'"
              class="classificate__shop-list-item-name-tag"
              :style="{ backgroundColor: info.shopType === 'SELF_OWNED' ? '#FB232F' : '#7728f5' }"
              >{{ shopType[info.shopType as keyof typeof shopType] }}
            </text>
            {{
              info.productName.length > 12 && info.shopType !== 'ORDINARY'
                ? info.productName.slice(0, 10)
                : info.productName.length > 12 && info.shopType === 'ORDINARY'
                  ? info.productName.slice(0, 12)
                  : info.productName
            }}
          </text>
        </template>
        <template v-else>
          <text>
            <text
              v-if="info.shopType === 'SELF_OWNED' || info.shopType === 'PREFERRED'"
              class="classificate__shop-list-item-name-tag"
              :style="{ backgroundColor: info.shopType === 'SELF_OWNED' ? '#FB232F' : '#7728f5' }"
              >{{ shopType[info.shopType as keyof typeof shopType] }}
            </text>
            {{ info.productName.length > 24 ? info.productName.slice(0, 24) : info.productName }}
          </text>
        </template>
      </view>
      <scroll-view scroll-x enhanced :show-scrollbar="false" style="width: 320rpx">
        <view
          v-if="info.skuVOS && info.skuVOS.length > 1 && Array.isArray(goodsShow) && goodsShow.includes(2) && info.skuVOS.some((item) => item.image)"
          style="display: flex; align-items: center; gap: 6rpx"
          class="classificate__shop-list-item-image"
        >
          <view
            v-for="(specImg, index) in info.skuVOS"
            :key="index"
            class="classificate__shop-list-item-image-item"
            :style="{ border: info.activeImg === index ? '2px solid #fb232f' : '2px solid transparent' }"
            @click.stop="handleChangeSpecImg(index, specImg, info)"
          >
            <u-image v-if="specImg.image" :src="specImg.image" :showLoading="false" :loadingIcon="''" mode="widthFix"></u-image>
          </view>
        </view>
      </scroll-view>
      <view v-if="goodsShow && goodsShow.length > 0 && goodsShow.includes(3)" class="classificate__shop-list-item-sold"
        >已售{{ info.salesVolume > 9999 ? divTenThousand(info.salesVolume) : info.salesVolume }}<text v-if="info.salesVolume > 10000">万+</text>
      </view>
      <view v-if="goodsShow && goodsShow.length > 0 && goodsShow.includes(4)" class="classificate__shop-list-item-discount">
        <view v-for="item in info.couponList && info.couponList?.length > 1 ? info.couponList.slice(0, 1) : info.couponList" :key="item.id">
          <text>{{ item.sourceDesc }}</text>
        </view>
        <view v-if="info.freightTemplateId === 0" class="classificate__list__item--info--discount-free">包邮</view>
      </view>
      <view class="classificate__shop-list-item-price">
        <view class="classificate__shop-list-item-price-left">
          {{ String(divTenThousand(info.salePrices[0])).split('.')[0]
          }}<text v-if="String(divTenThousand(info.salePrices[0])).split('.')[1]" style="font-size: 22rpx"
            >.{{ String(divTenThousand(info.salePrices[0])).split('.')[1] }}
          </text>
        </view>
        <q-icon
          v-if="goodsShow && goodsShow.length > 0 && goodsShow.includes(5)"
          name="icon-gouwuche5"
          size="36rpx"
          color="#FB232F"
          @click.stop="handleAddCart(info)"
        ></q-icon>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.classificate__shop-list {
  &-item {
    margin-bottom: 20rpx;
    width: 100%;
    height: 180rpx;
    display: flex;
    align-items: center;
    &-name {
      font-size: 24rpx;
      color: #000;
      font-weight: 500;
      font-family: LXGW Neo XiHei;
      font-weight: 400;
      overflow: hidden;
      white-space: nowrap;
      display: flex;
      align-items: center;
      flex-wrap: nowrap;

      &-tag {
        background-color: #7728f5;
        color: #fff;
        font-size: 20rpx;
        padding: 0 5rpx;
        line-height: 30rpx;
        border-radius: 5rpx;
        font-size: 20rpx;
        margin-right: 5rpx;
        flex-shrink: 0;
      }

      &-text {
        flex: 1;
        overflow: hidden;
        white-space: nowrap;
      }
    }

    &-image {
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
      gap: 6rpx;
      &::-webkit-scrollbar {
        display: none;
      }

      &-item {
        width: 60rpx;
        height: 60rpx;
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
    }

    &-sold {
      font-size: 22rpx;
      color: #a6a6a6;
      font-family: Arial;
      font-weight: 400;
    }

    &-discount {
      @include flex(flex-start);
      gap: 6px;
      font-size: 10px;
      color: #fa3534;
      & > view {
        padding: 0px 3px;
        border-radius: 2px;
        border: 1px solid #fa3534;
      }
      & > view:last-child {
        color: #e3600a;
        border: 1px solid #e3600a;
      }
    }
    &-price {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #fb232f;

      &-left {
        font-size: 30rpx;
        font-weight: 500;
        font-family: Kingsoft_Cloud_Font;
        &::before {
          content: '￥';
          font-size: 22rpx;
          color: #fb232f;
          font-weight: 400;
          font-family: Kingsoft_Cloud_Font;
        }
      }
    }
  }
}
</style>
