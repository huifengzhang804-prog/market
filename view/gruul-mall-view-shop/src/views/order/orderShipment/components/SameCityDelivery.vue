<script lang="ts" setup>
import { ElMessage, FormInstance, FormRules, TableInstance } from 'element-plus'
import { ApiNotDelivey, ApiOrderReceiver } from '../types'
import { doGetBatchDeliveryPrice, dogetOrderNotDetailsByOrderNo, doGetShopDeliveryMethod, doPutOrderDetailsByOrderNo } from '@/apis/order'
import { IcFreightFee } from '@/apis/order/types'
import { useVModel } from '@vueuse/core'
import Decimal from 'decimal.js'
import { ParamsDeliverWithNoString } from './notShipment.vue'

const ParentListFn = inject('reloadParentListFn') as Fn

const { divTenThousand } = useConvert()

const $props = defineProps({
    currentNo: {
        type: String,
        required: true,
    },
    isShow: { type: Boolean, required: true },
    currentShopOrderNo: { type: String, required: true },
})
const $emit = defineEmits(['update:isShow'])
const _isShow = useVModel($props, 'isShow', $emit)

// 未发货列表
const deliverList = ref<ApiNotDelivey[]>([])
// 地区信息
const orderReceiver = ref<ApiOrderReceiver>()
const deliverDialogFormData = reactive({
    deliverType: '' as 'IC_MERCHANT' | 'IC_OPEN',
    receiver: { name: '', mobile: '', address: '' },
})
const tableRef = ref<TableInstance>()

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
    // 收货人地址信息拼接
    deliverDialogFormData.receiver.address = `${Receiver.area && Receiver.area?.length ? Receiver.area.join(' ') : ''} ${Receiver.address}`
    // for 记录发货数量初始值
    for (let i = 0; i < shopOrderItems.length; i++) {
        shopOrderItems[i].deliveryNum = shopOrderItems[i].num
    }
    deliverList.value = shopOrderItems.filter((item: any) => item.sellType !== 'CONSIGNMENT')
    tableRef.value?.toggleAllSelection()
}

initNotDeliverList()
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
})
const deliverTypeOptions = ref<{ label: string; value: string }[]>([])

onMounted(() => {
    doGetShopDeliveryMethod().then((res) => {
        deliverTypeOptions.value = res.data
        if (res.data.length) {
            deliverDialogFormData.deliverType = res.data[0].value
            deliverTypeChange(deliverDialogFormData.deliverType)
        }
    })
})

const freightFee = ref<IcFreightFee>({
    balance: -100,
    orderPriceMap: {
        [$props.currentNo]: {
            discount: 0,
            pay: 0,
            total: 0,
        },
    },
    totalPrice: 0,
})

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

const deliverTypeChange = async (e: 'IC_MERCHANT' | 'IC_OPEN') => {
    if (e === 'IC_OPEN') {
        const res = await doGetBatchDeliveryPrice([$props.currentNo])
        freightFee.value = res.data
    }
}
const handleClose = () => {
    _isShow.value = false
}
const paramsRef = ref<ParamsDeliverWithNoString>({
    deliverType: '' as 'IC_MERCHANT' | 'IC_OPEN',
    receiver: { name: '', mobile: '', address: '', area: [] as unknown as [string, string, string?] },
    sender: { name: '', mobile: '', address: '', area: [] },
    cargo: '',
    remark: '',
    dropDeliver: true,
    items: [],
})
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
        const { deliverType, receiver } = deliverDialogFormData
        paramsRef.value.deliverType = deliverType
        paramsRef.value.receiver = receiver as unknown as { name: string; mobile: string; address: string; area: [string, string, string?] }
        paramsRef.value.items = orderList.map((item: ApiNotDelivey) => ({ itemId: item.id, num: item.deliveryNum }))
        console.log(paramsRef)
        sendProductFn(paramsRef.value)
    } catch (error) {
        console.dir(error)
    }
}
const sendProductFn = async (params: ParamsDeliverWithNoString) => {
    const { code, msg } = await doPutOrderDetailsByOrderNo($props.currentNo, params)
    if (code !== 200) {
        _isShow.value = false
        return ElMessage.error(msg || '发货失败')
    }
    _isShow.value = false
    ElMessage.success('发货成功')
    ParentListFn()
}
</script>

<template>
    <el-form ref="ruleFormRef" :model="deliverDialogFormData" :rules="rules" class="notShipment">
        <el-form-item :error="deliverTypeError" label="发货方式" label-width="90px" prop="deliverType">
            <el-select
                v-model="deliverDialogFormData.deliverType"
                placeholder="请选择同城配送发货方式"
                style="width: 200px"
                @change="deliverTypeChange"
            >
                <el-option v-for="item in deliverTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <div v-show="deliverDialogFormData.deliverType === 'IC_OPEN'" style="width: 100%">
                <div class="tips fcenter">
                    <div class="red">
                        账户余额：{{ divTenThousand(freightFee.balance).toFixed(2) }}元，预估 {{ divTenThousand(freightFee.totalPrice).toFixed(2) }}元
                    </div>
                    <el-popover :width="200" effect="dark" placement="right" popper-class="ic-detail-popover" trigger="click">
                        <template #reference>
                            <div class="blue cup">明细</div>
                        </template>
                        <div style="display: flex; justify-content: space-between">
                            <div>运费</div>
                            <div>￥{{ divTenThousand(freightFee?.orderPriceMap?.[currentNo]?.total || 0).toFixed(2) }}</div>
                        </div>
                        <div style="display: flex; justify-content: space-between">
                            <div>总优惠</div>
                            <div>￥{{ divTenThousand(freightFee?.orderPriceMap?.[currentNo]?.discount || 0).toFixed(2) }}</div>
                        </div>
                        <div style="display: flex; justify-content: space-between">
                            <div>应付金额</div>
                            <div>￥{{ divTenThousand(freightFee?.orderPriceMap?.[currentNo]?.pay || 0).toFixed(2) }}</div>
                        </div>
                    </el-popover>
                </div>
            </div>
        </el-form-item>
        <el-form-item label-width="90px">
            <template #label>
                <div class="send">配送信息</div>
            </template>
            <div v-if="orderReceiver">
                <span style="margin-right: 18px">{{ orderReceiver.name }}</span>
                <span style="margin-right: 18px">{{ orderReceiver.mobile }}</span>
                <span style="margin-right: 18px">{{ deliverDialogFormData.receiver.address }}</span>
            </div>
            <div v-else>暂无地址信息</div>
        </el-form-item>
    </el-form>

    <el-table
        ref="tableRef"
        :border="true"
        :cell-style="{ color: '#333', height: '100px' }"
        :data="deliverList"
        :header-cell-style="{
            backgroundColor: '#F6F8FA',
            color: '#515151',
            height: '48px',
        }"
        empty-text="暂无数据~"
        max-height="250"
        style="width: 100%; margin-bottom: 20px"
    >
        <template #empty>
            <ElTableEmpty />
        </template>
        <el-table-column align="center" label="商品">
            <template #default="{ row }">
                <div style="display: flex; justify-content: space-between; padding: 0px 10px">
                    <div style="display: flex; width: 70%">
                        <el-avatar :src="row.image" shape="square" size="large" style="width: 68px; height: 68px" />
                        <div class="info">
                            <div class="productName">
                                {{ row.productName }}
                            </div>
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
        <el-table-column align="center" label="待发货数" width="220">
            <template #default="{ row }">
                <div>{{ row.deliveryNum }}</div>
            </template>
        </el-table-column>
    </el-table>
    <div class="footer fcenter">
        <el-button class="mla" @click="handleClose">取消</el-button>
        <el-button :disabled="disableDeliverSubmit" type="primary" @click="handleeSubmit">确认</el-button>
    </div>
</template>

<style lang="scss" scoped>
.tips {
    flex: 1;
    margin-left: 10px;
    .red {
        font-size: 12px;
        font-weight: 400;
        margin-right: 10px;
        flex-shrink: 0;
        color: #f54319;
    }

    .blue {
        color: rgb(85, 92, 253);
        font-size: 14px;
    }
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
<style>
.ic-detail-popover {
    opacity: 0.5;
}
</style>
