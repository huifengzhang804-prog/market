<script lang="ts" setup>
import type { TabPaneName } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import AccordionFrom, { SearchFromDataType } from './components/accordionFrom.vue'
import PageManage from '@/components/PageManage/index.vue'
import OrderShipment from './orderShipment/Index.vue'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
// 测试拆分包裹
import SplitTable from './components/order-split-table/SplitTable'
import SplitTableColumn from './components/order-split-table/split-table-column.vue'
import { queryStatus, TabsName } from '@/composables/useOrderStatus'
import DateUtil from '@/utils/date'
import { doGetOrderList, doGetShopDeliveryConfig, doPutCloseShopOrder } from '@/apis/order'
import { useDeliveryOrderList } from '@/store/modules/order'
import { cloneDeep } from 'lodash-es'
import type { ApiOrder, OrderDataType, OrderListSearchData, ShopOrder, ShopOrderItem, ExtraMap, Query, PlatformList } from './types/order'
import { OrderReceiver } from './types/order'
import useOrderHeader from './hooks/useOrderHeader'
import { calculate } from './orderDetails/OrderStatusCalculate'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetExportData } from '@/apis/exportData'
import { WarningFilled } from '@element-plus/icons-vue'
import { PlatformDescriptionMap } from './types'
import CountDown from './components/count-down/index.vue'

const $useShopInfoStore = useShopInfoStore()
const $deliveryOrderList = useDeliveryOrderList()
const $router = useRouter()
const $route = useRoute()
const { divTenThousand } = useConvert()
const routeQuery = $route.query.name
const TabNavArr: Array<any[]> = []
// 第一个Tabs名称
const FirstTabsName = ref('全部订单')
// tab切换部分 当前高亮
const activeTabName = ref(routeQuery ? String(routeQuery) : ' ')
const PageConfig = ref({ size: 10, current: 1, total: 0 })
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
const orderListParams = reactive<Query>({
    status: routeQuery ? String(routeQuery) : '',
    params: {
        orderNo: $route.query.orderNo || '', // 订单号
        buyerNickname: '', // 买家昵称
        startTime: '', // 开始时间
        endTime: '', // 结束时间
        productName: '', // 商品名称
        receiverName: '', // 收货人姓名
        platform: '' as PlatformList | string, //所属渠道
    },
})
const dateTool = new DateUtil()
const isSelfDelivery = ref(true) // 是否自己发货
const platformDescMap: PlatformDescriptionMap = {
    WECHAT_MINI_APP: '小程序',
    WECHAT_MP: '公众号',
    H5: 'H5商城',
    IOS: 'IOS端',
    PC: 'PC商城',
    ANDROID: '安卓端',
    HARMONY: '鸿蒙端',
}
// table 数组
const multiSelect = ref<ApiOrder[]>([])
const tableKey = ref(0)
provide('reloadParentListFn', () => {
    tableKey.value = Date.now()
    initOrderList()
})

initTabs()

/**
/**
 * @description: 循环赋值
 * @returns {*}
 */
function loopAssignment(code: number, data: any) {
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
 * @description: 获取订单列表
 * @returns {*}
 */
async function initOrderList() {
    const params = {
        ...orderListParams.params,
        ...PageConfig.value,
        status: orderListParams.status,
        isMiniAppTip: true,
    }
    const { data, code } = await doGetOrderList(params)
    loopAssignment(code, data)
}
initOrderList()

/**
 * @description: 初始化tab
 * @returns {*}
 */
function initTabs() {
    for (const key in queryStatus) {
        TabNavArr.push([key, queryStatus[key as keyof typeof queryStatus]])
    }
}

const handleTabChange = async (name: TabPaneName) => {
    orderListParams.status = name as string
    initOrderList()
}
/**
 * @description: 近一个月/三个月/全部（下拉选择）
 * @param {*} event
 * @returns {*}
 */
const handleDropdownCommand = ($event: string) => {
    activeTabName.value = ' '
    FirstTabsName.value = $event
    if (FirstTabsName.value === '近一个月订单') {
        const startTime = dateTool.getLastMonth(new Date())
        loadHandleTabClick(startTime)
    } else if (FirstTabsName.value === '近三个月订单') {
        const startTime = dateTool.getLastThreeMonth(new Date())
        loadHandleTabClick(startTime)
    } else {
        // 请求全部订单清空时间
        orderListParams.params.startTime = ''
        orderListParams.params.endTime = ''
        initOrderList()
    }
}
/**
 * @description: Tabs 条件查询
 * @returns {*}
 */
const loadHandleTabClick = async (startTime: string) => {
    const endTime = dateTool.getYMDs(new Date())
    orderListParams.params.startTime = startTime
    orderListParams.params.endTime = endTime
    initOrderList()
}
/**
 * @description: 批量发货跳转路由 WAITING_FOR_DELIVER
 * @returns {*}
 */
const handleDatchDeliverys = () => {
    if (!multiSelect.value.length) return ElMessage.error('请选择商品')
    if (platformDeliverGoods.value) {
        return ElMessage.error({ message: '请在平台端进行发货处理' })
    }
    const payOrder = cloneDeep(multiSelect.value.filter((item) => item.status === 'PAID' && item.shopOrders[0].status === 'OK'))
    let deliverOrder = payOrder
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
            return !item.extra || item.extra.distributionMode !== 'SHOP_STORE'
        })
    if (!deliverOrder.length) {
        return ElMessage.error('暂无需要发货的商品')
    }
    $deliveryOrderList.SET_ORDER_LIST(deliverOrder)
    $router.push({ name: 'deliveryList' })
}
/**
 * @description: 获取子组件搜索表单数据
 * @returns {*}
 */
const GetSearchData = async (params: OrderListSearchData) => {
    orderListParams.params = {
        orderNo: params.no,
        buyerNickname: params.buyerNickname,
        productName: params.productName,
        receiverName: params.receiverName,
        startTime: params.startTime,
        endTime: params.endTime,
        platform: params.platform,
    }

    initOrderList()
}
/**
 * @description: 商品总价计算
 * @param {*} shopOrderItems
 * @returns {string} TotalPrice
 */
const calculateTotalPrice = (shopOrderItems: ShopOrderItem[] = []) => {
    return shopOrderItems.reduce((pre, item) => {
        return Number(
            divTenThousand(item.dealPrice)
                .mul(item.num)
                .add(pre)
                .add(divTenThousand(item.freightPrice || 0)),
        )
    }, 0)
}
/**
 * @description: 批量备注
 * @returns {*}
 */
const handleNote = () => {
    if (multiSelect.value.length) {
        remarkNos.value = multiSelect.value.map((item) => item.shopOrders[0].no)
        noteDialog.value = true
        return
    }
    ElMessage.error('请先选择订单')
}

/**
 * @description: 点击发货
 * @returns {*}
 */
const handleDelivery = (no: string, shopOrderNo: string, row: ApiOrder) => {
    currentNo.value = no
    currentShopOrderNo.value = shopOrderNo
    deliverDialog.value = true
}

/**
 * @description:默认展示第一个商品的单价
 * @param {*} prc
 * @returns {*}
 */
const unitPrice = computed(() => (shopOrderItems: ShopOrderItem[]) => divTenThousand(shopOrderItems[0].dealPrice))
/**
 * @description: 商品总数量展示
 * @param {*} prc
 * @returns {*}
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

const handleRemark = (shopOrder: ShopOrder) => {
    remarkNos.value = [shopOrder.no]
    const remark = shopOrder.remark.supplier || ''
    if (remark) {
        currentInitRemark.value = remark
    }
    noteDialog.value = true
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
    console.log(shopOrderItems)
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

const { getOrderAfterSaleStatus, getDeliveryMode } = useOrderHeader()

const initDeliveryConfig = async () => {
    if ($useShopInfoStore.shopInfo.shopType === 'SELF_OWNED') {
        const { data } = await doGetShopDeliveryConfig()
        if (data?.supplierDeliver) {
            isSelfDelivery.value = data?.supplierDeliver === 'OWN'
        }
    }
}
initDeliveryConfig()
// 导出数据
const exportData = async (SearchFromData: SearchFromDataType) => {
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
        else return ElMessage.success('导出成功')
    } else {
        let param = {
            buyerNickname: SearchFromData.buyerNickname,
            startTime: SearchFromData.clinchTime?.[0],
            endTime: SearchFromData.clinchTime?.[1],
            orderNo: SearchFromData.no,
            platformList: SearchFromData.platform,
            receiverName: SearchFromData.receiverName,
            productName: SearchFromData.productName,
            status: activeTabName.value,
            exportShopOrderIds: '',
        }
        param.status = param.status.trim()
        const { code, data, msg } = await doGetExportData(param)
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else return ElMessage.success('导出成功')
    }
}
const handleSpecs = (shopOrderItem: ShopOrderItem) => {
    if (shopOrderItem.specs) {
        return shopOrderItem.specs.join('、').replace(':-', '-').replace(':', '+')
    } else {
        return ''
    }
}

/**
 * @description: 分页器
 * @param {*} value
 * @returns {*}
 */
const handleSizeChange = (value: number) => {
    PageConfig.value.size = value
    initOrderList()
}
const handleCurrentChange = (value: number) => {
    PageConfig.value.current = value
    initOrderList()
}

/**
 * 需要平台发货
 */
const platformDeliverGoods = computed(() => {
    return isSelfDelivery.value === false && $useShopInfoStore.shopInfo.shopType === 'SELF_OWNED'
})

/**
 * 单个订单是否能发货的判断逻辑
 * @param row 订单信息
 */
const isShowDeliverGoodsBtns = (row: ApiOrder) => {
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
                    shopOrderItem.sellType === 'CONSIGNMENT'
                ) {
                    isShowDeliveryBtn = true
                }
            }
        }
    }
    return isShowDeliveryBtn
}
</script>

<template>
    <div class="search_container">
        <accordion-from :show="false" @search-data="GetSearchData" @export-data="exportData" />
    </div>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="activeTabName" style="margin-top: 15px" @tab-change="handleTabChange">
            <el-tab-pane name=" ">
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
                                <el-dropdown-item v-for="NameItem in TabsName" :key="NameItem" :command="NameItem">{{ NameItem }}</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
            </el-tab-pane>
            <!-- <el-tab-pane v-for="item in TabNavArr" :key="item[0]" :label="item[1]" :name="item[0]"></el-tab-pane> -->
            <el-tab-pane v-for="item in TabNavArr" :key="item[0]" :name="item[0]">
                <template #label>
                    <el-badge v-if="item[0] === 'UN_DELIVERY'" :value="extraMap?.AllDeliveryCount" class="item" :max="99">
                        <span>{{ item[1] }}</span>
                    </el-badge>
                    <span v-else>{{ item[1] }}</span>
                </template>
            </el-tab-pane>
        </el-tabs>
        <el-tooltip class="box-item" effect="dark" placement="bottom-end">
            <template #content>
                1、代销商品的发货和售后事宜均由供应商负责处理<br />
                2、开启平台发货后，【自营】供应商无需再处理【快递/虚拟】发货的订单
            </template>
            <QIcon name="icon-wenhao" class="last" size="18px"></QIcon>
        </el-tooltip>
    </div>
    <div class="handle_container">
        <template v-if="['UN_DELIVERY', ' '].includes(activeTabName)">
            <el-button type="primary" round @click="handleDatchDeliverys"> 批量发货 </el-button>
            <el-button type="primary" round @click="handleNote"> 批量备注 </el-button>
        </template>
        <el-button v-else type="primary" round @click="handleNote">批量备注</el-button>
    </div>
    <div v-if="activeTabName === 'UN_DELIVERY' && +extraMap?.AllDeliveryCount >= 1" class="reminder">
        <el-icon size="16" color="#f72020" style="transform: translateY(3px)"><WarningFilled /></el-icon>
        您共有 <span>{{ extraMap?.AllDeliveryCount || 0 }}</span> 笔待发货的订单，其中小程序端订单有
        <span>{{ extraMap?.miniDeliveryCount || 0 }}</span> 笔，超过 48 小时未发货，将触发支付风险提示，直至暂停小程序交易 ！！！
    </div>

    <split-table v-model:checkedItem="multiSelect" class="orderIndex-table" :data="orderData.records" header-selection>
        <template #noData>
            <div class="no_data">
                <img src="@/assets/images/no_shop_examine.png" alt="" />
                <p class="cont">暂无数据</p>
            </div>
        </template>
        <template #header="{ row }">
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
                    <span>店铺：{{ row.shopOrders[0].shopName }}</span>
                    <span>{{ platformDescMap[row.platform] }}</span>
                    <span>{{ getDeliveryMode(row) }}</span>
                    <span v-if="row.isPriority" style="color: #f54319">优先发货</span>
                </div>
                <RemarkFlag :content="row.shopOrders?.[0].remark.supplier" @seeRemark="handleRemark((row as ApiOrder).shopOrders?.[0])" />
            </div>
        </template>
        <split-table-column prop="name" label="商品" width="450px" :align="'left'">
            <template #default="{ shopOrderItems }">
                <!-- 已拆分数据展示  -->
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
                                    <span v-if="shopOrderItems?.[0].productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                    {{ shopOrderItems?.[0]?.productName }}
                                </p>
                            </el-tooltip>
                            <p v-else class="order-info__name">
                                <span v-if="shopOrderItems?.[0].productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
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
        </split-table-column>
        <split-table-column prop="shopOrders" label="实收金额(元)" :is-mixed="true" :align="'left'" width="170">
            <template #default="{ row }">
                <div class="collection">￥{{ calculateTotalPrice(row.shopOrders?.[0].shopOrderItems) }}</div>
            </template>
        </split-table-column>
        <split-table-column prop="age" label="买家" :is-mixed="true" :align="'left'" width="200">
            <template #default="{ row }">
                <div class="customer">
                    <div>
                        <span class="customer__name">{{ getOrderReceiver(row).name }}</span>
                    </div>
                    <span>{{ getOrderReceiver(row).mobile }}</span>
                </div>
            </template>
        </split-table-column>
        <split-table-column prop="orderStatus" label="订单状态" :is-mixed="true" width="190" :align="'left'">
            <template #default="{ row, shopOrderItems }">
                <div style="font-size: 14px">
                    <span class="after_sales">{{ getOrderAfterSaleStatus(row) ? '售后中' : '' }}</span>
                    <p>
                        {{ calculate(row).state.status
                        }}<span v-if="calculate(row).state.status === '待发货' && num(shopOrderItems) >= 1" style="color: #f54319">{{
                            '(' + row.shopOrders[0].shopOrderItems.reduce((prev: number, cur: ShopOrderItem) => prev + cur.num, 0) + ')'
                        }}</span>
                    </p>
                    <count-down
                        v-if="calculate(row).state.status === '待支付'"
                        :create-time="row.createTime"
                        :pay-timeout="row?.timeout?.payTimeout"
                    />
                </div>
            </template>
        </split-table-column>
        <split-table-column prop="sex" label="操作" width="100" :is-mixed="true" :align="'right'" fixed="right">
            <template #default="{ row, shopOrderItems }">
                <div class="operation">
                    <el-link type="primary" :underline="false" @click="handleCheckDetails(row, shopOrderItems)">查看详情 </el-link>
                    <el-link
                        v-if="row.status === 'UNPAID' && row.shopOrders[0].status === 'OK'"
                        type="danger"
                        :underline="false"
                        @click="handleOrderCommand('close', row)"
                        >关闭订单
                    </el-link>

                    <el-button v-if="isShowDeliverGoodsBtns(row)" type="primary" link @click="handleDelivery(row.no, row.shopOrders[0].no, row)"
                        >发货
                    </el-button>
                </div>
            </template>
        </split-table-column>
    </split-table>
    <PageManage
        :page-size="PageConfig.size"
        :page-num="PageConfig.current"
        :total="PageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <!-- 发货的弹窗部分s -->
    <order-shipment v-model:isShow="deliverDialog" :current-no="currentNo" :current-shop-order-no="currentShopOrderNo" />
    <!-- 备注弹窗s -->
    <remark-popup
        v-model:isShow="noteDialog"
        v-model:ids="remarkNos"
        v-model:remark="currentInitRemark"
        remark-type="GOODS"
        @success="handleRemarkSuccess"
    />
    <!-- 备注弹窗e -->
</template>

<style lang="scss" scoped>
@import '@/assets/css/goods/goods.scss';
// tab栏样式s
.caozuo_btn:hover {
    color: #fff;
    background: #309af3 !important;
}
@include b(orderIndex-table) {
    @include e(img-box) {
        width: 330px;
        display: flex;
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
        justify-content: center;
        align-items: center;
        font-size: 12px;
        color: #000000;
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
@include b(is-complete) {
    background: #eef1f6;
}
@include b(header-table) {
    width: 100%;
    display: flex;
    color: #333333;
    justify-content: space-between;
    font-size: 14px;
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
@include b(money_text) {
    font-size: 12px;
    color: #000000;
}

@include b(avatar_text_box) {
    @include flex(space-between, flex-start);
}
@include b(send) {
    font-size: 14px;
    color: #333333;
}

@include b(el-icon--right-top) {
    margin-left: 5px;
}
@include b(el-icon--right) {
    padding-top: 15px;
    position: absolute;
    left: -20px;
    opacity: 0;
}
@include b(copy) {
    color: #1890ff;
    margin-left: 8px;
    cursor: pointer;
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
@include b(reminder) {
    height: 40px;
    width: 100%;
    line-height: 40px;
    background-color: #fdc3c3;
    font-size: 15px;
    padding: 0 10px;
    margin-top: 13px;
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
.last {
    position: absolute;
    right: 20px;
    top: 40%;
    z-index: 6;
    cursor: pointer;
}
.collection {
    color: red;
}
</style>
<style scoped>
.example-showcase .el-dropdown-link {
    cursor: pointer;
    display: flex;
    align-items: center;
}
</style>
