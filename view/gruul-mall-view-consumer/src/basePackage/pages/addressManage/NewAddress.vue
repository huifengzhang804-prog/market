<script lang="ts" setup>
import { onLoad, onReady } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import Debounce from '@/components/debounce/debounce.vue'
import Auth from '@/components/auth/auth.vue'
import { doEditAddress, doGetAddress, doNewAddress } from '@/apis/address'
import { useNavBack } from '@/hooks/useNavBack'
import { type Address, type Geometry, GeometryType } from '@/apis/address/type'
// #ifdef H5
import AmapChoose from '@/basePackage/pages/addressManage/components/AmapChoose.vue'
// #endif
import { locToAddress } from '@/apis/amap'
import { useLocationStore } from '@/store/modules/location'
import { ref } from 'vue'

type Checked = {
  longitude: number
  latitude: number
  address: string
  name: string
}
const submitForm = ref<
  Address<GeometryType.Point> & {
    fakeAddress?: string
  }
>({
  id: '',
  name: '',
  mobile: '',
  // 详细地址不包含省市区
  address: '',
  /**
   * 物理定位
   */
  location: {
    type: GeometryType.Point,
    // 坐标
    coordinates: [] as unknown as Geometry['coordinates'],
  },
  /**
   * 省市区 数组
   */
  area: [] as unknown as [string, string, string?],
  isDefault: false,
  fakeAddress: '', // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
})
const formRef = ref()
const isEdit = ref(false)
const formRule = {
  name: [
    {
      required: true,
      message: '请输入收货人姓名',
      trigger: ['change', 'blur'],
    },
  ],
  mobile: [
    {
      required: true,
      message: '请输入手机号',
      trigger: ['change', 'blur'],
    },
    {
      message: '请输入正确的手机号',
      trigger: ['change', 'blur'],
      validator: (rule: any, value: string, callback: any) => {
        // @ts-ignore
        return uni.$u.test.mobile(value)
      },
    },
  ],
  area: [
    {
      required: true,
      message: '请选择所在地区',
      trigger: ['change', 'blur'],
      validator: (rule: any, value: string[], callback: any) => {
        return Boolean(value.length)
      },
    },
  ],
  fakeAddress: [
    {
      required: true,
      message: '请填写详细地址',
    },
    {
      message: '详细地址不能包含~号',
      validator: (rule: any, value: string, callback: any) => {
        return !value.includes('~')
      },
    },
  ],
}

onReady(() => {
  formRef.value.setRules(formRule)
})
onLoad(async (e: any) => {
  uni.$emit('updateTitle')
  if (e.id) {
    isEdit.value = true
    const { code, success, data } = await doGetAddress(e.id)
    if (code === 200 && data && success) {
      submitForm.value = data
      submitForm.value.fakeAddress = data.address.split('~')[2] || ''
      submitForm.value.address = `${data.address.split('~')[0] || ''}~${data.address.split('~')[1] || ''}`
    }
  }
})

const newAddress = async () => {
  // 处理下假地址
  const copyForm = JSON.parse(JSON.stringify(submitForm.value))
  copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
  delete copyForm.fakeAddress
  const { code } = await doNewAddress(copyForm)
  if (code === 200) {
    uni.showToast({
      icon: 'none',
      title: '添加成功',
      duration: 1000,
      success: () => {
        useNavBack()
      },
    })
  }
}

const editAddress = async () => {
  // 处理下假地址
  const copyForm = JSON.parse(JSON.stringify(submitForm.value))
  console.log(copyForm)
  copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
  delete copyForm.fakeAddress
  const { code } = await doEditAddress(copyForm.id!, copyForm)
  if (code === 200) {
    uni.showToast({
      icon: 'none',
      title: '更新成功',
      duration: 1000,
      success: () => {
        useNavBack()
      },
    })
  }
}
const handleSubmit = () => {
  // #ifndef MP-WEIXIN
  formRef.value.validate((valid: any) => {
    if (valid) {
      if (isEdit.value) {
        editAddress()
      } else {
        newAddress()
      }
    }
  })
  // #endif
  // #ifdef MP-WEIXIN
  mpSubmit()
  // #endif
}

/**
 * 微信端表单校验加提示
 */
const mpSubmit = () => {
  for (const key in formRule) {
    // @ts-ignore
    if (submitForm.value[key] === '' || submitForm.value[key] === undefined) {
      return uni.showToast({
        icon: 'none',
        // @ts-ignore
        title: formRule[key][0].message,
        duration: 1000,
      })
      // @ts-ignore
    } else if (key === 'mobile' && !uni.$u.test.mobile(submitForm.value.mobile)) {
      return uni.showToast({
        icon: 'none',
        title: formRule[key][1].message,
        duration: 1000,
      })
    } else if (key === 'fakeAddress' && submitForm.value?.fakeAddress?.includes('~')) {
      return uni.showToast({
        icon: 'none',
        title: formRule[key][1].message,
        duration: 1000,
      })
    }
  }
  if (isEdit.value) {
    editAddress()
  } else {
    newAddress()
  }
}

const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [res.longitude, res.latitude],
  }
  submitForm.value.location = checkedLocation
  const { area, address } = await locToAddress(res, false)
  if (area) {
    submitForm.value.area = area
  }
  if (address) {
    submitForm.value.address = `${address}~${res.name}`
  }
}
const toChooseAddress = () => {
  // #ifndef H5
  uni.chooseLocation({
    latitude: submitForm.value.location?.coordinates?.[1] || useLocationStore()?.getterLocation?.coordinates?.[1],
    longitude: submitForm.value.location?.coordinates?.[0] || useLocationStore()?.getterLocation?.coordinates?.[0],
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
// #ifdef H5
const showChooseMap = ref(false)
const placeChoose = (e: Checked) => {
  handleChooseRes(e)
}
// #endif
</script>

<template>
  <q-nav :title="isEdit ? '编辑地址' : '新增地址'"></q-nav>
  <view class="form">
    <u-form ref="formRef" :error-type="['toast']" :model="submitForm" label-align="center" label-width="200rpx">
      <u-form-item style="padding: 0" />
      <u-form-item label="收货人" prop="name" required>
        <u-input v-model="submitForm.name" placeholder="请输入姓名" />
      </u-form-item>
      <u-form-item label="手机号" prop="mobile" required>
        <u-input v-model="submitForm.mobile" maxlength="11" placeholder="请输入手机号" />
      </u-form-item>
      <u-form-item label="定位地址" prop="area" required right-icon="arrow-right">
        <view class="address_area" @click="() => toChooseAddress()">
          <view v-if="submitForm.address" class="address_name">{{ submitForm.address.split('~')[1] || '' }}</view>
          <view v-else style="color: #c0c4cc">打开地图选择定位</view>
          <view v-if="submitForm.area" class="address_detail_name">{{ submitForm.area?.join?.('') }}{{ submitForm.address.split('~')[0] }} </view>
        </view>
      </u-form-item>
      <u-form-item label="详细地址" prop="fakeAddress" required style="padding-right: 30rpx">
        <u-input v-model="submitForm.fakeAddress" maxlength="20" placeholder="例如: [3单元203室]" />
      </u-form-item>
    </u-form>
  </view>
  <Debounce @bounceTap="handleSubmit">
    <view class="bottom"> 保存并返回 </view>
  </Debounce>
  <!-- #ifdef H5 -->
  <AmapChoose
    v-model:show="showChooseMap"
    :initLocation="[
      submitForm.location?.coordinates?.[0] || useLocationStore()?.getterLocation?.coordinates?.[0]!,
      submitForm.location?.coordinates?.[1] || useLocationStore()?.getterLocation?.coordinates?.[1]!,
    ]"
    @placeChoose="placeChoose"
  />
  <!-- #endif -->
  <Auth />
</template>
<style lang="scss" scoped>
:deep(.u-form-item--left__content__label) {
  width: 200rpx;
}

@include b(form) {
  background: #fff;
  @include e(col) {
    width: 100%;
    height: 12rpx;
    background: #f5f5f5;
  }
  @include e(area) {
    width: 500rpx;
    height: 70rpx;
  }
  :deep(.u-form-item--left__content--required) {
    left: 16rpx;
  }
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
}

@include b(bottom) {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 98rpx;
  background: #fa3534;
  color: #fff;
  text-align: center;
  line-height: 98rpx;
  font-size: 36rpx;
}
</style>
