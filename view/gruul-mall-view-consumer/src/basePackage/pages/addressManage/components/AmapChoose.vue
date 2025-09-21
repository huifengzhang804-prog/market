<template>
  <view v-show="showPlace" class="amap-choose">
    <view class="top-button">
      <view class="cancel" @tap="toCancel">取消</view>
      <view v-show="errorBar" class="error_bar">
        <text>今日高德流量包已用完，请明天再试！</text>
        <u-icon color="white" name="close-circle" size="30" style="margin-left: 10rpx" @click="errorBar = false" />
      </view>
      <view :style="{ backgroundColor: isChecked ? color : '#999' }" class="conform" @tap="confrom">完成</view>
    </view>
    <view class="map-box">
      <view id="chooseMap" class="choose_map"></view>
    </view>
    <view class="content">
      <view class="search">
        <view>
          <u-icon color="#999999" name="search" size="30" />
          <input v-model="keyword" placeholder="搜索地点" @input="debounceInputValue" />
        </view>
        <text @tap="cancel">取消</text>
      </view>
      <view class="main">
        <view v-for="(item, index) in list" :key="index" @tap="toChecked(item)">
          <view>
            <view>{{ item.name }}</view>
            <text class="detail">{{ item.district ? item.district + '-' : '' }}{{ item.address ? item.address : '' }} </text>
          </view>
          <view>
            <text
              v-if="item.location === `${checked.longitude},${checked.latitude}` && item.name === checked.name"
              :style="{ color }"
              class="checked-item"
              >√
            </text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
// #ifdef H5
import AMapLoader from '@amap/amap-jsapi-loader'
import { useVModel } from '@vueuse/core'
import { debounce } from 'lodash'
import { computed, onUnmounted, type PropType, ref, watch } from 'vue'
import { Decimal } from 'decimal.js-light'
import { useAmapKeyStore } from '@/store/modules/amapKey'

const props = defineProps({
  color: {
    type: String,
    default: '#ff6000',
  },
  initLocation: {
    type: Array as unknown as PropType<[number, number]>,
    default: () => [],
  },
  show: {
    type: Boolean,
    default: false,
  },
})
const emit = defineEmits(['placeChoose', 'update:show'])
const showPlace = useVModel(props, 'show', emit)

type AddressListItem = {
  adcode: string
  city: string
  district: string
  location: string
  addressLocal: string
  address: string
  name: string
}
type Checked = {
  longitude: Long
  latitude: Long
  address: string
  name: string
}
const checked = ref<Checked>({
  longitude: '',
  latitude: '',
  address: '',
  name: '',
})
const list = ref<AddressListItem[]>([])
const keyword = ref('')
const timeId = ref()

/**
 * 是否选中
 */
const isChecked = computed(() => {
  return !!list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
})
watch(
  () => showPlace.value,
  (val) => {
    if (val && !AMap.value && !map.value) {
      initMapWeb()
      return
    }
    if (val && props.initLocation.length > 1) {
      let tempArr = [new Decimal(props.initLocation[0]).toFixed(6), new Decimal(props.initLocation[1]).toFixed(6)]
      addMarker(tempArr[0], tempArr[1])
      getPlacesOnPoint(`${tempArr[0]},${tempArr[1]}`)
      checked.value.latitude = tempArr[1]
      checked.value.longitude = tempArr[0]
    }
  },
)
onUnmounted(() => {
  map.value?.destroy()
})
const AMap = ref()
const map = ref()
const marker = ref()
const initMapWeb = () => {
  // @ts-ignore
  AMapLoader.reset() // 禁止多种API加载方式混用报错的bug
  AMapLoader.load({
    key: useAmapKeyStore().getAmapKey, // 申请好的Web端开发者Key，首次调用 load 时必填
    version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
    plugins: ['AMap.Geolocation'], //需要使用的的插件列表，如比例尺'AMap.Scale'，支持添加多个如：['...','...']
  }).then((AMapClass) => {
    AMap.value = AMapClass
    map.value = new AMap.value.Map('chooseMap', {
      zoom: 13,
    })
    map.value.on('click', (e: any) => {
      console.log(e)
      if (marker.value) {
        map.value.remove(marker.value)
      }
      addMarker(e.lnglat.lng, e.lnglat.lat)
      getPlacesOnPoint(`${e.lnglat.lng},${e.lnglat.lat}`)
    })
    if (props.initLocation.length > 1) {
      let tempArr = [new Decimal(props.initLocation[0]).toFixed(6), new Decimal(props.initLocation[1]).toFixed(6)]
      addMarker(tempArr[0], tempArr[1])
      getPlacesOnPoint(`${tempArr[0]},${tempArr[1]}`)
      checked.value.latitude = tempArr[1]
      checked.value.longitude = tempArr[0]
      return
    }
    const geolocation = new AMap.value.Geolocation({
      // 是否使用高精度定位，默认：true
      enableHighAccuracy: true,
      // 设置定位超时时间，默认：无穷大
      timeout: 10000,
      // 定位按钮的停靠位置的偏移量，默认：Pixel(10, 20)
      buttonOffset: new AMap.value.Pixel(10, 20),
      //  定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
      zoomToAccuracy: true,
      //  定位按钮的排放位置,  RB表示右下
      buttonPosition: 'RB',
    })
    geolocation.getCurrentPosition((status: any, result: any) => {
      if (result.info === 'SUCCESS' && result.position && result.position) {
        addMarker(result.position.lng, result.position.lat)
        getPlaces('', `${result.position.lng},${result.position.lat}`)
      }
    })
  })
}

const addMarker = (lng: Long, lat: Long) => {
  const icon = new AMap.value.Icon({
    // 图标的取图地址
    image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
    // 图标所用图片大小
    imageSize: new AMap.value.Size(32, 42),
  })
  marker.value = new AMap.value.Marker({
    icon,
    position: [lng, lat],
    offset: new AMap.value.Pixel(-16, -42),
  })
  map.value.add(marker.value)
  map.value.setCenter([lng, lat])
}
const errorBar = ref(false)
const getPlaces = (keywords: string, location: string) => {
  uni.request({
    url: `https://restapi.amap.com/v3/assistant/inputtips?key=${useAmapKeyStore().getAmapWebServiceKey}&keywords=${keywords}&location=${location}`,
    method: 'GET',
    success: (res) => {
      const result: any = res.data
      if (result.status === '1') {
        errorBar.value = false
        list.value = result.tips.filter((item: any) => item.location && item.location.length > 0)
        const findSamePoint = list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
        checked.value.address = findSamePoint?.address || ''
        checked.value.name = findSamePoint?.name || ''
      } else if (result.status === '0' && result.infocode === '10044') {
        errorBar.value = true
      }
    },
    fail: (err) => {
      uni.showToast({
        title: JSON.stringify(err),
        icon: 'none',
      })
    },
  })
}
/**
 * 根据经纬度获取周边POI
 * @param location 经纬度
 * @param radius 半径
 * @param types POI类型
 */
const getPlacesOnPoint = (
  location: string,
  radius = 3000,
  types = '010000|020000|030000|040000|050000|060000|070000|080000|090000|100000|110000|120000|130000|140000|150000|160000|170000|180000|190000|200000|220000|970000|980000|990000',
) => {
  uni.request({
    url: `https://restapi.amap.com/v5/place/around?key=${
      useAmapKeyStore().getAmapWebServiceKey
    }&location=${location}&radius=${radius}&types=${types}`, // 餐饮服务|住宿服务|生活服务|商务住宅|公司企业
    method: 'GET',
    success: (res) => {
      const result: any = res.data
      if (result.status === '1') {
        errorBar.value = false
        list.value = result.pois.filter((item: any) => item.location && item.location.length > 0)
        const findSamePoint = list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
        checked.value.address = findSamePoint?.address || ''
        checked.value.name = findSamePoint?.name || ''
      } else if (result.status === '0' && result.infocode === '10044') {
        errorBar.value = true
      }
    },
    fail: (err) => {
      uni.showToast({
        title: JSON.stringify(err),
        icon: 'none',
      })
    },
  })
}

const inputValue = (e: any) => {
  if (timeId.value) {
    clearTimeout(timeId.value)
  }
  timeId.value = setTimeout(() => {
    getPlaces(e.detail.value, `${checked.value.longitude},${checked.value.latitude}`)
    timeId.value = null
  })
}
const debounceInputValue = debounce(inputValue, 1000)

const cancel = () => {
  keyword.value = ''
  list.value = []
  if (marker.value) {
    map.value.remove(marker.value)
  }
}

const toCancel = () => {
  cancel()
  showPlace.value = false
}

const confrom = () => {
  if (!isChecked.value) {
    return
  }
  if (checked.value) {
    emit('placeChoose', checked.value)
    toCancel()
  }
}

const toChecked = (obj: AddressListItem) => {
  checked.value.address = obj.address
  checked.value.longitude = obj.location.split(',')[0]
  checked.value.latitude = obj.location.split(',')[1]
  checked.value.name = obj.name
  if (marker.value) {
    map.value.remove(marker.value)
    let tempArr = obj.location.split(',')
    addMarker(tempArr[0], tempArr[1])
  }
}
// #endif
</script>

<style lang="scss" scoped>
.amap-choose {
  position: fixed;
  top: 0;
  width: 100%;
  height: 100dvh;
  z-index: 99999999999;

  .top-button {
    top: 0;
    box-sizing: border-box;
    padding: 24rpx;
    position: fixed;
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    z-index: 100;

    .cancel {
      // color: white;
    }

    .error_bar {
      padding: 8rpx 14rpx;
      border-radius: 8rpx;
      color: white;
      background-color: #f54319;
      font-size: 22rpx;
    }

    .conform {
      // background-color: #ff6000;
      color: white;
      padding: 8rpx 14rpx;
      border-radius: 8rpx;
    }
  }

  .map-box {
    height: 60%;
    // background-color: pink;
    .choose_map {
      width: 100%;
      height: 100%;
    }

    :deep(.amap-logo) {
      display: none !important;
    }

    :deep(.amap-copyright) {
      display: none !important;
    }
  }

  .content {
    position: fixed;
    bottom: 0;
    border-radius: 24rpx 24rpx 0 0;
    height: 42%;
    width: 100%;
    background: white;
    box-sizing: border-box;
    padding: 24rpx;

    .search {
      width: 100%;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;

      & > view {
        width: 88%;
        height: 64rpx;
        background: #f4f4f4;
        border-radius: 14rpx;
        box-sizing: border-box;
        padding: 8rpx 24rpx;
        display: flex;
        flex-direction: row;
        align-items: center;

        input {
          margin-left: 8rpx;
          flex: 1;
        }
      }
    }

    .main {
      margin-top: 20rpx;
      height: calc(100% - 88rpx);
      // background: pink;
      overflow-y: auto;

      & > view {
        box-sizing: border-box;
        padding: 20rpx;
        width: 100%;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;

        .detail {
          color: #707070;
        }

        .checked-item {
          font-family: Arial, serif;
        }
      }
    }
  }
}
</style>
