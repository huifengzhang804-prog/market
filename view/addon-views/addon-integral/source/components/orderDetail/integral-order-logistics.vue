<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { doGetIntegralOrderInfoByNo } from '../../apis'
import { doGetLogisticsTrajectoryByWaybillNo } from '@/apis/afs'
import handleGetCompanyName from '@/assets/json/data.json'
import type { DeliverType } from '../../index'
import type { IntegralOrderInfo } from '../types/order'
import { useRoute } from 'vue-router'

const $route = useRoute()
const orderNo = $route.query.no as string | null

const danger = ref(false)
const orderInfo = ref<IntegralOrderInfo>({
  sellerRemark: '',
  buyerRemark: '',
  payTime: '',
  deliveryTime: '',
  accomplishTime: '',
  buyerId: '',
  buyerNickname: '',
  createTime: '',
  freightPrice: 0,
  image: '',
  salePrice: '',
  integralOrderReceiver: {
    address: '',
    area: [],
    createTime: '',
    id: '',
    mobile: '',
    name: '',
    orderNo: '',
    updateTime: '',
  },
  no: '',
  num: 1,
  price: '',
  productName: '',
  status: 'PAID',
  productType: 'REAL_PRODUCT',
})
const activities = ref([
  {
    context: '',
    status: '',
    time: '',
  },
])

initDeliveryPageAll()

/**
 * 是无需物流发货
 */
function isWithout(type: keyof typeof DeliverType) {
  return type === 'WITHOUT'
}
async function initDeliveryPageAll() {
  if (orderNo) {
    const { code, data, msg } = await doGetIntegralOrderInfoByNo(orderNo)
    if (code !== 200 || !data) {
      ElMessage.error(msg || '订单详情获取失败')
      return
    }
    orderInfo.value = data
    if (isWithout(data.integralOrderDeliverType)) {
      // 无需物流发货 不请求数据
      return
    }
    getAfterDelivery(data)
  }
}

/**
 * 查询发货后的轨迹
 */
const getAfterDelivery = async (orderItem: IntegralOrderInfo) => {
  if (orderItem.integralOrderDeliverType === 'WITHOUT') {
    activities.value = [
      {
        context: '无需物流',
        time: orderItem.createTime,
        status: '无需物流',
      },
    ]
    return
  }
  try {
    if (orderItem.expressCompanyName && orderItem.expressNo) {
      const { data, code, msg } = await doGetLogisticsTrajectoryByWaybillNo(orderItem.expressCompanyName, orderItem.expressNo)
      if (code !== 200) return ElMessage.error(msg || '物流轨迹获取失败')
      if (!data.data) {
        captureError(data, orderItem)
        return
      }
      activities.value = data.data
    } else {
      throw new Error('包裹信息获取失败')
    }
  } catch (error) {
    danger.value = true
    activities.value = [
      {
        context: '包裹异常',
        time: orderItem.integralOrderReceiver.updateTime,
        status: '包裹异常',
      },
    ]
  }
}
/**
 * 捕获错误
 * @returns {*}
 */
function captureError(data: { returnCode: string; message: string }, orderItem: IntegralOrderInfo) {
  switch (data.returnCode) {
    case '401':
      danger.value = true
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.integralOrderReceiver.updateTime,
          context: data.message,
        },
      ]
      break
    case '400':
      danger.value = true
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.integralOrderReceiver.updateTime,
          context: data.message,
        },
      ]
      break

    default:
      danger.value = true
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.integralOrderReceiver.updateTime,
          context: data.message,
        },
      ]
      break
  }
}
</script>

<template>
  <div class="logisticsInfo">
    <div v-if="orderInfo.integralOrderDeliverType">
      <div class="logisticsInfo__title">物流信息</div>
      <div class="logisticsInfo__text">
        收货地址：{{ orderInfo.integralOrderReceiver.area?.join('') }} {{ orderInfo.integralOrderReceiver.address }}
      </div>
      <template v-if="orderInfo.integralOrderDeliverType !== 'WITHOUT'">
        <div class="logisticsInfo__text">物流公司：{{ handleGetCompanyName(orderInfo.expressCompanyName) }}</div>
        <div class="logisticsInfo__text" style="margin-bottom: 20px">物流单号：{{ orderInfo.expressNo }}</div>
      </template>
      <div v-else class="logisticsInfo__text">物流公司：无需物流</div>
      <div class="logisticsInfo__divider" />
      <div class="logisticsInfo__title">物流详情</div>
      <div class="logisticsInfo__overflow">
        <el-timeline class="logisticsInfo__timeline">
          <el-timeline-item
            v-for="(activity, index) in activities.reverse()"
            :key="index"
            :timestamp="`${activity.context}`"
            style="padding-bottom: 42px"
            :color="index === 0 ? (danger ? '#F56C6C' : '#409eff') : ' '"
            class="logisticsInfo__timeline--item"
          >
            <el-row>
              <div :style="{ color: index === 0 ? (danger ? '#F56C6C' : '#409eff') : ' ' }" class="logisticsInfo__timeline--status">
                {{ activity.status }}
              </div>
              <div :style="{ color: index === 0 ? (danger ? '#F56C6C' : '#409eff') : ' ' }" class="logisticsInfo__timeline--time">
                {{ activity.time }}
              </div>
            </el-row>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
    <el-alert v-else title="暂无物流信息" type="info" :closable="false" />
  </div>
</template>

<style scoped lang="scss">
@include b(logisticsInfo) {
  padding: 0 16px;
  overflow-y: scroll;
  @include e(overflow) {
    height: calc(100vh - 400px);
    overflow-y: auto;
    padding: 0 10px;
  }
  @include e(title) {
    font-size: 14px;
    color: #333333;
    font-weight: Bold;
    margin: 17px 0;
  }
  @include e(text) {
    margin-bottom: 11px;
  }
  @include e(timeline) {
    margin-top: 42px;
    & li:nth-child(1) {
      & :deep(.el-timeline-item__timestamp) {
        color: #000;
      }
    }
    @include m(status) {
      font-size: 13px;
      font-weight: bold;
      margin-right: 10px;
      color: #838383;
    }
    @include m(time) {
      color: #838383;
    }
  }
  @include e(divider) {
    height: 4px;
    // margin: 0 -15px;
    background: #f2f2f2;
  }
}
</style>
