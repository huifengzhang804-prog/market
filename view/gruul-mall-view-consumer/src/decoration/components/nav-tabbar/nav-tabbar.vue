<script setup lang="ts">
import { type PropType, ref, computed } from 'vue'
import { useSettingStore } from '@/store/modules/setting'
// #ifndef H5
import linkNavTo from '@/libs/linkNavTo'
// #endif
import { cropImg } from '@/utils'
import type { NavBarMenuType } from '@decoration/components/types'

const $props = defineProps({
  codeStyle: {
    type: Number,
    default: 1,
  },
  defaultColor: {
    type: String,
    default: '',
  },
  selectColor: {
    type: String,
    default: '',
  },
  menuList: {
    type: Array as PropType<NavBarMenuType[]>,
    default() {
      return []
    },
  },
  currentChooseId: {
    type: String,
    default: '',
  },
  underfillColor: {
    type: String,
    default: '#fff',
  },
  // 是否开启节点占位
  placeholderNode: { type: Boolean, default: false },
})

const $emit = defineEmits(['tabChange'])
const $settingStore = useSettingStore()
// iphoneX以上机型底部安全距离
const safeHeight = useBottomSafe()

const handleChangeBar = (idx: number, item: NavBarMenuType) => {
  if (['/basePackage/pages/merchant/Index', '/pages/merchant/Index'].includes(item.link.url) || item.link.url === '/') {
    $emit('tabChange', { index: idx, link: $props.menuList[idx].link })
  } else {
    // #ifdef H5
    import('@/libs/linkNavTo').then(({ default: linkNavTo }) => {
      linkNavTo(item.link)
    })
    // #endif
    // #ifndef H5
    linkNavTo(item.link)
    // #endif
  }
}
const tabbarHeight = uni.upx2px(100)
</script>

<template>
  <view v-if="$props.menuList.length" class="tabbar" :style="{ height: `${tabbarHeight + safeHeight}px`, backgroundColor: $props.underfillColor }">
    <view v-for="(item, index) in $props.menuList" :key="item.text" class="tabbar__item" @click="handleChangeBar(index, item)">
      <!-- #ifndef H5 -->
      <image
        v-if="$props.currentChooseId !== item.link.id"
        mode="aspectFill"
        :src="cropImg(item.iconType === 1 ? item.defIcon : item.iconPath, 60, 60)"
        :class="[$props.codeStyle === 2 && index === 2 ? 'tabbar__item-big' : 'tabbar__item-image']"
      />
      <image
        v-else
        mode="aspectFill"
        :src="cropImg(item.iconType === 1 ? item.actIcon : item.selectedIconPath, 60, 60)"
        class="tabbar__item-image"
        :class="[$props.codeStyle === 2 && index === 2 ? 'tabbar__item-big' : 'tabbar__item-image']"
      />
      <!-- #endif -->

      <!-- #ifdef H5 -->
      <img
        v-if="$props.currentChooseId !== item.link.id"
        :src="cropImg(item.iconType === 1 ? item.defIcon : item.iconPath, 60, 60)"
        :class="[$props.codeStyle === 2 && index === 2 ? 'tabbar__item-big' : 'tabbar__item-image']"
      />
      <img
        v-else
        mode="aspectFill"
        :src="cropImg(item.iconType === 1 ? item.actIcon : item.selectedIconPath, 60, 60)"
        class="tabbar__item-image"
        :class="[$props.codeStyle === 2 && index === 2 ? 'tabbar__item-big' : 'tabbar__item-image']"
      />
      <!-- #endif -->

      <view class="tabbar__item-text" :style="{ color: $props.currentChooseId === item.link.id ? $props.selectColor : $props.defaultColor }">{{
        item.text
      }}</view>
    </view>
  </view>
  <view v-if="placeholderNode" :style="{ height: `${tabbarHeight + safeHeight}px` }"></view>
</template>

<style lang="scss" scoped>
@include b(tabbar) {
  position: fixed;
  box-shadow: 0px 0px 16px 0px rgba(142, 142, 142, 0.15);
  -webkit-box-shadow: 0px 0px 16px 0px rgba(142, 142, 142, 0.15);
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 100rpx;
  font-size: 20rpx;
  background: #fff;
  z-index: 99;
  @include flex(space-between);
  @include e(item) {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  @include e(item-text) {
    font-size: 26rpx;
    margin-top: 6rpx;
  }
  @include e(item-image) {
    width: 44rpx;
    height: 44rpx;
    // #ifdef H5
    object-fit: fill;
    // #endif
  }
  @include e(item-big) {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-top: -36rpx;
  }
}
</style>
