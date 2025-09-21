<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue'
import QNav from '@/components/q-nav/q-nav.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useStatusBar } from '@/hooks/useStatusBar'
import { doGetDistributeOrder } from '../apis'
import { Decimal } from 'decimal.js-light'
import Auth from '@/components/auth/auth.vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
type OrderInfoType = {
  list: any[]
  current: number
  pages: number
  size: number
  status: string
}

const { divTenThousand } = useConvert()
const tabsList = [
  {
    name: '',
    label: '全部',
  },
  {
    name: 'PAID',
    label: '已付款',
  },
  {
    name: 'COMPLETED',
    label: '已完成',
  },
  {
    name: 'CLOSED',
    label: '已关闭',
  },
]
const orderInfo = ref<OrderInfoType>({
  list: [],
  current: 1,
  pages: 1,
  size: 10,
  status: 'nomore',
})
const statistic = ref({
  invalid: 0,
  total: 0,
  unsettled: 0,
})
const currentTabIndex = ref(0)
const keywords = ref('')
const scrollHeight = computed(() => {
  let navHeight = 44
  // #ifdef H5
  navHeight = 44 + useStatusBar().value
  // #endif
  return `${useScreenHeight().value - useBottomSafe().value - navHeight - uni.upx2px(207) - uni.upx2px(122) - uni.upx2px(96)}px`
})
const showtip = ref(false)

initOrderList()

const handleChangeTabs = (idx: number) => {
  currentTabIndex.value = idx
  orderInfo.value.current = 1
  orderInfo.value.status = 'nomore'
  initOrderList()
}
const handleScrollBottom = () => {
  initOrderList(true)
}
async function initOrderList(isLoad = false) {
  orderInfo.value.status = 'loading'
  if (!isLoad) {
    // 刷新
    orderInfo.value.current = 1
    orderInfo.value.list = await reqCustomer()
  } else if (isLoad && orderInfo.value.current < orderInfo.value.pages) {
    // 更新
    orderInfo.value.current++
    orderInfo.value.list = orderInfo.value.list.concat(await reqCustomer())
  } else {
    orderInfo.value.status = 'nomore'
  }
}
async function reqCustomer() {
  const { current, size } = orderInfo.value
  const { code, data, msg } = await doGetDistributeOrder({
    current,
    size,
    status: tabsList[currentTabIndex.value].name,
    keywords: keywords.value,
  })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取分销订单失败'}`, icon: 'none' })
    orderInfo.value.status = 'loadmore'
    return []
  }
  orderInfo.value.status = 'loadmore'
  orderInfo.value.pages = +data.page.pages
  if (!keywords.value && currentTabIndex.value === 0) {
    statistic.value = data.statistic
  }
  return data.page.records
}
function convertStatus(status: string) {
  return status === 'COMPLETED' ? '已赚' : status === 'CLOSED' ? '已失效' : '待结算'
}
function convertOrderStatus(orderinfo: any) {
  if (
    orderinfo.items.some((item: any) => {
      return item.orderStatus === 'COMPLETED'
    })
  ) {
    return '已完成'
  } else if (
    orderinfo.items.some((item: any) => {
      return item.orderStatus === 'PAID'
    })
  ) {
    return '已付款'
  }
  if (
    orderinfo.items.some((item: any) => {
      return item.orderStatus === 'UNPAID'
    })
  ) {
    return '待支付'
  } else return '已关闭'
}
function convertLevel(level: string) {
  return level === 'ONE' ? '一级分销客户' : level === 'TWO' ? '二级分销客户' : '三级分销客户'
}
function handleSearch() {
  orderInfo.value.current = 1
  orderInfo.value.status = 'nomore'
  initOrderList()
}
function getcount(orderitem: any, level: string, orderStatus?: string) {
  let count = new Decimal(0)
  if ('items' in orderitem && orderitem.items.length) {
    orderitem.items.forEach((item: any) => {
      if (orderStatus && item.orderStatus !== orderStatus) return
      if (item[level.toLowerCase()].bonus) {
        count = count.add(divTenThousand(item[level.toLowerCase()].bonus))
      }
    })
  }

  return count.toNumber()
}
function copy(orderNo: string) {
  uni.setClipboardData({
    data: orderNo,
    success: function () {
      console.log('success')
    },
  })
}

const loadMore = () => {
  handleScrollBottom()
}
</script>

<template>
  <q-nav title="分销订单" color="#222" />
  <view class="count">
    <view class="count__accumulate">
      <view class="count__accumulate--num">{{ divTenThousand(statistic.total) }}</view>
      <view class="count__accumulate--word">累计佣金</view>
    </view>
    <view class="count__accumulate">
      <view class="count__accumulate--num">{{ divTenThousand(statistic.unsettled) }}</view>
      <view class="count__accumulate--word">待结算佣金</view>
    </view>
    <view class="count__accumulatey">
      <view class="count__accumulate--num">{{ divTenThousand(statistic.invalid) }}</view>
      <view class="count__accumulate--word">已失效佣金</view>
    </view>
  </view>
  <view class="search">
    <u-search
      v-model="keywords"
      shape="round"
      :show-action="true"
      placeholder="搜索产品名称"
      class="searchSearch"
      action-text="搜索"
      bg-color="#F2F2F2"
      height="71"
      color="rgb(153, 153, 153)"
      placeholder-color="rgb(153, 153, 153)"
      @custom="handleSearch"
    />
    <span
      ><q-icon name="icon-wenhao" size="40rpx" color="#999" @click="showtip = true" />
      <!-- <u-icon name="icon-wenhao" size="40" color="#999" @click="showtip = true"/> -->
    </span>
  </view>
  <!-- <view h5-nav-height="0"> -->
  <!-- <view> -->
  <view class="orderTabs">
    <view
      v-for="(item, index) in tabsList"
      :key="item.label"
      :class="['orderTabs__item', index === currentTabIndex ? 'choosed' : '']"
      @click="handleChangeTabs(index)"
      >{{ item.label }}</view
    >
  </view>
  <!-- </view> -->
  <scroll-view scroll-y :style="{ height: scrollHeight }" class="order" @scrolltolower="loadMore">
    <view v-for="item in orderInfo.list" :key="item.id" class="order-item" style="border-radius: 20rpx; overflow: hidden">
      <view class="order-item__no">
        <view>订单号：{{ item.orderNo }} <span style="color: #0075ff; margin-left: 10rpx" @click="copy(item.orderNo)">复制</span></view>
        <view v-if="convertOrderStatus(item) === '已付款'" class="afa3534">{{ convertOrderStatus(item) }}</view>
        <view v-else-if="convertOrderStatus(item) === '已完成'" class="a00cf78">{{ convertOrderStatus(item) }}</view>
        <view v-else class="a999999">{{ convertOrderStatus(item) }}</view>
      </view>
      <view v-for="(itm, inx) in item.items" :key="inx" class="order-item__com">
        <view class="order-item__com--img">
          <image style="width: 100%; height: 100%" :src="itm.image" mode="aspectFit" />
        </view>
        <view class="order-item__com--right">
          <view class="order-item__com--tile">{{ itm.productName }}</view>
          <view>
            <view class="order-item__com--info">
              <view><span style="font-size: 22rpx">￥</span>{{ divTenThousand(itm.dealPrice) }}</view>
              <view class="order-item__com--count">{{ itm.num }}</view>
            </view>
            <view class="order-item__com--price">
              <view
                >预计赚<span style="color: #fa3534; font-size: 22rpx">￥</span
                ><span style="font-size: 28rpx; color: #fa3534; font-weight: normal">
                  {{ itm.purchase ? divTenThousand(itm.one.bonus) : divTenThousand(itm[item.level.toLowerCase()].bonus) }}</span
                ></view
              >
              <view v-if="convertOrderStatus(item) !== '已关闭'" style="color: #fa3534; font-weight: bold">{{ convertStatus(itm.orderStatus) }}</view>
              <view v-else style="color: #999; font-weight: bold">{{ convertStatus(itm.orderStatus) }}</view>
            </view>
          </view>
        </view>
      </view>
      <view class="order-item__info">
        <view class="order-item__info-left">
          <image class="order-item__info-left--img" :src="item.buyerAvatar" mode="aspectFit" />
        </view>
        <view class="order-item__info-cen">
          <view style="margin-left: 31rpx; font-weight: bold; font-size: 32rpx; line-height: 48rpx">{{ item.buyerName }}</view>
          <view style="margin-left: 27rpx; ont-size: 28rpx; line-height: 48rpx">
            {{ item.purchase ? '内购订单' : convertLevel(item.level) }}
          </view>
        </view>

        <view class="order-item__info-right">
          <view style="line-height: 48rpx"
            >佣金合计：<span style="color: #fa3534; font-weight: bold"
              >￥{{ getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level) }}</span
            ></view
          >
          <view v-if="getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'PAID')" style="line-height: 48rpx"
            >待结算：<span style="color: #fa3534; font-weight: bold"
              >￥<span class="colorFe">{{ getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'PAID') }}</span></span
            >
          </view>
          <view v-if="getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'COMPLETED')" style="line-height: 48rpx"
            >已赚：<span style="color: #fa3534; font-weight: bold"
              >￥<span class="colorFe">{{ getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'COMPLETED') }}</span></span
            >
          </view>
          <view v-if="getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'CLOSED')" style="line-height: 48rpx"
            >已失效：<span style="color: #fa3534; font-weight: bold"
              >￥{{ getcount(item, item.currentUserLevel ? item.currentUserLevel : item.level, 'CLOSED') }}</span
            ></view
          >
        </view>
      </view>
    </view>
  </scroll-view>
  <u-popup v-model="showtip" mode="bottom" border-radius="14" width="600rpx">
    <view style="padding: 20rpx">
      <view style="text-align: center; font-weight: bold; font-size: 32rpx; margin: 30rpx 0">说明</view>
      <view style="margin-bottom: 30rpx"
        >分销订单统计您的团队在购买【分销商品】时产生的订单（即该订单对应的只能是分销商品），以商品为统计粒度统计佣金结算数据。</view
      >
      <view>订单状态</view>
      <view> 1、已付款：是指支付成功，订单处于物流配送中</view>
      <view> 2、已完成：是指商品客户已确认收货未退款</view>
      <view style="margin-bottom: 30rpx"> 3、已关闭：是指订单内商品【退款成功】 </view>
      <view>结算状态</view>
      <view>1、待结算：是指订单未完结(可能退款退货等情况)暂不计入累计佣金；</view>
      <view>2、已赚：是指对应商品没有退款(或退款失败)，对应佣金统计到【累计佣金】中</view>
      <view style="margin-bottom: 30rpx">3、已失效：指未付款订单+已付款且退款成功订单总和，您无法获得对应佣金 </view>
      <view>佣金统计维度 </view>
      <view>1、累计佣金：订单状态为【已完成】的【已赚】佣金之和</view>
      <view>2、待结算佣金：结算状态为【待结算】的佣金之和</view>
      <view style="margin-bottom: 30rpx">3、已失效佣金：商品退款成功，【已失效】的佣金之和</view>
    </view>
  </u-popup>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(count) {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  width: 750rpx;
  height: 207rpx;
  background-color: #fd5e37;
  color: #fff;
  @include e(accumulate) {
    @include m(word) {
      font-weight: bold;
      margin-top: 10rpx;
      text-align: center;
    }
    @include m(num) {
      text-align: center;
      font-size: 53rpx;
      font-weight: bolder;
    }
  }
}
@include b(search) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 122rpx;
  background: #fff;
  padding: 30rpx 16rpx 21rpx 28rpx;
}
@include b(orderTabs) {
  transform: translateY(-11px);
  height: 96rpx;
  background: #fff;
  width: 100%;
  font-size: 28rpx;
  font-weight: 600;
  @include flex(space-between);
  @include e(item) {
    width: 25%;
    line-height: 96rpx;
    text-align: center;
    position: relative;
  }
}
@include b(choosed) {
  width: 25%;
  color: #fa3534;
}
@include b(order) {
  box-sizing: border-box;
  padding: 0 20rpx 20rpx 20rpx;
}
@include b(order-item) {
  background: #fff;
  margin-bottom: 20rpx;
  font-size: 26rpx;
  @include e(no) {
    @include flex(space-between);
    width: 680rpx;
    margin: 0 auto;
    height: 86rpx;
    padding: 0 20rpx;
    border-bottom: 1px dashed rgb(222, 222, 222);
  }
  @include e(info) {
    padding: 46rpx 30rpx 20rpx 52rpx;
    // @include flex(space-between);
    display: flex;
  }
  @include e(info-left) {
    @include m(img) {
      width: 99rpx;
      height: 99rpx;
      margin-bottom: 30rpx;
    }
  }
  @include e(info-cen) {
    flex: 1;
  }
  @include e(info-right) {
    text-align: right;
    // @include flex;
    // @include m(icon) {
    //     margin-left: 4rpx;
    //     transform: rotate(-90deg);
    // }
  }
  @include e(com) {
    @include flex(space-between);
    padding: 20rpx 20rpx 20rpx 15rpx;
    border-bottom: 1px dashed rgb(222, 222, 222);
    width: 680rpx;
    margin: 0 auto;
    @include m(img) {
      width: 132rpx;
      height: 132rpx;
      margin-right: 20rpx;
      border-radius: 12rpx;
      overflow: hidden;
    }
    @include m(tile) {
      width: 412rpx;
      color: #333333;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-weight: bold;
    }
    @include m(right) {
      font-weight: bold;
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      height: 132rpx;
    }
    @include m(info) {
      @include flex(space-between);
      font-size: 28rpx;
      color: #333333;
    }
    @include m(price) {
      @include flex(space-between);
      margin-top: 10rpx;
      font-size: 24rpx;
    }
    @include m(count) {
      color: #999999;
      font-size: 24rpx;
      &::before {
        content: '×';
        display: inline-block;
        margin-right: 2rpx;
        vertical-align: bottom;
      }
    }
  }
}
.afa3534 {
  color: #fa3534;
}
.a00cf78 {
  color: #00cf78;
}
.a999999 {
  color: #999999;
}
.searchSearch {
  width: 100%;
  margin-right: 10rpx;
}
</style>
