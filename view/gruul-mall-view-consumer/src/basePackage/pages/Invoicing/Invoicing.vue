<script setup lang="ts">
import { ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import {
  doGetInvoiceSettings,
  doPostInvoiceRequest,
  doGetDefault,
  doGetInvoiceRequest,
  doGetInvoiceHeaderDetail,
  doPutwithdraw,
  doPostReSend,
} from '@/apis/invoice'
import { onLoad } from '@dcloudio/uni-app'
import { useInvoiceStore } from '@/store/modules/invoice'

const pages = getCurrentPages()
const { divTenThousand, mulTenThousand } = useConvert()
const showtip = ref(false)
const submitForm = ref({
  applicantId: '',
  applicantShopId: '',
  invoiceOwnerType: 'USER',
  shopSupplierName: '',
  orderNo: '',
  invoiceAmount: '',
  invoiceType: '',
  billingRemarks: '',
  invoiceHeaderId: '',
  invoiceStatus: '',
  denialReason: '',
  id: '',
})
const invoiceType = ref('VAT_COMBINED') //商家发票设置类型
const isDetails = ref(false)
const showheader = ref({
  header: '',
  taxIdentNo: '',
  openingBank: '',
  bankAccountNo: '',
  enterprisePhone: '',
  enterpriseAddress: '',
  email: '',
  invoiceHeaderType: '',
})

onLoad(async () => {
  const { orderNo, shopId, shopName, invoiceAmount, isDetail, id } = useInvoiceStore().getInvoiceInfo
  console.log('useInvoiceStore().getInvoiceInfo', useInvoiceStore().getInvoiceInfo)
  const invoiceHeaderId = useInvoiceStore().getInvoiceHeaderId
  const invoiceType = useInvoiceStore().getInvoiceType
  submitForm.value = {
    ...submitForm.value,
    orderNo,
    shopSupplierName: shopName,
    applicantShopId: shopId,
    invoiceAmount,
    invoiceType: invoiceType || '',
  }
  isDetails.value = isDetail
  if (invoiceHeaderId) {
    submitForm.value.invoiceHeaderId = invoiceHeaderId
    GetInvoiceSettings(shopId)
    GetInvoiceHeaderDetail()
    return
  }
  if (isDetails.value) {
    GetInvoiceRequest(id)
  } else {
    GetInvoiceSettings(shopId)
    GetDefault()
  }
})

/**
 * 获取发票设置
 * @param
 */
async function GetInvoiceSettings(shopId: Long) {
  const { data, code, msg } = await doGetInvoiceSettings({ invoiceSettingsClientType: 'SHOP', shopId })
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '获取开票设置失败'}` })
  invoiceType.value = data.invoiceSetupValue[0].invoiceType
  if (invoiceType.value === 'VAT_COMBINED') return (submitForm.value.invoiceType = 'VAT_GENERAL')
  submitForm.value.invoiceType = invoiceType.value
}
/**
 * 获取默认发票抬头
 * @param
 */
async function GetDefault() {
  const { data, code, msg } = await doGetDefault()
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '获取默认抬头失败'}` })
  submitForm.value.invoiceHeaderId = data.id
  GetInvoiceHeaderDetail()
}
/**
 * 获取发票详情
 * @param
 */
async function GetInvoiceRequest(id: string) {
  const { data, code, msg } = await doGetInvoiceRequest(id)
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '获取发票详情失败'}` })
  showheader.value = data
  submitForm.value = data
}
/**
 * 获取抬头详情
 * @param
 */
async function GetInvoiceHeaderDetail() {
  const { data, code, msg } = await doGetInvoiceHeaderDetail({ invoiceHeaderOwnerType: 'USER', invoiceHeaderId: submitForm.value.invoiceHeaderId })
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '获取抬头详情失败'}` })
  showheader.value = data
}
const changeType = (type: string) => {
  submitForm.value.invoiceType = type
  useInvoiceStore().SET_INVOICE_TYPE(type)
}
const submit = async () => {
  if (!submitForm.value.invoiceType) return uni.showToast({ icon: 'none', title: `请选择发票类型` })
  if (!submitForm.value.invoiceHeaderId) return uni.showToast({ icon: 'none', title: `请选择抬头` })

  const { data, code, msg } = await doPostInvoiceRequest(submitForm.value)
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '申请开票失败'}` })
  useInvoiceStore().DEL_HEADER_ID()
  useInvoiceStore().DEL_INVOICE_TYPE()
  uni.navigateTo({ url: '/pluginPackage/order/orderList/orderList' })
}
const NavtoInvoiceHeader = () => {
  uni.navigateTo({
    url: `/basePackage/pages/InvoiceHeader/InvoiceHeader`,
  })
}
const againRequest = () => {
  useInvoiceStore().SET_INVOICE_INFO({
    orderNo: submitForm.value.orderNo,
    shopId: submitForm.value.applicantShopId,
    shopName: submitForm.value.shopSupplierName,
    invoiceAmount: submitForm.value.invoiceAmount,
    isDetail: false,
    id: '',
  })
  uni.navigateTo({
    url: `/basePackage/pages/Invoicing/Invoicing`,
  })
}
const cancel = () => {
  useInvoiceStore().DEL_HEADER_ID()
  useInvoiceStore().DEL_INVOICE_TYPE()
  uni.navigateTo({ url: '/pluginPackage/order/orderList/orderList' })
}
const withdraw = async () => {
  const { data, code, msg } = await doPutwithdraw(submitForm.value.id)
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '撤销开票失败'}` })
  uni.navigateTo({ url: '/pluginPackage/order/orderList/orderList' })
}
const ReSend = async () => {
  const { data, code, msg } = await doPostReSend({ invoiceRequestId: submitForm.value.id, shopId: submitForm.value.applicantShopId })
  if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '重发发票失败'}` })
  uni.navigateTo({ url: '/pluginPackage/order/orderList/orderList' })
}
</script>

<template>
  <view v-if="!isDetails" class="type">
    <view
      v-if="invoiceType === 'VAT_SPECIAL' || invoiceType === 'VAT_COMBINED'"
      class="type__item"
      :class="{ active: submitForm.invoiceType === 'VAT_SPECIAL' }"
      @click="changeType('VAT_SPECIAL')"
    >
      <view class="type__item--symbol" :class="{ activeSymbol: submitForm.invoiceType === 'VAT_SPECIAL' }">
        <q-icon name="icon-duigou-copy" size="27rpx" />
      </view>
      <view class="type__item--title" :class="{ activeTitle: submitForm.invoiceType === 'VAT_SPECIAL' }">专用</view>
      <view class="type__item--desc" :class="{ activeTitle: submitForm.invoiceType === 'VAT_SPECIAL' }">增值税电子发票</view>
    </view>
    <view
      v-if="invoiceType === 'VAT_GENERAL' || invoiceType === 'VAT_COMBINED'"
      class="type__item"
      :class="{ active: submitForm.invoiceType === 'VAT_GENERAL' }"
      @click="changeType('VAT_GENERAL')"
    >
      <view class="type__item--symbol" :class="{ activeSymbol: submitForm.invoiceType === 'VAT_GENERAL' }">
        <q-icon name="icon-duigou-copy" size="27rpx" />
      </view>
      <view class="type__item--title" :class="{ activeTitle: submitForm.invoiceType === 'VAT_GENERAL' }">普通</view>
      <view class="type__item--desc" :class="{ activeTitle: submitForm.invoiceType === 'VAT_GENERAL' }">增值税电子发票</view>
    </view>
  </view>
  <view class="state">
    <view v-if="submitForm.invoiceStatus === 'REQUEST_IN_PROCESS'" class="state__item">
      <q-icon name="icon-a-Frame7" size="50rpx" color="#fff" style="margin-right: 18rpx" />开票中
    </view>
    <view v-else-if="submitForm.invoiceStatus === 'SUCCESSFULLY_INVOICED'" class="state__item" style="background: rgb(53, 190, 87)">
      <q-icon name="icon-dui" size="50rpx" color="#fff" style="margin-right: 18rpx" />开票成功
    </view>
    <view v-else-if="submitForm.invoiceStatus === 'FAILED_INVOICE_REQUEST'" class="state__item" style="background: rgb(233, 69, 68)">
      <q-icon name="icon-guanbi2" size="50rpx" color="#fff" style="margin-right: 18rpx" />开票失败
      <text style="font-size: 28rpx; margin-left: 30rpx">开票失败后，您可以重新申请开票</text></view
    >
  </view>
  <view class="basebox">
    <view>
      <q-icon name="icon-a-Frame12" size="30rpx" color="#F54319" />
      {{ submitForm.shopSupplierName }}
    </view>
    <view class="basebox__item">
      <view class="basebox__item--title">订单号:</view>
      {{ submitForm.orderNo }}
    </view>
    <view class="basebox__item">
      <view class="basebox__item--title">开票金额:</view>
      <view style="color: #f54319; font-weight: 500">￥ {{ divTenThousand(submitForm.invoiceAmount) }}</view>
      <q-icon name="icon-wenhao" size="32rpx" color="#999" style="margin-left: 20rpx" @click="showtip = true" />
    </view>
    <view v-if="isDetails" class="basebox__item">
      <view class="basebox__item--title">发票类型:</view>
      {{ submitForm.invoiceType === 'VAT_GENERAL' ? '增值税电子普通发票' : '增值税电子专用发票' }}
    </view>
    <view
      v-if="!isDetails"
      class="basebox__item"
      style="display: flex; justify-content: space-between; align-items: center"
      @click="NavtoInvoiceHeader"
    >
      <view style="display: flex">
        <view class="basebox__item--title">抬头选择:</view>
        {{ showheader.header }}
      </view>
      <q-icon name="icon-chevron-right" size="50rpx" color="rgb(153, 153, 153)" />
    </view>
  </view>
  <view class="basebox">
    <view class="basebox__head">抬头信息</view>
    <view class="basebox__item">
      <view class="basebox__item--title">抬头类型:</view>
      {{ showheader.invoiceHeaderType === 'PERSONAL' ? '个人或事业单位' : '企业' }}
    </view>
    <view class="basebox__item">
      <view class="basebox__item--title">发票抬头:</view>
      {{ showheader.header }}
    </view>
    <view v-if="showheader.invoiceHeaderType !== 'PERSONAL'" class="basebox__item">
      <view class="basebox__item--title">税号:</view>
      {{ showheader.taxIdentNo }}
    </view>
    <view v-if="showheader.invoiceHeaderType !== 'PERSONAL'" class="basebox__item">
      <view class="basebox__item--title">开户行:</view>
      {{ showheader.openingBank }}
    </view>
    <view v-if="showheader.invoiceHeaderType !== 'PERSONAL'" class="basebox__item">
      <view class="basebox__item--title">银行账户:</view>
      {{ showheader.bankAccountNo }}
    </view>
    <view v-if="showheader.invoiceHeaderType !== 'PERSONAL'" class="basebox__item">
      <view class="basebox__item--title">企业电话:</view>
      {{ showheader.enterprisePhone }}
    </view>
    <view v-if="showheader.invoiceHeaderType !== 'PERSONAL'" class="basebox__item">
      <view class="basebox__item--title">企业地址:</view>
      <view style="width: 520rpx">{{ showheader.enterpriseAddress }}</view>
    </view>
    <view class="basebox__item">
      <view class="basebox__item--title">邮箱地址:</view>
      {{ showheader.email }}
    </view>
  </view>
  <view class="basebox">
    <view>
      <view style="color: #000; font-weight: bold; margin-top: 10rpx; margin-right: 20rpx; margin-bottom: 10rpx">开票备注</view>
      <!-- #ifdef MP-WEIXIN -->
      <textarea
        v-if="!isDetails"
        v-model="submitForm.billingRemarks"
        type="textarea"
        :auto-height="true"
        maxlength="500"
        placeholder="请输入开票备注"
        style="width: 440rpx; margin-top: 12rpx"
      />
      <!-- #endif -->
      <!-- #ifndef MP-WEIXIN -->
      <u-input
        v-if="!isDetails"
        v-model="submitForm.billingRemarks"
        type="textarea"
        height="40"
        :auto-height="true"
        maxlength="500"
        placeholder="请输入开票备注"
      />
      <!-- #endif -->
      <view v-else>{{ submitForm.billingRemarks }}</view>
    </view>
    <view style="display: flex; justify-content: flex-end">{{ `${submitForm.billingRemarks ? submitForm.billingRemarks.length : '0'}/500` }}</view>
  </view>
  <view v-if="submitForm.invoiceStatus !== 'FAILED_INVOICE_REQUEST' && isDetails">开票成功后发票将发送至您的邮箱地址请注意查看邮件~</view>
  <view v-if="submitForm.invoiceStatus === 'FAILED_INVOICE_REQUEST'" class="basebox">
    <view class="basebox__item" style="margin-top: 0">
      <view class="basebox__item--title" style="color: #000; font-weight: bold; margin-right: 20rpx">拒绝原因</view>
      {{ submitForm.denialReason }}
    </view>
  </view>
  <view style="margin-top: 150rpx; width: 1rpx; height: 1rpx"></view>
  <view class="buttons">
    <template v-if="!isDetails">
      <view class="buttons__item" @click="cancel">取消</view>
      <view class="buttons__item" style="color: #f54319; font-weight: 600; border-left: 1px solid rgb(231, 231, 231)" @click="submit">提交申请</view>
    </template>
    <view v-if="submitForm.invoiceStatus === 'REQUEST_IN_PROCESS'" class="buttons__items" @click="withdraw">撤销开票申请</view>
    <view v-else-if="submitForm.invoiceStatus === 'SUCCESSFULLY_INVOICED'" class="buttons__items" @click="ReSend">重新发送发票到邮箱</view>
    <view v-else-if="submitForm.invoiceStatus === 'FAILED_INVOICE_REQUEST'" class="buttons__items" @click="againRequest">重新申请开票</view>
  </view>
  <u-popup v-model="showtip" mode="bottom" border-radius="20">
    <view style="padding: 20rpx">
      <view class="popTitle">开票金额说明</view>
      <view class="popContent"> 1、开票金额为消费者实付款金额，红包、优惠、购物券、消费返利等不在开票范围 </view>
      <view class="popContent">2、如订单发生退货退款、退款，开票金额也将对应退款金额扣除。</view>
    </view>
  </u-popup>
</template>

<style lang="scss" scoped>
@include b(type) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  @include e(item) {
    box-sizing: border-box;
    background: #fff;
    width: 345rpx;
    height: 155rpx;
    border: 2rpx solid rgb(255, 204, 204);
    border-radius: 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    @include m(symbol) {
      width: 57rpx;
      height: 40rpx;
      border-radius: 0px 10rpx 0px 20rpx;
      background: rgb(255, 204, 204);
      color: #fff;
      text-align: center;
      line-height: 40rpx;
      align-self: flex-end;
    }
    @include m(title) {
      margin-bottom: 11rpx;
      font-weight: 500;
      font-size: 30rpx;
      color: rgb(51, 51, 51);
    }
    @include m(desc) {
      font-size: 28rpx;
      color: rgb(153, 153, 153);
    }
  }
}
@include b(basebox) {
  box-sizing: border-box;
  width: 710rpx;
  padding: 20rpx;
  margin: 0 auto 20rpx;
  background: rgb(255, 255, 255);
  border-radius: 20rpx;
  font-size: 28rpx;
  @include e(item) {
    margin-top: 20rpx;
    display: flex;
    @include m(title) {
      width: 120rpx;
      text-align: right;
      margin-right: 20rpx;
    }
  }
  @include e(head) {
    font-weight: 500;
    color: rgb(51, 51, 51);
    text-align: center;
  }
}
@include b(buttons) {
  display: flex;
  position: fixed;
  width: 100%;
  bottom: 0;
  align-items: center;
  background: #fff;
  height: 100rpx;
  margin: auto 0;
  z-index: 100;
  font-size: 32rpx;

  @include e(item) {
    height: 77rpx;
    width: 50%;
    text-align: center;
    line-height: 77rpx;
  }
  @include e(items) {
    height: 100rpx;
    width: 100%;
    text-align: center;
    line-height: 100rpx;
    color: #fff;
    background: #f54319;
  }
}
@include b(state) {
  margin-bottom: 20rpx;
  @include e(item) {
    width: 750rpx;
    height: 134rpx;
    line-height: 134rpx;
    padding-left: 32rpx;
    color: #fff;
    font-size: 36rpx;
    background: rgb(85, 149, 255);
  }
}
.popTitle {
  font-size: 32rpx;
  color: rgb(51, 51, 51);
  text-align: center;
  font-weight: 700;
  margin-bottom: 40rpx;
}
.popContent {
  color: rgb(102, 102, 102);
  margin-bottom: 40rpx;
}

.active {
  border: 2rpx solid #f54319;
}
.activeSymbol {
  background: #f54319;
}
.activeTitle {
  color: #f54319;
}
</style>
