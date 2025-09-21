<script setup lang="ts">
import { ref, reactive, defineAsyncComponent } from 'vue'
import { useRoute } from 'vue-router'
import { doGetOrderDetails } from '@/apis/order'
import { ElMessage } from 'element-plus'
import { ShopOrderItem } from '@/views/order/types/order'
import type { ApiAfsOrder } from '@/views/afs/types/api'
type ActiveName = 'orderInfo' | 'logisticsInfo' | 'afterSalesInfo'

const tabPaneOrderDetails = [
    { label: '售后信息', name: 'afterSalesInfo' },
    { label: '订单信息', name: 'orderInfo' },
    { label: '物流信息', name: 'logisticsInfo' },
]
const $route = useRoute()
const reactiveAsyncComponent = reactive({
    afterSalesInfo: defineAsyncComponent(() => import('@/views/afs/components/afterSalesInfoHistory.vue')),
    orderInfo: defineAsyncComponent(() => import('@/views/afs/components/orderInfo.vue')),
    logisticsInfo: defineAsyncComponent(() => import('@/views/afs/components/logisticsInfo.vue')),
})
const activeName = ref<ActiveName>('afterSalesInfo')
// 订单详情数据
const OrderDetailsData = ref<ApiAfsOrder>()

initPathAfs()
initOrderDetails()

function initPathAfs() {
    if ($route.query.audit) activeName.value = $route.query.audit as ActiveName
}
/**
 * @description: 获取售后订单详情信息
 * @returns {*}
 */
async function initOrderDetails() {
    const { packageId, orderNo } = $route.query
    if (orderNo) {
        const { code, data } = await doGetOrderDetails(orderNo as string, { packageId: packageId || '', usePackage: true })
        if (code !== 200) return ElMessage.error('获取售后订单详情失败')
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
    <keep-alive>
        <component :is="reactiveAsyncComponent[activeName]" :order="OrderDetailsData" @init-order-details="initOrderDetails" />
    </keep-alive>
</template>
