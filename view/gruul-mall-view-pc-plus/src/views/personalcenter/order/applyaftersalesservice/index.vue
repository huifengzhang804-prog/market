<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, UploadProps } from 'element-plus'
import { doGetAfssHistory, doSubmitAfss } from '@/apis/afs'
import { getAfsStatusCn, getARefundReasonCn, systemInfo } from '@/hooks'
import { useConversionPrice, useConversionHaoPrice } from '@/utils/useConversionPrice'
import storage from '@/libs/storage'
import { ArrowRight, ArrowDown, Camera } from '@element-plus/icons-vue'
import { aRefundWhy } from '@/hooks/useAfssStatus'
import type { ParamsAfs } from '@/views/personalcenter/order/aftersales/types/index'
import type { ApiAfsOrder } from '@/views/personalcenter/order/aftersalesdetail/types'
import type { ApiShopInfo, ProcessingHistory } from '@/views/personalcenter/order/aftersalesdetail/types/history'
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

const afsForm = ref<ParamsAfs>({
  orderNo: '',
  shopId: '',
  itemId: '',
  reason: '',
  type: '',
  remark: '',
  refundAmount: 0,
  evidences: [],
})
const $router = useRouter()
const $route = useRoute()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const topComponents = ref()
topComponents.value = getterPropertiesList.value
const orderInfo = ref()
const max = ref<number>()
const shopInfo = ref<ApiShopInfo>({
  id: '',
  logo: '',
  name: '',
})
const processingHistory = ref<ProcessingHistory[]>([])
// 退款选择原因
const refundsList = ref<{ name: string; key: string; undelivered: boolean }[]>([])
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'

init()

async function init() {
  initRefundsList($route.query.type as string)
  orderInfo.value = new storage().getItem('CLIENT-applyAfterSalesOrder') || {}
  if (!orderInfo.value) return
  max.value = useConversionPrice(orderInfo.value.refundAmount).toNumber()
  afsForm.value.type = $route.query.type as string
  afsForm.value.itemId = orderInfo.value.itemId
  afsForm.value.shopId = orderInfo.value.shopId
  afsForm.value.orderNo = orderInfo.value.orderNo
  afsForm.value.refundAmount = useConversionPrice(orderInfo.value.refundAmount).toNumber()
  initHistory(orderInfo.value.no)
}
/**
 * @description: 退款原因
 * @returns {*}
 */
function initRefundsList(type: string) {
  const isReturnRefund = type === 'RETURN_REFUND'
  const undelivered = orderInfo.value?.packageStatus === 'WAITING_FOR_RECEIVE'
  for (const key in aRefundWhy) {
    if (aRefundWhy[key].isReturnRefund === isReturnRefund) {
      refundsList.value.push({ name: aRefundWhy[key].title, key, undelivered: aRefundWhy[key].undelivered })
      if (!isReturnRefund && !undelivered) {
        // 退货退款 并且没有发货
        refundsList.value = refundsList.value.filter((item) => item.undelivered)
      }
    }
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
 * @description: 总价
 * @param {*} computed
 * @returns {*}
 */
const tatolPirce = computed(() => {
  if (!orderInfo.value) return
  const { dealPrice, num, fixPrice, freightPrice } = orderInfo.value
  return useConversionPrice(dealPrice).mul(num).sub(useConversionPrice(fixPrice)).add(useConversionPrice(freightPrice)).toFixed(2).toString()
})

/**
 * @description:初始化协商历史（协商历史中添加金额和状态）
 * @param {*} no
 * @returns {*}
 */
async function initHistory(no?: string) {
  if (!no) return
  const { data, code, msg } = await doGetAfssHistory(no)

  if (code !== 200) return ElMessage.error(msg ? msg : '获取协商历史失败')
  if (data) {
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
    processingHistory.value = data.flatMap((item: any) => item.histories)
  }
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
const beforeImgUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp']
  const isLt5M = rawFile.size < 5 * 1024 * 1024
  if (afsForm.value.evidences.length >= 5) {
    ElMessage.error('上传文件数量不能超过5个！')
    return false
  }
  if (!whiteList.includes(rawFile.type)) {
    ElMessage.error('上传文件只能是 JPG,PNG,GIF,BMP 格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('上传文件大小不能超过 5MB!')
    return false
  }
  return true
}
const uploadImgSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  afsForm.value.evidences.push(response.data)
}
/**
 * @description:提交
 * @returns {*}
 */
const handleSubmit = async () => {
  const price = useConversionHaoPrice(afsForm.value.refundAmount).toNumber()
  if (!afsForm.value.reason) {
    return ElMessage.error('请选择退款原因')
  }
  const param = { ...unref(afsForm), refundAmount: price }
  const { code, data, msg } = await doSubmitAfss(param)
  if (code !== 200) return ElMessage.error(msg ? msg : '提交失败')
  ElMessage.success('提交成功')
  navaftersales()
}
const header = {
  Authorization: useUserStore().getterToken,
  'Device-Id': '1',
  Platform: 'PC',
  'Shop-Id': '1',
  'Client-Type': 'CONSUMER',
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
      <el-breadcrumb-item>申请售后</el-breadcrumb-item>
    </el-breadcrumb>
    <el-steps c-w-1600 ma :active="0" align-center process-status="success">
      <el-step title="申请退款" />
      <el-step title="商家审核" />
      <el-step title="完成退款" />
    </el-steps>
    <div c-w-1190 ma flex c-mt-47 text-left c-fs-14>
      <div c-w-320 b-1 c-bc-eee c-pb-10>
        <div c-h-50 fw-700 c-lh-50 c-bg-f3f3f3 e-c-3 c-pl-15 c-mb-10>申请售后商品</div>
        <div c-pl-15 e-c-9 c-mt-10>
          <div c-mb-19 flex>
            <img :src="orderInfo.image" c-w-80 c-h-80 c-mr-9 />
            <div>
              <div c-w-202 c-ellipsis-2 c-mb-20 e-c-3>
                {{ orderInfo.productName }}
              </div>
              <div c-w-202>
                {{ orderInfo.specs?.join(' ; ') }}
              </div>
            </div>
          </div>
          <div c-mb-10>
            订单编号 :<span c-ml-7 e-c-3>{{ orderInfo?.orderNo }}</span>
          </div>
          <div c-mb-10>
            下单时间 :<span c-ml-7 e-c-3>{{ orderInfo.createTime }}</span>
          </div>
          <div c-mb-10>
            商品售价 :<span c-ml-7 e-c-3>¥{{ useConversionPrice(orderInfo.dealPrice).toFixed(2) }} ×{{ orderInfo.num }}（数量）</span>
          </div>
          <div flex c-mb-10 items-center>
            <div c-w-63 e-tj>运费:</div>
            <div e-c-3 c-ml-7>¥{{ useConversionPrice(orderInfo.freightPrice).toFixed(2) }}</div>
          </div>
          <!-- <div flex c-mb-10>
                        <div c-w-63 e-tj>优惠:</div>
                        <div e-c-3 c-ml-7>-¥10.00</div>
                    </div>
                    <div flex c-mb-10>
                        <div c-w-63 e-tj>优惠券:</div>
                        <div e-c-3 c-ml-7>-¥10.00</div>
                    </div>
                    <div c-mb-10>积分可抵 :<span e-c-3 c-ml-7>-¥10.00</span></div> -->
          <div c-mb-10>
            实付金额 :<span e-c-3 c-ml-7>¥{{ tatolPirce }}</span>
          </div>
        </div>
      </div>
      <div c-w-874 b-1 c-bc-eee c-pl-38 c-pt-30 c-pb-10>
        <div e-c-3 c-fs-18 fw-700 c-pb-33 c-mb-19 b-b c-bc-f3f8f4>请按要求填写售后申请，以便快速通过审核</div>
        <div flex items-center c-fs-12 c-ml-15 c-mb-15>
          <div flex>
            <span c-c-e31436>*</span>
            <div e-tj c-w-54>退款原因</div>
          </div>
          <div c-ml-22>
            <el-select v-model="afsForm.reason" c-w-200 placeholder="请选择">
              <el-option v-for="item in refundsList" :key="item.key" :label="item.name" :value="item.key" />
            </el-select>
          </div>
        </div>
        <div flex items-center c-fs-12 c-ml-15 c-mb-15>
          <div flex>
            <span c-c-e31436>*</span>
            <div e-tj c-w-54>退款金额</div>
          </div>
          <div c-ml-22 c-w-200>
            <el-input-number
              v-model="afsForm.refundAmount"
              :disabled="afsForm.type === 'REFUND'"
              :controls="false"
              :precision="2"
              :min="0.01"
              :max="max"
              c-w-200
            />
          </div>
        </div>
        <div flex c-fs-12 c-ml-15 c-mb-15>
          <div>
            <div text-right e-tj c-w-56>退款说明</div>
            <div e-c-9 c-fs-10 c-mt-5>（200字）</div>
          </div>
          <div c-ml-22 c-w-325>
            <el-input v-model="afsForm.remark" maxlength="200" :rows="4" type="textarea" />
          </div>
        </div>
        <div flex c-fs-12 c-ml-15>
          <div e-tj c-w-54>上传凭证</div>
          <div c-ml-26>
            <div flex c-fs-12 c-c-ccc items-center>
              <el-upload
                :headers="header"
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
              <img v-for="item in afsForm.evidences" :key="item" :src="item" c-w-120 c-h-120 c-br-12 c-mr-18 />
            </div>
            <div e-c-f c-bg-e31436 text-center c-lh-30 c-h-30 c-w-100 c-mt-43 c-fs-14 cursor-pointer @click="handleSubmit">提交</div>
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
:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #fff;
  text-align: left;
}
:deep(.el-input-number .el-input__inner) {
  text-align: left;
}
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
</style>
