<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { getAfsStatusCn } from '@/hooks'
import DateUtil from '@/utils/date'
import { useUserStore } from '@/store/modules/user'
import { doGetAfsOrder, doGetUserData } from '@/apis/afs'
import type { ApiOrderAfsItem } from '@/views/personalcenter/order/aftersales/types/type'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'

const $router = useRouter()
const $userStore = useUserStore()
const keywords = ref('')
const status = ref('')
const startTime = ref('')
const afsOrderList = ref<ApiOrderAfsItem[]>([])
const pageConfig = shallowReactive({
  size: 10,
  current: 1,
  total: 0,
})
const statusList = [
  {
    value: '',
    label: '全部状态',
  },
  {
    value: 'PENDING',
    label: '待处理',
  },
  {
    value: 'PROCESSING',
    label: '处理中',
  },
  {
    value: 'COMPLETED',
    label: '已完成',
  },
  {
    value: 'CLOSED',
    label: '已关闭',
  },
]
const timeList = [
  {
    value: '',
    label: '全部订单',
  },
  {
    value: new DateUtil().getLastMonth(new Date()),
    label: '近一个月',
  },
  {
    value: new DateUtil().getLastThreeMonth(new Date()),
    label: '近三个月',
  },
]
const elMessageShow = ref(true)

initAfsOrder()
getUserInfo()

async function initAfsOrder() {
  const { code, data, msg } = await doGetAfsOrder({
    current: pageConfig.current,
    size: pageConfig.size,
    keywords: keywords.value,
    startTime: startTime.value,
    status: status.value,
  })
  if (code !== 200) {
    if (elMessageShow.value) {
      elMessageShow.value = false
      return ElMessage.error({
        message: msg || '获取列表失败',
        onClose: () => {
          elMessageShow.value = true
        },
      })
    }
  }
  if (data) {
    pageConfig.total = data.total
    afsOrderList.value = data.records
  }
}
/**
 * @description: 获取用户消息
 */
async function getUserInfo() {
  const { data, code, msg } = await doGetUserData()
  if (code !== 200) {
    if (elMessageShow.value) {
      elMessageShow.value = false
      return ElMessage.error({
        message: msg || '获取列表失败',
        onClose: () => {
          elMessageShow.value = true
        },
      })
    }
  }
  $userStore.addUserInfo(data as any)
}
const totalPrice = (price: string, num: number) => {
  return useConversionPrice(price).mul(num)
}
/**
 * @description: 分页切换
 */
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initAfsOrder()
}
/**
 * @description: 调用initAfsOrder
 */
const callinitAfsOrder = () => {
  pageConfig.current = 1
  initAfsOrder()
}
/**
 * @description: 导航去售后订单详情
 * @returns {*}
 */
const handleGoSalesOrder = (no: string, packageId?: string, formType?: boolean) => {
  $router.push({
    path: '/personalcenter/order/aftersalesdetail',
    query: { no, packageId, formType: formType?.toString() },
  })
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left c-mb-16>售后订单</div>
        <div flex justify-between items-center>
          <div flex items-center c-w-303 bg-white b-1 c-bc-DCDCDC c-fs-12>
            <el-input v-model="keywords" placeholder="请输入商品名称或订单号搜索 " />
            <div cursor-pointer c-w-48 c-h-32 c-lh-32 c-bg-eee b-l c-bc-DCDCDC @click="callinitAfsOrder">搜索</div>
          </div>
          <div c-fs-12 e-c-3 flex items-center>
            <div>下单时间:</div>
            <el-select
              v-model="startTime"
              placeholder="全部订单"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callinitAfsOrder"
            >
              <el-option v-for="item in timeList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <div c-ml-27>全部状态:</div>
            <el-select
              v-model="status"
              placeholder="全部状态"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callinitAfsOrder"
            >
              <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
        </div>
        <div flex e-c-3 c-fs-14 c-h-39 c-lh-39>
          <div c-ml-45>商品</div>
          <div c-ml-225>售价(元)</div>
          <div c-ml-71>数量</div>
          <div c-ml-67>实付金额(元)</div>
          <div c-ml-62>退款金额(元)</div>
          <div c-ml-74>售后状态</div>
          <div c-ml-75>操作</div>
        </div>
        <template v-for="afsOrder in afsOrderList" :key="afsOrder.id">
          <div b-1 c-bc-ebeae6 c-bg-f7f7f7 c-h-40 c-lh-40 e-c-9 c-fs-12 text-left c-w-1022>
            <span c-ml-24>售后编号：{{ afsOrder.no }}</span>
            <span c-ml-40>申请时间：{{ afsOrder.createTime }}</span>
          </div>
          <div c-w-1022 c-fs-12 e-c-3 c-mb-10>
            <div c-h-85 flex items-center b-1 c-bc-EBEAE6>
              <img :src="afsOrder.afsOrderItem.image" c-w-60 c-h-60 bg-black c-ml-24 />
              <div c-w-164 text-left c-ml-12>
                <div c-ellipsis-2>
                  {{ afsOrder.afsOrderItem.productName }}
                </div>
                <div e-c-9>
                  {{ afsOrder.afsOrderItem.specs?.join(' ; ') }}
                </div>
              </div>
              <div c-w-80 c-ml-20>￥{{ useConversionPrice(afsOrder.afsOrderItem.dealPrice) }}</div>
              <div c-w-40 c-ml-53>
                {{ afsOrder.afsOrderItem.num }}
              </div>
              <div c-w-80 c-ml-60>￥{{ totalPrice(afsOrder.afsOrderItem.dealPrice, afsOrder.afsOrderItem.num) }}</div>
              <div c-w-80 c-ml-60>￥{{ useConversionPrice(afsOrder.refundAmount) }}</div>
              <div c-w-80 c-ml-65>
                {{ getAfsStatusCn(afsOrder.status).list }}
              </div>
              <div c-w-87 c-ml-33>
                <div cursor-pointer hover-c-c-e31436 hover-underline @click="handleGoSalesOrder(afsOrder.no, afsOrder.packageId)">查看详情</div>
                <div
                  v-if="afsOrder.status === 'RETURN_REFUND_AGREE' || afsOrder.status === 'SYSTEM_RETURN_REFUND_AGREE'"
                  b-1
                  c-bc-ff6c00
                  c-w-87
                  c-h-24
                  c-lh-24
                  c-c-ff6c00
                  c-mt-6
                  cursor-pointer
                  @click="handleGoSalesOrder(afsOrder.no, afsOrder.packageId, true)"
                >
                  填写物流
                </div>
              </div>
            </div>
          </div>
        </template>
        <ToTopGoCar />
        <div c-mt-28 c-mb-43 flex justify-center>
          <el-pagination
            background
            layout="prev, pager, next"
            :total="+pageConfig.total"
            :current-page="pageConfig.current"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
