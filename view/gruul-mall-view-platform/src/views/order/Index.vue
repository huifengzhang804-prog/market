<script lang="ts" setup>
import { useRouter } from 'vue-router'
import SplitTable from '@/components/order/order-split-table/SplitTable'
import SplitTableColumn from '@/components/order/order-split-table/split-table-column.vue'
import CountDown from '@/components/order/count-down/index.vue'
import { getDeliveryMode, getOrderAfterSaleStatus } from './helper'
import { calculate } from './orderDetails/OrderStatusCalculate'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import AccordionFrom from '@/components/order/accordion-from.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { queryStatus, TabsName } from '@/composables/useOrderStatus'
import DateUtil from '@/utils/date'
import OrderShipment from './orderShipment/Index.vue'
import { doGetOrderList, doPutCloseShopOrder, importNeedDeliveryOrders } from '@/apis/order'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import type { ApiOrder, OrderReceiver, ShopOrderItem } from './types/order'
import { doGetShopDeliveryConfig, postAddContact } from '@/apis/set/platformDelivery'
import { useDeliveryOrderList } from '@/store/modules/order'
import { doGetExportData } from '@apis/exportData'
import type { PlatformDescriptionMap } from './types'
import { ShopOrder } from '@/apis/order/types'

enum SellTypeEnum {
    CONSIGNMENT = '代销',
    PURCHASE = '采购',
    OWN = '自有',
}

const $deliveryOrderList = useDeliveryOrderList()
const $router = useRouter()
const { divTenThousand } = useConvert()
const FirstTabsName = ref('全部订单')
const TabNavArr: any[] = []
const dropdownRef = ref()
const isDropdown = ref(false)
const searchStasusData = ref<{ title: string; name: string }[]>([])
// tab切换部分 当前高亮
const activeTabName = ref(' ')
// 当前备注的订单号ids
const currentRemarkIds = ref<string[]>([])
const remarkDialog = ref(false)
// tab表格
const orderInfoArr = ref<ApiOrder[]>([])
const multiSelect = ref<ApiOrder[]>([])
// 备注文本
const textarea = ref('')
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const tableStyleUp = ref(false)
const dateTool = new DateUtil()
// 条件查询
const queryConditionsTabs = reactive({
    params: {
        buyerNickname: '',
        receiverName: '',
        orderNo: '',
        productName: '',
        startTime: '', // 开始时间
        endTime: '', // 结束时间}
        distributionMode: '', // 配送方式
        shopType: '', // 店铺类型
        shopId: '',
        platform: '',
    },
})
const deliveryReactive = reactive({
    deliverDialog: false,
    currentNo: '',
    currentShopOrderNo: '',
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

initOrderList()
initTabs()
initSearchStasusData()

/**
 * 获取订单列表 UN_RECEIVE
 */
function initOrderList() {
    nextTick(() => {
        const { params } = queryConditionsTabs
        const query = {
            ...pageConfig,
            ...params,
            status: activeTabName.value,
            isMiniAppTip: true,
            shopIds: [] as Long[],
        }
        if (query.shopId) {
            query.shopIds = [query.shopId]
        }
        doGetOrderList<ApiOrder>(query).then(({ data }) => {
            pageConfig.current = data.current
            pageConfig.size = data.size
            pageConfig.total = data.total
            orderInfoArr.value = doGetFlatOrderList(data.records)
        })
    })
}

provide('reloadParentListFn', initOrderList)
/**
 * 根据商家信息拆分订单
 * @param { ApiOrder[] } 请求订单列表
 */
const doGetFlatOrderList = (orderList: ApiOrder[] = []) => {
    const newOrderList: ApiOrder[] = []
    orderList.forEach((order) => {
        order.shopOrders?.forEach((shopOrder) => {
            newOrderList.push({ ...order, shopOrders: [shopOrder], checked: false })
        })
    })
    return newOrderList
}

/**
 * 初始化tab
 */
function initTabs() {
    for (const [key, value] of Object.entries(queryStatus)) {
        TabNavArr.push({
            key,
            value,
        })
    }
}

function initSearchStasusData() {
    const newTabNavArr = TabNavArr.map((item) => ({ title: item.value, name: item.key }))
    newTabNavArr.unshift({ title: '全部订单', name: ' ' })
    searchStasusData.value = newTabNavArr
}

/**
 * 近一个月/三个月/全部（下拉选择）
 * @param {*} event
 */
const handleDropdownCommand = ($event: string) => {
    FirstTabsName.value = $event
    handleStartTimeAndstartEnd()
}

/**
 * 处理开始时间开始结束
 */
function handleStartTimeAndstartEnd() {
    if (FirstTabsName.value === '近一个月订单') {
        const startTime = dateTool.getLastMonth()
        loadHandleTabClick(startTime)
    } else if (FirstTabsName.value === '近三个月订单') {
        const startTime = dateTool.getLastThreeMonth()
        loadHandleTabClick(startTime)
    } else {
        queryConditionsTabs.params.startTime = ''
        queryConditionsTabs.params.endTime = ''
        initOrderList()
    }
}

/**
 * Tabs 条件查询
 */
const loadHandleTabClick = async (startTime: string) => {
    const endTime = dateTool.getYMDs()
    queryConditionsTabs.params.startTime = startTime
    queryConditionsTabs.params.endTime = endTime
    initOrderList()
}
/**
 * tab栏点击
 * @param {*} tab
 * @param {*} event
 */
const handleTabClick = async () => {
    if (activeTabName.value !== ' ') {
        queryConditionsTabs.params.endTime = ''
        queryConditionsTabs.params.startTime = ''
        initOrderList()
        return
    } else {
        handleDropdownCommand(FirstTabsName.value)
    }
}
/**
 * 全部/季度/月份下拉
 */
const handleOpen = () => {
    isDropdown.value = !isDropdown.value
    if (isDropdown.value) {
        dropdownRef.value.handleOpen()
    } else {
        dropdownRef.value.handleClose()
    }
}
/**
 * dealPrice
 * 默认展示第一个商品的单价
 */
const unitPrice = computed(() => (shopOrderItems: ShopOrderItem[]) => divTenThousand(shopOrderItems[0].dealPrice))
/**
 * 商品总数量展示
 * @param {*} prc
 */
const num = computed(() => (shopOrderItems: ShopOrderItem[]) => shopOrderItems.reduce((pre, item) => item.num + pre, 0))
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initOrderList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initOrderList()
}
/**
 * 模糊搜索
 * @param {*} params
 */
const handleSearchData = (params: any) => {
    queryConditionsTabs.params = params
    initOrderList()
}
// 批量备注
const handleRemarks = () => {
    if (!multiSelect.value.length) return ElMessage.error('请先选择订单')
    currentRemarkIds.value = multiSelect.value.map((item) => item.shopOrders[0].no)
    remarkDialog.value = true
}
// 单个备注
const handleRemark = (row: ShopOrder) => {
    currentRemarkIds.value = [row.no]
    textarea.value = row.remark?.platform || ''
    remarkDialog.value = true
}
const handleSuccess = () => {
    multiSelect.value = []
    initOrderList()
}

/**
 * 获取收货人信息
 */
const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}

/**
 * 商品总价计算
 * @param {*} shopOrderItems
 * @returns {string} TotalPrice
 */
const calculateTotalPrice = (shopOrderItems: ShopOrderItem[]) => {
    return shopOrderItems.reduce((pre, item) => {
        return Number(
            divTenThousand(item.dealPrice)
                .mul(item.num)
                .add(pre)
                .add(divTenThousand(item?.freightPrice || 0)),
        )
    }, 0)
}

const isPlatformDelivery = reactive({
    shop: false,
    supplier: false,
})
const initialDeliveryConfig = async () => {
    const { data } = (await doGetShopDeliveryConfig()) as any
    if (data?.shopDeliver === 'PLATFORM') isPlatformDelivery.shop = true
    if (data?.supplierDeliver === 'PLATFORM') isPlatformDelivery.supplier = true
}
initialDeliveryConfig()
const filterDeliveryDelieryBtn = (row: ApiOrder) => {
    if (['SHOP_STORE', 'INTRA_CITY_DISTRIBUTION'].includes(row?.extra?.distributionMode)) return false
    if (!['待发货', '待发货(部分发货)'].includes(calculate(row).state.status)) {
        return false
    }
    const shopOrderItems = row?.shopOrders?.[0]?.shopOrderItems || []
    if (isPlatformDelivery.supplier) {
        const consignmentLength = shopOrderItems?.filter((item) => item.sellType === 'CONSIGNMENT' && item.supplierShopType === 'SELF_OWNED').length
        if (consignmentLength > 0) {
            return true
        }
    }
    if (isPlatformDelivery.shop) {
        const notConsignmentLength = shopOrderItems?.filter((item) => item.sellType !== 'CONSIGNMENT').length
        const isSelfOwned = row?.shopOrders?.[0]?.shopType === 'SELF_OWNED'
        if (notConsignmentLength > 0 && isSelfOwned) return true
    }
    return false
}
const showDeliveryBtn = (row: ApiOrder) => {
    return filterDeliveryDelieryBtn(row)
}
const shippingMethodMap = {
    EXPRESS: '快递发货',
    VIRTUAL: '虚拟发货',
}
/**
 * 批量发货逻辑
 * @param command 发货类型
 */
const handleCommand = async (command: keyof typeof shippingMethodMap) => {
    const filterCanDeliveryList = multiSelect.value.filter((item) => filterDeliveryDelieryBtn(item))
    if (!filterCanDeliveryList.length) return ElMessage.error({ message: '请选择可发货的订单数据' })
    const orderNos = filterCanDeliveryList.map((item) => item.no)
    const { data } = await importNeedDeliveryOrders(orderNos)
    Object.keys(data).forEach((key) => {
        data[key as keyof typeof data] = data[key as keyof typeof data].filter((item) => item.extra.distributionMode === command)
        data[key as keyof typeof data].forEach((item) => {
            item.expressCompany = {
                logisticsCompanyCode: '',
                logisticsCompanyName: '',
                expressCompanyCode: '',
                expressCompanyName: '',
                expressNo: '',
            }
        })
    })
    if (Object.keys(data).every((key) => data[key as keyof typeof data].length === 0)) {
        return ElMessage.error(`暂无满足${shippingMethodMap[command]}的商品`)
    }
    $deliveryOrderList.SET_ORDER_LIST(data)
    $router.push({ name: 'deliveryList', query: { type: command } })
}
const currentDeliver = ref<ApiOrder>()
const handleDelivery = (no: string, shopOrderNo: string, row: ApiOrder) => {
    deliveryReactive.currentNo = no
    deliveryReactive.currentShopOrderNo = shopOrderNo
    currentDeliver.value = row
    deliveryReactive.deliverDialog = true
}
/**
 * 导出数据
 * @param SearchFromData.orderNo 订单号
 * @param SearchFromData.buyerNickname 买家昵称
 * @param SearchFromData.productName 商品名称
 * @param SearchFromData.receiverName 收货人姓名
 * @param SearchFromData.startTime 下单开始时间
 * @param SearchFromData.endTime 下单结束时间
 * @param SearchFromData.distributionMode 配送方式
 * @param SearchFromData.platform 所属渠道
 * @param SearchFromData.status 状态  待支付 待发货 待收货 已完成 已关闭
 * @param SearchFromData.exportShopOrderIds 需要导出的店铺订单ids
 */
const exportData = async (SearchFromData: any) => {
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
            orderNo: SearchFromData.orderNo,
            buyerNickname: SearchFromData.buyerNickname,
            productName: SearchFromData.productName,
            receiverName: SearchFromData.receiverName,
            startTime: SearchFromData.clinchTime?.[0],
            endTime: SearchFromData.clinchTime?.[1],
            distributionMode: SearchFromData.distributionMode,
            platform: SearchFromData.platform,
            status: activeTabName.value,
            exportShopOrderNos: '',
        }
        param.status = param.status.trim()
        const { code, data, msg } = await doGetExportData(param)
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else return ElMessage.success('导出成功')
    }
}

// 联系买家
const contactBuyer = (row: ApiOrder) => {
    postAddContact(0, row.buyerId).then(({ code }) => {
        if (code === 200) {
            $router.push({
                path: '/customerService',
                query: { id: row.buyerId, types: '订单', orderId: row.id, orderNo: row.no },
            })
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
const handleCheckDetails = (row: ApiOrder) => {
    const shopOrderItems = row.shopOrders[0].shopOrderItems
    const packageId = shopOrderItems.find((item) => item.packageId)?.packageId || ''
    $router.push({
        name: 'detailsIndex',
        query: { orderNo: row.shopOrders[0].orderNo, shopId: row.shopOrders[0].shopId, packageId },
    })
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
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <AccordionFrom :show="false" @search-data="handleSearchData" @change-show="tableStyleUp = $event" @export-data="exportData" />
    </el-config-provider>

    <div class="grey_bar"></div>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="activeTabName" style="margin-top: 16px" @tab-change="handleTabClick">
            <el-tab-pane name=" ">
                <template #label>
                    <span>{{ FirstTabsName }}</span>
                    <el-icon class="el-icon--right-top" @click.stop="handleOpen">
                        <ArrowDown />
                    </el-icon>
                    <el-dropdown ref="dropdownRef" placement="bottom-end" trigger="click" @command="handleDropdownCommand">
                        <span class="el-dropdown-link" style="height: 40px" @click.stop="handleOpen">
                            <el-icon class="el-icon--right">
                                <ArrowDown />
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
            <el-tab-pane v-for="item in TabNavArr" :key="item.key" :label="item.value" :name="item.key"></el-tab-pane>
        </el-tabs>
        <el-tooltip class="box-item" effect="dark" placement="bottom-end">
            <template #content>开启【平台发货】功能后，平台能够为【自营】店铺或供应商处理【快递/虚拟】发货的订单</template>
            <QIcon class="last" name="icon-wenhao" size="18px"></QIcon>
        </el-tooltip>
    </div>
    <div class="handle_container">
        <el-dropdown :disabled="!multiSelect.length" trigger="click" @command="handleCommand">
            <el-button :disabled="multiSelect.filter((item) => filterDeliveryDelieryBtn(item)).length === 0" style="margin-right: 10px">
                批量发货
                <el-icon>
                    <ArrowDown />
                </el-icon>
            </el-button>

            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item :disabled="multiSelect.filter((item) => filterDeliveryDelieryBtn(item)).length === 0" command="EXPRESS"
                        >快递发货
                    </el-dropdown-item>
                    <el-dropdown-item :disabled="multiSelect.filter((item) => filterDeliveryDelieryBtn(item)).length === 0" command="VIRTUAL"
                        >虚拟发货
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <el-button @click="handleRemarks">批量备注</el-button>
    </div>
    <SplitTable
        v-model:checkedItem="multiSelect"
        :class="{ tableUp: !tableStyleUp }"
        :data="orderInfoArr"
        autoTop
        class="orderIndex-table"
        header-selection
    >
        <template #noData>
            <div class="no_data">
                <img alt="" src="@/assets/image/no_shop_examine.png" />
                <p class="cont">暂无数据</p>
            </div>
        </template>
        <template #header="{ row }: { row: ApiOrder }">
            <div class="header-table">
                <div class="header-table__right">
                    <!-- 秒杀 -->
                    <i v-if="row.type === 'SPIKE'" class="iconfont icon-a-zuhe1273"></i>
                    <!-- 套餐 -->
                    <i v-if="row.type === 'PACKAGE'" class="iconfont icon-a-zuhe1275"></i>
                    <!-- 砍价 -->
                    <i v-if="row.type === 'BARGAIN'" class="iconfont icon-a-zuhe1274"></i>
                    <!-- 满减 -->
                    <!-- <i v-if="row.type === 'SPIKE'" class="iconfont icon-a-zuhe1276"></i> -->
                    <!-- 团购 -->
                    <i v-if="row.type === 'TEAM'" class="iconfont icon-a-zuhe1272"></i>
                    <span>订单号：{{ row.no }}</span>
                    <span>下单时间：{{ row.createTime }}</span>
                    <span>店铺：{{ row.shopOrders[0].shopName }}</span>
                    <span>{{ platformDescMap[row.platform] }}</span>
                    <span>{{ getDeliveryMode(row) }}</span>
                    <span v-if="row.isPriority" style="color: #f54319">优先发货</span>
                </div>
                <el-button
                    class="mla"
                    link
                    style="position: sticky; right: 23px; background: #f7f8fa; padding-left: 5px"
                    type="primary"
                    @click="handleCheckDetails(row)"
                    >查看详情
                </el-button>
                <RemarkFlag :content="row.shopOrders[0]?.remark?.platform" class="remark_flag" @see-remark="handleRemark(row.shopOrders[0])" />
            </div>
        </template>
        <SplitTableColumn align="left" label="商品" prop="name" width="500px">
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
        <SplitTableColumn :is-mixed="true" align="left" label="实收金额(元)" prop="shopOrders" width="150">
            <template #default="{ row }">
                <div class="collection">￥{{ calculateTotalPrice(row.shopOrders?.[0].shopOrderItems)?.toFixed(2) }}</div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :is-mixed="true" align="left" label="买家" prop="age" width="170">
            <template #default="{ row }">
                <div class="customer">
                    <div>
                        <span class="customer__name">{{ getOrderReceiver(row).name }}</span>
                        <i
                            class="iconfont icon-xiaoxi2"
                            style="font-size: 18px; color: #1bad19; position: relative; top: -2px; margin-left: 3px; cursor: pointer"
                            @click="contactBuyer(row)"
                        ></i>
                    </div>
                    <span>{{ getOrderReceiver(row).mobile }}</span>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :is-mixed="true" align="left" label="订单状态" prop="orderStatus" width="190">
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
                        :pay-timeout="String(row?.timeout?.payTimeout || '')"
                    />
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn :align="'right'" :is-mixed="true" fixed="right" label="操作" prop="sex" width="100">
            <template #default="{ row }">
                <div class="operation">
                    <el-button
                        v-if="row.status === 'UNPAID' && row.shopOrders[0].status === 'OK' && isPlatformDelivery.shop"
                        type="primary"
                        link
                        @click="closeShopOrder(row)"
                        >关闭订单</el-button
                    >
                    <el-button
                        v-if="showDeliveryBtn(row)"
                        link
                        style="margin-top: 3px"
                        type="primary"
                        @click="handleDelivery(row.no, row.shopOrders[0].no, row)"
                        >发货
                    </el-button>
                </div>
            </template>
        </SplitTableColumn>
    </SplitTable>

    <BetterPageManage
        :page-num="pageConfig.current"
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        @reload="initOrderList"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <OrderShipment
        v-model:isShow="deliveryReactive.deliverDialog"
        :current-deliver="currentDeliver"
        :current-no="deliveryReactive.currentNo"
        :current-shop-order-no="deliveryReactive.currentShopOrderNo"
    />
    <remark-popup
        v-model:ids="currentRemarkIds"
        v-model:isShow="remarkDialog"
        v-model:remark="textarea"
        remark-type="GOODS"
        @success="handleSuccess"
    />
    <!-- 备注弹窗e -->
</template>

<style lang="scss" scoped>
@import '@/assets/css/goods/goods.scss';

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
        align-items: flex-end;
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
    padding-top: -10px;
    position: absolute;
    left: -20px;
    opacity: 0;
}

@include b(header-table) {
    width: 100%;
    display: flex;
    flex: 1;
    color: #333333;
    justify-content: space-between;
    font-size: 14px;
    align-items: center;
    @include e(right) {
        display: flex;
        flex: 1;
        // 文字不换行
        white-space: nowrap;
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
        @include utils-ellipsis(1);
        max-width: 50%;
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

:deep(.el-dropdown-menu__item) {
    padding: 0;
    height: 37px;
    display: flex;
    justify-content: center;
}

:deep(.el-dropdown-menu__item:not(.is-disabled):focus) {
    color: #555cfd;
    background-color: rgb(85, 92, 253, 0.1);
}

:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
    color: #555cfd;
    background-color: rgb(85, 92, 253, 0.1);
}
</style>
<style lang="scss" scoped>
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

.notice {
    color: #f00;
    float: right;
    margin-right: 15px;
    line-height: 32px;
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
