<script setup lang="ts">
import { ElMessage, TableInstance } from 'element-plus'
import { Fn, useVModel } from '@vueuse/core'
import { dogetOrderNotDetailsByOrderNo, doPutOrderDetailsByOrderNo, setLogisticsDddress, doGetLogisticsList } from '@/apis/order'
import type { Address, ApiNotDelivey, ApiOrderReceiver, DeliverType, ParamsDeliver } from '@/views/order/orderShipment/types'
import type { FormInstance, FormRules } from 'element-plus'
import ShipmentSelect from '@/q-plugin/freight/ShipmentSelect.vue'
import { DISTRIBUTION } from '../../types/order'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

export interface ParamsDeliverWithNoString extends Omit<ParamsDeliver, 'deliverType'> {
    deliverType: '' | ParamsDeliver['deliverType']
}

export interface LogisticsInfo {
    freightId: string
    id: string
    logisticsCompanyCode: string
    logisticsCompanyName: string
    printName: string
}

export interface DeliverDialogForm {
    deliverType: keyof typeof DeliverType
    receiver: { name: string; mobile: string; address: string; area: [string, string, string?] }
    expressCompany: string
    addressaddress: string
    expressNo: string
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
    deliverType: [
        {
            required: true,
            message: '请选择发货方式',
            trigger: 'change',
        },
    ],
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
    expressCompany: [{ required: true, message: '请选择物流服务', trigger: 'change' }],
})
const deliverDialogFormData = reactive<DeliverDialogForm>({
    deliverType: '' as 'WITHOUT' | 'EXPRESS' | 'PRINT_EXPRESS' | 'IC_MERCHANT' | 'IC_OPEN',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
    expressCompany: '',
    addressaddress: '',
    expressNo: '',
})
// 未发货列表
const deliverList = ref<ApiNotDelivey[]>([])
// 发货地址数据
const deliveryAddressData = ref<Address[]>([])
// 地区信息
const orderReceiver = ref<ApiOrderReceiver>()
//物流公司列表
const companySelectList = ref<LogisticsInfo[]>([])
//打印机列表
const ParentListFn = inject('reloadParentListFn') as Fn
const distributionMode = ref('EXPRESS')
const platform = ref('')
const addressDialog = ref(false)
const showFlag = ref(true)

initNotDeliverList()
initLogisticsList()

/**
 * 获取未发货订单
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
    deliverDialogFormData.deliverType = initDistributionModeToDeliverTypeMap[distributionMode.value]
    deliverList.value = shopOrderItems.filter((item: any) => item.sellType !== 'CONSIGNMENT')
    tableRef.value?.toggleAllSelection()
}

/**
 * 映射发货方式 distributionMode => deliverType
 */
const initDistributionModeToDeliverTypeMap = {
    VIRTUAL: 'WITHOUT',
    EXPRESS: 'EXPRESS',
    SHOP_STORE: 'WITHOUT',
} as { [key: string]: 'WITHOUT' | 'EXPRESS' | 'PRINT_EXPRESS' | 'IC_MERCHANT' | 'IC_OPEN' }

let params: ParamsDeliverWithNoString = {
    deliverType: '',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
    sender: { name: '', mobile: '', address: '', area: [] },
    cargo: '',
    remark: '',
    dropDeliver: true,
    expressCompany: {
        expressCompanyName: '',
        expressCompanyCode: '',
        expressNo: '',
    },
    items: [],
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
        const { deliverType, receiver, expressCompany, expressNo } = deliverDialogFormData
        params.deliverType = deliverType
        params.receiver = receiver
        params.expressCompany = null
        if (deliverType !== 'WITHOUT' && distributionMode.value !== 'INTRA_CITY_DISTRIBUTION') {
            params.expressCompany = { expressCompanyName: '', expressCompanyCode: '', expressNo: '' }
            params.expressCompany.expressCompanyCode = expressCompany
            const company = companySelectList.value.find((item) => item.logisticsCompanyCode === expressCompany)
            if (company) {
                params.expressCompany.expressCompanyName = company.logisticsCompanyName
            } else {
                return ElMessage.error('请选择物流服务')
            }
            params.expressCompany.expressNo = expressNo
        }
        params.items = orderList.map((item: ApiNotDelivey) => ({ itemId: item.id, num: item.deliveryNum }))
        if (params.deliverType === 'PRINT_EXPRESS') {
            //  打印面单发货携带发货人信息
            getSenderAddress(params)
        }
        if (!params.sender?.address) {
            return (addressDialog.value = true)
        }
        sendProductFn(params)
    } catch (error) {
        console.dir(error)
    }
}
const sendProductFn = async (params: ParamsDeliverWithNoString) => {
    const { code, msg } = await doPutOrderDetailsByOrderNo($props.currentNo, params)
    if (code !== 200) return ElMessage.error(msg || '发货失败')
    _isShow.value = false
    ParentListFn()
    ElMessage.success('发货成功')
}
/**
 * 获取发货地址 联系人...
 */
function getSenderAddress(params: ParamsDeliverWithNoString) {
    const sender = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
    if (!sender) return (addressDialog.value = true)
    const { address, contactName, contactPhone, area } = sender
    params.sender = {
        address,
        name: contactName,
        mobile: contactPhone,
        area,
    }
}
const handleClose = () => {
    _isShow.value = false
}
const HandleloadDeliverDialogFormData = (e: DeliverDialogForm) => {
    deliverDialogFormData.addressaddress = e.addressaddress
    deliverDialogFormData.deliverType = e.deliverType
    deliverDialogFormData.expressCompany = e.expressCompany
    deliverDialogFormData.receiver = e.receiver
    deliverDialogFormData.expressNo = e.expressNo
}
const HandleloadCompanySelectListData = (e: LogisticsInfo[]) => {
    companySelectList.value = e
}
const HandleloadDeliveryAddressData = (e: any) => {
    deliveryAddressData.value = e
}
const isNotOpenOrder = computed(() => {
    return ['VIRTUAL', 'INTRA_CITY_DISTRIBUTION'].includes(distributionMode.value)
})
const FormRef = ref()

const logisticsForm = ref({
    address: '',
    contactName: '',
    contactPhone: '',
    zipCode: '',
    area: [],
    defSend: true as boolean | 'YES' | 'NO',
    defReceive: false as boolean | 'YES' | 'NO',
})
/**
 * 表单提交请求
 */
const submitHandle = async () => {
    await FormRef.value.validate()
    await loadSetLogisticsDddress()
}
const logisticsFormRules = {
    contactName: [
        { required: true, message: '请输入联系人', trigger: 'blur' },
        { max: 10, message: '最多输入10个字符', trigger: 'blur' },
    ],
    contactPhone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3|5|6|7|8|9]\d{9}$/, message: '请输入正确的号码格式', trigger: 'blur' },
    ],
    area: [{ type: 'array' as const, required: true, message: '请选择省市区', trigger: 'change' }],
    // 验证未写
    address: [{ required: true, message: '请填写详细地址', trigger: 'blur' }],
}

const ShipmentSelectRef = ref()
/**
 * 新增或者编辑地址列表
 */
const loadSetLogisticsDddress = async () => {
    const newLogisticsForm = { ...logisticsForm.value }
    newLogisticsForm.defReceive = newLogisticsForm.defReceive ? 'YES' : 'NO'
    newLogisticsForm.defSend = newLogisticsForm.defSend ? 'YES' : 'NO'
    const { code } = await setLogisticsDddress({ ...newLogisticsForm })
    if (code === 200) {
        ElMessage.success('增加成功')
        params.sender = {
            address: newLogisticsForm.address,
            mobile: newLogisticsForm.contactPhone,
            name: newLogisticsForm.contactName,
            area: newLogisticsForm.area,
        }
        showFlag.value = false
        addressDialog.value = false
        await nextTick()
        showFlag.value = true
    }
}
// 初始化发货地址
/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
    const { data, code } = await doGetLogisticsList()
    if (code !== 200) return
    deliveryAddressData.value = data.records
    const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
    if (!sender) return ElMessage.error('请设置默认发货地址')
    params.sender = {
        address: sender.address,
        mobile: sender.contactPhone,
        name: sender.contactName,
        area: sender.area,
    }
    return true
}

const handleDelete = (index: number) => {
    if (deliverList.value.length > 1) {
        deliverList.value.splice(index, 1)
    } else {
        ElMessage.warning('至少保留一个商品规格进行发货！')
    }
}
</script>

<template>
    <el-form ref="ruleFormRef" :model="deliverDialogFormData" class="notShipment" :rules="rules">
        <el-form-item v-if="distributionMode === 'EXPRESS'" label-width="90px" label="发货方式" prop="deliverType">
            <el-radio-group v-model="deliverDialogFormData.deliverType" class="ml-4" size="large">
                <el-radio value="EXPRESS" size="small">手动发货</el-radio>
                <el-radio value="PRINT_EXPRESS" size="small">打印快递单并发货</el-radio>
            </el-radio-group>
        </el-form-item>
        <div v-if="distributionMode === 'VIRTUAL'" style="color: #f00; padding-bottom: 16px">您可通过网络形式(如:聊天工具将商品发给用户)发货</div>
        <ShipmentSelect
            v-if="distributionMode === 'EXPRESS' && showFlag"
            ref="ShipmentSelectRef"
            :deliverDialogFormData="deliverDialogFormData"
            :loadDeliverDialogFormData="HandleloadDeliverDialogFormData"
            :loadCompanySelectListData="HandleloadCompanySelectListData"
            :loadDeliveryAddressData="HandleloadDeliveryAddressData"
        />
        <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px">
            <template #label>
                <div class="send">配送信息</div>
            </template>
            <div v-if="orderReceiver">
                <span style="margin-right: 16px">{{ orderReceiver.name }}</span>
                <span style="margin-right: 16px">{{ orderReceiver.mobile }}</span>
                <span>{{ deliverDialogFormData.receiver.area?.join('') }} {{ deliverDialogFormData.receiver.address }}</span>
            </div>
            <div v-else>暂无地址信息</div>
        </el-form-item>
        <el-table
            ref="tableRef"
            empty-text="暂无数据~"
            :data="deliverList"
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
                    <div style="display: flex; justify-content: space-between; padding: 4px 10px">
                        <div style="display: flex; width: 70%">
                            <el-avatar style="width: 68px; height: 68px" shape="square" size="large" :src="row.image" />
                            <div class="info">
                                <div class="productName">{{ row.productName }}</div>
                                <div class="specs">{{ row.specs?.join('、') }}</div>
                            </div>
                        </div>
                        <div class="price">
                            <div>￥{{ divTenThousand(row.dealPrice).mul(row.num) }}</div>
                            <div>x{{ row.num }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="待发数量" width="140" align="center">
                <template #default="{ row }">
                    {{ row.num }}
                </template>
            </el-table-column>
            <el-table-column v-if="distributionMode === 'EXPRESS'" label="发货数量" width="140" align="center">
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
                    <el-form-item label="地区选择" prop="area">
                        <el-cascader
                            v-model="logisticsForm.area"
                            :options="regionData"
                            :style="{ width: '100%' }"
                            :props="{
                                value: 'label',
                            }"
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
}
</style>
