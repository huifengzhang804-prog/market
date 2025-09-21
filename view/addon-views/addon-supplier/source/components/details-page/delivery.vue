<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { getDeliveryedPurchasePackages, doGetLogisticsTrajectoryByWaybillNo } from '../../apis'
import type { DeliveredPackage } from './purchase'
import { type PropType, ref } from 'vue'
import { ApiPurchaseOrder } from '../../index'

const $route = useRoute()
defineProps({
  order: {
    type: Object as PropType<ApiPurchaseOrder>,
    default() {
      return {}
    },
  },
})
const activities = ref<{ context: string; status: string; time: string }[]>([])
const hasBeenShippedOrders = ref<DeliveredPackage[]>([])
const currentIndex = ref(0)

initDeliveryPageAll()

/**
 * 查询所有已发货的包裹
 */
async function initDeliveryPageAll() {
  const { code, data } = await getDeliveryedPurchasePackages($route.query.orderNo as string)
  if (code !== 200) return ElMessage.error('包裹详情获取失败')
  if (!data.length) {
    return
  }
  hasBeenShippedOrders.value = data
  handleTabClick()
}

/**
 * 查询发货后的轨迹
 */
const handleTabClick = async () => {
  const orderItem = hasBeenShippedOrders.value[currentIndex.value]
  if (orderItem.type === 'WITHOUT') return
  try {
    const { data, code, msg } = await doGetLogisticsTrajectoryByWaybillNo(orderItem.express.expressCompanyCode, orderItem.express.expressNo)
    if (code !== 200) return ElMessage.error('包裹详情获取失败')
    if (!data.data) {
      captureError(data, orderItem)
      return
    }
    activities.value = data.data.reverse()
  } catch (error: any) {
    activities.value = [
      {
        status: '包裹异常',
        time: orderItem?.createTime,
        // time: orderItem?.updateTime,
        context: error.msg,
      },
    ]
  }
}

/**
 * 捕获错误
 */
function captureError(data: any, orderItem: DeliveredPackage) {
  switch (data.returnCode) {
    case '401':
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.createTime,
          // time: orderItem.updateTime,
          context: data.message,
        },
      ]
      break
    case '400':
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.createTime,
          // time: orderItem.updateTime,
          context: data.message,
        },
      ]
      break

    default:
      activities.value = [
        {
          status: '包裹异常',
          time: orderItem.createTime,
          // time: orderItem.updateTime,
          context: data.message,
        },
      ]
      break
  }
}
</script>

<template>
  <div class="content_container">
    <div style="padding-left: 30px; margin-bottom: 30px">
      <div style="color: #000; font-size: 16px; margin-left: -10px; font-weight: 700; margin-bottom: 10px">收货人信息</div>
      <div>收货人姓名：{{ hasBeenShippedOrders?.[0]?.receiver?.name }}</div>
      <div>联系人电话：{{ hasBeenShippedOrders?.[0]?.receiver?.mobile }}</div>
      <div>收货地址：{{ hasBeenShippedOrders?.[0]?.receiver?.address }}</div>
    </div>
    <el-tabs v-model="currentIndex" type="card" @tab-change="handleTabClick">
      <el-tab-pane v-for="(orderItem, index) in hasBeenShippedOrders" :key="index" :label="'包裹' + (index + 1)" :name="index">
        <div class="logisticsInfo">
          <div class="logisticsInfo__left">
            <el-table :data="orderItem.orderItems" border style="width: 500px">
              <el-table-column label="商品 " width="400px">
                <template #default="{ row }">
                  <div style="display: flex; align-items: center">
                    <el-image
                      :src="row?.image"
                      :title="row?.productName"
                      fits="cover"
                      shape="square"
                      size="large"
                      style="width: 70px; height: 70px; margin-right: 10px"
                    />
                    <div>
                      <div>{{ row?.productName }}</div>
                      <div>{{ row?.specs }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column align="center" label="发货数">
                <template #default="{ row }">{{ row?.num }}</template>
              </el-table-column>
            </el-table>

            <div v-if="orderItem.type !== 'WITHOUT'" style="margin-top: 20px">
              <div class="logisticsInfo__title">物流详情</div>
              <el-scrollbar ref="scrollbarRef" max-height="500px">
                <el-timeline class="logisticsInfo__timeline">
                  <el-timeline-item
                    v-for="(activity, index) in activities"
                    :key="index"
                    :color="index === 0 ? '#409eff' : ' '"
                    :timestamp="`${activity.context}`"
                    class="logisticsInfo__timeline--item"
                    style="padding-bottom: 42px"
                  >
                    <el-row>
                      <div :style="{ color: index === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--status">
                        {{ activity.status }}
                      </div>
                      <div :style="{ color: index === 0 ? '#409eff' : ' ' }" class="logisticsInfo__timeline--time">
                        {{ activity.time }}
                      </div>
                    </el-row>
                  </el-timeline-item>
                </el-timeline>
              </el-scrollbar>
            </div>
          </div>
          <div class="logisticsInfo__right">
            <div class="logisticsInfo__title">物流信息</div>
            <div v-if="orderItem.type !== 'WITHOUT'">
              <div class="logisticsInfo__text">收货地址：{{ hasBeenShippedOrders[currentIndex]?.receiver?.address }}</div>
              <div class="logisticsInfo__text">物流公司：{{ hasBeenShippedOrders[currentIndex]?.express?.expressCompanyName }}</div>
              <div class="logisticsInfo__text">物流单号：{{ hasBeenShippedOrders[currentIndex]?.express?.expressNo }}</div>
            </div>
            <div v-else style="text-align: center; height: 100px; line-height: 100px">无需物流</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
@include b(content_container) {
  overflow-y: auto;
}
@include b(logisticsInfo) {
  display: flex;
  justify-content: space-between;
  @include e(left) {
    flex: 3;
  }
  @include e(right) {
    flex: 2;
  }
  @include e(timeline-con) {
    max-height: 500px;
    overflow: auto;
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
    margin-top: 20px;
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
    margin: 0 -15px;
    background: #f2f2f2;
  }
}
</style>
