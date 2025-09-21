<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { Ref } from 'vue'
import { onReady } from '@dcloudio/uni-app'
import { getheader } from '@/libs/request/returnHeader'
//引入表单验证规则
import { formRule } from './addressRule'
//引入add添加店铺方法
import { doAddShops } from '@/apis/shops'
import Auth from '@/components/auth/auth.vue'
import { useLocationStore } from '@/store/modules/location'
import type { Checked, Coordinate, Geometry } from '@/apis/address/type'
import { GeometryType } from '@/apis/address/type'
// #ifdef H5
import AmapChoose from '@/basePackage/pages/addressManage/components/AmapChoose.vue'
// #endif
import { getLocation, locToAddress } from '@/apis/amap'

//form元素节点
const formData = ref()
//表单验证类型
const errorType = ref(['message'])
//表单数据
const formMerchant = reactive({
  companyName: '',
  name: '',
  logo: null,
  contractNumber: '',
  address: '',
  briefing: '',
  mode: 'B2B2C',
  registerInfo: {
    license: null,
    legalPersonIdFront: null,
    legalPersonIdBack: null,
  },
  license: null,
  legalPersonIdFront: null,
  legalPersonIdBack: null,
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
  location: {
    type: 'Point',
    coordinates: [] as unknown as Coordinate,
  } as Geometry,
  shopMode: 'COMMON',
  area: [] as unknown as [string, string, string?],
  fakeAddress: '', // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
})
//获取上传地址
const action: string = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_UPLOAD_URI
//安全内边距
// const safeBottom = useSafeBottom()
// iphoneX以上机型底部安全距离
const safeHeight: Ref<number> = ref(0)

onReady(() => {
  //设置表单验证规则
  formData.value.setRules(formRule)
})
/**
 * 设置安全距离
 * @param {*} success
 */
uni.getSystemInfo({
  success: (res) => {
    if (res.safeAreaInsets) {
      safeHeight.value = res.safeAreaInsets.bottom
    }
  },
})

/**
 * // 表单提交
 * @param {*} formData
 */
const submit = async () => {
  formMerchant.bankAccount.payee = formMerchant.payee
  formMerchant.bankAccount.bankName = formMerchant.bankName
  formMerchant.bankAccount.bankAccount = formMerchant.bankAcc
  formMerchant.bankAccount.openAccountBank = formMerchant.openAccountBank
  formMerchant.shopMode = formMerchant.mode === 'O2O' ? 'O2O' : 'COMMON'
  //表单验证
  formData.value.validate(async (valid: boolean) => {
    if (valid) {
      // 处理下假地址
      const copyForm = JSON.parse(JSON.stringify(formMerchant))
      copyForm.bankAccount.payee = copyForm.payee
      copyForm.bankAccount.bankName = copyForm.bankName
      copyForm.bankAccount.bankAccount = copyForm.bankAcc
      copyForm.bankAccount.openAccountBank = copyForm.openAccountBank
      copyForm.shopMode = copyForm.mode === 'O2O' ? 'O2O' : 'COMMON'
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      delete copyForm.fakeAddress
      const { code, msg } = await doAddShops(copyForm)
      if (code !== 200) {
        if (code === 100003) {
          return uni.showToast({
            title: '已存在同名店铺名称,请修改后重新提交',
            icon: 'none',
            duration: 2000,
          })
        }
        return uni.showToast({
          title: `${msg ?? '添加失败'}`,
          icon: 'none',
          duration: 2000,
        })
      }
      uni.showToast({
        title: '申请店铺成功',
        icon: 'none',
        duration: 2000,
      })
      uni.navigateBack({
        delta: 1,
      })
    } else {
      return uni.showToast({
        title: '请把表单填写完整',
        icon: 'none',
        duration: 2000,
      })
    }
  })
}

/**
 * //图片上传成功
 * @param {*} data 上传成功后的数据
 * @param {*} index 图片索引
 * @param {*} lists  图片列表
 * @param {*} name 图片名称
 */
const uploadSuccess = (data: any, index: number, lists: any[], name: 'logo' | 'license' | 'legalPersonIdBack' | 'legalPersonIdFront') => {
  // console.log('上传成功', data, name)
  if (data.code === 200) {
    if (name === 'logo') {
      formMerchant.logo = data.data
    } else {
      formMerchant[name] = formMerchant.registerInfo[name] = data.data
    }
    console.log(formMerchant)
  } else {
    uni.showToast({ title: '上传失败', icon: 'none' })
  }
}

/**
 * 图片删除
 * @param {*} index 图片索引
 * @param {*} lists  图片列表
 * @param {*} name  图片名称
 */
const onRemove = (index: number, lists: any[], name: 'logo' | 'license' | 'legalPersonIdBack' | 'legalPersonIdFront') => {
  console.log(index, lists, name)
  if (name === 'logo') {
    formMerchant.logo = null
  } else {
    formMerchant[name] = formMerchant.registerInfo[name] = null
  }
  console.log(formData.value)
  console.log(formMerchant)
}

/**
 * 获取当前用户地址
 */
const getUserLocation = async () => {
  const res = await getLocation()
  // 存储pinia
  useLocationStore().SET_LOCATION({
    type: GeometryType.Point,
    coordinates: [res.longitude, res.latitude],
  })
  formMerchant.location.coordinates = [res.longitude, res.latitude]
  console.log(res, '获取当前地址成功')
}
getUserLocation()

// #ifdef H5
const showChooseMap = ref(false)
const placeChoose = (e: Checked) => {
  handleChooseRes(e)
}
// #endif
const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [Number(res.longitude), Number(res.latitude)],
  }
  formMerchant.location = checkedLocation
  const { area, address } = await locToAddress(res, false)
  if (area) {
    formMerchant.area = area
  }
  if (address) {
    formMerchant.address = `${address}~${res.name}`
  }
}
const toChooseAddress = () => {
  // #ifndef H5
  uni.chooseLocation({
    latitude: formMerchant.location?.coordinates?.[1] || [121.489551, 29.936806][1],
    longitude: formMerchant.location?.coordinates?.[0] || [121.489551, 29.936806][0],
    success: (res) => {
      console.log(res)
      handleChooseRes(res)
    },
    fail: (err) => {
      console.log('err', err)
    },
  })
  // #endif
  // #ifdef H5
  showChooseMap.value = true
  // #endif
}
</script>

<template>
  <view :style="{ height: `${50 + safeHeight}px;` }">
    <u-form ref="formData" class="formMerchant" :label-width="190" label-align="center" :model="formMerchant" :error-type="errorType">
      <view style="padding: 0 20rpx">
        <span class="formMerchant__title">基本信息</span>
        <u-form-item label="商户名称" prop="companyName" :required="true">
          <u-input v-model="formMerchant.companyName" :maxlength="20" placeholder="请输入商户名称" />
        </u-form-item>
        <u-form-item label="店铺名称" :required="true" prop="name">
          <u-input v-model="formMerchant.name" :maxlength="20" placeholder="请输入店铺名称" />
        </u-form-item>
        <u-form-item label="店铺logo" :required="true" prop="logo" :label-width="190">
          <u-upload
            index="logo"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
            @on-remove="
              (index: number, lists: any[], name: string) => {
                onRemove(index, lists, 'logo')
              }
            "
          ></u-upload>
        </u-form-item>
        <u-form-item label="经营模式">
          <u-radio-group v-model="formMerchant.mode">
            <u-radio name="B2B2C"> 线上模式 </u-radio>
            <u-radio name="O2O"> O2O模式 </u-radio>
          </u-radio-group>
        </u-form-item>
        <u-form-item label="联系方式" :required="true" prop="contractNumber">
          <u-input v-model="formMerchant.contractNumber" :maxlength="11" placeholder="请输入您的联系方式" />
        </u-form-item>
        <!-- <u-form-item label="选择地址" prop="storeAddress">
          <MerchantMap :latitude="latitude" :longitude="longitude" @map-change="getMapLocation" />
        </u-form-item> -->
        <u-form-item label="定位地址" :required="true" prop="area" right-icon="arrow-right">
          <view class="address_area" @click="() => toChooseAddress()">
            <view v-if="formMerchant.address" class="address_name">{{ formMerchant.address.split('~')[1] || '' }}</view>
            <view v-else style="color: #c0c4cc">打开地图选择定位</view>
            <view v-if="formMerchant.area" class="address_detail_name"
              >{{ formMerchant.area?.join?.('') }}{{ formMerchant.address.split('~')[0] }}
            </view>
          </view>
        </u-form-item>
        <u-form-item :required="true" prop="fakeAddress" label="详细地址">
          <u-input v-model="formMerchant.fakeAddress" placeholder="例如: [3单元203室]" height="150" auto-height="false" maxlength="20" />
        </u-form-item>
        <u-form-item :required="true" prop="briefing">
          <view class="formMerchant__storeIntroduction">
            <span class="formMerchant__storeIntroduction--title">店铺介绍</span>
            <u-input
              v-model="formMerchant.briefing"
              placeholder="请输入店铺介绍"
              type="textarea"
              height="150"
              auto-height="false"
              width="500"
              maxlength="100"
            />
          </view>
        </u-form-item>
        <span class="formMerchant__title">信息登记</span>
        <u-form-item class="formMerchant__register" label="营业执照" :required="true" prop="license" :label-width="200">
          <u-upload
            index="license"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
            @on-remove="
              (index: number, lists: any[], name: string) => {
                onRemove(index, lists, 'license')
              }
            "
          ></u-upload>
          <img
            class="formMerchant__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e49e4b0dd235a3dd37b.png"
            alt=""
          />
        </u-form-item>
        <u-form-item label="证件照正面" :required="true" prop="legalPersonIdFront" :label-width="210">
          <u-upload
            index="legalPersonIdFront"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
            @on-remove="
              (index: number, lists: any[], name: string) => {
                onRemove(index, lists, 'legalPersonIdFront')
              }
            "
          ></u-upload>
          <img
            class="formMerchant__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e78e4b0dd235a3dd37c.png"
            alt=""
          />
        </u-form-item>
        <u-form-item label="证件照背面" :required="true" prop="legalPersonIdBack" :label-width="210">
          <u-upload
            index="legalPersonIdBack"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
            @on-remove="
              (index: number, lists: any[], name: string) => {
                onRemove(index, lists, 'legalPersonIdBack')
              }
            "
          ></u-upload>
          <img
            class="formMerchant__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1eb4e4b0dd235a3dd37d.png"
            alt=""
          />
          <!-- <span>请上传身份证正反面照片，仅限jpg、png，大小不超过2M，仅限上传2张</span> -->
        </u-form-item>
        <span class="formMerchant__title">收款账户</span>
        <u-form-item label="收款人" :required="true" prop="payee" :label-width="160">
          <u-input v-model="formMerchant.payee" placeholder="请输入收款人姓名" maxlength="60" />
        </u-form-item>
        <u-form-item label="银行名" :required="true" prop="bankName" :label-width="160">
          <u-input v-model="formMerchant.bankName" placeholder="请输入银行名" />
        </u-form-item>
        <u-form-item label="账号" :required="true" prop="bankAcc" :label-width="160">
          <u-input v-model="formMerchant.bankAcc" placeholder="请输入银行卡号" />
        </u-form-item>
        <u-form-item class="formMerchant__lastitem" label="开户行" :required="true" prop="openAccountBank" :label-width="160">
          <u-input v-model="formMerchant.openAccountBank" placeholder="请输入开户行名称" />
        </u-form-item>
      </view>

      <view style="height: 110rpx"></view>
      <view class="submit">
        <u-button class="submit__button" type="error" @click="submit">提交</u-button>
      </view>
      <!-- 底部占位误删 解决iphonex 底部bar -->
      <view :style="{ width: '100vw', height: safeHeight + 'px' }"></view>
    </u-form>
  </view>
  <!-- #ifdef H5 -->
  <AmapChoose
    v-model:show="showChooseMap"
    :initLocation="[
      formMerchant.location?.coordinates?.[0] || [121.489551, 29.936806][0],
      formMerchant.location?.coordinates?.[1] || [121.489551, 29.936806][1],
    ]"
    @placeChoose="placeChoose"
  />
  <!-- #endif -->
  <Auth />
</template>

<style lang="scss" scoped>
:deep(.u-form-item--left__content--required) {
  left: 10rpx !important;
}

@include b(formMerchant) {
  position: relative;
  .address_area {
    display: flex;
    flex-direction: column;
    width: 100%;
    min-height: 70rpx;
    .address_name {
      font-weight: bold;
      line-height: 40rpx;
    }
    .address_detail_name {
      color: #999;
      font-size: 22rpx;
      line-height: 25rpx;
    }
  }
  @include e(title) {
    display: inline-block;
    height: 55rpx;
    line-height: 55rpx;
    padding-left: 15rpx;
    color: #000;
    font-weight: bold;
  }
  @include e(storeIntroduction) {
    display: flex;
    flex-direction: column;
    width: 100%;
    padding-left: 20rpx;
    @include m(title) {
      font-weight: 600;
    }
  }
  @include e(register) {
    display: flex;
  }
  @include e(img) {
    width: 200rpx;
    height: 150rpx;
    margin-left: 50rpx;
  }
  @include e(lastitem) {
    padding-bottom: 100rpx;
  }
}
@include b(u-form-item) {
  border: none;
  background-color: #fff;
  :deep(.u-form-item--left) {
    font-weight: 700;
  }
}
@include b(submit) {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  z-index: 999;
  text-align: center;
  justify-content: center;
  @include e(button) {
    width: 80%;
    border-radius: 40rpx;
  }
}
.u-form-item--right {
  width: 100%;
}
:deep(.u-form-item--right__content__slot) {
  width: 100%;
  display: flex;
}
</style>
