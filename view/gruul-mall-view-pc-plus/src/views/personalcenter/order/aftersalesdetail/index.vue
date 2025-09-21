<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox, UploadProps } from 'element-plus'
import { doGetAfssInfo, doPutAfssCancel, doGetAfssHistory, doGetReturnedByAfsNo } from '@/apis/afs'
import { getAfsStatusCn, getARefundReasonCn, systemInfo } from '@/hooks'
import { useConversionPrice } from '@/utils/useConversionPrice'
import storage from '@/libs/storage'
import logisticsCompany from '@/assets/json/data.json'
import { MOBILE } from '@/utils/RegexPool'
import { ArrowRight, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import type { ApiAfsOrder } from '@/views/personalcenter/order/aftersalesdetail/types'
import type { ApiUserData, ApiShopInfo, ProcessingHistory } from './types/history'
import { storeToRefs } from 'pinia'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

// const $userStore = useUserStore()
const $router = useRouter()
const $route = useRoute()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const afsOrderInfo = ref()
const afsType = ref(false)
const formType = ref(false)
const afsorderType = ref(false)
const processingHistory = ref<ProcessingHistory[]>([])
const shopInfo = ref<ApiShopInfo>({
  id: '',
  logo: '',
  name: '',
})
const fromData = ref({
  expressNo: '',
  mobile: '',
  remark: '',
  deliverType: 'EXPRESS',
  expressCompanyName: '',
  expressCompanyCode: '',
})

const topComponents = ref()
topComponents.value = getterPropertiesList.value

initAfssInfo()
initformType()

async function initformType() {
  if ($route.query.formType) {
    formType.value = $route.query.formType as any
  }
}
async function initAfssInfo() {
  const { code, data, msg } = await doGetAfssInfo($route.query.no as string)
  if (code !== 200) return ElMessage.error(msg ? msg : '获取售后详情失败')
  afsOrderInfo.value = data
  if (data && data.no) {
    initHistory(data.no)
  }
}
/**
 * @description: 导航去售后订单
 * @returns {*}
 */
const navaftersales = () => {
  $router.push({
    path: '/personalcenter/order/aftersales',
    query: {},
  })
}
/**
 * @description: 导航去我的订单
 * @returns {*}
 */
const navmyorder = () => {
  $router.push({
    path: '/personalcenter/order/myorder',
    query: {},
  })
}
/**
 * @description: 导航去申请售后
 * @returns {*}
 */
const navapplyaftersales = () => {
  const applyAfterSalesOrder = {
    ...afsOrderInfo.value?.afsOrderItem,
    itemId: afsOrderInfo.value?.shopOrderItemId,
    orderNo: afsOrderInfo.value?.orderNo,
    refundAmount: afsOrderInfo.value?.refundAmount,
    shopId: afsOrderInfo.value?.shopId,
    no: afsOrderInfo.value?.no,
  }
  new storage().setItem('CLIENT-applyAfterSalesOrder', applyAfterSalesOrder, 60 * 60 * 2)
  $router.push({
    path: '/personalcenter/order/applyaftersales',
    query: {},
  })
}
/**
 * @description: 总价
 * @param {*} computed
 * @returns {*}
 */
const tatolPirce = computed(() => {
  if (!afsOrderInfo.value) return
  const { dealPrice, num } = afsOrderInfo.value.afsOrderItem
  return useConversionPrice(dealPrice).mul(num).toFixed(2).toString()
})
/**
 * @description: 撤销申请
 * @returns {*}
 */
const handleAfsCancel = async () => {
  ElMessageBox.confirm(`确定撤销本次申请吗?`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!afsOrderInfo.value) return ElMessage.error('撤销失败')
    const { code, msg } = await doPutAfssCancel(afsOrderInfo.value.no)
    if (code !== 200) return ElMessage.error(msg ? msg : '撤销失败')
    ElMessage.success('撤销成功')
    initAfssInfo()
  })
}
/**
 * @description:初始化协商历史（协商历史中添加金额和状态）
 * @param {*} no
 * @returns {*}
 */
async function initHistory(no: string) {
  const { data, code, msg } = await doGetAfssHistory(no)

  if (code !== 200) return ElMessage.error(msg ? msg : '获取协商历史失败')
  if (!data || !Array.isArray(data) || !data.length) return ElMessage.error('暂无协商历史')
  shopInfo.value = {
    id: data[0].shopId,
    logo: data[0].shopLogo,
    name: data[0].shopName,
  }
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
 * @description: 初始化协商历史（协商历史中整合用户头像和商家logo）
 * @returns {*}
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
        name: new storage().getItem('CLIENT-userStoreInfo')?.nickname,
        logo: new storage().getItem('CLIENT-userStoreInfo')?.avatar,
        afsStatusCn: afsInfo.desc,
        isConsumer: true,
      }
    } else if (isSystem) {
      return { ...item, name: systemInfo.name, logo: systemInfo.avatar, afsStatusCn: afsInfo.desc, isConsumer: false }
    }
    return { ...item, name: shopInfo.value.name, logo: shopInfo.value.logo, afsStatusCn: afsInfo.desc, isConsumer: false }
  })
}

/**
 * @description: 物流公司选择
 * @param {*} arr
 * @returns {*}
 */
const handlechange = (val: string) => {
  logisticsCompany.forEach((item) => {
    if (item.companyCode === val) {
      fromData.value.expressCompanyName = item.companyName
    }
  })
}
/**
 * @description: 提交表单
 * @returns {*}
 */
const handleSubmit = async () => {
  if (!afsOrderInfo.value) return
  const { expressCompanyName, expressCompanyCode, expressNo, mobile, remark, deliverType } = fromData.value
  if (!expressNo) {
    return ElMessage.error('请输入物流单号')
  }
  if (!/^[0-9a-zA-Z]*$/g.test(expressNo)) {
    return ElMessage.error('请输入正确的物流单号')
  }
  if (!expressCompanyName || !expressCompanyCode) {
    return ElMessage.error('请选择物流公司')
  }
  if (!MOBILE.test(mobile)) {
    return ElMessage.error('请提交正确手机号')
  }

  const params = { mobile, remark, deliverType, expressCompany: { expressCompanyName, expressCompanyCode, expressNo } }
  const { code, data, msg } = await doGetReturnedByAfsNo(afsOrderInfo.value.no, 'EXPRESS_REFUND', { expressRefund: params })
  if (code !== 200) return ElMessage.error(msg ? msg : '提交失败')
  initAfssInfo()
  setTimeout(() => {
    $router.go(-1)
  }, 500)
}
</script>

<template>
  <div bg-white c-pb-42>
    <div c-w-1200 c-h-110 flex ma items-center>
      <router-link to="/home">
        <div
          class="log"
          :style="{
            backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
          }"
        />
      </router-link>
    </div>
    <el-breadcrumb :separator-icon="ArrowRight" c-w-1190 ma c-mb-42>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 个人中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 订单中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navaftersales"> 售后订单 </el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>
    <el-steps
      v-if="afsOrderInfo?.type === 'REFUND'"
      c-w-1600
      ma
      :active="afsOrderInfo.status === 'SYSTEM_CLOSED' || afsOrderInfo.status === 'BUYER_CLOSED' ? 2 : getAfsStatusCn(afsOrderInfo.status).steps"
      align-center
      process-status="success"
    >
      <el-step title="申请退款" />
      <el-step title="审核未通过" />
      <el-step :title="afsOrderInfo.status === 'REFUND_REJECT' ? '售后关闭' : '售后成功'" />
    </el-steps>
    <el-steps
      v-if="afsOrderInfo?.type === 'RETURN_REFUND'"
      c-w-1425
      ma
      :active="afsOrderInfo.status === 'SYSTEM_CLOSED' || afsOrderInfo.status === 'BUYER_CLOSED' ? 3 : getAfsStatusCn(afsOrderInfo.status).steps"
      align-center
      process-status="success"
    >
      <el-step title="申请退货" />
      <el-step title="寄回商品" />
      <el-step title="商家收货" />
      <el-step :title="['RETURNED_REFUND_REJECT', 'RETURN_REFUND_REJECT'].includes(afsOrderInfo.status) ? '售后关闭' : '售后成功'" />
    </el-steps>
    <div c-w-1190 ma flex c-mt-47 text-left c-fs-14>
      <div c-w-320 b-1 c-bc-eee c-pb-10>
        <div c-h-50 fw-700 c-lh-50 c-bg-f3f3f3 e-c-3 c-pl-15 c-mb-10>售后申请</div>
        <div c-pl-15 e-c-9 b-b b-b-dashed c-bc-eee>
          <div c-mb-10>
            退款金额 ：<span e-c-3> ¥{{ afsOrderInfo && useConversionPrice(afsOrderInfo.refundAmount) }}</span>
          </div>
          <div c-mb-10>
            申请原因 ：<span e-c-3> {{ afsOrderInfo && getARefundReasonCn(afsOrderInfo.reason) }}</span>
          </div>
          <div c-mb-10>
            申请时间 ：<span e-c-3> {{ afsOrderInfo?.createTime }}</span>
          </div>
          <div v-if="!afsType" e-c-3 c-mb-10 hover-underline cursor-pointer @click="afsType = true">
            显示全部<el-icon>
              <ArrowDown />
            </el-icon>
          </div>
          <div v-if="afsType" flex c-mb-10>
            售后编号 ：
            <div e-c-3 break-all c-w-216>
              {{ afsOrderInfo?.no }}
            </div>
          </div>
          <div v-if="afsType" c-mb-10>
            问题描述 ：<span e-c-3> {{ afsOrderInfo?.explain }}</span>
          </div>
          <div v-if="afsType" e-c-3 c-mb-10 hover-underline cursor-pointer @click="afsType = false">
            收起<el-icon>
              <ArrowUp />
            </el-icon>
          </div>
        </div>
        <div c-pl-15 e-c-9 c-mt-10>
          <div c-mb-19 flex>
            <img :src="afsOrderInfo?.afsOrderItem.image" c-w-80 c-h-80 c-mr-9 />
            <div>
              <div c-w-202 c-ellipsis-2 c-mb-20 e-c-3>
                {{ afsOrderInfo?.afsOrderItem.productName }}
              </div>
              <div c-w-202>
                {{ afsOrderInfo?.afsOrderItem.specs?.join(' ; ') }}
              </div>
            </div>
          </div>
          <div c-mb-10>
            订单编号 :<span c-ml-7 e-c-3>{{ afsOrderInfo?.orderNo }}</span>
          </div>
          <div c-mb-10>
            下单时间 :<span c-ml-7 e-c-3>{{ afsOrderInfo?.afsOrderItem.createTime }}</span>
          </div>
          <div v-if="!afsorderType" e-c-3 c-mb-10 hover-underline cursor-pointer @click="afsorderType = true">
            显示全部<el-icon>
              <ArrowDown />
            </el-icon>
          </div>
          <div v-if="afsorderType" c-mb-10>
            商品售价 :<span c-ml-7 e-c-3
              >¥{{ afsOrderInfo && useConversionPrice(afsOrderInfo.afsOrderItem.dealPrice).toFixed(2) }} ×{{
                afsOrderInfo?.afsOrderItem.num
              }}（数量）</span
            >
          </div>
          <!-- <div flex c-mb-10>
                        <div c-w-63 e-tj>运费:</div>
                        <div e-c-3 c-ml-7>¥10.00</div>
                    </div>
                    <div flex c-mb-10>
                        <div c-w-63 e-tj>优惠:</div>
                        <div e-c-3 c-ml-7>-¥10.00</div>
                    </div>
                    <div flex c-mb-10>
                        <div c-w-63 e-tj>优惠券:</div>
                        <div e-c-3 c-ml-7>-¥10.00</div>
                    </div>
                    <div c-mb-10>积分可抵 :<span e-c-3 c-ml-7>-¥10.00</span></div> -->
          <div v-if="afsorderType" c-mb-10>
            实付金额 :<span e-c-3 c-ml-7>¥{{ tatolPirce }}</span>
          </div>
          <div v-if="afsorderType" e-c-3 c-mb-10 hover-underline cursor-pointer @click="afsorderType = false">
            收起<el-icon>
              <ArrowUp />
            </el-icon>
          </div>
        </div>
      </div>
      <div c-w-874 b-1 c-bc-eee c-pl-38 c-pt-30 c-pb-10>
        <div v-if="!formType" e-c-3 c-fs-18 fw-700>当前状态：{{ afsOrderInfo && getAfsStatusCn(afsOrderInfo.status).list }}</div>
        <div v-if="afsOrderInfo && !getAfsStatusCn(afsOrderInfo.status).closable" e-c-9 c-mt-13>关闭时间：{{ afsOrderInfo?.updateTime }}</div>
        <div
          v-if="afsOrderInfo && getAfsStatusCn(afsOrderInfo.status).applyForAgain"
          c-mt-19
          c-w-80
          c-h-30
          b-1
          c-bc-e31436
          c-c-e31436
          text-center
          c-lh-30
          cursor-pointer
          @click="navapplyaftersales"
        >
          再次申请
        </div>
        <div flex>
          <div
            v-if="(afsOrderInfo?.status === 'RETURN_REFUND_AGREE' || afsOrderInfo?.status === 'SYSTEM_RETURN_REFUND_AGREE') && !formType"
            c-mt-31
            c-w-80
            c-h-30
            b-1
            c-bc-FF6C00
            c-c-FF6C00
            text-center
            c-lh-30
            c-mr-16
            cursor-pointer
            @click="formType = true"
          >
            填写物流
          </div>
          <div
            v-if="afsOrderInfo && getAfsStatusCn(afsOrderInfo.status).closable && !formType"
            c-mt-31
            c-w-80
            c-h-30
            b-1
            c-bc-e31436
            c-c-e31436
            text-center
            c-lh-30
            cursor-pointer
            @click="handleAfsCancel"
          >
            撤销申请
          </div>
        </div>
        <div v-if="formType">
          <div c-mb-40 flex>
            <img :src="afsOrderInfo?.afsOrderItem.image" c-w-80 c-h-80 c-mr-9 />
            <div>
              <div c-ellipsis-1 c-mb-10 e-c-3 fw-700>
                {{ afsOrderInfo?.afsOrderItem.productName }}
              </div>
              <div c-mb-10 e-c-9>
                {{ afsOrderInfo?.afsOrderItem.specs?.join(' ; ') }}
              </div>
              <div c-c-e31436>
                {{ tatolPirce }}
              </div>
            </div>
          </div>
          <div flex items-center c-fs-12 c-ml-15 c-mb-15>
            <div flex>
              <span c-c-e31436>*</span>
              <div e-tj c-w-54>物流单号</div>
            </div>
            <div c-ml-22 c-w-200 b-1 c-bc-bbb>
              <el-input v-model="fromData.expressNo" placeholder="请填写物流单号" />
            </div>
          </div>
          <div flex items-center c-fs-12 c-ml-15 c-mb-15>
            <div flex>
              <span c-c-e31436>*</span>
              <div e-tj c-w-54>物流公司</div>
            </div>
            <div c-ml-22>
              <el-select v-model="fromData.expressCompanyCode" c-w-200 b-1 c-bc-bbb placeholder="请选择" @change="handlechange">
                <el-option v-for="item in logisticsCompany" :key="item.companyCode" :label="item.companyName" :value="item.companyCode" />
              </el-select>
            </div>
          </div>
          <div flex items-center c-fs-12 c-ml-15 c-mb-15>
            <div flex>
              <span c-c-e31436>*</span>
              <div e-tj c-w-54>联系电话</div>
            </div>
            <div c-ml-22 c-w-200 b-1 c-bc-bbb>
              <el-input v-model="fromData.mobile" placeholder="请填写联系电话" :maxlength="11" />
            </div>
          </div>
          <div flex c-fs-12 c-ml-15 c-mb-15>
            <div>
              <div text-right e-tj c-w-56>退货说明</div>
              <div e-c-9 c-fs-10 c-mt-5>（200字）</div>
            </div>
            <div c-ml-22 c-w-325>
              <el-input v-model="fromData.remark" maxlength="200" :rows="4" type="textarea" placeholder="请填写退货说明" />
            </div>
          </div>
          <div flex c-fs-12 c-ml-15>
            <div c-w-54 />
            <!-- <div e-tj c-w-54>上传凭证</div> -->
            <div c-ml-26>
              <!-- <div flex c-fs-12 c-c-ccc items-center>
                                <el-upload
                                    :action="uploadUrl"
                                    :show-file-list="false"
                                    :limit="5"
                                    :before-upload="beforeImgUpload"
                                    :on-success="uploadImgSuccess"
                                >
                                    <div c-w-120 c-h-40 b-1 c-bc-e31436 flex items-center justify-center c-mr-16 c-bg-fff5f7>
                                        <el-icon color="#e31436" size="28px">
                                            <Camera />
                                        </el-icon>
                                        <div c-ml-6 c-c-e31436>上传图片</div>
                                    </div>
                                </el-upload>
                                最多上传5张，每张大小不超5M;支持GIF,JPG,PNG,BMP格式
                            </div>
                            <div flex c-mt-18>
                                <div c-w-120 c-h-120 c-br-12 bg-black c-mr-18 v-for="item in 2"></div>
                            </div> -->
              <div e-c-f c-bg-e31436 text-center c-lh-30 c-h-30 c-w-100 c-mt-43 c-fs-14 cursor-pointer @click="handleSubmit">提交</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div c-w-1190 ma c-mt-14 text-left c-fs-14 b-1 c-bc-ccc>
      <div c-h-48 c-bg-f3f3f3 c-lh-48 fw-700 c-pl-15>协商记录</div>
      <div v-for="(historyItem, index) in processingHistory" :key="index" flex c-pt-32 c-pl-16 c-pb-30 c-pr-38 b-b b-b-dashed c-bc-eee>
        <img :src="historyItem.logo" c-w-40 c-h-40 e-br c-mr-16 />
        <div>
          <div flex items-center c-mb-10>
            <div c-mr-16 c-fs-12 e-c-3 fw-700>
              {{ historyItem.name }}
            </div>
            <div e-c-9>
              {{ historyItem.createTime }}
            </div>
          </div>
          <view v-if="historyItem.reason && historyItem.refundAmount" c-w-1000 e-c-3 fw-700>
            {{ historyItem.afsStatusCn }}，原因：{{ getARefundReasonCn(historyItem.reason as string) }}，金额：{{
              useConversionPrice(historyItem.refundAmount)
            }}元，说明：{{ historyItem.remark }}
          </view>
          <view v-else c-w-1000 e-c-3 fw-700>
            <view>{{ historyItem.afsStatusCn }}</view>
            <view v-if="!historyItem.isConsumer && historyItem.remark"> <br />拒绝说明： {{ historyItem.remark }} </view>
          </view>
          <view v-if="historyItem.evidences?.length" e-c-3 fw-700> 凭证： </view>
          <view flex c-gap-10 c-w-1000 e-c-3 fw-700>
            <img v-for="image in historyItem.evidences" :key="image" c-w-44 c-h-44 :src="image" />
          </view>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-step__head.is-success) {
  color: #e31436;
  border-color: #e31436;
}

:deep(.el-step__title.is-success) {
  color: #e31436;
  font-weight: bold;
}

:deep(.el-step__title.is-wait) {
  color: #333333;
}

:deep(.el-step__head.is-wait) {
  color: #333333;
}

:deep(.el-step__title.is-finish) {
  color: #333333;
}

:deep(.el-step__head.is-finish) {
  color: #333333;
  border-color: #eee;
}
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
</style>
