<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'
import { Coordinate } from '@/apis/afs/type'
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
        type: Array as unknown as PropType<Coordinate>,
        default: () => [],
    },
    disabled: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['change'])
const markerQueue = ref<any[]>([])
const placeSearch = ref()
const tipSearchValue = ref('')

initMap()

const createMarker = (AMap: any, map: any, position: number[]) => {
    console.log(position)

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
 * 点击地图获取坐标
 */
const getCoordinate = (e: any, AMap: any, map: any) => {
    createMarker(AMap, map, [e.lnglat.getLng(), e.lnglat.getLat()])
}
const searchPluginInit = (AMap: any, map: any) => {
    const auto = new AMap.AutoComplete({
        input: 'tipinput',
    })
    placeSearch.value = new AMap.PlaceSearch({
        map,
    })
    //注册监听，当选中某条记录时会触发
    auto.on('select', (e: any) => {
        select(e, placeSearch.value)
    })
    placeSearch.value.on('markerClick', (e: any) => {
        // 点击 marker 如果为 交通设施服务;公交车站;公交车站相关 拼接地址返回
        const { pname, cityname, adname, address, name } = e.data
        function getAddress() {
            if (e.data.type === '交通设施服务;公交车站;公交车站相关') {
                return pname + cityname + adname + address + name
            } else {
                return address
            }
        }
        $emit('change', {
            address: getAddress(),
            position: [e.data.location.lng.toString(), e.data.location.lat.toString()],
            area: [pname, cityname, adname],
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
            console.log('result', result)
            if (result.regeocode.addressComponent) {
                const { province, city, district } = result.regeocode.addressComponent
                $emit('change', {
                    address: result.regeocode.formattedAddress,
                    position: coordinates,
                    area: [province, city, district],
                })
            }
        } else {
            // ElMessage.error('获取地址失败')
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
            // 初始化地理位置目标
            if ($props.coordinates.length) {
                console.log('$props.coordinates', $props.coordinates)
                map.setCenter($props.coordinates)
                createMarker(
                    AMap,
                    map,
                    $props.coordinates.map((item) => Number(item)),
                )
            }
            let loc: number[] = [121.489551, 29.936806]
            geolocation.getCurrentPosition((status: string, result: any) => {
                console.log(status)
                console.log(result)
                if (status === 'complete' && result.position) {
                    loc = [result.position.lng, result.position.lat]
                } else {
                    console.log(status)
                    console.log(result)
                }
                if (!$props.coordinates.length) {
                    map.setCenter(loc)
                    createMarker(AMap, map, loc)
                }
            })

            // 初始化搜索插件
            searchPluginInit(AMap, map)
            if (!$props.disabled) {
                // 点击标记marker
                map.on('click', (e: any) => {
                    getCoordinate(e, AMap, map)
                })
            }
        })
        .catch((e) => {
            console.log('地图组件加载失败', e)
        })
}
function searchName(params: string) {
    placeSearch.value.search(params)
}
defineExpose({ searchName: searchName })
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
