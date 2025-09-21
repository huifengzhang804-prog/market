<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, UploadProps } from 'element-plus'
import { ArrowRight, Camera, CircleClose } from '@element-plus/icons-vue'
import { doGetOrderInfo } from '@/apis/order'
import { doPostbuyersEvaluation } from '@/apis/assess'
import { ApiOrder } from '@/views/personalcenter/order/myorder/types'
import { useUserStore } from '@/store/modules/user'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'

interface ParamsAssess {
  shopId: string
  orderNo: string
  items: {
    key: {
      productId: string
      skuId: string
    }
    specs: string[]
    comment: string
    medias: string[]
    rate: number
  }[]
}

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const $route = useRoute()
const $router = useRouter()
const orderInfo = ref<ApiOrder>({
  id: '',
  shopId: '',
  buyerId: '',
  no: '',
  createTime: '',
  updateTime: '',
  status: 'BUYER_CLOSED',
  type: 'COMMON',
  remark: '',
  orderPayment: {
    shopId: '',
    orderNo: '',
    payerId: '',
    type: 'ALIPAY',
    totalAmount: '',
    freightAmount: '',
    discountAmount: '',
    payAmount: '',
    payTime: '',
    id: '',
    sn: '',
    createTime: '',
  },
  orderDiscounts: [],
  shopOrders: [
    {
      no: '',
      status: 'OK',
      shopId: '',
      shopName: '',
      shopLogo: '',
      remark: '',
      createTime: '',
      id: '',
      orderNo: '',
      shopOrderItems: [
        {
          afsNo: '',
          afsStatus: 'NONE',
          dealPrice: '',
          status: 'OK',
          freightPrice: '',
          freightTemplateId: '',
          id: '',
          image: '',
          num: 1,
          orderNo: '',
          productId: '',
          productName: '',
          salePrice: '',
          shopId: '',
          skuId: '',
          specs: [''],
          weight: 0,
          packageId: '',
          fixPrice: '0',
          services: [],
          packageStatus: 'BUYER_COMMENTED_COMPLETED',
        },
      ],
    },
  ],
  keyNodeTimeout: {
    commentTimeout: '',
    confirmTimeout: '',
    payTimeout: '',
    confirmTimeoutMills: '',
    payTimeoutMills: '',
    commentTimeoutMills: '',
  },
  orderReceiver: { id: '', address: '', areaCode: [], isDefault: false, mobile: '', name: '' },
  extra: null,
  distributionMode: '',
})
const orderItems = ref([
  {
    afsStatus: 'NONE',
    dealPrice: '',
    freightPrice: '0',
    freightTemplateId: '0',
    id: '',
    image: '',
    num: 1,
    packageId: '',
    packageStatus: 'BUYER_WAITING_FOR_COMMENT',
    productId: '',
    productName: '',
    salePrice: '',
    shopId: '',
    skuId: '',
    specs: [''],
    rate: 5, //星级
    status: 'OK',
    weight: 1,
    comment: '', //说明
    medias: [] as any, //媒体列表
    picture: [] as any, //图片列表
    video: [] as any, //视频列表
  },
])
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'

initAssessOrder()

async function initAssessOrder() {
  const { code, data, msg } = await doGetOrderInfo($route.query.orderNo as string, $route.query.shopId as string, $route.query.packageId as string)

  if (code !== 200) return ElMessage.error(msg ? msg : '获取订单详情失败')
  orderInfo.value = data
  const shopOrderItems = orderInfo.value.shopOrders[0].shopOrderItems
  orderItems.value = shopOrderItems.map((order) =>
    Object.assign(order, {
      rate: 5,
      comment: '',
      medias: [],
      picture: [],
      video: [],
    }),
  ) as any
}

/**
 * @description: 表单提交
 */
let items: any[] = []
const handleSubmit = async (order) => {
  // const packageId = orderItems.value[0].packageId
  const { shopId } = order
  // const orderEvaluateItems = orderItems.value.map((order) => {
  const paramsList = {
    key: {
      productId: '',
      skuId: '',
    },
    specs: [''],
    comment: '',
    medias: [],
    rate: 0,
  }
  const { comment, rate, productId, skuId, medias, specs } = order
  const key = { productId, skuId }
  paramsList.key = key
  paramsList.comment = comment
  paramsList.rate = rate
  paramsList.medias = medias
  paramsList.specs = specs
  // return paramsList
  // })
  items = [paramsList]
  if (!comment) return ElMessage.error(`请输入商品的评级内容`)
  // if (validator(items)) {
  const orderNo = $route.query.orderNo as string
  const params: ParamsAssess = { shopId, orderNo, items }
  const { code, data, msg } = await doPostbuyersEvaluation(params)
  if (code !== 200) return ElMessage.error(msg ? msg : '提交失败')
  ElMessage.success('评价成功')
  $router.push({
    path: '/personalcenter/order/comment',
  })
  // }
}

function validator(
  params: {
    // shopId: string
    key: {
      productId: string
      skuId: string
    }
    comment: string
    medias: string[]
    rate: number
  }[],
) {
  const index = params.findIndex((item) => !item?.comment)
  if (index === -1) return true
  ElMessage.error(`请输入第${index + 1}件商品的评级内容`)
  return false
}

const beforeImgUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/bmp', 'video/mp4']
  const isLt5M = rawFile.size < 5 * 1024 * 1024
  if (!whiteList.includes(rawFile.type)) {
    ElMessage.error('上传文件只能是 JPG,PNG,GIF,BMP或MP4 格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('上传文件大小不能超过 5MB!')
    return false
  }
  return true
}
const uploadImgSuccess = (response: any, uploadFile: any, index: number) => {
  if (orderItems.value[index].medias.length > 5) {
    ElMessage.error('上传图片不能超过6张')
    return
  }
  if (uploadFile.raw.type === 'video/mp4') {
    orderItems.value[index].video.push(response.data)
  } else {
    orderItems.value[index].picture.push(response.data)
  }

  orderItems.value[index].medias = [...orderItems.value[index].video, ...orderItems.value[index].picture]
}
const deletevideo = (index: number, inx: number) => {
  orderItems.value[index].video.splice(inx, 1)
  orderItems.value[index].medias = [...orderItems.value[index].video, ...orderItems.value[index].picture]
}
const deletepicture = (index: number, inx: number) => {
  orderItems.value[index].picture.splice(inx, 1)
  orderItems.value[index].medias = [...orderItems.value[index].video, ...orderItems.value[index].picture]
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
const header = {
  Authorization: useUserStore().getterToken,
  'Device-Id': '1',
  Platform: 'PC',
  'Shop-Id': '1',
  'Client-Type': 'CONSUMER',
}
const topComponents = ref()
topComponents.value = getterPropertiesList.value
</script>

<template>
  <div bg-white c-pb-30>
    <div c-h-110 c-w-1200 flex items-center ma>
      <router-link to="/home">
        <div
          class="log"
          :style="{
            backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
          }"
        />
      </router-link>
    </div>
    <el-breadcrumb :separator-icon="ArrowRight" c-mb-24 c-w-1190 ma>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 个人中心</el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 订单中心</el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 我的订单</el-breadcrumb-item>
      <el-breadcrumb-item>评价</el-breadcrumb-item>
    </el-breadcrumb>

    <div b-1 c-bc-eee c-fs-14 c-mt-32 c-w-1200 ma>
      <div b-b c-bc-eee c-h-59 e-c-9 flex items-center>
        <div c-ml-188>商品信息</div>
        <div c-ml-529>商品晒单</div>
      </div>
      <div v-for="(order, index) in orderItems" :key="order.id" b-b c-bc-eee>
        <div v-if="order.packageStatus === 'SYSTEM_WAITING_FOR_COMMENT' || order.packageStatus === 'BUYER_WAITING_FOR_COMMENT'">
          <div flex items-center>
            <div c-w-424>
              <img :src="order.image" c-h-80 c-w-80 />

              <div c-ellipsis-4 c-mt-23 c-w-300 e-c-3 ma>
                {{ order.productName }}
              </div>
            </div>
            <div c-pb-37 c-pl-34 c-pt-28 text-left>
              <div flex items-center>
                <div flex>
                  <span c-c-e31436>*</span>
                  <div c-w-60 e-tj>商品评分</div>
                </div>
                <div c-ml-26>
                  <el-rate v-model="order.rate" size="small" />
                </div>
              </div>
              <div c-mt-28 flex>
                <div flex>
                  <span c-c-e31436>*</span>
                  <div c-w-60 e-tj>评价</div>
                </div>
                <div c-fs-12 c-ml-26 c-w-528>
                  <el-input
                    v-model="order.comment"
                    :rows="7"
                    maxlength="1000"
                    placeholder="商品是否给力，快来分享一下您的购物心得~"
                    type="textarea"
                  />
                </div>
                <div c-c-ccc c-fs-12 c-ml-13 self-end>500字</div>
              </div>
              <div c-mt-25 flex>
                <div c-w-60 e-tj>晒单</div>
                <div c-ml-26>
                  <div c-c-ccc flex items-center>
                    <el-upload
                      :action="uploadUrl"
                      :before-upload="beforeImgUpload"
                      :headers="header"
                      :on-success="(response:any,uploadFile:any)=>uploadImgSuccess(response,uploadFile,index)"
                      :show-file-list="false"
                    >
                      <div b-1 c-bc-bbb c-h-40 c-mr-16 c-w-146 flex items-center justify-center>
                        <el-icon color="#9b9b9b" size="28px">
                          <Camera />
                        </el-icon>
                        <div c-c-101010 c-ml-6>上传图片/视频</div>
                      </div>
                    </el-upload>
                    {{ order.medias.length }}/6
                  </div>
                  <div c-gap-15 c-mt-18 flex>
                    <div v-for="(item, inx) in order.video" :key="inx" position-relative>
                      <el-icon :size="20" position-absolute right-1 top-1 z-100 @click.stop="deletevideo(index, inx)">
                        <CircleClose />
                      </el-icon>
                      <video :controls="true" :src="item" c-br-12 c-h-90 c-w-90 />
                    </div>
                    <div v-for="(item, inx) in order.picture" :key="inx" position-relative>
                      <el-icon :size="20" position-absolute right-1 top-1 @click="deletepicture(index, inx)">
                        <CircleClose />
                      </el-icon>
                      <img :src="item" c-br-12 c-h-90 c-w-90 />
                    </div>
                  </div>
                  <div c-c-ccc c-fs-12 c-mt-32>每张不超过5M，支持格式jpg，jpeg，bmp，png，mp4</div>
                </div>
              </div>
            </div>
          </div>
          <div bg-white c-h-110 c-mb-32 c-mt-11 c-pt-36 ma>
            <div c-bg-e31436 c-fs-18 c-h-38 c-lh-38 c-w-108 cursor-pointer e-c-f fw-700 ma text-center @click="handleSubmit(order)">发表评价</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
</style>
