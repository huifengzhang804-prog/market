<script setup lang="ts">
import { ref, reactive, inject } from 'vue'
import { ElMessage } from 'element-plus'
import { Fn, useVModel } from '@vueuse/core'
import { AddressFn } from '@/components/q-address'
import QAddress from '@/components/q-address/q-address.vue'
import { dogetOrderNotDetailsByOrderNo, doPutOrderDetailsByOrderNo } from '@/apis/order'
import { doGetPrintList } from '@/apis/freight/freight-print'
import { doLogisticsSetDef, doGetLogisticsCompany, doGetLogisticsList, doGetLogisticsExpressUsableList } from '@/apis/freight/freightAdd'
import type { ApiNotDelivey, ApiOrderReceiver, ParamsDeliver } from '@/views/order/orderShipment/types'
import type { ApiLogisticCompany, ApiAddressItem } from '@/views/freight/components/types'
import type { FormInstance, FormRules, TableInstance } from 'element-plus'
import { DISTRIBUTION } from '../../types/order'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
type DeviceList = {
    deviceNo: string
    id: string
    printName: string
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
    expressCompany: [{ required: true, message: '请选择物流服务', trigger: 'blur' }],
})
// 发货类型枚举
const deliverType = [
    { key: 'EXPRESS', value: '手动发货' },
    { key: 'PRINT_EXPRESS', value: '打印快递单并发货' },
    // { key: 'WITHOUT', value: '无需物流发货' },
]
const deliverDialogFormData = reactive({
    deliverType: 'EXPRESS',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
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

const distributionMode = ref<keyof typeof DISTRIBUTION>('EXPRESS')
const platform = ref('')

initNotDeliverList()
initLogisticsList()
initLogisticsCompany()

/**
 * @description: 获取未发货订单
 * @returns {*}
 */
async function initNotDeliverList() {
    if (!$props.currentNo || !$props.currentShopOrderNo) return ElMessage.error('未填写订单号')
    const {
        data: { orderReceiver: Receiver, shopOrderItems, extra: distributeData },
        code,
    } = await dogetOrderNotDetailsByOrderNo($props.currentNo, $props.currentShopOrderNo)
    if (code !== 200) return ElMessage.error('获取未发货订单失败')
    orderReceiver.value = Receiver
    deliverDialogFormData.receiver = Receiver
    // for 记录发货数量初始值
    for (let i = 0; i < shopOrderItems.length; i++) {
        shopOrderItems[i].deliveryNum = shopOrderItems[i].num
    }
    distributionMode.value = distributeData?.distributionMode || 'EXPRESS'
    platform.value = distributeData?.platform || ''
    deliverDialogFormData.deliverType = distributionMode.value === 'VIRTUAL' ? 'WITHOUT' : 'EXPRESS'
    deliverList.value = shopOrderItems
    tableRef.value?.toggleAllSelection()
}
/**
 * @description: 获取可用物流服务
 * @returns {*}
 */
async function initLogisticsCompany() {
    const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
    if (code !== 200) {
        ElMessage.error('获取物流服务列表失败')
    } else {
        companySelectList.value = data.records
    }
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

    console.log('deliveryAddressData.value', deliveryAddressData.value)
    const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
    if (!sender) {
        return ElMessage.error('请设置默认发货地址')
    }
    deliverDialogFormData.addressaddress = sender.id
}

/**
 * @description: 发货请求
 * @returns {*}
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
            receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
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
        }
        const { deliverType, receiver, expressCompany, expressNo } = deliverDialogFormData
        params.deliverType = deliverType
        params.receiver = receiver
        params.expressCompany = null
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
 * @description: 获取发货地址 联系人...
 * @returns {*}
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
const isNotOpenOrder = computed(() => platform.value === 'WECHAT_MINI_APP' && distributionMode.value === 'VIRTUAL')
</script>

<template>
    <el-form ref="ruleFormRef" :model="deliverDialogFormData" class="notShipment" :rules="rules">
        <el-table
            ref="tableRef"
            empty-text="暂无数据~"
            :data="deliverList"
            max-height="250"
            style="width: 100%; margin-bottom: 20px"
            :header-row-style="() => ({ fontSize: '14px', color: '#333333', fontWeight: 'bold' })"
        >
            <el-table-column v-if="!isNotOpenOrder" type="selection" width="55" />
            <el-table-column label="商品信息">
                <template #default="{ row }">
                    <el-row>
                        <el-avatar style="width: 68px; height: 68px" shape="square" size="large" :src="row.image" />
                        <div style="width: 200px; padding-left: 10px">
                            <div>
                                <div class="notShipment__show">{{ row.productName }}</div>
                            </div>
                            <div>{{ row.specs?.join('、') }}</div>
                            <el-row style="width: 100px" justify="space-between" align="middle" class="money_text"
                                >￥{{ divTenThousand(row.dealPrice).mul(row.num) }}
                            </el-row>
                        </div>
                    </el-row>
                </template>
            </el-table-column>
            <el-table-column label="数量" width="100">
                <template #default="{ row }">
                    <div>*{{ row.num }}</div>
                </template>
            </el-table-column>
            <el-table-column label="发货数量" width="100">
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
        </el-table>
        <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px">
            <template #label>
                <div class="send">收货地址</div>
            </template>
            <div v-if="orderReceiver">
                {{ orderReceiver.name }},{{ orderReceiver.mobile }}
                <!-- <q-address :address="['330000', '330100', '330122']" /> -->
                {{ deliverDialogFormData.receiver.area?.join('') }}
                {{ deliverDialogFormData.receiver.address }}
            </div>
            <div v-else>暂无地址信息</div>
        </el-form-item>
        <el-form-item label-width="90px">
            <template #label>
                <div>发货方式</div>
            </template>
            <template v-if="distributionMode === 'EXPRESS'">
                <el-radio-group v-model="deliverDialogFormData.deliverType" class="ml-4" size="large">
                    <el-radio v-for="item in deliverType" :key="item.key" :label="item.key" size="large">{{ item.value }}</el-radio>
                </el-radio-group>
            </template>
            <template v-else>
                <span style="color: #f00">您可通过网络形式(如:聊天工具将商品发给用户)发货</span>
            </template>
        </el-form-item>
        <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px" prop="expressCompany">
            <template #label>
                <div class="send">物流服务</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-select v-model="deliverDialogFormData.expressCompany" class="m-2" placeholder="请选择" style="width: 100%; height: 30px">
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
        <!-- <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px" prop="">
            <template #label>
                <div class="send">选择打印机</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-select v-model="deliverDialogFormData.expressCode" class="m-2" placeholder="选择打印机" style="width: 100%; height: 30px">
                        <el-option v-for="item in printerList" :key="item.id" :label="item.printName" :value="item.id" />
                    </el-select>
                </el-col>
                
            </el-row>
        </el-form-item> -->
        <el-form-item v-if="deliverDialogFormData.deliverType === 'EXPRESS'" label-width="90px" prop="expressNo">
            <template #label>
                <div>物流单号</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-input v-model="deliverDialogFormData.expressNo" placeholder="" style="width: 100%; height: 30px" maxlength="40" />
                </el-col>
            </el-row>
        </el-form-item>
        <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label-width="90px" prop="">
            <template #label>
                <div>发货地址</div>
            </template>
            <el-row style="width: 100%">
                <el-col :span="20">
                    <el-select v-model="deliverDialogFormData.addressaddress" placeholder="选择发货地址" style="width: 100%; height: 30px">
                        <el-option
                            v-for="item in deliveryAddressData"
                            :key="item.id"
                            :value="item.id"
                            :label="`${item.area?.join('')}${item.address}`"
                        />
                    </el-select>
                </el-col>
            </el-row>
        </el-form-item>
    </el-form>
    <footer class="footer"><el-button @click="handleClose">取消</el-button> <el-button type="primary" @click="handleeSubmit">确认</el-button></footer>
</template>

<style lang="scss" scoped>
@import './style/table.scss';
@include b(footer) {
    @include flex(flex-end);
}
</style>
