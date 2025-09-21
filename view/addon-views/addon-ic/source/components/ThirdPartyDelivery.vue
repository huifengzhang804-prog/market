<script setup lang="ts">
import DescStep from '@/views/set/components/DescStep.vue'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import { doGetShopUuptStatus, type ShopUuptStatus, doPostSmsCaptcha, type QueryCaptchaResult, doAuthSms, getRechargeQrCode } from '../apis'
import { onMounted, ref, computed, watch, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import QIcon from '@/components/q-icon/q-icon.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import { debounce } from 'lodash-es'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()

onMounted(() => {
  getShopUuptStatus()
})

/**
 * UUPT账号的状态
 */
const shopUuptStatus = ref<ShopUuptStatus>({
  platformActivated: false,
  cityOpen: false,
  activated: false,
  balance: 0,
  frozen: 0,
})
/**
 * 获取当前商户UUPT账号的状态
 */
const getShopUuptStatus = async () => {
  try {
    const { data, code } = await doGetShopUuptStatus()
    if (code !== 200) {
      ElMessage.error('获取第三方配送信息失败')
      return
    }
    shopUuptStatus.value = data
  } catch (error) {
    console.log(error)
    ElMessage.error('获取第三方配送信息失败')
  }
}

/**
 * 授权登录弹窗
 */
const authorizationActivationDialogVisible = ref(false)
const authorizationActivation = () => {
  authorizationActivationDialogVisible.value = true
}
/**
 * 充值弹窗
 */
const rechargeUuptDialogVisible = ref(false)
/**
 * 充值UU跑腿
 */
const rechargeUupt = async () => {
  // 发请求拿到二维码
  const { data } = await getRechargeQrCode()
  rechargeQrCode.value = data.h5Qrcode
  rechargeUuptDialogVisible.value = true
}
/**
 * 充值二维码
 */
const rechargeQrCode = ref('')

/**
 * 平台未激活
 */
const notPlatformActivated = computed(() => !shopUuptStatus.value.platformActivated)
/**
 * 平台激活但是未开通城市
 */
const notCityOpen = computed(() => shopUuptStatus.value.platformActivated && !shopUuptStatus.value.cityOpen)
/**
 * 平台激活已开通城市但是手机号未绑定
 */
const notActivated = computed(() => shopUuptStatus.value.platformActivated && shopUuptStatus.value.cityOpen && !shopUuptStatus.value.activated)

interface AuthForm {
  mobile: string
  captcha?: string
  smsCode?: string
}
/**
 * 授权手机号表单
 */
const form = ref<AuthForm>({
  mobile: '',
  captcha: '',
  smsCode: '',
})
/**
 * 手机号校验规则
 */
const validateMobile = (rule: any, value: any, callback: FN) => {
  if (value === '') {
    base64CaptchaObj.value.base64Captcha = ''
    callback(new Error('请输入手机号'))
  } else if (!REGEX_MOBILE(value)) {
    base64CaptchaObj.value.base64Captcha = ''
    callback(new Error('手机号格式有误'))
  } else {
    callback()
  }
}
/**
 * 图片验证码
 */
const base64CaptchaObj = ref<QueryCaptchaResult>({
  needCaptcha: false,
  base64Captcha: '',
})
/**
 * 获取图片验证码
 * @param reGet 是否清空验证码
 */
const getImgCaptcha = async (reGet = true) => {
  if (reGet) {
    form.value.captcha = ''
  }
  try {
    await validateFieldMobile()
    const res = await doPostSmsCaptcha({ ...form.value })
    if (res?.msg) {
      if (res?.data?.msg) {
        ElMessage.error(res?.data?.msg)
      } else {
        ElMessage.error(res.msg)
      }
      return Promise.reject(res.msg)
    }
    if (res?.data) {
      base64CaptchaObj.value.needCaptcha = res?.data?.needCaptcha || false
      if (res.data.needCaptcha) {
        base64CaptchaObj.value.base64Captcha = res.data.base64Captcha
      } else {
        base64CaptchaObj.value.base64Captcha = ''
      }
      return res.data.needCaptcha
    }
  } catch (error) {
    console.log('sss', error)
    return Promise.reject(error)
  }
}
const rules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      validator: validateMobile,
      trigger: 'change',
    },
  ],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  smsCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }],
}
const intervalTime = ref(60)
const formRef = ref()
const timer = ref<NodeJS.Timeout>()
const btnLoading = ref(false)
/**
 * 获取短信验证码
 */
const getSmsCode = async () => {
  // 进入倒计时
  intervalTime.value = 60
  btnLoading.value = true
  setTimeout(() => {
    btnLoading.value = false
  }, 1000)
  const needCaptcha = await getImgCaptcha(false)
  form.value.captcha = ''
  if (!needCaptcha) {
    // 计时器60秒
    timer.value = setInterval(() => {
      if (intervalTime.value > 0) {
        intervalTime.value--
      } else {
        clearInterval(timer.value)
        intervalTime.value = 60
        // getImgCaptcha()
      }
    }, 1000)
  }
}

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})

/**
 * 校验手机号
 */
const validateFieldMobile = async () => {
  try {
    const valid = await formRef.value.validateField('mobile')
    console.log(valid)
    if (valid) {
      return valid
    }
  } catch (error) {
    console.log('校验手机号失败', error)
    return Promise.reject(error)
  }
}
/**
 * 防抖输入
 */
const mobileChange = debounce(getImgCaptcha, 200)
// 防抖重新获取图片验证码
const reGetImgCaptcha = debounce(getImgCaptcha, 5000, {
  // 立即执行
  leading: true,
  // 结尾不执行
  trailing: false,
})
/**
 * 确认授权开通
 */
const confirmAuthorizationActivation = () => {
  formRef.value.validate(async (valid: boolean, fields: any) => {
    if (valid) {
      const res = await doAuthSms({ mobile: form.value.mobile, smsCode: form.value.smsCode })
      if (res.success) {
        // 授权成功，关闭弹窗
        authorizationActivationDialogVisible.value = false
        // 刷新uupt状态
        getShopUuptStatus()
      } else {
        ElMessage.error(res?.data?.msg || res.msg)
      }
    } else {
      console.log('error submit!', fields)
    }
  })
}
const recharged = () => {
  getShopUuptStatus()
  rechargeUuptDialogVisible.value = false
}
</script>

<template>
  <div class="tab_container fdc1">
    <div class="describe_bar">
      <div class="top">
        开通流程：
        <DescStep :steps="['① 授权开通', '② 充值', '③勾选第三方配送']"></DescStep>
      </div>
      <div class="bottom">免责声明：<span class="danger">因开通第三方配送服务产生的一切费用由您自行承担，本平台不加收任何费用。</span></div>
    </div>
    <TitleBar name="UU跑腿">
      <template #right>
        <div v-if="shopUuptStatus.activated" class="right_text">UU跑腿配送费从账户余额扣，配送中金额冻结</div>
        <div v-else-if="notActivated" class="right_text">
          <el-button type="primary" text @click="authorizationActivation">授权开通</el-button>
        </div>
      </template>
    </TitleBar>
    <div v-if="notPlatformActivated" class="error_msg">平台运营商未注册UU跑腿开发者账号，请通知平台运营商设置【第三方配送】！</div>
    <div v-else-if="notCityOpen" class="error_msg">店铺所在(地区)区域，UU跑腿暂不支持配送，无法授权开通 ，或联系平台运营人员调整店铺地址！</div>
    <div v-else-if="notActivated" class="error_msg">您还未开通UU跑腿配送服务，请授权开通UU跑腿！</div>
    <div v-else class="success_msg">
      <div class="descriptions fcenter">
        <div class="descriptions_label" style="width: 120px">开通状态：</div>
        <div class="descriptions_content">
          <span class="open_status">已开通</span>
        </div>
      </div>
      <div class="descriptions fcenter">
        <div class="descriptions_label" style="width: 120px">账户金额(元)：</div>
        <div class="descriptions_content">
          <span class="money red">{{ divTenThousand(shopUuptStatus.balance) }}</span>
          <el-button type="primary" text @click="rechargeUupt">充值</el-button>
        </div>
      </div>
      <div class="descriptions fcenter">
        <div class="descriptions_label" style="width: 120px">
          冻结金额(元)
          <el-tooltip effect="dark" content="配送中金额冻结" placement="bottom">
            <q-icon class="cup" name="icon-wenhao"></q-icon>
          </el-tooltip>
          ：
        </div>
        <div class="descriptions_content">
          <span class="money">{{ divTenThousand(shopUuptStatus.frozen) }}</span>
        </div>
      </div>
    </div>
    <el-dialog v-model="authorizationActivationDialogVisible" title="授权开通UU跑腿" width="500px">
      <div>
        <el-form ref="formRef" :rules="rules" :model="form" label-width="150px" label-position="left">
          <el-form-item label="手机号（账户名）" prop="mobile">
            <el-input v-model="form.mobile" maxlength="11" placeholder="请输入手机号码" style="width: 250px"></el-input>
          </el-form-item>

          <el-form-item v-if="base64CaptchaObj.needCaptcha" label="图形验证码" prop="captcha">
            <div class="img_captcha_container fcenter">
              <el-input v-model="form.captcha" maxlength="6" placeholder="请输入验证码" style="width: 140px"></el-input>
              <el-image
                :src="base64CaptchaObj.base64Captcha"
                alt=""
                style="margin-left: 25px; width: 85px; height: 40px"
                class="cup"
                @click="() => reGetImgCaptcha()"
              >
                <template #error>
                  <div style="color: #b6b6b6">稍后重试</div>
                </template>
              </el-image>
            </div>
          </el-form-item>
          <el-form-item label="短信验证码" prop="smsCode">
            <div class="sms_captcha_container fcenter">
              <el-input v-model="form.smsCode" maxlength="6" placeholder="请输入短信验证码" style="width: 140px"></el-input>
              <el-button v-if="intervalTime === 60" style="margin-left: 25px" plain :loading="btnLoading" @click="getSmsCode">获取</el-button>
              <el-button v-else style="margin-left: 25px" plain disabled>重新发送 {{ intervalTime }}s</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="authorizationActivationDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmAuthorizationActivation">确 定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="rechargeUuptDialogVisible" title="充值UU跑腿" width="500px">
      <div class="ccenter fdc">
        <div>请使用微信或支付宝的【扫一扫】功能扫码充值</div>
        <el-image style="width: 300px; height: 300px" :src="rechargeQrCode" fit="cover"></el-image>
        <el-button type="link" @click="recharged">已充值返回并刷新</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.describe_bar {
  background: rgba(85, 92, 253, 0.05);
  padding-top: 20px;
  padding-left: 16px;
  padding-right: 16px;
  padding-bottom: 13px;
  .top {
    display: flex;
    flex-wrap: wrap;
  }
  .bottom {
    margin-top: 11px;
    color: rgb(51, 51, 51);
    font-size: 13px;
    font-weight: 400;
    .danger {
      color: rgb(245, 67, 25);
      font-size: 13px;
      font-weight: 500;
    }
  }
}
.right_text {
  color: rgb(153, 153, 153);
  font-size: 14px;
  font-weight: 400;
}
.error_msg {
  color: rgb(245, 67, 25);
  font-size: 14px;
  font-weight: 500;
}
.success_msg {
  .descriptions {
    margin-bottom: 15px;
    .descriptions_label {
      text-align: right;
      margin-right: 18px;
      color: rgb(102, 102, 102);
      font-size: 14px;
      font-weight: 500;
    }
    .descriptions_content {
      display: flex;
      align-items: center;
      .money {
        margin-right: 12px;
      }
      .red {
        color: #f54319;
      }
    }
  }
  .open_status {
    font-size: 14px;
    font-weight: 500;
    color: #00bb2c;
  }
}
</style>
