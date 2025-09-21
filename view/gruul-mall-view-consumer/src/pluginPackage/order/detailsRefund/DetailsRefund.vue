<script lang="ts" setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import QSection from '@/components/q-section/q-section.vue'
import storage from '@/utils/storage'
import OrderTracking from '@/components/order/order-tracking.vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import DetailsRefundDesc from './components/details-refund-desc.vue'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import OrderInfo from '@/pluginPackage/order/detailsRefund/components/order-info.vue'
import OrderDetailStatus from '@/pluginPackage/order/orderDetail/components/order-detail-status.vue'
import { aRefundType, getAfsStatusCn } from '@/pluginPackage/order/hooks/useAfssStatus'
import { useSettingStore } from '@/store/modules/setting'
import { doGetAfssInfo, doPutAfssCancel } from '@/apis/afs'
import { doGetLogisticsTrajectoryByWaybillNo } from '@/apis/order'
import type { ApiAfsOrder } from '@/pluginPackage/order/detailsRefund/types'
import type { AFSSTATUS } from '@pluginPackage/order/applyAfterSales/types'
import Auth from '@/components/auth/auth.vue'

interface AfssTypeItem {
  fontSize: number
  text: string
  key: string
}

const { divTenThousand } = useConvert()
const afsOrderInfo = ref<ApiAfsOrder>()
const isShowSheet = ref(false)
const selectionList = ref<AfssTypeItem[]>([])
const currentPackageId = ref('')
// 确认模态框
const isShowModal = ref(false)
const pathNo = ref('')
const navTitle = ref('')
const $settingStore = useSettingStore()
// 售后基本路由
const afsBaseUrl = '/pluginPackage/order/applyAfterSales/ApplyAfterSales'
// 售后退货物流轨迹详情
const currentDeliveryLocation = ref({
  area: [] as string[],
  areaName: '',
  context: '',
  ftime: '',
  status: '',
  time: '',
})

initARefundType()
onLoad(async ({ no, packageId }: any) => {
  uni.$emit('updateTitle')
  if (!no) return
  pathNo.value = no
  if (packageId) {
    currentPackageId.value = packageId
  }
  initAfssInfo()
})

async function initAfssInfo() {
  const { code, data, msg } = await doGetAfssInfo(pathNo.value)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取售后详情失败'}`, icon: 'none' })
  if (data?.afsPackage?.expressCompanyCode) {
    const { expressCompanyCode, expressNo, shopId } = data.afsPackage
    logisticsTrajectory(expressCompanyCode, expressNo, shopId)
  }
  navTitle.value = data.type === 'RETURN_REFUND' ? '退货退款' : '退款'
  afsOrderInfo.value = data
}

/**
 * 获取售后退货物流轨迹
 * @param {*} expressCompanyCode
 * @param {*} no
 */
async function logisticsTrajectory(expressCompanyCode: string, no: string, shopId: Long) {
  const { code: status, data: res } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, no, shopId)
  if (status !== 200) return
  if (!res.status || res.status !== '200') {
    currentDeliveryLocation.value.context = res.message
    return
  }
  currentDeliveryLocation.value = res.data[res.data.length - 1]
}

/**
 * 退款类型
 */
function initARefundType() {
  for (const [key, val] of Object.entries(aRefundType)) {
    selectionList.value.push({
      fontSize: 28,
      text: val,
      key,
    })
  }
}

/**
 * 订单afs状态
 * @param {*} computed
 */
const orderAfsStatus = computed(() => {
  if (!afsOrderInfo.value) return
  return getAfsStatusCn(afsOrderInfo.value.status)
})
/**
 * 撤销申请
 */
const handleAfssClosed = () => {
  isShowModal.value = true
}
/**
 * 再次申请（如果是退款直接去退款）
 */
const handleAfssApplyForAgain = () => {
  //如果未发货 则直接申请 直接退款
  if (afsOrderInfo.value!.packageStatus === 'WAITING_FOR_DELIVER') {
    return uni.navigateTo({
      url: `${afsBaseUrl}?type=${selectionList.value[0].key}&no=${afsOrderInfo.value?.orderNo}&itemId=${afsOrderInfo.value?.shopOrderItemId}`,
    })
  }
  //否则选择 直接退款 还是 退货退款
  setAfsOrderInfo()
  isShowSheet.value = true
}

/**
 * 将商品存入本地供再次申请售后使用
 * @param {*} params
 */
function setAfsOrderInfo() {
  if (afsOrderInfo.value?.no) {
    const { no: afsNo, afsOrderItem, refundAmount: dealPrice, status: afsstatus, packageId, shopId, shopOrderItemId: id } = afsOrderInfo.value
    const { productId, image, num, productName, salePrice, services, skuId, specs } = afsOrderItem
    const params = {
      productId,
      afsNo,
      dealPrice,
      afsstatus,
      packageId,
      id,
      shopId,
      image,
      num,
      productName,
      salePrice,
      services,
      skuId,
      specs,
    }
    storage.set('applyAfterSalesOrder', params)
  }
}

/**
 * 跳转协商历史
 */
const handeGoNgtHistory = () => {
  if (!afsOrderInfo.value) return
  const url = `/basePackage/pages/negotiationHistory/NegotiationHistory?no=${afsOrderInfo.value.no}&shopId=${afsOrderInfo.value.shopId}`
  uni.navigateTo({ url })
}
/**
 * 总价
 * @param {*} computed
 */
const tatolPirce = computed(() => {
  if (!afsOrderInfo.value) return
  const { dealPrice, num } = afsOrderInfo.value.afsOrderItem
  return divTenThousand(dealPrice).mul(num).toString()
})
/**
 * 申请售后
 * @param {*} index
 */
const handleClickSheet = (index: number) => {
  const params = `?type=${selectionList.value[index].key}&no=${afsOrderInfo.value?.orderNo}&itemId=${afsOrderInfo.value?.shopOrderItemId}`
  uni.navigateTo({
    url: `${afsBaseUrl}${params}`,
  })
  isShowSheet.value = false
}
/**
 * 确定撤销申请
 */
const handleModalConfirm = async () => {
  if (!afsOrderInfo.value) return
  const { code, msg } = await doPutAfssCancel(afsOrderInfo.value?.no)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '撤销失败'}`, icon: 'none' })
  uni.showToast({ title: '撤销成功', icon: 'none' })
  isShowModal.value = false
  initAfssInfo()
}
/**
 * 导航去首页
 */
const handleGoHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
/**
 * 导航去填写单号 orderNo shopOrderNo dealPrice salePrice
 */
const handleFillInNo = () => {
  const afsOrder = { ...afsOrderInfo.value, packageId: currentPackageId.value }
  storage.set('returnOrder', afsOrder)
  uni.navigateTo({ url: `/basePackage/pages/checkLogistics/CheckLogistics?no=${afsOrderInfo.value?.no}` })
}
const handleStoreReturn = () => {
  const afsOrder = { ...afsOrderInfo.value, packageId: currentPackageId.value }
  storage.set('returnOrder', afsOrder)
  uni.navigateTo({ url: `/basePackage/pages/storeReturn/StoreReturn?no=${afsOrderInfo.value?.no}` })
}
const isfillInTheNo = (status: keyof typeof AFSSTATUS) => {
  return ['SYSTEM_RETURN_REFUND_AGREE', 'RETURN_REFUND_AGREE'].includes(status)
}
</script>

<template>
  <q-nav :title="`${navTitle}详情`" bgColor="linear-gradient(to bottom, #fed0bf, #fff)" color="#333333" />
  <view class="detailsRefund">
    <OrderDetailStatus
      :count-down-left="orderAfsStatus?.countDown.leftText"
      :count-down-right="orderAfsStatus?.countDown.rightText"
      :count-down-show="orderAfsStatus?.countDown.isCountDown"
      :height="200"
      :pay-time-out="afsOrderInfo?.timeout?.requestAgreeTimeout"
      :right-show="false"
      :status="orderAfsStatus?.title"
      :update-time="afsOrderInfo?.updateTime"
      padding="0 50rpx"
    />
    <view class="transform_container">
      <details-refund-desc
        :price="afsOrderInfo?.refundAmount || 0"
        :price-show="orderAfsStatus?.desc === '退款成功'"
        :status="orderAfsStatus?.desc"
      />
      <order-tracking
        v-if="currentDeliveryLocation.context"
        :context="currentDeliveryLocation.context"
        :status="currentDeliveryLocation.status"
        :time="currentDeliveryLocation.time"
        bg-color="#fff"
      />
      <view v-if="!orderAfsStatus?.success" class="describe">
        <div :style="{ marginBottom: afsOrderInfo?.afsPackage && '20px' }" color="#333">
          <!-- <div class="">退货地址<span color="#eee">(请将商品退货至此地址)</span></div> -->
          <div class="ccenter">
            {{ afsOrderInfo?.afsPackage?.receiverName }}
            {{ afsOrderInfo?.afsPackage?.receiverMobile }}
          </div>
          <div class="">{{ afsOrderInfo?.afsPackage?.receiverAddress }}</div>
        </div>
        <view class="describe__cancel">
          <u-button v-if="orderAfsStatus?.closable" class="describe__cancel--btn" @click="handleAfssClosed">撤销申请 </u-button>
          <u-button v-if="orderAfsStatus?.applyForAgain" class="describe__cancel--btn" @click="handleAfssApplyForAgain"> 再次申请 </u-button>
          <u-button
            v-if="afsOrderInfo?.status && isfillInTheNo(afsOrderInfo.status)"
            class="describe__cancel--btn"
            plain
            style="margin-left: 30rpx"
            type="error"
            @click="handleFillInNo"
          >
            快递退货
          </u-button>
          <u-button
            v-if="afsOrderInfo?.status && isfillInTheNo(afsOrderInfo.status)"
            class="describe__cancel--btn"
            plain
            style="margin-left: 30rpx"
            type="error"
            @click="handleStoreReturn"
          >
            到店退货
          </u-button>
        </view>
      </view>
      <details-refund-desc v-if="!orderAfsStatus?.success" :price="afsOrderInfo?.refundAmount" :status="'申请退款金额'" />
      <q-section title="协商历史" @click="handeGoNgtHistory" />
      <view class="order">
        <view class="order__title">退款信息</view>
        <order-info
          :name="afsOrderInfo?.afsOrderItem.productName"
          :num="afsOrderInfo?.afsOrderItem.num"
          :price="tatolPirce"
          :sku="afsOrderInfo?.afsOrderItem?.specs?.join(' ') || ''"
          :src="afsOrderInfo?.afsOrderItem.image"
        />
        <view class="order__text">退款说明：{{ afsOrderInfo?.explain }}</view>
        <view class="order__text">
          退款金额：
          <text class="order__text--price">{{ afsOrderInfo?.refundAmount && divTenThousand(afsOrderInfo?.refundAmount) }} </text>
        </view>
        <view class="order__text">申请时间：{{ afsOrderInfo?.createTime }}</view>
      </view>
    </view>
    <QFooterDefault bg-color="#F54319" text="返回首页" @click="handleGoHome" />
  </view>
  <u-modal
    v-model="isShowModal"
    :async-close="true"
    :show-cancel-button="true"
    content="是否撤销申请"
    @cancel="isShowModal = false"
    @confirm="handleModalConfirm"
  />
  <u-popup v-model="isShowSheet" :border-radius="20" length="auto" mode="bottom" safe-area-inset-bottom>
    <view class="popup">
      <popup-title icon="close-circle" title="退款方式" @close="isShowSheet = false" />
      <view v-for="(item, index) in selectionList" :key="item.key" class="popup__item" @click="handleClickSheet(index)">
        <text>{{ item.text }}</text>
      </view>
    </view>
  </u-popup>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(detailsRefund) {
  position: relative;
  .transform_container {
    position: absolute;
    top: 155rpx;
    width: 100%;
    padding-bottom: 108rpx;
  }
  @include e(nav) {
  }
  @include e(status) {
    height: 240rpx;
    background-color: #fe4e63;
    color: #fff;
    padding-left: 60rpx;
  }
}

@include b(describe) {
  padding: 20rpx;
  background: #fff;
  margin-bottom: 20rpx;
  @include e(text) {
    font-size: 26rpx;
    color: #959595;
    margin: 20rpx;
    &::before {
      content: ' ';
      display: inline-block;
      width: 0;
      height: 0;
      border: 2px solid #ccc;
      border-radius: 5rpx;
      background: #959595;
      margin-right: 15rpx;
      margin-bottom: 5rpx;
    }
  }
  @include e(cancel) {
    @include flex(flex-end);
    @include m(btn) {
      margin: 0;
      width: 180rpx;
      height: 70rpx;
    }
  }
}

@include b(order) {
  padding: 20rpx 0;
  background: #fff;
  @include e(text) {
    padding: 0 20rpx;
    margin-bottom: 15rpx;
    @include m(price) {
      &::before {
        content: '￥';
        color: #000;
      }
    }
  }
  @include e(title) {
    padding: 0 20rpx;
    margin: 20rpx 0;
  }
}

@include b(popup) {
  @include e(item) {
    @include flex;
    height: 124rpx;
    font-size: 28rpx;
    color: #333333;
    line-height: 124rpx;
    border-top: 1px solid #f5f5f5;
  }
}
</style>
