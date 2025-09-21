<script setup lang="ts">
import type { PropType } from 'vue'
import { ApiOrder, OrderReceiver, ShopOrderItem } from '@/views/order/types/order'
import SplitTable from '@/components/order/order-split-table/SplitTable'
import SplitTableColumn from '@/components/order/order-split-table/split-table-column.vue'

type ApiEnumLogisticCompany = 'id' | 'logisticsCompanyCode' | 'logisticsCompanyName' | 'logisticsCompanyStatus'
export type ApiLogisticCompany = Record<ApiEnumLogisticCompany, string>

const $emit = defineEmits(['filterOrderList'])
const $props = defineProps({
    tabData: { type: Array as PropType<any[]>, default: () => [] },
    companySelectList: { type: Array as PropType<ApiLogisticCompany[]>, default: () => [] },
    showPeople: { type: Boolean, default: true },
})
const { divTenThousand } = useConvert()
/**
 * 默认展示第一个商品的单价
 */
const unitPrice = computed(() => (shopOrderItems: ShopOrderItem[]) => divTenThousand(shopOrderItems[0].dealPrice))

/**
 * 商品总数量展示
 */
const num = computed(() => (shopOrderItems: ShopOrderItem[]) => shopOrderItems.reduce((pre, item) => item.num + pre, 0))

const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}
const handleSpecs = (shopOrderItem: ShopOrderItem) => {
    if (shopOrderItem.specs) {
        return shopOrderItem.specs.join('、').replace(':-', '-').replace(':', '+')
    } else {
        return ''
    }
}
</script>

<template>
    <SplitTable class="orderIndex-table" :data="$props.tabData">
        <template #noData>
            <div class="no_data">
                <img src="@/assets/image/no_shop_examine.png" alt="" />
                <p class="cont">暂无数据</p>
            </div>
        </template>
        <template #header="{ row }">
            <div class="fcenter f1">
                <div>订单号:{{ row.no }}</div>
                <div style="margin-left: 5px; margin-right: 5px">|</div>
                <div>下单时间:{{ row.createTime }}</div>
                <div style="margin-left: 5px; margin-right: 5px">|</div>
                <div>店铺名称:{{ row.shopOrders[0].shopName }}</div>
            </div>
        </template>
        <SplitTableColumn label="商品" width="450px" align="center">
            <template #default="{ shopOrderItems }">
                <!-- 已拆分数据展示 -->
                <div class="orderIndex-table__commodity">
                    <div class="orderIndex-table__img-box">
                        <el-image
                            v-for="item in shopOrderItems.slice(0, 1)"
                            :key="item.id"
                            fits="cover"
                            style="width: 68px; height: 68px"
                            shape="square"
                            size="large"
                            :src="item.image"
                            :title="item.productName"
                        />
                        <span class="order-info">
                            <el-tooltip
                                v-if="shopOrderItems?.[0]?.productName.length >= 44"
                                effect="dark"
                                :content="shopOrderItems?.[0]?.productName"
                                placement="top-start"
                            >
                                <p class="order-info__name">
                                    {{ shopOrderItems?.[0]?.productName }}
                                </p>
                            </el-tooltip>
                            <p v-else class="order-info__name">
                                {{ shopOrderItems?.[0]?.productName }}
                            </p>
                            <el-tooltip
                                v-if="handleSpecs(shopOrderItems?.[0]).length >= 20"
                                effect="dark"
                                :content="handleSpecs(shopOrderItems?.[0])"
                                placement="top-start"
                            >
                                <p class="order-info__spec">{{ handleSpecs(shopOrderItems?.[0]) }}</p></el-tooltip
                            >
                            <p v-else class="order-info__spec">{{ handleSpecs(shopOrderItems?.[0]) }}</p>
                        </span>
                    </div>
                    <div class="orderIndex-table__img-mask">
                        <span>￥{{ unitPrice(shopOrderItems)?.toFixed(2) }}</span>
                        <span style="color: #838383; font-size: 10px">x{{ num(shopOrderItems) }}</span>
                    </div>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn label="待发货数" :is-mixed="true" align="center">
            <template #default="{ row }">
                <div>{{ num(row.shopOrders[0].shopOrderItems) }}</div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn v-if="showPeople" label="收件人信息" :is-mixed="true" width="300">
            <template #default="{ row }">
                <div class="fdc" style="line-height: 18px">
                    <div>{{ getOrderReceiver(row).name }}</div>
                    <div>{{ getOrderReceiver(row).mobile }}</div>
                    <div>{{ getOrderReceiver(row).area?.join('') }}{{ getOrderReceiver(row).address?.replaceAll('~', '') }}</div>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn label="操作" :is-mixed="true" fixed="right" width="120px">
            <template #default="{ row }">
                <el-button class="mla" type="danger" text size="small" @click="$emit('filterOrderList', row)">移除</el-button>
            </template>
        </SplitTableColumn>
    </SplitTable>
</template>

<style scoped lang="scss">
.fcenter {
    padding-left: 20px;
    padding-right: 10px;
}

@include b(orderIndex-table) {
    position: relative;
    overflow-x: auto;
    transition: height 0.5s;
    @include e(top) {
        @include flex(space-between);
        width: 100%;
    }
    @include e(top-time) {
        @include flex;
        & > div:nth-child(2) {
            padding: 0 60px;
        }
    }
    @include e(commodity) {
        width: 100%;
        display: flex;
    }
    @include e(img-box) {
        width: 390px;
        display: flex;
        font-size: 14px;
        align-items: center;
        .el-image {
            flex-shrink: 0;
            margin-right: 10px;
        }
        .order-info {
            display: flex;
            flex-direction: column;
            justify-content: space-evenly;
            height: 100%;
        }
        .order-info__name {
            line-height: 20px;
        }
        .order-info__spec {
            line-height: 20px;
            color: rgb(153, 153, 153);
            font-size: 14px;
            font-weight: 400;
        }
    }

    @include e(img) {
        flex-shrink: 0;
        border-radius: 5px;
        position: relative;
    }

    @include e(img-mask) {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: end;
        font-size: 12px;
        color: #666666;
        margin-left: 20px;
    }
}
</style>
