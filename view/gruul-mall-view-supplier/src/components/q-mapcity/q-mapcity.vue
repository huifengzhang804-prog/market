<script setup lang="ts">
import { watchEffect } from 'vue'
import type { PropType } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { useAmapKeyStore } from '@/store/modules/amapKey'

const $props = defineProps({
    width: {
        type: String,
        default: '500',
    },
    height: {
        type: String,
        default: '300',
    },
    coordinates: {
        type: Array as PropType<string[]>,
        default: () => [],
    },
    area: {
        type: Number,
        default: 5,
    },
})

initMap()

const createMarker = (AMap: any, map: any, position: number[]) => {
    const marker = new AMap.Marker({
        position,
        map,
    })

    map.add(marker)
}
/**
 * @description: 创建地图实例
 * @description: AMap.Geolocation 获取当前位置定位 AMap.PlaceSearch 搜索周边  AMap.AutoComplete 搜索提示  AMap.Geocoder 逆地理编码
 * @returns {*}
 */
function initMap() {
    AMapLoader.load({
        key: useAmapKeyStore().getAmapKey, // 申请好的Web端开发者Key，首次调用 load 时必填
        version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: ['AMap.Geolocation', 'AMap.PlaceSearch', 'AMap.AutoComplete', 'AMap.Geocoder'],
    })
        .then((AMap) => {
            // 初始化地图
            const map = new AMap.Map('container', {
                zoom: 11.5, //缩放级别
                resizeEnable: true, //是否监控地图容器尺寸变化
                animateEnable: false,
            })
            //设置中心店
            map.setCenter($props.coordinates)
            // 创建标记点
            let arr: number[] = []
            $props.coordinates.forEach((item) => {
                arr.push(Number(item))
            })
            createMarker(AMap, map, arr)
            // 创建圆形区域
            handleChangeArea(AMap, map, $props.area, $props.coordinates)
            // 地址转化为经纬度
            handleChangeinp(AMap)
        })
        .catch((e) => {
            console.log('地图组件加载失败', e)
        })
}
/**
 * @description: 根据传递进来的coordinates和直径大小创建标记点
 * @returns {*}
 */
const handleChangeArea = (AMap: any, map: any, area: number, coordinates: string[]) => {
    const obj = {
        lng: coordinates[0],
        lat: coordinates[1],
    }
    const center = new AMap.LngLat(obj.lng, obj.lat)
    const radius = area * 1000
    const circle = new AMap.Circle({
        center,
        radius,
        borderWeight: 3,
        strokeColor: 'blue',
        strokeOpacity: 1,
        strokeWeight: 3,
        fillOpacity: 0.4,
        strokeStyle: 'dashed',
        strokeDasharray: [10, 10],
        // 线样式还支持 'dashed'
        fillColor: '#1791fc',
        zIndex: 50,
    })
    map.add(circle)
}
watchEffect(() => {
    console.log('coordinates', $props.area)

    initMap()
})
/**
 * @description: 地址转化为经纬度
 * @returns {*}
 */
const handleChangeinp = (AMap: any) => {
    var geocoder = new AMap.Geocoder({
        city: '浙江省', // city 指定进行编码查询的城市，支持传入城市名、adcode 和 citycode
    })

    var address = '宁波市鄞州区钟公庙街道万达广场(四明中路)万达广场(鄞州店)'

    geocoder.getLocation(address, function (status, result) {
        if (status === 'complete' && result.info === 'OK') {
            // result中对应详细地理坐标信息
            console.log(status, result.geocodes[0].location)
        }
    })
}
</script>

<template>
    <div class="map">
        <div id="container" :style="{ width: $props.width + 'px', height: $props.height + 'px' }"></div>
    </div>
</template>

<style lang="scss" scoped>
@include b(map) {
    position: relative;
    @include e(tip) {
        position: absolute;
        right: 0;
        top: 0;
        width: 200px;
        z-index: 9;
    }
}
</style>
