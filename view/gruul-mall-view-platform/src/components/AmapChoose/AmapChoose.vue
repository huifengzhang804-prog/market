<script setup lang="ts">
import { doGetAddressListByKeywordsAndLocation, doGetPoiNearby } from '@/apis/address'
import AMapLoader from '@amap/amap-jsapi-loader'
import { debounce } from 'lodash-es'
import userIcon from '@/assets/image/ic/user.png'
import Decimal from 'decimal.js'
import type { AddressListItem, Checked, Coordinate } from '@/apis/afs/type'
import { useAmapKeyStore } from '@/store/modules/amapKey'

const props = defineProps({
    initLocation: {
        type: Array as unknown as PropType<Coordinate>,
        default: () => [],
    },
})
const emit = defineEmits(['placeChoose'])
const list = ref<AddressListItem[]>([])
const keyword = ref('')
const timeId = ref()
const clickedPoint = ref<Long[]>([])

const checked = ref<Checked>({
    longitude: '',
    latitude: '',
    address: '',
    name: '',
})

onMounted(() => {
    initMapWeb()
})
const errorBar = ref(false)

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
            keyword.value = ''
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
                getPlacesOnPoint(`${result.position.lng},${result.position.lat}`)
                getPlaces('', `${result.position.lng},${result.position.lat}`)
            }
        })
    })
}
const addMarker = (lng: Long, lat: Long) => {
    // 记录当前点
    clickedPoint.value = [lng, lat]
    const icon = new AMap.value.Icon({
        // 图标的取图地址
        image: userIcon,
        // 图标所用图片大小
        imageSize: new AMap.value.Size(40, 40),
    })
    marker.value = new AMap.value.Marker({
        icon,
        position: [lng, lat],
        offset: new AMap.value.Pixel(-20, -40),
    })
    map.value.add(marker.value)
    map.value.setCenter([lng, lat])
}
/**
 * 根据经纬度获取周边POI
 * @param location 经纬度
 * @param radius 半径
 * @param types POI类型
 */
const getPlacesOnPoint = async (
    location: string,
    radius = 3000,
    types = '010000|020000|030000|040000|050000|060000|070000|080000|090000|100000|110000|120000|130000|140000|150000|160000|170000|180000|190000|200000|220000|970000|980000|990000',
) => {
    const result = await doGetPoiNearby({
        location,
        radius,
        types,
    })
    if (result.status === '1') {
        errorBar.value = false
        list.value = result.pois.filter((item: any) => item.location && item.location.length > 0)
        const findSamePoint = list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
        checked.value.address = findSamePoint?.address || ''
        checked.value.name = findSamePoint?.name || ''
    } else if (result.status === '0' && result.infocode === '10044') {
        errorBar.value = true
    }
}

const getPlaces = async (keywords: string, location: string) => {
    if (location === ',') {
        location = ''
    }
    const result = await doGetAddressListByKeywordsAndLocation({ keywords, location })
    if (result.status === '1') {
        errorBar.value = false
        list.value = result.tips.filter((item: any) => item.location && item.location.length > 0)
        const findSamePoint = list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
        checked.value.address = findSamePoint?.address || ''
        checked.value.name = findSamePoint?.name || ''
    } else if (result.status === '0' && result.infocode === '10044') {
        errorBar.value = true
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
    emit('placeChoose', checked.value)
}
const inputValue = (e: any) => {
    if (timeId.value) {
        clearTimeout(timeId.value)
    }
    timeId.value = setTimeout(() => {
        getPlaces(e, `${clickedPoint.value[0]},${clickedPoint.value[1]}`)
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

/**
 * 是否选中
 */
const isChecked = computed(() => {
    return !!list.value.find((item) => item.location === `${checked.value.longitude},${checked.value.latitude}`)
})
</script>

<template>
    <div class="df choose_amap_container">
        <div id="chooseMap" style="width: 666px; height: 552px">
            <div class="search_input">
                <el-input v-model="keyword" placeholder="请输入定位地址" @input="debounceInputValue"></el-input>
            </div>
        </div>
        <div class="right_list">
            <div class="title_tips">请选择</div>
            <div class="list_container">
                <div v-for="(item, index) in list" :key="index" class="list_item">
                    <div class="fdc fcenter address_container">
                        <div class="address_name">{{ item.name }}</div>
                        <div class="address_address">{{ item.district ? item.district + '-' : '' }}{{ item.address ? item.address : '' }}</div>
                    </div>
                    <div v-if="item.distance && item.distance !== 0" class="distance mla">{{ item.distance }}m</div>
                    <div
                        class="choose_btn cup mla"
                        :class="{
                            is_choosing: item.location === `${checked.longitude},${checked.latitude}` && item.name === checked.name,
                        }"
                        @click="toChecked(item)"
                    >
                        {{ item.location === `${checked.longitude},${checked.latitude}` && item.name === checked.name ? '已选' : '选择' }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.choose_amap_container {
    #chooseMap {
        position: relative;
        .search_input {
            position: absolute;
            right: 22px;
            top: 17px;
            z-index: 999;
        }
    }

    .right_list {
        width: 666px;
        height: 552px;
        display: flex;
        flex-direction: column;
        text-align: left;
        margin-left: 19px;
        .title_tips {
            color: rgb(51, 51, 51);
            font-size: 16px;
            margin-bottom: 20px;
        }
        .list_container {
            display: flex;
            flex-direction: column;
            flex: 1;
            overflow-y: scroll;
            .list_item {
                border: 1px solid #e9ecf0;
                border-radius: 2px;
                width: 100%;
                padding-left: 8px;
                padding-top: 12px;
                padding-bottom: 12.5px;
                padding-right: 13px;
                display: flex;
                align-items: center;
                .address_container {
                    width: 0;
                    flex: 1;
                    align-items: normal;
                    .address_name {
                        color: #333333;
                        font-size: 14px;
                        font-weight: 600;
                        margin-bottom: 3.5px;
                    }
                    .address_address {
                        width: 100%;
                        // 溢出隐藏省略号
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        color: #999999;
                        font-size: 13px;
                        font-weight: 400;
                    }
                }
            }
            .distance {
                color: rgb(51, 51, 51);
                font-size: 14px;
            }
            .choose_btn {
                border: 1px solid #f54319;
                border-radius: 2px;
                height: 32px;
                line-height: 32px;
                text-align: center;
                padding: 0 16px;
                color: #f54319;
                font-size: 14px;
                margin-left: 52px;
            }
            .is_choosing {
                border: 1px solid #f54319;
                border-radius: 2px;
                height: 32px;
                line-height: 32px;
                text-align: center;
                padding: 0 16px;
                color: #fff;
                font-size: 14px;
                margin-left: 52px;
                background-color: #f54319;
            }
        }
    }
}
</style>
