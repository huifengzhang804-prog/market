<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { doGetICOrder, doGetUuptCourierInfo } from '@/apis/order'
import { GeometryType } from '@/apis/afs/type'
import { type CourierLoctionInfo, HandleErrorStatus, IcOrder, type IcOrderDetail, type IcOrderTimes, type Order } from '@/apis/order/types'
import computedTime from '@/utils/computedTime'
import AMap from './AMap.vue'
// import DeliverErrorDialog from '@/views/order/components/DeliverErrorDialog.vue'
import QTimeLine from '@/views/order/orderDetails/components/QTimeLine/index.vue'
import QTimeLineItem from '@/views/order/orderDetails/components/QTimeLineItem/index.vue'
import Decimal from 'decimal.js'
import CourierIcon from '@/assets/image/ic/courier.png'
import UserIcon from '@/assets/image/ic/user.png'

const props = defineProps({
    order: {
        type: Object as PropType<Order>,
        default() {
            return {}
        },
    },
})

const orderShopInfo = computed(() => {
    return props.order.shopOrders[0]
})

let timerId: NodeJS.Timeout | null = null

const icOrderDetail = ref<IcOrderDetail>({
    // 收货点位置
    receiverLocation: {
        coordinates: [0, 0],
        type: GeometryType.Point,
    },
    orders: [],
})
const OrderParam = computed(() => {
    return { shopId: orderShopInfo.value.shopId, orderNo: props.order.no }
})
/**
 * 获取同城配送订单详情
 */
const getOrderDetail = async () => {
    const { data, code, msg } = await doGetICOrder(OrderParam.value)
    if (data && code === 200) {
        icOrderDetail.value = data
    } else {
        ElMessage.error({ message: msg || '获取订单配送详情失败' })
    }
    return data
}
/**
 * 发请求获取 UU 跑腿配送员信息
 */
const getUuptCourierInfo = async () => {
    const { data, code, msg } = await doGetUuptCourierInfo(OrderParam.value)
    if (data && code === 200) {
        courierLoction.value = data
        return data
    } else {
        Promise.reject(msg)
    }
}
/**
 * 映射骑手状态
 */
const courierStatusMap = (item: IcOrder) => {
    // times 中第一个 isCurrent 为 true 的元素(即第一个元素)
    return times(item)[0].content
}
/**
 * 自定义骑手位置点标记
 */
const courierContent = (item: IcOrder) => {
    return `
<div class="img_container">
    <img src="${CourierIcon}" style="width: 50px;height: 40px;"/>
    <div class="marker-box">
        <div style="color: #000;font-size: 16px;font-weight: bold;line-height: 41px;">${courierStatusMap(item)}</div>
        <div style="font-size: 14px;line-height: 41px;color: #666666">距${times(item)[0].by === 'takeOrderTime' ? '商家' : '你'}
        <span style="color: #555CFD">${distanceFormat(courierLoction.value.distance)}</span>，预计
        <span style="color: #555CFD">${courierLoction.value.minutes}分钟</span> 后${times(item)[0].by === 'takeOrderTime' ? '到达商家' : '送达'}</div>
    </div>
</div>`
}

/**
 * 距离格式化 超过一千米显示 公里，否则显示 米
 * @param distance
 */
const distanceFormat = (distance: Long) => {
    const decimalDistance = new Decimal(distance)
    return decimalDistance.gt(1000) ? decimalDistance.div(1000).toFixed(2) + 'km' : distance + 'm'
}

/**
 * 自定义收货地点标记
 <img src="${myLocationIcon}" style="width: 29px;height: 19px; "/>
 *
 */
const mineContent = ref(`
    <img src="${UserIcon}" style="width: 50px;height: 50px; border-radius: 50% "/>
`)

const time = ref()
/**
 * 清除点标记并创建点标记
 */
const createMarker = async (item: IcOrder) => {
    const findIndex = icOrderDetail.value.orders.findIndex((it) => {
        /**
         * 通过时间线判断是否为当前订单
         */
        return Object.keys(it.times).every((key) => {
            return it.times[key as keyof typeof it.times] === item.times[key as keyof typeof it.times]
        })
    })
    // 清除点标记
    AMapRef.value[findIndex]?.clearMarker()
    // 创建收货点标记
    AMapRef.value[findIndex]?.createMarker(icOrderDetail.value.receiverLocation.coordinates, mineContent.value, [-25, -50])
    // 创建骑手实时位置点标记
    if (courierLoction.value.location && courierLoction.value.courier.name !== '') {
        AMapRef.value[findIndex]?.createMarker(courierLoction.value.location.coordinates, courierContent(item), [-25, -50])
        await nextTick()
        let flag = 0
        try {
            time.value = setInterval(async () => {
                if (++flag >= 30 || AMapRef.value[findIndex]?.map) {
                    //  3秒后 取消轮询调用
                    if (time.value) {
                        clearInterval(time.value)
                    }
                }
                AMapRef.value[findIndex]?.setMapCenterAndZoom([
                    icOrderDetail.value.receiverLocation.coordinates,
                    courierLoction.value?.location?.coordinates,
                ])
            }, 100)
        } catch (error) {
            if (time.value) {
                clearInterval(time.value)
            }
        }
    }
}
/**
 * 初始化/刷新 骑手位置
 */
const initRiderPosition = async (item: IcOrder) => {
    if (showMap(item)) {
        await getUuptCourierInfo()
        createMarker(item)
    }
}
onMounted(async () => {
    await toRefresh()
})

onUnmounted(() => {
    if (timerId) {
        clearTimeout(timerId)
        timerId = null
    }
    if (time.value) {
        clearInterval(time.value)
        time.value = null
    }
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
/**
 * 显示地图的条件
 */
const showMap = (item: IcOrder) => {
    // 为UUPT订单 且 订单状态为已接单=>已送达之间的状态(TAKEN,ARRIVED_SHOP,PICKUP)
    return courierLoction.value.location && item?.type === 'UUPT' && ['TAKEN', 'ARRIVED_SHOP', 'PICKUP'].includes(item?.status)
}
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
const times = (timeItem: IcOrder) => {
    const orderTimes = timeItem.times
    const timeByListCopy: TimeBy[] = JSON.parse(JSON.stringify(timeByList))
    let arr = timeByListCopy
        .filter((item) => !!orderTimes[item.by])
        .map((item) => {
            return {
                ...item,
                content: item.by === 'errorTime' ? timeItem.statusDesc : item.content,
                time: orderTimes[item.by]!,
            }
        })
        .sort((a, b) => -a.time.localeCompare(b.time)) //按时间倒叙排序
    arr[arr.length - 1].isLast = true
    arr[0].isCurrent = true
    return arr
}

const AMapRef = ref()
const OrderTypeMap = {
    UUPT: 'UU跑腿',
    SELF: '商家自配',
}

const toRefresh = async () => {
    if (timerId) {
        clearTimeout(timerId)
        timerId = null
    }
    const detail = icOrderDetail.value
    //如果订单已送达 则不刷新
    if (detail?.orders?.[0]?.status === 'DELIVERED') {
        return
    }
    await getOrderDetail()
    initRiderPosition(icOrderDetail.value.orders[0])
    //15后刷新一次
    timerId = setTimeout(toRefresh, 15000)
}
</script>

<template>
    <div v-for="(item, index) in icOrderDetail.orders" :key="index">
        <div v-if="item.errorHandler || item.status === 'ERROR'" class="error_bar_container">
            <div class="title">异常信息</div>
            <div class="content">
                <div class="value">异常原因描述：{{ item.statusDesc }}</div>
                <template v-if="item.errorHandler">
                    <div class="value">订单状态重置为：{{ HandleErrorStatus[item.errorHandler.status] }}</div>
                    <div class="value">处理时间：{{ item.times?.errorHandleTime }}</div>
                    <div class="value">处理说明：{{ item.errorHandler.desc }}</div>
                </template>
            </div>
        </div>
        <el-descriptions title="配送信息">
            <el-descriptions-item label="配送方：">{{ OrderTypeMap[item.type] }}</el-descriptions-item>
            <el-descriptions-item label="配送员：">{{ item.courier?.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号：">{{ item.courier?.mobile }}</el-descriptions-item>
            <el-descriptions-item label="取餐号：">{{ item.pickupCode }}</el-descriptions-item>
            <el-descriptions-item label="配送时长：">{{ computedTime(item?.deliverSeconds || 0) }}</el-descriptions-item>
            <el-descriptions-item label="预计送达：">立即送达</el-descriptions-item>
        </el-descriptions>
        <el-descriptions style="margin-top: 30px" title="物流信息"></el-descriptions>
        <div class="logistics_info_container df">
            <div class="left">
                <QTimeLine>
                    <QTimeLineItem v-for="it in times(item)" :key="it.time" :data="it">
                        <!-- <template #title>
                            <div class="df">
                                {{ it.title }}
                                <DeliverErrorDialog
                                    v-if="it.by === 'errorTime' && !item?.errorHandler"
                                    :orderNo="order.no"
                                    style="transform: translateY(-9px)"
                                    @handled="getOrderDetail"
                                />
                            </div>
                        </template> -->
                    </QTimeLineItem>
                </QTimeLine>
            </div>
            <div v-if="showMap(item) && courierLoction.courier.name" class="right mla" style="position: relative">
                <el-button style="position: absolute; top: 20px; left: 20px; z-index: 10" type="danger" @click="toRefresh"> 刷新 </el-button>
                <AMap ref="AMapRef" :coordinates="courierLoction.location!.coordinates" height="520px" width="698px"></AMap>
            </div>
        </div>
        <!-- 分割线 -->
        <el-divider border-style="dashed" />
    </div>
    <el-empty v-if="!icOrderDetail.orders.length"></el-empty>
</template>

<style lang="scss" scoped>
.error_bar_container {
    border-radius: 5px;
    background: rgb(245, 67, 25, 0.05);
    padding-top: 15px;
    padding-left: 18px;
    padding-right: 50rpx;
    padding-bottom: 19px;
    color: #f54319;
    margin-bottom: 22px;

    .title {
        font-weight: bold;
        font-size: 16px;
        margin-bottom: 12px;
    }

    .content {
        display: grid;
        font-size: 14px;
        // 响应式布局
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));

        .value {
            margin-bottom: 10px;
        }
    }
}

.logistics_info_container {
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
            bottom: 40px;
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
}
</style>
