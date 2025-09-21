<script setup lang="ts">
import { watch, ref, nextTick, onMounted, computed } from 'vue'
import type { ApiCategoryData, DeCategoryType } from '@/pages/platform/types'
import type { PropType } from 'vue'
import type { RetrieveCommodity } from '@/apis/good/model'
import { cloneDeep } from 'lodash'
import Waterfall from './waterfall.vue'
import List from './list.vue'
import { doAddTocar } from '@/apis/good'
import { throttle } from 'lodash'
import QIcon from '@/components/q-icon/q-icon.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'

const $props = defineProps({
  height: {
    type: Number,
    default: 1000,
  },
  firstCateList: {
    type: Object as PropType<ApiCategoryData>,
    default() {
      return {}
    },
  },
  currentChooseIndex: {
    type: Number,
    default: 0,
  },
  list: {
    type: Array as PropType<RetrieveCommodity[]>,
    default() {
      return []
    },
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
})

const $emit = defineEmits(['changeTab', 'listLoadMore', 'changeTwoTab'])
const showmore = ref(false)
const firstList = ref<ApiCategoryData>($props.firstCateList)
const currentTwoId = ref('')
const liveTwoShow = ref(false)

// 添加ref引用
const sortScroll = ref()
const secScroll = ref()

// 添加高度状态
const firstModuleHeight = ref(0)
const secondModuleHeight = ref(0)

// 获取第一个模块高度的方法
const getFirstModuleHeight = () => {
  const query = uni.createSelectorQuery()
  query
    .select('.five_one_scroll')
    .boundingClientRect((data) => {
      if (data && !Array.isArray(data) && typeof data.height === 'number') {
        firstModuleHeight.value = data.height
        console.log('第一个模块高度1:', firstModuleHeight.value)
      } else {
        firstModuleHeight.value = uni.upx2px(90)
      }
    })
    .exec()
}

// 获取第二个模块高度的方法
const getSecondModuleHeight = () => {
  const query = uni.createSelectorQuery()
  query
    .select('.five_two_scroll')
    .boundingClientRect((data) => {
      if (data && !Array.isArray(data) && typeof data.height === 'number') {
        secondModuleHeight.value = data.height
        console.log('第二个模块高度2:', secondModuleHeight.value)
      } else {
        secondModuleHeight.value = uni.upx2px(210)
      }
    })
    .exec()
}

// 获取两个模块高度的综合方法
const getModulesHeight = () => {
  nextTick(() => {
    getFirstModuleHeight()
    getSecondModuleHeight()
  })
}

watch(
  () => $props.firstCateList,
  () => {
    initfirstCateList()
    currentTwoId.value = firstList.value.children?.[0]?.id || ''
  },
)

const goodsListCopy = ref<RetrieveCommodity[]>([])
const weixinHeight = ref(0)

watch(
  () => $props.list,
  async (val: RetrieveCommodity[]) => {
    goodsListCopy.value = cloneDeep(val)
    // 再次等待tick，确保数据更新完成
    await nextTick()
  },
  {
    deep: true,
    immediate: true,
  },
)

onMounted(() => {
  if (firstList.value.children && firstList.value.children.length > 0) {
    currentTwoId.value = firstList.value.children[0].id
  }
  // 在组件挂载后获取高度
  getModulesHeight()
})

async function initfirstCateList() {
  const list = $props.firstCateList
  if (!list.children) list.children = []
  firstList.value = list
  if (firstList.value.children && firstList.value.children.length > 10) {
    firstList.value.children.push({
      id: '-1',
      categoryImg: '',
      name: '收起',
      children: [],
    })
  }
  // 数据更新后重新获取高度
  nextTick(() => {
    getModulesHeight()
  })
}
const hanleChangeTab = (idx: number) => {
  $emit('changeTab', idx)
}
const scrolltolower = () => {
  $emit('listLoadMore', currentTwoId.value)
}

const handleNavToRetrive = (item: any) => {
  currentTwoId.value = item.id
  $emit('changeTwoTab', item.id)
}
const handleChangeSpecImg = (index: number, specImg: { image: string; id: string }, item: RetrieveCommodity) => {
  item.activeImg = index
  item.pic = specImg.image
}

const handleAddCart = throttle(async (val: RetrieveCommodity, skuId?: string) => {
  jumpGoods(val.shopId, val.productId, true, skuId)
}, 500)

const handleSecondShow = () => {
  liveTwoShow.value = true
  // #ifdef MP-WEIXIN
  weixinHeight.value = 170
  // #endif
}

const handleChooseUnit = (item: any, index: number) => {
  liveTwoShow.value = false
  handleNavToRetrive(item)

  // 计算滚动位置，让选中的项目居中显示
  nextTick(() => {
    // 每个项目的宽度大约是 110rpx + 40rpx 间距 = 150rpx
    // 转换为 px (假设 1rpx = 0.5px)
    const itemWidth = 75 // 150rpx * 0.5
    const containerWidth = uni.getSystemInfoSync().windowWidth * 0.83 // 83% 的屏幕宽度
    const scrollPosition = Math.max(0, index * itemWidth - containerWidth / 2 + itemWidth / 2)
    scrollOptions.value = scrollPosition
  })
}

const scrollOptions = ref(0)

const handleScroll = (e: any) => {
  scrollOptions.value = e.detail.scrollLeft
}
</script>

<template>
  <view class="classificate">
    <scroll-view
      ref="sortScroll"
      class="classificate__sort five_one_scroll"
      style="padding: 0 20rpx; height: 90rpx"
      scroll-x
      enhanced
      :show-scrollbar="false"
    >
      <view style="display: flex; align-items: center">
        <view
          v-for="(item, index) in $props.config.categoryList"
          :key="item.platformCategoryFirstId"
          class="classificate__sort-item"
          :class="{ 'classificate__sort-item--active': $props.currentChooseIndex === index }"
          @click="hanleChangeTab(index)"
        >
          {{ item.platformCategoryFirstName }}
        </view>
      </view>
    </scroll-view>
    <view ref="secScroll" class="five_two_scroll" style="width: 100%; background-color: #fff; display: flex; height: 210rpx">
      <scroll-view
        class="classificate__sort"
        scroll-x
        enhanced
        :show-scrollbar="false"
        style="width: 83%"
        :scroll-left="scrollOptions"
        @scroll="handleScroll"
      >
        <view class="classificate__sec">
          <view v-for="(item, index) in firstList.children" :key="item.id" @click="handleNavToRetrive(item)">
            <view
              v-if="
                (index !== 9 || (firstList.children && firstList.children.length === 10) || showmore) &&
                ((firstList.children && firstList.children.length <= 10) || (firstList.children && firstList.children.length !== index + 1))
              "
              class="classificate__sec-item"
            >
              <image :src="item.categoryImg" class="classificate__sec-item--pic"></image>
              <view class="classificate__sec-item--name" :style="{ color: currentTwoId === item.id ? '#fa3534' : '#333' }">{{ item.name }}</view>
            </view>
          </view>
        </view>
      </scroll-view>
      <view v-if="firstList.children && firstList.children.length > 4" class="classificate__sort-icon">
        <q-icon name="icon-gengduo" size="90rpx" color="#C4C4C4" @click="handleSecondShow"></q-icon>
      </view>
    </view>
    <scroll-view scroll-y :style="{ height: $props.height - firstModuleHeight - secondModuleHeight + 'px' }" @scrolltolower="scrolltolower">
      <view style="box-shadow: 0px 100px 35px 140px #fff"></view>
      <template v-if="config.commodityShow === 2">
        <Waterfall :list="goodsListCopy" :config="config" @changeSpecImg="handleChangeSpecImg" @addCart="handleAddCart" />
      </template>
      <template v-else>
        <List :list="goodsListCopy" :config="config" @changeSpecImg="handleChangeSpecImg" @addCart="handleAddCart" />
      </template>
    </scroll-view>

    <u-popup v-model="liveTwoShow" mode="top" border-radius="20" height="auto">
      <view class="classificate__second" :style="{ marginTop: weixinHeight + 'rpx' }">
        <view
          v-for="(item, index) in firstList.children"
          :key="item.id"
          class="classificate__second-item"
          :style="{
            color: currentTwoId === item.id ? '#FA3534' : '#232323',
          }"
          @click="handleChooseUnit(item, index)"
        >
          <image :src="item.categoryImg" style="width: 110rpx; height: 110rpx; border-radius: 10rpx" />
          <view class="classificate__second-itemName">{{ item.name }}</view>
        </view>
      </view>
      <view style="width: 100%; text-align: center; padding: 20rpx 0; color: #979797" @click="liveTwoShow = false">
        <text>点击收起<q-icon name="icon-jiantoushang" color="#979797" size="24rpx" style="margin-left: 10rpx"></q-icon></text>
      </view>
    </u-popup>
  </view>
</template>

<style lang="scss" scoped>
@include b(classificate) {
  position: relative;
  @include e(sort) {
    width: 100%;
    padding: 20rpx 12px;
    background: #fff;
    box-sizing: border-box;
    white-space: nowrap;
    &::-webkit-scrollbar {
      display: none;
      width: 0 !important;
    }
  }
  @include e(sort-item) {
    flex-shrink: 0;
    padding: 9px 0;
    margin-right: 29px;
    font-size: 16px;
    cursor: pointer;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #000;

    @include e(sort-icon) {
      width: 100rpx;
      height: 100rpx;
      line-height: 100rpx;
      background-color: #ebebeb;
      border-radius: 55rpx;
      text-align: center;
      position: relative;
      top: 25rpx;
    }
  }

  @include e(sort-item--active) {
    font-weight: 600;
    color: #fa3534;
    font-family: Microsoft YaHei;
    position: relative;
    &::after {
      content: '';
      position: absolute;
      left: 50%;
      bottom: 0px;
      transform: translateX(-50%);
      width: 15px;
      height: 5px;
      background: #fa3534;
      border-radius: 1px 1px 5px 5px;
    }
  }

  @include e(sec) {
    width: 100%;
    background: #ffffff;
    display: flex;
    gap: 40rpx;
  }
  @include e(sec-item) {
    width: 110rpx;
    display: flex;
    flex-shrink: 0;
    flex-direction: column;
    align-items: center;
    @include m(pic) {
      width: 110rpx;
      height: 110rpx;
      margin-bottom: 10px;
      border-radius: 10rpx;
    }
    @include m(name) {
      width: 110rpx;
      color: #333;
      font-size: 12px;
      font-weight: 500;
      text-align: center;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  @include b(list) {
    width: 100%;
    background-color: #fff;
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
  }
  @include e(list-item) {
    background: #fff;
    width: 172px;
    height: 219px;
    margin-bottom: 14rpx;
    border-radius: 20rpx;
    @include m(pic) {
      border-radius: 20rpx 20rpx 0 0;
      width: 172px;
      height: 163px;
    }
  }
  @include e(list-item-info) {
    height: 59px;
    padding: 10px;
    font-size: 11px;
    @include m(name) {
      width: 155px;
      margin-bottom: 6rpx;
      @include utils-ellipsis();
    }
    @include m(price) {
      color: #fa3534;
    }
  }

  @include b(classificate__second) {
    width: 100%;
    background-color: #fff;
    display: flex;
    flex-wrap: wrap;
    gap: 30rpx;
    padding: 40rpx;
  }
  @include b(classificate__second-itemName) {
    width: 110rpx;
    @include utils-ellipsis(2);
  }
}
</style>
