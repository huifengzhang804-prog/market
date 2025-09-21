<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { doGetUserDataAccount, doPutResetPassword, doGetResetPasswordSms, doPostLogout } from '@/apis/sign'
import { useAdminInfo } from '@/store/modules/admin'
import { REGEX } from '@/constant'
import { useVModel } from '@vueuse/core'
import { REGEX_MOBILE } from '@/libs/validate'
import type { FormInstance, FormRules } from 'element-plus'

interface AccountInfo {
    email?: string
    mobile?: string
    username?: string
}

const props = defineProps({
    isShow: {
        type: Boolean,
        default() {
            return false
        },
    },
    loadUserData: {
        type: Boolean,
        default() {
            return true
        },
    },
})
const emit = defineEmits(['update:isShow'])
const _isShow = useVModel(props, 'isShow', emit)

const ruleFormRef = ref<FormInstance>()
const toggleBindingText = ref('')
const form = ref({ code: '' })
const router = useRouter()
// 密码form data
const passwordForm = ref({ password: '', confirmPassword: '', code: '', mobile: '' })
const time = ref(60)
const timeText = ref('获取验证码')
const isCountdown = ref(false)
const strength = reactive({
    weak: false,
    medium: false,
    strong: false,
})
const phoneValidatePass = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入手机号'))
    } else if (!REGEX_MOBILE(value)) {
        callback(new Error('手机号格式有误'))
    } else {
        callback()
    }
}
const rules = reactive<FormRules>({
    mobile: [
        {
            required: true,
            validator: phoneValidatePass,
            trigger: 'blur',
        },
    ],
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
// const timesb
let times: NodeJS.Timer

onMounted(() => props.loadUserData && getUserData())

function confirmPasswordValid(rule: any, value: any, callback: any) {
    if (value !== passwordForm.value.password) {
        callback(new Error('密码输入不一致'))
        return
    }
    callback()
}

async function getUserData() {
    const { data, code } = await doGetUserDataAccount()
    console.log('code', code)

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
    _isShow.value = true
}
const handleToggleBindingMobile = () => {
    toggleBindingText.value = '换绑手机号'
    _isShow.value = true
}
const handleToggleBindingPassword = () => {
    toggleBindingText.value = '修改密码'
    _isShow.value = true
}
/**
 * 倒计时获取验证码
 */
const handleSendVerificationCode = async () => {
    if (!REGEX_MOBILE(passwordForm.value.mobile)) {
        ElMessage.error('手机号输入有误')
        return
    }
    const { code } = await doGetResetPasswordSms(passwordForm.value.mobile)
    if (code !== 200) {
        ElMessage.error('验证码获取失败')
        return
    }
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
    _isShow.value = false
    const { password, confirmPassword, code, mobile } = passwordForm.value
    const { code: res } = await doPutResetPassword(code, password, confirmPassword, mobile)
    if (res !== 200) return ElMessage.error('提交失败')
    ElMessage.success('重置密码成功')
    passwordForm.value = {
        code: '',
        password: '',
        confirmPassword: '',
        mobile: '',
    }
    // 清缓存
    useAdminInfo().REMOVE_ADMIN_INFO()
    // 退出登录
    doPostLogout()
    router.replace({
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
    timeText.value = '获取验证码'
    isCountdown.value = false
    time.value = 60
    if (times) {
        clearInterval(times as NodeJS.Timeout)
    }
    passwordForm.value = {
        code: '',
        password: '',
        confirmPassword: '',
        mobile: '',
    }
    ruleFormRef.value.resetFields()
    _isShow.value = false
}
</script>

<template>
    <el-dialog v-model="_isShow" :title="toggleBindingText" width="600px">
        <el-tag class="tag">
            <div>
                <div class="tag__box mb-10">
                    <el-icon>
                        <i-ep-infoFilled />
                    </el-icon>
                    弱密码：全是数字或全是字母，6-20个字符
                </div>
                <div class="tag__box mb-10">
                    <el-icon>
                        <i-ep-infoFilled />
                    </el-icon>
                    中密码：数字和26个英文字母，6-20个字符
                </div>
                <div class="tag__box">
                    <el-icon>
                        <i-ep-infoFilled />
                    </el-icon>
                    强密码：由数字、26个英文字母或者下划线组成的字符串，6-20个字符
                </div>
            </div>
        </el-tag>
        <el-form ref="ruleFormRef" :model="passwordForm" :rules="rules" label-width="100px">
            <el-form-item label="手机号" prop="mobile">
                <el-row :gutter="24">
                    <el-col :span="20">
                        <el-input v-model="passwordForm.mobile" maxlength="11" placeholder="请输入手机号码" />
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item label="验证码" prop="code">
                <el-row :gutter="24">
                    <el-col :span="14">
                        <el-input v-model="passwordForm.code" maxlength="4" placeholder="4位数字验证码" />
                    </el-col>
                    <el-col :span="6">
                        <el-button
                            :disabled="!!(passwordForm.mobile.length !== 11) || isCountdown"
                            round
                            style="width: 100px"
                            type="primary"
                            @click="handleSendVerificationCode"
                        >
                            {{ isCountdown ? `${time}秒重新发送` : `${timeText}` }}
                        </el-button>
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item>
                <span :class="{ danger: strength.weak }" class="weak">弱</span>
                <span :class="{ success: strength.medium }" class="medium">中</span>
                <span :class="{ brand: strength.strong }" class="strong">强</span>
            </el-form-item>
            <el-form-item label="新密码" prop="password">
                <el-input
                    v-model.trim="passwordForm.password"
                    maxlength="20"
                    placeholder="6到20个字符"
                    show-password
                    style="width: 300px"
                    type="password"
                    @keyup="handleStrengthShow"
                />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    v-model.trim="passwordForm.confirmPassword"
                    maxlength="20"
                    placeholder="两次输入必须一致"
                    show-password
                    style="width: 300px"
                    type="password"
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(tag) {
    display: block;
    padding: 20px;
    height: auto;
    margin-bottom: 20px;
    border-radius: 15px;
    @include e(box) {
        @include flex;
        justify-content: flex-start;
        align-items: center;
    }
}

@include b(mb-10) {
    margin-bottom: 10px;
}

@include b(flex) {
    @include flex;
    justify-content: flex-start;
    margin-top: 20px;
}

@include b(grid-contents) {
    border-radius: 4px;
    min-height: 30px;
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
