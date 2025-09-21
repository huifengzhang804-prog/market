<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useConversionPrice, useConversionHaoPrice } from '@/utils/useConversionPrice'
import { doGetPaymentHistory, doGetSavingManage } from '@/apis/paymentDetail'
import { doGetUserBalance } from '@/apis/consumer'
import { ApiOnlineTopUp, OnlineTopUpItem, BillingDetailsItem, dealTypeCn, DEAL_TYPE } from '@/views/personalcenter/assets/balance/types'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { SearchConfig, PageConfig, ApiResponse, PaymentHistoryResponse } from '@/views/personalcenter/assets/balance/types'

const $router = useRouter()
const activeName = ref<string>('1')
const balance = ref<string>('0')
const priceVal = ref<number>()
const changeType = ref<string>('INCREASE')
const topUpParam = ref<ApiOnlineTopUp>({
  switching: true,
  discountsState: true,
  ruleJson: [],
  id: '',
})
const currentTopUpItem = ref<OnlineTopUpItem>({
  id: '',
  ladderMoney: '',
  presentedGrowthValue: '',
  presentedMoney: '',
})
const searchConfig = reactive<SearchConfig>({
  chooseTime: ['', ''],
})
const billingDetailList = ref<BillingDetailsItem[]>([])
const pageConfig = reactive<PageConfig>({
  current: 1,
  size: 10,
  total: 1,
})
const activeIndex = ref<number>(0)

initUserBalance()
paymentHistory()
initSavingManage()

async function initSavingManage() {
  const { code, data, msg } = (await doGetSavingManage()) as ApiResponse<ApiOnlineTopUp>
  if (code !== 200) return ElMessage.error(msg ? msg : '获取储值管理信息失败')
  topUpParam.value = data
  if (data.ruleJson && data.ruleJson.length > 0) {
    currentTopUpItem.value = data.ruleJson[0]
  }
}

async function initUserBalance() {
  const { code, msg, data } = (await doGetUserBalance()) as ApiResponse<string>
  if (code !== 200) return ElMessage.error(msg ? msg : '获取余额失败')
  balance.value = data || '0'
}

async function paymentHistory() {
  const { code, data, msg } = (await doGetPaymentHistory({
    size: pageConfig.size,
    current: pageConfig.current,
    dealAggregationType: 'ALL',
    changeType: changeType.value,
    leftQueryTime: searchConfig.chooseTime ? searchConfig.chooseTime[0] : null,
    rightQueryTime: searchConfig.chooseTime ? searchConfig.chooseTime[1] : null,
  })) as ApiResponse<PaymentHistoryResponse>
  if (code !== 200) return ElMessage.error(msg ? msg : '获取账单明细失败')
  const { records, total } = data
  pageConfig.total = total
  billingDetailList.value = records
}

const handleChangeDate = (e: string[]) => {
  pageConfig.current = 1
  searchConfig.chooseTime = e
  paymentHistory()
}
const handlegotoTopUp = () => {
  activeName.value = '2'
}
const changeTypes = (Type: string) => {
  pageConfig.current = 1
  changeType.value = Type
  paymentHistory()
}
const handleChangePage = (e: number) => {
  pageConfig.current = e
  paymentHistory()
}
const handleLadderMoneyClick = (index: number, topUpItem: OnlineTopUpItem) => {
  activeIndex.value = index
  currentTopUpItem.value = topUpItem
}
const handleTopup = () => {
  let price = ''
  let ruleId = ''
  if (priceVal.value) {
    price = String(useConversionHaoPrice(priceVal.value))
  } else {
    price = currentTopUpItem.value.ladderMoney
    ruleId = currentTopUpItem.value.id
  }
  if (Number(price) <= 0) return ElMessage.error('请填写金额')
  $router.push({
    path: '/pay',
    query: { price: useConversionPrice(price).toString(), ruleId },
  })
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <el-tabs v-model="activeName">
          <el-tab-pane name="1">
            <template #label>
              <span class="custom-tabs-label" c-w-116 c-fs-18 e-c-3 fw-700>
                <span>我的储值</span>
              </span>
            </template>

            <div e-c-3 c-fs-16 text-left>
              当前储值：
              <div text-center c-c-e31436 c-fs-30 fw-800>{{ useConversionPrice(balance) }}元</div>
              <div
                v-if="topUpParam.switching"
                c-w-80
                c-h-30
                c-lh-30
                e-c-f
                text-center
                c-bg-e31436
                cursor-pointer
                style="float: right"
                @click="handlegotoTopUp"
              >
                储值充值
              </div>
            </div>
            <div e-c-3 c-fs-14 fw-700 text-left c-mt-37 c-mb-10>交易记录</div>
            <div b-b c-bc-ccc c-fs-12 e-c-3 flex>
              <div
                c-w-120
                cursor-pointer
                c-pb-10
                :class="changeType === 'INCREASE' ? 'b-b-2 c-bc-e31436 c-c-e31436 ' : ''"
                @click="changeTypes('INCREASE')"
              >
                收入
              </div>
              <div c-w-1 c-h-15 c-bg-bbb />
              <div
                c-w-120
                c-pb-10
                cursor-pointer
                :class="changeType === 'REDUCE' ? 'b-b-2 c-bc-e31436 c-c-e31436 ' : ''"
                @click="changeTypes('REDUCE')"
              >
                支出
              </div>
              <div c-ml-295 c-mb-4 flex items-center>
                日期：
                <div b-1 c-bc-DCDCDC>
                  <el-date-picker
                    v-model="searchConfig.chooseTime"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD"
                    @change="handleChangeDate"
                  />
                </div>
              </div>
              <div c-w-79 c-h-31 b-1 c-bc-ccc c-lh-31 c-fs-12 c-ml-16 cursor-pointer @click="paymentHistory">查询</div>
            </div>
            <el-table :data="billingDetailList" style="width: 100%">
              <el-table-column prop="createTime" label="交易日期" />
              <el-table-column prop="dealType" label="类型">
                <template #default="{ row }">
                  <div>{{ dealTypeCn[row.dealType as keyof typeof DEAL_TYPE] }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="money" label="金额（元）">
                <template #default="{ row }">
                  <div>{{ useConversionPrice(row.money) }}</div>
                </template>
              </el-table-column>
            </el-table>
            <div c-mt-28 c-mb-43 flex justify-center>
              <el-pagination
                background
                layout="prev, pager, next, total"
                :total="+pageConfig.total"
                :current-page="pageConfig.current"
                @current-change="handleChangePage"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane v-if="topUpParam.switching" name="2">
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
                <span>储值充值</span>
              </span>
            </template>

            <div v-if="activeName === '2'" c-h-57 c-bg-f9f9f9 text-left c-c-101010 c-lh-57 c-fs-14 c-pl-19>
              充{{ useConversionPrice(currentTopUpItem.ladderMoney) }}元，
              <span v-if="topUpParam.discountsState">
                额外赠送{{ useConversionPrice(currentTopUpItem.presentedMoney) }}元；赠送{{ currentTopUpItem.presentedGrowthValue }}成长值</span
              >
            </div>
            <div v-if="topUpParam.switching" flex c-fs-15 c-ml-20 items-center c-mt-24 flex-wrap>
              <div c-fs-14 e-c-3 fw-700 c-w-90 text-right>充值金额</div>
              <div
                v-for="(item, index) in topUpParam.ruleJson"
                :key="item.id"
                c-ml-24
                c-mt-14
                c-w-133
                b-1
                c-h-40
                c-lh-40
                cursor-pointer
                :class="index === activeIndex ? 'c-bc-e31436' : 'c-bc-eee'"
                @click="handleLadderMoneyClick(index, item)"
              >
                {{ item.ladderMoney && useConversionPrice(item.ladderMoney) }}元
              </div>
            </div>
            <div flex c-fs-15 c-ml-20 items-center c-mt-24>
              <div c-fs-14 e-c-3 fw-700 c-w-90 text-right>自定义金额</div>
              <div b-1 c-bc-eee c-ml-24 c-h-40 flex items-center c-w-190>
                <el-input-number v-model="priceVal" :controls="false" :precision="2" :min="0.01" :max="100000" placeholder="单笔最多充值10万" />
                <div c-w-40 c-h-40 c-lh-40 c-bg-e9e9e9>元</div>
              </div>
            </div>
            <div flex c-fs-15 c-ml-20 items-center c-mt-24>
              <div c-fs-14 e-c-3 fw-700 c-w-90 text-right>当前可用余额</div>
              <div c-fs-20 c-c-e31436 fw-900 c-ml-24>{{ useConversionPrice(balance) }}元</div>
            </div>
            <div c-w-114 c-h-40 c-lh-40 e-c-f c-fs-14 c-bg-e31436 c-ml-128 c-mt-28 cursor-pointer @click="handleTopup">立即充值</div>
          </el-tab-pane>
        </el-tabs>
        <ToTopGoCar />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss"></style>
