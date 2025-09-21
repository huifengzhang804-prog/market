<template>
    <div class="shopFinance">
        <el-form
            ref="currentFormRef"
            :model="submitForm"
            label-width="82px"
            :rules="financeRules"
            :disabled="$route.name === 'previewShop' || $route.name === 'previewSupplier'"
        >
            <el-form-item label="收款人" prop="payee">
                <el-input v-model="submitForm.payee" placeholder="请填写收款人" maxlength="60"></el-input>
            </el-form-item>
            <el-form-item label="银行名" prop="bankName">
                <el-input v-model="submitForm.bankName" placeholder="请填写银行名" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="账号" prop="bankAcc">
                <el-input v-model="submitForm.bankAcc" placeholder="请填写账号" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="开户行" prop="openAccountBank">
                <el-input v-model="submitForm.openAccountBank" placeholder="请填写开户行" maxlength="20"></el-input>
            </el-form-item>
        </el-form>
        <div
            v-if="[ShopStatus.NORMAL, ShopStatus.FORBIDDEN].includes(submitForm.status) && ['storeList', 'supplierList'].includes($route.query.tabRadio as string)"
        >
            <div style="line-height: 28px">入驻信息</div>
            <div class="SettledInfo">
                <div><span>入住方式：</span>{{ settledWayList[submitForm.extra.settledWay as keyof typeof settledWayList] }}</div>
                <div><span>店铺ID：</span>{{ submitForm.no }}</div>
                <div v-if="submitForm.extra.settledWay === 'ADD'"><span>添加时间：</span>{{ submitForm.createTime }}</div>
                <div v-else><span>审批时间：</span>{{ submitForm.extra.auditTime }}</div>
                <div><span>操作员：</span>{{ submitForm.extra.operatorName }}</div>
                <div><span>操作员电话：</span>{{ submitForm.extra.operatorPhone }}</div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { Ref } from 'vue'
import { FormRules } from 'element-plus'
import { ShopFormType } from '../types'
import { ShopStatus } from '@apis/shops/types/response'
const $route = useRoute()
//父组件
const $parent = inject('addShops')
const submitForm = ($parent as { submitForm: Ref<ShopFormType> }).submitForm
const currentFormRef = ref()
defineExpose({
    currentFormRef,
    componentFlag: 'finance',
})
const settledWayList = {
    APPLY: '申请入驻',
    ADD: '添加入驻',
}
const financeRules = reactive<FormRules>({
    payee: [
        {
            required: true,
            message: '请填写收款人',
            trigger: 'blur',
        },
    ],
    bankName: [
        {
            required: true,
            message: '请填写银行名',
            trigger: 'blur',
        },
    ],
    bankAcc: [
        {
            required: true,
            message: '请填写账号',
            trigger: 'blur',
        },
    ],
    openAccountBank: [
        {
            required: true,
            message: '请填写开户行',
            trigger: 'blur',
        },
    ],
})
</script>

<style lang="scss">
@include b(shopFinance) {
    padding: 20px 25px 70px 46px;
}
.SettledInfo {
    span {
        display: inline-block;
        width: 90px;
        text-align: right;
        line-height: 28px;
    }
}
</style>
