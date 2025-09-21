<template>
  <view class="skeleton-waterfall">
    <view class="skeleton-waterfall-container">
      <view v-for="(column, columnIndex) in skeletonColumns" :key="columnIndex" class="skeleton-waterfall-column">
        <view v-for="item in column" :key="item" class="skeleton-item">
          <!-- 商品图片骨架 -->
          <view class="skeleton-item-pic skeleton-shimmer"></view>

          <view class="skeleton-item-info">
            <!-- 商品名称骨架 -->
            <view class="skeleton-item-name">
              <view class="skeleton-tag skeleton-shimmer"></view>
              <view class="skeleton-title skeleton-shimmer"></view>
            </view>

            <!-- 规格图片骨架 -->
            <view class="skeleton-spec-container">
              <view v-for="i in 3" :key="i" class="skeleton-spec-item skeleton-shimmer"></view>
            </view>

            <!-- 销量骨架 -->
            <view class="skeleton-sales skeleton-shimmer"></view>

            <!-- 优惠信息骨架 -->
            <view class="skeleton-discount">
              <view class="skeleton-coupon skeleton-shimmer"></view>
              <view class="skeleton-free skeleton-shimmer"></view>
            </view>

            <!-- 价格和购物车骨架 -->
            <view class="skeleton-price-container">
              <view class="skeleton-price skeleton-shimmer"></view>
              <view class="skeleton-cart skeleton-shimmer"></view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  count?: number // 骨架屏项目数量
  columns?: number // 列数
}

const props = withDefaults(defineProps<Props>(), {
  count: 6,
  columns: 2,
})

// 生成骨架屏数据
const skeletonColumns = computed(() => {
  const columns: number[][] = Array.from({ length: props.columns }, () => [])
  for (let i = 0; i < props.count; i++) {
    const columnIndex = i % props.columns
    columns[columnIndex].push(i)
  }
  return columns
})
</script>

<style lang="scss" scoped>
.skeleton-waterfall {
  box-sizing: border-box;

  .skeleton-waterfall-container {
    display: flex;
    gap: 20rpx;
    width: 100%;
  }

  .skeleton-waterfall-column {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    min-width: 0;
  }

  .skeleton-item {
    width: 100%;
    background-color: #fff;
    border-radius: 10rpx;
    overflow: hidden;
  }

  .skeleton-item-pic {
    width: 100%;
    height: 200rpx;
    background-color: #f5f5f5;
    border-radius: 10rpx 10rpx 0 0;
  }

  .skeleton-item-info {
    padding: 8px 5px;
  }

  .skeleton-item-name {
    display: flex;
    align-items: flex-start;
    gap: 8rpx;
    margin-bottom: 5px;
  }

  .skeleton-tag {
    width: 47rpx;
    height: 28rpx;
    background-color: #f5f5f5;
    border-radius: 4rpx;
    flex-shrink: 0;
  }

  .skeleton-title {
    flex: 1;
    height: 32rpx;
    background-color: #f5f5f5;
    border-radius: 4rpx;
  }

  .skeleton-spec-container {
    display: flex;
    gap: 6rpx;
    margin: 5px 0;
  }

  .skeleton-spec-item {
    width: 38px;
    height: 38px;
    background-color: #f5f5f5;
    border-radius: 4px;
    flex-shrink: 0;
  }

  .skeleton-sales {
    width: 80rpx;
    height: 26rpx;
    background-color: #f5f5f5;
    border-radius: 4rpx;
    margin: 2px 0;
  }

  .skeleton-discount {
    display: flex;
    gap: 6px;
    margin: 7px 0;
  }

  .skeleton-coupon {
    width: 60rpx;
    height: 24rpx;
    background-color: #f5f5f5;
    border-radius: 2rpx;
  }

  .skeleton-free {
    width: 40rpx;
    height: 24rpx;
    background-color: #f5f5f5;
    border-radius: 2rpx;
  }

  .skeleton-price-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 7px;
  }

  .skeleton-price {
    width: 80rpx;
    height: 32rpx;
    background-color: #f5f5f5;
    border-radius: 4rpx;
  }

  .skeleton-cart {
    width: 40rpx;
    height: 40rpx;
    background-color: #f5f5f5;
    border-radius: 50%;
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
}
</style>
