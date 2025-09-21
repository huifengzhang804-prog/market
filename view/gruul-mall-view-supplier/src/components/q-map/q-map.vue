<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage } from 'element-plus'
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
})
const $emit = defineEmits(['change'])
const markerQueue = ref<any[]>([])
const tipSearchValue = ref('')

initMap()

const createMarker = (AMap: any, map: any, position: number[]) => {
    const marker = new AMap.Marker({
        position,
        map,
    })
    // 删除上一个标记点
    if (markerQueue.value.length) {
        map.remove(markerQueue.value.shift())
    }
    map.add(marker)
    markerQueue.value.push(marker)
    geocoder(AMap, position)
}
/**
 * @description: 点击地图获取坐标
 */
const getCoordinate = (e: any, AMap: any, map: any) => {
    createMarker(AMap, map, [e.lnglat.getLng(), e.lnglat.getLat()])
}
const searchPluginInit = (AMap: any, map: any) => {
    const auto = new AMap.AutoComplete({
        input: 'tipinput',
    })
    const placeSearch = new AMap.PlaceSearch({
        map,
    })
    //注册监听，当选中某条记录时会触发

    auto.on('select', (e: any) => {
        select(e, placeSearch)
    })
    placeSearch.on('markerClick', (e: any) => {
        $emit('change', {
            address: e.data.address,
            position: [e.data.location.lng.toString(), e.data.location.lat.toString()],
        })
    })
}
function select(e: any, placeSearch: any) {
    placeSearch.setCity(e.poi.adcode)
    //关键字查询查询
    placeSearch.search(e.poi.name)
}

function geocoder(AMap: any, position: number[]) {
    const geocoder = new AMap.Geocoder({
        radius: 1000, //范围，默认：500
    })
    geocoder.getAddress(position, (status: string, result: any) => {
        if (status === 'complete' && result.regeocode) {
            const coordinates = position.map((item) => item.toString())
            $emit('change', {
                address: result.regeocode.formattedAddress,
                position: coordinates,
            })
        } else {
            ElMessage.error('获取地址失败')
        }
    })
}

function initMap() {
    AMapLoader.load({
        key: useAmapKeyStore().getAmapKey, // 申请好的Web端开发者Key，首次调用 load 时必填
        version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: ['AMap.Geolocation', 'AMap.PlaceSearch', 'AMap.AutoComplete', 'AMap.Geocoder'],
    })
        .then((AMap) => {
            const map = new AMap.Map('container', {
                zoom: 10,
                resizeEnable: true,
            })
            const geolocation = new AMap.Geolocation({
                enableHighAccuracy: true, // 高精度开启
                radius: 10000,
                extensions: 'all',
            })
            geolocation.getCurrentPosition()
            // 初始化地理位置目标
            if ($props.coordinates.length) {
                map.setCenter($props.coordinates)
                createMarker(
                    AMap,
                    map,
                    $props.coordinates.map((item) => Number(item)),
                )
            }
            // 初始化搜索插件
            searchPluginInit(AMap, map)
            // 点击标记marker
            map.on('click', (e: any) => {
                getCoordinate(e, AMap, map)
            })
            map.on()
        })
        .catch((e) => {
            console.log('地图组件加载失败', e)
        })
}
</script>

<template>
    <div class="map">
        <el-input id="tipinput" v-model="tipSearchValue" placeholder="请输入查询地址" class="map__tip" maxlength="20" />
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
