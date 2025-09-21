<script setup lang="ts">
// #ifdef H5
import AMap from '@/pluginPackage/order/orderDetail/components/AMap/index.vue'
// #endif
import { onLoad } from '@dcloudio/uni-app'
import { doGetICOrder, doGetUuptCourierInfo } from '@/apis/order'
import { reactive, ref, nextTick, onBeforeUnmount, computed, onUnmounted } from 'vue'
import type { CourierLoctionInfo, IcOrderDetail, IcOrderTimes } from '@/apis/order/types'
import { GeometryType } from '@/apis/address/type'
import { computedTime } from '@/pluginPackage/order/orderDetail/utils'
import QTimeLine from '@/pluginPackage/order/orderDetail/components/QTimeLine/index.vue'
import QTimeLineItem from '@/pluginPackage/order/orderDetail/components/QTimeLineItem/index.vue'

// 客户地址图标
let myLocationIcon = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564dcae4b0dd23f6b86f2e.png'
// #ifndef APP-PLUS
myLocationIcon = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564ed2e4b0dd23f6b86f30.png'
// #endif

// 骑手图标
let courierIcon = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564d20e4b0dd23f6b86f2d.png'
// #ifndef APP-PLUS
courierIcon = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/10/28671f2e6b72ade2c8504fc20a.png'
// #endif

onLoad((res) => {
  OrderParam.orderNo = res?.orderNo || ''
  OrderParam.shopId = res?.shopId || ''
  initRiderPosition()
})

const timerId = ref()
/**
 * 轮询 每隔15秒 获取一次 UU 跑腿配送员信息
 */
const deepGetUuptCourierInfo = () => {
  timerId.value = setInterval(async () => {
    await getOrderDetail()
    if (showMap.value) {
      await getUuptCourierInfo()
      // 重新绘制地图marks
      // #ifdef MP-WEIXIN || APP-PLUS
      // 微信小程序使用map组件，重新绘制地图marks 设置缩放级别
      setMapZoom()
      // #endif
      // #ifdef H5
      // 非小程序使用高德自带包 小程序使用map组件以减小小程序包体积
      createMarker()
      // #endif
      if (timerId.value) {
        clearInterval(timerId.value)
        timerId.value = null
        deepGetUuptCourierInfo()
      }
    }
  }, 15000)
}

/**
 * 初始化/刷新 骑手位置
 */
const initRiderPosition = async () => {
  if (timerId.value) {
    clearInterval(timerId.value)
    timerId.value = null
  }
  const detail = icOrderDetail.value
  //如果订单已送达 则不刷新
  if (detail?.orders?.[0]?.status === 'DELIVERED') {
    return
  }
  await getOrderDetail()
  if (showMap.value) {
    await getUuptCourierInfo()
    // #ifdef MP-WEIXIN || APP-PLUS
    setMapZoom()
    // #endif
    // #ifdef H5
    createMarker()
    // #endif
    // 开始轮询查询UU 跑腿配送员信息
    deepGetUuptCourierInfo()
  }
}

// 偏差的经纬度量
const correctDistance = ref(0.005)
// #ifdef MP-WEIXIN || APP-PLUS
const setMapZoom = async () => {
  const MapContext = uni.createMapContext('courier_map')
  await nextTick()
  // 设置缩放级别 包含的点: 客户地址和配送员位置 为了范围稍微大一点，所以两个点的上下左右需要加上一定距离
  if (courierLoction.value?.location) {
    MapContext.includePoints({
      points: [
        {
          latitude: courierLoction.value?.location?.coordinates[1] + correctDistance.value,
          longitude: courierLoction.value?.location?.coordinates[0] + correctDistance.value,
        },
        {
          latitude: courierLoction.value?.location?.coordinates[1] - correctDistance.value,
          longitude: courierLoction.value?.location?.coordinates[0] - correctDistance.value,
        },
        {
          latitude: courierLoction.value?.location?.coordinates[1] + correctDistance.value,
          longitude: courierLoction.value?.location?.coordinates[0] - correctDistance.value,
        },
        {
          latitude: courierLoction.value?.location?.coordinates[1] - correctDistance.value,
          longitude: courierLoction.value?.location?.coordinates[0] + correctDistance.value,
        },
        {
          latitude: icOrderDetail.value.receiverLocation.coordinates[1] + correctDistance.value,
          longitude: icOrderDetail.value.receiverLocation.coordinates[0] + correctDistance.value,
        },
        {
          latitude: icOrderDetail.value.receiverLocation.coordinates[1] - correctDistance.value,
          longitude: icOrderDetail.value.receiverLocation.coordinates[0] - correctDistance.value,
        },
        {
          latitude: icOrderDetail.value.receiverLocation.coordinates[1] + correctDistance.value,
          longitude: icOrderDetail.value.receiverLocation.coordinates[0] - correctDistance.value,
        },
        {
          latitude: icOrderDetail.value.receiverLocation.coordinates[1] - correctDistance.value,
          longitude: icOrderDetail.value.receiverLocation.coordinates[0] + correctDistance.value,
        },
      ],
    })
  }
}
// #endif

/**
 * 卸载时清除定时器
 */
onBeforeUnmount(() => {
  if (timerId.value) {
    clearInterval(timerId.value)
  }
})

/**
 * 发请求获取 UU 跑腿配送员信息
 */
const getUuptCourierInfo = async () => {
  const { data, code, msg } = await doGetUuptCourierInfo(OrderParam)
  console.log('uu跑腿配送员信息', data)
  if (data && code === 200) {
    courierLoction.value = data
    return data
  } else {
    Promise.reject(msg)
  }
}
/**
 * 获取同城配送订单详情
 */
const getOrderDetail = async () => {
  const { data, code, msg } = await doGetICOrder(OrderParam)
  if (data && code === 200) {
    icOrderDetail.value = data
  } else {
    uni.showToast({ title: msg || '获取订单配送详情失败', icon: 'none' })
  }
  return data
}
/**
 * 页面参数: 订单id 店铺id
 */
const OrderParam = reactive({
  orderNo: '',
  shopId: '',
})

/**
 * 订单详情
 */
const icOrderDetail = ref<IcOrderDetail>({
  // 收货点位置
  receiverLocation: {
    coordinates: [0, 0],
    type: GeometryType.Point,
  },
  orders: [],
})

type TimeBy = {
  by: keyof IcOrderTimes
  title: string
  content: string
  isCurrent?: boolean // 是否进行到当前这一步
  isLast?: boolean // 是否是最后一步
}
/**
 * 各个时间节点
 */
const timeByList: TimeBy[] = [
  { by: 'errorTime', title: '配送异常', content: '', isCurrent: false },
  { by: 'deliveredTime', title: '已送达', content: '商品已送达', isCurrent: false },
  { by: 'pickupTime', title: '配送中', content: '骑手正在送货', isCurrent: false },
  { by: 'arrivalShopTime', title: '待取货', content: '骑手正在店内取货', isCurrent: false },
  { by: 'takeOrderTime', title: '待到店', content: '骑手正赶往商家', isCurrent: false },
  { by: 'shippingTime', title: '待接单', content: '等待骑手接单', isCurrent: false },
]
const times = computed(() => {
  if (icOrderDetail.value.orders.length === 0) {
    return []
  } else {
    const orderTimes = icOrderDetail.value.orders[0].times
    let arr = timeByList
      .filter((item) => !!orderTimes[item.by])
      .map((item) => {
        return {
          ...item,
          content: item.by === 'errorTime' ? icOrderDetail.value.orders[0].statusDesc : item.content,
          time: orderTimes[item.by]!,
        }
      })
      .sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime()) // 按照时间排序
    arr[arr.length - 1].isLast = true
    arr[0].isCurrent = true
    return arr
  }
})

/**
 * 映射骑手状态
 */
const courierStatusMap = computed(() => {
  // times 中第一个 isCurrent 为 true 的元素(即第一个元素)
  return times.value[0].content
})

/**
 * 记录骑手位置
 */
const courierLoction = ref<CourierLoctionInfo>({
  courier: {
    name: '',
    mobile: '',
    avatar: '',
  },
  location: {
    coordinates: [0, 0],
    type: GeometryType.Point,
  },
  distance: 0,
  expectTime: '',
  minutes: 0,
})
const time = ref()
onUnmounted(() => {
  if (timerId.value) {
    clearTimeout(timerId.value)
    timerId.value = null
  }
  if (time.value) {
    clearInterval(time.value)
    time.value = null
  }
})

/**
 * 地图点标记(收货点标记,配送员实时点标记)
 */
const markers = computed(() => {
  const markerList: any[] = [
    {
      id: 0,
      latitude: icOrderDetail.value.receiverLocation.coordinates[1],
      longitude: icOrderDetail.value.receiverLocation.coordinates[0],
      iconPath: myLocationIcon,
      width: 30,
      height: 30,
    },
  ]
  if (courierLoction.value.location && courierLoction.value.courier.name !== '') {
    markerList[1] = {
      id: 1,
      latitude: courierLoction.value.location.coordinates[1],
      longitude: courierLoction.value.location.coordinates[0],
      iconPath: courierIcon,
      width: 50,
      height: 50,
      customCallout: {
        display: 'ALWAYS',
      },
      // #ifdef APP-PLUS
      label: {
        content: `
        ${courierStatusMap.value}
        距${times.value[0].by === 'takeOrderTime' ? '商家' : '你'}${courierLoction.value.distance}m，
        预计${courierLoction.value.minutes}分钟后${times.value[0].by === 'takeOrderTime' ? '到达商家' : '送达'}`,
      },
      // #endif
    }
  }
  return markerList
})

// #ifdef H5
// H5使用高德地图
const AMapRef = ref()
/**
 * h5 自定义骑手位置点标记
 */
const courierContent = computed(() => {
  return `
<div class="img_container">
    <img src="${courierIcon}" style="width: 50px;height: 51px;"/>
    <div class="marker-box">
        <div style="color: #000;font-size: 16px;font-weight: bold;margin-bottom:2px">${courierStatusMap.value}</div>
        <div style="font-size: 14px;color: #666666">距${times.value[0].by === 'takeOrderTime' ? '商家' : '你'}
        <span style="color: #555CFD">${courierLoction.value.distance}m</span>，预计
        <span style="color: #555CFD">${courierLoction.value.minutes}分钟</span> 后${times.value[0].by === 'takeOrderTime' ? '到达商家' : '送达'}</div>
    </div>
</div>`
})
/**
 * h5 自定义收货地点标记
 */
const mineContent = ref(`
<img src="${myLocationIcon}" style="width: 30px;height: 30px;"/>
`)

/**
 * h5 清除点标记并创建点标记
 */
const createMarker = async () => {
  // 清除点标记
  AMapRef.value?.clearMarker()
  // 创建收货点标记
  AMapRef.value?.createMarker(icOrderDetail.value.receiverLocation.coordinates, mineContent.value, [-15, -15])
  // 创建骑手实时位置点标记
  if (courierLoction.value.location && courierLoction.value.courier.name !== '') {
    AMapRef.value?.createMarker(courierLoction.value.location.coordinates, courierContent.value, [-25, -51])
    await nextTick()
    let flag = 0
    try {
      time.value = setInterval(async () => {
        if (++flag >= 30 || AMapRef.value?.map) {
          //  3秒后 取消轮询调用
          if (time.value) {
            clearInterval(time.value)
          }
        }
        AMapRef.value?.setMapCenterAndZoom([icOrderDetail.value.receiverLocation.coordinates, courierLoction.value?.location?.coordinates])
      }, 100)
    } catch (error) {
      if (time.value) {
        clearInterval(time.value)
      }
    }
  }
}
// #endif

/**
 * 拨打骑手/商家电话
 */
const callPhone = () => {
  if (icOrderDetail.value.orders[0]?.courier?.mobile || courierLoction.value?.courier?.mobile) {
    uni.makePhoneCall({
      phoneNumber: icOrderDetail.value.orders[0]?.courier?.mobile || courierLoction.value?.courier?.mobile,
    })
  }
}

/**
 * 显示地图的条件
 */
const showMap = computed(() => {
  // 为UUPT订单 且 订单状态为已接单=>已送达之间的状态(TAKEN,ARRIVED_SHOP,PICKUP)
  return (
    courierLoction.value.location &&
    icOrderDetail.value.orders[0]?.type === 'UUPT' &&
    ['TAKEN', 'ARRIVED_SHOP', 'PICKUP'].includes(icOrderDetail.value.orders[0]?.status)
  )
})
</script>

<template>
  <view class="delivery_container">
    <!-- #ifdef H5 -->
    <AMap
      v-if="showMap && courierLoction.courier.name"
      ref="AMapRef"
      :coordinates="courierLoction.location!.coordinates"
      :correctDistance="correctDistance"
      width="100%"
      height="1000rpx"
      class="courier_map"
    >
      <view v-if="showMap" class="refresh_btn">
        <image
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/11/56729721972ade2c8504fc21d.png"
          class="refresh_btn_icon"
          @click="initRiderPosition"
        ></image>
      </view>
    </AMap>
    <!-- #endif -->

    <!-- #ifdef MP-WEIXIN || APP-PLUS-->
    <map
      v-if="showMap && courierLoction.courier.name"
      id="courier_map"
      :markers="markers"
      :coordinates="courierLoction.location!.coordinates"
      style="width: 100%; height: 1000rpx"
      :latitude="courierLoction.location!.coordinates[1]"
      :longitude="courierLoction.location!.coordinates[0]"
      class="courier_map"
    >
      <block slot="callout">
        <cover-view class="custom_callout" :marker-id="1">
          <cover-view class="custom_callout_title">{{ courierStatusMap }}</cover-view>
          <cover-view class="custom_callout_content">
            <cover-view>距{{ times[0].by === 'takeOrderTime' ? '商家' : '你' }}</cover-view>
            <cover-view class="custom_callout_blue">{{ courierLoction.distance }}m</cover-view>
            <cover-view>，预计</cover-view>
            <cover-view class="custom_callout_blue">{{ courierLoction.minutes }}分钟</cover-view>
            <cover-view>后{{ times[0].by === 'takeOrderTime' ? '到达商家' : '送达' }}</cover-view>
          </cover-view>
        </cover-view>
      </block>
      <cover-view v-if="showMap" class="refresh_btn">
        <cover-image
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/11/56729721972ade2c8504fc21d.png"
          class="refresh_btn_icon"
          @click="initRiderPosition"
        ></cover-image>
      </cover-view>
    </map>
    <!-- #endif -->

    <view v-if="icOrderDetail.orders[0]" class="delivery_info">
      <view class="delivery_info_base">
        <view class="delivery_info_base_container">
          <view class="left">
            配送员：{{ icOrderDetail.orders[0].type === 'SELF' ? icOrderDetail.orders[0]?.courier?.name : courierLoction?.courier?.name }}
            <u-icon
              v-if="icOrderDetail.orders[0]?.courier?.mobile || courierLoction?.courier?.mobile"
              name="phone-fill"
              color="#F54319"
              :size="40"
              @click="callPhone"
            ></u-icon>
          </view>
          <view class="right">取货号：{{ icOrderDetail.orders[0].pickupCode }}</view>
        </view>
        <view class="delivery_info_base_container">
          <view class="left">配送方：{{ icOrderDetail.orders[0].type === 'SELF' ? '商家自配' : 'UU跑腿' }}</view>
          <view class="right">配送时长：{{ computedTime(icOrderDetail.orders[0].deliverSeconds || 0) }}</view>
        </view>
      </view>
      <QTimeLine>
        <QTimeLineItem v-for="it in times" :key="it.time" :data="it" curColor="#fe550e"> </QTimeLineItem>
      </QTimeLine>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.delivery_container {
  .delivery_info {
    position: relative;
    padding-left: 40rpx;
    padding-right: 40rpx;
    padding-top: 44rpx;
    padding-bottom: 80rpx;
    background-color: #fff;
    white-space: unset;
    .delivery_info_base {
      margin-bottom: 50rpx;
      .delivery_info_base_container {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20rpx;
        color: rgb(51, 51, 51);
        font-size: 28rpx;
        font-weight: 500;
      }
    }
  }
  :deep(.img_container) {
    position: relative;
    .marker-box {
      position: absolute;
      bottom: 100%;
      left: 50%;
      transform: translateX(-50%);
      padding: 11px 10px;
      border-radius: 6px;
      box-shadow: 0px 2px 5px 0px rgba(0, 0, 0, 0.08);
      background: rgb(255, 255, 255);
      // 文字不换行
      white-space: nowrap;
    }
    &::after {
      content: '';
      position: absolute;
      bottom: 50px;
      left: 50%;
      transform: translateX(-50%);
      // 下箭头三角
      width: 0;
      height: 0;
      border-left: 10px solid transparent;
      border-right: 10px solid transparent;
      border-top: 10px solid #fff;
    }
  }
  :deep(.custom_callout) {
    display: flex;
    flex-direction: column;
    padding: 11px 10px;
    border-radius: 6px;
    box-shadow: 0px 2px 5px 0px rgba(0, 0, 0, 0.08);
    background: rgb(255, 255, 255);
    .custom_callout_title {
      color: #000;
      font-size: 32rpx;
      font-weight: bold;
      margin-bottom: 2px;
    }
    .custom_callout_content {
      font-size: 28rpx;
      color: #666666;
      display: flex;
      align-items: center;
      flex-wrap: nowrap;
      .custom_callout_blue {
        color: #555cfd;
        width: 100rpx;
      }
    }
  }
}
.refresh_btn {
  position: absolute;
  right: 0;
  bottom: 60rpx;
  width: 60rpx;
  height: 60rpx;
  .refresh_btn_icon {
    width: 100%;
    height: 100%;
    z-index: 9999999999999999;
  }
  .refresh_btn_icon:active {
    animation: rotate 1s linear reverse;
  }
  @keyframes rotate {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(520deg);
    }
  }
}
.courier_map {
  position: relative;
}
</style>
