<script lang="ts" setup>
import type { PropType } from 'vue'
import { initSpanTag } from '@/composables/useMergeCells'
import { payTypeFn } from '@/composables/usePaymentCn'
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import { distributionModeStatus, getAfsOrderInfoStatusCn } from '@/composables/useAfsStatus'
import Decimal from 'decimal.js'
import { SellTypeEnum } from '../types'
import { ApiOrder, discountTypeConf, OrderDiscountDetail } from '@/views/order/types/order'
import { OrderDiscount, RemarkItem } from '@/apis/order/types'

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

/**
 * 步骤条初始化
 */
function initStepsActive() {
    const orderStatus = $props.order.status
    if (orderStatus !== 'PAID') {
        stepsActive.value = 0
        return
    }
    stepsActive.value = 1
    if (!$props.order.shopOrderPackages) return
    const packagesStatus = $props.order?.shopOrderPackages?.[0]?.status
    if (packagesStatus === 'WAITING_FOR_RECEIVE') {
        stepsActive.value = 2
        return
    }
    if (packagesStatus === 'BUYER_WAITING_FOR_COMMENT' || packagesStatus === 'SYSTEM_WAITING_FOR_COMMENT') {
        stepsActive.value = 3
        return
    }
}

initStepsActive()

const remark = ref<RemarkItem[]>([])

const freightPrice = ref(0)
const discounts = ref<OrderDiscountDetail[]>([])
//优惠统计
const statistics = ref({
    //总优惠
    totalDiscount: new Decimal(0),
    // 店铺优惠
    shopDiscount: new Decimal(0),
    // 其它优惠项
    otherDiscount: [] as OrderDiscountDetail[],
})
/**
 * 优惠后的实付金额
 */
const amountRealPay = ref(0)
watch(
    () => $props.order,
    (val) => {
        if (Object.values(val).length) {
            initStepsActive()
            remark.value = val.shopOrders[0]?.remark?.items || []
            $props.order.orderDiscounts?.forEach((item: OrderDiscount) => {
                discounts.value.push({
                    ...discountTypeConf[item.sourceType],
                    price: new Decimal(item.sourceAmount),
                })
            })
            let totalDiscount = new Decimal(0)
            let shopDiscount = new Decimal(0)
            const otherDiscount = [] as OrderDiscountDetail[]
            discounts.value.forEach((item) => {
                totalDiscount = totalDiscount.add(item.price)
                if (item.isShopDiscount) {
                    shopDiscount = shopDiscount.add(item.price)
                } else {
                    otherDiscount.push(item)
                }
            })
            statistics.value = {
                totalDiscount,
                shopDiscount,
                otherDiscount: otherDiscount.sort((a, b) => a.sort - b.sort),
            }
            freightPrice.value = $props.order.shopOrders
                .flatMap((shopOrder) => shopOrder.shopOrderItems)
                .map((shopOrderItem) => new Decimal(shopOrderItem.freightPrice))
                .reduce((pre, current) => current.add(pre))
                .toNumber()
            const amount = val.shopOrders
                .flatMap((shopOrder) => shopOrder.shopOrderItems)
                .reduce((p, item) => {
                    return p.add(new Decimal(item.dealPrice).mul(new Decimal(item.num)).add(item.fixPrice))
                }, new Decimal(0))
            const totalPrice = new Decimal(amount)
            const totalPrice_ = totalPrice.toNumber() < 0 ? new Decimal(0) : totalPrice // 运费不参与优惠计算 最后相加
            amountRealPay.value = totalPrice_.add(freightPrice.value).toNumber()
        } else {
            amountRealPay.value = 0
        }
    },
    { immediate: true },
)
</script>

<template>
    <div v-if="$props.order.orderReceiver" class="orderInfo">
        <div class="orderInfo__title">订单状态：{{ getAfsOrderInfoStatusCn($props.order) }}</div>
        <div class="orderInfo__steps">
            <el-steps :active="stepsActive" align-center process-status="finish">
                <el-step :description="$props.order.createTime" title="提交订单" />
                <el-step :description="$props.order.orderPayment.payTime" title="买家已付款" />
                <el-step :description="$props.order?.shopOrderPackages ? $props.order?.shopOrderPackages?.[0]?.deliveryTime : ''" title="商家发货" />
                <el-step :description="$props.order?.shopOrderPackages ? $props.order?.shopOrderPackages?.[0]?.confirmTime : ''" title="买家收货" />
            </el-steps>
        </div>
        <div class="orderInfo__userInfo">
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">订单信息</div>
                <div>订单号：{{ $props.order.no }}</div>
                <div>下单时间：{{ $props.order.createTime }}</div>
                <div>支付时间：{{ $props.order.orderPayment?.createTime }}</div>
                <div>支付方式：{{ payTypeFn($props.order.orderPayment?.type) }}支付</div>
                <div>配送方式：{{ distributionModeStatus[$props.order.extra?.distributionMode] }}</div>
            </div>
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">收货人信息</div>
                <div>收货人姓名：{{ $props.order.orderReceiver.name }}</div>
                <div>联系人电话：{{ $props.order.orderReceiver.mobile }}</div>
                <div>收货地址：{{ $props.order.orderReceiver.area?.join('') }} {{ $props.order.orderReceiver.address }}</div>
                <RemarkView :remark="remark" class="detail__item" />
                <!-- <div style="margin-bottom: 20px">买家留言：-</div> -->
            </div>
            <div class="orderInfo__userInfo--right">
                <div class="orderInfo__userInfo--title">买家信息</div>
                <div>用户昵称：{{ $props.order?.buyerNickname || '无' }}</div>
            </div>
        </div>
        <el-table
            :data="$props.order.shopOrders[0].shopOrderItems"
            :get-span-arr="initSpanTag($props.order.shopOrders[0].shopOrderItems, 'no')"
            border
            calss="orderInfo__tab"
            style="width: 100%"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <!-- <el-table-column label="包裹订单" width="160" align="center">
<template #default>
<div>{{ $props.order.no }}</div>
</template>
</el-table-column> -->
            <el-table-column align="center" label="商品">
                <template #default="{ row }">
                    <div class="orderInfo__tab--goods">
                        <el-image
                            :src="row.image"
                            :title="row.productName"
                            fits="cover"
                            shape="square"
                            size="large"
                            style="width: 70px; height: 70px"
                        />
                        <div class="orderInfo__tab--goods-right">
                            <div class="orderInfo__tab--goods-right-show">{{ row.productName }}</div>
                            <div>{{ row.specs?.join(',') }}</div>
                            <div>{{ SellTypeEnum[row?.merged?.sellType] }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column align="center" label="单价&amp;数量">
                <template #default="{ row }">
                    <div class="orderInfo__tab--price">
                        <div>单价： ￥{{ divTenThousand(row.dealPrice) }}</div>
                        <div>数量：&nbsp;*{{ row.num }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column align="center" label="优惠">
                <template #default>
                    <div>运费:{{ divTenThousand(freightPrice) }}</div>
                    <!--   店铺优惠    -->
                    <div v-show="statistics.shopDiscount.cmp(0) > 0">店铺优惠:{{ divTenThousand(statistics.shopDiscount) }}</div>
                    <!--   其它折扣项    -->
                    <div v-for="(item, idx) in statistics.otherDiscount" :key="idx">{{ item.name }}:{{ divTenThousand(item.price) }}</div>
                    <!--   优惠折扣项    -->
                    <div v-show="statistics.totalDiscount.cmp(0) > 0">总优惠:{{ divTenThousand(statistics.totalDiscount) }}</div>
                </template>
            </el-table-column>
            <el-table-column align="center" label="总价">
                <template #default="{ row }">
                    <div>总价： ￥{{ divTenThousand(row.dealPrice).mul(row.num) }}</div>
                </template>
            </el-table-column>
        </el-table>
        <div class="orderInfo__footer">
            实收款：<span>{{ divTenThousand(amountRealPay) }}</span
            >元
        </div>
    </div>
    <el-empty v-else description="暂无数据~" />
</template>

<style lang="scss" scoped>
@include b(orderInfo) {
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
