<script setup lang="ts">
import { ref, type PropType, watch, computed } from 'vue'
import { doGetDeliveryTime } from '@pluginPackage/shopStore/apis'
import type { DoPostStoreDistanceListResponseQuery, DoGetDeliveryTimeResponseQuery } from '@/apis/plugin/shopStore/model'
import { storeToRefs } from 'pinia'
import { useLocationStore } from '@/store/modules/location'

const { getterLocation, getterAddress, getterArea } = storeToRefs(useLocationStore())

const $props = defineProps({
  pointList: {
    type: Array as PropType<DoPostStoreDistanceListResponseQuery[]>,
    default: () => [],
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
  isO2o: {
    type: Boolean,
    default: false,
  },
  msg: { type: String, default: '选择门店' },
})
const emit = defineEmits(['getMapLocation', 'getPoint', 'getDate', 'mapChange'])
const showStore = ref(false)
const showTime = ref(false)
const pointId = ref('')
const point = ref<DoPostStoreDistanceListResponseQuery>()
const deliveryTime = ref<DoGetDeliveryTimeResponseQuery>()
const titleMsg = ref($props.msg)
// 提货时间
const pointTime = ref('')
// 日期
const pointDate = computed(() => {
  if (!deliveryTime.value) return []
  let dates = []
  let currentDate = new Date()
  currentDate.setUTCDate(currentDate.getUTCDate() + deliveryTime.value.startDeliveryDay)
  let index = 0
  while (index < deliveryTime.value.endDeliveryDay - deliveryTime.value.startDeliveryDay + 1) {
    dates.push(currentDate.toISOString().slice(0, 10))
    currentDate.setUTCDate(currentDate.getUTCDate() + 1)
    index++
  }
  return dates
})

const pointTimeArr = computed(() => {
  if (deliveryTime.value?.businessStartTime) {
    const timeArr = []
    const { businessStartTime, businessEndTime } = deliveryTime.value
    const e = businessEndTime.substring(2)
    const s = businessStartTime.substring(2)
    const start = Number(businessStartTime.substring(0, 2))
    const end = Number(businessEndTime.substring(0, 2))
    const between = end + 1 - start
    for (let index = 0; index < between; index++) {
      const startTime = start + index
      const endTime = start + index
      const newStartTime = startTime >= 10 ? startTime : '0' + startTime
      const newEndTime = endTime >= 10 ? endTime : '0' + endTime
      timeArr.push({ time: `${newStartTime}${s}-${newEndTime}${e}`, type: false, endTime: endTime })
    }
    return timeArr
  }
  return []
})
const dArrActiveIndex = ref(0)
const timeArrActiveIndex = ref(0)
const timeScrollTop = ref(0)
const show = ref(false)

watch(
  () => $props.pointList,
  (val) => {
    if (val.length) {
      // pointId.value = val[0].id
      // point.value = val[0]
    }
  },
)
watch(
  () => point.value?.id,
  () => {
    getDeliveryTime()
  },
)

/**
 * 获取提货时间
 */
async function getDeliveryTime() {
  const { code, data } = await doGetDeliveryTime($props.shopId, { id: pointId.value })
  if (code !== 200) {
    uni.showToast({ title: '获取提货时间失败', icon: 'none' })
    return
  }
  deliveryTime.value = data
}
/**
 * 确定选中提货点
 */
const handlePointClick = () => {
  point.value = $props.pointList.find((item) => item.id === pointId.value)
  if (point.value) {
    emit('getPoint', point.value)
  }
  dArrActiveIndex.value = 0
  timeArrActiveIndex.value = 0
  pointTime.value = ''
  emit('getDate', '')
  showStore.value = false
}
const handleTimeClick = (idx: number, type: boolean) => {
  if (!type) {
    pointTimeArr.value.forEach((item, index) => {
      if (index !== idx) {
        item.type = false
      }
    })
    timeArrActiveIndex.value = idx
    const mm_dd = pointDate.value[dArrActiveIndex.value]
    const time = pointTimeArr.value[timeArrActiveIndex.value].time
    const params = mm_dd + ' ' + time
    pointTime.value = params
    emit('getDate', params)
    showTime.value = false
  } else {
    pointTime.value = ''
    showTime.value = false
  }
}
const handleDateClick = (idx: number) => {
  dArrActiveIndex.value = idx
  timeScrollTop.value = timeScrollTop.value ? 0 : 1
  pointTimeArr.value.forEach((item) => {
    item.type = false
  })
  pointTime.value = ''
}
/**
 * 暂无门店处理
 */
function temporarilyUnableShopStorePoint() {
  show.value = true
  titleMsg.value = '店铺暂无门店，请联系商家处理'
}
const handleChoosePoint = () => {
  // if ($props.isO2o) return
  if (titleMsg.value === '店铺暂无门店，请联系商家处理') return (show.value = true)
  showStore.value = true
}
const handleChooseLocation = () => {
  showStore.value = false
  // #ifndef H5
  uni.chooseLocation({
    latitude: getterLocation.value?.coordinates[1],
    longitude: getterLocation.value?.coordinates[0],
    success: (res) => {
      if (!res.longitude) return
      emit('mapChange', res)
    },
    fail: (err) => {
      console.log('err', err)
    },
  })
  // #endif
  // #ifdef H5
  uni.$emit('showChooseMap', true)
  // #endif
}

defineExpose({ temporarilyUnableShopStorePoint, showStore })
</script>

<template>
  <view class="storeinfo">
    <view class="storeinfo__data">
      <view class="storeinfo__data--tips">自提点</view>
      <view v-if="!point?.id" class="storeinfo__data--notselected" @click="handleChoosePoint">
        {{ titleMsg }}<u-icon v-if="!isO2o" name="arrow-right" color="#969799"></u-icon>
      </view>
      <view v-else class="storeinfo__data--selected" @click="showStore = true">
        <view class="storeinfo__data--storename">{{ point?.storeName }}</view>
        <view class="storeinfo__data--phone">{{ point?.functionaryPhone }}</view>
      </view>
    </view>
    <view v-if="point?.id" class="storeinfo__address" @click="showStore = true">
      <view>{{ point?.detailedAddress }}</view>
      <u-icon name="arrow-right" color="#969799"></u-icon>
    </view>
    <view class="storeinfo__cell" style="border-top: 1px solid #ededed">
      <template v-if="point?.id">
        <view class="storeinfo__cell--title">提货时间</view>
        <view class="storeinfo__cell--content storeinfo__cell--time" @click="showTime = true">
          {{ pointTime || '选择提货时间' }}<u-icon name="arrow-right" color="#969799"> </u-icon>
        </view>
      </template>
    </view>
    <view class="storeinfo__cell">
      <view class="storeinfo__cell--title">配送方式</view>
      <view class="storeinfo__cell--content">到店自提</view>
    </view>
  </view>
  <u-popup v-model="showStore" mode="bottom" border-radius="14">
    <view class="storepopup__title">选择自提点</view>
    <view class="storepopup__address" @click="handleChooseLocation">
      <view class="storepopup__address--word">当前地址：{{ getterArea?.join('') }}{{ getterAddress }}</view>
      <u-icon name="arrow-right" color="#969799"></u-icon
    ></view>
    <scroll-view scroll-y class="storepopup__content">
      <u-radio-group v-model="pointId" wrap active-color="#F54319">
        <u-radio v-for="(item, index) in pointList" :key="index" :name="item.id">
          <view class="storepopup__content-item">
            <view class="storepopup__content-item__left">
              <view class="storepopup__content-item__left--name">{{ item.storeName }}</view>
              <view class="storepopup__content-item__left--phone">{{ item.functionaryPhone }}</view>
              <view class="storepopup__content-item__left--detail"> {{ item.detailedAddress }} </view>
            </view>
            <view class="storepopup__content-item__right">
              <view class="storepopup__content-item__right--icon"></view>
              <view v-if="item.distance?.toFixed(2)" class="storepopup__content-item__right--distance"
                >距您:<text style="color: red">{{ item.distance.toFixed(2) }}km</text></view
              >
            </view>
          </view>
        </u-radio>
      </u-radio-group>
    </scroll-view>
    <view class="storepopup__btn" @click="handlePointClick">确认</view>
  </u-popup>
  <u-popup v-model="showTime" mode="bottom" border-radius="14">
    <view class="timepopup__title">选择提货时间</view>
    <view class="timepopup__box">
      <view class="timepopup__box_left">
        <scroll-view scroll-y class="timepopup__box_left-scroll">
          <view
            v-for="(item, index) in pointDate"
            :key="index"
            class="timepopup__box_left--item"
            :class="{ 'timepopup__box_left--active': dArrActiveIndex === index }"
            @click="handleDateClick(index)"
          >
            {{ item }}
          </view>
        </scroll-view>
      </view>
      <view class="timepopup__box_right">
        <u-checkbox-group shape="circle" active-color="#F54319">
          <scroll-view scroll-y class="timepopup__box_right-scroll" :scroll-top="timeScrollTop">
            <template v-for="(i, index) in pointTimeArr" :key="index">
              <view
                v-if="dArrActiveIndex !== 0 || new Date().getHours() < i.endTime + 1"
                class="timepopup__box_right--item"
                :class="{ 'timepopup__box_right--active': timeArrActiveIndex === index }"
              >
                <text> {{ i.time }}</text>
                <u-checkbox v-model="i.type" :name="index" @change="handleTimeClick(index, i.type)"> </u-checkbox>
              </view>
            </template>
          </scroll-view>
        </u-checkbox-group>
      </view>
    </view>
  </u-popup>
  <u-modal
    v-model="show"
    :content="titleMsg"
    :content-style="{ fontSize: '14px', fontWeight: 'bold', color: '#000', padding: '20px' }"
    :title-style="{ fontSize: '14px', fontWeight: 'bold', color: '#f29100' }"
    :confirm-style="{ color: '#000' }"
  >
  </u-modal>
</template>

<style scoped lang="scss">
@include b(storeinfo) {
  background: #fff;
  border-radius: 20rpx;
  padding: 34rpx 46rpx 48rpx 36rpx;

  @include e(data) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #777777;
    font-size: 26rpx;
    margin-bottom: 32rpx;
    @include m(tips) {
      width: 160rpx;
      height: 50rpx;
      // text-align: center;
      line-height: 50rpx;
      color: #333333;
      font-size: 28rpx;
      // font-weight: bold;
      border-radius: 40rpx;
      margin-right: 28rpx;
    }
    @include m(storename) {
      color: #333333;
      font-size: 28rpx;
      font-weight: bold;
    }
    @include m(notselected) {
      color: #000;
    }
    @include m(selected) {
      display: flex;
      justify-content: space-between;
      width: 100%;
    }
  }
  @include e(address) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 42rpx;
    font-size: 28rpx;
  }
  @include e(cell) {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 24rpx;
    @include m(title) {
      color: #333333;
      font-size: 28rpx;
      // font-weight: bold;
    }
    @include m(content) {
      // width: 200rpx;
    }
    @include m(time) {
      flex: 1;
      @include flex;
      justify-content: flex-end;
    }
  }
}
@include b(storepopup) {
  @include e(title) {
    font-size: 30rpx;
    text-align: center;
    margin-bottom: 34rpx;
    margin-top: 36rpx;
    font-weight: 700;
  }
  @include e(address) {
    font-size: 20rpx;

    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30rpx;

    // text-align: center;
    margin: 0 auto;
    @include m(word) {
      width: 700rpx;
      @include utils-ellipsis;
    }
  }
  @include e(content) {
    padding: 0 30rpx;
    height: 750rpx;
  }
  @include e(content-item) {
    font-size: 22rpx;
    color: #838383;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 34rpx 0;
    border-bottom: 1px solid #f2f2f2;
  }
  @include e(content-item__left) {
    margin-left: 50rpx;
    width: 380rpx;
    @include m(name) {
      color: #000;
      font-weight: bold;
    }
    @include m(detail) {
      @include utils-ellipsis(2);
    }
  }
  @include e(content-item__right) {
    @include m(icon) {
      width: 40rpx;
      height: 40rpx;
      margin: 0 auto;
    }
    @include m(distance) {
      color: #000;
      width: 240rpx;
      text-align: center;
    }
  }
  @include e(btn) {
    margin: 36rpx auto;
    width: 688rpx;
    height: 78rpx;
    line-height: 78rpx;
    text-align: center;
    border-radius: 36rpx;
    font-size: 28rpx;
    color: #fff;
    background: linear-gradient(90deg, #f54319 2%, #fb4b5c 70%, #ff5656 98%);
  }
}
@include b(timepopup) {
  @include e(box) {
    width: 100vw;
    @include flex;
    justify-content: space-between;
    align-items: flex-start;
    @include e(box_left-scroll) {
      height: 300px;
    }
    @include e(box_right-scroll) {
      height: 300px;
    }
    @include e(box_left) {
      height: 300px;
      background: #f6f6f6;
      width: 120px;
      @include m(item) {
        background: #f6f6f6;
        height: 60px;
        line-height: 60px;
        text-align: center;
      }
      @include m(active) {
        background: #fff;
      }
    }
    @include e(box_right) {
      flex: 1;
      @include m(item) {
        padding: 0 0 20px 0;
        padding-left: 100rpx;
        width: calc(100vw - 120px);
        @include flex;
        justify-content: space-between;
        &:nth-child(1) {
          padding-top: 20px;
        }
      }
    }
  }
  @include e(title) {
    font-size: 30rpx;
    text-align: center;
    margin-bottom: 34rpx;
    margin-top: 36rpx;
    font-weight: 700;
  }
  @include e(content) {
    display: flex;
  }
  @include e(content-left) {
    width: 232rpx;
    @include m(item) {
      height: 80rpx;
      line-height: 80rpx;
      text-align: center;
      background: #f6f6f6;
    }
  }
  @include e(content-right) {
    padding-left: 66rpx;
    @include m(item) {
      height: 80rpx;
      line-height: 80rpx;
    }
  }
  @include e(btn) {
    margin: 36rpx auto;
    width: 688rpx;
    height: 78rpx;
    line-height: 78rpx;
    text-align: center;
    border-radius: 36rpx;
    font-size: 28rpx;
    color: #fff;
    background: linear-gradient(90deg, #f54319 2%, #fb4b5c 70%, #ff5656 98%);
  }
}
</style>
