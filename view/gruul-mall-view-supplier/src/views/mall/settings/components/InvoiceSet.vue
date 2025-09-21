<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetinvoiceSettings, doPostinvoiceSettings } from '@/apis/invoice'

const $shopInfoStore = useShopInfoStore()

const invoiceSetupValue = ref({
    id: '',
    shopId: $shopInfoStore.getterShopInfo.id,
    invoiceSettingsClientType: 'SUPPLIER',
    invoiceSetupValue: [
        {
            invoiceType: 'NO_INVOICE',
            orderCompleteDays: 0,
            option: true,
        },
    ],
})
const orderCompleteDays = ref({
    VAT_GENERAL: 0,
    VAT_SPECIAL: 0,
    VAT_COMBINED: 0,
})

onMounted(() => {
    getinvoiceset()
})

async function getinvoiceset() {
    const { code, data, msg } = await doGetinvoiceSettings({ invoiceSettingsClientType: 'SUPPLIER', shopId: $shopInfoStore.getterShopInfo.id })
    if (code !== 200) {
        ElMessage.error(msg || '获取发票设置失败')
        return
    }
    if (data.id) {
        invoiceSetupValue.value = data
        orderCompleteDays.value[data.invoiceSetupValue[0].invoiceType as keyof typeof orderCompleteDays.value] =
            data.invoiceSetupValue[0].orderCompleteDays
    }
}

/**
 * @description: 保存
 * @returns
 */
const save = async () => {
    invoiceSetupValue.value.invoiceSetupValue[0].orderCompleteDays =
        orderCompleteDays.value[invoiceSetupValue.value.invoiceSetupValue[0].invoiceType as keyof typeof orderCompleteDays.value]

    const { code, data, msg } = await doPostinvoiceSettings(invoiceSetupValue.value)
    if (code !== 200) {
        ElMessage.error(msg || '保存发票设置失败')
        return
    }
    ElMessage.success('保存成功')
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
        <el-radio-group v-model="invoiceSetupValue.invoiceSetupValue[0].invoiceType" style="margin-left: 50px">
            <el-radio label="NO_INVOICE" style="width: 600px; margin-bottom: 10px">不提供发票</el-radio>
            <el-radio label="VAT_GENERAL" style="width: 600px; margin-bottom: 10px"
                >增值税电子普通发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_GENERAL" :step="1" step-strictly :max="9999" :min="0" :controls="false" />
                天后，停止开票
            </el-radio>
            <el-radio label="VAT_SPECIAL" style="width: 600px; margin-bottom: 10px"
                >增值税电子专用发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_SPECIAL" :step="1" step-strictly :max="9999" :min="0" :controls="false" />
                天后，停止开票
            </el-radio>
            <el-radio label="VAT_COMBINED" style="width: 600px; margin-bottom: 10px"
                >增值税电子专用发票 + 增值税电子普通发票，订单完成
                <el-input-number v-model="orderCompleteDays.VAT_COMBINED" :step="1" step-strictly :max="9999" :min="0" :controls="false" />
                天后，停止开票
            </el-radio>
        </el-radio-group>
        <div class="order__save ccenter">
            <el-button type="primary" round @click="save">保存</el-button>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

.transaction_container {
    position: relative;
    overflow-y: scroll;

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
            margin-top: auto;
            width: 100%;
            height: 52px;
            box-shadow: 0px 4px 20px 4px rgba(0, 0, 0, 0.1);
            background: rgb(255, 255, 255);
            z-index: 99;
            flex-shrink: 0;
        }
    }
}
</style>
