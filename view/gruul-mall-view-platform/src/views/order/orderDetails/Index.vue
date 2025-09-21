<script setup lang="ts">
import { doGetOrder } from '@/apis/order'
import type { OrderDetail } from '@/apis/order/type/order'

type ActiveName = 'orderInfo' | 'logisticsInfo'

const tabPaneOrderDetails = [
    { label: '订单信息', name: 'orderInfo' },
    { label: '物流信息', name: 'logisticsInfo' },
]
const $route = useRoute()
const reactiveAsyncComponent = computed(() => {
    return {
        orderInfo: defineAsyncComponent(() => import('./components/orderInfo.vue')),
        logisticsInfo: !OrderDetailsData?.value?.icStatus
            ? defineAsyncComponent(() => import('./components/logisticsInfo.vue'))
            : defineAsyncComponent(() => import('./components/SameCityDeliveryLogistics.vue')),
    }
})
const activeName = ref<ActiveName>('orderInfo')
// 订单
const OrderDetailsData = ref<OrderDetail>()

initOrderDetails()

async function initOrderDetails() {
    const { orderNo, shopId } = $route.query
    if (!orderNo) return
    const { code, data } = await doGetOrder({ shopId }, orderNo as string)
    if (code === 200) {
        OrderDetailsData.value = data
    }
}
</script>

<template>
    <div class="tab_container">
        <el-tabs v-model="activeName">
            <el-tab-pane v-for="tabPaneItem in tabPaneOrderDetails" :key="tabPaneItem.label" :label="tabPaneItem.label" :name="tabPaneItem.name" />
        </el-tabs>
    </div>
    <div class="table_container">
        <component :is="reactiveAsyncComponent[activeName]" :order="OrderDetailsData"></component>
    </div>
</template>
