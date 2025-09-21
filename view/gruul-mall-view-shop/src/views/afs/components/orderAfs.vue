<script setup lang="ts">
import { useRoute } from 'vue-router'
import { doGetOrderDetails } from '@/apis/order'
import { ElMessage } from 'element-plus'
import { Order } from '@/apis/order/types'

type ActiveName = 'orderInfo' | 'logisticsInfo' | 'afterSalesInfo'

const tabPaneOrderDetails = [
    { label: '售后信息', name: 'afterSalesInfo' },
    { label: '订单信息', name: 'orderInfo' },
    { label: '物流信息', name: 'logisticsInfo' },
]
const $route = useRoute()
// 订单详情数据
const OrderDetailsData = ref<Order>()
const reactiveAsyncComponent = computed(() => {
    return {
        afterSalesInfo: defineAsyncComponent(() => import('@/views/afs/components/afterSalesInfoHistory.vue')),
        orderInfo: defineAsyncComponent(() => import('@/views/afs/components/orderInfo.vue')),
        logisticsInfo:
            OrderDetailsData?.value?.distributionMode !== 'INTRA_CITY_DISTRIBUTION'
                ? defineAsyncComponent(() => import('@/views/afs/components/logisticsInfo.vue'))
                : defineAsyncComponent(() => import('@/views/order/orderDetails/components/SameCityDeliveryLogistics.vue')),
    }
})
const activeName = ref<ActiveName>('afterSalesInfo')

initPathAfs()
initOrderDetails()

function initPathAfs() {
    if ($route.query.audit) activeName.value = $route.query.audit as ActiveName
}
/**
 * 获取售后订单详情信息
 */
async function initOrderDetails() {
    const { packageId, orderNo } = $route.query
    if (orderNo) {
        const { code, data } = await doGetOrderDetails(orderNo as string, { packageId: packageId || '' })
        if (code !== 200) return ElMessage.error('获取售后订单详情失败')
        OrderDetailsData.value = data
    }
}
</script>

<template>
    <div class="pd">
        <el-tabs v-model="activeName">
            <el-tab-pane v-for="tabPaneItem in tabPaneOrderDetails" :key="tabPaneItem.label" :label="tabPaneItem.label" :name="tabPaneItem.name" />
        </el-tabs>
        <keep-alive>
            <component :is="reactiveAsyncComponent[activeName]" :order="OrderDetailsData" @init-order-details="initOrderDetails" />
        </keep-alive>
    </div>
</template>

<style lang="scss" scoped>
.pd {
    padding-left: 16px;
    padding-right: 16px;
    overflow-y: scroll;
}
</style>
