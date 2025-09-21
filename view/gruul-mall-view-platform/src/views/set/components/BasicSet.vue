<script lang="ts" setup>
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import {
    doGetBasicSet,
    doGetGoodsAuditSetting,
    doGetIntergalTimeoutSetting,
    doPutGoodsAuditSetting,
    doPutIntergalTimeoutSetting,
    doSaveBasicSet,
    doPutSupplierOrderPaymentMethod,
    doGetSupplierOrderPaymentMethod,
} from '@/apis/setting'
import Decimal from 'decimal.js'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import QPluginPurchaseOrderSetting from '@/q-plugin/supplier/PurchaseOrderSetting.vue'

const purchaseOrderSettingRef = ref<InstanceType<typeof QPluginPurchaseOrderSetting> | null>(null)
const oneHourSecond = 60 * 60
const oneDaySecond = 24 * oneHourSecond
const timeout = reactive({
    d: 0,
    h: 0,
    m: 0,
    supplierD: 0,
    supplierH: 0,
    supplierM: 0,
    payTimeout: 0,
    confirmTimeout: 0,
    commentTimeout: 0,
    afsAuditTimeout: 0,
    integralTimeout: 0,
    productAuditType: '',
    paymentMethods: [],
})
const ruleFormRef = ref()
// 未支付订单校验
const notPayValid = (rule: any, value: any, callback: any) => {
    const minutes = timeout.d * oneDaySecond + timeout.h * oneHourSecond + timeout.m * 60
    if (minutes < 15 * 60 || minutes > 30 * 24 * 60 * 60) {
        callback(new Error('可以设置的区间为15分钟至30天'))
        return
    }
    callback()
}
// 0-30区间校验
const validIntervalCheck = (min: number, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请填写天数'))
    }
    if (value < min || value > 30) {
        return callback(new Error(`区间为${min}-30`))
    }
    callback()
}
const rules = reactive<FormRules>({
    //支付超时时间
    payTimeout: [{ validator: notPayValid, trigger: 'blur' }],
    //确认收货超时时间
    confirmTimeout: [{ validator: (_, val, callback) => validIntervalCheck(1, val, callback), trigger: 'blur' }],
    //评价超时时间
    commentTimeout: [{ validator: (_, val, callback) => validIntervalCheck(3, val, callback), trigger: 'blur' }],
    //售后审核超时时间
    afsAuditTimeout: [{ validator: (_, val, callback) => validIntervalCheck(1, val, callback), trigger: 'blur' }],
})

onMounted(() => {
    initForm()
    initGoodsAuditSetting()
    initIntergalTimeoutSetting()
    initSupplierOrderPaymentMethod()
})
const submitBasicForm = async () => {
    if (!timeout.paymentMethods.length) {
        return ElMessage.error('请选择采购支付方式')
    }

    const formEl = ruleFormRef.value as FormInstance
    if (!formEl) return
    const valid = await formEl.validate()
    if (!valid) {
        return
    }
    //表单校验成功后再提交
    const form = {
        //支付超时时间 单位秒
        payTimeout: timeout.d * oneDaySecond + timeout.h * oneHourSecond + timeout.m * 60,
        //确认收货超时时间 单位秒
        confirmTimeout: timeout.confirmTimeout * oneDaySecond,
        //评价超时时间 单位秒
        commentTimeout: timeout.commentTimeout * oneDaySecond,
        //售后审核超时时间
        afsAuditTimeout: timeout.afsAuditTimeout * oneDaySecond,
    }
    doSaveBasicSet(form).then((res) => {
        const { code, success, msg } = res
        if (code === 200 && success) {
            ElMessage.success('保存成功')
        } else {
            ElMessage.error(msg || '保存失败')
        }
    })
    purchaseOrderSettingRef.value?.qPlugin?.currentComponentRef?.handOrderTime()
    // handOrderTime()
    handleSubmitGoodsAudit()
    handleSubmitIntergalTimeout()
    handleSubmitSupplierOrderPaymentMethod()
}

const handleSubmitGoodsAudit = () => {
    doPutGoodsAuditSetting(timeout.productAuditType)
}
const handleSubmitIntergalTimeout = () => {
    doPutIntergalTimeoutSetting(timeout.integralTimeout * oneDaySecond)
}
const handleSubmitSupplierOrderPaymentMethod = () => {
    doPutSupplierOrderPaymentMethod({ paymentMethods: timeout.paymentMethods })
}

const secondToDays = (seconds: Long) => {
    return Math.floor(new Decimal(seconds).toNumber() / oneDaySecond)
}

const secondToPayTimeout = (seconds: Long) => {
    const nums = new Decimal(seconds).toNumber()
    let dd = Math.floor(nums / oneDaySecond)
    let hh = Math.floor((nums % oneDaySecond) / oneHourSecond)
    let mm = Math.floor((nums % oneHourSecond) / 60)
    return [dd, hh, mm]
}

async function initForm() {
    const { code, data } = await doGetBasicSet()
    if (code !== 200) {
        return
    }
    // handlesupplierTime()
    const timeSplit = secondToPayTimeout(data.payTimeout)
    timeout.d = timeSplit[0]
    timeout.h = timeSplit[1]
    timeout.m = timeSplit[2]

    timeout.confirmTimeout = secondToDays(data.confirmTimeout)
    timeout.commentTimeout = secondToDays(data.commentTimeout)
    timeout.afsAuditTimeout = secondToDays(data.afsAuditTimeout)
}

async function initGoodsAuditSetting() {
    const { code, data } = await doGetGoodsAuditSetting()
    if (code !== 200) {
        return
    }
    timeout.productAuditType = data as string
}

async function initSupplierOrderPaymentMethod() {
    const { code, data } = await doGetSupplierOrderPaymentMethod()
    if (code !== 200) return
    timeout.paymentMethods = data.paymentMethods
}

async function initIntergalTimeoutSetting() {
    const { code, data } = await doGetIntergalTimeoutSetting()
    if (code !== 200) return
    timeout.integralTimeout = secondToDays(data as Long)
}
</script>

<template>
    <div style="padding: 0 16px">
        <el-form ref="ruleFormRef" :model="timeout" :rules="rules">
            <TitleBar color="#555CFD" name="下单设置" style="height: 50px" />
            <div class="msg">
                <el-form-item label="未支付的顾客订单" label-width="180px" prop="payTimeout" style="flex: 1">
                    <el-input-number v-model="timeout.d" :controls="false" :min="0" style="width: 60px" />
                    <text class="ml12 mr12">天</text>
                    <el-input-number v-model="timeout.h" :controls="false" :min="0" style="width: 60px" />
                    <text class="ml12 mr12">小时</text>
                    <el-input-number v-model="timeout.m" :controls="false" :min="0" style="width: 60px" />
                    <span class="ml12">分后，内未付款，自动取消订单</span>
                </el-form-item>
                <span class="ml42 msg__text">最长可设置30天， 最短可设置15分钟</span>
            </div>
            <QPluginPurchaseOrderSetting ref="purchaseOrderSettingRef" />
            <div class="msg">
                <el-form-item label="已发货订单" label-width="180px" prop="confirmTimeout" style="flex: 1">
                    <el-input-number v-model="timeout.confirmTimeout" :controls="false" :max="30" :min="3" style="width: 118px" />
                    <span class="ml12">天，自动确认收货</span>
                </el-form-item>
                <span class="ml42 msg__text">请考虑物流运输时间，最长可设置30天</span>
            </div>
            <div class="msg">
                <el-form-item label="自动好评" label-width="180px" prop="commentTimeout" style="flex: 1">
                    <span>已完成订单&ensp;</span>
                    <el-input-number v-model="timeout.commentTimeout" :controls="false" :max="30" :min="3" style="width: 92px" />
                    <span class="ml12">天，自动评价好评</span>
                </el-form-item>
                <span class="ml42 msg__text">最长可设置30天， 最短可设置3天</span>
            </div>
            <div class="msg">
                <el-form-item label="申请售后" label-width="180px" prop="afsAuditTimeout" style="flex: 1">
                    <el-input-number v-model="timeout.afsAuditTimeout" :controls="false" :min="0" style="width: 106px" />
                    <span class="ml12">天后，商家未处理自动同意售后申请</span>
                </el-form-item>
                <span class="ml42 msg__text">默认3天，设置后以设置时间为准</span>
            </div>
            <div class="msg">
                <el-form-item label="采购订单" label-width="180px" prop="paymentMethods" style="flex: 1" required :show-message="false">
                    <el-checkbox-group v-model="timeout.paymentMethods">
                        <el-checkbox value="SHOP_BALANCE" name="type"> 店铺余额 </el-checkbox>
                        <el-checkbox value="PAY_OFF_LINE" name="type"> 线下打款 </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <span class="ml42 msg__text">控制商家选择支付方式来支付采购订单货款</span>
            </div>
            <TitleBar color="#555CFD" name="商品审核" style="height: 50px" />
            <div class="msg">
                <el-form-item label="商品审核" label-width="110px" prop="goodsPass" style="flex: 1">
                    <el-radio-group v-model="timeout.productAuditType">
                        <el-radio value="SYSTEM_AUDIT">自动通过</el-radio>
                        <el-radio value="MANUALLY_AUDIT">人工审核</el-radio>
                    </el-radio-group>
                </el-form-item>
            </div>

            <el-form-item label-width="20px"></el-form-item>
        </el-form>
    </div>
    <div class="preservation">
        <el-button type="primary" @click="submitBasicForm()">保存</el-button>
    </div>
</template>
<style lang="scss" scoped>
@include b(msg) {
    @include flex;
    justify-content: space-between;
    @include e(text) {
        height: 32px;
        width: 300px;
    }
}

@include b(ml42) {
    margin-left: 42px;
}

@include b(msg__text) {
    font-size: 12px;
    color: #999;
}

@include b(ml12) {
    margin-left: 12px;
}

@include b(ml56) {
    margin-left: 56px;
}

@include b(ml240) {
    margin-left: 240px;
}

@include b(colorRed) {
    color: #f57373;
}

@include b(mr12) {
    margin-right: 12px;
}

@include b(preservation) {
    width: 100%;
    display: flex;
    justify-content: center;
    height: 52px;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
    z-index: 100;
    background-color: white;
    margin-top: auto;
}
</style>
