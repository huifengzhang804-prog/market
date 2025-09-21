<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import city from '../city.json'
import bank from '../bank.json'
const prop = defineProps({
    account: {
        type: Boolean,
        default: false,
    },
})
const settlementForm = reactive({
    bankAccountType: 'BANK_ACCOUNT_TYPE_CORPORATE',
    accountName: '',
    accountBank: '',
    bankAddressCode: '',
    accountNumber: '',
    bankBranch: '',
    bankBranchId: '',
})
const emit = defineEmits(['accountClose', 'update:account', 'settlement'])
// 取消按钮
const closeFn = (formEl: FormInstance | undefined) => {
    emit('accountClose', !prop.account)
    if (!formEl) return
    formEl.resetFields()
}
// 确认按钮
const ruleFormRef = ref<FormInstance>()
const primaryFn = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            console.log('submit!')
            emit('accountClose', false)
        } else {
            console.log('error submit!', fields)
        }
    })

    emit('settlement', settlementForm)
}
const closeFn1 = () => {
    emit('accountClose', !prop.account)
    ruleFormRef.value.resetFields()
}
const propsAccount = computed({
    get() {
        return prop.account
    },
    set(value: boolean) {
        emit('update:account', value)
    },
})

const mold = ref<string>('你为经营者对公银行账户，请务必填写开户名为的银行账号')

const rules = reactive<FormRules>({
    bankAccountType: [{ required: true, message: '请选择账户类型', trigger: 'blur' }],
    accountName: [{ required: true, message: '请输入开户名称', trigger: 'blur' }],
    accountBank: [{ required: true, message: '请输入开户银行', trigger: 'blur' }],
    bankAddressCode: [{ required: true, message: '请选择开户支行地址所在城市', trigger: 'blur' }],
    accountNumber: [{ required: true, message: '请输入银行账号', trigger: 'blur' }],
    bankBranch: [{ required: true, message: '请输入开户支行', trigger: 'blur' }],
})
// 单选框点击切换
const corporateFn = () => {
    settlementForm.bankAccountType = 'BANK_ACCOUNT_TYPE_CORPORATE'
    mold.value = '你为经营者对公银行账户，请务必填写开户名为的银行账号'
    console.log(settlementForm.bankAccountType)
}
const personageFn = () => {
    settlementForm.bankAccountType = 'BANK_ACCOUNT_TYPE_PERSONAL'
    mold.value = '你为经营者个人银行卡，请务必填写开户名为的银行账号'
    console.log(settlementForm.bankAccountType)
}
// 城市
const cityFn = (value: any) => {
    if (value.length === 2) {
        console.log(value[1])
        settlementForm.bankAddressCode = value[1]
    } else {
        settlementForm.bankAddressCode = value[0]
    }
}

// 开户行id settlementForm.bankBranchId
const bankFn = () => {
    const res = bank.find((i) => {
        return (i.name = settlementForm.bankBranch)
    })
    settlementForm.bankBranchId = res.id
}
</script>

<template>
    <div>
        <el-dialog v-model="propsAccount" :close-on-click-modal="false" :before-close="closeFn1">
            <el-form ref="ruleFormRef" :model="settlementForm" label-width="130px" style="padding: 0 20px" :rules="rules">
                <h3 style="margin-top: -25px">结算账户</h3>
                <el-divider border-style="dashed" />
                <el-form-item label="账户类型" prop="bankAccountType">
                    <el-radio-group v-model="settlementForm.bankAccountType">
                        <el-radio label="BANK_ACCOUNT_TYPE_CORPORATE" @click="corporateFn">对公银行账户</el-radio>
                        <el-radio label="BANK_ACCOUNT_TYPE_PERSONAL" @click="personageFn">个人银行账户</el-radio>
                    </el-radio-group>
                    <p style="font-size: 12px; color: #ccc">{{ mold }}</p>
                </el-form-item>
                <el-form-item label="开户名称" prop="accountName">
                    <el-input v-model="settlementForm.accountName" placeholder="请输入" style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item>
                    <p style="font-size: 12px; color: #ccc">若因特殊原因需结算至非同名账户，在入驻完成后登录商户平台修改结算账户户名</p>
                </el-form-item>
                <el-form-item label="开户银行" prop="accountBank">
                    <el-input v-model="settlementForm.accountBank" placeholder="请输入" style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item>
                    <p style="font-size: 12px; color: #ccc">
                        示例：工商银行<br />城市商业银行、农村商业银行、信用合作联社及其他银行,请输入“其他银行”
                    </p>
                </el-form-item>
                <el-form-item label="开户支行所在城市" prop="bankAddressCode">
                    <el-cascader v-model="settlementForm.bankAddressCode" :options="city" @change="cityFn" />
                </el-form-item>
                <el-form-item label="开户支行" prop="bankBranch">
                    <el-input v-model="settlementForm.bankBranch" placeholder="请输入" style="width: 300px" @change="bankFn"></el-input>
                </el-form-item>
                <el-form-item>
                    <p style="font-size: 12px; color: #ccc">
                        示例：中国工商银行股份有限公司北京通州支行新华分理处 <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        宁波银行股份有限公司港隆支行
                    </p>
                </el-form-item>
                <el-form-item>
                    <p style="font-size: 12px; color: #ccc"></p>
                </el-form-item>
                <el-form-item label="银行账号" prop="accountNumber">
                    <el-input v-model="settlementForm.accountNumber" placeholder="请输入" style="width: 220px"></el-input>
                    <a
                        style="font-size: 12px; color: #4466ad; margin-left: 10px"
                        href="https://kf.qq.com/faq/140225MveaUz150819mYFjuE.html"
                        target="_blank"
                        >常用银行账号位数参考</a
                    >
                </el-form-item>
                <div class="btn">
                    <el-button type="warning" @click="closeFn(ruleFormRef)">返回</el-button>
                    <el-button type="primary" @click="primaryFn(ruleFormRef)">确认</el-button>
                </div>
            </el-form>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.btn {
    display: flex;
    justify-content: center;
}
</style>
