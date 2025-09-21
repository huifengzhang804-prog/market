<script setup lang="ts">
import { ref, reactive, defineAsyncComponent, computed } from 'vue'
import { useRoute } from 'vue-router'
import { doGetOrderList, doGetOrderDetails } from '@/apis/order'
import type { ApiOrder } from '@/views/order/types/order'
type ActiveName = 'orderInfo' | 'logisticsInfo'

// 如果订单没有发货 物流信息页面不可查看隐藏
const tabPaneOrderDetails = ref([{ label: '订单信息', name: 'orderInfo' }])

const $route = useRoute()
const isShipment = computed(() => $route.query.packageId)
const reactiveAsyncComponent = reactive({
    orderInfo: defineAsyncComponent(() => import('./components/orderInfo.vue')),
    logisticsInfo: defineAsyncComponent(() => import('./components/logisticsInfo.vue')),
})
const activeName = ref<ActiveName>('orderInfo')
// 订单详情数据
const orderDetailsData = ref<ApiOrder>()

initOrderDetails()
watch(
    () => isShipment.value,
    (res) => {
        if (res) {
            //如果订单已发货 展示物流信息页面
            tabPaneOrderDetails.value.push({ label: '物流信息', name: 'logisticsInfo' })
        }
    },
    {
        immediate: true,
    },
)

/**
 * @description: 获取订单详情信息 如果已经发货携带包裹id
 * @returns {*}
 */
async function initOrderDetails() {
    if ($route.query.orderNo) {
        const { code, data } = await doGetOrderList({ usePackage: false }, $route.query.orderNo as string)
        if (code !== 200) return
        orderDetailsData.value = data
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
        <component :is="reactiveAsyncComponent[activeName]" :order="orderDetailsData" />
    </keep-alive>
</template>
