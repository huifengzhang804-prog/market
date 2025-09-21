<script setup lang="ts">
import { ref, reactive, computed, onUnmounted } from 'vue'
import { doGetResetPasswordSms, doPutResetPassword } from '@/apis/sign'
import { useRouter } from 'vue-router'
import { REGEX } from '@/constant'
import { REGEX_MOBILE } from '@/libs/validate'
import { ElMessage } from 'element-plus'

const formRef = ref()
const changePass = ref({
    mobile: '',
    code: '',
    password: '',
    confirmPassword: '',
})
const rules = ref({
    mobile: [
        {
            required: true,
            message: '请输入手机号',
            trigger: ['change', 'blur'],
        },
        { pattern: REGEX.MOBILE, message: '请输入正确的手机号', trigger: 'blur' },
    ],
    code: [
        {
            required: true,
            message: '请输入验证码',
            trigger: ['change', 'blur'],
        },
    ],
    password: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码必须是6~20位', trigger: 'blur' },
        { pattern: REGEX.PASSWORD, message: '密码格式不正确', trigger: 'blur' },
    ],
    confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码必须是6~20位', trigger: 'blur' },
        { pattern: REGEX.PASSWORD, message: '密码格式不正确', trigger: 'blur' },
        { validator: confirmPasswordValid, trigger: 'blur' },
    ],
})
const timeText = ref('获取验证码')
const isCountdown = ref(false)
const time = ref(60)
const next = ref(false)
let times
const strength = reactive({
    weak: false,
    medium: false,
    strong: false,
})

onUnmounted(() => {
    if (times) {
        clearInterval(times)
    }
})

function confirmPasswordValid(rule: any, value: any, callback: any) {
    if (value !== changePass.value.password) {
        callback(new Error('密码输入不一致'))
        return
    }
    callback()
}
/**
 * @description: 倒计时 17621228898
 * @returns {*}
 */
const handleSendVerificationCode = async () => {
    if (!changePass.value.mobile) return ElMessage.error('请输入手机号')
    const { code, data } = await doGetResetPasswordSms(changePass.value.mobile)
    if (code !== 200) {
        ElMessage.error('验证码获取失败')
        return
    }
    isCountdown.value = true
    times = setInterval(() => {
        if (time.value === 0) {
            timeText.value = '重新发送'
            time.value = 60
            isCountdown.value = false
            clearInterval(times)
        }
        time.value--
    }, 1000)
}
const $router = useRouter()
const handleSubmit = async () => {
    const isValida = await formRef.value.validate()
    if (!isValida) return
    const { code, password, confirmPassword, mobile } = changePass.value
    const { code: res, data } = await doPutResetPassword(code, password, confirmPassword, mobile)
    if (res !== 200) return ElMessage.error('提交失败')
    ElMessage.success('重置密码成功')
    changePass.value = {
        mobile: '',
        code: '',
        password: '',
        confirmPassword: '',
    }
    formRef.value.resetFields()
    $router.replace('sign')
}
/**
 * @description: 密码强度
 * @param {*}
 * @returns {*}
 */
const handleStrengthShow = () => {
    // 弱密码：全是数字或全是字母，6-16个字符
    const weakReg = /^[0-9]{6,16}$|^[a-zA-Z]{6,16}$/
    // 中密码：数字和26个英文字母，6-16个字符
    const mediumReg = /^[A-Za-z0-9]{6,16}$/
    // 强密码：由数字、26个英文字母或者下划线组成的字符串，6-16个字符
    const strongReg = /^\w{6,16}$/
    const pas = changePass.value.password
    if (pas !== '') {
        if (pas.length >= 6 && pas.length <= 16) {
            if (pas.match(weakReg)) {
                strength.weak = true
                strength.medium = false
                strength.strong = false
            } else if (pas.match(mediumReg)) {
                strength.weak = true
                strength.medium = true
                strength.strong = false
            } else if (pas.match(strongReg)) {
                strength.weak = true
                strength.medium = true
                strength.strong = true
            }
        }
    } else {
        strength.weak = false
        strength.medium = false
        strength.strong = false
    }
}
const validateSubmit = computed(() => {
    const { code, password, confirmPassword, mobile } = changePass.value
    return REGEX_MOBILE(mobile) && code.length && password.length >= 6 && confirmPassword.length >= 6
})
</script>

<template>
    <div class="change-pass">
        <div class="head">
            <el-link class="head--item">启山科技</el-link>
            <el-link class="head--item" href="/sign">登录</el-link>
        </div>
        <div class="main">
            <el-card class="box-card">
                <div class="main__form">
                    <div class="main__form--title">找回密码</div>
                    <el-form ref="formRef" :model="changePass" :rules="rules">
                        <el-form-item prop="mobile" label="手机号" label-width="80px">
                            <el-row :gutter="24" justify="space-between">
                                <el-col :span="12">
                                    <el-input v-model="changePass.mobile" size="large" placeholder="请输入手机号"> </el-input>
                                </el-col>
                                <el-col :span="12">
                                    <el-button
                                        size="large"
                                        type="primary"
                                        plain
                                        :disabled="!MOBILE.test(changePass.mobile) || isCountdown"
                                        @click="handleSendVerificationCode"
                                    >
                                        {{ isCountdown ? `${time}秒后重新发送` : `${timeText}` }}
                                    </el-button>
                                </el-col>
                            </el-row>
                        </el-form-item>
                        <el-form-item prop="code" label="验证码" label-width="80px">
                            <el-input v-model="changePass.code" size="large" placeholder="请输入验证码"></el-input>
                        </el-form-item>
                        <el-form-item prop="password" label="新密码" label-width="80px">
                            <el-input
                                v-model.trim="changePass.password"
                                size="large"
                                show-password
                                type="password"
                                placeholder="请输入您的新密码"
                                maxlength="20"
                                @keyup="handleStrengthShow"
                            />
                            <!-- <span class="weak" :class="{ danger: strength.weak }">弱</span>
                            <span class="medium" :class="{ success: strength.medium }">中</span>
                            <span class="strong" :class="{ brand: strength.strong }">强</span> -->
                        </el-form-item>
                        <el-form-item prop="confirmPassword" label="确认密码" label-width="80px">
                            <el-input
                                v-model.trim="changePass.confirmPassword"
                                size="large"
                                show-password
                                type="password"
                                placeholder="两次输入必须一致"
                                maxlength="20"
                            />
                        </el-form-item>
                    </el-form>
                    <div class="main__form--button-c">
                        <el-button size="large" class="main__form--button" type="primary" plain :disabled="!validateSubmit" @click="handleSubmit">
                            提交
                        </el-button>
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>

<style scoped lang="scss">
@include b(change-pass) {
    background: #f9fbff;
    height: 100vh;
}
@include b(head) {
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 100px;
    background-image: radial-gradient(123.45% 123.45% at 50% -191.89%, #b0e0ff 0, #18a7fd 46.56%, #3a77fd 100%);
    box-shadow: 0 13px 30px rgb(29 102 189 / 20%);
    @include m(item) {
        font-weight: 700;
        color: #fff;
    }
}
@include b(main) {
    width: 500px;
    margin: 0 auto;
    margin-top: 60px;
    @include e(form) {
        padding: 50px 20px;
        @include m(title) {
            font-weight: 700;
            font-size: 16px;
            padding-left: 40px;
            margin-bottom: 20px;
            &::before {
                content: '';
                display: inline-block;
                width: 3px;
                height: 12px;
                margin-right: 8px;
                background: #409eff;
            }
        }
        @include m(admin) {
            margin-top: 30px;
            margin-bottom: 15px;
        }
        @include m(button-c) {
            margin-top: 40px;
            display: flex;
            justify-content: center;
        }
        @include m(button) {
            width: 300px;
        }
    }
}
</style>
