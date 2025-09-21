<script setup lang="ts">
import type { PropType } from 'vue'
// import QAddress from '@/components/q-address/q-address.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetFirstDeliveryPage, doGetLogisticsTrajectoryByWaybillNo } from '@/apis/afs'
import type { ApiAfsOrder, ApiLogistics01 } from '@/views/afs/types/api'

const $props = defineProps({
    order: {
        type: Object as PropType<ApiAfsOrder>,
        default() {
            return {}
        },
    },
})
const $route = useRoute()
const parcel = ref('')
// 第一个包裹的订单快递信息（快递公司，快递单号等）
const logistics = ref<ApiLogistics01>({
    createTime: '',
    deleted: false,
    expressCompanyCode: '',
    expressCompanyName: '',
    expressNo: '',
    id: '',
    orderNo: '',
    receiverAddress: '',
    receiverMobile: '',
    receiverName: '',
    remark: '',
    shopId: '',
    status: 'BUYER_COMMENTED_COMPLETED',
    type: 'EXPRESS',
    updateTime: '',
    version: 0,
    success: true,
})
const currentDeliveryLocation = ref([
    {
        area: '',
        areaName: '',
        context: '',
        ftime: '',
        status: '',
        time: '',
    },
])
//未发货
const notDeliverGoods = ref(false)

watch(
    () => $props.order,
    (val) => {
        if (!val.shopOrderPackages) {
            notDeliverGoods.value = true
            return
        }
        initFirstDeliveryPage()
    },
    {
        immediate: true,
    },
)

async function initFirstDeliveryPage() {
    const { no, shopOrderItemId, packageId, orderNo } = $route.query
    console.log('packageId', packageId)
    const { code, data } = await doGetFirstDeliveryPage($props.order.no, $props.order.shopOrders[0].no, packageId ? (packageId as string) : '')
    if (code !== 200) return ElMessage.error('物流信息获取失败')
    logistics.value = data
    if (!logistics.value) return
    const { expressCompanyCode, expressNo } = logistics.value
    const { code: status, data: res } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, expressNo, $route.query.packageId as string)
    if (status !== 200) return ElMessage.error('物流轨迹获取失败')
    if (!res.status || res.status !== '200') {
        currentDeliveryLocation.value[0].context = res.message
        return
    }
    currentDeliveryLocation.value = res.data
}
</script>

<template>
    <div class="logisticsInfo">
        <!-- <el-row>
            <el-button ref="button" plain @click="parcel = ''">包裹1</el-button>
            <el-button plain @click="parcel = 'Shipment'">未发货</el-button>
        </el-row> -->
        <div v-if="!notDeliverGoods">
            <div class="logisticsInfo__title">物流信息</div>
            <div class="logisticsInfo__text">收货地址：{{ logistics.receiverAddress }}</div>
            <div class="logisticsInfo__text">物流公司：{{ logistics.expressCompanyName }}</div>
            <div class="logisticsInfo__text" style="margin-bottom: 20px">物流单号：{{ logistics.expressNo }}</div>
            <div class="logisticsInfo__divider" />
            <div class="logisticsInfo__title">物流详情</div>
            <el-timeline class="logisticsInfo__timeline">
                <el-timeline-item
                    v-for="(activity, index) in currentDeliveryLocation"
                    :key="index"
                    :timestamp="`${activity.time}`"
                    style="padding-bottom: 42px"
                    :color="index === 0 ? '#409eff' : ' '"
                    class="logisticsInfo__timeline--item"
                >
                    <el-row>
                        <div :style="{ color: index === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--status">
                            {{ activity.status }}
                        </div>
                        <div :style="{ color: index === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--time">
                            {{ activity.context }}
                        </div>
                    </el-row>
                </el-timeline-item>
            </el-timeline>
        </div>
        <div v-else>
            <div class="logisticsInfo__title">物流信息</div>
            <div class="logisticsInfo__text">物流详情:该商品，暂未发货。</div>
        </div>
    </div>
    <!-- <el-empty v-else description="暂无数据~" /> -->
</template>

<style scoped lang="scss">
@include b(logisticsInfo) {
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
        margin-top: 42px;
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
