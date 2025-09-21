<script setup lang="ts">
import type { PropType } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import linkNavTo from '@/libs/linkNavTo'
import { useUserStore } from '@/store/modules/user'
import type { UserCenterMenuItem } from '../types'
import type { LinkType } from '@decoration/components/types'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'

const $props = defineProps({
  showStyle: {
    type: Number,
    default: 1,
  },
  menuScratchable: {
    type: Array as PropType<UserCenterMenuItem[]>,
    default() {
      return []
    },
  },
  menuList: {
    type: Array as PropType<UserCenterMenuItem[]>,
    default() {
      return []
    },
  },
})
const $userStore = useUserStore()

const handleNavTo = (link: LinkType) => {
  if ($userStore.getterToken) {
    linkNavTo(link)
  } else {
    // uni.showToast({ icon: 'none', title: '请先登录111' })
    uni.$emit(TOKEN_DEL_KEY)
  }
}
</script>

<template>
  <!-- 列表模式 -->
  <view v-if="$props.showStyle === 1" class="box">
    <template v-for="item in $props.menuList" :key="item.menuName">
      <view v-if="item.showMenu && item.menuName" class="box__list-item" hover-class="hover-class" @click="handleNavTo(item.linkSelectItem)">
        <view class="box__list-item-left">
          <u-image :width="50" :height="50" :src="item.menuIconUrl" />
          <text class="box__list-item-left--text">{{ item.menuName }}</text>
        </view>
        <q-icon name="icon-xiajiantou" size="40rpx" class="box__list-item--icon" />
      </view>
      <view v-if="!item.menuName" style="height: 30rpx"> </view>
    </template>
  </view>
  <view v-else class="box white">
    <view class="box__gird-title">我的工具</view>
    <view class="box__grid-content">
      <template v-for="item in $props.menuScratchable" :key="item.menuName">
        <view v-if="item.showMenu" class="box__grid-item" hover-class="hover-class" @click="handleNavTo(item.linkSelectItem)">
          <u-image :width="56" :height="56" :src="item.menuIconUrl" />
          <view class="box__grid-item--text">{{ item.menuName }}</view>
        </view>
      </template>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(box) {
  margin: 0 28rpx;
  margin-top: 20rpx;
  border-radius: 10rpx;
  @include e(list-item) {
    @include flex(space-between);
    height: 96rpx;
    margin-bottom: 10rpx;
    color: #303133;
    border-bottom: 1px solid #ebeef5;
    background-color: #ffffff;
    padding: 0 30rpx;
    font-size: 26rpx;
    font-weight: 500;
    transition: border-bottom-color 0.3s;
    outline: 0;
    @include m(icon) {
      transform: rotate(-90deg);
    }
  }

  @include e(list-item-left) {
    @include flex();
    @include m(text) {
      margin-left: 30rpx;
      margin-bottom: -6rpx;
    }
  }
  // grid
  @include e(gird-title) {
    padding: 30rpx 40rpx 0 40rpx;
    font-size: 28rpx;
    font-weight: bold;
  }
  @include e(grid-content) {
    padding: 30rpx 0;
    display: grid;
    grid-template-columns: repeat(4, 172rpx);
    // grid-template-rows: 100px 200px;
    // grid-column-gap: 50px;
    grid-row-gap: 50rpx;
  }
  @include e(grid-item) {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 26rpx;
    color: #45403c;
    @include m(text) {
      margin-top: 30rpx;
    }
  }
}
.white {
  background-color: #fff;
}
</style>
