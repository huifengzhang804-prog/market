<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app'
import { reactive, ref, onMounted, onUnmounted, computed } from 'vue'
import AddressSelect from '@/pluginPackage/order/confirmOrder/components/address-select.vue'
import Order from '@pluginPackage/integral/mall/components/confirmOrder/order.vue'
import STORAGE from '@/utils/storage'
import { Decimal } from 'decimal.js-light'
import { doGetDefaultAddress } from '@/apis/address'
import { doPostIntegralOrderCreate, doGetOrderCreateConditions } from '@pluginPackage/integral/api'
import { PAY_TYPE } from '@/apis/paymentDetail/model'
import type { DoPostIntegralOrderCreateParameter } from '@pluginPackage/integral/api'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'
import { useChosseAddress } from '@/hooks/useChooseAddress'
import type { TConfirmAddress } from '@/hooks/useChooseAddress'
import price from '../price.vue'
import useAddressDispatcher from '@/store/dispatcher/useAddressDispatcher'
import { GeometryType, type Address } from '@/apis/address/type'

// 确认订单页面地址变更
const { confirmOredrAddress } = useChosseAddress()
const { getConfirmAddress, offConfirmAddress } = confirmOredrAddress()
const { divTenThousand } = useConvert()
const $useAddressDispatcher = useAddressDispatcher()

const submitForm = reactive<DoPostIntegralOrderCreateParameter>({
  receiver: {
    name: '',
    mobile: '',
    area: [] as unknown as [string, string, string?],
    address: '',
    location: {
      type: GeometryType.Point,
      coordinates: [0, 0],
    },
  },
  source: 'PRODUCT',
  buyerRemark: '',
  productId: '',
  num: 1,
})
const goodInfo = ref<IntegralGoodsInfoType>({
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
  productType: '',
})
const isLoading = ref(false)
const submitLoading = ref(false)

onLoad(() => {
  initOrderInfo()
  initDefaultAddress()
})
onMounted(() => {
  $useAddressDispatcher.addCartSubscriber(initDefaultAddress)
  // 接受addressManage选中地址信息
  getConfirmAddress((e: TConfirmAddress) => {
    submitForm.receiver = e
  })
})
onUnmounted(() => {
  $useAddressDispatcher.removeCartSubscriber(initDefaultAddress)
  offConfirmAddress()
})

const initOrderInfo = () => {
  const info = STORAGE.get('integralOrderInfo') as IntegralGoodsInfoType | null
  if (info) {
    submitForm.productId = info.id
    goodInfo.value = info
  } else {
    uni.showToast({ title: '商品信息获取失败', icon: 'none' })
    uni.navigateBack()
  }
}
/**
 * 函数尾调用
 * @param {*} orderNo
 */
const checkOrderCreation = (orderNo: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    loopCheckOrderCreation(1, orderNo, resolve, reject)
  })
}
const loopCheckOrderCreation = (count: number, orderNo: string, resolve = () => {}, reject = () => {}) => {
  if (count >= 20) {
    isLoading.value = false
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
    isLoading.value = false
    resolve()
  })
}

const handleSubmit = async () => {
  if (goodInfo.value.productType !== 'VIRTUAL_PRODUCT' && !submitForm.receiver?.area?.length) {
    return uni.showToast({ title: '请先选择地址', icon: 'none' })
  }
  submitLoading.value = true
  const { data, code, msg } = await doPostIntegralOrderCreate(submitForm).catch((e) => {
    submitLoading.value = false
    return e
  })
  switch (code) {
    case 200:
      isLoading.value = true
      checkOrderCreation(data).then(() => {
        if (new Decimal(priceTotal.value).lessThanOrEqualTo(0)) {
          return uni.showToast({
            title: '兑换成功',
            icon: 'none',
            duration: 2000,
            complete: () => {
              setTimeout(() => {
                uni.redirectTo({
                  url: `/pluginPackage/integral/mall/Index?goodId=${goodInfo.value.id}&shopId=0`,
                })
                isLoading.value = false
              }, 1500)
            },
          })
        }
        const { id, integralPrice } = goodInfo.value
        // 支付运费
        const extra = encodeURIComponent(JSON.stringify({ ruleId: id }))
        const infoQuery = `?no=${data}&orderType=${PAY_TYPE.INTEGRAL}&extra=${extra}`
        const priceQuery = `&integral=${integralPrice}&price=${priceTotal.value}`
        uni.redirectTo({
          url: `/basePackage/pages/pay/Index${infoQuery + priceQuery}`,
        })
        isLoading.value = false
      })
      break
    default:
      uni.showToast({
        icon: 'none',
        title: `${msg ? msg : '订单提交失败'}`,
      })
      isLoading.value = false
      break
  }
  submitLoading.value = false
}

async function initDefaultAddress() {
  if (goodInfo.value.productType === 'VIRTUAL_PRODUCT') return
  let receiver = { name: '', mobile: '', area: [] as unknown as [string, string, string?], address: '' } as Address
  try {
    const { data, code } = await doGetDefaultAddress()
    if (code === 200 && data) {
      receiver = data
    } else {
      uni.showToast({ icon: 'none', title: '请添加收货地址' })
      throw new Error('请添加收货地址')
    }
  } catch (err) {
    return Promise.reject('请添加收货地址')
  } finally {
    submitForm.receiver = receiver
  }
}

const priceTotal = computed(() =>
  new Decimal(goodInfo.value?.freightPrice || 0)
    .mul(10000)
    .add(goodInfo.value?.salePrice || 0)
    .toString(),
)
</script>
<template>
  <view class="confirm">
    <address-select :order="goodInfo" :info="submitForm.receiver" />
    <view class="confirm__list">
      <order :info="goodInfo" />
    </view>
    <!-- <view class="settlement">
        <u-cell-item title="买家留言：" :title-style="{ fontWeight: 700, color: '#333333' }" :border-bottom="false" :arrow="false" />
        <u-input
            v-model="submitForm.buyerRemark"
            type="textarea"
            :border="false"
            placeholder="针对本次交易的说明（45字内）"
            maxlength="45"
            style="padding: 0 40rpx"
        />
    </view> -->
    <view class="settlement settlementPrice">
      <view class="settlementPrice__title"> 价格明细</view>
      <u-cell-group :border="false">
        <u-cell-item
          :arrow="false"
          :border-bottom="false"
          :title-style="{ color: '#333333' }"
          :value="(goodInfo.integralPrice || 0) + '积分+￥' + divTenThousand(goodInfo.salePrice)"
          title="商品总价"
        >
        </u-cell-item>
        <u-cell-item
          :arrow="false"
          :border-bottom="false"
          :title-style="{ color: '#333333' }"
          :value="'￥' + goodInfo.freightPrice.toFixed(2)"
          title="运费"
        />
      </u-cell-group>
      <view class="settlementPrice__total">
        <text style="font-size: 30rpx; font-weight: 700"> 应付款</text>
        <price :integral="goodInfo.integralPrice" :sale-price="priceTotal" />
      </view>
    </view>
    <!-- bar -->
    <view class="confirm__bar">
      <view class="confirm__bar-price"></view>
      <view class="confirm__bar--btn">
        <u-button
          :custom-style="{ padding: 0, margin: 0, width: '100%', height: '100%', borderRadius: '0' }"
          :disabled="goodInfo.productType !== 'VIRTUAL_PRODUCT' && !submitForm.receiver.area.length"
          :loading="submitLoading"
          throttle-time="1000"
          type="error"
          @click="handleSubmit"
        >
          提交订单
        </u-button>
      </view>
    </view>
  </view>
  <u-loading :show="isLoading" class="loading" mode="flower" size="100" />
  <Auth />
</template>

<style lang="scss" scoped>
@import '@/assets/css/animate.scss';

@include b(confirm) {
  padding: 20rpx 0 88rpx 0;
  margin: 0 20rpx;
  box-sizing: border-box;
  @include e(list) {
    margin-top: 14rpx;
  }
  @include e(bar) {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 99;
    height: 98rpx;
    background: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx;
    @include m(price) {
      font-size: 36rpx;
    }
    @include m(btn) {
      width: 198rpx;
      height: 70rpx;
      background: #f83f30;
      line-height: 88rpx;
      text-align: center;
      color: #fff;
      font-size: 28rpx;
      border-radius: 6rpx;
    }
  }
  @include e(bar-price) {
    @include flex();
    font-size: 28rpx;
    margin-left: 20rpx;
    @include m(total) {
      margin-left: 20rpx;
      font-weight: bold;
      font-size: 36rpx;
      color: #f83f30;
    }
    @include m(price) {
      font-weight: bold;
      font-size: 34rpx;
      color: #f83f30;
    }
  }
}

@include b(loading) {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@include b(settlement) {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 22rpx;
  overflow: hidden;
}

@include b(settlementPrice) {
  padding: 20rpx 30rpx;
  .u-cell {
    padding: 0rpx 20rpx;
  }
  @include e(title) {
    font-size: 26rpx;
    font-weight: 700;
    color: '#333333';
    margin-bottom: 15rpx;
  }

  @include e(total) {
    margin-top: 15rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>
