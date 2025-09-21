<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doGetOrderDetails } from '@/apis/afs'
import { Order } from '@/apis/order/types'

type ActiveName = 'afsOrderInfo' | 'afsLogisticsInfo' | 'afsHistory'

const tabPaneOrderDetails = [
    { label: '售后信息', name: 'afsHistory' },
    { label: '订单信息', name: 'afsOrderInfo' },
    { label: '物流信息', name: 'afsLogisticsInfo' },
]
const $route = useRoute()
// 订单详情数据
const OrderDetailsData = ref<Order>()
const reactiveAsyncComponent = computed(() => {
    return {
        afsOrderInfo: defineAsyncComponent(() => import('@/views/afs/components/afs-order-info.vue')),
        afsLogisticsInfo:
            OrderDetailsData?.value?.distributionMode !== 'INTRA_CITY_DISTRIBUTION'
                ? defineAsyncComponent(() => import('@/views/afs/components/afs-logistics-info.vue'))
                : defineAsyncComponent(() => import('@/views/order/orderDetails/components/SameCityDeliveryLogistics.vue')),
        afsHistory: defineAsyncComponent(() => import('@/views/afs/components/afs-history.vue')),
    }
})
const activeName = ref<ActiveName>('afsHistory')

initOrderDetails()

/**
 * 获取售后订单详情信息
 */
async function initOrderDetails() {
    const { packageId, orderNo } = $route.query
    if (orderNo) {
        const { code, data } = await doGetOrderDetails(orderNo as string, { packageId: packageId || '', usePackage: true })
        if (code !== 200) return ElMessage.error('获取售后订单详情失败')
        if (data) {
            OrderDetailsData.value = data
        }
    }
}
</script>

<template>
    <div style="padding: 16px; overflow-y: scroll">
        <el-tabs v-model="activeName">
            <el-tab-pane v-for="tabPaneItem in tabPaneOrderDetails" :key="tabPaneItem.label" :label="tabPaneItem.label" :name="tabPaneItem.name" />
        </el-tabs>
        <component :is="reactiveAsyncComponent[activeName]" :order="OrderDetailsData" />
    </div>
</template>
