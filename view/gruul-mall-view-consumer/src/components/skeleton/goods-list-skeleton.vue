<template>
  <view class="goods-list-skeleton">
    <view v-for="i in count" :key="i" class="goods-list-skeleton-item">
      <!-- 商品图片骨架 -->
      <view class="goods-list-skeleton-pic skeleton-shimmer"></view>

      <view class="goods-list-skeleton-info">
        <!-- 商品名称和标签骨架 -->
        <view class="goods-list-skeleton-name">
          <view class="goods-list-skeleton-tag skeleton-shimmer"></view>
          <view class="goods-list-skeleton-title skeleton-shimmer"></view>
        </view>

        <!-- 规格图片骨架 -->
        <view class="goods-list-skeleton-spec-container">
          <view v-for="specIndex in 3" :key="specIndex" class="goods-list-skeleton-spec-item skeleton-shimmer"></view>
        </view>

        <!-- 销量骨架 -->
        <view class="goods-list-skeleton-sales skeleton-shimmer"></view>

        <!-- 优惠信息骨架 -->
        <view class="goods-list-skeleton-discount">
          <view class="goods-list-skeleton-coupon skeleton-shimmer"></view>
          <view class="goods-list-skeleton-free skeleton-shimmer"></view>
        </view>

        <!-- 价格和购物车骨架 -->
        <view class="goods-list-skeleton-price-container">
          <view class="goods-list-skeleton-price skeleton-shimmer"></view>
          <view class="goods-list-skeleton-cart skeleton-shimmer"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
interface Props {
  count?: number // 骨架屏项目数量
}

const props = withDefaults(defineProps<Props>(), {
  count: 6,
})
</script>

<style lang="scss" scoped>
.goods-list-skeleton {
  background-color: #fff;
  display: flex;
  flex-direction: column;

  .goods-list-skeleton-item {
    padding: 10px;
    height: 100%;
    display: flex;
    align-items: flex-start;

    .goods-list-skeleton-pic {
      width: 115px;
      height: 115px;
      border-radius: 5px;
      background-color: #f5f5f5;
      flex-shrink: 0;
    }

    .goods-list-skeleton-info {
      padding-left: 10px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      flex: 1;
      height: 115px;

      .goods-list-skeleton-name {
        width: 490rpx;
        display: flex;
        align-items: center;
        gap: 8rpx;

        .goods-list-skeleton-tag {
          width: 47rpx;
          height: 28rpx;
          background-color: #f5f5f5;
          border-radius: 4rpx;
          flex-shrink: 0;
        }

        .goods-list-skeleton-title {
          flex: 1;
          height: 32rpx;
          background-color: #f5f5f5;
          border-radius: 4rpx;
        }
      }

      .goods-list-skeleton-spec-container {
        display: flex;
        align-items: center;
        gap: 6rpx;
        margin: 5px 0;

        .goods-list-skeleton-spec-item {
          width: 33px;
          height: 33px;
          background-color: #f5f5f5;
          border-radius: 4px;
          flex-shrink: 0;
        }
      }

      .goods-list-skeleton-sales {
        width: 80rpx;
        height: 26rpx;
        background-color: #f5f5f5;
        border-radius: 4rpx;
        margin: 2px 0;
      }

      .goods-list-skeleton-discount {
        display: flex;
        gap: 6px;
        margin: 7px 0;

        .goods-list-skeleton-coupon {
          width: 60rpx;
          height: 24rpx;
          background-color: #f5f5f5;
          border-radius: 2rpx;
        }

        .goods-list-skeleton-free {
          width: 40rpx;
          height: 24rpx;
          background-color: #f5f5f5;
          border-radius: 2rpx;
        }
      }

      .goods-list-skeleton-price-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 7px;

        .goods-list-skeleton-price {
          width: 80rpx;
          height: 32rpx;
          background-color: #f5f5f5;
          border-radius: 4rpx;
        }

        .goods-list-skeleton-cart {
          width: 40rpx;
          height: 40rpx;
          background-color: #f5f5f5;
          border-radius: 50%;
        }
      }
    }
  }
}

// 闪烁动画
.skeleton-shimmer {
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
    animation: shimmer 1.5s infinite;
  }
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
</style>
