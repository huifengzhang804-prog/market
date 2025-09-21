<script setup lang="ts">
import { computed, inject, reactive, ref, type PropType } from 'vue'
import GroupListItem from '@pluginPackage/group/components/group-list-item.vue'
import type { StorageSku, comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import type { ApiGroupListType, ApiGroupInfo, GroupDispatcherType } from '@/apis/plugin/group/model'

const $props = defineProps({
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  groupInfo: {
    type: Object as PropType<ApiGroupInfo>,
    default() {
      return {}
    },
  },
  groupExist: {
    type: Boolean,
    default: false,
  },
  groupJoin: {
    type: Boolean,
    default: false,
  },
  groupList: {
    type: Array as PropType<ApiGroupListType[]>,
    default() {
      return []
    },
  },
})
const { divTenThousand } = useConvert()
const showGroupList = ref(false)
const comProvideGoodsUse = inject('comProvideGoodsUse') as comDispatcherType
const groupProvide = inject('groupProvide') as GroupDispatcherType
const updateSetOperation = inject('updateSetOperation') as (key: any, value: any, isGroup?: boolean) => void
const groupScrollList = ref<ApiGroupListType[]>([])
const groupScrollPage = reactive({
  current: 1,
  size: 10,
  pages: 0,
})
// 与当前sku匹配出的拼团信息
const currentGroupInfo = computed(() => {
  const currentSkuArr = $props.groupInfo.skus.find((item) => {
    return item.skuId === $props.currentChoosedSku.id
  })
  return currentSkuArr || { prices: ['0'] }
})
// 是否展示凑团
const isHuddle = computed(() => {
  return $props.groupJoin && $props.groupList.length && $props.groupInfo.huddle
})

const handleShowList = () => {
  showGroupList.value = true
  initGroupList()
}
async function initGroupList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    groupScrollPage.current = 1
    groupScrollList.value = await getGroupList()
  } else if (isLoad && groupScrollPage.current < groupScrollPage.pages) {
    // 更新
    groupScrollPage.current++
    groupScrollList.value = groupScrollList.value.concat(await getGroupList())
  }
}
async function getGroupList() {
  const { shopId, productId } = $props.currentChoosedSku
  const data = await groupProvide.requestGroupList({ shopId, goodId: productId, ...groupScrollPage })
  if (data) {
    groupScrollPage.pages = data.pages
    return data.records
  } else {
    return []
  }
}
/**
 * 开团
 */
const handleCreateGroup = (userNum: number) => {
  if (comProvideGoodsUse && comProvideGoodsUse.setOperation) {
    // get 拼团活动的详情
    const orderParam = comProvideGoodsUse.getParam(comProvideGoodsUse.productId.value)
    if (orderParam) {
      let extra = { userNum }
      orderParam.extra = extra
      // 缓存 订单项 (订单需要知道是否是拼团活动)
      comProvideGoodsUse.addParam(orderParam)
    }
    const groupIndex = comProvideGoodsUse.TEAM.groupInfo.value.users.findIndex((item) => item === userNum)
    comProvideGoodsUse.TEAM.groupIndex.value = groupIndex
    // 开启弹窗之前需要把拼团的skus价格数据赋值给 总商品详情
    updateSetOperation('control', true)
    updateSetOperation('immediately', false)
    updateSetOperation('type', 'BUY')
    updateSetOperation('source', 'ACTIVITY', true)
  }
}
/**
 * 快速拼团
 */
function completeCountDown(item: ApiGroupListType) {}
</script>

<template>
  <view v-if="$props.groupExist && $props.groupInfo.teamStatus === 'OPEN'" class="group">
    <!-- 我要开团 -->
    <view v-if="$props.groupJoin" class="group__create">
      <view class="group__create-title f14 fontBold">我要开团</view>
      <view class="group__create-content">
        <view v-for="(item, index) in $props.groupInfo.users" :key="item" class="group__create-item">
          <view class="f14 fontBold">
            <text class="group__red">{{ item }}</text>
            <text>人团</text>
            <text class="group__mg">拼团价</text>
            <text class="group__red">￥{{ divTenThousand(currentGroupInfo.prices[index]) }}</text>
          </view>
          <view class="group__btn" @click="handleCreateGroup(item)">立即开团</view>
        </view>
      </view>
    </view>
    <view v-if="isHuddle" class="group__list">
      <view class="group__list-title">
        <text class="f14 fontBold">快速成团</text>
        <text class="f14 group__list-title--more" @click="handleShowList">查看全部</text>
      </view>
      <view class="group__list-content">
        <GroupListItem
          v-for="item in $props.groupList"
          :key="item.teamNo"
          :choose-group-index="item.totalNum"
          type="JOIN"
          :group-item="item"
          @count-end="completeCountDown"
        />
      </view>
    </view>
    <u-popup v-model="showGroupList" mode="center" border-radius="15" :mask-close-able="false" :closeable="true" width="666">
      <view class="group__pop-title f14 fontBold">正在拼团</view>
      <scroll-view scroll-y class="group__pop-content" style="height: 300px">
        <GroupListItem
          v-for="item in groupScrollList"
          :key="item.teamNo"
          :choose-group-index="item.totalNum"
          type="JOIN"
          :is-all="true"
          :group-item="item"
        />
      </scroll-view>
    </u-popup>
  </view>
</template>
<style lang="scss" scoped>
@include b(group) {
  margin: 0 10rpx;
  border-radius: 16rpx;
  background: #fff;
  @include e(create-title) {
    padding: 10rpx 0 16rpx 16rpx;
  }
  @include e(create-content) {
    padding: 14rpx 36rpx 10rpx 46rpx;
  }
  @include e(create-item) {
    @include flex(space-between);
    margin-bottom: 8rpx;
  }
  @include e(red) {
    color: #f00707;
  }
  @include e(mg) {
    margin: 0 10rpx;
  }
  @include e(btn) {
    width: 140rpx;
    height: 54rpx;
    line-height: 54rpx;
    text-align: center;
    color: #fff;
    border-radius: 6rpx;
    background-color: rgba(222, 50, 36, 1);
    font-weight: bold;
  }
  @include e(list-title) {
    padding: 50rpx 14rpx 20rpx 16rpx;
    @include flex(space-between);
    @include m(more) {
      color: #999;
      @include flex;
      &::after {
        content: '>';
        font-size: 24rpx;
        margin-left: 12rpx;
      }
    }
  }
  @include e(list-content) {
    padding: 0 36rpx 20rpx 32rpx;
  }
  @include e(pop-title) {
    width: 666rpx;
    height: 94rpx;
    line-height: 94rpx;
    text-align: center;
  }
  @include e(pop-content) {
    box-sizing: border-box;
    padding: 0 36rpx;
    height: 540rpx;
    overflow-y: auto;
  }
}
</style>
