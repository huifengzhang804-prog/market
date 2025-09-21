<script lang="ts" setup>
import { ref, type PropType } from 'vue'
import type { ConcernShopInfoItem } from '@/pages/modules/home/components/payattention/types'
import { onPageScroll } from '@dcloudio/uni-app'
import PayattentionImages from './payattention-images.vue'
import { doCancelAttentionAndAttention } from '@/apis/concern'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'

const $collectionDispatcherStore = useCollectionDispatcher()

const $props = defineProps({
  shop: { type: Object as PropType<ConcernShopInfoItem>, default: () => ({}) },
})

function navToShopInfo(shopId: Long) {
  uni.navigateTo({ url: `/basePackage/pages/merchant/Index?shopId=${shopId}` })
}

enum ShopTypeEnum {
  SELF_OWNED = '自营',
  PREFERRED = '优选',
  ORDINARY = '普通',
}

const showMore = ref(false)
const cancelTooltip = () => {
  showMore.value = false
}
const cacelFocusShopInfo = async () => {
  const { data, code, msg } = await doCancelAttentionAndAttention($props.shop.name, $props.shop.shopId, false, $props.shop.logo)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : `取消失败`}`, icon: 'none' })
  uni.showToast({
    title: `取消成功`,
    icon: 'none',
  })
  $collectionDispatcherStore.updateShopData()
}
onPageScroll(() => cancelTooltip())
defineExpose({ cancelTooltip })
</script>

<template>
  <view scroll-y="true">
    <view class="share">
      <view class="share__title">
        <view class="share__title-left">
          <view class="share__title-img">
            <image :src="$props.shop.logo" class="share__title-img--image" @click="navToShopInfo($props.shop.shopId)" />
          </view>
          <view class="share__title-ctx">
            <view class="share__title-ctx--weight">
              <text
                v-if="$props.shop.shopType && $props.shop.shopType !== 'ORDINARY'"
                :class="$props.shop.shopType === 'PREFERRED' ? 'prefer' : 'self'"
                >{{ ShopTypeEnum[$props.shop.shopType] }}
              </text>
              <text class="shop-name" @click="navToShopInfo($props.shop.shopId)">{{ $props.shop.name?.slice(0, 15) }} </text>
              <view class="icon" @click.stop="showMore = !showMore">
                <u-icon name="more-dot-fill" size="28" />
                <view v-if="showMore" class="dropdown-dialog">
                  <view class="dropdown-dialog__triangle"></view>
                  <view class="dropdown-dialog__wrapper">
                    <view class="dropdown-dialog__option" @click.stop="navToShopInfo($props.shop.shopId)">进入店铺 </view>
                    <view class="dropdown-dialog__option" @click.stop="cacelFocusShopInfo()">取消关注</view>
                  </view>
                </view>
              </view>
            </view>
            <view class="info">
              <text class="info__text info__new-text">新品上市</text>
              <text class="info__text">浏览 {{ $props.shop.pv }}</text>
              <text class="info__text">加购 {{ $props.shop.buyMore }}</text>
            </view>
          </view>
        </view>
      </view>
      <view class="share__content">
        <PayattentionImages :productList="$props.shop.productList" :shopId="shop.shopId" />
      </view>
      <view class="share__notice">
        <text class="tag">{{ $props.shop.newTips }}</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(tag) {
  flex-shrink: 0;
  &::before {
    display: inline-block;
    content: '上新';
    color: #fff;
    padding: 4rpx;
    border-radius: 10rpx;
    background: #24ce42;
    margin-right: 6rpx;
  }
}

@include b(share) {
  background: #fff;
  padding: 20rpx;
  border-radius: 16rpx;
  box-sizing: border-box;
  margin-bottom: 20rpx;
  @include e(title) {
    height: 140rpx;
    @include flex(space-between);
    width: 654rpx;
  }
  @include e(content) {
    @include flex(space-between);
    flex-wrap: wrap;
    margin-left: 114rpx;
    @include m(img) {
      width: 354rpx;
      height: 354rpx;
      border-radius: 10rpx;
      margin-bottom: 16rpx;
    }
  }
  @include e(notice) {
    margin-left: 114rpx;
    font-size: 20rpx;
    color: #838383;
    padding: 30rpx 0;
  }
  @include e(title-img) {
    width: 104rpx;
    height: 104rpx;
    margin-right: 12rpx;
    border-radius: 50%;
    background-color: #f9f9f9;
    flex-shrink: 0;
    @include flex();
    @include m(image) {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
    }
  }
  @include e(title-left) {
    @include flex(flex-start);
    width: 654rpx;
  }
  @include e(title-ctx) {
    font-size: 20rpx;
    color: #838383;
    flex: 1;
    @include m(weight) {
      @include flex(space-between, center);
      @include b(prefer) {
        width: 56rpx;
        height: 32rpx;
        border-radius: 4rpx;
        background-color: #fd9224;
        line-height: 32rpx;
        text-align: center;
        color: #fff;
        font-size: 20rpx;
        display: inline-block;
        margin-right: 16rpx;
        flex-shrink: 0;
      }
      @include b(self) {
        width: 56rpx;
        height: 32rpx;
        border-radius: 4rpx;
        background-color: #f54319;
        line-height: 32rpx;
        text-align: center;
        color: #fff;
        font-size: 20rpx;
        display: inline-block;
        margin-right: 16rpx;
        flex-shrink: 0;
      }
      @include b(shop-name) {
        font-size: 28rpx;
        line-height: 40rpx;
        color: #333;
        font-weight: 500;
        flex: 1;
      }
      @include b(icon) {
        flex-shrink: 0;
        color: #666;
        margin-left: 16rpx;
        font-size: 24rpx;
        position: relative;
        display: inline-block;
        @include b(dropdown-dialog) {
          position: absolute;
          // background-color: #fff;
          width: 188rpx;
          right: 0;
          top: 50rpx;
          @include e(wrapper) {
            width: 188rpx;
            background-color: #fff;
            box-shadow: 0px 0px 28rpx 8rpx rgba(219, 219, 219, 0.25);
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            border-radius: 8rpx;
            z-index: 12;
          }
          @include e(option) {
            padding: 8rpx 0 10rpx 0;
            line-height: 34rpx;
            text-align: center;
            color: #333;
            font-size: 24rpx;
          }
        }
      }
      // width: 300rpx;
      // @include utils-ellipsis(1);
      // font-size: 24rpx;
      // color: #333333;
      // font-weight: bold;
    }
    @include b(info) {
      color: #666;
      font-size: 24rpx;
      margin-top: 24rpx;
      line-height: 36rpx;
      position: relative;
      @include e(text) {
        margin-right: 16rpx;
      }

      @include e(new-text) {
        color: #f54319;
        font-weight: 700;
      }
    }
  }
  @include e(title-right) {
    width: 126rpx;
    height: 50rpx;
    border: 1px solid #333;
    line-height: 50rpx;
    text-align: center;
    font-size: 24rpx;
    border-radius: 30rpx;
    &::after {
      display: inline-block;
      content: '>';
      margin-left: 6rpx;
    }
  }
}

.dropdown-dialog__triangle {
  position: absolute;
  background-color: #fff;
  z-index: 0;
  right: 8rpx;
  top: -12rpx;
  box-shadow: 0px 0px 28rpx 8rpx rgba(219, 219, 219, 0.25);
  // margin-top: 100px;
  // margin-left: 100px;
}

.dropdown-dialog__triangle:before,
.dropdown-dialog__triangle:after {
  content: '';
  position: absolute;
  background-color: #fff;
  box-shadow: 0px 0px 28rpx 8rpx rgba(219, 219, 219, 0.25);
}

.dropdown-dialog__triangle,
.dropdown-dialog__triangle:before,
.dropdown-dialog__triangle:after {
  width: 28rpx;
  height: 28rpx;
  border-top-right-radius: 30%;
}

.dropdown-dialog__triangle {
  transform: rotate(-60deg) skewX(-30deg) scale(1, 0.886);
}

.dropdown-dialog__triangle:before {
  transform: rotate(-135deg) skewX(-45deg) scale(1.414, 0.707) translate(0, -50%);
}

.dropdown-dialog__triangle:after {
  transform: rotate(135deg) skewY(-45deg) scale(0.707, 1.414) translate(50%);
}
</style>
