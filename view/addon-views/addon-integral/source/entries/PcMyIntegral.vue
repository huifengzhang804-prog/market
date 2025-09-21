<script setup lang="ts">
import 'uno.css'
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {
  doGetUserIntegralDetailInfo,
  doGetUserIntegralSystemtotal,
  doGetIntegralBehaviorDays,
  doGetIntegralBehaviorSave,
  doGetIntegralRulesInfo,
  doGetIntegralOrderList,
  doPutCompleteIntegralOrder,
  doDelIntegralOrder,
  doGetLogisticsTrajectoryByWaybillNo,
  doGetIntegralOrderDetail,
} from '../apis'
import { IntegralDetailInfos, IntegralRulesParameter, IIntegralOrderListParams, IOrderList } from '../index'
import formatRichText from '@/utils/formatRichText'
import { ElMessage, ElMessageBox } from 'element-plus'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import useConvert from '@/composables/useConvert'
import OrderTrace from '../components/orderTrace.vue'
import Countdown from '@/components/countdown.vue'
const { divTenThousand } = useConvert()
const $router = useRouter()

const activeName = ref('1')

const gainIntegralCn = reactive<any>({
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
})
const indexInt = reactive<any>({
  0: '第一天',
  1: '第二天',
  2: '第三天',
  3: '第四天',
  4: '第五天',
  5: '第六天',
  6: '第七天',
})
interface Params {
  size: number
  current: number
  pages: number
  beginTime: string
  endTime: string
  gainIntegralType?: string
}
let params = reactive<Params>({
  size: 10,
  current: 1,
  pages: 1,
  beginTime: '',
  endTime: '',
})
const total = ref(0)
const list = ref<IntegralDetailInfos[]>([])
const lists = ref<IntegralDetailInfos[]>([])
// 列表
const UserIntegralDetailInfo = async () => {
  const { data, code, msg } = await doGetUserIntegralDetailInfo(params)
  if (code === 200) {
    list.value = data?.records
    lists.value = data?.records
    total.value = +data?.total
    earnIntegral.value = data?.statistics?.earnIntegral || '0'
  } else ElMessage.error(msg)
}
UserIntegralDetailInfo()
const systemtotal = ref('')
// 当前积分
const UserIntegralSystemtotal = async () => {
  const { code, data, msg } = await doGetUserIntegralSystemtotal()
  if (code === 200) systemtotal.value = data
  else ElMessage.error(msg)
}
UserIntegralSystemtotal()
// 分页
const handleChangePage = (e: number) => {
  params.current = e
  UserIntegralDetailInfo()
}
// 分页
const handleChangePages = (e: number) => {
  params.current = e
  UserIntegralDetailInfo()
}
const paginationKey = ref(0)
const continueDays = ref<number>()
const todayState = ref(false)
const ids = ref('1')
// tab切换
const refreshFn = async (pane: any) => {
  ids.value = pane?.props?.name
  if (pane?.props?.name === '1' || pane?.props?.name === '2') {
    if (pane?.props?.name === '2') {
      params = { size: 10, current: 1, pages: 1, beginTime: '', endTime: '', gainIntegralType: 'DAY_SIGN_IN' }
      IntegralBehaviorDays()
      initRulesInfo()
    } else {
      params = { size: 10, current: 1, pages: 1, beginTime: '', endTime: '' }
      UserIntegralSystemtotal()
    }
    UserIntegralDetailInfo()
    value.value = ''
    paginationKey.value = Date.now()
  } else if (pane?.props?.name === '3') {
    IntegralOrderListInt()
  } else if (pane?.props?.name === '4') {
    initRulesInfo()
  }
}
const IntegralBehaviorDays = async () => {
  const { code, data, msg } = await doGetIntegralBehaviorDays('SING_IN')
  if (code === 200) {
    continueDays.value = data.continueDays
    todayState.value = data.todayState
  } else ElMessage.error(msg)
}
const earnIntegral = ref('')
const value = ref('')
// 月份筛选数据
const pickerChange = (val: any) => {
  if (!val) {
    if (ids.value === '1') params = { size: 10, current: 1, pages: 1, beginTime: '', endTime: '' }
    else if (ids.value === '2') params = { size: 10, current: 1, pages: 1, beginTime: '', endTime: '', gainIntegralType: 'DAY_SIGN_IN' }
    UserIntegralDetailInfo()
  }
  params.beginTime = formatDate(val)
  const data = params.beginTime.split('-')
  if (data) params.endTime = formatDate(new Date(+data[0], +data[1], 0))
  else params.endTime = ''
  UserIntegralDetailInfo()
}
// 转时间格式
function formatDate(date: Date) {
  let year = date.getFullYear()
  let month: string | number = date.getMonth() + 1
  if (month < 10) month = `0${month}`
  let day: string | number = date.getDate()
  if (day < 10) day = `0${day}`
  return year + '-' + month + '-' + day
}
// 签到
const signInFn = async () => {
  if (todayState.value) return
  const { code, data, msg } = await doGetIntegralBehaviorSave('SING_IN')
  if (code !== 200) return ElMessage.error(msg || '签到失败')
  else if (data >= 0) {
    todayState.value = true
    IntegralBehaviorDays()
    UserIntegralDetailInfo()
    UserIntegralSystemtotal()
  }
}
// 每天的积分数
const rulesInfo = ref([0, 0, 0, 0, 0, 0, 0])
// 获取积分规则
async function initRulesInfo() {
  const { code, data, msg } = await doGetIntegralRulesInfo()
  if (code !== 200) return ElMessage.error(msg || '签到失败')
  if (code === 200) {
    submitFrom.value = data
    formatData(data)
  }
  if (data && data.integralGainRule && Array.isArray(data.integralGainRule)) {
    const integralGainRule = data.integralGainRule.find((item: any) => item.gainRuleType === 'SING_IN')
    if (integralGainRule && integralGainRule.open) {
      const {
        rulesParameter: { basicsValue, extendValue },
      } = integralGainRule
      rulesInfo.value = rulesInfo.value.map((item) => basicsValue)
      for (const key in extendValue) {
        rulesInfo.value[Number(key) - 1] = extendValue[key] + basicsValue
      }
    }
  }
}

const submitFrom = ref<IntegralRulesParameter>({
  indate: 12,
  useRule: '',
  ruleInfo: '',
  integralGainRule: [
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'SHARE',
      open: false,
    },
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'LOGIN',
      open: false,
    },
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'SING_IN',
      open: false,
    },
  ],
})
// 积分规则映射对象
const integralGainRuleMapping = ref({
  LOGIN: [
    {
      key: 0,
      value: 0,
    },
    {
      key: 0,
      value: 0,
    },
  ],
  SING_IN: [
    {
      key: 0,
      value: 0,
    },
    {
      key: 0,
      value: 0,
    },
  ],
})
/**
 * 后端返回数据整理
 */
function formatData(data: IntegralRulesParameter) {
  data?.integralGainRule?.forEach((item) => {
    let arr: any = []
    let integralArray = integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN']
    if (integralArray) {
      for (const key in item.rulesParameter.extendValue) {
        arr.push({
          key,
          value: item.rulesParameter.extendValue[key],
        })
      }
      integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN'] = arr
    }
  })
}
const param: IIntegralOrderListParams = {
  size: 10,
  current: 1,
  pages: 1,
  status: '',
}
const IntegralOrderList = ref<IOrderList[]>([])
const totalList = ref(0)
// 积分订单 doGetIntegralOrderList
const IntegralOrderListInt = async () => {
  const { data, code, msg } = await doGetIntegralOrderList(param)
  if (code === 200) {
    IntegralOrderList.value = data.records
    totalList.value = +data?.total
  } else return ElMessage.error(msg || '获取订单列表失败')
}
// 订单分页
const handleChangePageList = (e: number) => {
  param.current = e
  IntegralOrderListInt()
}
// 订单详情
const handleNavToDetail = (item: IOrderList) => {
  let intOrd = true
  $router.push({
    path: '/personalcenter/order/orderdetail',
    query: { orderNo: item?.no, packageId: item?.productId || '', intOrd: intOrd as any },
  })
}
// 确认收货
const confirmReceipt = async (item: IOrderList) => {
  const showModalProps = {
    content: '是否确认收货',
    showClose: true,
    isSubmit: true,
  }
  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, data, msg } = await doPutCompleteIntegralOrder(item?.no)
    if (code !== 200) return ElMessage.error(`${msg ? msg : '确认收货失败'}`)
    IntegralOrderListInt()
  })
}
// 付款
const paymentFn = (item: IOrderList) => {
  if (!item) return
  $router.push({
    path: '/pay',
    query: { no: item?.no, integral: item?.price, ruleId: item?.id, price: divTenThousand(+item?.freightPrice + +item?.salePrice) as any },
  })
}
// 取消订单
const integralOrderFn = async (item: IOrderList) => {
  const showModalProps = {
    content: '是否取消订单',
    showClose: true,
    isSubmit: true,
  }
  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, data, msg } = await doDelIntegralOrder(item?.no)
    if (code !== 200) return ElMessage.error(`${msg ? msg : '取消订单失败'}`)
    IntegralOrderListInt()
  })
}
const LogisticsData = reactive({
  deliveryPackages: { expressCompanyName: '', expressNo: '' },
  orderLocation: [{ area: '', areaName: '', context: '', ftime: '', status: '', time: '' }],
})
const dialogVisible = ref(false)
const integralOrder = ref<any>()
// 订单追踪
const orderTraceFn = async (item: IOrderList) => {
  dialogVisible.value = true
  integralOrder.value = await integralOrderDetail(item?.no)
  const { data, code, msg } = await doGetLogisticsTrajectoryByWaybillNo(
    integralOrder.value?.expressCompanyName,
    integralOrder.value?.expressNo,
    '',
    integralOrder.value?.integralOrderReceiver?.mobile,
  )
  if (code !== 200) {
    ElMessage.error(msg || '订单追踪获取失败')
  } else if (!data.status || data.status !== '200') {
    LogisticsData.orderLocation[0].context = data.message
  } else if (data.data && Array.isArray(data.data)) {
    // dialogVisible.value = true
    LogisticsData.orderLocation = data.data.reverse()
  }
}
const handleClose = () => {
  dialogVisible.value = false
}
// 积分详情追踪
const integralOrderDetail = async (orderNo: string) => {
  if (orderNo) {
    const { data, code, msg } = await doGetIntegralOrderDetail(orderNo as string)
    if (code !== 200) ElMessage.error(msg || '订单详情获取失败')
    else return data
  }
}
</script>

<template>
  <div c-w-1024 c-ml-49>
    <el-tabs v-model="activeName" @tab-click="refreshFn">
      <el-tab-pane name="1">
        <template #label>
          <span class="custom-tabs-label" c-w-116 c-fs-18 e-c-3 fw-700>
            <span>我的积分</span>
          </span>
        </template>
        <div text-left>当前积分：</div>
        <div e-c-3 c-fs-14 c-mb-37 text-center>
          <div c-c-e31436 c-fs-30 fw-800>{{ systemtotal }}</div>
          <!-- <div c-w-80 c-h-30 c-lh-30 e-c-f text-center c-bg-e31436 c-ml-73>赚积分</div> -->
        </div>
        <!-- <div e-c-3 c-fs-14 fw-700 text-left c-mt-47 c-mb-24>积分明细</div> -->
        <el-date-picker
          v-model="value"
          placeholder="请选择月份"
          type="month"
          style="border: 1px solid #e4e7ed; float: right"
          @change="pickerChange"
        />
        <!-- <div style="float: right; width: 150px; text-align: left">合计：{{ earnIntegral }}</div> -->
        <el-table :data="list">
          <el-table-column prop="createTime" label="日期" c-ml-42 />
          <el-table-column label="收入/支出">
            <template #default="{ row }">
              {{ row?.changeType === 'INCREASE' ? `+ ${row?.variationIntegral}` : `- ${row?.variationIntegral}` }}
            </template>
          </el-table-column>

          <el-table-column label="详细说明">
            <template #default="{ row }">
              {{ gainIntegralCn[row?.gainIntegralType] }}<span v-if="row?.particulars">（{{ row?.particulars }}）</span>
            </template>
          </el-table-column>
        </el-table>
        <div :key="paginationKey" c-mt-28 c-mb-43 flex justify-center>
          <el-pagination background layout="prev, pager, next, total" :total="total" @current-change="handleChangePage" />
        </div>
      </el-tab-pane>
      <el-tab-pane name="2">
        <template #label>
          <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
            <span @click="refreshFn">每日签到</span>
          </span>
        </template>
        <div class="box">
          <div class="text">已连续签到 {{ continueDays }} 天</div>
          <div class="box__right">
            <div v-for="(item, index) in rulesInfo" :key="index">
              <div>{{ indexInt[index] }}</div>
              <div class="box__right--image">
                <img
                  v-if="continueDays as number > index"
                  src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20231027/629984cd86b54a7197f915254e241d35.png"
                  alt=""
                  style="width: 50px; height: 53px; transform: translateY(-4px)"
                />
                <img
                  v-else
                  src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20231027/746d51e5d20f4e549831f947f2745ad3.png"
                  alt=""
                />
              </div>
              <div style="font-weight: normal; color: #666">{{ item }}积分</div>
            </div>
          </div>
          <div class="box__bot" @click="signInFn">{{ todayState ? '已签到' : '签到' }}</div>
        </div>
        <el-date-picker v-model="value" placeholder="请选择月份" type="month" style="border: 1px solid #e4e7ed; float: left" @change="pickerChange" />
        <div style="float: right; width: 150px; text-align: left">合计：{{ earnIntegral }}</div>
        <el-table :data="lists">
          <el-table-column prop="createTime" label="日期" c-ml-42 />
          <el-table-column label="收入/支出">
            <template #default="{ row }">
              {{ row?.changeType === 'INCREASE' ? `+ ${row?.variationIntegral}` : `- ${row?.variationIntegral}` }}
            </template>
          </el-table-column>

          <el-table-column label="详细说明">
            <template #default="{ row }">
              {{ gainIntegralCn[row?.gainIntegralType] }}<span v-if="row?.particulars">（{{ row?.particulars }}）</span>
            </template>
          </el-table-column>
        </el-table>
        <div :key="paginationKey" c-mt-28 c-mb-43 flex justify-center>
          <el-pagination background layout="prev, pager, next, total" :total="total" @current-change="handleChangePages" />
        </div>
      </el-tab-pane>
      <el-tab-pane name="3">
        <template #label>
          <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
            <span>积分订单</span>
          </span>
        </template>
        <div flex e-c-3 c-fs-14 c-h-39 c-lh-39 class="ListTil">
          <div c-ml-150>商品</div>
          <div c-ml-210>售价</div>
          <div c-ml-100>数量</div>
          <div c-ml-100>总计</div>
          <div c-ml-100>订单状态</div>
          <div c-ml-90>操作</div>
        </div>
        <div v-for="(item, index) in IntegralOrderList" :key="index" class="list">
          <div class="list__til">
            <div style="padding-left: 30px">订单号：{{ item?.no }}</div>
            <div style="width: 200px">下单时间：{{ item?.createTime }}</div>
          </div>
          <div class="shop">
            <div class="shop__left">
              <div class="shop__left--image">
                <img :src="item?.image" alt="" />
              </div>
              <div class="shop__left--text">
                {{ item?.productName }}
              </div>
            </div>
            <div style="display: flex; justify-content: space-evenly; width: 700px; align-items: center">
              <div class="shop__centerleft">
                {{ item?.price }}分<span v-if="item?.salePrice !== '0'">
                  + ￥ <span style="font-size: 16px">{{ divTenThousand(item?.salePrice).toFixed(2) }}</span></span
                >
              </div>
              <div style="width: 30px">x{{ item.num }}</div>
              <div style="text-align: center; width: 150px">
                <div style="color: #f7b471">
                  {{ item?.price }}分<span v-if="+item?.freightPrice + +item?.salePrice !== 0" style="color: #f5071e">
                    + ￥
                    <span style="font-size: 14px">{{ divTenThousand(+item?.freightPrice + +item?.salePrice).toFixed(2) }}</span></span
                  >
                </div>
                <div v-if="item?.freightPrice !== '0'" style="color: #999; margin: 5px 0">
                  （含运费：￥{{ divTenThousand(item?.freightPrice).toFixed(2) }}）
                </div>
                <div v-if="item?.price !== '0' && item?.salePrice !== '0'" style="margin-top: 5px">积分支付+ 储值支付</div>
              </div>
              <div>
                <div v-if="item?.status === 'UNPAID'" style="color: #ff6c00">等待付款</div>
                <div v-if="item?.status === 'ON_DELIVERY'" style="color: #71b247">等待收货</div>
                <div v-if="item?.status === 'PAID'" style="color: #71b247">待发货</div>
                <div v-if="item?.status === 'ACCOMPLISH'">已完成</div>
                <div v-if="item?.status === 'SYSTEM_CLOSED'">已关闭</div>
                <div
                  v-if="item?.status === 'ACCOMPLISH' || item?.status === 'ON_DELIVERY'"
                  style="margin-top: 5px; cursor: pointer"
                  @click="orderTraceFn(item)"
                >
                  订单追踪
                </div>
                <div style="margin-top: 5px; cursor: pointer" @click="handleNavToDetail(item)">查看详情</div>
              </div>
              <div style="text-align: center; width: 120px">
                <div v-if="item?.status === 'UNPAID'">
                  <el-icon color="#e31436"> <Clock /> </el-icon>剩余时间：<Countdown :order="item" />
                </div>
                <div v-if="item?.status === 'UNPAID'" class="moe" @click="paymentFn(item)">付款</div>
                <div v-if="item?.status === 'ON_DELIVERY'" class="moe gree" @click="confirmReceipt(item)">确认收货</div>
                <div v-if="item?.status === 'UNPAID'" style="cursor: pointer" @click="integralOrderFn(item)">取消订单</div>
              </div>
            </div>
          </div>
        </div>
        <div :key="paginationKey" c-mt-28 c-mb-43 flex justify-center>
          <el-pagination background layout="prev, pager, next, total" :total="totalList" @current-change="handleChangePageList" />
        </div>
        <el-dialog v-model="dialogVisible" title="物流信息" width="50%" top="5vh" :before-close="handleClose">
          <OrderTrace :order-location="LogisticsData.orderLocation" :integral-order="integralOrder" />
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane name="4">
        <template #label>
          <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
            <span>积分说明</span>
          </span>
        </template>
        <div class="integral">
          <div class="integral_rules" style="padding-top: 0">| 积分获取规则</div>
          <div>
            <div style="padding-left: 10px">每日首次分享获得{{ submitFrom?.integralGainRule?.[0]?.rulesParameter?.basicsValue ?? 0 }}点积分</div>
            <div style="padding: 10px">
              每日首次登录获得{{ submitFrom?.integralGainRule?.[1]?.rulesParameter?.basicsValue ?? 0 }}点积分, 第{{
                integralGainRuleMapping.LOGIN[0].key
              }}天连续登录将额外获赠{{ integralGainRuleMapping.LOGIN[0].value }}点积分； 第{{
                integralGainRuleMapping.LOGIN[1].key
              }}天连续登录将额外获得{{ integralGainRuleMapping.LOGIN[1].value }}点积分。
            </div>
            <div style="padding-left: 10px">
              每日首次签到获得{{ submitFrom?.integralGainRule?.[2]?.rulesParameter?.basicsValue ?? 0 }}点积分, 第{{
                integralGainRuleMapping.SING_IN[0].key
              }}天连续签到将额外获赠{{ integralGainRuleMapping.SING_IN[0].value }}点积分； 第{{
                integralGainRuleMapping.SING_IN[1].key
              }}天连续签到将额外获得{{ integralGainRuleMapping.SING_IN[1].value }}点积分。
            </div>
          </div>
          <div class="line">
            <div class="integral_rules">| 积分抵扣规则</div>
            <div>
              <!-- eslint-disable-next-line vue/no-v-html -->
              <div v-html="formatRichText(submitFrom?.useRule)"></div>
            </div>
          </div>
          <div class="line">
            <div class="integral_rules">| 积分有效期</div>
            <div>
              <div style="padding-left: 10px">积分有效期为{{ submitFrom?.indate }}个月</div>
            </div>
          </div>
          <div class="line">
            <div class="integral_rules">| 积分使用规则</div>
            <div>
              <!-- eslint-disable-next-line vue/no-v-html -->
              <div style="padding-left: 10px" v-html="formatRichText(submitFrom?.ruleInfo)"></div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <ToTopGoCar />
  </div>
</template>

<style scoped lang="scss">
@include b(box) {
  width: 1020px;
  height: 264px;
  font-family: PingFang SC;
  background: linear-gradient(127.62deg, rgb(255, 101, 20) 0.627%, rgb(255, 171, 31) 108.185%);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: space-evenly;
  @include e(left) {
    width: 190px;
    color: #fff;
    @include m(til) {
      font-size: 24px;
      line-height: 34px;
      padding: 54px 0 20px 0;
    }
    @include m(num) {
      font-size: 40px;
      font-weight: 700;
    }
  }
  @include e(right) {
    width: 760px;
    height: 144px;
    background-color: #fff;
    border-radius: 8px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    font-size: 12px;
    color: #333;
    font-weight: bold;
    @include m(image) {
      width: 47px;
      height: 49px;
      margin: 8px 0;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
  @include e(bot) {
    width: 102px;
    height: 40px;
    font-size: 18px;
    line-height: 40px;
    color: #ff7e00;
    background: rgb(255, 217, 181);
    box-shadow: 0px 4px 4px 0px rgb(227, 115, 0);
    border-radius: 4px;
    margin: 16px auto 0;
    cursor: pointer;
  }
}
@include b(text) {
  color: #fff;
  font-family: PingFang SC;
  font-size: 18px;
  font-weight: 400;
  line-height: 25px;
  text-align: center;
}
@include b(integral) {
  font-size: 16px !important;
  text-align: left;
}
@include b(integral_rules) {
  color: #999;
  padding: 40px 0 15px 10px;
}
@include b(ListTil) {
  font-weight: bold;
  background-color: #efefef;
  border: 1px solid #ccc;
}
@include b(list) {
  text-align: left;
  font-size: 12px;
  margin-bottom: 15px;
  @include e(til) {
    display: flex;
    justify-content: space-between;
    background-color: #f7f7f7;
    color: #999;
    height: 40px;
    line-height: 40px;
    border: 1px solid #ebeae6;
  }
}
@include b(shop) {
  height: 100px;
  border: 1px solid #ebeae6;
  display: flex;
  align-items: center;
  @include e(left) {
    width: 360px;
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    @include m(image) {
      width: 60px;
      height: 60px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(text) {
      width: 260px;
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
  @include e(centerleft) {
    width: 100px;
    white-space: nowrap;
  }
}
@include b(moe) {
  width: 87px;
  height: 24px;
  line-height: 24px;
  color: #e31436;
  border: 1px solid #e31436;
  margin: 5px auto;
  cursor: pointer;
}
@include b(gree) {
  color: #71b247 !important;
  border: 1px solid #71b247 !important;
}

:deep(.el-table, .cell) {
  color: #333 !important;
}
</style>
