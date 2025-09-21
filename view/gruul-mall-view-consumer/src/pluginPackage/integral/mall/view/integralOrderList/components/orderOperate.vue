<template>
  <view v-if="!['PAID', 'SYSTEM_CLOSED'].includes(info.status)" class="operate">
    <view>
      <view v-if="['UNPAID'].includes(info.status)" class="operate__more">
        <view style="display: flex; align-items: center; width: 100%" @click.stop="handleOpen(info.id)">
          <text>更多</text> &nbsp;<q-icon name="icon-xiajiantou" />
        </view>
        <view v-if="isMore" class="operate__more-mode" @click.stop="delOrderVisible = true"> 取消订单</view>
      </view>
    </view>
    <view class="operate__btns">
      <view v-if="['UNPAID'].includes(info.status)" class="button" @click.stop="handleNavToAddress">修改地址</view>
      <view
        v-if="['ON_DELIVERY', 'ACCOMPLISH'].includes(info.status) && !['VIRTUAL_PRODUCT'].includes(info?.productType || '')"
        class="button"
        @click.stop="viewLogistics"
      >
        查看物流
      </view>
      <view v-if="['UNPAID'].includes(info.status)" class="button red" @click.stop="toPay">去支付</view>
      <view v-if="['ON_DELIVERY'].includes(info.status)" class="button red" @click.stop="receiptVisible = true"> 确认收货 </view>
    </view>
  </view>
  <u-modal
    v-model="receiptVisible"
    :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
    :show-cancel-button="true"
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
    :zoom="false"
    confirm-color="#FA3534"
    confirm-text="确定"
    @cancel="() => (receiptVisible = false)"
    @confirm="confirmReceipt"
  >
    <view class="login-box"> 请您确认是否收货？</view>
  </u-modal>

  <u-modal
    v-model="delOrderVisible"
    :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
    :show-cancel-button="true"
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
    :zoom="false"
    confirm-color="#FA3534"
    confirm-text="确定"
    @cancel="() => (receiptVisible = false)"
    @confirm="delOrder"
  >
    <view class="login-box"> 请您确认是否取消订单？</view>
  </u-modal>
</template>

<script lang="ts" setup>
import { ref, type PropType, type Ref, inject, computed, onUnmounted } from 'vue'
import { doPutCompleteIntegralOrder, doDelIntegralOrder } from '@/pluginPackage/integral/api'
import { useChosseAddress } from '@/hooks/useChooseAddress'
import { doGetIntegralReceiver } from '@/pluginPackage/integral/api'

import type { TConfirmAddress } from '@/hooks/useChooseAddress'
import type { IOrderList } from '@/pluginPackage/integral/api/types'

const props = defineProps({
  info: {
    type: Object as PropType<IOrderList>,
    default() {
      return {}
    },
  },
})
const handleOpen = inject('handleOpen') as (id: Long) => void
const openId = inject('openId') as Ref<string>
const isMore = computed(() => openId.value === props.info.id)

const emit = defineEmits(['init-order'])

const receiptVisible = ref(false)

const delOrderVisible = ref(false)

//确认收货
const confirmReceipt = async () => {
  const { code, msg } = await doPutCompleteIntegralOrder(props.info.no)
  if (code !== 200) return uni.showToast({ title: `${msg || '收货失败'}`, icon: 'none' })
  emit('init-order')
}

// 查看物流
const viewLogistics = () => {
  const { expressCompanyName, expressNo, integralOrderReceiverVO, image, expressName } = props.info
  const query = `expressCompanyCode=${expressCompanyName}&expressNo=${expressNo}&receiverMobile=${integralOrderReceiverVO?.mobile}&expressName=${expressName}&img=${image}`
  uni.navigateTo({ url: `/pluginPackage/integral/mall/view/integralLogistics/integralLogistics?${query}` })
}
// 取消订单
const delOrder = async () => {
  const { code, msg } = await doDelIntegralOrder(props.info.no)
  if (code !== 200) return uni.showToast({ title: `${msg || '收货失败'}`, icon: 'none' })
  emit('init-order')
}
// 跳转至修改地址页面
const handleNavToAddress = () => {
  if (props.info.status === 'UNPAID') {
    // 跳转前存入修改地址信息
    getnteralListAddress(async (e: TConfirmAddress) => {
      const { code, msg } = await doGetIntegralReceiver(props.info.no, e)
      if (code !== 200) return uni.showToast({ title: `${msg || '修改地址失败失败'}`, icon: 'none' })
    })
    uni.navigateTo({
      url: `/basePackage/pages/addressManage/AddressManage?callKey=integralList`,
    })
  }
}
const { interalListAddress } = useChosseAddress()
const { getnteralListAddress, offInteralListAddress } = interalListAddress()
// 修改地址
// getnteralListAddress(async (e: TConfirmAddress) => {
//     const { code, msg } = await doGetIntegralReceiver(props.info.no, e)
//     if (code !== 200) return uni.showToast({ title: `${msg || '修改地址失败失败'}`, icon: 'none' })
// })

const { divTenThousand } = useConvert()

const priceTotal = computed(() => String(+props.info.freightPrice + +props.info.salePrice))

// 去支付
const toPay = () => {
  const { price, no, productId } = props.info
  // 支付运费
  const extra = encodeURIComponent(JSON.stringify({ ruleId: productId }))
  const infoQuery = `?no=${no}&orderType=INTEGRAL&extra=${extra}`
  const priceQuery = `&integral=${price}&price=${priceTotal.value}`
  uni.redirectTo({
    url: `/basePackage/pages/pay/Index${infoQuery + priceQuery}`,
  })
}

onUnmounted(() => {
  offInteralListAddress()
})
</script>

<style lang="scss" scoped>
.login-box {
  height: 120rpx;
  text-align: center;
  line-height: 120rpx;
}

@include b(operate) {
  margin-top: 15rpx;
  height: 52rpx;
  display: flex;
  align-items: center;
  @include e(more) {
    position: relative;
    flex-shrink: 0;
    width: 100rpx;
    height: 52rpx;
    line-height: 52rpx;
  }
  @include e(more-mode) {
    position: absolute;
    bottom: -65rpx;
    left: 0;
    width: 130rpx;
    height: 60rpx;
    background-color: #fff;
    box-shadow: 0 0 10px 0px #999;
    line-height: 60rpx;
    text-align: center;
    color: #fa101d;
    border-radius: 6rpx;
  }
  @include e(btns) {
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }
}

.button {
  border: 1px solid rgb(209, 209, 209);
  background-color: #fff;
  color: #333333;
  border-radius: 6rpx;
  margin-left: 16rpx;
  width: 179rpx;
  height: 52rpx;
  text-align: center;
  line-height: 52rpx;
}

.red {
  background: #f54319;
  color: #fff;
  border: 1px solid #f54319;
}
</style>
