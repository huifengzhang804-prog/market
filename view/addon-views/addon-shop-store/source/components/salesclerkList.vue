<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { REGEX_MOBILE } from '@/libs/validate'
import { PostAddassIstant, doPostSmsCode, GetAssistantList, PutAssistant, DelAssistant, GetStoreList } from '../apis'
import { storeListType, salesclerkListType } from '../index'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
import { doGetCaptchaSlider } from '@/apis/sign'

const salesclerkList = ref<salesclerkListType[]>([])
const storeList = ref<storeListType[]>([])
const addsalesclerk = ref(false)
const editsalesclerk = ref(false)
const showSliderCaptcha = ref(false)
const intervalTime = ref(60)
const interval = ref(null as any)
const captchaList = ref()
const salesclerkinfo = ref({
  assistantPhone: '',
  assistantPhoneCode: '',
})
const associatedstores = ref({
  shopAssistantId: '',
  storeId: '',
})
/**
 * 当前选中店员手机号
 */
const phone = ref('')

onMounted(() => {
  getSalesclerkList()
  getStoreList()
})

/**
 * 验证码回调（倒计时）
 */
const slideCaptchaSuccess = (data: any) => {
  ElMessage.success('验证码已发送')
  intervalTime.value -= 1
  showSliderCaptcha.value = false
  salesclerkinfo.value.assistantPhoneCode = data?.data
  interval.value = setInterval(() => {
    intervalTime.value -= 1
    if (intervalTime.value <= 0) {
      intervalTime.value = 60
      if (interval.value) {
        clearInterval(interval.value)
        interval.value = null
      }
      return
    }
  }, 999)
}
/**
 * 发送验证码
 */
const sendSmsCode = async () => {
  if (!salesclerkinfo.value.assistantPhone) {
    return ElMessage.error('请填写手机号')
  }
  if (!REGEX_MOBILE(salesclerkinfo.value.assistantPhone)) {
    return ElMessage.error('请填写正确的手机号')
  }
  const { code, data } = await doGetCaptchaSlider(salesclerkinfo.value.assistantPhone, 'SHOP_STORE_FOUND')
  if (code !== 200) {
    return
  }
  if (data.captcha) {
    captchaList.value = data.captcha
    showSliderCaptcha.value = true
  } else {
    salesclerkinfo.value.assistantPhoneCode = data.smsCode
  }
}
/**
 * 添加店员
 */
const SaveAddSalesclerk = async () => {
  clearInterval(interval.value)
  interval.value = null
  intervalTime.value = 60
  const { code, msg } = await PostAddassIstant(salesclerkinfo.value)
  if (code === 200) {
    ElMessage.success('添加成功')
    getSalesclerkList()
    salesclerkinfo.value = { assistantPhone: '', assistantPhoneCode: '' }
    addsalesclerk.value = false
  } else {
    salesclerkinfo.value.assistantPhone = ''
    salesclerkinfo.value.assistantPhoneCode = ''
    ElMessage.error(msg ? msg : '添加失败')
  }
}
/**
 * 打开店员关联门店弹窗
 */
const EditeSalesclerk = (assistantPhone: string, shopAssistantId: string, storeId: string) => {
  phone.value = assistantPhone
  associatedstores.value.shopAssistantId = shopAssistantId
  associatedstores.value.storeId = storeId
  editsalesclerk.value = true
}
/**
 * 保存店员关联门店
 */
const SaveEditeSalesclerk = async () => {
  const { code, msg } = await PutAssistant(associatedstores.value.shopAssistantId, { storeId: associatedstores.value.storeId })
  if (code === 200) {
    editsalesclerk.value = false
    ElMessage.success('添加成功')
    getSalesclerkList()
  } else {
    ElMessage.error(msg ? msg : '添加失败')
  }
}
/**
 * 删除店员
 */
const DelSalesclerk = async (shopAssistantId: string) => {
  ElMessageBox.confirm('确定删除该店员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, msg } = await DelAssistant(shopAssistantId)
    if (code === 200) {
      ElMessage.success('删除成功')
      getSalesclerkList()
    } else {
      ElMessage.error(msg ? msg : '删除失败')
    }
  })
}

/**
 * 获取店员列表
 */
async function getSalesclerkList() {
  const { code, data } = await GetAssistantList()
  if (code === 200 && data) {
    salesclerkList.value = data as salesclerkListType[]
  } else {
    ElMessage.error('获取列表失败')
  }
  addsalesclerk.value = false
}

/**
 * 获取门店列表
 */
async function getStoreList() {
  const { code, data } = await GetStoreList({ size: 5 })
  if (code === 200 && data) {
    storeList.value = data.records
  } else {
    ElMessage.error('获取列表失败')
  }
}
// 取消
const cancelFn = () => {
  addsalesclerk.value = false
  Object.keys(salesclerkinfo.value).forEach((key) => (salesclerkinfo.value[key as keyof typeof salesclerkinfo.value] = ''))
  intervalTime.value = 60
  clearInterval(interval.value)
}
</script>

<template>
  <div class="fdc1 overh">
    <div class="handle_container fcenter">
      <el-button round type="primary" @click="addsalesclerk = true"> 添加店员</el-button>
      <div style="color: #8f8f95" class="mla">店员凭手机号登录门店移动端，核销到店自提订单</div>
    </div>
    <div class="table_container">
      <el-table
        :data="salesclerkList"
        :header-cell-style="{
          'background-color': '#F6F8FA',
          'font-weight': 'bold',
          color: '#515151',
        }"
        empty-text="暂无数据~"
        style="width: 100%; height: calc(100vh - 210px)"
      >
        <el-table-column label="店员手机号">
          <template #default="{ row }">
            <div class="ellipsis">{{ row.assistantPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="所属门店">
          <template #default="{ row }">
            <div class="ellipsis">{{ row.storeName ? row.storeName : '' }}</div>
          </template>
        </el-table-column>
        <el-table-column align="right" label="操作">
          <template #default="{ row }">
            <el-button link type="primary" style="margin-right: 16px" @click="EditeSalesclerk(row.assistantPhone, row.id, row.storeId)"
              >编辑</el-button
            >
            <el-button link type="danger" @click="DelSalesclerk(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="addsalesclerk" center title="店员" width="500px" :before-close="cancelFn">
      <div class="addsalesclerk__phone">
        店员手机号
        <div class="addsalesclerk__phone--input">
          <el-input v-model="salesclerkinfo.assistantPhone" maxlength="11" placeholder="请输入电话号码" style="width: 200px"></el-input>
          <el-button v-if="intervalTime === 60" plain @click="sendSmsCode">获取验证码</el-button>
          <el-button v-else disabled link> {{ intervalTime }}</el-button>
        </div>
      </div>
      <div class="addsalesclerk__code">
        <div class="addsalesclerk__code--title">验证码</div>
        <el-input
          v-model="salesclerkinfo.assistantPhoneCode"
          maxlength="6"
          placeholder="请输入验证码"
          style="width: 200px; margin-left: 35px"
        ></el-input>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button round @click="cancelFn">取消</el-button>
          <el-button round type="primary" @click="SaveAddSalesclerk"> 保存 </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="editsalesclerk" center title="所属门店" width="500px">
      <div class="addsalesclerk__phone">
        店员手机号
        <div class="addsalesclerk__phone--input">{{ phone }}</div>
      </div>
      <div class="addsalesclerk__code">
        <div class="addsalesclerk__code--title">所属门店</div>
        <el-select v-model="associatedstores.storeId" placeholder="选择门店" style="margin-left: 35px">
          <el-option v-for="item in storeList" :key="item.id" :label="item.storeName" :value="item.id" />
        </el-select>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button round @click="editsalesclerk = false">取消</el-button>
          <el-button round type="primary" @click="SaveEditeSalesclerk"> 保存 </el-button>
        </span>
      </template>
    </el-dialog>
    <SliderCaptcha
      v-model="showSliderCaptcha"
      :do-submit="doPostSmsCode"
      :get-form="() => salesclerkinfo.assistantPhone"
      :scale="1"
      :captcha-list="captchaList"
      sms-type="SHOP_STORE_FOUND"
      @success="slideCaptchaSuccess"
    />
  </div>
</template>

<style lang="scss" scoped>
@include b(addsalesclerk) {
  @include e(phone) {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    @include m(input) {
      margin-left: 35px;
    }
  }
  @include e(code) {
    display: flex;
    align-items: center;
    @include m(title) {
      width: 72px;
      text-align: right;
    }
  }
}
</style>
