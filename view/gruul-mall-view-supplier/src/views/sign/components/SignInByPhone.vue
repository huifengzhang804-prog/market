<template>
    <div v-loading="loading" class="SignFlow">
        <el-form ref="signFormRef" :model="signForm" :rules="signRules" class="SignInForm" hide-required-asterisk size="large" @submit="submitForm">
            <div class="SignInForm__tabs">
                <div
                    :class="{ 'SignInForm__tab--active': signHandler.password }"
                    class="SignInForm__tab"
                    @click="signForm.loginType = LoginType.PASSWORD"
                >
                    密码登录
                </div>
                <div
                    :class="{ 'SignInForm__tab--active': signHandler.mobile }"
                    class="SignInForm__tab"
                    @click="signForm.loginType = LoginType.SMS_CODE"
                >
                    验证码登录
                </div>
            </div>
            <el-form-item prop="account">
                <el-input v-model="signForm.account" placeholder="请输入手机号 或 账号" :maxlength="11">
                    <template #prepend>
                        <qIcon class="icon-wode-wode" size="20px" color="#999" />
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input
                    v-if="signHandler.password"
                    v-model="signForm.password"
                    :type="passIsShow ? 'text' : 'password'"
                    placeholder="请输入登录密码"
                    :maxlength="20"
                >
                    <template #prepend>
                        <qIcon class="icon-mima" size="20px" color="#999" />
                    </template>
                    <template #append>
                        <qIcon
                            :class="passIsShow ? 'icon-yanjing_xianshi' : 'icon-yanjing_yincang'"
                            size="20px"
                            color="#999"
                            @click="passIsShow = !passIsShow"
                        />
                    </template>
                </el-input>
                <el-input v-if="signHandler.mobile" v-model="signForm.password" maxlength="4" placeholder="输入短信验证码">
                    <template #prepend>
                        <qIcon class="icon-mima" size="20px" color="#999" />
                    </template>
                    <template #suffix>
                        <el-button v-if="signForm.time === intervalTime" link class="sms-btn" @click="sendSmsCode"> 发送验证码 </el-button>
                        <el-button v-else disabled link> {{ signForm.time }}秒</el-button>
                    </template>
                </el-input>
            </el-form-item>
            <el-button
                class="SignInForm__button"
                :color="canLogin ? '#FF794D' : '#B0B0B0'"
                native-type="submit"
                :disabled="!canLogin"
                @click="submitForm"
                >登录</el-button
            >
            <div class="SignInForm__option">
                <el-link :underline="false" @click="handleResetPassword"> 忘记密码 ?</el-link>
            </div>
            <slider-captcha
                v-model="signForm.showSliderCaptcha"
                :do-submit="doPostSmsCode"
                :get-form="() => signForm.account"
                :scale="1"
                :captchaList="captchaList"
                @success="slideCaptchaSuccess"
            />
        </el-form>
    </div>
    <!-- 忘记密码 -->
    <change-password v-model:is-show="toggleBindingDialog" :load-user-data="false" />
</template>

<script lang="ts" setup>
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { reactive, ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getLoginPermShops, signByUser, doPostSmsCode, doGetCaptchaSlider, doGetUserMenu } from '@/apis/sign'
import { REGEX } from '@/constant'
import ChangePassword from '@/views/business/components/changePassword.vue'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
import type { FormRules } from 'element-plus'
import { doGetShopInfo } from '@/apis/afs'
import { GrantType } from '@/apis/sign/index.type'
import { useMenuList } from '@/store/modules/menu'

const canLogin = computed(() => signForm.account && signForm.password)
const passIsShow = ref(false)
interface AccountInfo {
    email?: string
    mobile?: string
    username?: string
}

enum LoginType {
    PASSWORD = 'password',
    SMS_CODE = 'sms_code',
}

interface SignHandle {
    grantType: GrantType
    password: boolean
    mobile: boolean
    label: string
    placeholder: string
    formData: () => any
    accountValidate: (account: string, callback: (arg?: Error) => void) => void
    passwordValidate: (password: string, callback: (arg?: Error) => void) => void
}

const toggleBindingDialog = ref(false)
const intervalTime = 60
const loading = ref(false)
const signFormRef = ref()
const router = useRouter()
const route = useRoute()
const shopStore = useShopInfoStore()
const passwordForm = ref({ password: '', confirmPassword: '', code: '' })
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
const loginTypeHandler: Record<LoginType, SignHandle> = {
    password: {
        grantType: GrantType.PASSWORD,
        password: true,
        mobile: false,
        label: '手机号码',
        placeholder: '请输入手机号码',
        formData: () => {
            return { username: signForm.account, password: signForm.password }
        },
        accountValidate(account, callback) {
            if (!account) {
                callback(new Error('请输入手机号 或 账号'))
                return
            }
            callback()
        },
        passwordValidate(password, callback) {
            if (!password) {
                callback(new Error('请输入密码'))
                return
            }
            callback()
        },
    },
    sms_code: {
        grantType: GrantType.SMS_CODE,
        password: false,
        mobile: true,
        label: '手机号码',
        placeholder: '请输入手机号码',
        formData: () => {
            return { mobile: signForm.account, code: signForm.password }
        },
        accountValidate(account, callback) {
            if (/^1\d{10}$/.test(account)) {
                callback()
                return
            }
            callback(new Error('请输入正确的手机号'))
        },
        passwordValidate(password, callback) {
            if (/\d{4}$/.test(password)) {
                callback()
                return
            }
            callback(new Error('请输入正确的验证码'))
        },
    },
}

function confirmPasswordValid(rule: any, value: any, callback: any) {
    if (value !== passwordForm.value.password) {
        callback(new Error('密码输入不一致'))
        return
    }
    callback()
}

const signHandler = computed<SignHandle>(() => loginTypeHandler[signForm.loginType])

const signForm = reactive({
    readonly: true,
    showSliderCaptcha: false,
    loginType: LoginType.PASSWORD,
    shop: null,
    visible: false,
    shops: [],
    account: null,
    password: null as null | string,
    disabled: false,
    time: intervalTime,
    interval: null as any,
})
const captchaList = ref()

watch(
    () => signForm.loginType,
    () => (signForm.password = null),
)

const signRules = reactive({
    account: [
        {
            required: true,
            validator: (rule: any, value: string, callback: (arg?: Error) => void) => signHandler.value.accountValidate(value, callback),
            trigger: 'blur',
        },
    ],
    // shop: [{ required: true, message: '请选择店铺' }],
    password: [
        {
            validator: (rule: any, value: string, callback: (arg?: Error) => void) => signHandler.value.passwordValidate(value, callback),
        },
    ],
})

const changeShop = (shop: any) => {
    signForm.shop = shop
    signForm.visible = false
}
/**
 * @description: 忘记密码
 * @returns {*}
 */
const handleResetPassword = () => {
    toggleBindingDialog.value = true
}
const usernameChange = () => {
    signForm.shops = []
    signForm.shop = null
}
const sendSmsCode = () => {
    const formRef = signFormRef.value
    if (!formRef) return
    formRef.validateField('account', (valid: boolean) => {
        if (!valid) return
        formRef.validateField('shop', (shopValid: boolean) => {
            if (!shopValid) return
            getCaptchaSlider()
        })
    })
}

const getCaptchaSlider = async () => {
    const { code, data } = await doGetCaptchaSlider(signForm.account, 'LOGIN')
    if (code !== 200) {
        return
    }
    if (data.captcha) {
        captchaList.value = data.captcha
        signForm.showSliderCaptcha = true
    } else {
        signForm.password = data.smsCode
    }
}

const slideCaptchaSuccess = (response: { code: number; data: string }) => {
    signForm.password = response.data
    ElMessage.success('验证码已发送')
    signForm.time -= 1
    signForm.showSliderCaptcha = false
    signForm.interval = setInterval(() => {
        signForm.time -= 1
        if (signForm.time <= 0) {
            signForm.time = intervalTime
            if (signForm.interval) {
                clearInterval(signForm.interval)
                signForm.interval = null
            }
            return
        }
    }, 999)
}

const loadShop = () => {
    const account = signForm.account
    if (!account) {
        return
    }
    getLoginPermShops(account).then((response) => {
        const shops = response.data
        if (!shops || shops.length === 0) {
            signForm.shops = []
            ElMessage.error('用户名或手机号填写有误。')
            return
        }
        signForm.shops = shops
        if (shops.length === 1) {
            signForm.visible = false
            signForm.shop = shops[0]
            return
        }
        signForm.visible = true
    })
}

function submitForm(e: Event) {
    e.preventDefault()
    e.stopPropagation()
    const formRef = signFormRef.value
    if (!formRef) return
    formRef.validate((valid: boolean) => {
        if (!valid) return
        signByUser(signHandler.value.grantType, signHandler.value.formData()).then(async ({ code, data, msg }: any) => {
            if (code !== 200) {
                ElMessage.error(msg)
                return
            }
            const { value, refreshToken, open } = data
            try {
                const shopResult = await doGetShopInfo(open.shopId)
                const shopData = shopResult?.data
                if (shopData) {
                    shopStore.SET_SHOP_INFO({
                        id: shopData?.id,
                        logo: shopData?.logo,
                        name: shopData?.name,
                        newTips: shopData?.newTips || '',
                        status: shopData?.status,
                        // mode: shopData?.mode,
                        shopId: open.shopId,
                        userId: open.userId,
                        shopType: shopData?.shopType,
                        shopMode: shopData?.shopMode,
                    })
                    shopStore.SET_SHOP_TOKEN({ access_token: value, refresh_token: refreshToken.value })
                    // 登录成功后获取菜单数据
                    await asyncGetSubMenuData()
                    router.replace({ path: '/' })
                }
            } catch (err) {
                ElMessage.error(err as string)
            }
        })
    })
}

const asyncGetSubMenuData = async () => {
    const res = await doGetUserMenu()
    if (res.code !== 200) {
        return
    }
    // 存到pinia
    useMenuList().SET_MENU(res.data.menus)
    useMenuList().SET_ISADMIN(res.data.isAdmin)
}
</script>

<style lang="scss" scoped>
// 覆盖样式
:deep(.el-input__inner) {
    border-color: transparent;
}

:deep(.el-form-item) {
    border-bottom: 1px solid #f8f5f9;
}

:deep(.el-checkbox__inner) {
    width: 12px;
    height: 12px;
}

:deep(.el-checkbox__inner)::after {
    height: 6px;
    left: 3px;
    top: 0;
}

:deep(.el-checkbox__label) {
    color: #909399;
    padding-left: 6px;
    font-size: 12px;
}
.SignInForm__option {
    margin-top: 50px;
}
:deep(.SignInForm__option .el-button--text) {
    color: #909399;
}

.SignFlow {
    margin-top: 50px;
}
.SignInForm__tabs {
    margin-bottom: 30px;
    display: flex;
    justify-content: center;
    .SignInForm__tab {
        margin: 0 10px;
        width: 120px;
        height: 40px;
        box-sizing: border-box;
        line-height: 40px;
        display: inline-block;
        list-style: none;
        font-size: 24px;
        font-weight: 500;
        color: #999;
        position: relative;
        cursor: pointer;
    }

    .SignInForm__tab--active {
        color: #ff794d;
    }
}

:deep(.el-input-group__prepend) {
    box-shadow: none;
    background-color: #fff;
    padding: 0;
}
:deep(.el-input-group__append) {
    box-shadow: none;
    background-color: #fff;
    padding: 0;
}
:deep(.el-input__wrapper) {
    box-shadow: none;
}
:deep(.el-form-item.is-error .el-input__wrapper) {
    box-shadow: none;
}
:deep(.el-form-item.is-error) {
    border-bottom: 1px solid var(--el-color-danger);
}
.SignInForm__button {
    width: 100%;
    color: #fff;
    height: 48px;
    line-height: 48px;
}
.sms-btn {
    color: #5790ff;
}
.el-form-item + .el-form-item {
    margin-top: 30px;
}
</style>
