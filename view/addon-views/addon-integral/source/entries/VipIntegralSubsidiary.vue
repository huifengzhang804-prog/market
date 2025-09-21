<script lang="ts" setup>
import { reactive, defineProps, ref, PropType, watch } from 'vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { doGetIntegralDetailInfo } from '../apis'
import useConvert from '@/composables/useConvert'
import type { IntegralDetailInfo, GAIN_INTEGRAL_TYPE } from '../index'

interface PropertiesType {
  userId: string
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const integralDetailList = ref<IntegralDetailInfo[]>([])
const handleSizeChange = (e: number) => {
  pageConfig.size = e
  pageConfig.current = 1
  initIntegralList()
}
const handleCurrentChange = (e: number) => {
  pageConfig.current = e
  initIntegralList()
}
const staticPayType = {
  DAY_LOGIN: '每日登入',
  INTEGRAL_PRODUCT_EXCHANGE: '积分商品兑换',
  DAY_SHARE: '每日分享',
  INTEGRAL_CLEAR: '积分清空',
  DAY_SIGN_IN: '每日签到',
  SYSTEM_RECHARGE: '系统充值',
  SYSTEM_DEDUCT: '系统扣除',
  ORDER_CONSUMPTION: '订单消费',
  ORDER_CANCEL: '订单取消',
  INTEGRAL_CONSUME: '消费所得',
} as Record<keyof typeof GAIN_INTEGRAL_TYPE, string>

watch(
  () => props.properties.userId,
  (value) => {
    if (value) {
      initIntegralList()
    }
  },
  { immediate: true },
)

async function initIntegralList() {
  const { code, data } = await doGetIntegralDetailInfo({ userId: props.properties.userId, ...pageConfig })
  if (code === 200) {
    integralDetailList.value = data.records
    pageConfig.total = data.total
  } else {
    ElMessage.error('获取交易明细失败')
  }
}

function convertDealTip(type: keyof typeof GAIN_INTEGRAL_TYPE) {
  if (!type) {
    return ''
  }
  return staticPayType[type]
}
</script>

<template>
  <el-tab-pane label="积分明细" name="theIntegralSubsidiary">
    <el-table :data="integralDetailList" :header-row-style="{ background: '#f6f8fa' }" :row-style="{ height: '60px' }" style="width: 100%">
      <el-table-column label="变动类型">
        <template #default="{ row }: { row: IntegralDetailInfo }">
          <div>
            {{ convertDealTip(row.gainIntegralType) }} <span v-if="row.particulars">({{ row.particulars }})</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="积分变动" prop="createTime">
        <template #default="{ row }: { row: IntegralDetailInfo }">
          <div>{{ row.changeType === 'INCREASE' ? '+' : '-' }} {{ row.variationIntegral }}</div>
        </template>
      </el-table-column>
      <el-table-column label="变动时间" prop="createTime" />
    </el-table>
    <PageManage
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </el-tab-pane>
</template>

<style lang="scss" scoped></style>
