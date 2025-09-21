<script lang="ts" setup>
import {
    doGetBatchDeliveryPrice,
    doGetLogisticsList,
    doGetOrderList,
    doGetShopDeliveryMethod,
    doPostOrderBatchDeliver,
    setLogisticsDddress,
} from '@/apis/order'
import type { CascaderOption, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import UnExpressTable from '@/views/order/deliveryList/un-express-table.vue'
import { AddressFn } from '@/components/q-address'
import { useDeliveryOrderList } from '@/store/modules/order'
import { useRoute, useRouter } from 'vue-router'
import DeliveryListSelect from '@/q-plugin/freight/DeliveryListSelect.vue'
import DeliveryListExpressTable from '@/q-plugin/freight/DeliveryListExpressTable.vue'
import type { ApiOrder, OrderReceiver } from '@/views/order/types/order'
import { DeliverType, type ExpressCompany } from '@/views/order/orderShipment/types'
import { BatchDeliverBody, IcFreightFee, UserAddressDTO } from '@/apis/order/types'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import Decimal from 'decimal.js'

const regionData = useGDRegionDataStore().getterGDRegionData

interface ApiOrderExpressCompany extends ApiOrder {
    expressCompany: ExpressCompany
}

const { divTenThousand } = useConvert()
const $deliveryOrderList = useDeliveryOrderList()
const TabData = ref<ApiOrderExpressCompany[]>([])
const $router = useRouter()
const route = useRoute()

const PageConfig = ref({ size: 1000, current: 1, total: 0 })

type DeliverDialogFormData = {
    deliveryMethod: ChooseDeliverType
    deliverType: keyof typeof DeliverType | ''
    printId: string
    receiver: UserAddressDTO
    expressCompany?: ExpressCompany
    addressaddress: string
}
const deliverDialogFormData = reactive<DeliverDialogFormData>({
    deliveryMethod: '', // 发货方式
    deliverType: '', // 配送方式
    printId: '',
    receiver: { name: '', mobile: '', address: '' },
    // expressCompany: {
    //     expressCompanyCode: '',
    //     expressCompanyName: '',
    // },
    addressaddress: '',
})

const DatchDeliveryFormRules = reactive<FormRules>({
    deliverType: [{ required: true, trigger: 'blur', message: '请选择发货方式' }],
    addressaddress: [{ required: true, trigger: 'change', message: '请选择服务' }],
    printId: [{ required: true, trigger: 'change', message: '请选择' }],
    'expressCompany.expressCompanyCode': [
        {
            validator(rule, value, callback) {
                if (deliverDialogFormData.deliveryMethod === 'VIRTUAL') {
                    callback()
                } else if (!deliverDialogFormData?.expressCompany?.expressCompanyCode) {
                    callback(new Error('请选择物流服务'))
                } else {
                    callback()
                }
            },
        },
    ],
})
//物流公司列表
const companySelectList = ref<any[]>([])
// 发货地址数据
const deliveryAddressData = ref<any[]>([])

watch(
    () => deliverDialogFormData.deliverType,
    (val) => {
        if (val !== 'PRINT_EXPRESS') return
        if (!deliverDialogFormData.addressaddress) {
            ElMessage.info('请添加设置物流')
        }
    },
)

/**
 * 移除商品
 */
function filterOrderList(goods: ApiOrderExpressCompany) {
    TabData.value = TabData.value.filter((order) => order.id !== goods.id)
    $deliveryOrderList.SET_ORDER_LIST(TabData.value)
}

type ChooseDeliverType = '' | 'VIRTUAL' | 'EXPRESS' | 'INTRA_CITY_DISTRIBUTION'

/**
 * 内存中获取待发货商品
 */
function initStoreDeliveryOrderList() {
    TabData.value = $deliveryOrderList.orderList || []
    TabData.value.forEach((item) => {
        item.expressCompany = {
            expressCompanyCode: '',
            expressCompanyName: '',
            expressNo: '',
        }
    })
    console.log('从内存中获取的数据', TabData.value)
    if (['VIRTUAL', 'INTRA_CITY_DISTRIBUTION', 'EXPRESS'].includes(route.query.type as string)) {
        deliverDialogFormData.deliveryMethod = route.query.type as ChooseDeliverType
        if (deliverDialogFormData.deliveryMethod === 'VIRTUAL') {
            deliverDialogFormData.deliverType = 'WITHOUT'
        } else if (deliverDialogFormData.deliveryMethod === 'EXPRESS') {
            deliverDialogFormData.deliverType = 'EXPRESS'
        }
    } else {
        ElMessage.error('已强制返回订单列表页选择发货方式')
        $router.replace('/order/delivery')
    }
}

initStoreDeliveryOrderList()

const shippingMethodMap = {
    EXPRESS: '快递发货',
    INTRA_CITY_DISTRIBUTION: '同城配送',
    VIRTUAL: '虚拟发货',
}

/**
 * 获取全部待发货的订单
 */
async function initOederList() {
    const status = 'UN_DELIVERY'
    const { data, code } = await doGetOrderList({
        ...PageConfig.value,
        status,
        distributionMode: deliverDialogFormData.deliveryMethod,
    })
    loopAssignment(code, data)
    if (route.query?.type === 'INTRA_CITY_DISTRIBUTION') {
        handleDeliveryMethodChange(deliverDialogFormData.deliverType)
    }
}

/**
 * 循环赋值
 */
function loopAssignment(code: number, data: any) {
    if (code === 200) {
        ElMessage.success('导入成功')
        const { records, current, size, total } = data
        console.log(records)
        records.forEach((order: any) => {
            order.expressCompany = {
                expressCompanyCode: '',
                expressCompanyName: '',
                expressNo: '',
            }
        })
        const filterData = records.filter((item: any) => {
            return !item.extra || item.extra?.distributionMode !== 'SHOP_STORE'
        })
        const deliverOrder: any[] = []
        for (let i = 0; i < filterData.length; i++) {
            const payInfo = filterData[i]
            payInfo.shopOrders.forEach((shopOrder: any) => {
                shopOrder.shopOrderItems = shopOrder.shopOrderItems.filter((shopOrderItem: any) => shopOrderItem.sellType !== 'CONSIGNMENT')
            })
            const shopOrders = payInfo.shopOrders.filter((shopOrder: any) => shopOrder.shopOrderItems.length > 0)
            if (shopOrders.length > 0) {
                deliverOrder.push(payInfo)
            }
        }
        TabData.value = deliverOrder
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
 * 获取发货地址 联系人...
 */
const addressDialog = ref(false)

function getSenderAddress(params: UserAddressDTO) {
    const sender = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
    if (!sender) return (addressDialog.value = true)
    const { address, contactName, contactPhone } = sender
    const addressArr = [sender.provinceCode, sender.cityCode, sender.regionCode]
    params.address = `${AddressFn(regionData, addressArr)}${address}`
    params.mobile = contactPhone
    params.name = contactName
}

/**
 * 拼接数据
 * @param {*} params
 */
function getReceiverAddress(params: OrderReceiver) {
    const { name, mobile } = params
    const Receiver = { name, mobile, address: '' }
    Receiver.address = `${params.area.join('')}${params.address}`
    return Receiver
}

const DatchDeliveryFormRef = ref()
/**
 * 确定发货
 */
const handleSubmit = async () => {
    console.log('deliverDialogFormData表单', deliverDialogFormData)
    let data = []
    DatchDeliveryFormRef.value.validate(async (valid: boolean, filed: any) => {
        if (!valid) return
        switch (deliverDialogFormData.deliverType) {
            case 'EXPRESS':
                data = await manualDelivery()
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
    })
}

/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
    const { data, code } = await doGetLogisticsList()
    if (code !== 200) return true
    const sender = data.records.find((item: any) => item.defSend === 'YES')
    if (!sender) return true
}

const logisticsForm = ref({
    address: '',
    contactName: '',
    contactPhone: '',
    zipCode: '',
    Provinces: [],
    defSend: true,
    defReceive: false,
})
/**
 * 表单提交请求
 */
const FormRef = ref()
const submitHandle = async () => {
    await FormRef.value.validate()
    await loadSetLogisticsDddress()
}

const logisticsFormRules = reactive<FormRules>({
    contactName: [
        { required: true, message: '请输入联系人', trigger: 'blur' },
        { max: 10, message: '最多输入10个字符', trigger: 'blur' },
    ],
    contactPhone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3|5|6|7|8|9]\d{9}$/, message: '请输入正确的号码格式', trigger: 'blur' },
    ],
    Provinces: [{ type: 'array', required: true, message: '请选择类别', trigger: 'change' }],
    // 验证未写
    address: [{ required: true, message: '请填写详细地址', trigger: 'blur' }],
})

/**
 * 新增或者编辑地址列表
 */
const loadSetLogisticsDddress = async () => {
    const newLogisticsForm = {
        ...logisticsForm.value,
        provinceCode: logisticsForm.value.Provinces[0],
        cityCode: logisticsForm.value.Provinces[1],
        regionCode: logisticsForm.value.Provinces[2],
        defReceive: logisticsForm.value.defReceive ? 'YES' : 'NO',
        defSend: logisticsForm.value.defSend ? 'YES' : 'NO',
    }
    Reflect.deleteProperty(newLogisticsForm, 'Provinces')
    const { code } = await setLogisticsDddress({ ...newLogisticsForm })
    if (code === 200) {
        ElMessage.success('增加成功')
        addressDialog.value = false
    }
}

/**
 * 手动发货 快递
 */
async function manualDelivery() {
    const valid = await initLogisticsList()
    if (valid) {
        // 没有默认地址弹窗
        addressDialog.value = true
        return []
    }
    const isVlidate = computedTableData.value.every(
        (item) => item.expressCompany.expressCompanyName && item.expressCompany.expressCompanyCode && item.expressCompany.expressNo,
    )
    if (!isVlidate) {
        ElMessage.error({ message: '请选择物流服务或填写运单号' })
        return []
    }
    const data = computedTableData.value.map((order) => {
        const expressCompany = order.expressCompany
        const params: BatchDeliverBody = {
            orderNo: order.no,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType as keyof typeof DeliverType,
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
        }
        return params
    })
    return data
}

/**
 * 打印发货
 */
const printDelivery = () => {
    const sender: UserAddressDTO = { name: '', mobile: '', address: '' }
    getSenderAddress(sender)
    const currentCode = deliverDialogFormData.expressCompany?.expressCompanyCode || ''
    const currentName = companySelectList.value.find((item) => item.logisticsCompanyCode === currentCode)?.logisticsCompanyName
    const data = computedTableData.value.map((order) => {
        const receiver = getReceiverAddress(order.shopOrders[0].orderReceiver || order.orderReceiver)
        const params: BatchDeliverBody = {
            orderNo: order.no,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType as keyof typeof DeliverType,
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
 * 无需物流
 */
const unDelivery = () => {
    const data = computedTableData.value.map((order) => {
        const params: BatchDeliverBody = {
            orderNo: order.no,
            orderDelivery: {
                deliverType: deliverDialogFormData.deliverType as keyof typeof DeliverType,
                cargo: '',
                receiver: order.shopOrders[0].orderReceiver || order.orderReceiver,
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
 * 发货清除缓存跳路由
 * @param {*} params
 */
async function batchDeliver(params: BatchDeliverBody[]) {
    const { code, data: res, msg } = await doPostOrderBatchDeliver(params)
    if (code !== 200) {
        if (res) {
            const errIndex = TabData.value.findIndex((item) => item.no === res)
            // errIndex 之前的数据全部成功 从TabData删除
            if (errIndex > -1) {
                TabData.value.splice(0, errIndex)
                $deliveryOrderList.SET_ORDER_LIST(TabData.value)
                return ElMessage.warning({ message: msg || `订单号为:${res}的商品发货失败,请确认后重试` })
            }
        }
        return ElMessage.error({ message: msg || '系统错误！' })
    }
    $deliveryOrderList.SET_ORDER_LIST([])
    ElMessage.success({ message: '发货成功' })
    $router.replace({ name: 'orderIndex' })
}

const cancelBatchDeliver = () => {
    $deliveryOrderList.SET_ORDER_LIST([])
    $router.replace({ name: 'orderIndex' })
}
const HandleloadDeliverDialogFormData = (e: DeliverDialogFormData) => {
    deliverDialogFormData.addressaddress = e.addressaddress
    deliverDialogFormData.printId = e.printId
    deliverDialogFormData.receiver = e.receiver
}
const HandleloadCompanySelectListData = (e: any) => {
    companySelectList.value = e
}
const HandleloadDeliveryAddressData = (e: any[]) => {
    deliveryAddressData.value = e
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
const HandleloadexpressCompanyMapData = (e: LogisticsCompany) => {
    if (e) {
        deliverDialogFormData.expressCompany = {
            ...e,
            expressCompanyCode: e.logisticsCompanyCode,
            expressCompanyName: e.logisticsCompanyName,
        }
        TabData.value.forEach((item) => {
            item.expressCompany.expressCompanyName = e.logisticsCompanyName
            item.expressCompany.expressCompanyCode = e.logisticsCompanyCode
        })
    } else {
        deliverDialogFormData.expressCompany = {
            expressCompanyCode: '',
            expressCompanyName: '',
        }
    }
}

const computedTableData = computed(() => {
    return TabData.value
})
export type CompanySelectList = {
    logisticsCompanyCode: string
    logisticsCompanyName: string
    id: Long
}
const handleExpressCompanyName = (val: string, row: ApiOrderExpressCompany, type: 'select' | 'input' = 'select') => {
    const data = TabData.value.find((item) => item.no === row.no)
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

const deliveryMethodList = ref<{ label: string; value: string }[]>([])
if (route.query?.type === 'INTRA_CITY_DISTRIBUTION') {
    doGetShopDeliveryMethod().then((res) => {
        deliveryMethodList.value = res.data
        if (res.data.length) {
            deliverDialogFormData.deliverType = res.data[0].value
            handleDeliveryMethodChange(deliverDialogFormData.deliverType)
        }
    })
}

const freightFee = ref<IcFreightFee>({
    balance: -100,
    orderPriceMap: {},
    totalPrice: 0,
})
const handleDeliveryMethodChange = async (e: keyof typeof DeliverType | '') => {
    if (e === 'IC_OPEN') {
        const res = await doGetBatchDeliveryPrice(computedTableData.value.map((item) => item.no))
        freightFee.value = res.data
    }
}

/**
 * uupt余额不足提示
 */
const deliverTypeError = ref('')

/**
 * 是否不可发货  非商家配送且余额不足
 */
const disableDeliverSubmit = computed(() => {
    if (deliverDialogFormData.deliverType !== 'IC_OPEN') {
        return false
    }
    return new Decimal(freightFee.value.balance).lessThan(freightFee.value.totalPrice)
})

watch(
    () => disableDeliverSubmit.value,
    (val) => {
        nextTick(() => {
            deliverTypeError.value = val ? 'UU跑腿余额不足，请充值！！！' : ''
        })
    },
)
</script>

<template>
    <el-form
        v-if="deliverDialogFormData.deliveryMethod"
        ref="DatchDeliveryFormRef"
        :model="deliverDialogFormData"
        :rules="DatchDeliveryFormRules"
        class="DatchDelivery_container fdc1 overh"
        label-position="left"
    >
        <div class="shipping_method_title">
            {{ shippingMethodMap[deliverDialogFormData.deliveryMethod] }}
        </div>
        <el-form-item
            v-if="deliverDialogFormData.deliveryMethod !== 'VIRTUAL'"
            :error="deliverTypeError"
            label="发货方式"
            label-width="100px"
            prop="deliverType"
            style="margin-left: 16px"
        >
            <el-select
                v-if="deliverDialogFormData.deliveryMethod === 'INTRA_CITY_DISTRIBUTION'"
                v-model="deliverDialogFormData.deliverType"
                placeholder="请选择同城发货方式"
                style="width: 200px"
                @change="handleDeliveryMethodChange"
            >
                <el-option v-for="item in deliveryMethodList" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <el-radio-group
                v-else-if="deliverDialogFormData.deliveryMethod === 'EXPRESS'"
                v-model="deliverDialogFormData.deliverType"
                class="ml-4"
                size="default"
            >
                <el-radio value="EXPRESS" size="small">手动发货</el-radio>
                <el-radio value="PRINT_EXPRESS" size="small">打印快递单并发货</el-radio>
            </el-radio-group>
            <div v-else>虚拟发货</div>
            <div v-show="deliverDialogFormData.deliverType === 'IC_OPEN'" style="width: 100%">
                <div class="tips fcenter">
                    <div class="red">账户余额：{{ divTenThousand(freightFee.balance) }}元，预估 {{ divTenThousand(freightFee.totalPrice) }}元</div>
                </div>
            </div>
        </el-form-item>
        <div v-else style="margin-left: 16px; color: #f54319">您可通过网络形式(如:聊天工具将商品发给用户)发货</div>
        <DeliveryListSelect
            v-if="!['INTRA_CITY_DISTRIBUTION', 'VIRTUAL'].includes(deliverDialogFormData.deliveryMethod)"
            :deliver-dialog-form-data="deliverDialogFormData"
            :loadCompanySelectListData="HandleloadCompanySelectListData"
            :loadDeliverDialogFormData="HandleloadDeliverDialogFormData"
            :loadDeliveryAddressData="HandleloadDeliveryAddressData"
            :loadexpressCompanyMapData="HandleloadexpressCompanyMapData"
            style="margin-left: 16px"
        />
        <el-row justify="end" style="width: 100%">
            <el-button link type="primary" @click="initOederList">重新导入</el-button>
        </el-row>
        <el-form-item class="fdc1 overh table_item">
            <DeliveryListExpressTable
                v-if="deliverDialogFormData.deliverType === 'EXPRESS'"
                :changeExpressCompanyName="handleExpressCompanyName"
                :companySelectList="companySelectList"
                :filterOrderList="filterOrderList"
                :tableData="computedTableData"
            />
            <UnExpressTable
                v-else
                :company-select-list="companySelectList"
                :freightFee="freightFee"
                :showFreightFee="deliverDialogFormData.deliverType === 'IC_OPEN'"
                :showPeople="deliverDialogFormData.deliveryMethod !== 'VIRTUAL'"
                :tabData="computedTableData"
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

    <!-- 弹出层 -->
    <el-dialog v-model="addressDialog" width="650px">
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">添加发货地址</h4>
                <el-tag type="warning">你还未填写默认退货地址，为保证消费体验，请先填写默认地址再进行发货哦</el-tag>
            </div>
        </template>
        <el-form ref="FormRef" :model="logisticsForm" :rules="logisticsFormRules" label-position="right" label-width="90px" style="max-width: 100%">
            <el-row :gutter="8">
                <el-col :span="12">
                    <el-form-item label="联系人" prop="contactName">
                        <el-input v-model="logisticsForm.contactName" maxlength="10" placeholder="请输入联系人" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="contactPhone">
                        <el-input
                            v-model="logisticsForm.contactPhone"
                            maxlength="11"
                            onkeyup="value=value.replace(/[^\d]/g,'')"
                            placeholder="请输入手机号"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="地区选择" prop="Provinces">
                        <el-cascader
                            v-model="logisticsForm.Provinces"
                            :options="(regionData as CascaderOption[])"
                            :style="{ width: '100%' }"
                            filterable
                            placeholder="请选择省/市/区"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="详细地址" prop="address">
                        <el-input v-model="logisticsForm.address" maxlength="50" placeholder="" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="邮政编号" prop="zipCode">
                        <el-input
                            v-model="logisticsForm.zipCode"
                            maxlength="6"
                            onkeyup="value=value.replace(/[^\d]/g,'')"
                            placeholder="请输入邮政编号"
                        />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="addressDialog = false">取消</el-button>
                <el-button type="primary" @click="submitHandle">提交</el-button>
            </span>
        </template>
    </el-dialog>
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
    .tips {
        color: rgb(96, 98, 102);
        margin-left: 10px;

        .red {
            margin-right: 10px;
        }
    }
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
