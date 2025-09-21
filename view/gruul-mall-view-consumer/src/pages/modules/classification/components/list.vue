<template>
  <view class="classificate__list">
    <skeleton-index v-if="list.length && loading" type="goods-list" :count="skeletonCount" :columns="2" />

    <view v-show="!loading">
      <view v-for="item in list" :key="item.id" class="classificate__list__item" @click="handleNavTodetail(item)">
        <image :src="item.pic" class="classificate__list__item--pic" mode=""></image>
        <view class="classificate__list__item--info" :style="{ height: item.skuVOS && item.skuVOS.length > 1 ? '120px' : '115px' }">
          <view class="classificate__list__item--info--name">
            <text
              v-if="item.skuVOS && item.skuVOS.length > 1 && item.skuVOS.every((v) => v.image)"
              style="overflow: hidden; text-overflow: clip; white-space: nowrap; flex: 1; min-width: 0"
            >
              <template v-if="config?.goodsShow">
                <text
                  v-if="item.shopType !== 'ORDINARY' && config?.goodsShow?.includes(1)"
                  class="classificate__list__item--info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              <template v-else>
                <text
                  v-if="item.shopType !== 'ORDINARY'"
                  class="classificate__list__item--info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              {{ item.productName.length > 16 ? item.productName.slice(0, 16) : item.productName }}
            </text>
            <!-- 两个展示 -->
            <text v-else>
              <template v-if="config?.goodsShow">
                <text
                  v-if="item.shopType !== 'ORDINARY' && config?.goodsShow?.includes(1)"
                  class="classificate__list__item--info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              <template v-else>
                <text
                  v-if="item.shopType !== 'ORDINARY'"
                  class="classificate__list__item--info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              {{ item.productName.length > 35 ? item.productName.slice(0, 35) : item.productName }}
            </text>
          </view>
          <template v-if="config.goodsShow">
            <scroll-view scroll-x enhanced :show-scrollbar="false" style="width: 340rpx">
              <view
                v-if="
                  item.skuVOS &&
                  item.skuVOS.length > 1 &&
                  Array.isArray(config?.goodsShow) &&
                  config.goodsShow.includes(2) &&
                  item.skuVOS.every((v) => v.image)
                "
                class="classificate__list__item--info--specImg"
              >
                <view
                  v-for="(specImg, index) in item.skuVOS"
                  :key="index"
                  class="classificate__list__item--info--specImg-item"
                  :style="{ border: item.activeImg === index ? '2px solid #fb232f' : '2px solid transparent' }"
                  @click.stop="handleChangeSpecImg(index, specImg, item)"
                >
                  <u-image v-if="specImg.image" :src="specImg.image" :showLoading="false" mode="widthFix"></u-image>
                </view>
              </view>
            </scroll-view>
          </template>
          <template v-else>
            <scroll-view
              v-if="item.skuVOS && item.skuVOS.length > 1 && item.skuVOS.every((v) => v.image)"
              scroll-x
              enhanced
              :show-scrollbar="false"
              style="width: 340rpx"
            >
              <view class="classificate__list__item--info--specImg" style="display: flex; align-items: center; gap: 6rpx">
                <view
                  v-for="(specImg, index) in item.skuVOS"
                  :key="index"
                  class="classificate__list__item--info--specImg-item"
                  :style="{ border: item.activeImg === index ? '2px solid #fb232f' : '2px solid transparent' }"
                  @click.stop="handleChangeSpecImg(index, specImg, item)"
                >
                  <u-image v-if="specImg.image" :src="specImg.image" :showLoading="false" mode="widthFix"></u-image>
                </view>
              </view>
            </scroll-view>
          </template>
          <template v-if="config.goodsShow">
            <view
              v-if="item.salesVolume && Array.isArray(config?.goodsShow) && config.goodsShow.includes(3)"
              class="classificate__list__item--info--sale"
            >
              已售 <text>{{ item.salesVolume > 10000 ? divTenThousand(item.salesVolume) : item.salesVolume }}</text
              ><text v-if="item.salesVolume > 10000">万+</text>
            </view>
          </template>
          <template v-else>
            <view v-if="item.salesVolume" class="classificate__list__item--info--sale">
              已售 <text>{{ item.salesVolume > 10000 ? divTenThousand(item.salesVolume) : item.salesVolume }}</text
              ><text v-if="item.salesVolume > 10000">万+</text>
            </view>
          </template>
          <template v-if="config.goodsShow">
            <view v-if="Array.isArray(config?.goodsShow) && config.goodsShow.includes(4)" class="classificate__list__item--info--discount">
              <view v-for="coupon in item.couponList && item.couponList?.length > 1 ? item.couponList.slice(0, 1) : item.couponList" :key="coupon.id">
                {{ coupon.sourceDesc }}
              </view>
              <view v-if="item.freightTemplateId === 0" class="classificate__list__item--info--discount-free">包邮</view>
            </view>
          </template>
          <template v-else>
            <view class="classificate__list__item--info--discount">
              <view v-for="coupon in item.couponList && item.couponList?.length > 1 ? item.couponList.slice(0, 1) : item.couponList" :key="coupon.id">
                {{ coupon.sourceDesc }}
              </view>
              <view v-if="item.freightTemplateId === 0" class="classificate__list__item--info--discount-free">包邮</view>
            </view>
          </template>
          <view class="classificate__list__item--info--price">
            <view class="classificate__list__item--info--price--num">
              <text style="font-size: 16px; font-weight: 500; color: #fb232f">
                {{ String(divTenThousand(item.salePrices[0])).split('.')[0] }}
              </text>
              <text v-if="String(divTenThousand(item.salePrices[0])).split('.')[1]" style="font-size: 12px">
                .{{ String(divTenThousand(item.salePrices[0])).split('.')[1] }}
              </text>
            </view>
            <template v-if="config.goodsShow">
              <q-icon
                v-if="Array.isArray(config?.goodsShow) && config.goodsShow.includes(5)"
                name="icon-gouwuche8"
                class="classificate__list__item--info--price--shopCar"
                size="40rpx"
                @click.stop="handleAddCart(item)"
              />
            </template>
            <template v-else>
              <q-icon name="icon-gouwuche8" class="classificate__list__item--info--price--shopCar" size="40rpx" @click.stop="handleAddCart(item)" />
            </template>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { RetrieveCommodity } from '@/apis/good/model'
import type { DeCategoryType } from '@/pages/platform/types'
import { computed, ref, type PropType } from 'vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import QIcon from '@/components/q-icon/q-icon.vue'
import SkeletonIndex from '@/components/skeleton/index.vue'

const { divTenThousand } = useConvert()

const $props = defineProps({
  list: {
    type: Array as PropType<RetrieveCommodity[]>,
    default: () => [],
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default: () => ({}),
  },
  loading: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['changeSpecImg', 'addCart'])
const skuId = ref('')

const skeletonCount = computed(() => {
  return Math.max(6, $props.list.length || 5)
})

const handleNavTodetail = (info: RetrieveCommodity) => {
  jumpGoods(info.shopId, info.productId)
}

const handleChangeSpecImg = (index: number, specImg: any, item: any) => {
  skuId.value = specImg.skuId
  $emit('changeSpecImg', index, specImg, item)
}

const handleAddCart = (item: RetrieveCommodity, event?: Event) => {
  if (event) {
    event.stopPropagation()
  }
  $emit('addCart', item, skuId.value)
}
</script>

<style lang="scss" scoped>
@include b(classificate__list) {
  background-color: #fff;
  display: flex;
  flex-direction: column;
  @include e(item) {
    padding: 10px;
    height: 100%;
    @include flex(flex-start);
    @include m(pic) {
      width: 115px;
      height: 115px;
      border-radius: 5px;
    }

    @include m(info) {
      padding-left: 10px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      flex: 1;

      @include m(name) {
        width: 490rpx;
        font-size: 15px;
        font-family: LXGW Neo XiHei;
        color: #232323;
        overflow: hidden;
        display: flex;
        align-items: center;
      }

      @include m(tag) {
        display: inline-block;
        color: #fff;
        width: 47rpx;
        text-align: center;
        height: 28rpx;
        color: #fff;
        border-radius: 4rpx;
        font-size: 18rpx;
        line-height: 28rpx;
        flex-shrink: 0;
        // #ifdef MP-WEIXIN
        position: relative;
        top: -3rpx;
        // #endif
      }

      @include m(sale) {
        font-size: 13px;
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
        gap: 6rpx;

        &::-webkit-scrollbar {
          display: none;
        }
      }
      @include m(specImg-item) {
        width: 33px;
        height: 33px;
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
        & > view {
          padding: 0px 3px;
          border-radius: 2px;
          border: 1px solid #fa3534;
        }
        .classificate__list__item--info--discount-free {
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
</style>
