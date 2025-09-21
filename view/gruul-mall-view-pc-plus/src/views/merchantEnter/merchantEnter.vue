<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import { ElMessage, UploadProps, ElLoading } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { doAddShops } from '@/apis/enter'
import type { FormInstance, FormRules } from 'element-plus'
import AmapChooseDialog from '../personalcenter/set/address/components/AmapChooseDialog.vue'
import { Checked, Geometry, GeometryType } from '@/apis/address/types'
import { doGetAddressByLocation } from '@/apis/address'

const $router = useRouter()
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'

/* *表单数据 */
const formMerchant = reactive({
  companyName: '',
  name: '',
  logo: '',
  contractNumber: '',
  address: '',
  briefing: '',
  mode: 'COMMON',
  registerInfo: {
    license: '',
    legalPersonIdFront: '',
    legalPersonIdBack: '',
  },
  license: '',
  legalPersonIdFront: '',
  legalPersonIdBack: '',
  bankAccount: {
    payee: '',
    bankName: '',
    bankAccount: '',
    openAccountBank: '',
  },
  payee: '',
  bankName: '',
  bankAcc: '',
  openAccountBank: '',
  area: [] as unknown as [string, string, string?],
  location: {
    type: GeometryType.Point,
    coordinates: [] as unknown as [number, number],
  } as Geometry,
  shopMode: 'COMMON',
  fakeAddress: '', // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
})
const header = {
  Authorization: useUserStore().getterToken,
  'Device-Id': '1',
  Platform: 'PC',
  'Shop-Id': '1',
  'Client-Type': 'CONSUMER',
}

const uploadImgSuccessLogo: UploadProps['onSuccess'] = (response, uploadFile) => {
  formMerchant.logo = response.data
}
const uploadImgSuccessLicense: UploadProps['onSuccess'] = (response, uploadFile) => {
  formMerchant.license = response.data
}
const uploadImgSuccessFront: UploadProps['onSuccess'] = (response, uploadFile) => {
  formMerchant.legalPersonIdFront = response.data
}
const uploadImgSuccessBack: UploadProps['onSuccess'] = (response, uploadFile) => {
  formMerchant.legalPersonIdBack = response.data
}

const clickFn = (id: string) => {
  btnCancelFn(ruleFormRef.value, false)
  const loading = ElLoading.service({
    lock: true,
    text: '加载中',
    background: 'rgba(255, 255, 255, 0.7)',
  })
  setTimeout(() => {
    loading.close()
    formMerchant.shopMode = id
  }, 300)
}
const ruleFormRef = ref<FormInstance>()
const rules = reactive<FormRules>({
  companyName: { required: true, message: `请填写商家名称`, trigger: 'blur' },
  name: [
    { required: true, message: `请填写名称`, trigger: 'blur' },
    { max: 8, min: 2, message: `长度在 2 - 8 个字符串之间`, trigger: 'change' },
  ],
  contractNumber: { required: true, message: `请填写联系方式`, trigger: 'blur' },
  briefing: { required: true, message: `请填写店铺介绍`, trigger: 'blur' },
  mode: { required: true, message: `请选择经营模式`, trigger: 'blur' },
  bankName: { required: true, message: `请填写银行名称`, trigger: 'blur' },
  payee: { required: true, message: `请填写收款人`, trigger: 'blur' },
  bankAcc: { required: true, message: `请填写账户`, trigger: 'blur' },
  openAccountBank: { required: true, message: `请填写开户行`, trigger: 'blur' },
  logo: { required: true, message: `请上传logo`, trigger: 'blur' },
  license: { required: true, message: `请上传营业执照`, trigger: 'blur' },
  legalPersonIdFront: { required: true, message: `请上传证件照(正面)`, trigger: 'blur' },
  legalPersonIdBack: { required: true, message: `请上传证件照(反面)`, trigger: 'blur' },
  address: { required: true, message: `请填写详细地址`, trigger: 'blur' },
  location: { required: true, message: '' },
  area: [{ required: true, message: '请选择定位', trigger: 'change' }],
  fakeAddress: [
    {
      required: true,
      message: '请填写详细地址',
      trigger: 'change',
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value.includes('~')) {
          callback(new Error('详细地址不能包含~号'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
})

// 取消
const btnCancelFn = (formEl: FormInstance | undefined, bol: boolean) => {
  if (!formEl) return
  formEl.resetFields()
  if (bol) {
    // 关闭当前页面
    window.close()
  }
}

// 提交
const btnPrimaryFn = (formEl: FormInstance | undefined) => {
  if (formMerchant.shopMode === 'SUPPLIER') formMerchant.mode = 'SUPPLIER'
  formMerchant.bankAccount.payee = formMerchant.payee
  formMerchant.bankAccount.bankName = formMerchant.bankName
  formMerchant.bankAccount.bankAccount = formMerchant.bankAcc
  formMerchant.bankAccount.openAccountBank = formMerchant.openAccountBank
  formMerchant.registerInfo.legalPersonIdBack = formMerchant.legalPersonIdBack
  formMerchant.registerInfo.legalPersonIdFront = formMerchant.legalPersonIdFront
  formMerchant.registerInfo.license = formMerchant.license
  const copyForm = JSON.parse(JSON.stringify(formMerchant))
  copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
  if (!formEl) return
  // @ts-ignore
  formEl.validate(async (valid: boolean) => {
    if (valid) {
      if (!copyForm.location.coordinates.length) return ElMessage.error('请打开地图选择定位')
      const params = {
        ...copyForm,
        shopMode: copyForm.mode,
      }
      delete params.mode
      const { code, msg } = await doAddShops(params)
      if (code === 200) {
        formEl.resetFields()
        ElMessage.success('申请提交成功')
        setTimeout(() => {
          // 关闭当前页面
          window.close()
        }, 500)
      } else {
        ElMessage.error(msg || '添加失败')
      }
    } else {
      ElMessage.error('请把信息填写完整')
    }
  })
}

onMounted(() => {
  document.body.style.setProperty('--el-color-primary', '#4B80FF')
  document.body.style.setProperty('--el-color-primary-light-3', '#4B80FF')
  document.body.style.setProperty('--el-color-primary-light-5', '#4B80FF')
  document.body.style.setProperty('--el-color-primary-dark-2', '#4B80FF')
  // 设置网页名称
  document.title = '商家入驻'
})

const showChooseMap = ref(false)
const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [Number(res.longitude), Number(res.latitude)],
  }
  formMerchant.location = checkedLocation

  const { area, address } = await doGetAddressByLocation(checkedLocation.coordinates, false)
  if (area) {
    formMerchant.area = area
  }
  if (address) {
    formMerchant.address = `${address}~${res.name}`
  }
}
const placeChoosed = (place: Checked) => {
  handleChooseRes(place)
}
</script>
<template>
  <div style="background-color: #fff">
    <div class="con">
      <div class="title">{{ formMerchant.shopMode !== 'SUPPLIER' ? '商家' : '供应商' }}入驻申请</div>
      <div class="select">
        <span class="select__Text"><i class="select__Text--imp">* </i>商家类型(单选)</span>
        <span :class="formMerchant.shopMode === 'COMMON' ? 'select__Fn clickFn' : 'select__Fn'" @click="clickFn('COMMON')">店铺入驻</span>
        <span
          :class="formMerchant.shopMode === 'SUPPLIER' ? 'select__Fn clickFn' : 'select__Fn'"
          style="margin-left: 16px"
          @click="clickFn('SUPPLIER')"
          >供应商入驻</span
        >
      </div>
      <el-form ref="ruleFormRef" :model="formMerchant" :rules="rules" label-width="116px">
        <div class="headline"><span class="headline__titleLine" />基本信息</div>
        <el-row>
          <el-col :span="8">
            <el-form-item label="商户名称" prop="companyName">
              <el-input v-model="formMerchant.companyName" maxlength="20" class="input" placeholder="请输入商家" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="formMerchant.shopMode === 'COMMON' ? '店铺名称' : '供应商名称'" prop="name">
              <el-input
                v-model="formMerchant.name"
                maxlength="20"
                :placeholder="formMerchant.shopMode === 'COMMON' ? '请输入店铺名称' : '请输入供应商名称'"
                class="input"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系方式" prop="contractNumber">
              <el-input v-model="formMerchant.contractNumber" maxlength="11" placeholder="请输入联系方式" class="input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col v-if="formMerchant.shopMode !== 'SUPPLIER'" :span="8">
            <el-form-item label="经营模式" prop="mode">
              <el-radio-group v-model="formMerchant.mode" class="ml-4">
                <el-radio value="COMMON" size="large"> 线上模式 </el-radio>
                <el-radio value="O2O" size="large"> O2O模式 </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="定位地址" prop="area" class="address_form_item">
              <div class="fcenter cup" @click="showChooseMap = true">
                <div class="address_area">
                  <div v-if="formMerchant.address" class="address_name">{{ formMerchant.address.split('~')[1] || '' }}</div>
                  <div v-else style="color: #0066ff">打开地图选择定位</div>
                  <div v-if="formMerchant.area" class="address_detail_name">
                    {{ formMerchant.area?.join?.('') }}{{ formMerchant.address.split('~')[0] }}
                  </div>
                </div>
                <el-icon style="color: rgb(0, 102, 255)"><ArrowRight /></el-icon>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="text-align: left">
          <el-col :span="16"
            ><el-form-item label="详细地址" prop="fakeAddress">
              <el-input
                v-model="formMerchant.fakeAddress"
                style="width: 625px"
                :rows="1"
                type="textarea"
                placeholder="例如: [3单元203室]"
                :maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="text-align: left">
          <el-col :span="16">
            <el-form-item :label="formMerchant.shopMode === 'COMMON' ? '店铺介绍' : '供应商介绍'" prop="briefing">
              <el-input
                v-model="formMerchant.briefing"
                :autosize="{ minRows: 3, maxRows: 3 }"
                type="textarea"
                style="width: 625px"
                :rows="3"
                placeholder="请输入店铺介绍"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="headline"><span class="headline__titleLine" />收款账户</div>
        <el-row>
          <el-col :span="8">
            <el-form-item label="银行名称" prop="bankName">
              <el-input v-model="formMerchant.bankName" class="input" placeholder="请输入银行名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="收款人" prop="payee">
              <el-input v-model="formMerchant.payee" placeholder="请输入收款人" class="input" maxlength="60" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="账户" prop="bankAcc">
              <el-input v-model="formMerchant.bankAcc" placeholder="请输入账户" class="input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="开户行" prop="openAccountBank">
              <el-input v-model="formMerchant.openAccountBank" placeholder="请输入开户行" class="input" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="headline"><span class="headline__titleLine" />资料上传</div>
        <el-row style="margin-bottom: 6px">
          <el-col :span="8" style="text-align: left">
            <el-form-item :label="formMerchant.shopMode === 'COMMON' ? '店铺logo' : '供应商logo'" prop="logo">
              <el-upload class="avatar" :headers="header" :action="uploadUrl" :show-file-list="false" :on-success="uploadImgSuccessLogo">
                <img v-if="formMerchant.logo" :src="formMerchant.logo" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="8" style="text-align: left">
            <el-form-item label="营业执照" prop="license">
              <el-upload class="avatar" :headers="header" :action="uploadUrl" :show-file-list="false" :on-success="uploadImgSuccessLicense">
                <img v-if="formMerchant.license" :src="formMerchant.license" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <div class="sampleGraph">
                <img class="sampleGraph__img" src="https://obs.xiaoqa.cn/gruul/20220712/33c6a922f6b34ddb816debb2ad096aaf.png" />
                <span class="sampleGraph__text">要拍清晰，不可上传模糊图片</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="margin-bottom: 4px">
          <el-col :span="8" style="text-align: left">
            <el-form-item label="证件照(正面)" prop="legalPersonIdFront">
              <el-upload class="avatar" :headers="header" :action="uploadUrl" :show-file-list="false" :on-success="uploadImgSuccessFront">
                <img v-if="formMerchant.legalPersonIdFront" :src="formMerchant.legalPersonIdFront" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <div class="sampleGraph">
                <img class="sampleGraph__img" src="https://obs.xiaoqa.cn/gruul/20220713/1ce1c16ab0e34bf7beda44db1c540d00.png" />
                <span class="sampleGraph__text">要拍清晰，不可上传模糊图片</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="8" style="text-align: left">
            <el-form-item label="证件照(反面)" prop="legalPersonIdBack">
              <el-upload class="avatar" :headers="header" :action="uploadUrl" :show-file-list="false" :on-success="uploadImgSuccessBack">
                <img v-if="formMerchant.legalPersonIdBack" :src="formMerchant.legalPersonIdBack" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <div class="sampleGraph">
                <img class="sampleGraph__img" src="https://obs.xiaoqa.cn/gruul/20220713/ba2c76dae4ad4319bb948afcad241f48.png" />
                <span class="sampleGraph__text">要拍清晰，不可上传模糊图片</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <AmapChooseDialog v-model="showChooseMap" :initLocation="formMerchant.location.coordinates" @placeChoose="placeChoosed"></AmapChooseDialog>
      </el-form>
    </div>
    <div class="btn">
      <div style="display: flex">
        <div class="btn__cancel" @click="btnCancelFn(ruleFormRef, true)">取消</div>
        <div class="btn__primary" @click="btnPrimaryFn(ruleFormRef)">提交</div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="scss">
.address_form_item {
  .fcenter {
    width: 100%;
    .address_area {
      padding: 1px 11px;
      display: flex;
      flex-direction: column;
      max-width: calc(100% - 80px);
      text-align: left;
      line-height: 18px;
      .address_name {
        font-size: 16px;
        font-weight: bold;
      }
      .address_detail_name {
        color: #a8abb2;
      }
    }
  }
}
@include b(clickFn) {
  background-color: #0066ff;
  color: #fff !important;
}
@include b(headline) {
  text-align: left;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 18px;
  @include e(titleLine) {
    display: inline-block;
    width: 4px;
    height: 20px;
    margin-right: 8px;
    background-color: #4b80ff;
    transform: translateY(3px);
  }
}
@include b(input) {
  width: 223px;
  border: 1px solid rgb(215, 220, 228);
  border-radius: 2px;
  height: 28px;
  font-size: 14px;
}
@include b(con) {
  width: 1190px;
  margin: 0 auto;
}
@include b(title) {
  font-size: 28px;
  color: #141414;
  padding: 32px 0 48px 0;
}
@include b(select) {
  display: flex;
  margin-bottom: 37px;
  @include e(Text) {
    width: 116px;
    text-align: right;
    font-size: 14px;
    line-height: 28px;
    margin-right: 4px;
    font-weight: 600;
    @include m(imp) {
      color: #ff5f67;
      padding: 0 4px;
      font-weight: normal;
    }
  }
  @include e(Fn) {
    width: 88px;
    height: 28px;
    line-height: 28px;
    font-size: 14px;
    border: 1px solid #d7dce4;
    color: #9d9d9d;
    border-radius: 2px;
    overflow: hidden;
    cursor: pointer;
  }
}
@include b(sampleGraph) {
  width: 100px;
  height: 100px;
  // border: 1px dashed #dcdae2;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 28px;
  transform: translateY(-10px);
  @include e(img) {
    width: 90px;
    // height: 80px;
    margin: 6px 8px;
    margin-left: 5px;
    margin-top: 7px;
  }
  @include e(text) {
    // padding: 0 6px 0 11px;
    // width: 230px;
    font-size: 12px;
    color: #9d9d9d;
    line-height: 17px;
    margin-top: 3px;
    // padding-left: 130px;
  }
}
@include b(btn) {
  margin-top: auto;
  padding: 17px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 10px #2e2c2c4d;
  @include e(cancel) {
    cursor: pointer;
    width: 104px;
    line-height: 40px;
    color: #0066ff;
    border: 1px solid #0066ff;
  }
  @include e(primary) {
    cursor: pointer;
    width: 104px;
    line-height: 40px;
    color: #fff;
    background-color: #0066ff;
    margin-left: 20px;
  }
}
:deep().--el-color-primary {
  color: #0066ff !important;
}
@include b(avatar) {
  width: 100px;
  height: 100px;
  border: 1px dashed #dcdae2;
  border-radius: 4px;
  display: block;
  display: flex;
  justify-content: center;
}
</style>
