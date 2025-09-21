<script setup lang="ts">
import { ref, PropType, watch, onMounted, onUpdated } from 'vue'
import { useMergeCells, initSpanTag } from '@/composables/useMergeCells'
import { payTypeFn } from '@/composables/usePaymentCn'
import { getAfsOrderInfoStatusCn } from '@/composables/useAfsStatus'
import Decimal from 'decimal.js'
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import { ApiOrder } from '@/views/order/types/order'

const $props = defineProps({
    order: {
        type: Object as PropType<ApiOrder>,
        default() {
            return {}
        },
    },
})
const { divTenThousand } = useConvert()
const stepsActive = ref(0)
const remark = ref<Record<string, any>>({})

watch(
    () => $props.order,
    (val) => {
        initStepsActive()
        if (Object.values(val).length) {
            remark.value = val.shopOrders[0].remark?.items || []
        }
    },
    { immediate: true },
)

/**
 * @description: 步骤条初始化
 * @returns {*}
 */
function initStepsActive() {
    const orderStatus = $props.order.status
    if (orderStatus !== 'PAID') {
        stepsActive.value = 0
        return
    }
    stepsActive.value = 1
    if (!$props.order.shopOrderPackages) return
    const packagesStatus = $props.order.shopOrderPackages[0].status
    if (packagesStatus === 'WAITING_FOR_RECEIVE') {
        stepsActive.value = 2
        return
    }
    if (packagesStatus === 'BUYER_WAITING_FOR_COMMENT' || packagesStatus === 'SYSTEM_WAITING_FOR_COMMENT') {
        stepsActive.value = 3
        return
    }
}
const totalPrice = () => {
    if ($props.order.shopOrders && $props.order.shopOrders[0].shopOrderItems) {
        return $props.order.shopOrders[0].shopOrderItems.reduce((p, item) => {
            return divTenThousand(item.dealPrice).mul(item.num).add(p)
        }, new Decimal(0))
    }
}
</script>

<template>
    <div v-if="$props.order.orderReceiver" class="orderInfo">
        <div class="orderInfo__title">订单状态：{{ getAfsOrderInfoStatusCn($props.order) }}</div>
        <div class="orderInfo__steps">
            <el-steps :active="stepsActive" align-center process-status="finish">
                <el-step title="提交订单" :description="$props.order.createTime" />
                <el-step title="买家已付款" :description="$props.order.orderPayment.payTime" />
                <el-step title="商家发货" :description="$props.order?.shopOrderPackages ? $props.order?.shopOrderPackages[0].deliveryTime : ''" />
                <el-step title="买家收货" :description="$props.order?.shopOrderPackages ? $props.order?.shopOrderPackages[0].confirmTime : ''" />
            </el-steps>
        </div>
        <div class="orderInfo__userInfo">
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">买家信息</div>
                <div>用户昵称：{{ $props.order.orderReceiver.name }}</div>
                <div>买家姓名：{{ $props.order.orderReceiver.name }}</div>
                <div>买家手机号：{{ $props.order.orderReceiver.mobile }}</div>
                <div>收货地址：{{ $props.order.orderReceiver.address }}</div>
                <div style="margin-bottom: 20px; display: flex">
                    <span>买家留言：</span><RemarkView :remark="remark" class="detail__item" style="background-color: #f7f8fa" />
                </div>
            </div>
            <div class="orderInfo__userInfo--right">
                <div class="orderInfo__userInfo--title">订单信息</div>
                <div>订单编号：{{ $props.order.no }}</div>
                <div>创建时间：{{ $props.order.createTime }}</div>
                <div>支付方式：{{ payTypeFn($props.order.orderPayment.type) }}支付</div>
                <div style="margin-bottom: 20px">付款时间：{{ $props.order.orderPayment.createTime }}</div>
            </div>
        </div>
        <el-table
            :cell-style="
                ({ row, column, rowIndex, columnIndex }) => {
                    if (columnIndex === 0)
                        return {
                            borderBottom: 'none',
                        }
                }
            "
            :span-method="useMergeCells"
            :data="$props.order.shopOrders[0].shopOrderItems"
            :get-span-arr="initSpanTag($props.order.shopOrders[0].shopOrderItems, 'no')"
            style="width: 100%"
            calss="orderInfo__tab"
            border
        >
            <el-table-column label="包裹订单" width="160" align="center">
                <template #default>
                    <div>{{ $props.order.no }}</div>
                </template>
            </el-table-column>
            <el-table-column label="商品" width="230" align="center">
                <template #default="{ row }">
                    <div class="orderInfo__tab--goods">
                        <el-image
                            fits="cover"
                            style="width: 70px; height: 70px"
                            shape="square"
                            size="large"
                            :src="row.image"
                            :title="row.productName"
                        />
                        <div class="orderInfo__tab--goods-right">
                            <div class="orderInfo__tab--goods-right-show">{{ row.productName }}</div>
                            <div>{{ row.specs }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="单价&amp;数量" align="center">
                <template #default="{ row }">
                    <div class="orderInfo__tab--price">
                        <div>单价： ￥{{ divTenThousand(row.dealPrice) }}</div>
                        <div>数量：&nbsp;*{{ row.num }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="优惠" align="center">
                <template #default>
                    <div>积分抵扣:--</div>
                    <div>满减优惠:--</div>
                    <div>优惠券优惠:--</div>
                    <div>会员优惠--</div>
                </template>
            </el-table-column>
            <el-table-column label="总价" align="center">
                <template #default="{ row }">
                    <div>总价： ￥{{ divTenThousand(row.dealPrice).mul(row.num) }}</div>
                </template>
            </el-table-column>
        </el-table>
        <div class="orderInfo__footer">
            实收款：<span>{{ totalPrice() }}</span
            >元
        </div>
    </div>
    <el-empty v-else description="暂无数据~" />
</template>

<style scoped lang="scss">
@include b(orderInfo) {
    padding: 0 16px;
    overflow-y: scroll;
    @include e(title) {
        font-size: 18px;
        font-weight: Bold;
        color: #515151;
    }
    @include e(steps) {
        padding: 20px;
        margin: 20px;
        border: 1px solid #d5d5d5;
    }
    @include e(footer) {
        text-align: right;
        & span:only-child {
            font-size: 22px;
            font-weight: bold;
            color: #2e99f3;
        }
    }
    @include e(userInfo) {
        display: flex;
        background: #f7f8fa;
        margin-bottom: 22px;
        padding: 0 30px;
        @include m(left) {
            flex: 0.5;
            & div:nth-of-type(n + 2) {
                margin-bottom: 11px;
            }
        }
        @include m(right) {
            flex: 0.5;
            & div:nth-of-type(n + 2) {
                margin-bottom: 11px;
            }
        }
        @include m(title) {
            font-size: 14px;
            color: #333333;
            font-weight: Bold;
            margin: 17px 0;
        }
    }
    @include e(tab) {
        @include m(goods) {
            display: flex;
            justify-content: space-between;
        }
        @include m(goods-right) {
            font-size: 12px;
            color: #586884;
            width: 132px;
            margin-left: 10px;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-direction: column;
        }
        @include m(goods-right-show) {
            @include utils-ellipsis(2);
        }
        @include m(price) {
            display: flex;
            font-size: 12px;
            color: #50596d;
            justify-content: center;
            align-items: flex-start;
            flex-direction: column;
        }
    }
}

//去除table的每一条数据的下边框
.el-table td {
    border-bottom: none;
}
//去除表格的最下面一行的边框
.tableStyle::before {
    width: 0;
}
//设置表的外边框
.el-table {
    border-radius: 9px;
    border: 1px solid #d5d5d5;
}
.el-table th.is-leaf {
    border-bottom: none;
}
</style>
