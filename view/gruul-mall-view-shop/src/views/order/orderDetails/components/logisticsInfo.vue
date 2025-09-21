<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doGetDeliveryPackage, doGetLogisticsTrajectoryByWaybillNo } from '@/apis/afs'
import { type ShopOrderItem, type Apipackage, SellTypeEnum } from '@/views/order/types/order'
import type { PropType } from 'vue'
import type { Order } from '@/apis/order/types'

// 添加物流轨迹接口返回类型
interface LogisticsTrajectoryResponse {
    code: number
    msg: string
    data: {
        data: Array<{
            context: string
            status: string
            time: string
        }>
        returnCode?: string
        message?: string
    }
}

const $props = defineProps({
    order: {
        type: Object as PropType<Order>,
        default() {
            return {}
        },
    },
})
const activities = ref<{ context: string; status: string; time: string }[]>([])
const hasBeenShippedOrders = ref<Apipackage[]>([])
const goodsTable = ref<ShopOrderItem[]>([])
const currentIndex = ref(0)
const orderReceiver = computed(() => {
    const order = $props.order
    if (!order || !order.shopOrders) return null
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
})

initDeliveryPageAll()

/**
 * 查询所有已发货的包裹
 */
async function initDeliveryPageAll() {
    const { code, data, msg } = await doGetDeliveryPackage($props.order.no, $props.order.shopOrders[0].no)
    if (code !== 200) return ElMessage.error(msg || '包裹详情获取失败')
    if (!data || !Array.isArray(data) || data.length === 0) {
        return
    }
    hasBeenShippedOrders.value = data
    handleTabClick()
}
/**
 * 查询发货后的轨迹
 * @param {*} orderItem
 */
const handleTabClick = async () => {
    goodsTable.value = []
    const orderItem = hasBeenShippedOrders.value[currentIndex.value]
    $props.order.shopOrders[0].shopOrderItems.forEach((item) => {
        if (item.packageId === +orderItem.id) {
            goodsTable.value.push(item)
        }
    })
    if (orderItem.type === 'WITHOUT') return
    try {
        const { data, code, msg } = (await doGetLogisticsTrajectoryByWaybillNo(
            orderItem.expressCompanyCode,
            orderItem.expressNo,
            $props.order.orderReceiver.mobile.slice(-4),
        )) as LogisticsTrajectoryResponse
        if (code !== 200) return ElMessage.error(msg || '包裹详情获取失败')
        if (!data.data) {
            captureError(data, orderItem)
            return
        }
        activities.value = data.data.reverse()
    } catch (error: any) {
        activities.value = [
            {
                status: '包裹异常',
                time: orderItem?.updateTime,
                context: error.msg,
            },
        ]
    }
}
/**
 * 捕获错误
 */
function captureError(data: any, orderItem: Apipackage) {
    switch (data.returnCode) {
        case '401':
            activities.value = [
                {
                    status: '包裹异常',
                    time: orderItem.updateTime,
                    context: data.message,
                },
            ]
            break
        case '400':
            activities.value = [
                {
                    status: '包裹异常',
                    time: orderItem.updateTime,
                    context: data.message,
                },
            ]
            break

        default:
            activities.value = [
                {
                    status: '包裹异常',
                    time: orderItem.updateTime,
                    context: data.message,
                },
            ]
            break
    }
}
</script>

<template>
    <div style="padding-left: 30px; margin-bottom: 30px">
        <div style="color: #000; font-size: 16px; margin-left: -10px; font-weight: 700; margin-bottom: 10px">收货人信息</div>
        <div>收货人姓名：{{ orderReceiver?.name }}</div>
        <div>联系人电话：{{ orderReceiver?.mobile }}</div>
        <div>收货地址：{{ orderReceiver?.area?.join('') }} {{ orderReceiver?.address }}</div>
    </div>
    <el-tabs v-model="currentIndex" type="card" @tab-change="handleTabClick">
        <el-tab-pane v-for="(orderItem, index) in hasBeenShippedOrders" :key="index" :label="'包裹' + (index + 1)" :name="index">
            <div class="logisticsInfo">
                <div class="logisticsInfo__left">
                    <el-table :data="goodsTable" style="width: 500px; height: 300px" border>
                        <template #empty> <ElTableEmpty /> </template>
                        <el-table-column label="商品 " width="400px">
                            <template #default="{ row }">
                                <div style="display: flex; align-items: center">
                                    <el-image
                                        fits="cover"
                                        style="width: 70px; height: 70px; margin-right: 10px"
                                        shape="square"
                                        size="large"
                                        :src="row.image"
                                        :title="row.productName"
                                    />
                                    <div>
                                        <div style="font-weight: 600">{{ row.productName }}</div>
                                        <div>{{ row.specs?.join(', ') }}</div>
                                        <div>{{ SellTypeEnum[row?.sellType as keyof typeof SellTypeEnum] }}</div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="发货数" align="center">
                            <template #default="{ row }">{{ row.num }} </template>
                        </el-table-column>
                    </el-table>

                    <div v-if="orderItem.type !== 'WITHOUT'" style="margin-top: 20px">
                        <div class="logisticsInfo__title">物流详情</div>
                        <el-scrollbar ref="scrollbarRef" height="500px">
                            <el-timeline class="logisticsInfo__timeline">
                                <el-timeline-item
                                    v-for="(activity, i) in activities"
                                    :key="i"
                                    :timestamp="`${activity.context}`"
                                    style="padding-bottom: 42px"
                                    :color="i === 0 ? '#409eff' : ' '"
                                    class="logisticsInfo__timeline--item"
                                >
                                    <el-row>
                                        <div :style="{ color: i === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--status">
                                            {{ activity.status }}
                                        </div>
                                        <div :style="{ color: i === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--time">
                                            {{ activity.time }}
                                        </div>
                                    </el-row>
                                </el-timeline-item>
                            </el-timeline>
                        </el-scrollbar>
                    </div>
                </div>
                <div class="logisticsInfo__right">
                    <div class="logisticsInfo__title">物流信息</div>
                    <div v-if="orderItem.type !== 'WITHOUT'">
                        <div class="logisticsInfo__text">发货方：{{ hasBeenShippedOrders[currentIndex].deliverShopName }}</div>
                        <!-- <div class="logisticsInfo__text">收货地址：{{ hasBeenShippedOrders[currentIndex].receiverAddress }}</div> -->
                        <div class="logisticsInfo__text">物流公司：{{ hasBeenShippedOrders[currentIndex].expressCompanyName }}</div>
                        <div class="logisticsInfo__text">物流单号：{{ hasBeenShippedOrders[currentIndex].expressNo }}</div>
                    </div>
                    <div v-else style="text-align: center; height: 100px; line-height: 100px">无需物流</div>
                </div>
            </div>
        </el-tab-pane>
    </el-tabs>
</template>

<style scoped lang="scss">
@include b(logisticsInfo) {
    display: flex;
    justify-content: space-between;
    @include e(left) {
        flex: 3;
    }
    @include e(right) {
        flex: 2;
    }
    @include e(timeline-con) {
        max-height: 500px;
        overflow: auto;
    }
    @include e(title) {
        font-size: 14px;
        color: #333333;
        font-weight: Bold;
        margin: 17px 0;
    }
    @include e(text) {
        margin-bottom: 11px;
    }
    @include e(timeline) {
        margin-top: 20px;
        & li:nth-child(1) {
            & :deep(.el-timeline-item__timestamp) {
                color: #000;
            }
        }
        @include m(status) {
            font-size: 13px;
            font-weight: bold;
            margin-right: 10px;
            color: #838383;
        }
        @include m(time) {
            color: #838383;
        }
    }
    @include e(divider) {
        height: 4px;
        margin: 0 -15px;
        background: #f2f2f2;
    }
}
</style>
