<template>
  <view class="classificate__list">
    <!-- 预加载骨架屏 -->
    <skeleton-index v-if="waterfallColumns.length && loading" type="waterfall" :count="skeletonCount" :columns="2" />
    <!-- 实际内容 -->
    <view v-show="!loading" class="waterfall-container">
      <view v-for="(column, columnIndex) in waterfallColumns" :key="columnIndex" class="waterfall-column">
        <view v-for="item in column" :key="item.id" class="classificate__list-item" @click="handleNavTodetail(item)">
          <image :src="item.pic" class="classificate__list-item--pic" mode="widthFix"></image>
          <view class="classificate__list-item-info">
            <view class="classificate__list-item-info--name">
              <template v-if="config?.goodsShow">
                <text
                  v-if="item.shopType !== 'ORDINARY' && config?.goodsShow?.includes(1)"
                  class="classificate__list-item-info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              <template v-else>
                <text
                  v-if="item.shopType !== 'ORDINARY'"
                  class="classificate__list-item-info--tag"
                  :style="{ backgroundColor: item.shopType === 'PREFERRED' ? '#8239F6' : '#FB232F' }"
                >
                  {{ item.shopType === 'PREFERRED' ? '优选' : '自营' }}
                </text>
              </template>
              <text
                v-if="item.skuVOS && item.skuVOS.length > 1 && config?.goodsShow?.includes(2) && item.skuVOS.every((v) => v.image)"
                style="flex: 1; overflow: hidden; white-space: nowrap"
              >
                {{ item.productName.length > 10 ? item.productName.slice(0, 10) : item.productName }}
              </text>
              <!-- 两行展示不展示三个点 -->
              <text v-else>{{ item.productName.length > 22 ? item.productName.slice(0, 22) : item.productName }}</text>
            </view>

            <template v-if="config?.goodsShow">
              <view
                v-if="item.skuVOS && item.skuVOS.length > 1 && config?.goodsShow?.includes(2) && item.skuVOS.every((v) => v.image)"
                class="horizontal-scroll"
              >
                <scroll-view scroll-x enhanced :show-scrollbar="false">
                  <view class="classificate__list-item-info--specImg">
                    <view
                      v-for="(specImg, index) in item.skuVOS"
                      :key="index"
                      class="classificate__list-item-info--specImg-item"
                      :style="{ border: item.activeImg === index ? '2px solid #fb232f' : '2px solid transparent' }"
                      @click.stop="handleChangeSpecImg(index, specImg, item)"
                    >
                      <u-image v-if="specImg.image" :src="specImg.image" :showLoading="false" mode="widthFix"></u-image>
                    </view>
                  </view>
                </scroll-view>
              </view>
            </template>
            <template v-else>
              <view v-if="item.skuVOS && item.skuVOS.length > 1 && item.skuVOS.every((v) => v.image)" class="horizontal-scroll">
                <scroll-view scroll-x enhanced :show-scrollbar="false">
                  <view class="classificate__list-item-info--specImg">
                    <view
                      v-for="(specImg, index) in item.skuVOS"
                      :key="index"
                      class="classificate__list-item-info--specImg-item"
                      :style="{ border: item.activeImg === index ? '2px solid #fb232f' : '2px solid transparent' }"
                      @click.stop="handleChangeSpecImg(index, specImg, item)"
                    >
                      <u-image v-if="specImg.image" :src="specImg.image" :showLoading="false" mode="widthFix"></u-image>
                    </view>
                  </view>
                </scroll-view>
              </view>
            </template>
            <template v-if="config?.goodsShow">
              <view v-if="item.salesVolume && config?.goodsShow?.includes(3)" class="classificate__list-item-info--sale">
                已售 <text>{{ item.salesVolume > 10000 ? divTenThousand(item.salesVolume) : item.salesVolume }}</text
                ><text v-if="item.salesVolume > 10000">万+</text>
              </view>
            </template>
            <template v-else>
              <view v-if="item.salesVolume" class="classificate__list-item-info--sale">
                已售 <text>{{ item.salesVolume > 10000 ? divTenThousand(item.salesVolume) : item.salesVolume }}</text
                ><text v-if="item.salesVolume > 10000">万+</text>
              </view>
            </template>
            <template v-if="config?.goodsShow">
              <view v-if="config?.goodsShow?.includes(4)" class="classificate__list-item-info--discount">
                <view
                  v-for="coupon in item.couponList && item.couponList?.length > 1 ? item.couponList.slice(0, 1) : item.couponList"
                  :key="coupon.id"
                >
                  {{ coupon.sourceDesc }}
                </view>
                <view v-if="item.freightTemplateId === 0" class="classificate__list-item-info--discount-free">包邮</view>
              </view>
            </template>
            <template v-else>
              <view class="classificate__list-item-info--discount">
                <view
                  v-for="coupon in item.couponList && item.couponList?.length > 1 ? item.couponList.slice(0, 1) : item.couponList"
                  :key="coupon.id"
                >
                  {{ coupon.sourceDesc }}
                </view>
                <view v-if="item.freightTemplateId === 0" class="classificate__list-item-info--discount-free">包邮</view>
              </view>
            </template>
            <view class="classificate__list-item-info-price">
              <view class="classificate__list-item-info-price--num">
                <text style="font-size: 18px; font-weight: 500; color: #fb232f">
                  {{ String(divTenThousand(item.salePrices[0])).split('.')[0] }}
                </text>
                <text v-if="String(divTenThousand(item.salePrices[0])).split('.')[1]" style="font-size: 12px">
                  .{{ String(divTenThousand(item.salePrices[0])).split('.')[1] }}
                </text>
              </view>
              <template v-if="config?.goodsShow">
                <q-icon
                  v-if="config?.goodsShow?.includes(5)"
                  name="icon-gouwuche8"
                  class="classificate__list-item-info-price--shopCar"
                  size="40rpx"
                  @click.stop="handleAddCart(item)"
                />
              </template>
              <template v-else>
                <q-icon name="icon-gouwuche8" class="classificate__list-item-info-price--shopCar" size="40rpx" @click.stop="handleAddCart(item)" />
              </template>
            </view>
          </view>
        </view>
      </view>
    </view>
    <!-- </scroll-view> -->
  </view>
</template>

<script setup lang="ts">
import type { RetrieveCommodity } from '@/apis/good/model'
import type { DeCategoryType } from '@/pages/platform/types'
import { ref, type PropType, computed, watch } from 'vue'
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
    default: () => {},
  },
  loading: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['changeSpecImg', 'addCart'])
const skuId = ref('')

// 瀑布流列数
const COLUMN_COUNT = 2

// 骨架屏数量
const skeletonCount = computed(() => {
  return Math.max(6, $props.list.length || 6)
})

// 计算瀑布流布局
const waterfallColumns = computed(() => {
  const columns: RetrieveCommodity[][] = Array.from({ length: COLUMN_COUNT }, () => [])
  $props.list.forEach((item, idx) => {
    const columnIndex = idx % COLUMN_COUNT
    columns[columnIndex].push(item)
  })
  return columns
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
.classificate__list {
  // height: 100vh;
  padding: 10px 12px;
  padding-top: 10px;
  box-sizing: border-box;

  .waterfall-scroll-container {
    height: 100%;
    width: 100%;
    overflow-x: hidden;
  }

  .waterfall-container {
    display: flex;
    gap: 20rpx;
    min-height: 100%;
    width: 100%;
    overflow-x: hidden;
  }

  .waterfall-column {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    min-width: 0;
  }

  .classificate__list-item {
    width: 100%;
    background-color: #fff;
    overflow: hidden;
    border-radius: 10rpx;
    background-color: #fff;
    .classificate__list-item--pic {
      display: block;
      width: 100%;
      height: auto;
      border-radius: 10rpx;
      max-width: 100%;
    }
  }

  .classificate__list-item-info {
    width: 100%;
    padding: 8px 5px;
    font-size: 11px;

    .classificate__list-item-info--tag {
      display: inline-block;
      width: 47rpx;
      text-align: center;
      color: #fff;
      border-radius: 4rpx;
      font-size: 18rpx;
      line-height: 28rpx;
      margin-right: 8rpx;
      flex-shrink: 0;
      position: relative;
      top: -2rpx;
    }

    .classificate__list-item-info--name {
      font-size: 15px;
      color: #333;
    }

    .classificate__list-item-info--specImg {
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
    .classificate__list-item-info--specImg-item {
      width: 38px;
      height: 38px;
      margin-right: 6rpx;
      flex-shrink: 0;
      cursor: pointer;
      overflow: hidden;

      &:last-child {
        margin-right: 0;
      }

      .u-image {
        width: 100%;
        height: 100%;
        border-radius: 4px;
      }
    }

    .classificate__list-item-info--sale {
      font-size: 13px;
      color: #a6a6a6;
      margin-top: 2px;
    }

    .classificate__list-item-info--discount {
      @include flex(flex-start);
      gap: 6px;
      font-size: 10px;
      margin-top: 7px;
      color: #fa3534;
      & > view {
        padding: 0px 3px;
        border-radius: 2px;
        border: 1px solid #fa3534;
      }
      .classificate__list-item-info--discount-free {
        color: #e3600a;
        border: 1px solid #e3600a;
        margin-left: 6px;
      }
    }
  }

  .classificate__list-item-info-price {
    margin-top: 7px;
    @include flex(space-between, center);
    font-family: Kingsoft_Cloud_Font;

    .classificate__list-item-info-price--num {
      color: #fb232f;
      font-size: 16px;
      font-weight: 500;
      &::before {
        content: '￥';
        font-size: 12px;
      }
    }

    .classificate__list-item-info-price--shopCar {
      color: #fb232f;
      padding-right: 3px;
    }
  }
}
</style>
