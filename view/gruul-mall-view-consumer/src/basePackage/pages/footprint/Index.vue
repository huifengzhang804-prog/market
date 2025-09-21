<script setup lang="ts">
import { computed, reactive, ref, toRefs, watch } from 'vue'
import MyCalendar from '@/components/my-calendar/Index.vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import QNav from '@/components/q-nav/q-nav.vue'
import Auth from '@/components/auth/auth.vue'
import { useFootprintCollection } from '@/hooks'
import { doGetUserFootprintList, doDelUserFootprint, doAddAssess } from '@/apis/consumer/footprint'
import { EMPTY_GB } from '@/constant'
import type { Date, FootprintList } from '@/basePackage/pages/footprint/type'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({ total: 0, size: 10, current: 1, pages: 1 })
const footprintList = ref<FootprintList[]>([])
const triggered = ref(false)
const calendarRef = ref()
const qNavRef = ref<InstanceType<typeof QNav>>()
const bottomRef = ref()
const checkboxAll = computed(() => {
  const footprint = footprintList.value.flatMap((item) => item.records)
  return footprint.every((item) => item.done)
})
const queryMonth = reactive({ month: 0, footMarkDate: '' })
const isOperation = ref(true)
const isShrinkage = ref(true)
const loadStatus = ref('loading')
const pageH = reactive({
  bH: uni.upx2px(106),
  titleH: uni.upx2px(88),
  calendarH: 0,
  screenHeight: 0,
  statusBarHeight: 0,
})
const { bH, titleH, calendarH, screenHeight, statusBarHeight } = toRefs(pageH)
const customStyle = {
  color: '#fff',
  fontSize: '28rpx',
  width: '158rpx',
  backgroundColor: 'rgba(255, 255, 255, 0.34)',
}

// 底部导航安全距离高度
const safeHeight = useBottomSafe()
// 没有数据禁止请求
const prohibitRequest = ref(false)
// 记录长按
const longPressRecord = ref('0')
const text = { loadmore: '加载更多', loading: '努力加载中', nomore: '到底啦,仅展示近60天的记录哦' }
const emptyConfig = {
  title: '您还没有足迹呢，快去商品详情里面看看吧',
  background: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220531/1ce266a5e3fb43a4ae9b764fd8a166df.png',
}

const remainingHighly = computed(() => {
  let height = 0
  // #ifndef MP-WEIXIN
  height = useScreenHeight().value - calendarH.value - titleH.value - bH.value - statusBarHeight.value - safeHeight.value
  // #endif
  // #ifdef MP-WEIXIN
  height = useScreenHeight().value - calendarH.value - bH.value - statusBarHeight.value + uni.upx2px(15)
  // #endif
  return height
})

initFootprintList_()
// 状态栏高度和屏幕高度
uni.getSystemInfo({
  success: function (res) {
    pageH.screenHeight = res.screenHeight
    if (res.statusBarHeight) {
      // #ifndef MP-WEIXIN
      if (res.platform === 'android') {
        pageH.statusBarHeight = res.statusBarHeight + 50
      } else {
        pageH.statusBarHeight = res.statusBarHeight + 45
      }
      // #endif
      // #ifdef MP-WEIXIN
      let custom = uni.getMenuButtonBoundingClientRect()
      pageH.statusBarHeight = custom.bottom + custom.top - res.statusBarHeight
      // #endif
    }
  },
})

watch(
  () => calendarRef.value?.canlendarPanelH,
  (val) => {
    calendarH.value = val
  },
)

/**
 * 按天查询
 * @param {*} date
 */
const changeDate = async (date: Date) => {
  const params = `${date.year}-${date.month < 10 ? '0' + date.month : date.month}-${date.day < 10 ? '0' + date.day : date.day}`
  queryMonth.footMarkDate = params
  initFootprintList_()
}
/**
 * 按月查足迹
 * @param {*} date
 */
const changeMonth = async (date: Date) => {
  queryMonth.footMarkDate = ''
  queryMonth.month = date.month
  initFootprintList_()
}
/**
 * 下拉刷新被触发，发请求
 */
const refresherrefresh = async () => {
  triggered.value = true
  await initFootprintList_()
  triggered.value = false
}
const refresherrestore = () => {}
const refresherabort = () => {}
/**
 * 滚动到底部/右边，会触发 scrolltolower 事件
 */
const scrolltolower = async () => {
  initFootprintList_(true)
}
/**
 * 点击图片跳转详情
 * @param {Long} goodId
 * @param {Long} shopId
 */
const handleNavToDetail = (goodId: Long, shopId: Long) => {
  if (!isOperation.value) return
  jumpGoods(shopId, goodId)
}
/**
 * 批量管理全部选中
 * @param {*}
 */
const handleCheckboxAll = (e: { value: boolean; name: string }) => {
  let footprint = footprintList.value.flatMap((item) => item.records)
  footprint = footprint.map((item) => {
    item.done = e.value
    return item
  })
}
/**
 * 长按记录
 * @param {*} id
 * @param {*} index
 */
const handleLongtap = (orderId: string) => {
  if (longPressRecord.value === orderId) return (longPressRecord.value = '0')
  longPressRecord.value = orderId
}
/**
 * 点击删除
 * @param {*} id
 */
const handleLongtapDel = async (id: string) => {
  const { code, msg } = await doDelUserFootprint([id])
  if (code === 200) {
    uni.showToast({
      icon: 'none',
      title: '删除成功',
    })
    initFootprintList_()
  } else {
    uni.showToast({
      icon: 'none',
      title: `${msg ? msg : '删除失败'}`,
    })
  }
  longPressRecord.value = '0'
}
/**
 * 删除 | 收藏
 */
const handleDelAndAssess = async (type: '删除' | '收藏') => {
  const footprint = footprintList.value.flatMap((item) => item.records)
  const paramsArr = footprint
    .filter((item) => item.done)
    .map((item) => {
      if (type === '删除') {
        return item.id
      }
      return { productId: item.productId, shopId: item.shopId, supplierId: item.supplierId }
    })
  if (!paramsArr.length) {
    uni.showToast({
      icon: 'none',
      title: '没有可操作的商品',
    })
    isOperation.value = true
    return
  }
  const { code, msg } =
    type === '删除' ? await doDelUserFootprint(paramsArr) : await doAddAssess({ whetherCollect: true, userCollectDTOList: paramsArr })
  if (code === 200) {
    isOperation.value = true
    initFootprintList_()
    uni.showToast({
      icon: 'none',
      title: `${type}成功`,
    })
    if (type === '删除') {
      // 初始化足迹标识
      calendarRef.value.initFootprint()
    }
    return
  }
  uni.showToast({
    icon: 'none',
    title: `${msg ? msg : `${type}失败`}`,
  })
}
/**
 * 点击批量管理
 */
const handleOperation = () => {
  if (!footprintList.value.length)
    return uni.showToast({
      icon: 'none',
      title: '没有可操作的商品',
    })

  isOperation.value = false
}
const handleCompleteInit = () => {
  // initFootprintList()
  isOperation.value = true
}
// test
/**
 * 足迹列表
 */
async function initFootprintList_(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    footprintList.value = await getCommodityList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    footprintList.value = footprintList.value.concat(await getCommodityList())
  } else {
    loadStatus.value = 'nomore'
  }
}
async function getCommodityList() {
  loadStatus.value = 'loading'
  const { code, data, msg } = await doGetUserFootprintList({ ...pageConfig, ...queryMonth })
  if (code !== 200) {
    loadStatus.value = 'nomore'
    uni.showToast({ title: `${msg ? msg : '获取收藏列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.pages = data.pages
  loadStatus.value = 'nomore'
  return useFootprintCollection(data.records)
}
</script>
<template>
  <q-nav ref="qNavRef" title="我的足迹" class="footprintT" />
  <my-calendar ref="calendarRef" @change-month="changeMonth" @change-date="changeDate" @change-shrinkage="isShrinkage = $event" />
  <template v-if="footprintList.length">
    <scroll-view
      scroll-y
      :style="{ height: `${remainingHighly}px` }"
      class="footprint"
      refresher-enabled
      :refresher-threshold="45"
      :refresher-triggered="triggered"
      @refresherrefresh="refresherrefresh"
      @refresherrestore="refresherrestore"
      @refresherabort="refresherabort"
      @scrolltolower="scrolltolower"
    >
      <view class="footprint__refresh">下拉即可刷新</view>
      <view v-for="footprint in footprintList" :key="footprint.date">
        <view class="footprint__date">{{ footprint.date }}</view>
        <view class="footprint__image-box">
          <view v-for="item in footprint.records" :key="item.id" class="footprint__image-main">
            <u-image
              class="footprint__image"
              border-radius="15"
              :width="236"
              :height="236"
              :src="item.productPic"
              @longpress="handleLongtap(item.id)"
              @click="handleNavToDetail(item.productId, item.shopId)"
            >
            </u-image>
            <view v-if="longPressRecord === item.id" class="footprint__image--mask" @longpress="handleLongtap(item.id)">
              <u-button shape="circle" :plain="true" :custom-style="customStyle" @click="handleLongtapDel(item.id)">删除 </u-button>
            </view>
            <text class="footprint__image--price">{{ divTenThousand(item.productPrice) }}</text>
            <u-checkbox v-if="!isOperation" v-model="item.done" size="44" shape="circle" class="footprint__image--checkbox"></u-checkbox>
          </view>
        </view>
      </view>
      <u-loadmore v-if="footprintList.length || loadStatus === 'loading'" :status="loadStatus" :load-text="text" />
    </scroll-view>
  </template>
  <!-- #ifndef MP-WEIXIN -->
  <q-empty
    v-if="prohibitRequest && !footprintList.length"
    style="margin-top: 200rpx"
    :title="emptyConfig.title"
    :background="emptyConfig.background"
  />
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <u-empty v-if="!footprintList.length" text="您还没有足迹呢~" font-size="30" icon-size="550" :src="EMPTY_GB.COLLECTION_EMPTY" margin-top="200" />
  <view style="height: 106rpx"> </view>
  <!-- #endif -->
  <view v-if="isOperation" ref="bottomRef" class="bottom" @click="handleOperation">批量管理</view>
  <view v-else class="operation">
    <u-checkbox v-model="checkboxAll" size="44" shape="circle" class="operation__checkbox" @change="handleCheckboxAll">全选</u-checkbox>
    <view class="operation__btn">
      <u-button shape="circle" plain style="color: red; width: 158rpx" @click="handleCompleteInit">完成</u-button>
      <u-button shape="circle" plain style="color: red; width: 158rpx" @click="handleDelAndAssess('收藏')">移入收藏</u-button>
      <u-button type="error" shape="circle" style="width: 158rpx" @click="handleDelAndAssess('删除')">删除</u-button>
    </view>
  </view>
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}

/* 570 */
@include b(footprint) {
  padding: 0 12rpx 106rpx 12rpx;

  @include e(refresh) {
    height: 100rpx;
    font-size: 22rpx;
    text-align: center;
    color: #b2b2b2;
    line-height: 100rpx;
  }

  @include e(date) {
    height: 34rpx;
    font-size: 24rpx;
    font-weight: bold;
    text-align: LEFT;
    color: #333333;
    line-height: 34rpx;
  }

  @include e(image-box) {
    margin-top: 18rpx;
    @include flex;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  @include e(image-main) {
    position: relative;
    margin-bottom: 20rpx;
    width: 236rpx;
    font-size: 34rpx;
    color: #f83f30;
    margin-right: 10rpx;
    @include utils-ellipsis(1);
  }

  @include e(image) {
    margin-bottom: 18rpx;

    @include m(checkbox) {
      position: absolute;
      top: 10rpx;
      right: -10rpx;
      z-index: 10;
    }

    @include m(mask) {
      @include flex;
      position: absolute;
      top: 0;
      width: 236rpx;
      height: 100%;
      border-radius: 15rpx;
      background: rgba(0, 0, 0, 0.53);
    }

    @include m(price) {
      &::before {
        content: '￥';
        width: 26rpx;
        height: 36rpx;
        color: #f83f30;
        font-size: 14rpx;
      }
    }
  }
}

@include b(bottom) {
  position: fixed;
  bottom: 0;
  width: 750rpx;
  height: 106rpx;
  background: #ffffff;
  font-size: 28rpx;
  font-weight: bold;
  text-align: CENTER;
  color: #333333;
  line-height: 106rpx;
  z-index: 10;
  border-top: 1rpx solid #f5f5f5;
}

@include b(operation) {
  @include flex;
  justify-content: space-between;
  padding: 0 14rpx;
  position: fixed;
  bottom: 0;
  z-index: 10;
  width: 750rpx;
  height: 106rpx;
  background: #ffffff;
  border-top: 1rpx solid #f5f5f5;

  @include e(btn) {
    width: 506rpx;
    @include flex(space-between);
  }

  @include e(checkbox) {
    color: #8f8f8f;
    font-size: 24rpx;
  }
}
</style>
