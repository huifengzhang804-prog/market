<script setup lang="ts">
/**
 * 高德地图 h5 暂不支持小程序
 */
// #ifdef H5
import type { Coordinate } from '@/apis/address/type'
import { useAmapKeyStore } from '@/store/modules/amapKey'
import AMapLoader from '@amap/amap-jsapi-loader'
import { onUnmounted, type PropType, ref } from 'vue'
// import { useVModel } from '@vueuse/core'

const props = defineProps({
  zoom: {
    type: Number,
    default: 13.02,
  },
  width: {
    type: String,
    default: '100%',
  },
  height: {
    type: String,
    default: '100%',
  },
  coordinates: {
    type: Array as unknown as PropType<Coordinate>,
    default: () => [],
  },
  // 偏差的经纬度量
  correctDistance: {
    type: Number,
    default: 0,
  },
})

const map = ref<Obj>()
const AMapRef = ref<Obj>()
/**
 * 初始化地图
 */
const initAMap = () => {
  AMapLoader.load({
    key: useAmapKeyStore().getAmapKey, // 申请好的Web端开发者Key，首次调用 load 时必填
    version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
    plugins: ['AMap.Geolocation', 'AMap.PlaceSearch', 'AMap.AutoComplete', 'AMap.Geocoder'], //需要使用的的插件列表，如比例尺'AMap.Scale'，支持添加多个如：['...','...']
  })
    .then((AMap) => {
      AMapRef.value = AMap
      map.value = new AMap.Map('amap_container', {
        zoom: props.zoom,
        resizeEnable: true,
        center: props.coordinates,
      })

      map.value!.on('zoomchange', () => {
        console.log('缩放', map.value!.getZoom())
      })
    })
    .catch((e) => {
      console.log(e)
    })
}
initAMap()
onUnmounted(() => {
  map.value?.destroy()
})

/**
 * 标记点列表
 */
const marker = ref<Obj[]>([])

/**
 * 创建自定义标记
 * @param position 坐标
 * @param content 内容(可为html文本)
 * @param offset 偏移量(x,y)
 */
const createMarker = (position: number[], content = '', offset: [number, number] = [0, 0]) => {
  let timer
  let timers = 0
  if (map.value && AMapRef.value) {
    if (timer) {
      clearTimeout(timer)
    }
    marker.value = new AMapRef.value.Marker({
      position,
      content: content,
      offset: new AMapRef.value.Pixel(offset[0], offset[1]),
    })
    map.value.add(marker.value)
  } else {
    if (timer) {
      clearTimeout(timer)
    }
    if (timers >= 30) {
      console.log('地图组件未初始化')
      return
    }
    timer = setTimeout(() => {
      timers += 1
      createMarker(position, content, offset)
    }, 100)
  }
}

/**
 * 获取地图边界
 * 计算两点之间的中心点以及合适的缩放倍率
 * 包含的点: 客户地址和配送员位置 为了范围稍微大一点，所以两个点的上下左右需要加上一定距离
 */
const getBounds = (coordinates: [number, number][]): [number[], number[]] => {
  let minLat = Number.MAX_VALUE
  let maxLat = Number.MIN_VALUE
  let minLng = Number.MAX_VALUE
  let maxLng = Number.MIN_VALUE

  for (const coordinate of coordinates) {
    const lat = coordinate[0]
    const lng = coordinate[1]
    minLat = Math.min(minLat, lat)
    maxLat = Math.max(maxLat, lat)
    minLng = Math.min(minLng, lng)
    maxLng = Math.max(maxLng, lng)
  }

  return [
    [maxLat + props.correctDistance, maxLng + props.correctDistance],
    [minLat - props.correctDistance, minLng - props.correctDistance],
  ]
}
/**
 * 获取地图缩放倍率
 * @param bounds 地图边界
 * @param mapDim 地图宽高
 */
const getZoomLevel = (bounds: [number[], number[]], mapDim: [number, number]): number => {
  const WORLD_DIM = 256
  const ZOOM_MAX = 21

  const ne = bounds[0]
  const sw = bounds[1]

  const latDiff = ne[0] - sw[0]
  const lngDiff = ne[1] - sw[1]

  const latZoom = Math.log((mapDim[0] * 360) / latDiff / WORLD_DIM) / Math.log(2)
  const lngZoom = Math.log((mapDim[1] * 180) / lngDiff / WORLD_DIM) / Math.log(2)
  return Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX)
}
/**
 * 获取中心点
 * @param bounds 地图边界
 */
const getCenterPoint = (bounds: [number[], number[]]): [number, number] => {
  const ne = bounds[0]
  const sw = bounds[1]

  const lat = (ne[0] + sw[0]) / 2
  const lng = (ne[1] + sw[1]) / 2

  return [lat, lng]
}
/**
 * 获取地图实际宽高(px)
 */
const getMapWidthAndHeightPx = (): [number, number] => {
  const container = document.getElementById('amap_container')
  if (container) {
    return [container.clientWidth, container.clientHeight]
  }
  return [0, 0]
}

const setMapCenterAndZoom = (coordinates: [number, number][]) => {
  if (map.value) {
    const bounds = getBounds(coordinates)
    const zoomLevel = getZoomLevel(bounds, getMapWidthAndHeightPx())
    const centerPoint = getCenterPoint(bounds)
    map.value.setZoomAndCenter(zoomLevel, centerPoint)
  }
}
/**
 * 清除标记
 */
const clearMarker = () => {
  if (map.value) {
    map.value.remove(marker.value) //清除 marker
  }
}
defineExpose({
  createMarker,
  clearMarker,
  setMapCenterAndZoom,
  map,
})
// #endif
</script>

<template>
  <view id="amap_container" class="amap_container" :style="`width:${width};height:${height}`">
    <slot></slot>
  </view>
</template>

<style lang="scss" scoped>
.amap_container {
  position: relative;
}
</style>
