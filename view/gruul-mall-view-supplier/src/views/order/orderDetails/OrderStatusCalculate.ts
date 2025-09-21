import { ApiOrder, OrderDiscount, ORDERSTATUS, PACKAGESTATUS, SHOPITEMSTATUS, ShopOrderItem, SHOPORDERSTATUS } from '@views/order/types/order'
import { AFSSTATUS } from '@views/afs/types'
import Decimal from 'decimal.js'
import { afsStatusHandler } from '../hooks/useOrderHeader'

/**
 * 订单状态
 */
enum ItemStatus {
    NONE = '',
    //待发货
    WAITING_DELIVERY = '待发货',
    //部分发货
    PART_DELIVERY = '部分发货',
    //已发货
    DELIVERED = '已发货',
}

/**
 * 售后状态
 */
enum ItemAfsStatus {
    //无售后
    NONE = '',
    //售后中
    ING = '售后中',
    //售后完成
    SUCCESS = '已完成',
}

/**
 * 合并后的订单项
 */
export interface MergedItem {
    merged: ShopOrderItem
    discountAmount: Decimal
    subtotal: Decimal
    discounts: { level: string; name: string; amount: Decimal }[]
    status: ItemStatusResult | Record<string, never>
}

/**
 * 计算结果
 */
export interface ItemStatusResult {
    //订单状态
    status: ItemStatus
    //售后状态
    afsStatus: ItemAfsStatus
    //待发货数量
    waitingDeliveryCount: number
    //已发货数量
    deliveredCount: number
}

export enum OrderStateStatus {
    UNPAID = '待支付',
    UN_DELIVERY = '待发货',
    UN_DELIVERY_PART = '待发货(部分发货)',
    UN_RECEIVE = '待收货',
    UN_EVALUATE = '待评价',
    COMPLETED = '已完成',
    CLOSED = '已关闭',
    TEAMING = '拼团中',
    TEAM_FAIL = '拼团失败',
}

export interface OrderSate {
    status: OrderStateStatus
    activeStep: number
    createTime: string
    payTime: string | undefined
    deliveryTime: string | undefined
    receiveTime: string | undefined
}

export class StatusBuilder {
    orderStatus: keyof typeof ORDERSTATUS | null = null
    shopOrderStatus: keyof typeof SHOPORDERSTATUS | null = null
    itemStatuses: Map<
        string,
        {
            num: number
            status: keyof typeof SHOPITEMSTATUS
            packageStatus: keyof typeof PACKAGESTATUS
            afsStatus: keyof typeof AFSSTATUS
        }
    > = new Map()

    static create(orderStatus: keyof typeof ORDERSTATUS, shopStatus: keyof typeof SHOPORDERSTATUS) {
        return new StatusBuilder().mainStatus(orderStatus).shopStatus(shopStatus)
    }

    /**
     * @description: 订单状态
     * @param status 订单状态
     */
    mainStatus(status: keyof typeof ORDERSTATUS) {
        this.orderStatus = status
        return this
    }

    /**
     * @description: 店铺订单状态
     * @param status 店铺订单状态
     */
    shopStatus(status: keyof typeof SHOPORDERSTATUS) {
        this.shopOrderStatus = status
        return this
    }

    /**
     * @description: 商品项状态
     * @param itemId 商品项id
     * @param status 商品项状态
     */
    itemStatus(
        itemId: string,
        status: {
            num: number
            status: keyof typeof SHOPITEMSTATUS
            packageStatus: keyof typeof PACKAGESTATUS
            afsStatus: keyof typeof AFSSTATUS
        },
    ) {
        this.itemStatuses.set(itemId, status)
        return this
    }

    calculate(): ItemStatusResult {
        const defaultResult: ItemStatusResult = {
            status: ItemStatus.NONE,
            afsStatus: ItemAfsStatus.NONE,
            waitingDeliveryCount: 0,
            deliveredCount: 0,
        }
        if (
            !this.orderStatus ||
            !this.shopOrderStatus ||
            this.orderStatus === 'UNPAID' ||
            this.orderStatus === 'TEAMING' ||
            this.orderStatus !== 'PAID' ||
            this.shopOrderStatus !== 'OK' ||
            this.itemStatuses.size === 0
        ) {
            return defaultResult
        }
        // 其他情况处理完毕 现在处于 订单状态为已支付，店铺订单状态为OK
        //过滤出所有商品项状态是 OK 的 并且做已发货和未发货的数量统计
        const itemStatuses = Array.from(this.itemStatuses.values())
        let totalCount = 0
        let okCount = 0
        let itemAfsStatus = ItemAfsStatus.SUCCESS
        itemStatuses.forEach((item) => {
            const currentNum = item.num
            totalCount += currentNum
            if (packageStatusHandler[item.packageStatus].delivered) {
                defaultResult.deliveredCount += currentNum
            } else {
                defaultResult.waitingDeliveryCount += currentNum
            }

            if (itemStatusHandler[item.status].isOk) {
                okCount += currentNum
            }

            itemAfsStatus =
                itemAfsStatus !== ItemAfsStatus.ING
                    ? afsStatusHandler[item.afsStatus].ing
                        ? ItemAfsStatus.ING
                        : afsStatusHandler[item.afsStatus].closed
                        ? ItemAfsStatus.NONE
                        : itemAfsStatus
                    : itemAfsStatus
        })
        defaultResult.status =
            totalCount === defaultResult.deliveredCount
                ? ItemStatus.DELIVERED
                : totalCount === defaultResult.waitingDeliveryCount
                ? ItemStatus.WAITING_DELIVERY
                : ItemStatus.PART_DELIVERY
        defaultResult.afsStatus = itemAfsStatus
        return defaultResult
    }
}

export interface OrderMergedItems {
    //订单状态统计结果
    state: OrderSate
    //订单总价
    total: Decimal
    //实收款
    actual: Decimal
    //运费
    freight: Decimal
    //平台优惠
    platform: Decimal
    //店铺优惠
    shop: Decimal
    //订单合并后所有的订单项
    items: MergedItem[]
}

function getItemKey(item: ShopOrderItem) {
    return `${item.productId}-${item.skuId}`
}

export function calculate(order: ApiOrder): OrderMergedItems {
    const orderMergedItems: OrderMergedItems = {
        state: {
            status: OrderStateStatus.CLOSED,
            activeStep: 0,
            createTime: order.createTime,
            payTime: undefined,
            deliveryTime: undefined,
            receiveTime: undefined,
        },
        total: new Decimal(0),
        actual: new Decimal(0),
        freight: new Decimal(0),
        platform: new Decimal(0),
        shop: new Decimal(0),
        items: [],
    }
    if (!order || !order.shopOrders) return orderMergedItems
    const shopOrder = order.shopOrders[0]
    const orderStatus = order.status
    const shopOrderStatus = shopOrder.status
    const orderItems = shopOrder.shopOrderItems
    const discountMap = itemDiscountMap(order.orderDiscounts, orderItems)
    const itemsMap = orderItems.reduce(
        (counterMap, item) => {
            const key = getItemKey(item)
            let counter = counterMap.get(key)
            const discounts = discountMap.get(key) || []
            const discountAmount = discounts.reduce((pre, current) => current.amount.add(pre), new Decimal(0))
            if (!counter) {
                counter = {
                    ok: 0,
                    waitingDelivery: 0,
                    waitingReceive: 0,
                    waitingComment: 0,
                    commented: 0,
                    mergedItem: {
                        merged: { ...item, num: 0 },
                        discountAmount: discountAmount,
                        subtotal: discountAmount.mul(-1),
                        discounts: discounts,
                        status: {},
                    },
                    statusBuilder: StatusBuilder.create(orderStatus, shopOrderStatus),
                    sellType: '',
                }
                counterMap.set(key, counter)
            }
            //合并 item 数据
            const currentTotalAmount = new Decimal(item.salePrice).mul(item.num)
            //订单总额 订单实际付款金额 订单运费
            orderMergedItems.total = orderMergedItems.total.add(currentTotalAmount)
            orderMergedItems.actual = orderMergedItems.actual.add(
                new Decimal(item.dealPrice)
                    .mul(item.num)
                    .add(item?.fixPrice || 0)
                    .add(item?.freightPrice || 0),
            )
            orderMergedItems.freight = orderMergedItems.freight.add(item.freightPrice)
            counter.mergedItem.merged.num += item.num
            counter.mergedItem.subtotal = counter.mergedItem.subtotal.add(currentTotalAmount)
            counter.statusBuilder.itemStatus(item.id, item)
            counter.sellType = item.sellType
            if (itemStatusHandler[item.status].isOk) {
                counter.ok++
            }
            //统计商品项各状态数量
            const { delivered, received, commented } = packageStatusHandler[item.packageStatus]
            if (!delivered && item.status !== 'CLOSED') {
                counter.waitingDelivery++
            } else if (!received || item.status === 'CLOSED') {
                counter.waitingReceive++
            } else if (!commented) {
                counter.waitingComment++
            } else {
                counter.commented++
            }
            return counterMap
        },
        new Map<
            string,
            {
                waitingDelivery: number
                waitingReceive: number
                waitingComment: number
                commented: number
                ok: number
                mergedItem: MergedItem
                statusBuilder: StatusBuilder
                sellType: string
            }
        >(),
    )
    //平台店铺折扣统计
    const shopPlatformReduce = Array.from(discountMap.values()).reduce(
        (statistic, discounts) => {
            discounts.forEach((discount) =>
                discount.isPlatform
                    ? (statistic.platform = statistic.platform.add(discount.amount))
                    : (statistic.shop = statistic.shop.add(discount.amount)),
            )
            return statistic
        },
        {
            platform: new Decimal(0),
            shop: new Decimal(0),
        },
    )
    orderMergedItems.platform = shopPlatformReduce.platform
    orderMergedItems.shop = shopPlatformReduce.shop
    //更新映射合并后的商品项 同时更新每项状态
    orderMergedItems.items = Array.from(itemsMap.values()).map((counter) => {
        const mergedItem = counter.mergedItem
        mergedItem.status = counter.statusBuilder.calculate()
        return mergedItem
    })
    //检查订单是否已关闭
    if (!orderStatus || !['UNPAID', 'PAID', 'TEAMING'].includes(orderStatus) || !shopOrderStatus || shopOrderStatus !== 'OK') {
        return orderMergedItems
    }
    //检查订单是否未支付
    if (orderStatus === 'UNPAID') {
        orderMergedItems.state.status = OrderStateStatus.UNPAID
        return orderMergedItems
    }
    //以下为已支付/拼团中 状态
    orderMergedItems.state.activeStep = 1
    orderMergedItems.state.status = OrderStateStatus.UN_DELIVERY
    orderMergedItems.state.payTime = order.orderPayment.payTime
    const { deliverTime, receiveTime } = shopOrder.extra || {}
    //统计所有的状态的商品数量
    const { ok, waitingDelivery, waitingReceive, waitingComment, commented } = Array.from(itemsMap.values()).reduce(
        (counter, item) => {
            counter.ok += item.ok
            counter.waitingDelivery += item.waitingDelivery
            counter.waitingReceive += item.waitingReceive
            counter.waitingComment += item.waitingComment
            counter.commented += item.commented
            return counter
        },
        {
            ok: 0,
            waitingDelivery: 0,
            waitingReceive: 0,
            waitingComment: 0,
            commented: 0,
        },
    )
    //根据包裹状态计算整体订单状态
    if (commented > 0 && commented >= ok) {
        orderMergedItems.state.deliveryTime = deliverTime
        orderMergedItems.state.receiveTime = receiveTime
        orderMergedItems.state.status = OrderStateStatus.COMPLETED
        orderMergedItems.state.activeStep = 3
    } else if (waitingComment > 0) {
        orderMergedItems.state.deliveryTime = deliverTime
        orderMergedItems.state.receiveTime = receiveTime
        orderMergedItems.state.status = OrderStateStatus.UN_EVALUATE
        orderMergedItems.state.activeStep = 3
    } else if (waitingReceive > 0 && waitingDelivery === 0) {
        orderMergedItems.state.deliveryTime = deliverTime
        orderMergedItems.state.status = OrderStateStatus.UN_RECEIVE
        orderMergedItems.state.activeStep = 2
    } else {
        orderMergedItems.state.status = waitingReceive > 0 ? OrderStateStatus.UN_DELIVERY_PART : OrderStateStatus.UN_DELIVERY
        orderMergedItems.state.activeStep = 1
    }
    if (ok === 0) {
        orderMergedItems.state.status = OrderStateStatus.CLOSED
    }
    // 处理订单状态为拼团中和拼团失败的情况
    if (['TEAMING', 'TEAM_FAIL'].includes(orderStatus)) {
        orderMergedItems.state.status = OrderStateStatus[orderStatus]
    }
    return orderMergedItems
}

/**
 * discount 转化为itemDiscountMap
 * Map<`productId-skuId`,{ level: string; name: string; amount: Decimal }>
 */
function itemDiscountMap(orderDiscounts: OrderDiscount[], shopOrderItems: ShopOrderItem[]) {
    if (!shopOrderItems) {
        return new Map<string, { level: string; name: string; amount: Decimal; isPlatform: boolean }[]>()
    }
    if (!orderDiscounts) {
        return new Map<string, { level: string; name: string; amount: Decimal; isPlatform: boolean }[]>()
    }
    const itemIdKeyMap = shopOrderItems.reduce((map, current) => {
        map.set(current.id, `${current.productId}-${current.skuId}`)
        return map
    }, new Map<string, string>())
    const itemKeyDiscountMap = orderDiscounts.reduce((map, current) => {
        const items = current.discountItems
        if (!items) {
            return map
        }
        items.forEach((item) => {
            const itemKey = itemIdKeyMap.get(item.itemId)
            if (!itemKey) {
                return
            }
            const discounts = map.get(itemKey)
            if (!discounts) {
                map.set(
                    itemKey,
                    new Map([
                        [
                            current.id,
                            {
                                level: discountDataByType[current.sourceType].level,
                                name: current.sourceDesc,
                                amount: new Decimal(item.discountAmount),
                                isPlatform: discountDataByType[current.sourceType].isPlatform,
                            },
                        ],
                    ]),
                )
                return
            }
            let currentDiscount = discounts.get(current.id)
            if (!currentDiscount) {
                currentDiscount = {
                    level: discountDataByType[current.sourceType].level,
                    name: current.sourceDesc,
                    amount: new Decimal(0),
                    isPlatform: discountDataByType[current.sourceType].isPlatform,
                }
                discounts.set(current.id, currentDiscount)
            }
            currentDiscount.amount = currentDiscount.amount.add(item.discountAmount)
        })
        return map
    }, new Map<string, Map<string, { level: string; name: string; amount: Decimal; isPlatform: boolean }>>())
    return Array.from(itemKeyDiscountMap.keys()).reduce((map, current) => {
        map.set(current, Array.from(itemKeyDiscountMap.get(current)?.values() || []))
        return map
    }, new Map<string, { level: string; name: string; amount: Decimal; isPlatform: boolean }[]>())
}

const itemStatusHandler: Record<keyof typeof SHOPITEMSTATUS, { isOk: boolean }> = {
    OK: { isOk: true },
    CLOSED: { isOk: false },
}
const packageStatusHandler: Record<
    keyof typeof PACKAGESTATUS,
    {
        delivered: boolean
        received: boolean
        commented: boolean
    }
> = {
    WAITING_FOR_DELIVER: { delivered: false, received: false, commented: false },
    WAITING_FOR_RECEIVE: { delivered: true, received: false, commented: false },
    BUYER_WAITING_FOR_COMMENT: { delivered: true, received: true, commented: false },
    SYSTEM_WAITING_FOR_COMMENT: { delivered: true, received: true, commented: false },
    BUYER_COMMENTED_COMPLETED: { delivered: true, received: true, commented: true },
    SYSTEM_COMMENTED_COMPLETED: { delivered: true, received: true, commented: true },
}

const discountDataByType = {
    PLATFORM_COUPON: {
        isPlatform: true,
        level: '平台',
        name: '平台优惠券',
        price: new Decimal(0),
    },
    SHOP_COUPON: {
        isPlatform: false,
        level: '店铺',
        name: '店铺优惠',
        price: new Decimal(0),
    },
    MEMBER_DEDUCTION: {
        isPlatform: true,
        level: '平台',
        name: '会员折扣',
        price: new Decimal(0),
    },
    FULL_REDUCTION: {
        isPlatform: false,
        level: '店铺',
        name: '满减',
        price: new Decimal(0),
    },
    CONSUMPTION_REBATE: {
        isPlatform: true,
        level: '平台',
        name: '消费返利',
        price: new Decimal(0),
    },
}
