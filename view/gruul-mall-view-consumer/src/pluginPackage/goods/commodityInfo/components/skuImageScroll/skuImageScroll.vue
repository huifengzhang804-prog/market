<script setup lang="ts">
import { computed, reactive, ref, watch, type PropType } from 'vue'
import type { StorageSku, ProductResponse } from '@/pluginPackage/goods/commodityInfo/types'
import { throttle } from 'lodash'

const props = defineProps({
  sku: {
    type: Array as PropType<StorageSku[]>,
    default() {
      return []
    },
  },
  info: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  skuConfig: {
    type: Object as PropType<{ currentSkuId: Long; isMainPic: boolean }>,
    default: () => ({}),
  },
})

const hasPicSkusLength = computed(() => {
  return props.sku.filter((item) => item.image).length
})
const skuChooseScreenWidth = reactive({
  screenWidth: 0,
  index: -1,
  scrollLeft: 0,
})
const $emit = defineEmits(['skuImageChange'])

uni.getSystemInfo({
  success: (res) => {
    skuChooseScreenWidth.screenWidth = res.screenWidth
  },
})

// 立即执行的防抖
const debounceClick = throttle(
  (e) => {
    if (!e.target.dataset.item) {
      return
    }
    const sku = e.target.dataset.item
    const index = parseInt(e.target.dataset.ind)

    // 防止重复点击同一项
    if (skuChooseScreenWidth.index === index) {
      return
    }

    handleSkusClick(sku, index)
  },
  300,
  {
    leading: true,
    trailing: false,
  },
)

const handleSkusClick = (sku: StorageSku, index: number, request = true) => {
  // 防止无效的index
  if (index < -1 || index >= props.sku.length) {
    console.warn('无效的index:', index)
    return
  }

  // 防止重复处理同一index
  if (skuChooseScreenWidth.index === index) {
    return
  }

  const windowWidth = skuChooseScreenWidth.screenWidth // 屏幕宽度
  const imageWidth = uni.upx2px(120) // 图片宽度 120rpx
  const imageMargin = uni.upx2px(20) // 图片间距 20rpx
  const splitWidth = uni.upx2px(60) // 分割文字宽度 60rpx
  const containerPadding = uni.upx2px(30) // 容器左右padding 15rpx * 2

  // 计算当前选中项的位置
  let targetPosition = 0

  if (index === -1) {
    // 主图位置
    targetPosition = 0
  } else {
    // SKU图片位置：主图 + 分割文字 + 前面所有SKU图片
    targetPosition = imageWidth + imageMargin + splitWidth + imageMargin + (imageWidth + imageMargin) * index
  }

  // 计算可滚动区域宽度（减去容器padding）
  const scrollableWidth = windowWidth - containerPadding * 2

  // 计算滚动位置，让选中项居中显示
  let centerPosition = 0

  if (index === -1) {
    // 主图不需要滚动
    centerPosition = 0
  } else {
    // 计算选中项应该滚动到的位置
    // 使用更简单的计算方式：目标位置减去可滚动区域的一半
    centerPosition = targetPosition - scrollableWidth / 2 + imageWidth / 2
  }

  // 确保滚动位置在合理范围内
  const finalScrollLeft = Math.max(0, centerPosition)

  // 只有当计算出的位置合理时才更新滚动位置
  if (finalScrollLeft >= 0) {
    skuChooseScreenWidth.scrollLeft = finalScrollLeft
  }

  skuChooseScreenWidth.index = index
  if (request) {
    $emit('skuImageChange', sku, index)
  }
}

// 保留这些变量以备将来使用
// const skuConfigCompute = computed(() => ({
//   currentSkuId: props.skuConfig?.currentSkuId,
//   skus: props.sku,
//   isMain: props.skuConfig?.isMainPic,
// }))

// const curSkuIdTemp = ref('')
// const isProcessing = ref(false)

// 移除watch，改为手动控制
// watch(
//   () => JSON.parse(JSON.stringify(skuConfigCompute.value)),
//   (val, old) => {
//     // 防止重复处理
//     if (isProcessing.value) {
//       return
//     }

//     // 如果从主图切换到主图,直接返回
//     if (val.isMain && old.isMain) {
//       return
//     }

//     // 防止无效的index
//     const index = props.sku?.findIndex((item) => item.id === val.currentSkuId)
//     if (index === -1) {
//       return
//     }

//     // 添加调试信息
//     console.log('watch触发:', { val, old, index })

//     isProcessing.value = true

//     // 如果是切换到主图,要切换到相应位置
//     if (val.isMain && !old.isMain) {
//       handleSkusClick(props.sku[index], -1, false)
//       setTimeout(() => {
//         isProcessing.value = false
//       }, 100)
//       return
//     }
//     // 如果是sku发生变化,要切换到相应位置,记录skuId
//     if (val.currentSkuId !== old.currentSkuId) {
//       curSkuIdTemp.value = val.currentSkuId
//       handleSkusClick(props.sku[index], index, false)
//       setTimeout(() => {
//         isProcessing.value = false
//       }, 100)
//       return
//     }

//     isProcessing.value = false
//     // !!! 从主图切换到第一张sku图的情况在这种设计下,没办法只能由父组件来控制(不然小轮播图的红框会闪一下旧的sku)
//     // TODOS:直接获取大轮播图的 change 事件
//   },
//   { deep: false },
// )
defineExpose({
  handleSkusClick,
})
</script>

<template>
  <scroll-view
    v-if="hasPicSkusLength > 1"
    class="swiper-sku"
    scroll-x
    enhanced
    :show-scrollbar="false"
    :scroll-left="skuChooseScreenWidth.scrollLeft"
    scroll-with-animation
    scroll-anchoring
    enable-passive
    enable-flex
  >
    <view class="swiper_sku_img_container" @click="debounceClick">
      <view
        class="swiper-sku__image"
        :class="{ 'active-sku': skuChooseScreenWidth.index === -1 }"
        :data-ind="-1"
        :data-item="sku?.[0]"
        :style="`background-image: url(${props.info.pic})`"
      >
      </view>
      <view class="split_text">
        <view>选择</view>
        <view>规格</view>
      </view>
      <template v-if="props.sku.some((item) => item.image)">
        <view
          v-for="(item, ind) in props.sku"
          :key="item.id"
          class="swiper-sku__image"
          :class="{ 'active-sku': ind === skuChooseScreenWidth.index }"
          :data-ind="ind"
          :data-item="item"
          :style="`background-image: url(${item.image})`"
        >
        </view>
      </template>
    </view>
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(swiper-sku) {
  padding: 10rpx 15rpx !important;
  box-sizing: border-box;
  white-space: nowrap;
  overflow-anchor: none;
  .swiper_sku_img_container {
    display: flex;
    align-items: center;
    width: 100%;
  }
  @include e(image) {
    display: inline-block;
    border-radius: 14rpx;
    overflow: hidden;
    flex-shrink: 0;
    width: 120rpx;
    height: 120rpx;
    margin-right: 20rpx;
    border: 2px solid transparent;
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center;
  }
  .split_text {
    width: 60rpx;
    font-size: 24rpx;
    color: #777;
    display: flex;
    flex-wrap: wrap;
    flex-shrink: 0;
    flex-direction: column;
    align-items: center;
    margin-right: 20rpx;
  }
}
@include b(active-sku) {
  border-color: #e31436;
}
::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent;
}
</style>
