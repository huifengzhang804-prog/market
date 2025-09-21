<script lang="ts" setup>
import type { TabPaneName } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import AccordionFrom from './components/accordionFrom.vue'
import PageManage from '@/components/PageManage.vue'
import OrderShipment from './orderShipment/Index.vue'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
// 测试拆分包裹
import SplitTable from './components/order-split-table/SplitTable'
import SplitTableColumn from './components/order-split-table/split-table-column.vue'
import { queryStatus, TabsName } from '@/composables/useOrderStatus'
import DateUtil from '@/utils/date'
import { doGetOrderList, doGetShopDeliveryConfig, doPutCloseShopOrder, postAddContact } from '@/apis/order'
import { useDeliveryOrderList } from '@/store/modules/order'
import { cloneDeep, debounce } from 'lodash-es'
import type { ApiOrder, ExtraMap, OrderDataType, OrderListSearchData, ShopOrderItem } from './types/order'
import { OrderReceiver } from './types/order'
import type { ShopOrder } from '@/apis/order/types'
import { getDeliveryMode, getOrderAfterSaleStatus } from './helper'
import useClipboard from 'vue-clipboard3'
import CountDown from './components/count-down/index.vue'
import { calculate } from './orderDetails/OrderStatusCalculate'
import { ArrowDown, WarningFilled } from '@element-plus/icons-vue'
import { doGetExportData } from '@/apis/exportData'
import type { PlatformDescriptionMap } from './types'
import { L } from '@/utils/types'
import DeliverErrorDialog from './components/DeliverErrorDialog.vue'
import PrintTicketDialog from './components/PrintTicketDialog.vue'
import { downloadAnimation } from '@/utils/useDownloadAnimation'

enum SellTypeEnum {
    CONSIGNMENT = '代销',
    PURCHASE = '采购',
    OWN = '自有',
}

const { toClipboard } = useClipboard()
const $deliveryOrderList = useDeliveryOrderList()
const $router = useRouter()
const $route = useRoute()
const { divTenThousand } = useConvert()
const routeQuery = $route.query.name
const TabNavArr = ref<string[][]>([])
const $useShopInfoStore = useShopInfoStore()

// 第一个Tabs名称
const FirstTabsName = ref('全部订单')
// tab切换部分 当前高亮
const activeTabName = ref(routeQuery ? String(routeQuery) : ' ')
const PageConfig = ref({ size: 10, current: 1, total: 0 })
const isPackUp = ref(false)
// 订单数据
const orderData = reactive<OrderDataType>({
    records: [],
})
let extraMap = ref<ExtraMap>({ AllDeliveryCount: '0', miniDeliveryCount: '0' })
//发货弹窗
const deliverDialog = ref(false)
// 备注弹窗
const noteDialog = ref(false)
// 备注id
const remarkNos = ref<string[]>([])
const currentInitRemark = ref('')
// 当前订单号
const currentNo = ref('')
const currentShopOrderNo = ref('')
const orderListParams = reactive({
    status: routeQuery ? String(routeQuery) : '',
    params: {
        orderNo: $route.query.orderNo || '', // 订单号
        buyerNickname: '', // 买家昵称
        startTime: '', // 开始时间
        endTime: '', // 结束时间
        productName: '', // 商品名称
        receiverName: '', // 收货人姓名
        distributionMode: '', //配送方式
        platform: '', //所属渠道
    },
})
const platformDescMap: PlatformDescriptionMap = {
    WECHAT_MINI_APP: '小程序',
    WECHAT_MP: '公众号',
    H5: 'H5商城',
    IOS: 'IOS端',
    PC: 'PC商城',
    ANDROID: '安卓端',
    HARMONY: '鸿蒙端',
}
const dateTool = new DateUtil()

// table 数组
const multiSelect = ref<ApiOrder[]>([])
const tableKey = ref(0)
provide('reloadParentListFn', () => {
    tableKey.value = Date.now()
    initOrderList()
})

initTabs()

function handleData(code: number, data: L<ApiOrder>) {
    if (code !== 200) {
        ElMessage.error('订单列表获取失败')
        return
    }
    orderData.records = data.records
    PageConfig.value.current = data.current
    PageConfig.value.size = data.size
    PageConfig.value.total = data.total
    extraMap.value = data.extraMap
    return
}

/**
 * 获取订单列表
 */
async function initOrderList() {
    const params = {
        ...orderListParams.params,
        ...PageConfig.value,
        status: orderListParams.status,
        isMiniAppTip: true,
    }
    const { data, code } = await doGetOrderList(params)
    handleData(code, data)
}

initOrderList()

/**
 * 初始化tab
 */
function initTabs() {
    for (const key in queryStatus) {
        TabNavArr.value.push([key, queryStatus[key as keyof typeof queryStatus]])
    }
}

const handleTabChange = async (name: TabPaneName) => {
    orderListParams.status = name as string
    initOrderList()
}
/**
 * 近一个月/三个月/全部（下拉选择）
 * @param {*} event
 */
const handleDropdownCommand = ($event: string) => {
    const beforeActiveTabName = activeTabName.value
    activeTabName.value = ' '
    FirstTabsName.value = $event
    if (FirstTabsName.value === '近一个月订单') {
        const startTime = dateTool.getLastMonth(new Date())
        loadHandleTabClick(startTime, beforeActiveTabName)
    } else if (FirstTabsName.value === '近三个月订单') {
        const startTime = dateTool.getLastThreeMonth(new Date())
        loadHandleTabClick(startTime, beforeActiveTabName)
    } else {
        // 请求全部订单清空时间
        orderListParams.params.startTime = ''
        orderListParams.params.endTime = ''
        if (beforeActiveTabName === activeTabName.value) initOrderList()
    }
}
/**
 * Tabs 条件查询
 */
const loadHandleTabClick = async (startTime: string, beforeActiveTabName: string) => {
    const endTime = dateTool.getYMDs(new Date())
    orderListParams.params.startTime = startTime
    orderListParams.params.endTime = endTime
    if (beforeActiveTabName === activeTabName.value) initOrderList()
}

const isSelfDelivery = ref(true) // 是否自己发货
const initDeliveryConfig = async () => {
    if ($useShopInfoStore.shopInfo.shopType === 'SELF_OWNED') {
        const { data } = await doGetShopDeliveryConfig()
        if (data?.shopDeliver) {
            isSelfDelivery.value = data?.shopDeliver === 'OWN'
        }
    }
}
initDeliveryConfig()
/**
 * 需要平台发货
 */
const platformDeliverGoods = computed(() => {
    return isSelfDelivery.value === false && $useShopInfoStore.shopInfo.shopType === 'SELF_OWNED'
})

/**
 * 获取子组件搜索表单数据
 */
const GetSearchData = async (params: OrderListSearchData) => {
    orderListParams.params = {
        orderNo: params.no,
        buyerNickname: params.buyerNickname,
        productName: params.productName,
        receiverName: params.receiverName,
        startTime: params.startTime,
        endTime: params.endTime,
        distributionMode: params.distributionMode,
        platform: params.platform,
    }

    initOrderList()
}
/**
 * 商品总价计算
 * @param {*} shopOrderItems
 * @returns {string} TotalPrice
 */
const calculateTotalPrice = (shopOrderItems: ShopOrderItem[] = []) => {
    return shopOrderItems
        .reduce((pre, item) => {
            return Number(
                divTenThousand(item.dealPrice)
                    .mul(item.num)
                    .add(pre)
                    .add(divTenThousand(item?.freightPrice || 0)),
            )
        }, 0)
        ?.toFixed(2)
}
/**
 * 批量备注
 */
const handleNote = () => {
    if (multiSelect.value.length) {
        remarkNos.value = multiSelect.value.map((item) => item.shopOrders[0].no)
        noteDialog.value = true
        return
    }
    ElMessage.error('请先选择订单')
}

const currentDeliver = ref<ApiOrder>()
/**
 * 点击发货
 */
const handleDelivery = (no: string, shopOrderNo: string, row: ApiOrder) => {
    currentNo.value = no
    currentShopOrderNo.value = shopOrderNo
    currentDeliver.value = row
    deliverDialog.value = true
}

/**
 * 默认展示第一个商品的单价
 * @param {*} prc
 */
const unitPrice = computed(() => (shopOrderItems: ShopOrderItem[]) => divTenThousand(shopOrderItems[0].dealPrice))
/**
 * 商品总数量展示
 * @param {*} prc
 */
const num = computed(() => (shopOrderItems: ShopOrderItem[]) => shopOrderItems.reduce((pre, item) => item.num + pre, 0))

/**
 * 获取收货人信息
 */
const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}
const handleOrderCommand = (e: string, order: ApiOrder) => {
    switch (e) {
        case 'close':
            closeShopOrder(order)
            break

        default:
            break
    }
}
const closeShopOrder = async (order: ApiOrder) => {
    ElMessageBox.confirm('确定取消该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code } = await doPutCloseShopOrder(order.no, order.shopOrders[0].no)
        if (code !== 200) return ElMessage.error('订单关闭失败')
        ElMessage.success('订单关闭成功')
        order.shopOrders[0].status = 'SELLER_CLOSED'
    })
}
const handleRemark = (shopOrder?: ShopOrder) => {
    if (shopOrder) {
        remarkNos.value = [shopOrder.no]
        currentInitRemark.value = shopOrder.remark?.shop || ''
        noteDialog.value = true
    }
}
const handleRemarkSuccess = () => {
    multiSelect.value = []
    initOrderList()
}
/**
 * 查看详情
 */
const handleCheckDetails = (row: ApiOrder, shopItems: ShopOrderItem[]) => {
    const shopOrderItems = row.shopOrders[0].shopOrderItems
    const packageId = shopOrderItems.find((item) => item.packageId)?.packageId || ''
    const query = { orderNo: row.shopOrders[0].orderNo } as any
    const firstItem = shopItems[0]
    if (shopItems.length === 1) {
        query.shopOrderItemsId = firstItem.id
    }
    if (packageId) {
        query.packageId = packageId
    }
    $router.push({ name: 'detailsIndex', query: query })
}
// 导出数据
const exportData = async (SearchFromData: any, e: MouseEvent) => {
    if (multiSelect.value.length) {
        let exportShopOrderIdList = multiSelect.value.map((item) => item.shopOrders)
        let exportShopOrderId = exportShopOrderIdList.map((ite) => {
            let ites = ite.map((it: { no: string }) => it.no)
            return ites
        })
        const newExportShopOrderIds: string[] = []
        exportShopOrderId.forEach((ids) => {
            ids.forEach((idData) => newExportShopOrderIds.push(idData))
        })
        const exportShopOrderNos = Array.from(new Set(newExportShopOrderIds))
        const { code, data, msg } = await doGetExportData({ exportShopOrderNos })
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else {
            downloadAnimation(e)
            return ElMessage.success('导出成功')
        }
    } else {
        let param = {
            buyerNickname: SearchFromData.buyerNickname,
            distributionMode: SearchFromData.distributionMode,
            startTime: SearchFromData.clinchTime?.[0],
            endTime: SearchFromData.clinchTime?.[1],
            orderNo: SearchFromData.no,
            platform: SearchFromData.platform,
            productName: SearchFromData.productName,
            receiverName: SearchFromData.receiverName,
            status: activeTabName.value,
            exportShopOrderIds: '',
        }
        param.status = param.status.trim()
        const { code, data, msg } = await doGetExportData(param)
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else {
            downloadAnimation(e)
            return ElMessage.success('导出成功')
        }
    }
}
const debounceExportData = debounce(exportData, 1000, {
    leading: true,
})
// 分页方法
const handleSizeChange = (val: number) => {
    PageConfig.value.current = 1
    PageConfig.value.size = val
    initOrderList()
}
const handleCurrentChange = (val: number) => {
    PageConfig.value.current = val
    initOrderList()
}

/**
 * 联系买家
 */
const handleContact = (row: ApiOrder) => {
    postAddContact(row.shopOrders[0].shopId, row.buyerId).then(({ code }) => {
        if (code === 200) {
            $router.push({ path: '/message/customer/service', query: { id: row.buyerId } })
        }
    })
}

const handleSpecs = (shopOrderItem: ShopOrderItem) => {
    if (shopOrderItem.specs) {
        return shopOrderItem.specs.join('、').replace(':-', '-').replace(':', '+')
    } else {
        return ''
    }
}
/**
 * 单个订单是否能发货的判断逻辑
 * @param row 订单信息
 */
const isShowDeliverGoodsBtns = (row: ApiOrder) => {
    if (getDeliveryMode(row) === '到店自提') return false
    const currentOrderStatus = calculate(row).state.status
    const shopOrderItems = row.shopOrders[0].shopOrderItems
    let isShowDeliveryBtn = false
    if (!platformDeliverGoods.value) {
        if (['待发货', '待收货', '已完成', '待发货(部分发货)', '待评价'].includes(currentOrderStatus)) {
            for (let i = 0; i < shopOrderItems.length; i++) {
                const shopOrderItem = shopOrderItems[i]
                if (
                    shopOrderItem.status === 'OK' &&
                    shopOrderItem.packageStatus === 'WAITING_FOR_DELIVER' &&
                    shopOrderItem.sellType !== 'CONSIGNMENT'
                ) {
                    isShowDeliveryBtn = true
                }
            }
        }
    } else if (
        ['待发货', '待收货', '已完成', '待发货(部分发货)', '待评价'].includes(currentOrderStatus) &&
        row?.extra?.distributionMode === 'INTRA_CITY_DISTRIBUTION'
    ) {
        // 如果需要平台发货的话 只有同城配送订单才能进一步发货
        for (let i = 0; i < shopOrderItems.length; i++) {
            const shopOrderItem = shopOrderItems[i]
            if (shopOrderItem.status === 'OK' && shopOrderItem.packageStatus === 'WAITING_FOR_DELIVER' && shopOrderItem.sellType !== 'CONSIGNMENT') {
                isShowDeliveryBtn = true
            }
        }
    }
    return isShowDeliveryBtn
}
const shippingMethodMap = {
    EXPRESS: '快递发货',
    INTRA_CITY_DISTRIBUTION: '同城配送',
    VIRTUAL: '虚拟发货',
}
/**
 * 批量发货逻辑
 * @param command 发货类型
 */
const handleCommand = (command: keyof typeof shippingMethodMap) => {
    if (!multiSelect.value.length) return ElMessage.error('请选择商品')
    const payOrder = cloneDeep(
        multiSelect.value.filter((item) => item.status === 'PAID' && item.shopOrders[0].status === 'OK' && item?.extra?.distributionMode === command),
    )
    const filterPayOrder = payOrder
        .filter((item) => {
            let currentShopOrder = item.shopOrders[0].shopOrderItems
            currentShopOrder = currentShopOrder.filter(
                (ite) =>
                    ite.status === 'OK' &&
                    ite.packageStatus === 'WAITING_FOR_DELIVER' &&
                    (!ite.afsStatus || ite.afsStatus === 'NONE' || ite.afsStatus === 'REFUND_REQUEST' || ite.afsStatus === 'RETURN_REFUND_REQUEST'),
            )
            return currentShopOrder.length
        })
        .filter((item) => {
            return $useShopInfoStore.shopInfo.shopMode === 'O2O' || !item.extra || item.extra.distributionMode !== 'SHOP_STORE'
        })
    const deliverOrder: any[] = []
    for (let i = 0; i < filterPayOrder.length; i++) {
        const payInfo = filterPayOrder[i]
        payInfo.shopOrders.forEach((shopOrder) => {
            shopOrder.shopOrderItems = shopOrder.shopOrderItems.filter((shopOrderItem) => shopOrderItem.sellType !== 'CONSIGNMENT')
        })
        const shopOrders = payInfo.shopOrders.filter((shopOrder) => shopOrder.shopOrderItems.length > 0)
        if (shopOrders.length > 0) {
            deliverOrder.push(payInfo)
        }
    }
    if (!deliverOrder.length) {
        return ElMessage.error(`暂无满足${shippingMethodMap[command]}的商品`)
    }
    console.log('存储的订单', deliverOrder)
    $deliveryOrderList.SET_ORDER_LIST(deliverOrder)
    $router.push({ name: 'deliveryList', query: { type: command } })
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <AccordionFrom :show="false" @search-data="GetSearchData" @search-change="isPackUp = $event" @export-data="debounceExportData" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container" style="position: relative">
        <!-- tab栏切换部分s -->
        <el-tabs v-model="activeTabName" style="margin-top: 12px" @tab-change="handleTabChange">
            <el-tab-pane key=" " name=" ">
                <template #label>
                    <span>{{ FirstTabsName }}</span>
                    <el-icon class="el-icon--right-top">
                        <i-ep-arrow-down />
                    </el-icon>
                    <el-dropdown placement="bottom-end" trigger="click" @command="handleDropdownCommand">
                        <span class="el-dropdown-link" style="height: 40px" @click.stop="() => {}">
                            <el-icon class="el-icon--right">
                                <i-ep-arrow-down />
                            </el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item v-for="NameItem in TabsName" :key="NameItem" :command="NameItem">{{ NameItem }} </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
            </el-tab-pane>
            <el-tab-pane v-for="item in TabNavArr" :key="item[0]" :name="item?.[0]">
                <template #label>
                    <el-badge v-if="item[0] === 'UN_DELIVERY'" :max="99" :value="extraMap?.AllDeliveryCount" class="item">
                        <span>{{ item[1] }}</span>
                    </el-badge>
                    <span v-else>{{ item[1] }}</span>
                </template>
            </el-tab-pane>
        </el-tabs>
        <el-tooltip class="box-item" effect="dark" placement="bottom-end">
            <template #content
                >1、代销商品的发货和售后事宜均由供应商负责处理<br />
                2、到店自提的订单需要在门店移动端上进行【备货和核销】<br />
                3、开启平台发货后，【自营】店铺无需再处理【快递/虚拟】发货的订单<br />
                4、同城订单可商家自配或UU跑腿配送，在待收货（配送期间）暂停售后服务
            </template>
            <QIcon class="last" name="icon-wenhao" size="18px"></QIcon>
        </el-tooltip>
    </div>
    <div class="handle_container">
        <!-- tab栏切换部分e -->
        <template v-if="['UN_DELIVERY', ' '].includes(activeTabName)">
            <el-dropdown :disabled="!multiSelect.length" trigger="click" @command="handleCommand">
                <el-button :disabled="!multiSelect.length" style="margin-right: 10px">
                    批量发货
                    <el-icon>
                        <ArrowDown />
                    </el-icon>
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :disabled="platformDeliverGoods" command="EXPRESS">快递发货</el-dropdown-item>
                        <el-dropdown-item command="INTRA_CITY_DISTRIBUTION">同城配送</el-dropdown-item>
                        <el-dropdown-item :disabled="platformDeliverGoods" command="VIRTUAL">虚拟发货</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>

            <el-button round @click="handleNote"> 批量备注</el-button>
        </template>
        <el-button v-else round @click="handleNote">批量备注</el-button>
        <div v-if="activeTabName === 'UN_DELIVERY' && +extraMap?.AllDeliveryCount >= 1" class="reminder">
            <el-icon color="#f72020" size="16" style="transform: translateY(3px)">
                <WarningFilled />
            </el-icon>
            您共有 <span>{{ extraMap?.AllDeliveryCount || 0 }}</span> 笔待发货的订单，其中小程序端订单有
            <span>{{ extraMap?.miniDeliveryCount || 0 }}</span> 笔，超过 48 小时未发货，将触发支付风险提示，直至暂停小程序交易 ！！！
        </div>
    </div>

    <SplitTable v-model:checkedItem="multiSelect" :data="orderData.records" autoTop class="orderIndex-table" header-selection>
        <template #noData>
            <div class="no_data">
                <img alt="" src="@/assets/images/no_shop_examine.png" />
                <p class="cont">暂无数据</p>
            </div>
        </template>
        <template #header="{ row, shopOrderItems }">
            <div class="header-table">
                <div class="header-table__right">
                    <!-- 秒杀 -->
                    <i v-if="row.type === 'SPIKE'" class="iconfont icon-a-zuhe1273"></i>
                    <!-- 套餐 -->
                    <i v-if="row.type === 'PACKAGE'" class="iconfont icon-a-zuhe1275"></i>
                    <!-- 砍价 -->
                    <i v-if="row.type === 'BARGAIN'" class="iconfont icon-a-zuhe1274"></i>
                    <!-- 满减 -->
                    <i v-if="row.type === ''" class="iconfont icon-a-zuhe1276"></i>
                    <!-- 团购 -->
                    <i v-if="row.type === 'TEAM'" class="iconfont icon-a-zuhe1272"></i>
                    <span>订单号：{{ row.no }}</span>
                    <span>下单时间：{{ row.createTime }}</span>
                    <span>{{ platformDescMap[row.platform] }}</span>
                    <span>{{ getDeliveryMode(row) }}</span>
                    <span v-if="row.isPriority" style="color: #f54319">优先发货</span>
                </div>
                <el-button
                    class="mla"
                    link
                    style="position: sticky; right: 23px; background: #f7f8fa; padding-left: 5px"
                    type="primary"
                    @click="handleCheckDetails(row, shopOrderItems)"
                >
                    查看详情
                </el-button>
                <RemarkFlag :content="row.shopOrders?.[0].remark?.shop" @seeRemark="handleRemark((row as ApiOrder).shopOrders?.[0])" />
            </div>
        </template>
        <SplitTableColumn align="center" label="商品" prop="name" width="450px">
            <template #default="{ shopOrderItems }">
                <!-- 已拆分数据展示 -->
                <div class="orderIndex-table__commodity">
                    <div class="orderIndex-table__img-box">
                        <el-image
                            v-for="item in shopOrderItems.slice(0, 1)"
                            :key="item.id"
                            :src="item.image"
                            :title="item.productName"
                            fits="cover"
                            shape="square"
                            size="large"
                            style="width: 68px; height: 68px"
                        />
                        <span class="order-info">
                            <el-tooltip
                                v-if="shopOrderItems?.[0]?.productName.length >= 44"
                                :content="shopOrderItems?.[0]?.productName"
                                effect="dark"
                                placement="top-start"
                            >
                                <p class="order-info__name">
                                    <span v-if="shopOrderItems[0].sellType === 'CONSIGNMENT'" class="order-info__consignment_sales">{{
                                        SellTypeEnum[shopOrderItems[0].sellType as keyof typeof SellTypeEnum]
                                    }}</span>
                                    <span v-if="shopOrderItems[0].productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                    {{ shopOrderItems?.[0]?.productName }}
                                </p>
                            </el-tooltip>
                            <p v-else class="order-info__name">
                                <span v-if="shopOrderItems[0].sellType === 'CONSIGNMENT'" class="order-info__consignment_sales">{{
                                    SellTypeEnum[shopOrderItems[0].sellType as keyof typeof SellTypeEnum]
                                }}</span>
                                <span v-if="shopOrderItems[0].productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                {{ shopOrderItems?.[0]?.productName }}
                            </p>
                            <el-tooltip
                                v-if="handleSpecs(shopOrderItems?.[0]).length >= 20"
                                :content="handleSpecs(shopOrderItems?.[0])"
                                effect="dark"
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
        <SplitTableColumn :align="'left'" :is-mixed="true" label="实收金额(元)" prop="shopOrders" width="170">
            <template #default="{ row }">
                <div class="collection">￥{{ calculateTotalPrice(row.shopOrders?.[0].shopOrderItems) }}</div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :align="'left'" :is-mixed="true" label="买家" prop="age" width="200">
            <template #default="{ row }">
                <div class="customer">
                    <div>
                        <span class="customer__name">{{ getOrderReceiver(row).name }}</span>
                        <i
                            class="iconfont icon-xiaoxi2 cup"
                            style="font-size: 16px; color: #1bad19; position: relative; top: -2px"
                            @click="handleContact(row)"
                        ></i>
                    </div>
                    <span>{{ getOrderReceiver(row).mobile }}</span>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :align="'left'" :is-mixed="true" label="订单状态" prop="orderStatus" width="190">
            <template #default="{ row, shopOrderItems }">
                <div style="font-size: 14px">
                    <span class="after_sales">{{ getOrderAfterSaleStatus(row) ? '售后中' : '' }}</span>
                    <p v-if="['UN_RECEIVE', ' '].includes(activeTabName)">
                        {{ calculate(row, true).state.status
                        }}<span v-if="calculate(row).state.status === '待发货' && num(shopOrderItems) >= 1" style="color: #f54319">{{
                            '(' + row.shopOrders[0].shopOrderItems.reduce((prev: number, cur: any) => prev + cur.num, 0) + ')'
                        }}</span>
                    </p>
                    <p v-else>
                        {{ calculate(row, false).state.status
                        }}<span v-if="calculate(row).state.status === '待发货' && num(shopOrderItems) >= 1" style="color: #f54319">{{
                            '(' + row.shopOrders[0].shopOrderItems.reduce((prev: number, cur: any) => prev + cur.num, 0) + ')'
                        }}</span>
                    </p>
                    <p
                        v-if="row.icStatus && ['UN_RECEIVE', ' '].includes(activeTabName)"
                        :style="`color:${row.icStatus === 'ERROR' ? '#f54319' : 'inherit'}`"
                    >
                        {{ calculate(row).icText }}
                    </p>
                    <count-down
                        v-if="calculate(row).state.status === '待支付'"
                        :create-time="row.createTime"
                        :pay-timeout="row?.timeout?.payTimeout"
                    />
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :align="'right'" :is-mixed="true" fixed="right" label="操作" prop="sex" width="180">
            <template #default="{ row }">
                <div class="operation">
                    <div class="fcenter">
                        <el-button
                            v-if="row.status === 'UNPAID' && row.shopOrders[0].status === 'OK'"
                            link
                            type="primary"
                            @click="handleOrderCommand('close', row)"
                            >关闭订单
                        </el-button>
                        <el-button v-if="isShowDeliverGoodsBtns(row)" link type="primary" @click="handleDelivery(row.no, row.shopOrders[0].no, row)">
                            发货
                        </el-button>
                        <PrintTicketDialog
                            v-if="
                                ['SHOP_STORE', 'INTRA_CITY_DISTRIBUTION'].includes(row.distributionMode) &&
                                row.status === 'PAID' &&
                                activeTabName !== 'CLOSED'
                            "
                            :linkOptions="row.printLinks"
                            :orderNo="row.no"
                        ></PrintTicketDialog>

                        <DeliverErrorDialog v-if="row.icStatus === 'ERROR'" :orderNo="row.no" @handled="initOrderList"></DeliverErrorDialog>
                    </div>
                </div>
            </template>
        </SplitTableColumn>
    </SplitTable>
    <div v-if="PageConfig.total" class="pagination">
        <PageManage
            :page-num="PageConfig.current"
            :page-size="PageConfig.size"
            :total="PageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <!-- 发货的弹窗部分s -->
    <OrderShipment
        v-if="deliverDialog"
        v-model:isShow="deliverDialog"
        :currentDeliver="currentDeliver"
        :currentNo="currentNo"
        :currentShopOrderNo="currentShopOrderNo"
    />
    <!-- 备注弹窗s -->
    <remark-popup
        v-model:ids="remarkNos"
        v-model:isShow="noteDialog"
        v-model:remark="currentInitRemark"
        remark-type="GOODS"
        @success="handleRemarkSuccess"
    />
    <!-- 备注弹窗e -->
</template>
<style lang="scss" scoped>
@import '@/assets/css/goods/goods.scss';
// tab栏样式s
@include b(shop-name) {
    width: 120px;
    @include utils-ellipsis($line: 1);
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
        justify-content: space-between;
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

.operation {
    display: flex;
    flex-direction: column;
    font-size: 14px;
    letter-spacing: 1px;
    height: 55px;
    justify-content: center;
}

@include b(money_text) {
    font-size: 12px;
    color: #000000;
    @include utils-ellipsis($line: 1);
}

@include b(avatar_text) {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    @include e(bottom) {
        margin-bottom: 5px;
    }
}

@include b(el-icon--right-top) {
    margin-left: 5px;
}

@include b(el-icon--right) {
    top: 10px;
    position: absolute;
    left: -20px;
    opacity: 0;
}

@include b(header-table) {
    width: 100%;
    display: flex;
    color: #333333;
    justify-content: space-between;
    font-size: 14px;
    align-items: center;
    @include e(right) {
        display: flex;
        i {
            position: absolute;
            left: 0;
            top: 0;
        }
        span::after {
            content: '|';
            margin: 0 10px;
            color: #999;
        }
        span:last-child::after {
            content: '';
            margin: 0;
        }
    }
}

.after_sales {
    color: #f54319;
    white-space: nowrap;
}

@include b(order-info) {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    margin: 0 8px;
    word-break: break-all;
    line-height: 1.5;
    overflow: hidden;
    width: 0;

    @include e(name) {
        @include utils-ellipsis(2);
        line-height: 18px;
        color: #333;
        @include e(consignment_sales) {
            display: inline-block;
            width: 30px;
            line-height: 18px;
            font-size: 12px;
            border-radius: 4px;
            background-color: rgba(255, 129, 0, 0.1);
            color: #ff8100;
            text-align: center;
        }
    }
    @include e(spec) {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        color: #999;
    }
}

@include b(collection) {
    font-size: 14px;
    color: #ff860b;
    margin-left: 3px;
}

@include b(customer) {
    width: 100%;
    height: 46px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    font-size: 14px;
    color: #333;
    @include e(name) {
        display: inline-block;
        max-width: 50%;
        @include utils-ellipsis(1);
        color: #555cfd;
    }
}

@include b(copy) {
    color: #1890ff;
    margin-left: 8px;
    cursor: pointer;
}

@include b(reminder) {
    height: 50px;
    line-height: 50px;
    background-color: #fef5f3;
    border: 1px solid #fa9c86;
    font-size: 15px;
    padding: 0 10px;
    margin: 0 24px 16px 16px;
    color: #333;
    span {
        font-size: 16px;
        color: #f72020;
        font-weight: bold;
    }
}

:deep(.el-badge__content, .is-fixed) {
    background-color: transparent;
    color: #f72020;
    font-weight: bold;
    font-size: 14px;
    padding-left: 14px;
    border: transparent;
}

.no_data {
    margin-top: 120px;

    img {
        width: 220px;
        height: 140px;
    }

    .cont {
        color: #737b80;
        text-align: center;
        margin-top: 50px;
    }
}

.example-showcase .el-dropdown-link {
    cursor: pointer;
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
}

.el-popper.is-customized {
    /* Set padding to ensure the height is 32px */
    padding: 6px 12px;
    background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
}

.el-popper.is-customized .el-popper__arrow::before {
    background: linear-gradient(45deg, #b2e68d, #bce689);
    right: 0;
}

/* *---------------- */
@include b(item) {
    line-height: 20px;
    height: 20px;
}

.last {
    position: absolute;
    right: 20px;
    top: 40%;
    z-index: 6;
    cursor: pointer;
}
</style>
