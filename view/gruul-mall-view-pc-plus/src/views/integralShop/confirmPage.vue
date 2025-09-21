<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { SuccessFilled, ArrowDown } from '@element-plus/icons-vue'
import { Decimal } from 'decimal.js'
import storage from '@/libs/storage'
import useConvert from '@/composables/useConvert'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { MOBILE } from '@/utils/RegexPool'
import { doGetAddressList, doSetAddressDefault, doDelAddress, doEditAddress, doNewAddress, doGetAddressByLocation } from '@/apis/address'
import { doPostIntegralOrderCreate, doGetOrderCreateConditions } from '@/apis/integral'
import { Checked, Geometry, GeometryType } from '@/apis/address/types'
import AmapChooseDialog from '@/views/personalcenter/set/address/components/AmapChooseDialog.vue'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'

const formRef = ref()
const showChooseMap = ref(false)

interface AddressItemType {
  id: string
  address: string
  area: [string, string, string?]
  isDefault: boolean
  mobile: string
  name: string
  fakeAddress: string // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
  showSet: boolean
  location: Geometry
}

const $router = useRouter()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const topComponents = ref()
topComponents.value = getterPropertiesList.value
const { divTenThousand } = useConvert()
const addressList = ref<AddressItemType[]>([])
const submitForm = reactive({
  receiver: {
    id: '',
    name: '',
    mobile: '',
    area: [] as unknown as [string, string, string?],
    address: '',
  },
  source: 'PRODUCT',
  buyerRemark: '',
  productId: '',
  num: 1,
})
const goodInfo = ref({
  albumPics: [],
  createTime: '',
  detail: '',
  freightPrice: 0,
  id: '',
  integralPrice: '',
  integralProductAttributes: [],
  name: '',
  pic: '',
  price: '',
  salePrice: '',
  status: 'SELL_ON',
  stock: 0,
  virtualSalesVolume: 0,
})
const addressFlag = ref(false)
const showAllAddress = ref(false)
const addressinfo = ref<AddressItemType>({
  id: '',
  address: '',
  area: [] as unknown as [string, string, string?],
  isDefault: false,
  mobile: '',
  name: '',
  showSet: false,
  fakeAddress: '',
  location: { type: GeometryType.Point, coordinates: [] as unknown as [number, number] },
})
const rules = ref({
  name: [
    { required: true, message: '请输入姓名', trigger: 'change' },
    { min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'change' },
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'change' },
    { pattern: MOBILE, message: '手机号格式不正确', trigger: 'change' },
  ],
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
const submitLoading = ref(false)
const priceTotal = computed(() =>
  new Decimal(goodInfo.value?.freightPrice || 0)
    .mul(10000)
    .add(goodInfo.value?.salePrice || 0)
    .toString(),
)

onMounted(() => {
  initOrderInfo()
  getAddressList()
})
const productType = ref<'VIRTUAL_PRODUCT' | 'REAL_PRODUCT'>()

const initOrderInfo = () => {
  const info = new storage().getItem(`integralOrderInfo`)
  productType.value = info.productType
  if (info) {
    submitForm.productId = info.id
    goodInfo.value = info
  } else {
    ElMessage.error('商品信息获取失败')
    $router.back()
  }
}
// 地址
const getAddressList = async () => {
  if (productType.value === 'VIRTUAL_PRODUCT') return
  const { data, code, success, msg } = await doGetAddressList()
  if (code === 200 && success) {
    addressList.value = data.records
    // 取默认地址
    const defaultAddressIdx = data.records.findIndex((item: AddressItemType) => item.isDefault) as number
    if (defaultAddressIdx !== -1) {
      const defaultAddress = data.records[defaultAddressIdx]
      submitForm.receiver = defaultAddress
      // 取到默认地址之后获取运费
      // initFreightPrice(commodityList.value)
      if (defaultAddressIdx > 2) {
        // 一行展示三个 超出此范围往前调整数据
        data.records.splice(defaultAddressIdx, 1)
        data.records.unshift(defaultAddress)
      }
    }
  } else {
    ElMessage.error(msg)
  }
}
/**
 * 删除地址
 */
const handleDelAddress = async (id: string) => {
  const isValidate = await ElMessageBox.confirm('确定删除该地址吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { code } = await doDelAddress(id)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  getAddressList()
}
/**
 * 重置表单
 */
const reset = async () => {
  addressinfo.value = {
    id: '',
    address: '',
    area: [] as unknown as [string, string, string?],
    isDefault: false,
    mobile: '',
    name: '',
    fakeAddress: '',
    location: {
      type: GeometryType.Point,
      coordinates: [] as unknown as [number, number],
    },
    showSet: false,
  }
  formRef.value.clearValidate()
}

/**
 * 新增地址
 */
const handleAddAddress = async () => {
  addressFlag.value = true
  await nextTick()
  formRef.value.clearValidate()
}

/**
 * 修改地址
 */
const handlePutAddress = async (address: AddressItemType) => {
  addressFlag.value = true
  await nextTick()
  addressinfo.value = JSON.parse(JSON.stringify(address))
  addressinfo.value.fakeAddress = address.address.split('~')[2] || ''
  addressinfo.value.address = `${address.address.split('~')[0] || ''}~${address.address.split('~')[1] || ''}`
  formRef.value.clearValidate()
}
/**
 * 设置默认地址
 */
const handleChangeCheck = async (id: string) => {
  const { code, success } = await doSetAddressDefault(id, true)
  if (code === 200 && success) {
    ElMessage.success('设置成功')
    getAddressList()
  }
}

const newAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      // 处理下假地址
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doNewAddress(copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('添加成功')
          getAddressList()
          addressFlag.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '添加失败')
        }
      })
    }
  })
}
const editAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doEditAddress(addressinfo.value.id, copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('修改成功')
          getAddressList()
          addressFlag.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '修改失败')
        }
      })
    }
  })
}
const handleChooseAddress = (address: AddressItemType) => {
  submitForm.receiver = address
  // 取到地址之后获取当前运费
  // initFreightPrice(commodityList.value)
}

const handleSubmit = async () => {
  if (productType.value === 'VIRTUAL_PRODUCT') {
    submitForm.receiver = {
      id: '',
      name: '',
      mobile: '',
      area: [] as unknown as [string, string, string?],
      address: '',
    }
  } else if (!submitForm.receiver?.area?.length) {
    return ElMessage.error('请先选择地址')
  }
  submitLoading.value = true
  const { data, code, msg } = await doPostIntegralOrderCreate({
    ...submitForm,
    receiver: {
      ...submitForm.receiver,
      area: submitForm.receiver.area as string[],
    },
  }).catch((e) => {
    submitLoading.value = false
    return e
  })
  switch (code) {
    case 200:
      // isLoading.value = true
      checkOrderCreation(data).then(() => {
        if (new Decimal(priceTotal.value).lessThanOrEqualTo(0)) {
          ElMessage.success('兑换成功')
          return setTimeout(() => {
            $router.replace({
              path: '/personalcenter/assets/integral',
              query: {},
            })
            // isLoading.value = false
          }, 500)
        }
        const { id, integralPrice } = goodInfo.value
        // 支付运费
        // const extra = encodeURIComponent(JSON.stringify({ ruleId: id }))
        // const infoQuery = `?no=${data}&orderType=${PAY_TYPE.INTEGRAL}&extra=${extra}`
        // const priceQuery = `&integral=${integralPrice}&price=${divTenThousand(priceTotal.value)}`
        $router.replace({
          path: '/pay',
          query: { no: data, integral: integralPrice, price: divTenThousand(priceTotal.value).toString(), ruleId: id },
        })
        // uni.redirectTo({
        //     url: `/basePackage/pages/pay/Index${infoQuery + priceQuery}`,
        // })
        // isLoading.value = false
      })
      break
    default:
      ElMessage.error(msg || '订单提交失败')
      // isLoading.value = false
      break
  }
  submitLoading.value = false
}
/**
 * 函数尾调用
 * @param {*} orderNo
 * @returns {*}
 */
const checkOrderCreation = (orderNo: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    loopCheckOrderCreation(1, orderNo, resolve, reject)
  })
}
const loopCheckOrderCreation = (count: number, orderNo: string, resolve = () => {}, reject = () => {}) => {
  if (count >= 20) {
    // isLoading.value = false
    reject()
    return
  }
  doGetOrderCreateConditions(orderNo).then(({ code, data }) => {
    if (code !== 200 || !data) {
      setTimeout(() => {
        loopCheckOrderCreation(count + 1, orderNo, resolve, reject)
      }, 550)
      return
    }
    // isLoading.value = false
    resolve()
  })
}
const toChooseAddress = () => {
  showChooseMap.value = true
}
const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [Number(res.longitude), Number(res.latitude)],
  }
  addressinfo.value.location = checkedLocation

  const { area, address } = await doGetAddressByLocation(checkedLocation.coordinates, false)
  if (area) {
    addressinfo.value.area = area
  }
  if (address) {
    addressinfo.value.address = `${address}~${res.name}`
  }
}
const placeChoosed = (place: Checked) => {
  handleChooseRes(place)
}
</script>

<template>
  <div bg-white>
    <div c-w-1200 c-h-110 flex ma justify-between items-center>
      <router-link to="/home">
        <div
          class="log"
          :style="{
            backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
          }"
        />
      </router-link>
      <el-steps c-w-500 :active="-1" align-center>
        <el-step title="积分商城" :icon="SuccessFilled" />
        <el-step title="填写核对订单信息" :icon="SuccessFilled" />
        <el-step title="成功提交订单" />
      </el-steps>
    </div>
    <div v-if="productType !== 'VIRTUAL_PRODUCT'" c-w-1190 c-h-30 c-lh-30 text-left ma fw-700 e-c-3 b-b c-bc-f3f8f4>选择收货地址</div>
    <div v-if="productType !== 'VIRTUAL_PRODUCT'" c-w-1190 flex ma c-mt-10 flex-wrap h-auto :class="showAllAddress ? '' : 'overflow-hidden c-h-140'">
      <div
        v-for="item in addressList"
        :key="item.id"
        c-fs-12
        e-c-3
        c-pl-20
        c-pt-9
        c-pr-10
        c-w-378
        c-h-130
        c-mr-18
        fw-700
        c-mb-15
        class="!c-bg-FFFBEF"
        :class="item.id === submitForm.receiver.id ? 'b-2 c-bc-e31436' : 'b-2 c-bc-fff'"
        @mouseleave="item.showSet = false"
        @mouseenter="item.showSet = true"
        @click="handleChooseAddress(item)"
      >
        <div flex items-center justify-between c-h-30 b-b c-bc-f3f8f4>
          <div c-fs-14>
            {{ item.name }}
          </div>
          <div v-if="item.isDefault" e-c-6 fw-200>默认地址</div>
          <div v-if="!item.isDefault && item.showSet" cursor-pointer c-c-e31436 fw-200 @click.stop="handleChangeCheck(item.id)">默认地址</div>
        </div>
        <div text-left c-mt-5 c-lh-23>
          <div>{{ item.mobile }}</div>
          <div c-mt-8 c-h-23 style="overflow: hidden">{{ item.area?.join('') }} {{ item.address }}</div>
        </div>
        <div v-show="item.showSet" e-c-6 text-left c-mt-10>
          <span class="c-mr-41 hover-c-#e31436 hover-b-b cursor-pointer" @click.stop="handlePutAddress(item)">修改</span>
          <span class="c-mr-41 hover-c-#e31436 hover-b-b cursor-pointer" @click.stop="handleDelAddress(item.id)">删除</span>
        </div>
      </div>
    </div>
    <div c-w-1190 ma>
      <div v-if="!showAllAddress && addressList.length > 3" c-c-d12147 text-left c-fs-14 underline cursor-pointer @click="showAllAddress = true">
        显示全部地址
        <el-icon>
          <ArrowDown />
        </el-icon>
      </div>
      <div v-if="showAllAddress && addressList.length > 3" c-c-d12147 text-left c-fs-14 underline cursor-pointer @click="showAllAddress = false">
        收起
      </div>

      <div
        v-if="productType !== 'VIRTUAL_PRODUCT'"
        b-1
        c-bc-999999
        c-w-112
        c-h-28
        c-lh-28
        c-fs-14
        c-c-101010
        c-mt-7
        cursor-pointer
        @click="handleAddAddress"
      >
        +新增收货地址
      </div>

      <div text-left fw-700 e-c-3 c-mt-27>确认商品信息</div>
      <div b-1 c-bc-ebeae6 c-mt-13 c-fs-12 c-mb-10>
        <div flex e-c-9 c-bg-f7f7f7 b-b c-bc-ebeae6 c-h-40 c-lh-40>
          <div e-c-3 c-w-150 c-ml-17 fw-700>商品信息</div>
          <div c-ml-424>单价</div>
          <div c-ml-254>数量</div>
          <div c-ml-238>小计</div>
        </div>
        <div c-h-86 b-1 c-bc-ebeae6 flex items-center>
          <img :src="goodInfo.pic" c-h-60 c-w-60 c-ml-17 />
          <div text-left c-ml-24>
            <div e-c-3 c-mb-8 c-w-285 truncate>
              {{ goodInfo.name }}
            </div>
            <!-- <div e-c-9 c-w-285 truncate>{{ sku.specs?.join(';') }}</div> -->
          </div>
          <div c-w-200 c-ml-110>
            <div e-c-3 ma>
              {{ goodInfo.integralPrice }}积分<span v-if="goodInfo.salePrice !== '0'"> + ￥{{ useConversionPrice(goodInfo.salePrice) }} </span>
            </div>
            <div v-if="false" c-w-60 c-h-21 c-lh-21 c-c-e31436 b-1 c-bc-E31436 c-bg-fdecef ma>秒杀价</div>
          </div>
          <div e-c-3 c-w-150 c-ml-115 ma>1</div>
          <div c-w-200 fw-700 e-c-3 c-pr-10 style="text-align: right">
            {{ goodInfo.integralPrice }}积分<span v-if="goodInfo.salePrice !== '0'"> + ￥{{ useConversionPrice(goodInfo.salePrice) }} </span>
          </div>
        </div>
        <!-- <div v-if="false" b-1 c-bc-ebeae6 flex justify-end items-center e-c-3 c-h-44 c-pr-23>运费：￥10.00</div> -->
      </div>
      <div text-left fw-700 e-c-3 c-mt-27>结算信息</div>
      <div b-1 c-bc-ebeae6 c-fs-12 c-pl-13 c-pt-13 c-pr-25 c-pb-23>
        <!-- 折扣展示 -->
        <div c-mt-30>
          <div flex justify-end c-mb-7>
            <div e-c-9><span c-c-e31436>1</span>件商品总计：</div>
            <div c-w-200 flex justify-end fw-700 e-c-3>
              {{ goodInfo.integralPrice }}积分<span v-if="goodInfo.salePrice !== '0'"> + ￥{{ useConversionPrice(goodInfo.salePrice) }} </span>
            </div>
          </div>
          <div flex justify-end c-mb-7>
            <div e-c-9>运费：</div>
            <div c-w-200 flex justify-end fw-700 e-c-3>￥{{ goodInfo.freightPrice }}</div>
          </div>
        </div>
      </div>
      <div flex justify-end items-center c-fs-14 e-c-9 c-mt-38>
        应付金额：<span c-c-e31436 c-fs-22 fw-900
          >{{ goodInfo.integralPrice }}积分<span v-if="Number(goodInfo.salePrice) + Number(goodInfo.freightPrice) !== 0">
            + ￥{{ useConversionPrice(goodInfo.salePrice).add(goodInfo.freightPrice) }}
          </span></span
        >
      </div>
      <div flex justify-end items-center c-mt-20>
        <el-button
          :loading="submitLoading"
          type="danger"
          c-w-120
          c-h-40
          c-lh-40
          c-bg-e31436
          e-c-f
          c-fs-18
          fw-700
          cursor-pointer
          @click="handleSubmit"
        >
          提交订单
        </el-button>
      </div>
    </div>
  </div>
  <el-dialog v-model="addressFlag" :title="addressinfo.id ? '编辑收货地址' : '新增收货地址'" width="500" @close="reset">
    <el-form ref="formRef" :model="addressinfo" label-width="80px" :rules="rules">
      <el-form-item label="收货人" prop="name">
        <el-input v-model="addressinfo.name" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="addressinfo.mobile" placeholder="请输入正确的11位手机号码" :maxlength="11" />
      </el-form-item>
      <el-form-item label="定位地址" prop="area" class="address_form_item">
        <div class="fcenter cup" @click="toChooseAddress">
          <div class="address_area">
            <div v-if="addressinfo.address" class="address_name">{{ addressinfo.address.split('~')[1] || '' }}</div>
            <div v-else style="color: #f54319">打开地图选择定位</div>
            <div v-if="addressinfo.area" class="address_detail_name">{{ addressinfo.area?.join?.('') }}{{ addressinfo.address.split('~')[0] }}</div>
          </div>
          <el-icon style="color: #f54319" class="mla"><ArrowRight /></el-icon>
        </div>
      </el-form-item>
      <el-form-item label="详细地址" prop="fakeAddress">
        <el-input v-model="addressinfo.fakeAddress" :rows="5" type="textarea" placeholder="例如: [3单元203室]" :maxlength="20" />
      </el-form-item>
      <el-form-item label="" prop="isDefault">
        <el-checkbox v-model="addressinfo.isDefault" label="设为默认收货地址" size="large" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button type="primary" @click="() => (addressinfo.id ? editAddress() : newAddress())">保存并使用</el-button>
      </div>
    </template>
  </el-dialog>
  <AmapChooseDialog v-model="showChooseMap" :initLocation="addressinfo.location.coordinates" @placeChoose="placeChoosed"></AmapChooseDialog>
</template>
<style scoped lang="scss">
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
.address_form_item {
  .fcenter {
    width: 100%;
    .address_area {
      padding: 1px 11px;
      display: flex;
      flex-direction: column;
      width: calc(100% - 80px);
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
:deep(.el-icon svg) {
  color: #e31436;
}

:deep(.el-step__title.is-wait) {
  color: #333333;
  font-size: 14px;
}

:deep(.el-step.is-center .el-step__head) {
  height: 31.5px;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 1px;
}
</style>
