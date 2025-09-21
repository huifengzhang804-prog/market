<script setup lang="ts">
import { ref, unref, computed } from 'vue'
import { onLoad, onReady } from '@dcloudio/uni-app'
import { Decimal } from 'decimal.js-light'
import OrderGoodsSpec from '@/components/order/order-goods-spec.vue'
import DetermineBtn from '@/components/determine-btn/determine-btn.vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import { doSubmitAfss } from '@/apis/afs'
import { aRefundWhy, aRefundType } from '@/pluginPackage/order/hooks/useAfssStatus'
import { UPLOAD_URL } from '@/hooks/useOrderStatus'
import type { ParamsAfs, UploadItem, ARefundType } from '@pluginPackage/order/applyAfterSales/types'
import type { ShopOrderItem } from '@/pluginPackage/order/orderList/types'
import { doGetShopOrderItem } from '@/apis/order'
import { getheader } from '@/libs/request/returnHeader'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand, mulTenThousand } = useConvert()
const afsForm = ref<ParamsAfs>({
  orderNo: '',
  type: '',
  shopId: '',
  itemId: '',
  reason: '',
  remark: '',
  refundAmount: '',
  evidences: [],
})
const navTitle = ref()
const orderInfo = ref<ShopOrderItem>()
const afssFormRef = ref()
const uploadRef = ref()
const isShowSheet = ref(false)
//原因回显
const why = ref('')
// 退款选择原因
const refundsList = ref<{ name: string; key: string; undelivered: boolean }[]>([])
const fileList: UploadItem[] = []
const uploadUrl = UPLOAD_URL

const rules = {
  // 字段名称
  reason: [
    {
      required: true,
      message: '请选择售后原因',
      trigger: ['change', 'blur'],
    },
  ],
  // remark: [
  //     {
  //         required: true,
  //         message: '请填写退款说明',
  //         trigger: ['change', 'blur'],
  //     },
  // ],
  refundAmount: [],
}
// 退款金额 没发货加上运费 发货后不计运费  dealPrice*num - fixPrice <= 0《（freightPrice） 退货/退款运费不计
const maxPrice = computed(() => {
  if (orderInfo.value) {
    const { dealPrice, num, fixPrice, freightPrice } = orderInfo.value
    const tPrice = new Decimal(dealPrice).mul(num).add(fixPrice).add(freightPrice)
    return divTenThousand(tPrice).toNumber()
  } else {
    return 0
  }
})

onLoad(({ type, no, itemId }: any) => {
  // type 0 退款 1 退货退款 1597112016100347904
  if (!type || !no || !itemId) return
  navTitle.value = aRefundType[type as keyof typeof ARefundType]
  uni.setNavigationBarTitle({
    title: aRefundType[type as keyof typeof ARefundType],
  })
  initRefundsList(type)
  doGetShopOrderItem(no, itemId).then(({ code, data, msg }) => {
    if (code !== 200 || !data) return
    orderInfo.value = data
    afsForm.value.orderNo = no
    afsForm.value.type = type
    afsForm.value.itemId = data.id
    afsForm.value.shopId = data.shopId
    afsForm.value.refundAmount = divTenThousand(new Decimal(data.dealPrice).mul(data.num).add(data.fixPrice)).toString()
  })
})
onReady(() => afssFormRef.value.setRules(rules))

/**
 * 退款原因
 */
function initRefundsList(type: string) {
  const isReturnRefund = type === 'RETURN_REFUND'
  const undelivered = orderInfo.value?.packageStatus === 'WAITING_FOR_RECEIVE'
  for (const key in aRefundWhy) {
    if (aRefundWhy[key].isReturnRefund === isReturnRefund) {
      refundsList.value.push({ name: aRefundWhy[key].title, key, undelivered: aRefundWhy[key].undelivered })
      if (!isReturnRefund && !undelivered) {
        // 退货退款 并且没有发货
        refundsList.value = refundsList.value.filter((item) => item.undelivered)
      }
    }
  }
}

/**
 * 导航去申请售后
 */
const handleGoApplyAfterSales = () => {
  isShowSheet.value = true
}
/**
 * 提交
 */
const handleSubmit = async () => {
  const res = await afssFormRef.value.validate()
  if (!res) return
  afsForm.value.evidences = uploadRef.value.lists.filter((item: UploadItem) => item.progress === 100).map((item: UploadItem) => item.response.data)

  const price = mulTenThousand(afsForm.value.refundAmount).toString()
  const param = { ...unref(afsForm), refundAmount: price }
  uni.showLoading({
    title: '售后申请中',
  })
  const { code, msg } = await doSubmitAfss(param)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '提交失败'}`, icon: 'none' })
  const params = `?orderNo=${afsForm.value.orderNo}&shopId=${afsForm.value.shopId}&packageId=${orderInfo.value?.packageId || ''}`
  uni.hideLoading()
  uni.navigateTo({ url: `/pluginPackage/order/orderDetail/OrderDetail${params}` })
}
/**
 * 确定退款理由
 */
const handleDetermineWhy = () => {
  if (!afsForm.value.reason) return uni.showToast({ title: '请选择退款原因', icon: 'none' })

  const name = refundsList.value.find((item) => item.key === afsForm.value.reason)!.name
  isShowSheet.value = false
  why.value = name
}
/**
 * 限制输入最大金额
 * @param {*} val
 */
const onKeyInput = (val: number) => {
  if (!orderInfo.value) return
  afsForm.value.refundAmount = val > maxPrice.value ? maxPrice.value : val
  if (val <= 0 && maxPrice.value !== 0) {
    afsForm.value.refundAmount = 0.01
  }
}

const chooseReasonForRefund = (key: string) => {
  afsForm.value.reason = key
}
</script>

<template>
  <view class="good">
    <view class="good__item">
      <order-goods-spec class="good__item-body" :info="orderInfo"></order-goods-spec>
    </view>
    <view class="good__item good__form">
      <u-form ref="afssFormRef" :model="afsForm" :error-type="['toast']">
        <!--<view v-if="navTitle === '申请退款'" class="good__item-amount">
                    <view>
                    </view>
                </view> -->
        <view class="good__item-amount refunds">
          退款金额：<text class="good__item-amount--price">{{ maxPrice }}</text>
          <text v-if="orderInfo?.freightPrice" style="margin-left: 20rpx; font-size: 12px; color: #999"
            >(运费 ￥{{ divTenThousand(orderInfo?.freightPrice).toFixed(2) }})
          </text>
        </view>
        <view v-if="!maxPrice && orderInfo?.freightPrice" class="good__item-freightPrice">
          本次退款仅退运费{{ divTenThousand(orderInfo.freightPrice) }}
        </view>
        <u-form-item prop="reason" :border-bottom="false" :required="true">
          <u-section
            color="#333333"
            class="good__item-amount--why"
            :bold="false"
            font-size="26"
            :show-line="false"
            title="退款原因"
            @click="handleGoApplyAfterSales"
          >
            <template #right>
              <view style="display: flex">
                <view v-if="why" class="good__item-amount--why-text">
                  {{ why }}
                </view>
                <view v-else>查看更多</view>
                <u-icon name="arrow-right" style="margin-left: 10rpx" label-size="0" />
              </view>
            </template>
          </u-section>
          <u-popup v-model="isShowSheet" mode="bottom" :border-radius="20" length="auto" safe-area-inset-bottom>
            <view class="popup">
              <popup-title title="退款原因" @close="isShowSheet = false" />
              <u-radio-group v-model="afsForm.reason" active-color="#f63e42">
                <view v-for="item in refundsList" :key="item.name" class="popup__item" @click="chooseReasonForRefund(item.key)">
                  <text>{{ item.name }}</text>
                  <u-radio :name="item.key" />
                </view>
              </u-radio-group>
              <DetermineBtn @click="handleDetermineWhy" />
            </view>
          </u-popup>
        </u-form-item>
        <u-form-item class="good__item-title" prop="remark" :border-bottom="false" label="退款说明（200个字符）" label-position="top">
          <u-input v-model="afsForm.remark" type="textarea" placeholder="请输入退款说明" height="150" :maxlength="200" />
        </u-form-item>
        <view class="good__item-title" style="margin-bottom: 48rpx">拍照上传凭证（最多5张）</view>
        <u-upload
          ref="uploadRef"
          :action="uploadUrl"
          :header="getheader()"
          :file-list="fileList"
          :show-upload-list="true"
          :custom-btn="true"
          :max-count="5"
          :limit-type="['png', 'jpg', 'jpeg', 'webp', 'gif']"
          :max-size="5 * 1024 * 1024"
        >
          <template #addBtn>
            <view class="good__slot-btn" hover-class="slot-btn__hover" hover-stay-time="150">
              <u-icon name="camera" size="60" color="#c0c4cc" />
              <text class="good__slot-btn--text">上传凭证</text>
              <text class="good__slot-btn--text">（最多5张）</text>
            </view>
          </template>
        </u-upload>
      </u-form>
    </view>
  </view>
  <DetermineBtn text="提交" :fixed="true" @click="handleSubmit" />
  <Auth />
</template>

<style scoped lang="scss">
@include b(good) {
  padding: 10rpx 12rpx;
  padding-bottom: 146rpx;
  @include e(item) {
    box-sizing: border-box;
    border-radius: 10rpx;
    background: #fff;
    margin-bottom: 14rpx;
    padding: 20rpx 20rpx 20rpx 20rpx;
  }
  @include e(item-freightPrice) {
    font-size: 22rpx;
    color: #82848a;
    margin: 20rpx 20rpx 0 20rpx;
  }
  @include e(form) {
    padding: 36rpx 28rpx;
  }
  @include e(item-amount) {
    background: #ebebeb;
    height: 98rpx;
    border: 2rpx solid #ebebeb;
    border-radius: 50rpx;
    font-size: 28rpx;
    color: #333333;
    line-height: 98rpx;
    padding: 0 20rpx;
    @include m(max-price) {
      font-size: 26rpx;
      color: #d5d5d5;
      flex-shrink: 0;
    }
    @include m(price) {
      color: #fa4a5a;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 24rpx;
        margin-right: 2rpx;
      }
    }
    @include m(why) {
      margin: 30rpx 0;
    }
    @include m(why-text) {
      font-size: 26rpx;
      font-weight: Bold;
      color: #515151;
    }
  }
  @include e(item-title) {
    font-size: 26rpx;
    color: #333333;
    margin-bottom: 26rpx;
  }
  @include e(slot-btn) {
    width: 166rpx;
    height: 166rpx;
    background: #ffffff;
    border: 2rpx dotted #797979;
    border-radius: 0rpx;
    @include flex;
    flex-direction: column;
    @include m(text) {
      font-size: 20rpx;
      color: #838383;
    }
  }
}
@include b(popup) {
  padding: 0 30rpx;
  @include e(item) {
    @include flex;
    justify-content: space-between;
    height: 100rpx;
    font-size: 26rpx;
    color: #000000;
    line-height: 56rpx;
    width: 720rpx;
  }
  @include e(title) {
    position: relative;
    height: 126rpx;
    font-size: 30rpx;
    font-weight: Bold;
    text-align: center;
    color: #000000;
    line-height: 126rpx;
    @include m(item) {
      position: absolute;
      right: 10rpx;
      top: 52rpx;
    }
  }
}
@include b(refunds) {
  background: #fff;
  display: flex;
  align-items: center;
  padding-left: 20px;
  @include e(title) {
    flex-shrink: 0;
    &::after {
      content: '￥';
      display: inline-block;
      font-size: 24rpx;
      margin-right: 2rpx;
    }
  }
}

.pre-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}

.pre-item {
  flex: 0 0 48.5%;
  border-radius: 10rpx;
  height: 140rpx;
  overflow: hidden;
  position: relative;
  margin-bottom: 20rpx;
}

.pre-item-image {
  width: 100%;
  height: 140rpx;
}
</style>
