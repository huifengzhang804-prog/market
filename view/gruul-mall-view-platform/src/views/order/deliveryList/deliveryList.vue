<script setup lang="ts">
import { importNeedDeliveryOrders } from '@/apis/order'
import { ElMessage } from 'element-plus'
import ExpressTable from '@/views/order/deliveryList/express-table.vue'
import UnExpressTable from '@/views/order/deliveryList/un-express-table.vue'
import { AddressFn } from '@/components/q-address'
import { useDeliveryOrderList } from '@/store/modules/order'
import { useRouter } from 'vue-router'
import { doGetLogisticsExpressUsableList } from '@/apis/freight/freightAdd'
import { doPostOrderBatchDeliver } from '@/apis/order'
import type { FormRules } from 'element-plus'
import type { ApiOrder } from '@/views/order/types/order'
import { doGetLogisticsList } from '@/apis/set/platformDelivery'
import { useRoute } from 'vue-router'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import { DeliverType, UserAddressDTO } from '@/apis/order/types'

const regionData = useGDRegionDataStore().getterGDRegionData

// 从路由中获取参数
const route = useRoute()
const { type } = route.query
const isMemory = ref(false)
isMemory.value = type === 'memory'

type ApiEnumLogisticCompany = 'id' | 'logisticsCompanyCode' | 'logisticsCompanyName' | 'logisticsCompanyStatus'
export type ApiLogisticCompany = Record<ApiEnumLogisticCompany, string>
export interface ApiOrderExpressCompany extends ApiOrder {
    expressCompany: ExpressCompany
}
const $deliveryOrderList = useDeliveryOrderList()
type ShopType = 'shopOrders' | 'supplierOrders'
type TabDataType = {
    [k in ShopType]: ApiOrderExpressCompany[]
}
const TabData = ref<TabDataType>({ shopOrders: [], supplierOrders: [] })
// 批量发货携带参数
const $router = useRouter()
type DeliverDialogFormData = {
    deliveryMethod: ChooseDeliverType
    deliverType: keyof typeof DeliverType | ''
    printId: string
    receiver: UserAddressDTO
    expressCompany?: ExpressCompany
    addressaddress: string
    shopType: ShopType
}
const deliverDialogFormData = reactive<DeliverDialogFormData>({
    deliveryMethod: '', // 发货方式
    deliverType: '',
    printId: '',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
    // expressCompany: {
    //     expressCompanyCode: '',
    //     expressCompanyName: '',
    // },
    addressaddress: '',
    shopType: 'shopOrders', // 商家类型 shopOrders:自营店铺|supplierOrders:自营供应商
})
// 发货类型枚举
const deliverType = [
    { key: 'EXPRESS', value: '手动发货' },
    { key: 'PRINT_EXPRESS', value: '打印快递单并发货' },
]
const DatchDeliveryFormRules = reactive<FormRules>({
    deliverType: [{ required: true, trigger: 'change', message: '请选择发货方式' }],
    addressaddress: [{ required: true, trigger: 'change', message: '请选择物流地址' }],
    printId: [{ required: true, trigger: 'change', message: '请选择打印机' }],
    'expressCompany.expressCompanyCode': [
        {
            validator(rule, value, callback) {
                if (deliverDialogFormData.deliveryMethod === 'VIRTUAL') {
                    callback()
                } else if (!deliverDialogFormData.expressCompany?.expressCompanyCode) {
                    callback(new Error('请选择物流服务'))
                } else {
                    callback()
                }
            },
        },
    ],
})
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
// 发货地址数据
const deliveryAddressData = ref<any[]>([])

initStoreDeliveryOrderList()
const shippingMethodMap = {
    '': '',
    EXPRESS: '快递发货',
    VIRTUAL: '虚拟发货',
}
initLogisticsCompany()

/**
 * 移除商品
 * @param {*} id
 */
function filterOrderList(goods: ApiOrderExpressCompany) {
    TabData.value[deliverDialogFormData.shopType] = TabData.value?.[deliverDialogFormData.shopType].filter((order) => order.id !== goods.id)
    $deliveryOrderList.SET_ORDER_LIST(TabData.value)
}

type ChooseDeliverType = '' | 'VIRTUAL' | 'EXPRESS'
/**
 * 内存中获取待发货商品
 */
function initStoreDeliveryOrderList() {
    TabData.value = $deliveryOrderList.getOrderList || { shopOrders: [], supplierOrders: [] }
    Object.keys(TabData.value).forEach((key) => {
        TabData.value[key as keyof typeof TabData.value].forEach((item) => {
            item.expressCompany = {
                expressCompanyCode: '',
                expressCompanyName: '',
                expressNo: '',
            }
        })
    })
    console.log('从内存中获取的数据', TabData.value)
    if (['VIRTUAL', 'EXPRESS'].includes(route.query.type as string)) {
        deliverDialogFormData.deliveryMethod = route.query.type as ChooseDeliverType
        if (deliverDialogFormData.deliveryMethod === 'VIRTUAL') {
            deliverDialogFormData.deliverType = 'WITHOUT'
            deliverDialogFormData.shopType = 'shopOrders'
        } else if (deliverDialogFormData.deliveryMethod === 'EXPRESS') {
            deliverDialogFormData.deliverType = 'EXPRESS'
        }
    } else {
        ElMessage.error('已强制返回订单列表页选择发货方式')
        $router.replace('/order')
    }
}
/**
 * 获取全部待发货的订单
 */
async function initOederList() {
    if (deliverDialogFormData.deliveryMethod) {
        const { data, code } = await importNeedDeliveryOrders(undefined, deliverDialogFormData.deliveryMethod)
        loopAssignment(code, data)
    }
}
type ExpressCompany = {
    expressCompanyCode: string
    expressCompanyName: string
    expressNo?: string
}
/**
 * 循环赋值
 */
function loopAssignment(code: number, data: TabDataType) {
    if (code === 200) {
        ElMessage.success('导入成功')
        Object.keys(data).forEach((key) => {
            data[key as ShopType].forEach((item) => {
                item.expressCompany = {
                    expressCompanyCode: '',
                    expressCompanyName: '',
                    expressNo: '',
                }
            })
        })
        TabData.value = data
        return
    }
    ElMessage.error('导入失败')
}

/**
 * 获取物流服务列表  如果此列表无数据
 */
async function initLogisticsCompany() {
    const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
    if (code !== 200) {
        ElMessage.error('获取物流公司失败')
        return
    }
    companySelectList.value = data.records
}
/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
    const { data, code } = await doGetLogisticsList({})
    if (code !== 200) {
        ElMessage({ message: '请刷新重试...', type: 'warning' })
    } else {
        deliveryAddressData.value = data.records?.filter((item: any) => item.defSend === 'YES')
    }
    const sender = deliveryAddressDataComputed.value.find((item) => item.defSend === 'YES')

    if (!sender) {
        // ElMessage.info('请添加设置物流')
        return
    }
    deliverDialogFormData.addressaddress = sender.id
}
initLogisticsList()
/**
 * 获取发货地址 联系人...
 */
function getSenderAddress(params: any) {
    const sender = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
    if (!sender) return ElMessage.error({ message: '未获取到发货信息' })
    const { address, contactName, contactPhone } = sender
    const addressArr = [sender.provinceCode, sender.cityCode, sender.regionCode]
    params.address = `${AddressFn(regionData, addressArr)}${address}`
    params.mobile = contactPhone
    params.name = contactName
}
/**
 * 拼接数据
 */
function getReceiverAddress(params: any) {
    const { name, mobile } = params
    const Receiver = { name, mobile, address: '' }
    Receiver.address = `${params.area}${params.address}`
    return Receiver
}

const DatchDeliveryFormRef = ref()
/**
 * 确定发货
 */
const handleSubmit = async () => {
    let data = []
    DatchDeliveryFormRef.value.validate(async (valid: boolean, filed: any) => {
        if (valid) {
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
            console.log('发货表单', data)
            if (!data || !data.length) return ElMessage.error({ message: '请填写完整' })
            batchDeliver(data)
        }
    })
}
/**
 * 手动发货
 */
function manualDelivery() {
    let selfShopType = ''
    if (deliverDialogFormData.shopType === 'shopOrders') {
        selfShopType = 'SELF_SHOP'
    } else {
        selfShopType = 'SELF_SUPPLIER'
    }

    const isVlidate = computedTableData.value.every(
        (item) => item.expressCompany.expressCompanyCode && item.expressCompany.expressCompanyName && item.expressCompany.expressNo,
    )
    if (!isVlidate) {
        ElMessage.error({ message: '请选择物流服务或填写运单号' })
        return []
    }

    const data = computedTableData.value?.map((order) => {
        const expressCompany = order.expressCompany
        const params = {
            orderNo: order.no,
            shopId: order?.shopOrders?.[0]?.shopId,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType,
                receiver: order.shopOrders[0].orderReceiver || order.orderReceiver,
                cargo: '',
                remark: '',
                dropDeliver: true,
                expressCompany,
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
            selfShopType,
        }
        return params
    })
    return data
}
/**
 * 打印发货
 */
const printDelivery = () => {
    let selfShopType = ''
    if (deliverDialogFormData.shopType === 'shopOrders') {
        selfShopType = 'SELF_SHOP'
    } else {
        selfShopType = 'SELF_SUPPLIER'
    }
    const sender = { name: '', mobile: '', address: '' }
    getSenderAddress(sender)
    const currentCode = deliverDialogFormData.expressCompany?.expressCompanyCode || ''
    const currentName = companySelectList.value.find((item) => item.logisticsCompanyCode === currentCode)?.logisticsCompanyName
    const data = computedTableData.value.map((order) => {
        const receiver = getReceiverAddress(order.shopOrders[0].orderReceiver || order.orderReceiver)
        const params = {
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
            selfShopType,
        }
        return params
    })
    return data
}
/**
 * 无需物流
 */
const unDelivery = () => {
    let selfShopType = ''
    if (deliverDialogFormData.shopType === 'shopOrders') {
        selfShopType = 'SELF_SHOP'
    } else {
        selfShopType = 'SELF_SUPPLIER'
    }
    const data = computedTableData.value.map((order) => {
        const params = {
            orderNo: order.no,
            shopId: order?.shopOrders?.[0]?.shopId,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType as keyof typeof DeliverType,
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
            selfShopType,
        }
        return params
    })
    return data
}
/**
 * 发货清除缓存跳路由
 * @param {*} params
 */
async function batchDeliver(params: any) {
    const { code, data: res, msg } = await doPostOrderBatchDeliver(params)
    if (code !== 200) {
        const errIndex = TabData.value[deliverDialogFormData.shopType].findIndex((item) => item.no === res)
        if (errIndex > -1) {
            TabData.value[deliverDialogFormData.shopType].splice(0, errIndex)
            $deliveryOrderList.SET_ORDER_LIST(TabData.value)
            return ElMessage.warning({ message: msg || `订单号为:${res}的商品发货失败,请确认后重试` })
        }
        return ElMessage.error({ message: msg || '系统错误！' })
    }
    $deliveryOrderList.SET_ORDER_LIST({ shopOrders: [], supplierOrders: [] })
    ElMessage.success({ message: '发货成功' })
    $router.replace({ name: 'order' })
}
type LogisticsCompany = {
    freightId: string
    id: string
    logisticsCompanyCode: string
    logisticsCompanyName: string
    printName: string
}
/**
 * 批量设置物流公司
 */
const handleExpressCompanyChange = (e: LogisticsCompany) => {
    if (deliverDialogFormData.deliveryMethod === 'EXPRESS') {
        if (e) {
            deliverDialogFormData.expressCompany = {
                ...e,
                expressCompanyCode: e.logisticsCompanyCode,
                expressCompanyName: e.logisticsCompanyName,
            }
            TabData.value[deliverDialogFormData.shopType]?.forEach((item) => {
                item.expressCompany.expressCompanyCode = e.logisticsCompanyCode
                item.expressCompany.expressCompanyName = e.logisticsCompanyName
            })
        } else {
            deliverDialogFormData.expressCompany = {
                expressCompanyCode: '',
                expressCompanyName: '',
            }
        }
        console.log('撒旦发生', computedTableData.value)
    }
    // if (deliverDialogFormData.deliverType === 'PRINT_EXPRESS') {
    //     if (deliveryAddressDataComputed.value.length === 1) {
    //         deliverDialogFormData.addressaddress = deliveryAddressDataComputed.value?.[0]?.id
    //     }
    // }
}
const cancelBatchDeliver = () => {
    $deliveryOrderList.SET_ORDER_LIST({ shopOrders: [], supplierOrders: [] })
    $router.replace({ name: 'order' })
}

const handleChangeShopType = (val: any) => {
    deliverDialogFormData.addressaddress = ''
    if (deliveryAddressDataComputed.value.length === 1) {
        deliverDialogFormData.addressaddress = deliveryAddressDataComputed.value?.[0]?.id
    }
}
const deliveryAddressDataComputed = computed(() => {
    return deliveryAddressData.value.filter((item) =>
        deliverDialogFormData.shopType === 'shopOrders' ? item.defSelfShop === 'YES' : item.defSelfSupplier === 'YES',
    )
})
const computedTableData = computed(() => {
    return TabData.value[deliverDialogFormData.shopType]
})
export type CompanySelectList = {
    logisticsCompanyCode: string
    logisticsCompanyName: string
    id: Long
}
const tableDataChange = (val: string, row: ApiOrderExpressCompany, type: 'select' | 'input' = 'select') => {
    const data = TabData.value[deliverDialogFormData.shopType]?.find((item) => item.no === row.no)
    if (data) {
        if (type === 'select') {
            data.expressCompany.expressCompanyName = val
            data.expressCompany.expressCompanyCode = companySelectList.value.find((i) => i.logisticsCompanyName === val)?.logisticsCompanyCode || ''
        }
        if (type === 'input') {
            data.expressCompany.expressNo = val
        }
    }
}
const HandleloadDeliverDialogFormData = (e: DeliverDialogFormData) => {
    const findItem = deliveryAddressDataComputed.value.find((item) => item.id === e)
    if (findItem) {
        deliverDialogFormData.printId = findItem.printId
        deliverDialogFormData.receiver = findItem.receiver
    } else {
        deliverDialogFormData.printId = ''
        deliverDialogFormData.receiver = { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] }
    }
}
</script>

<template>
    <el-form
        v-if="deliverDialogFormData.deliveryMethod"
        ref="DatchDeliveryFormRef"
        :model="deliverDialogFormData"
        :rules="DatchDeliveryFormRules"
        class="DatchDelivery_container fdc1 overh"
        label-position="right"
    >
        <div class="shipping_method_title">
            {{ shippingMethodMap[deliverDialogFormData.deliveryMethod] }}
        </div>
        <template v-if="deliverDialogFormData.deliveryMethod !== 'VIRTUAL'">
            <el-form-item label="发货方式" prop="deliverType" label-width="100px">
                <el-radio-group
                    v-if="deliverDialogFormData.deliveryMethod === 'EXPRESS'"
                    v-model="deliverDialogFormData.deliverType"
                    class="ml-4"
                    size="default"
                >
                    <el-radio v-for="item in deliverType" :key="item.key" :value="item.key" size="small">{{ item.value }}</el-radio>
                </el-radio-group>
                <div v-else>虚拟发货</div>
            </el-form-item>
            <el-form-item label="商家类型" prop="shopType" label-width="100px">
                <el-radio-group v-model="deliverDialogFormData.shopType" class="ml-4" size="default" @change="handleChangeShopType">
                    <el-radio value="shopOrders" size="small">自营店铺</el-radio>
                    <el-radio value="supplierOrders" size="small">自营供应商</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="物流服务" prop="expressCompany.expressCompanyCode" label-width="100px">
                <el-select
                    v-model="deliverDialogFormData.expressCompany"
                    placeholder="请选择物流服务"
                    style="width: 444px"
                    clearable
                    value-key="id"
                    @change="handleExpressCompanyChange"
                >
                    <el-option v-for="item in companySelectList" :key="item.id" :label="item.logisticsCompanyName" :value="item" />
                </el-select>
            </el-form-item>
        </template>

        <div v-else style="margin-left: 16px; color: #f54319">您可通过网络形式(如:聊天工具将商品发给用户)发货</div>

        <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label="发货地址" prop="addressaddress" label-width="100px">
            <el-select
                v-model="deliverDialogFormData.addressaddress"
                placeholder="请选择发货地址"
                style="width: 444px"
                @change="HandleloadDeliverDialogFormData"
            >
                <el-option
                    v-for="item in deliveryAddressDataComputed"
                    :key="item.id"
                    :value="item.id"
                    :label="`${item.area?.join('')} ${item.address}`"
                />
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-row justify="end" style="width: 100%"><el-button type="primary" link @click="initOederList">重新导入</el-button> </el-row>
        </el-form-item>
        <el-form-item class="fdc1 overh table_item">
            <ExpressTable
                v-if="deliverDialogFormData.deliverType === 'EXPRESS'"
                :tableData="computedTableData"
                :companySelectList="companySelectList"
                @update:tableData="tableDataChange"
                @filterOrderList="filterOrderList"
            />
            <UnExpressTable
                v-else
                :tabData="computedTableData"
                :showPeople="deliverDialogFormData.deliveryMethod !== 'VIRTUAL'"
                @filterOrderList="filterOrderList"
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
    padding: 20px 25px;
    .shipping_method_title {
        color: rgb(51, 51, 51);
        font-size: 14px;
        font-weight: bold;
        margin-bottom: 28px;
        margin-left: 16px;
    }
    @include e(tabText) {
        color: #000;
    }
    @include e(tool) {
        width: 100%;
        @include flex();
        position: sticky;
        bottom: 0;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        z-index: 100;
        margin-top: auto;
    }
}
</style>
