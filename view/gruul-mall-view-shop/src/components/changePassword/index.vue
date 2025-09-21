<script setup lang="ts">
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetUserDataAccount, doGetMyResetPasswordSms, doPutMyResetPassword, doPostLogout, doGetCaptchaSlider } from '@/apis/sign'
import { REGEX } from '@/constant'
import type { FormInstance, FormRules } from 'element-plus'
interface AccountInfo {
    email?: string
    mobile?: string
    username?: string
    manageMobile?: string
}

const ruleFormRef = ref<FormInstance>()
const toggleBindingText = ref('')
const form = ref({ code: '' })
const $router = useRouter()
// 密码form data
const passwordForm = ref({ password: '', confirmPassword: '', code: '' })
const time = ref(60)
const captchaList = ref()
const timeText = ref('获取验证码')
const isCountdown = ref(false)
const isUpdatePassword = ref(false)
const changePasswordFlag = computed(() => {
    return useShopInfoStore().getterChangePasswordFlag
})
const strength = reactive({
    weak: false,
    medium: false,
    strong: false,
})
const passwordFormFocus = reactive({
    code: false,
    password: false,
})
// 密码校验控制
const passwordRules = ref<boolean | null>(null)
// 二次密码校验控制
const confirmPasswordRules = ref(true)
const validatePassword = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码'))
        passwordRules.value = false
        return
    }
    if (value.length < 6 && value.length > 20) {
        callback(new Error('密码必须是6~20位'))
        passwordRules.value = false
        return
    }
    if (!REGEX.PASSWORD.test(value)) {
        callback(new Error('密码格式不正确'))
        passwordRules.value = false
        return
    }
    passwordRules.value = true
    callback()
}

const rules = reactive<FormRules>({
    code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    password: [{ required: true, message: '', validator: validatePassword, trigger: 'blur' }],
    confirmPassword: [{ required: true, message: '', validator: confirmPasswordValid, trigger: 'blur' }],
})
const info = ref<AccountInfo>({
    email: '',
    mobile: '',
    username: '',
})
const showSliderCaptcha = ref(false)
// const timesb
let times: NodeJS.Timer

getUserData()

function confirmPasswordValid(rule: any, value: any, callback: any) {
    if (value === '' || value !== passwordForm.value.password) {
        callback(new Error('密码输入不一致'))
        confirmPasswordRules.value = false
        return
    }
    confirmPasswordRules.value = true
    callback()
}
async function getUserData() {
    const { data, code } = await doGetUserDataAccount()
    if (code !== 200) return ElMessage.error('获取基本信息失败')
    info.value = data
}

const getCaptchaSlider = async () => {
    const { code, data } = await doGetCaptchaSlider(info.value?.manageMobile || '', 'RESET_PASSWORD')
    if (code !== 200) {
        return
    }
    if (data.captcha) {
        captchaList.value = data.captcha
        showSliderCaptcha.value = true
    } else {
        passwordForm.value.code = data.smsCode
    }
}

const handleToggleBindingPassword = () => {
    toggleBindingText.value = '修改密码'
    isUpdatePassword.value = true
    passwordFormFocus.code = false
    passwordFormFocus.password = false
}

watch(
    () => useShopInfoStore().getterChangePasswordFlag,
    (val) => {
        if (val) {
            handleToggleBindingPassword()
        }
    },
)

/**
 * 倒计时获取验证码
 */
const handleSendVerificationCode = async (response: any) => {
    passwordForm.value.code = response?.data || ''
    showSliderCaptcha.value = false
    isCountdown.value = true
    times = setInterval(() => {
        if (time.value === 1) {
            timeText.value = '重新发送'
            isCountdown.value = false
            time.value = 60
            clearInterval(times as NodeJS.Timeout)
        }
        time.value--
    }, 1000)
}
const handleSubmit = async () => {
    if (!ruleFormRef.value) return
    const isValida = await ruleFormRef.value.validate()
    if (!isValida) return
    useShopInfoStore().SET_CHANGE_PASSWORD_FLAG(false)
    const { password, confirmPassword, code } = passwordForm.value
    const { code: res } = await doPutMyResetPassword(code, password, confirmPassword)
    if (res !== 200) return ElMessage.error('提交失败')
    ElMessage.success('重置密码成功')
    passwordForm.value = {
        code: '',
        password: '',
        confirmPassword: '',
    }
    // 清缓存
    useShopInfoStore().DEL_SHOP_INFO()
    // 退出登录
    doPostLogout()
    $router.replace({
        path: '/sign',
    })
    ruleFormRef.value.resetFields()
}
/**
 * 密码强度
 * @param {*}
 */
const handleStrengthShow = () => {
    // 弱密码：全是数字或全是字母，6-16个字符
    const weakReg = /^[0-9]{6,16}$|^[a-zA-Z]{6,16}$/
    // 中密码：数字和26个英文字母，6-16个字符
    const mediumReg = /^[A-Za-z0-9]{6,16}$/
    // 强密码：由数字、26个英文字母或者下划线组成的字符串，6-16个字符
    const strongReg = /^\w{6,16}$/
    const pas = passwordForm.value.password
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
const handleClose = () => {
    if (!ruleFormRef.value) return
    timeText.value = '重新发送'
    isCountdown.value = false
    time.value = 60
    if (times) {
        clearInterval(times as NodeJS.Timeout)
    }
    passwordForm.value = {
        code: '',
        password: '',
        confirmPassword: '',
    }
    ruleFormRef.value.resetFields()
    passwordRules.value = null
    confirmPasswordRules.value = true
    useShopInfoStore().SET_CHANGE_PASSWORD_FLAG(false)
}
</script>

<template>
    <el-dialog v-model="changePasswordFlag" :title="toggleBindingText" width="500px" destroy-on-close @close="handleClose">
        <el-form
            v-if="isUpdatePassword"
            ref="ruleFormRef"
            :model="passwordForm"
            :rules="rules"
            label-width="95px"
            label-position="left"
            hide-required-asterisk
        >
            <el-form-item label="手机号">
                {{ info?.manageMobile }}
            </el-form-item>
            <el-form-item label="验证码" prop="code" style="margin-bottom: 18px">
                <div class="verification_code">
                    <el-input
                        v-model="passwordForm.code"
                        placeholder="请输入4位数验证码"
                        maxlength="4"
                        style="width: 140px"
                        :type="passwordFormFocus.code ? 'text' : 'search'"
                        @focus="passwordFormFocus.code = true"
                        @blur="passwordFormFocus.code = false"
                    />

                    <el-button class="el-Verification" type="primary" plain :disabled="isCountdown" round @click="getCaptchaSlider">{{
                        isCountdown ? `${time}秒重新发送` : `${timeText}`
                    }}</el-button>
                </div>
            </el-form-item>

            <el-form-item label="输入新密码" prop="password">
                <div style="display: flex; align-items: center">
                    <el-input
                        v-model.trim="passwordForm.password"
                        :show-password="passwordFormFocus.password"
                        :type="passwordFormFocus.password ? 'password' : 'text'"
                        placeholder="6-10个字符"
                        style="width: 248px"
                        maxlength="20"
                        @keyup="handleStrengthShow"
                        @focus="passwordFormFocus.password = true"
                    />
                    <div class="strength">
                        <span class="weak" :class="{ danger: strength.weak }">弱</span>
                        <span class="medium" :class="{ success: strength.medium }">中</span>
                        <span class="strong" :class="{ brand: strength.strong }">强</span>
                    </div>
                </div>
                <p class="password_rules">
                    <QIcon
                        :name="passwordRules ? 'icon-dui1' : 'icon-guanbi11'"
                        :color="passwordRules ? '#00BB2C' : passwordRules !== null ? '#F54319' : '#999'"
                        :size="passwordRules ? '16px' : '14px'"
                        style="margin-right: 5px"
                    />密码长度 6~20 位，数字、字母、字符至少包含两种
                </p>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                    v-model.trim="passwordForm.confirmPassword"
                    :show-password="passwordFormFocus.password"
                    :type="passwordFormFocus.password ? 'password' : 'text'"
                    placeholder="两次密码必须一致"
                    style="width: 248px"
                    maxlength="20"
                    @focus="passwordFormFocus.password = true"
                />
                <p v-if="!confirmPasswordRules" style="color: #f54319; margin-top: 12px; line-height: 20px">
                    两次密码不一致，请检查&emsp;&emsp;&emsp;
                </p>
            </el-form-item>
        </el-form>
        <el-form v-else ref="ruleFormRef" :model="form" :rules="rules">
            <el-form-item label="验证方式">
                <div style="margin-left: 50px">手机验证</div>
            </el-form-item>
            <el-form-item label="绑定手机">
                <div style="margin-left: 50px">18345208780</div>
            </el-form-item>
            <el-form-item label="验证码" prop="code">
                <el-input v-model.number="form.code" placeholder="6位数字验证码" maxlength="6" style="width: 120px; margin-left: 50px" />
                <el-button :style="{ marginLeft: '20px' }" type="primary" round @click="handleSendVerificationCode">{{
                    isCountdown ? `${time}秒后重新发送` : `${timeText}`
                }}</el-button>
            </el-form-item>
        </el-form>
        <slider-captcha
            v-model="showSliderCaptcha"
            :do-submit="doGetMyResetPasswordSms"
            :get-form="() => info?.manageMobile"
            :scale="1"
            :captcha-list="captchaList"
            @success="handleSendVerificationCode"
        />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
.weak,
.medium,
.strong {
    display: inline-block;
    height: 35px;
    width: 18px;
    border-bottom: 4px solid gainsboro;
    margin-left: 3px;
    font-size: 12px;
    text-align: center;
    margin-right: 14px;
}
@include b(danger) {
    color: #f54319;
    border-bottom-color: #f54319;
}
@include b(success) {
    color: #fd9224;
    border-bottom-color: #fd9224;
}
@include b(brand) {
    color: #00bb2c;
    border-bottom-color: #00bb2c;
}
.verification_code {
    display: flex;
    .el-Verification {
        margin-left: 20px;
        border-radius: 20px !important;
        background-color: #fff;
        &:hover {
            background-color: #fff;
            color: #555cfd;
        }
    }
}
.password_rules {
    color: #999;
    display: flex;
    align-items: center;
    line-height: 22px;
    margin-top: 8px;
}
.strength {
    display: flex;
    align-items: center;
    margin-left: 15px;
    position: relative;
    top: -3px;
}
.el-row:last-child {
    margin-bottom: 0;
}
.el-col {
    border-radius: 4px;
}
.grid-content {
    border-radius: 4px;
    min-height: 36px;
}
:deep(.el-input__wrapper) {
    background-color: #f6f6f6;
    box-shadow: none;
}
:deep(.is-error .el-input__wrapper) {
    background-color: #f543190d;
}
</style>
