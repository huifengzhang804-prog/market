<template>
    <div class="tab_container">
        <el-tabs v-model="activeName">
            <el-tab-pane label="订单信息" name="basic"></el-tab-pane>
            <el-tab-pane v-if="deliveryCount && deliveryCount > 0" label="物流信息" name="delivery"></el-tab-pane>
        </el-tabs>
    </div>
    <div class="content_container" style="overflow-y: scroll">
        <component :is="componentMap[activeName]" :key="key" :order="orderDetails" :reload="initOrderDetails" />
    </div>
</template>

<script lang="ts" setup>
import { getPurchaseOrderDetails } from '@/apis/order/purchase'
import { ApiPurchaseOrder } from './components/split-table/order'

const activeName = ref<'basic' | 'delivery'>('basic')
const componentMap = {
    basic: defineAsyncComponent(() => import('./components/details-page/basic.vue')),
    delivery: defineAsyncComponent(() => import('./components/details-page/delivery.vue')),
}
const key = ref(0)
const $route = useRoute()
const orderDetails = ref<ApiPurchaseOrder>()
async function initOrderDetails() {
    if ($route.query.orderNo) {
        const { code, data } = await getPurchaseOrderDetails($route.query.orderNo as string)
        orderDetails.value = data
        key.value = Date.now()
    }
}
const deliveryCount = computed(() => orderDetails?.value?.orderItems?.filter((item) => item.packageStatus !== 'WAITING_FOR_DELIVER').length)
onMounted(() => initOrderDetails())
</script>
