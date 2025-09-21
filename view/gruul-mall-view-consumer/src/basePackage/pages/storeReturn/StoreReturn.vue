<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onReady } from '@dcloudio/uni-app'
import storage from '@/utils/storage'
import ReturnShopForm from './components/return-shop-form.vue'
import ReturnShopGoods from './components/return-shop-goods.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import type { ApiAfsOrder } from '@pluginPackage/order/detailsRefund/types'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand } = useConvert()
const navTitle = ref('查看物流')
const afsNo = ref('')
const orderInfo = ref<ApiAfsOrder>()
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

// 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
onReady(() => {
  if (uFormRef.value) {
    uFormRef.value.setRules(rules)
  }
})
onLoad(async ({ no }: any) => {
  if (!no) return
  navTitle.value = '到店退货'
  uni.setNavigationBarTitle({
    title: '到店退货',
  })
  afsNo.value = no
  orderInfo.value = storage.get(`returnOrder`)
})
</script>
<template>
  <view class="CheckLogistics">
    <return-shop-goods :src="orderInfo?.afsOrderItem.image" :mask="false">
      <view>{{ orderInfo?.afsOrderItem.productName }}</view>
      <view class="sku">{{ orderInfo?.afsOrderItem.specs }}</view>
      <view class="price">
        <text>￥{{ orderInfo?.afsOrderItem && divTenThousand(orderInfo?.afsOrderItem.salePrice) }}</text>
        <text class="num">数量X{{ orderInfo?.afsOrderItem.num }}</text>
      </view>
    </return-shop-goods>
    <!-- 填写表单 -->
    <return-shop-form :afs-no="afsNo" :order-no="orderInfo!.orderNo" :shop-id="orderInfo!.shopId" :package-id="orderInfo!.packageId as string" />
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
