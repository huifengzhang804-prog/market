<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onReady } from '@dcloudio/uni-app'
import storage from '@/utils/storage'
import LogisticsForm from './components/logistics-form.vue'
import LogisticsGoods from './components/logistics-goods.vue'
import { doGetLogisticsTrajectoryByWaybillNo, doGetFirstDeliveryPage } from '@/apis/order'
import { REGEX_MOBILE } from '@/libs/validate'
import type { ApiLogistics01 } from '@/pluginPackage/order/orderDetail/types'
import type { ApiAfsOrder } from '@pluginPackage/order/detailsRefund/types'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand } = useConvert()
const navTitle = ref('查看物流')
const afsNo = ref('')
const orderInfo = ref<ApiAfsOrder>()
const storageOrders = ref([
  {
    afsNo: '',
    afsStatus: 'RETURNED_REFUND_CONFIRM',
    dealPrice: '',
    freightPrice: '',
    freightTemplateId: '',
    id: '',
    image: '',
    num: 0,
    packageId: '',
    packageStatus: 'WAITING_FOR_RECEIVE',
    productId: '',
    productName: '',
    salePrice: '',
    shopId: '',
    skuId: '',
    specs: [''],
    status: 'OK',
    weight: 1,
    orderId: '',
  },
])
const form = ref({ recipientsPhone: '' })
const totalNum = ref(0)

const showModal = ref(false)
const uFormRef = ref()
const rules = {
  recipientsPhone: [
    {
      required: true,
      message: '请输入手机号',
      trigger: ['change', 'blur'],
    },
    { pattern: REGEX_MOBILE, message: `手机号不正确`, trigger: ['change', 'blur'] },
  ],
}
const apiLogistics = ref<ApiLogistics01>({
  createTime: '',
  deleted: false,
  expressCompanyCode: '',
  expressCompanyName: '',
  expressNo: '',
  id: '',
  orderNo: '',
  receiverAddress: '',
  receiverMobile: '',
  receiverName: '',
  remark: '',
  shopId: '',
  status: 'BUYER_COMMENTED_COMPLETED',
  type: 'EXPRESS',
  updateTime: '',
  version: 0,
  success: true,
})

// 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
onReady(() => {
  if (uFormRef.value) {
    uFormRef.value.setRules(rules)
  }
})
onLoad(async ({ orderNo, shopOrderNo, no }: any) => {
  if (orderNo && shopOrderNo) {
    storageOrders.value = storage.get('logisticsOrders')
    totalNum.value = totalNumber(storageOrders.value as any[])
  }
  if (!no) return
  navTitle.value = '填写退货物流'
  uni.setNavigationBarTitle({
    title: '填写退货物流',
  })
  afsNo.value = no
  orderInfo.value = storage.get(`returnOrder`)
})

function totalNumber(orders: any[]) {
  return orders.reduce((pre, order) => order.num + pre, 0)
}
/**
 * 初始化当前物流
 * @param {*} companyCode
 * @param {*} waybillNo
 */
async function initCurrentLogistics(companyCode: string, waybillNo: string, recipientsPhone?: string) {
  const { code, msg } = await doGetLogisticsTrajectoryByWaybillNo(companyCode, waybillNo, apiLogistics.value.shopId, recipientsPhone)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取物流追踪失败'}`, icon: 'none' })
    return
  }
}
const handleConfirm = async () => {
  try {
    const validate = await uFormRef.value.validate()
    if (validate && form.value.recipientsPhone.length === 11 && REGEX_MOBILE(form.value.recipientsPhone)) {
      initCurrentLogistics(apiLogistics.value.expressCompanyCode, apiLogistics.value.expressNo, form.value.recipientsPhone)
      showModal.value = false
    } else {
      uni.showToast({ title: '手机号输入有误', icon: 'none' })
    }
  } catch (error) {
    console.log(error)
  }
}
const handleCancel = () => {
  showModal.value = false
}
</script>
<template>
  <view class="CheckLogistics">
    <LogisticsGoods :src="orderInfo?.afsOrderItem.image" :mask="false">
      <view>{{ orderInfo?.afsOrderItem.productName }}</view>
      <view class="sku">{{ orderInfo?.afsOrderItem.specs?.length ? orderInfo?.afsOrderItem.specs.join('、') : '' }}</view>
      <view class="price">
        <text>￥{{ orderInfo?.afsOrderItem && divTenThousand(orderInfo?.afsOrderItem.salePrice) }}</text>
        <text class="num">数量X{{ orderInfo?.afsOrderItem.num }}</text>
      </view>
    </LogisticsGoods>
    <!-- 填写表单 -->
    <LogisticsForm v-if="orderInfo" :afsNo="afsNo" :orderNo="orderInfo.orderNo" :shopId="orderInfo!.shopId" :packageId="orderInfo.packageId!" />
    <u-popup v-model="showModal" mode="center" border-radius="20" width="80%">
      <view class="popup_msg">顺丰快递查询</view>
      <view style="padding: 0 40rpx 20rpx 40rpx">
        <u-form ref="uFormRef" :model="form" label-width="100">
          <u-form-item label="手机号" prop="recipientsPhone">
            <u-input v-model="form.recipientsPhone" type="number" height="40" trim :maxlength="11" placeholder="请输入收货人手机号" />
          </u-form-item>
        </u-form>
        <view class="modal__btn">
          <u-button shape="circle" size="medium" ripple @click="handleCancel">取消</u-button>
          <u-button type="primary" size="medium" shape="circle" ripple @click="handleConfirm">确定</u-button>
        </view>
      </view>
    </u-popup>
  </view>
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
@include b(CheckLogistics) {
}
@include b(price) {
  color: red;
  font-size: 30rpx;
  display: flex;
  justify-content: space-between;
}
@include b(sku) {
  color: #ccc;
}
@include b(num) {
  color: #000;
  font-size: 20rpx;
}
@include b(modal) {
  @include e(btn) {
    display: flex;
    justify-content: space-between;
    margin: 20rpx 0;
  }
}
@include b(popup_msg) {
  line-height: 80rpx;
  height: 80rpx;
  text-align: center;
  font-size: 26rpx;
}
</style>
