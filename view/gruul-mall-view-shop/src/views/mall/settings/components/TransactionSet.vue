<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { gettrade, posttrade } from '@/apis/mall/settings'
import { FormType, formTypeText } from '@/apis/mall/settings/types'
import { doGetinvoiceSettings, doPostinvoiceSettings } from '@/apis/invoice'

enum InvoiceType {
    NO_INVOICE = 'NO_INVOICE',
    VAT_GENERAL = 'VAT_GENERAL',
    VAT_SPECIAL = 'VAT_SPECIAL',
    VAT_COMBINED = 'VAT_COMBINED',
}

type InvoiceTypeKeys = keyof typeof InvoiceType

type WritableInvoiceType = {
    [P in InvoiceTypeKeys]: number
}

interface ApiForm {
    key: string
    type: FormType
    required: boolean
    placeholder: string
}

type InvoiceSetupValue = {
    invoiceType: keyof typeof InvoiceType
    orderCompleteDays: number
    option: boolean
}
type InvoiceSetup = {
    id: string
    shopId: Long
    invoiceSettingsClientType: 'SHOP'
    invoiceSetupValue: InvoiceSetupValue[]
}

const $shopInfoStore = useShopInfoStore()
const isLock = ref(false)
const form = ref({
    orderNotify: false,
})
const custom = ref<ApiForm>({
    key: '',
    type: FormType.TEXT,
    required: false,
    placeholder: '请输入',
})
const forms = ref<ApiForm[]>([])
const id = ref<Long>('')
const dialogVisible = ref(false)

const coustomRules = {
    key: [
        {
            required: true,
            message: '表单名称不可为空',
            trigger: 'blur',
        },
        {
            min: 1,
            max: 8,
            message: '表单名称最多8位',
            trigger: 'blur',
        },
    ],
    type: [
        {
            required: true,
            message: '表单类型不可为空',
            trigger: 'blur',
        },
    ],
    placeholder: [
        {
            required: true,
            message: '表单提示语不可为空',
            trigger: 'blur',
        },
    ],
}
const currentIndex = ref<number>(0)
const isEdit = ref(false)
const CustomRef = ref<FormInstance>()

const invoiceSetupObj = ref<InvoiceSetup>({
    id: '',
    shopId: $shopInfoStore.getterShopInfo.id,
    invoiceSettingsClientType: 'SHOP',
    invoiceSetupValue: [
        {
            invoiceType: InvoiceType.NO_INVOICE,
            orderCompleteDays: 0,
            option: true,
        },
    ],
})

const orderCompleteDays = ref<WritableInvoiceType>({
    NO_INVOICE: 0,
    VAT_GENERAL: 0,
    VAT_SPECIAL: 0,
    VAT_COMBINED: 0,
})

onMounted(() => {
    Gettrade()
    getinvoiceset()
})

async function getinvoiceset() {
    const { code, data, msg } = await doGetinvoiceSettings<InvoiceSetup>({
        invoiceSettingsClientType: 'SHOP',
        shopId: $shopInfoStore.getterShopInfo.id,
    })
    if (code !== 200) {
        ElMessage.error(msg || '获取发票设置失败')
        return
    }
    if (data.id) {
        invoiceSetupObj.value = data
        orderCompleteDays.value[data.invoiceSetupValue[0].invoiceType] = data.invoiceSetupValue[0].orderCompleteDays
    }
}

/**
 * 获取交易设置消息
 */
const Gettrade = async () => {
    const res = await gettrade<{
        customForm: ApiForm[]
        orderNotify: boolean
        shopId: Long
    }>({})
    if (res.data) {
        form.value.orderNotify = res.data.orderNotify
        if (res.data.customForm) {
            forms.value = res.data.customForm
        }
        id.value = res.data.shopId
    }
}
/**
 * 关闭弹窗
 */
const toggleDialogVisible = () => {
    isLock.value = false
    dialogVisible.value = false
    custom.value = {
        key: '',
        type: FormType.TEXT,
        required: false,
        placeholder: '',
    }
    isEdit.value = false
}
/**
 * 获取表格删除
 */
const handleDel = (i: number) => {
    forms.value.splice(i, 1)
}
/**
 * 获取表格编辑
 */
const handleEdit = (item: ApiForm, i: number) => {
    custom.value = { ...item }
    currentIndex.value = i
    isEdit.value = true
    dialogVisible.value = true
}
/**
 * 获取弹窗确认
 */
const onConfirm = async () => {
    if (!dialogVisible.value) return
    try {
        const isValidate = await CustomRef?.value?.validate()
        if (!isValidate) return
        if (isEdit.value) {
            forms.value[currentIndex.value] = custom.value
        } else {
            forms.value.push(custom.value)
        }
        custom.value = {
            key: '',
            type: FormType.TEXT,
            required: false,
            placeholder: '请输入',
        }
        isEdit.value = false
        dialogVisible.value = false
    } catch (error) {
        console.error(error)
    }
}
/**
 * 保存
 */
const save = () => {
    posttrade({ ...form.value, customForm: forms.value, id: id.value })
        .then(() => {
            invoiceSetupObj.value.invoiceSetupValue[0].orderCompleteDays =
                orderCompleteDays.value[invoiceSetupObj.value.invoiceSetupValue[0].invoiceType]

            doPostinvoiceSettings(invoiceSetupObj.value)
                .then(() => {
                    ElMessage.success('保存成功')
                })
                .catch((err) => {
                    ElMessage.error(err)
                })
        })
        .catch((err) => {
            ElMessage.error(err)
        })
}
</script>

<template>
    <div class="transaction_container fdc1">
        <div class="order__tip" style="display: flex; align-items: center; justify-content: space-between">
            <div>
                <div class="order__tip--lump"></div>
                <span class="order__tip--title">发票设置</span>
            </div>
        </div>
        <el-radio-group v-model="invoiceSetupObj.invoiceSetupValue[0].invoiceType" style="margin-left: 50px">
            <el-radio style="width: 600px; margin-bottom: 10px" value="NO_INVOICE">不提供发票</el-radio>
            <el-radio style="width: 600px; margin-bottom: 10px" value="VAT_GENERAL"
                >增值税电子普通发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_GENERAL" :controls="false" :max="9999" :min="0" :step="1" step-strictly />
                天后，停止开票
            </el-radio>
            <el-radio style="width: 600px; margin-bottom: 10px" value="VAT_SPECIAL"
                >增值税电子专用发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_SPECIAL" :controls="false" :max="9999" :min="0" :step="1" step-strictly />
                天后，停止开票
            </el-radio>
            <el-radio style="width: 600px; margin-bottom: 10px" value="VAT_COMBINED"
                >增值税电子专用发票 + 增值税电子普通发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_COMBINED" :controls="false" :max="9999" :min="0" :step="1" step-strictly />
                天后，停止开票
            </el-radio>
        </el-radio-group>
        <div class="order__tip" style="margin-top: 20px">
            <div class="order__tip--lump"></div>
            <span class="order__tip--title">下单设置</span>
        </div>
        <el-form :model="form" label-width="120px">
            <el-form-item label="订单播报">
                <el-switch v-model="form.orderNotify" />
            </el-form-item>

            <el-form-item label="订单备注">
                <el-button text type="primary" @click="dialogVisible = true">添加</el-button>
            </el-form-item>
        </el-form>
        <div>
            <el-table
                :cell-style="{ fontSize: '14px', color: '#333333' }"
                :data="forms"
                :header-cell-style="{ background: '#F7F8FA' }"
                :header-row-style="{ color: '#333', height: '48px' }"
                style="width: 80%; margin-left: 100px"
            >
                <template #empty>
                    <ElTableEmpty />
                </template>
                <el-table-column label="表单名称" prop="key"></el-table-column>
                <el-table-column label="表单格式" prop="type">
                    <template #default="{ row }: { row: ApiForm }">
                        {{ formTypeText[row.type] }}
                    </template>
                </el-table-column>
                <el-table-column label="必填" prop="required">
                    <template #default="scope">
                        {{ scope.row.required ? '是' : '否' }}
                    </template>
                </el-table-column>
                <el-table-column label="提示语" prop="placeholder"></el-table-column>
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button size="small" text type="primary" @click="handleEdit(scope.row, scope.$index)">编辑</el-button>

                        <el-button size="small" text type="primary" @click="handleDel(scope.$index)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <el-dialog v-model="dialogVisible" title="订单备注" width="40%">
            <el-form ref="CustomRef" :model="custom" :rules="coustomRules" label-width="80px">
                <el-form-item label="表单名称" prop="key">
                    <el-input v-model="custom.key" maxlength="4"></el-input>
                </el-form-item>
                <el-form-item label="表单格式" prop="type">
                    <el-select v-model="custom.type" placeholder="请选择" style="width: 100%">
                        <el-option v-for="(val, key) of formTypeText" :key="key" :label="val" :value="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否必填" prop="required">
                    <el-radio-group v-model="custom.required">
                        <el-radio :value="true">开启</el-radio>
                        <el-radio :value="false">关闭</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="提示语" prop="placeholder">
                    <el-input v-model="custom.placeholder" maxlength="60"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span>
                    <el-button @click="toggleDialogVisible">取 消</el-button>
                    <el-button type="primary" @click="onConfirm">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
    <div class="order__save ccenter">
        <el-button round type="primary" @click="save">保存</el-button>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

.transaction_container {
    overflow-y: scroll;
    padding-bottom: 52px;
    @include b(order) {
        @include e(tip) {
            vertical-align: center;
            background: rgb(242, 242, 244);
            padding: 15px 15px 15px 30px;
            margin-bottom: 10px;
            margin-left: 16px;
            margin-right: 16px;

            @include m(title) {
                margin-left: 12px;
                color: #333;
                font-weight: 700;
            }

            @include m(lump) {
                display: inline-block;
                width: 3px;
                height: 12px;
                background-color: #555cfd;
            }
        }
        @include e(save) {
            position: absolute;
            bottom: 0;
            width: 100%;
            height: 52px;
            box-shadow: 0 4px 20px 4px rgba(0, 0, 0, 0.1);
            background: rgb(255, 255, 255);
            z-index: 99;
            flex-shrink: 0;
        }
    }
}
</style>
