<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { Fn, useVModel } from '@vueuse/core'
import { AddressFn } from '@/components/q-address'
import { doPutOrderDetailsByOrderNo, importNeedDeliveryOrders } from '@/apis/order'
import { doGetLogisticsList } from '@/apis/set/platformDelivery'
import { doGetLogisticsExpressUsableList } from '@/apis/freight/freightAdd'
import type { ApiNotDelivey, ApiOrderReceiver, ParamsDeliver } from '@/views/order/orderShipment/types'
import type { FormInstance, FormRules, TableInstance } from 'element-plus'
import { DISTRIBUTION } from '../../types/order'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

type ApiEnumLogisticCompany = 'id' | 'logisticsCompanyCode' | 'logisticsCompanyName' | 'logisticsCompanyStatus'
export type ApiLogisticCompany = Record<ApiEnumLogisticCompany, string>
interface ApiAddressItem {
    address: string
    cityCode: string
    contactName: string
    contactPhone: string
    defReceive: string
    defSend: string
    deleted: false
    id: string
    provinceCode: string
    regionCode: string
    shopId: string
    defSelfSupplier?: 'YES' | 'NO'
    defSelfShop?: 'YES' | 'NO'
}

const $props = defineProps({
    currentNo: {
        type: String,
        required: true,
    },
    isShow: { type: Boolean, required: true },
    currentShopOrderNo: { type: String, required: true },
})
const $emit = defineEmits(['update:isShow'])
const { divTenThousand } = useConvert()
const _isShow = useVModel($props, 'isShow', $emit)
const tableRef = ref<TableInstance>()
const currentShopType = ref<'shopOrders' | 'supplierOrders'>('shopOrders')
// 校验实例
const ruleFormRef = ref<FormInstance>()
// 校验
const rules = reactive<FormRules>({
    address: [],
    company: [
        {
            required: true,
            message: '请选择物流公司',
            trigger: 'change',
        },
    ],
    expressNo: [
        { required: true, message: '请填写物流单号', trigger: 'blur' },
        { min: 8, max: 20, message: '请填写正确的物流单号', trigger: 'blur' },
    ],
})
// 发货类型枚举
const deliverType = [
    { key: 'EXPRESS', value: '手动发货' },
    { key: 'PRINT_EXPRESS', value: '打印快递单并发货' },
    // { key: 'WITHOUT', value: '无需物流发货' },
]
const deliverDialogFormData = reactive({
    deliverType: 'EXPRESS',
    receiver: { name: '', mobile: '', address: '' },
    expressCompany: '',
    expressCode: '',
    addressaddress: '',
    expressNo: '',
})
// 未发货列表
const deliverList = ref<ApiNotDelivey[]>([])
// 发货地址数据
const deliveryAddressData = ref<ApiAddressItem[]>([])
// 地区信息
const orderReceiver = ref<ApiOrderReceiver>()
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
//打印机列表
const ParentListFn = inject('reloadParentListFn') as Fn
const currentOrderDispatcher = reactive({
    shopOrders: {
        orderReceiver: {} as ApiOrderReceiver,
        deliverList: [] as ApiNotDelivey[],
        deliverDialogFormData: {
            deliverType: 'EXPRESS',
            receiver: { name: '', mobile: '', address: '', area: [] as string[] },
            expressCompany: '',
            expressCode: '',
            addressaddress: '',
            expressNo: '',
        },
    },
    supplierOrders: {
        orderReceiver: {} as ApiOrderReceiver,
        deliverList: [] as ApiNotDelivey[],
        deliverDialogFormData: {
            deliverType: 'EXPRESS',
            receiver: { name: '', mobile: '', address: '', area: [] as string[] },
            expressCompany: '',
            expressCode: '',
            addressaddress: '',
            expressNo: '',
        },
    },
})
const distributionMode = ref<keyof typeof DISTRIBUTION>('EXPRESS')
const platform = ref('')

initNotDeliverList()
initLogisticsList()
initLogisticsCompany()

/**
 * 获取未发货订单
 */
async function initNotDeliverList() {
    if (!$props.currentNo || !$props.currentShopOrderNo) return ElMessage.error('未填写订单号')
    const { data } = await importNeedDeliveryOrders([$props.currentNo])
    if (data?.shopOrders?.length) {
        const { orderReceiver: Receiver, shopOrders, extra: distributeData } = data.shopOrders[0]
        const shopOrderItems = shopOrders.map((item) => item.shopOrderItems).flat()
        currentOrderDispatcher.shopOrders.orderReceiver = Receiver
        for (const key in currentOrderDispatcher.shopOrders.deliverDialogFormData.receiver) {
            currentOrderDispatcher.shopOrders.deliverDialogFormData.receiver[key] = Receiver[key as keyof typeof Receiver]
        }
        // 收货人地址信息拼接
        currentOrderDispatcher.shopOrders.deliverDialogFormData.receiver.address = `${Receiver.address}`
        // for 记录发货数量初始值
        for (let i = 0; i < shopOrderItems.length; i++) {
            shopOrderItems[i].deliveryNum = shopOrderItems[i].num
        }
        currentOrderDispatcher.shopOrders.deliverList = shopOrderItems
        if (!shopOrderItems?.length) {
            currentShopType.value = 'supplierOrders'
        }
        distributionMode.value = distributeData?.distributionMode || 'EXPRESS'
        platform.value = distributeData?.platform || ''
        deliverDialogFormData.deliverType = distributionMode.value === 'VIRTUAL' ? 'WITHOUT' : 'EXPRESS'
    } else {
        currentShopType.value = 'supplierOrders'
    }
    if (data?.supplierOrders?.length) {
        const { orderReceiver: Receiver, shopOrders, extra: distributeData } = data.supplierOrders[0]
        const shopOrderItems = shopOrders?.[0]?.shopOrderItems || []
        currentOrderDispatcher.supplierOrders.orderReceiver = Receiver
        for (const key in currentOrderDispatcher.supplierOrders.deliverDialogFormData.receiver) {
            currentOrderDispatcher.supplierOrders.deliverDialogFormData.receiver[key as keyof typeof deliverDialogFormData.receiver] = Receiver[key]
        }
        // 收货人地址信息拼接
        currentOrderDispatcher.supplierOrders.deliverDialogFormData.receiver.address = `${Receiver.address}`
        // for 记录发货数量初始值
        for (let i = 0; i < shopOrderItems.length; i++) {
            shopOrderItems[i].deliveryNum = shopOrderItems[i].num
        }
        currentOrderDispatcher.supplierOrders.deliverList = shopOrderItems
        distributionMode.value = distributeData?.distributionMode || 'EXPRESS'
        platform.value = distributeData?.platform || ''
        deliverDialogFormData.deliverType = distributionMode.value === 'VIRTUAL' ? 'WITHOUT' : 'EXPRESS'
    }
    // return
    // const {
    //     data: { orderReceiver: Receiver, shopOrderItems },
    //     code,
    // } = await dogetOrderNotDetailsByOrderNo($props.currentNo, $props.currentShopOrderNo)
    // if (code !== 200) return ElMessage.error('获取未发货订单失败')
    // orderReceiver.value = Receiver
    // for (const key in deliverDialogFormData.receiver) {
    //     deliverDialogFormData.receiver[key as keyof typeof deliverDialogFormData.receiver] = Receiver[key]
    // }
    // // 收货人地址信息拼接
    // deliverDialogFormData.receiver.address = `${Receiver.address}`
    // // for 记录发货数量初始值
    // for (let i = 0; i < shopOrderItems.length; i++) {
    //     shopOrderItems[i].deliveryNum = shopOrderItems[i].num
    // }
    // deliverList.value = shopOrderItems
    tableRef.value?.toggleAllSelection()
}
/**
 * 获取可用物流服务
 */
async function initLogisticsCompany() {
    const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
    if (code !== 200) {
        return ElMessage.error('获取物流服务列表失败')
    } else {
        companySelectList.value = data.records
    }
}
/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
    const { data, code } = await doGetLogisticsList({})
    if (code !== 200) {
        ElMessage({ message: '请刷新重试...', type: 'warning' })
    } else {
        deliveryAddressData.value = data.records?.filter((item: ApiAddressItem) => item.defSend === 'YES')
    }
    console.log('deliveryAddressData.value', deliveryAddressData.value)
    const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
    if (!sender) {
        return ElMessage.error('请设置默认发货地址')
    }
    deliverDialogFormData.addressaddress = sender.id
}

/**
 * 发货请求
 */
const handleeSubmit = async () => {
    const orderList = tableRef.value?.getSelectionRows()
    if (!orderList.length) return ElMessage.error({ message: '请选择商品' })
    try {
        if (!ruleFormRef.value) {
            return
        }
        await ruleFormRef.value.validate()
        const params: ParamsDeliver = {
            deliverType: '',
            receiver: { name: '', mobile: '', address: '' },
            sender: { name: '', mobile: '', address: '' },
            cargo: '',
            remark: '',
            dropDeliver: true,
            expressCompany: {
                expressCompanyName: '',
                expressCompanyCode: '',
                expressNo: '',
            },
            items: [],
            shopId: '',
            selfShopType: currentShopType.value === 'shopOrders' ? 'SELF_SHOP' : 'SELF_SUPPLIER',
        }
        let { deliverType, receiver, expressCompany, expressNo } = currentOrderDispatcher[currentShopType.value].deliverDialogFormData
        deliverType =
            distributionMode.value === 'VIRTUAL' ? 'WITHOUT' : currentOrderDispatcher[currentShopType.value].deliverDialogFormData.deliverType

        params.deliverType = deliverType
        params.receiver = receiver
        params.expressCompany = null
        console.log(distributionMode.value, '')
        if (deliverType !== 'WITHOUT') {
            console.log('expressCompany', expressCompany)
            params.expressCompany = { expressCompanyName: '', expressCompanyCode: '', expressNo: '' }
            params.expressCompany.expressCompanyCode = expressCompany
            const company = companySelectList.value.find((item) => item.logisticsCompanyCode === expressCompany)
            if (company) {
                params.expressCompany.expressCompanyName = company.logisticsCompanyName
            } else {
                return Promise.reject('获取物流公司失败')
            }
            params.expressCompany.expressNo = expressNo
        }
        params.items = orderList.map((item: ApiNotDelivey) => ({ itemId: item.id, num: item.deliveryNum }))
        if (params.deliverType === 'PRINT_EXPRESS') {
            //  打印面单发货携带发货人信息
            getSenderAddress(params)
        }
        params.shopId = orderList?.[0]?.shopId
        const { code, msg } = await doPutOrderDetailsByOrderNo($props.currentNo, params)
        if (code !== 200) return ElMessage.error(msg || '发货失败')
        _isShow.value = false
        ParentListFn()
        ElMessage.success('发货成功')
    } catch (error) {
        console.dir(error)
    }
}
/**
 * 获取发货地址 联系人...
 */
function getSenderAddress(params: ParamsDeliver) {
    const sender = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
    if (!sender) return ElMessage.error({ message: '未获取到发货信息' })
    const { address, contactName, contactPhone } = sender
    const addressArr = [sender.provinceCode, sender.cityCode, sender.regionCode]
    params.sender.address = `${AddressFn(regionData, addressArr)}${address}`
    params.sender.mobile = contactPhone
    params.sender.name = contactName
}
const handleClose = () => {
    _isShow.value = false
}
const handleChangeShopType = () => {
    tableRef.value?.toggleAllSelection()
    currentOrderDispatcher[currentShopType.value].deliverDialogFormData.addressaddress = ''
    if (deliveryAddressDataComputed.value.length === 1) {
        currentOrderDispatcher[currentShopType.value].deliverDialogFormData.addressaddress = deliveryAddressDataComputed.value?.[0]?.id
    }
}
const handleChangeDeliveryType = (val: any) => {
    if (val === 'PRINT_EXPRESS') {
        if (deliveryAddressDataComputed.value.length === 1) {
            currentOrderDispatcher[currentShopType.value].deliverDialogFormData.addressaddress = deliveryAddressDataComputed.value?.[0]?.id
        }
    }
}
const deliveryAddressDataComputed = computed(() => {
    return deliveryAddressData.value.filter((item) =>
        currentShopType.value === 'shopOrders' ? item.defSelfShop === 'YES' : item.defSelfSupplier === 'YES',
    )
})

const handleDelete = (index: number) => {
    if (currentOrderDispatcher?.[currentShopType.value]?.deliverList.length > 1) {
        currentOrderDispatcher?.[currentShopType.value]?.deliverList.splice(index, 1)
    } else {
        ElMessage.warning('至少保留一个商品规格进行发货！')
    }
}

const isNotOpenOrder = computed(() => platform.value === 'WECHAT_MINI_APP' && ['VIRTUAL', 'INTRA_CITY_DISTRIBUTION'].includes(distributionMode.value))
</script>

<template>
    <el-form ref="ruleFormRef" :model="currentOrderDispatcher?.[currentShopType]?.deliverDialogFormData" class="notShipment" :rules="rules">
        <el-form-item
            v-if="distributionMode === 'EXPRESS' || distributionMode === 'INTRA_CITY_DISTRIBUTION'"
            label-width="90px"
            style="align-items: center"
        >
            <template #label>
                <div>发货方式</div>
            </template>
            <template v-if="distributionMode === 'EXPRESS'">
                <el-radio-group
                    v-model="currentOrderDispatcher[currentShopType].deliverDialogFormData.deliverType"
                    class="ml-4"
                    size="large"
                    @change="handleChangeDeliveryType"
                >
                    <el-radio v-for="item in deliverType" :key="item.key" :value="item.key" size="large">{{ item.value }}</el-radio>
                </el-radio-group>
            </template>
            <template v-else-if="distributionMode === 'INTRA_CITY_DISTRIBUTION'">
                <span style="color: #f00">由【达达快送】提供同城配送服务</span>
            </template>
        </el-form-item>
        <p v-else style="color: #f00; padding-bottom: 18px">您可通过网络形式(如:聊天工具将商品发给用户)发货</p>
        <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px" prop="">
            <template #label>
                <div class="send">物流服务</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-select
                        v-model="currentOrderDispatcher[currentShopType].deliverDialogFormData.expressCompany"
                        class="m-2"
                        placeholder="请选择"
                        style="width: 100%; height: 30px"
                    >
                        <el-option
                            v-for="item in companySelectList"
                            :key="item.logisticsCompanyName"
                            :label="item.logisticsCompanyName"
                            :value="item.logisticsCompanyCode"
                        />
                    </el-select>
                </el-col>
            </el-row>
        </el-form-item>
        <el-form-item
            v-if="distributionMode === 'EXPRESS' && currentOrderDispatcher[currentShopType].deliverDialogFormData.deliverType === 'EXPRESS'"
            label-width="90px"
            prop="expressNo"
        >
            <template #label>
                <div>物流单号</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-input
                        v-model="currentOrderDispatcher[currentShopType].deliverDialogFormData.expressNo"
                        placeholder=""
                        style="width: 100%; height: 30px"
                        maxlength="40"
                    />
                </el-col>
            </el-row>
        </el-form-item>
        <el-form-item
            v-if="currentOrderDispatcher?.[currentShopType]?.deliverDialogFormData.deliverType === 'PRINT_EXPRESS'"
            label-width="90px"
            prop=""
        >
            <template #label>
                <div>发货地址</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-select
                        v-model="currentOrderDispatcher[currentShopType].deliverDialogFormData.addressaddress"
                        placeholder="选择发货地址"
                        style="width: 100%; height: 30px"
                    >
                        <el-option
                            v-for="item in deliveryAddressDataComputed"
                            :key="item.id"
                            :value="item.id"
                            :label="`${AddressFn(regionData, [item.provinceCode, item.cityCode, item.regionCode])}${item.address}`"
                        />
                    </el-select>
                </el-col>
            </el-row>
        </el-form-item>
        <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px">
            <template #label>
                <div class="send">配送信息</div>
            </template>
            <div v-if="currentOrderDispatcher?.[currentShopType]?.orderReceiver">
                <span style="margin-right: 16px">{{ currentOrderDispatcher?.[currentShopType]?.orderReceiver.name }}</span>
                <span style="margin-right: 16px">{{ currentOrderDispatcher?.[currentShopType]?.orderReceiver.mobile }}</span>
                <span
                    >{{ currentOrderDispatcher?.[currentShopType]?.deliverDialogFormData.receiver.area?.join('') }}
                    {{ currentOrderDispatcher?.[currentShopType]?.deliverDialogFormData.receiver.address }}</span
                >
            </div>
            <div v-else>暂无地址信息</div>
        </el-form-item>
        <!-- <el-form-item v-if="currentOrderDispatcher?.[currentShopType]?.deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px" prop=""> -->

        <el-table
            ref="tableRef"
            empty-text="暂无数据~"
            :data="currentOrderDispatcher?.[currentShopType]?.deliverList"
            max-height="250"
            style="width: 100%; margin-bottom: 20px"
            :header-cell-style="{
                backgroundColor: '#E9ECF0',
                color: '#333',
                height: '48px',
            }"
            :border="true"
            :cell-style="{ color: '#333', height: '92px' }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="商品" align="center">
                <template #default="{ row }">
                    <div style="display: flex; justify-content: space-between; padding: 0px 10px">
                        <div style="display: flex; width: 70%">
                            <el-avatar style="width: 68px; height: 68px" shape="square" size="large" :src="row.image" />
                            <div class="info">
                                <div class="productName">
                                    {{ row.productName }}
                                </div>
                                <div class="specs">{{ row.specs?.join('、') }}</div>
                            </div>
                        </div>
                        <div class="price">
                            <div>￥{{ divTenThousand(row.dealPrice) }}</div>
                            <div>x{{ row.num }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="待发货数" :width="distributionMode === 'EXPRESS' ? 140 : 300" align="center">
                <template #default="{ row }">
                    <div>{{ row.num }}</div>
                </template>
            </el-table-column>
            <el-table-column v-if="distributionMode === 'EXPRESS'" label="发货数" width="140" align="center">
                <template #default="{ row }">
                    <el-input-number
                        v-model="row.deliveryNum"
                        style="width: 100%"
                        :controls="false"
                        placeholder=""
                        :min="1"
                        :max="row.num"
                        :disabled="isNotOpenOrder"
                    />
                </template>
            </el-table-column>
            <el-table-column v-if="distributionMode === 'EXPRESS'" label="操作" width="100" align="center">
                <template #default="{ $index }">
                    <el-button type="danger" link @click="handleDelete($index)">移除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-form>
    <footer class="footer"><el-button @click="handleClose">取消</el-button> <el-button type="primary" @click="handleeSubmit">确认</el-button></footer>
</template>

<style lang="scss" scoped>
@include b(footer) {
    @include flex(flex-end);
}
@include b(info) {
    flex: 1;
    padding-left: 10px;
    text-align: left;
    display: flex;
    flex-direction: column;
}
@include b(productName) {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
}
@include b(specs) {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #999;
}
@include b(price) {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: end;
    margin-right: 20px;
}
</style>
