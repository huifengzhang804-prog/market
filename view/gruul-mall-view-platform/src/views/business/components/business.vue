<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doGetUserDataAccount, doGetMyResetPasswordSms, doPutMyResetPassword, doPostLogout, doGetCaptchaSlider } from '@/apis/sign'
import { useAdminInfo } from '@/store/modules/admin'
import { REGEX } from '@/constant'
import type { FormInstance, FormRules } from 'element-plus'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
interface AccountInfo {
    email?: string
    mobile?: string
    username?: string
    manageMobile?: string
}
const captchaList = ref()
const ruleFormRef = ref<FormInstance>()
const toggleBindingDialog = ref(false)
const toggleBindingText = ref('')
const form = ref({ code: '' })
const $router = useRouter()
// 密码form data
const passwordForm = ref({ password: '', confirmPassword: '', code: '' })
const time = ref(60)
const timeText = ref('获取验证码')
const isCountdown = ref(false)
const isUpdatePassword = ref(false)
const strength = reactive({
    weak: false,
    medium: false,
    strong: false,
})
const passwordFormFocus = reactive({
    code: false,
    password: false,
})
const rules = reactive<FormRules>({
    code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
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
    if (value !== passwordForm.value.password) {
        callback(new Error('密码输入不一致'))
        return
    }
    callback()
}
async function getUserData() {
    const { data, code } = await doGetUserDataAccount()
    if (code !== 200) return ElMessage.error('获取基本信息失败')
    console.log('data', data)
    info.value = data
}
const handleToggleBinding = (type: string) => {
    switch (type) {
        case 'vx':
            handleToggleBindingVx()
            break
        case 'mobile':
            handleToggleBindingMobile()
            break
        case 'password':
            handleToggleBindingPassword()
            break
        default:
            break
    }
}
const handleToggleBindingVx = () => {
    toggleBindingText.value = '换绑微信'
    toggleBindingDialog.value = true
}
const handleToggleBindingMobile = () => {
    toggleBindingText.value = '换绑手机号'
    toggleBindingDialog.value = true
}
const handleToggleBindingPassword = () => {
    toggleBindingText.value = '修改密码'
    isUpdatePassword.value = true
    passwordFormFocus.code = false
    passwordFormFocus.password = false
    toggleBindingDialog.value = true
}

const sendSmsCode = () => {
    getCaptchaSlider()
}
const getCaptchaSlider = async () => {
    const { code, data } = await doGetCaptchaSlider(info.value.manageMobile || info.value.mobile || '', 'RESET_PASSWORD')
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
    toggleBindingDialog.value = false
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
    useAdminInfo().REMOVE_ADMIN_INFO()
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
    toggleBindingDialog.value = false
}
</script>

<template>
    <div style="padding: 30px">
        <div v-if="info.username" class="row">
            <div>
                <span style="display: inline-block; width: 180px">注册账号</span>
                <span>
                    {{ info.username }}
                </span>
            </div>
        </div>
        <div v-if="info.manageMobile || info.mobile" class="row">
            <div>
                <span style="display: inline-block; width: 180px">手机号</span>
                <span>
                    {{ info.manageMobile || info.mobile }}
                </span>
            </div>
        </div>
        <div v-if="info.email" class="row">
            <div>
                <span style="display: inline-block; width: 180px">邮箱</span>
                <span>
                    {{ info.email }}
                </span>
            </div>
        </div>
        <div class="row" style="display: flex">
            <div>
                <span style="display: inline-block; width: 180px">密码</span>
                <span style="display: inline-block; width: 188px">******</span>
            </div>
            <div>
                <el-button type="primary" plain round @click="handleToggleBinding('password')">修改密码</el-button>
            </div>
        </div>
    </div>

    <el-dialog v-model="toggleBindingDialog" center :title="toggleBindingText" width="600px" destroy-on-close @close="handleClose">
        <el-form v-if="isUpdatePassword" ref="ruleFormRef" :model="passwordForm" :rules="rules" label-width="100px">
            <el-form-item>
                <span class="weak" :class="{ danger: strength.weak }">弱</span>
                <span class="medium" :class="{ success: strength.medium }">中</span>
                <span class="strong" :class="{ brand: strength.strong }">强</span>
            </el-form-item>
            <p style="margin: 0 0 10px 100px">密码长度 6~20 位，数字、字母、字符至少包含两种</p>
            <el-form-item label="新密码" prop="password">
                <el-input
                    v-model.trim="passwordForm.password"
                    :show-password="passwordFormFocus.password"
                    :type="passwordFormFocus.password ? 'password' : 'text'"
                    placeholder="6到20个字符"
                    style="width: 300px"
                    maxlength="20"
                    @keyup="handleStrengthShow"
                    @focus="passwordFormFocus.password = true"
                />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    v-model.trim="passwordForm.confirmPassword"
                    :show-password="passwordFormFocus.password"
                    :type="passwordFormFocus.password ? 'password' : 'text'"
                    placeholder="两次输入必须一致"
                    style="width: 300px"
                    maxlength="20"
                    @focus="passwordFormFocus.password = true"
                />
            </el-form-item>
            <el-form-item label="手机号">
                {{ info?.manageMobile }}
            </el-form-item>
            <el-form-item label="验证码" prop="code">
                <el-row :gutter="24">
                    <el-col :span="14">
                        <el-input
                            v-model="passwordForm.code"
                            placeholder="4位数字验证码"
                            maxlength="4"
                            :type="passwordFormFocus.code ? 'text' : 'search'"
                            @focus="passwordFormFocus.code = true"
                            @blur="passwordFormFocus.code = false"
                        />
                    </el-col>
                    <el-col :span="6">
                        <el-button style="width: 100px" type="primary" :disabled="isCountdown" round @click="sendSmsCode">{{
                            isCountdown ? `${time}秒重新发送` : `${timeText}`
                        }}</el-button>
                    </el-col>
                </el-row>
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
.row {
    margin-bottom: 60px;
}
.weak,
.medium,
.strong {
    display: inline-block;
    height: 35px;
    width: 48px;
    border-bottom: 4px solid gainsboro;
    margin-left: 3px;
    font-size: 12px;
    text-align: center;
}
@include b(danger) {
    border-bottom-color: #f56c6c;
}
@include b(success) {
    border-bottom-color: #67c23a;
}
@include b(brand) {
    border-bottom-color: #409eff;
}
.el-row {
    margin-bottom: 20px;
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
</style>
