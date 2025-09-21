<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { doGetOrderList } from '@/apis/order'
import { ElMessage } from 'element-plus'
import ExpressTable from '@/views/order/deliveryList/express-table.vue'
import UnExpressTable from '@/views/order/deliveryList/un-express-table.vue'
import { AddressFn } from '@/components/q-address'
import { useDeliveryOrderList } from '@/store/modules/order'
import { useRouter } from 'vue-router'
import {
    doLogisticsSetDef,
    doGetLogisticsCompany,
    doGetLogisticsList,
    doGetLogisticsCompanyByShopIdList,
    doGetLogisticsExpressUsableList,
} from '@/apis/freight/freightAdd'
import { doPostOrderBatchDeliver } from '@/apis/order'
import type { FormInstance, FormRules } from 'element-plus'
import type { ApiAddressItem, ApiLogisticCompany } from '@/views/freight/components/types'
import type { OrderDataType, ApiOrder, ShopOrder, OrderListSearchData, ShopOrderItem } from '@/views/order/types/order'
import type { ApiNotDelivey, BatchParamsDeliver, ExpressCompany } from '@/views/order/orderShipment/types'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
interface ApiOrderExpressCompany extends ApiOrder {
    expressCompany: ExpressCompany
}
console.log('regionData', regionData)

const $deliveryOrderList = useDeliveryOrderList()
const TabData = ref<ApiOrderExpressCompany[]>([])
// 批量发货携带参数
const batchParamsDeliver = ref<BatchParamsDeliver[]>([])
const $router = useRouter()
const PageConfig = ref({ size: 1000, current: 1, total: 0 })
const expressMap = ref<Map<string, ExpressCompany>>(new Map())
const expressCompanyMap = computed({
    set: (val) => (expressMap.value = val),
    get: () => {
        computedTableData.value.forEach((order) => {
            expressMap.value.set(order.no, { logisticsCompanyCode: deliverDialogFormData.expressCompany.logisticsCompanyCode || '', expressNo: '' })
        })
        return expressMap.value
    },
})
const deliverDialogFormData = reactive({
    deliverType: 'EXPRESS' as 'PRINT_EXPRESS' | 'EXPRESS' | 'WITHOUT',
    printId: '',
    platform: 'WECHAT_MINI_APP' as 'WECHAT_MINI_APP' | 'H5' | 'APP' | 'PC',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
    expressCompany: {
        logisticsCompanyCode: '',
        logisticsCompanyName: '',
        expressNo: '',
    },
    addressaddress: '',
})
// 发货类型枚举
const deliverType = [
    { key: 'EXPRESS', value: '手动发货' },
    { key: 'PRINT_EXPRESS', value: '打印快递单并发货' },
    { key: 'WITHOUT', value: '无需物流发货' },
]
const DatchDeliveryFormRules = reactive<FormRules>({
    deliverType: [{ required: true, trigger: 'blur', message: '请选择' }],
    addressaddress: [{ required: true, trigger: 'change', message: '请选择服务' }],
    printId: [{ required: true, trigger: 'change', message: '请选择' }],
    logisticsCompanyCode: [
        {
            validator(rule, value, callback, source, options) {
                if (!deliverDialogFormData.expressCompany.logisticsCompanyCode) {
                    callback(new Error('请选择物流11服务'))
                    return
                }
                callback()
            },
        },
    ],
})
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
// 发货地址数据
const deliveryAddressData = ref<ApiAddressItem[]>([])

initStoreDeliveryOrderList()
initLogisticsCompany()
initLogisticsList()
watch(
    () => deliverDialogFormData.deliverType,
    (val) => {
        if (val !== 'PRINT_EXPRESS') return
        if (!deliverDialogFormData.addressaddress) {
            ElMessage.info('请添加设置物流')
        }
        // initPrinterList()
    },
)

/**
 * @description: 移除商品
 * @param {*} id
 * @returns {*}
 */
function filterOrderList(goods: ApiOrderExpressCompany) {
    TabData.value = TabData.value.filter((order) => order.id !== goods.id)
    $deliveryOrderList.SET_ORDER_LIST(TabData.value)
    expressCompanyMap.value.delete(goods.no)
}
/**
 * @description: 内存中获取待发货商品
 * @returns {*}
 */
function initStoreDeliveryOrderList() {
    TabData.value = $deliveryOrderList.orderList
    // TabData.value.forEach((order) => {
    //     expressCompanyMap.value.set(order.no, { logisticsCompanyCode: '', expressNo: '' })
    // })
}
/**
 * @description: 获取全部待发货的订单
 * @returns {*}
 */
async function initOederList() {
    const status = 'UN_DELIVERY'
    const { data, code } = await doGetOrderList({ ...PageConfig.value, status })
    loopAssignment(code, data)
}
/**
 * @description: 循环赋值
 * @returns {*}
 */
function loopAssignment(code: number, data: any) {
    if (code === 200) {
        ElMessage.success('导入成功')
        const { records, current, size, total } = data
        records.forEach((order: ApiOrderExpressCompany) => {
            expressCompanyMap.value.set(order.no, { logisticsCompanyCode: '', expressNo: '' })
        })
        TabData.value = records.filter((item: { extra: { distributionMode: string } }) => {
            return !item.extra || item.extra.distributionMode !== 'SHOP_STORE'
        })
        PageConfig.value = {
            size,
            current,
            total,
        }
        return
    }
    ElMessage.error('导入失败')
}

/**
 * @description: 获取物流服务列表  如果此列表无数据
 * @returns {*}
 */
async function initLogisticsCompany() {
    const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
    if (code !== 200) {
        ElMessage.error('获取物流公司失败')
        return
    }
    if (data.records.length) {
        deliverDialogFormData.expressCompany.logisticsCompanyCode = data.records[0].logisticsCompanyCode
    }
    companySelectList.value = data.records
}
/**
 * @description: 获取发货物流地址列表
 * @returns {*}
 */
async function initLogisticsList() {
    const { data, code } = await doGetLogisticsList({})
    if (code !== 200) {
        ElMessage({ message: '请刷新重试...', type: 'warning' })
    } else {
        deliveryAddressData.value = data.records
    }
    const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
    if (!sender) {
        // ElMessage.info('请添加设置物流')
        return
    }
    deliverDialogFormData.addressaddress = sender.id
}

/**
 * @description: 获取发货地址 联系人...
 * @returns {*}
 */
function getSenderAddress(params: { name: string; mobile: string; address: string }) {
    const sender = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
    if (!sender) return ElMessage.error({ message: '未获取到发货信息' })
    const { address, contactName, contactPhone } = sender
    const addressArr = [sender.provinceCode, sender.cityCode, sender.regionCode]
    params.address = `${AddressFn(regionData, addressArr)}${address}`
    params.mobile = contactPhone
    params.name = contactName
}

/**
 * @description: 确定发货
 * @returns {*}
 */
const handleSubmit = async () => {
    let data
    switch (deliverDialogFormData.deliverType) {
        case 'EXPRESS':
            data = manualDelivery()
            break
        case 'PRINT_EXPRESS':
            data = printDelivery()
            break
        default:
            data = unDelivery()
            break
    }
    if (!data) return
    batchDeliver(data)
}
/**
 * @description:手动发货
 * @param {*} params
 * @returns {*}
 */
function manualDelivery() {
    console.log('deliverDialogFormData.expressCompany', deliverDialogFormData.expressCompany)
    if (!deliverDialogFormData.expressCompany.logisticsCompanyCode) {
        ElMessage.error({ message: '请选择物流服务或填写运单号' })
        return false
    }
    const initExpressCompanyMap = new Map()
    for (const iterator of expressCompanyMap.value) {
        initExpressCompanyMap.set(iterator[0], {
            expressCompanyCode: iterator[1].logisticsCompanyCode,
            expressNo: iterator[1].expressNo,
            expressCompanyName:
                companySelectList.value.find((item) => item.logisticsCompanyCode === iterator[1].logisticsCompanyCode)?.logisticsCompanyName || '',
        })
    }
    const isVlidate = Array.from(initExpressCompanyMap.values()).every((item) => item.expressNo && item.expressCompanyCode)
    console.log('initExpressCompanyMap.values()', initExpressCompanyMap.values())
    if (!isVlidate) {
        ElMessage.error({ message: '请选择物流服务或填写运单号' })
        return false
    }
    const data = computedTableData.value.map((order) => {
        const expressCompany = initExpressCompanyMap.get(order.no)
        const params: BatchParamsDeliver = {
            orderNo: order.no,
            shopId: order?.shopOrders?.[0]?.shopId,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType,
                receiver: order.shopOrders[0].orderReceiver || order.orderReceiver,
                cargo: '',
                remark: '',
                dropDeliver: true,
                expressCompany: expressCompany,
                items: order.shopOrders[0].shopOrderItems
                    .filter((item) => {
                        return (
                            item.status === 'OK' &&
                            item.packageStatus === 'WAITING_FOR_DELIVER' &&
                            (!item.afsStatus ||
                                item.afsStatus === 'NONE' ||
                                item.afsStatus === 'REFUND_REQUEST' ||
                                item.afsStatus === 'RETURN_REFUND_REQUEST')
                        )
                    })
                    .map((item) => ({
                        itemId: item.id,
                        num: item.num,
                    })),
            },
        }
        return params
    })
    return data
}
/**
 * @description:打印发货
 * @returns {*}
 */
const printDelivery = () => {
    const sender = { name: '', mobile: '', address: '' }
    getSenderAddress(sender)
    const currentCode = deliverDialogFormData.expressCompany.logisticsCompanyCode
    const currentName = companySelectList.value.find((item) => item.logisticsCompanyCode === currentCode).logisticsCompanyName
    const data = computedTableData.value.map((order) => {
        const receiver = order.shopOrders[0].orderReceiver || order.orderReceiver
        const params: BatchParamsDeliver = {
            orderNo: order.no,
            shopId: order?.shopOrders?.[0]?.shopId,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType,
                receiver: receiver,
                sender: sender,
                cargo: '',
                remark: '',
                dropDeliver: true,
                expressCompany: {
                    expressCompanyCode: currentCode,
                    expressCompanyName: currentName,
                },
                items: order.shopOrders[0].shopOrderItems
                    .filter((item) => {
                        console.log('item', item)
                        return (
                            item.status === 'OK' &&
                            item.packageStatus === 'WAITING_FOR_DELIVER' &&
                            (!item.afsStatus ||
                                item.afsStatus === 'NONE' ||
                                item.afsStatus === 'REFUND_REQUEST' ||
                                item.afsStatus === 'RETURN_REFUND_REQUEST')
                        )
                    })
                    .map((item) => ({
                        itemId: item.id,
                        num: item.num,
                    })),
            },
        }
        return params
    })
    return data
}
/**
 * @description: 无需物流
 * @returns {*}
 */
const unDelivery = () => {
    const data = computedTableData.value.map((order) => {
        const params: BatchParamsDeliver = {
            orderNo: order.no,
            shopId: order?.shopOrders?.[0]?.shopId,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType,
                cargo: '',
                remark: '',
                dropDeliver: true,
                items: order.shopOrders[0].shopOrderItems
                    .filter((item) => {
                        console.log('item', item)
                        return (
                            item.status === 'OK' &&
                            item.packageStatus === 'WAITING_FOR_DELIVER' &&
                            (!item.afsStatus ||
                                item.afsStatus === 'NONE' ||
                                item.afsStatus === 'REFUND_REQUEST' ||
                                item.afsStatus === 'RETURN_REFUND_REQUEST')
                        )
                    })
                    .map((item) => ({
                        itemId: item.id,
                        num: item.num,
                    })),
            },
        }
        return params
    })
    return data
}
/**
 * @description: 发货清除缓存跳路由
 * @param {*} params
 * @returns {*}
 */
async function batchDeliver(params: any) {
    const { code, data: res, msg } = await doPostOrderBatchDeliver(params)
    if (code !== 200) return ElMessage.error({ message: msg || '发货失败' })
    $deliveryOrderList.SET_ORDER_LIST([])
    ElMessage.success({ message: '发货成功' })
    $router.replace({ name: 'orderIndex' })
}
/**
 * @description:选择物流公司
 * @param {*} val
 * @returns {*}
 */
const handleExpressCompanyChange = (val: string) => {
    if (deliverDialogFormData.deliverType === 'EXPRESS') {
        for (const iterator of expressCompanyMap.value) {
            iterator[1].logisticsCompanyCode = val
        }
    }
}
const cancelBatchDeliver = () => {
    $deliveryOrderList.SET_ORDER_LIST([])
    $router.replace({ name: 'orderIndex' })
}
const computedTableData = computed(() => {
    // const data = TabData.value.filter((item) => {
    //     return deliverDialogFormData.deliverType === 'WITHOUT'
    //         ? item.extra.distributionMode === 'VIRTUAL'
    //         : item.extra.distributionMode === 'EXPRESS' &&
    //               (deliverDialogFormData.platform !== 'APP'
    //                   ? item.platform === deliverDialogFormData.platform
    //                   : ['ANDROID', 'IOS', 'HARMONY'].includes(item.platform))
    // })
    return TabData.value
})
</script>

<template>
    <el-form ref="DatchDeliveryFormRef" :model="deliverDialogFormData" :rules="DatchDeliveryFormRules" class="DatchDelivery_container fdc1 overh">
        <!-- <el-form-item label="所属渠道" prop="platform" label-width="100px">
            <el-radio-group v-model="deliverDialogFormData.platform" class="ml-4" size="default">
                <el-radio label="WECHAT_MINI_APP" size="small">小程序</el-radio>
                <el-radio label="H5" size="small">H5商城</el-radio>
                <el-radio label="APP" size="small">APP商城</el-radio>
                <el-radio label="PC" size="small">PC商城</el-radio>
            </el-radio-group>
        </el-form-item> -->
        <el-form-item label="发货方式" prop="deliverType" label-width="100px">
            <el-radio-group v-model="deliverDialogFormData.deliverType" class="ml-4" size="default">
                <el-radio v-for="item in deliverType" :key="item.key" :label="item.key" size="small">{{ item.value }}</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-show="deliverDialogFormData.deliverType !== 'WITHOUT'" label="物流服务" prop="logisticsCompanyCode" label-width="100px">
            <el-select
                v-model="deliverDialogFormData.expressCompany.logisticsCompanyCode"
                placeholder="请选择物流服务"
                style="width: 444px"
                clearable
                @change="handleExpressCompanyChange"
            >
                <el-option
                    v-for="item in companySelectList"
                    :key="item.logisticsCompanyName"
                    :label="item.logisticsCompanyName"
                    :value="item.logisticsCompanyCode"
                />
            </el-select>
        </el-form-item>
        <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label="发货地址" prop="addressaddress" label-width="100px">
            <el-select v-model="deliverDialogFormData.addressaddress" placeholder="请选择发货地址" style="width: 444px">
                <el-option v-for="item in deliveryAddressData" :key="item.id" :value="item.id" :label="`${item.area?.join('')}${item.address}`" />
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-row justify="end" style="width: 100%"><el-button type="primary" link @click="initOederList">导入全部待发货订单</el-button> </el-row>
        </el-form-item>
        <el-form-item class="fdc1 overh table_item">
            <express-table
                v-show="deliverDialogFormData.deliverType === 'EXPRESS'"
                v-model="expressCompanyMap"
                :table-data="computedTableData"
                :logistics-company-code="deliverDialogFormData.expressCompany.logisticsCompanyCode"
                :company-select-list="companySelectList"
                @filter-order-list="filterOrderList"
            />
            <un-express-table
                v-show="!['EXPRESS'].includes(deliverDialogFormData.deliverType)"
                :tab-data="computedTableData"
                :company-select-list="companySelectList"
                @filter-order-list="filterOrderList"
            />
        </el-form-item>
    </el-form>
    <div class="DatchDelivery_container__tool">
        <el-row justify="center" style="width: 100%">
            <el-button round type="primary" @click="handleSubmit">确认</el-button>
            <!-- <el-button round @click="cancelBatchDeliver">取消</el-button> -->
            <el-popconfirm title="确定退出批量发货?" @confirm="cancelBatchDeliver">
                <template #reference>
                    <el-button round>取消</el-button>
                </template>
            </el-popconfirm>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
.table_item {
    :deep(.el-form-item__content) {
        flex-direction: column;
        display: flex;
        flex: 1;
        overflow: hidden;
    }
}
@include b(avatar_text_box) {
    @include flex(space-between, flex-start);
}
@include b(send) {
    font-size: 14px;
    color: #333333;
}
@include b(DatchDelivery_container) {
    padding: 10px 25px;
    @include e(tabText) {
        color: #000;
    }
    @include e(tool) {
        margin-top: auto;
        @include flex;
        position: sticky;
        bottom: 0;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        z-index: 100;
    }
}
</style>
