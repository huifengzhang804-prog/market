<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { Ref } from 'vue'
import { onReady } from '@dcloudio/uni-app'
import { getheader } from '@/libs/request/returnHeader'
//引入表单验证规则
import { formRule } from './addressRule'
//引入add添加供应商方法
import { doAddShops } from '@/apis/shops'
import Auth from '@/components/auth/auth.vue'
import { useLocationStore } from '@/store/modules/location'
import type { Checked, Coordinate, Geometry } from '@/apis/address/type'
import { GeometryType } from '@/apis/address/type'
// #ifdef H5
import AmapChoose from '@/basePackage/pages/addressManage/components/AmapChoose.vue'
// #endif
import { getLocation, locToAddress } from '@/apis/amap'
import type { Result } from '@/utils/types'

//form元素节点
const formData = ref()
//表单验证类型
const errorType = ref(['message'])
//表单数据
const formFournisseurs = reactive({
  companyName: '',
  name: '',
  logo: '',
  contractNumber: '',
  address: '',
  briefing: '',
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
  location: {
    type: 'Point',
    coordinates: [] as unknown as Coordinate,
  } as Geometry,
  shopMode: 'SUPPLIER',
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
 * 表单提交
 */
const submit = async () => {
  //表单验证
  formData.value.validate(async (valid: boolean) => {
    if (valid) {
      // 处理下假地址
      const copyForm = JSON.parse(JSON.stringify(formFournisseurs))
      copyForm.bankAccount.payee = copyForm.payee
      copyForm.bankAccount.bankName = copyForm.bankName
      copyForm.bankAccount.bankAccount = copyForm.bankAcc
      copyForm.bankAccount.openAccountBank = copyForm.openAccountBank
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      delete copyForm.fakeAddress
      const { code, msg } = await doAddShops(copyForm)
      if (code !== 200) {
        if (code === 100003) {
          return uni.showToast({
            title: '已存在同名供应商名称,请修改后重新提交',
            icon: 'none',
            duration: 2000,
          })
        }
        return uni.showToast({
          title: msg ?? '添加失败',
          icon: 'none',
          duration: 2000,
        })
      }
      uni.showToast({
        title: '申请供应商成功',
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
const uploadSuccess = (data: Result, index: number, lists: string[], name: string) => {
  // console.log('上传成功', data, name)
  if (data.code === 200) {
    if (name === 'logoUpload') {
      formFournisseurs.logo = data.data
    } else if (name === 'businessLicenseUpload') {
      formFournisseurs.license = formFournisseurs.registerInfo.license = data.data
    } else if (name === 'personPhotoAfter') {
      formFournisseurs.legalPersonIdFront = formFournisseurs.registerInfo.legalPersonIdFront = data.data
    } else if (name === 'personPhotoBefore') {
      formFournisseurs.legalPersonIdBack = formFournisseurs.registerInfo.legalPersonIdBack = data.data
    }
    console.log(formFournisseurs)
  } else {
    console.log('上传失败')
  }
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
  formFournisseurs.location.coordinates = [res.longitude, res.latitude]
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
  formFournisseurs.location = checkedLocation
  const { area, address } = await locToAddress(res, false)
  if (area) {
    formFournisseurs.area = area
  }
  if (address) {
    formFournisseurs.address = `${address}~${res.name}`
  }
}
const toChooseAddress = () => {
  // #ifndef H5
  uni.chooseLocation({
    latitude: formFournisseurs.location?.coordinates?.[1] || [121.489551, 29.936806][1],
    longitude: formFournisseurs.location?.coordinates?.[0] || [121.489551, 29.936806][0],
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
  <view :style="{ height: `${50 + safeHeight}px` }">
    <u-form ref="formData" class="formFournisseurs" :label-width="190" label-align="center" :model="formFournisseurs" :error-type="errorType">
      <view style="padding: 0 20rpx">
        <span class="formFournisseurs__title">基本信息</span>
        <u-form-item label="商户名称" :required="true" prop="companyName">
          <u-input v-model="formFournisseurs.companyName" :maxlength="20" placeholder="请输入商户名称" />
        </u-form-item>
        <u-form-item label="供应商名称" :required="true" prop="name" :label-width="210">
          <u-input v-model="formFournisseurs.name" :maxlength="20" placeholder="请输入供应商名称" />
        </u-form-item>
        <u-form-item label="供应商logo" :required="true" prop="storeLogo" :label-width="215">
          <u-upload
            index="logoUpload"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
          ></u-upload>
        </u-form-item>
        <u-form-item label="联系方式" :required="true" prop="contractNumber">
          <u-input v-model="formFournisseurs.contractNumber" :maxlength="11" placeholder="请输入您的联系方式" />
        </u-form-item>
        <!-- <u-form-item label="选择地址" :required="true" prop="storeAddress">
          <FournisseursMap :latitude="latitude" :longitude="longitude" @map-change="getMapLocation" />
        </u-form-item> -->
        <u-form-item label="定位地址" :required="true" prop="area" right-icon="arrow-right">
          <view class="address_area" @click="() => toChooseAddress()">
            <view v-if="formFournisseurs.address" class="address_name">{{ formFournisseurs.address.split('~')[1] || '' }}</view>
            <view v-else style="color: #c0c4cc">打开地图选择定位</view>
            <view v-if="formFournisseurs.area" class="address_detail_name"
              >{{ formFournisseurs.area?.join?.('') }}{{ formFournisseurs.address.split('~')[0] }}
            </view>
          </view>
        </u-form-item>
        <u-form-item :required="true" prop="fakeAddress" label="详细地址">
          <u-input v-model="formFournisseurs.fakeAddress" placeholder="例如: [3单元203室]" height="150" auto-height="false" maxlength="20" />
        </u-form-item>
        <u-form-item :required="true" prop="briefing">
          <view class="formFournisseurs__storeIntroduction">
            <span class="formFournisseurs__storeIntroduction--title">供应商介绍</span>
            <u-input
              v-model="formFournisseurs.briefing"
              placeholder="请输入供应商介绍"
              type="textarea"
              height="150"
              auto-height="false"
              width="500"
            />
          </view>
        </u-form-item>
        <span class="formFournisseurs__title">信息登记</span>
        <u-form-item class="formFournisseurs__register" label="营业执照" :required="true" :label-width="200" prop="license">
          <u-upload
            index="businessLicenseUpload"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
          ></u-upload>
          <img
            class="formFournisseurs__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e49e4b0dd235a3dd37b.png"
            alt=""
          />
        </u-form-item>
        <u-form-item label="证件照正面" :required="true" :label-width="210" prop="legalPersonIdFront">
          <u-upload
            index="personPhotoBefore"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
          ></u-upload>
          <img
            class="formFournisseurs__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e78e4b0dd235a3dd37c.png"
            alt=""
          />
        </u-form-item>
        <u-form-item label="证件照背面" :required="true" :label-width="210" prop="legalPersonIdBack">
          <u-upload
            index="personPhotoAfter"
            :action="action"
            :header="getheader()"
            width="150rpx"
            height="150rpx"
            :max-size="5 * 1024 * 1024"
            max-count="1"
            @on-success="uploadSuccess"
          ></u-upload>
          <img
            class="formFournisseurs__img"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1eb4e4b0dd235a3dd37d.png"
            alt=""
          />
          <!-- <span>请上传身份证正反面照片，仅限jpg、png，大小不超过2M，仅限上传2张</span> -->
        </u-form-item>
        <span class="formFournisseurs__title">收款账户</span>
        <u-form-item label="收款人" :required="true" :label-width="160" prop="payee">
          <u-input v-model="formFournisseurs.payee" placeholder="请输入收款人姓名" maxlength="60" />
        </u-form-item>
        <u-form-item label="银行名" :required="true" :label-width="160" prop="bankName">
          <u-input v-model="formFournisseurs.bankName" placeholder="请输入银行名" />
        </u-form-item>
        <u-form-item label="账号" :required="true" :label-width="160" prop="bankAcc">
          <u-input v-model="formFournisseurs.bankAcc" placeholder="请输入银行卡号" />
        </u-form-item>
        <u-form-item class="formFournisseurs__lastitem" label="开户行" :required="true" :label-width="160" prop="openAccountBank">
          <u-input v-model="formFournisseurs.openAccountBank" placeholder="请输入开户行名称" />
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
      formFournisseurs.location?.coordinates?.[0] || [121.489551, 29.936806][0],
      formFournisseurs.location?.coordinates?.[1] || [121.489551, 29.936806][1],
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
@include b(formFournisseurs) {
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
