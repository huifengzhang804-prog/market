<template>
  <view :style="{ background: showMode === 'waterfall' ? '#f2f2f2' : '#fff' }">
    <view class="sort">
      <view
        v-for="(item, index) in sortList"
        :key="item.title"
        :class="{ active: currentIndex === index }"
        class="sort__tab"
        @click="handleChangeSort(index)"
      >
        {{ item.title }}
      </view>
      <view :class="{ active: currentIndex && currentIndex >= sortList.length }" class="sort__price sort__tab" @click="handleChangeSort(3)">
        <view style="margin-right: 10rpx">价格</view>
        <view class="sort__flex">
          <QIcon :color="currentIndex === 3 ? '#F54319' : '#999999'" class="sort__top" name="icon-shangjiantou1" />
          <QIcon :color="currentIndex === 4 ? '#F54319' : '#999999'" class="sort__bottom" name="icon-shangjiantou1" />
        </view>
      </view>
      <view class="sort__tab">
        <q-icon
          :name="showMode === 'waterfall' ? 'icon-liebiao' : 'icon-mofang1'"
          :size="showMode === 'waterfall' ? '35rpx' : '32rpx'"
          color="#666"
          style="display: inline-block; width: 36rpx"
          @click="changeShowMode"
        ></q-icon>
      </view>
    </view>
    <scroll-view :refresher-threshold="30" :style="{ height: scrollViewHeight }" scroll-y @scrolltolower="emit('initList', true)">
      <view :style="{ paddingBottom: '14rpx' }">
        <view style="box-shadow: 0px 160px 30px 190px rgb(255, 255, 255)"></view>
        <waterfall
          v-if="showMode === 'waterfall'"
          :list="list"
          :loading="preload"
          idKey="productId"
          :addTime="0"
          @changeSpecImg="handleChangeSpecImg"
          @addCart="handleAddCart"
        />
        <List
          v-else-if="list.length && showMode === 'list'"
          :list="list"
          :loading="preload"
          @changeSpecImg="handleChangeSpecImg"
          @addCart="handleAddCart"
        />
        <u-loadmore v-if="list.length && isPreloadList" :status="loadMoreStatus" />
        <q-empty v-if="!list.length && isPreloadList" :background="emptyConfig.background" :height="scrollViewHeight" :title="emptyConfig.title" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import QIcon from '@/components/q-icon/q-icon.vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { computed, onMounted, type PropType, ref } from 'vue'
import { doAddTocar, type RetrieveOrderByParams } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { useVModel } from '@vueuse/core'
import type { RetrieveCommodity } from '@/apis/good/model'
import waterfall from '@/pages/modules/classification/components/waterfall.vue'
import List from '@/pages/modules/classification/components/list.vue'
import { throttle } from 'lodash'

const selloutImg = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/96756530de4b0dd23f6b86f35.png'

const emit = defineEmits(['updateorderByParams', 'initList', 'update:list'])

const $props = defineProps({
  list: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
  loadMoreStatus: {
    type: String,
    default: 'nomore',
  },
  preload: {
    type: Boolean,
    default: false,
  },
})

// 展示模式 （瀑布流/列表）
const showMode = ref<'waterfall' | 'list'>('waterfall')
const list = useVModel($props, 'list', emit)
const currentIndex = ref<number | null>(null)
const isPreloadList = ref(false)
// 排序列表
const sortList = [
  {
    title: '综合',
  },
  {
    title: '销量',
  },
  {
    title: '新品',
  },
]
// 排序key
const enumSortKey: RetrieveOrderByParams = [
  [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
  { order: 'salesVolume', sort: 'DESC' },
  { order: 'createTime', sort: 'DESC' },
  { order: 'salePrices', sort: 'ASC' },
  { order: 'salePrices', sort: 'DESC' },
]
// 更改排序
const handleChangeSort = (idx: number) => {
  if (idx >= sortList.length) {
    currentIndex.value = currentIndex.value === 3 ? 4 : 3
  } else {
    currentIndex.value = idx
  }
  if (currentIndex.value === 0) {
    // 综合排序
    emit('updateorderByParams', [enumSortKey[1], enumSortKey[2]])
  } else {
    emit('updateorderByParams', [enumSortKey[currentIndex.value]])
  }
  emit('initList')
}

const changeShowMode = () => {
  showMode.value = showMode.value === 'waterfall' ? 'list' : 'waterfall'
  // 存储展示模式
  // #ifdef H5
  localStorage.setItem('showMode', showMode.value)
  // #endif
  // #ifndef H5
  uni.setStorageSync('showMode', showMode.value)
  // #endif
}

const searchNode = uni.upx2px(70)
const sortNode = uni.upx2px(120)
const navNode = uni.upx2px(88)
const safeHeight = ref(0)

onMounted(() => {
  uni.getSystemInfo({
    success: (res) => {
      if (res.safeAreaInsets) {
        const systermInfo = uni.getSystemInfoSync()
        safeHeight.value = systermInfo.screenHeight - (res.safeArea?.bottom ?? 0)
      }
    },
  })
  setTimeout(() => {
    isPreloadList.value = true
  }, 1000)
  // #ifdef H5
  showMode.value = (localStorage.getItem('showMode') as 'waterfall' | 'list') || showMode.value
  // #endif
  // #ifndef H5
  showMode.value = (uni.getStorageSync('showMode') as 'waterfall' | 'list') || showMode.value
  // #endif
})

const systermInfo = uni.getSystemInfoSync()
// 可视区域滑动高度计算
const scrollViewHeight = computed(() => {
  let differentialHeight = -6
  // #ifdef MP-WEIXIN
  differentialHeight = 2
  // #endif

  return `${systermInfo.screenHeight - (navNode + searchNode + sortNode + safeHeight.value + differentialHeight)}px`
})

const handleItemClick = (e: any) => {
  jumpGoods(e.shopId, e.productId)
}

const emptyConfig = {
  title: '暂无商品',
  background: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220531/1ce266a5e3fb43a4ae9b764fd8a166df.png',
}
const refresh = () => {
  // 瀑布流组件会自动响应list的变化，无需手动清除
}

const handleChangeSpecImg = (index: number, specImg: { image: string; id: string }, item: RetrieveCommodity) => {
  item.activeImg = index
  item.pic = specImg.image
}

const handleAddCart = throttle(async (val: RetrieveCommodity, skuId: string) => {
  const res = await doAddTocar({
    skuId: skuId || val.skuIds[0],
    shopId: val.shopId,
    productId: val.productId,
    num: 1,
  })
  if (res.code === 200) {
    uni.showToast({
      title: '加入购物车成功',
      icon: 'success',
    })
  }
}, 500)

defineExpose({
  refresh,
})
</script>

<style lang="scss" scoped>
@include b(sort) {
  background: #fff;
  height: 114rpx;
  @include flex(space-between);
  font-size: 24rpx;
  @include e(tab) {
    padding: 0 25rpx;
    height: 64rpx;
    font-size: 28rpx;
    text-align: center;
    line-height: 64rpx;
  }
  @include e(price) {
    @include flex();
  }
  @include e(flex) {
    display: flex;
    flex-direction: column;
  }
  @include e(bottom) {
    line-height: 14rpx;
    transform: rotate(180deg) scale(0.6);
  }
  @include e(top) {
    line-height: 14rpx;
    transform: scale(0.6);
  }
}

.active {
  color: #f54319;
  font-weight: bold;
  position: relative;
  &::after {
    content: '';
    display: block;
    width: 10px;
    height: 4px;
    border-radius: 2px;
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    background: #f54319;
  }
}

@include b(sell-out) {
  position: absolute;
  top: 0;
  width: 100%;
  height: 78%;
  background: rgba(0, 0, 0, 0.5);
  @include flex();
  @include e(img) {
    display: block;
    width: 234rpx;
    height: 181.11rpx;
  }
}
</style>
