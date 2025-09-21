<script setup lang="ts">
import { ref } from 'vue'
import Portrait from '@/components/portrait/portrait.vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/modules/user'
import { doGetAfssHistory } from '@/apis/afs'
import { getAfsStatusCn, getARefundReasonCn, systemInfo } from '@/pluginPackage/order/hooks/useAfssStatus'
import type { ApiUserData, ProcessingHistory } from './types'
import type { ApiAfsOrder } from '@/pluginPackage/order/detailsRefund/types'
import Auth from '@/components/auth/auth.vue'
import { doGetShopInfo } from '@/apis/good'

const { divTenThousand } = useConvert()
const userInfo = ref<ApiUserData>({
  avatar: '',
  createTime: '',
  deleted: false,
  gender: '',
  id: '',
  nickname: '',
  updateTime: '',
  userId: '',
  version: 0,
})
const shopInfo = ref({
  logo: '',
  name: '',
})
const $userStore = useUserStore()
const processingHistory = ref<ProcessingHistory[]>([])

onLoad(async ({ no, shopId }: any) => {
  if (!no || !shopId) return uni.showToast({ title: `获取数据失败`, icon: 'none' })
  initUserInfo()
  initHistory(no)
  initShopInfo(shopId)
})

function initUserInfo() {
  const { info } = $userStore.getterUserInfo
  userInfo.value = info
}
async function initShopInfo(shopId: Long) {
  const { data, code, msg } = await doGetShopInfo({ shopId, type: 'PRODUCT_DETAIL' })
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取商铺信息失败'}`, icon: 'none' })
  if (data) {
    shopInfo.value = data
  }
}
/**
 * 初始化协商历史（协商历史中添加金额和状态）
 * @param {*} no
 */
async function initHistory(no: string) {
  const { data, code, msg } = await doGetAfssHistory(no)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取协商历史失败'}`, icon: 'none' })
  for (let i = 0; i < data.length; i++) {
    data[i].histories[data[i].histories.length - 1] = Object.assign(data[i].histories[data[i].histories.length - 1], {
      reason: data[i].reason,
      refundAmount: data[i].refundAmount,
    })
  }
  processingHistory.value = data.flatMap((item: ApiAfsOrder) => item.histories)
  initProcessingHistory()
}
/**
 * 初始化协商历史（协商历史中整合用户头像和商家logo）
 */
const initProcessingHistory = () => {
  if (!processingHistory.value) return
  processingHistory.value = processingHistory.value.map((item) => {
    const afsInfo = getAfsStatusCn(item.afsStatus)
    const isConsumer = afsInfo.isConsumer
    const isSystem = afsInfo.isSystem
    if (isConsumer) {
      return {
        ...item,
        name: userInfo.value?.nickname,
        logo: userInfo.value?.avatar,
        afsStatusCn: afsInfo.desc,
        isConsumer: true,
        type: afsInfo.type,
        title: afsInfo.title,
      }
    } else if (isSystem) {
      return {
        ...item,
        name: systemInfo.name,
        logo: systemInfo.avatar,
        afsStatusCn: afsInfo.desc,
        isConsumer: false,
        type: afsInfo.type,
        title: afsInfo.title,
      }
    }
    console.log(afsInfo, '===============')
    return {
      ...item,
      name: shopInfo.value.name,
      logo: shopInfo.value.logo,
      afsStatusCn: afsInfo.desc,
      isConsumer: false,
      type: afsInfo.type,
      title: afsInfo.title,
    }
  })
  console.log('processingHistory.value', processingHistory.value)
}
/**
 * 拒绝说明 / 急速退款权益等
 * @param {*} afsStatus
 */
function initRemarkType(afsStatus: ProcessingHistory['afsStatus']) {
  return ['RETURNED_REFUND_REJECT', 'REFUND_REJECT', 'RETURN_REFUND_REJECT'].includes(afsStatus) ? '拒绝说明：' : ''
}
</script>
<template>
  <portrait
    v-for="(historyItem, index) in processingHistory"
    :key="index"
    :time="historyItem.createTime"
    :src="historyItem.logo"
    :title="historyItem.name"
  >
    <!-- 快递退货 -->
    <div v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressCompanyName" class="detail">
      物流公司：{{ historyItem.historyBuyerReturnedInfo.expressRefund.expressCompany.expressCompanyName }}
    </div>
    <div v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressNo" class="detail">
      物流单号：{{ historyItem.historyBuyerReturnedInfo.expressRefund.expressCompany.expressNo }}
    </div>
    <div v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.mobile" class="detail">
      联系电话：{{ historyItem.historyBuyerReturnedInfo.expressRefund.mobile }}
    </div>
    <!-- 到店退货 -->
    <div v-if="historyItem?.historyBuyerReturnedInfo?.goStoreRefund" class="detail">
      退货店员名：{{ historyItem.historyBuyerReturnedInfo.goStoreRefund?.shopAssistantName }}
    </div>
    <div v-if="historyItem?.historyBuyerReturnedInfo?.goStoreRefund" class="detail">
      退货店员电话：{{ historyItem.historyBuyerReturnedInfo.goStoreRefund?.mobile }}
    </div>
    <template v-if="['RETURN_REFUND_REQUEST', 'REFUND_REQUEST'].includes(historyItem?.afsStatus)">
      <div v-if="historyItem?.remark" class="detail">退款说明：{{ historyItem?.remark }}</div>
    </template>
    <template v-else>
      <div v-if="historyItem?.remark" class="detail">补充描述：{{ historyItem?.remark }}</div>
    </template>
    <template v-if="!historyItem.isConsumer">
      <div class="detail">审批类型：{{ historyItem?.title }}</div>
      <div class="detail">审批时间：{{ historyItem?.createTime }}</div>
    </template>
    <template v-else-if="['RETURN_REFUND_REQUEST', 'REFUND_REQUEST'].includes(historyItem?.afsStatus)">
      <div class="detail">退款类型：{{ historyItem?.type }}</div>
      <div class="detail">退款金额：￥{{ divTenThousand(historyItem.refundAmount) }}</div>
      <div class="detail">退款原因：{{ getARefundReasonCn(historyItem.reason as string) }}</div>
    </template>
    <div v-if="['RETURN_REFUND_REQUEST', 'REFUND_REQUEST'].includes(historyItem?.afsStatus)" class="detail">
      申请时间：{{ historyItem.createTime }}
    </div>
    <div v-else-if="historyItem.isConsumer" class="detail">发货时间：{{ historyItem.createTime }}</div>
    <view v-if="historyItem.evidences?.length">拍照凭证：</view>
    <view class="images">
      <u-image v-for="image in historyItem.evidences" :key="image" :width="88" :height="88" border-radius="10" class="images__image" :src="image" />
    </view>
  </portrait>
  <Auth />
</template>

<style scoped lang="scss">
@include b(images) {
  margin-top: 20rpx;
  @include flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  @include e(image) {
    width: 155rpx;
    height: 160rpx;
    margin-right: 15rpx;
    border-radius: 30rpx;
  }
}
@include b(detail) {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #000;
}
</style>
