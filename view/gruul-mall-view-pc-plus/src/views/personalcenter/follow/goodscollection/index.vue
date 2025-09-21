<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { doGetAssessOrderList } from '@/apis/assess'
import type { ApiAssessItem } from './types'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { StarFilled } from '@element-plus/icons-vue'
import { doCancelAssessOrder } from '@/apis/consumer'

const $router = useRouter()
const productName = ref('')
const pageConfig = shallowReactive({
  size: 10,
  current: 1,
  total: 0,
})
const assessList = ref<ApiAssessItem[]>([])

initAssessList()

async function initAssessList() {
  const { size, current } = pageConfig
  const { code, data, msg } = await doGetAssessOrderList({ size, current, productName: productName.value })
  if (code !== 200) return ElMessage.error(msg ? msg : '获取收藏列表失败')
  if (data) {
    pageConfig.total = data.total
    assessList.value = data.records
  }
}
/**
 * @description: 调用initEvaluateList
 */
const callinitAssessList = () => {
  pageConfig.current = 1
  initAssessList()
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
/**
 * @description: 分页切换
 */
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initAssessList()
}
// 取消收藏
const cancelCollectionFn = async (productId: string, shopId: string) => {
  const { code, msg } = await doCancelAssessOrder(shopId, productId)
  if (code === 200) {
    ElMessage.success('取消收藏成功')
    initAssessList()
  } else ElMessage.error(msg || '取消收藏失败')
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left c-mb-16>商品收藏</div>
        <div flex items-center c-w-303 bg-white b-1 c-bc-DCDCDC c-fs-12>
          <el-input v-model="productName" placeholder="请输入商品名称搜索" />
          <div cursor-pointer c-w-48 c-h-32 c-lh-32 c-bg-eee b-l c-bc-DCDCDC @click="callinitAssessList">搜索</div>
        </div>
        <div c-fs-12 text-left c-mt-17 flex flex-wrap c-gap-17>
          <div
            v-for="item in assessList"
            :key="item.productId"
            position-relative
            b-1
            c-bc-eaeaea
            c-w-189
            c-mb-15
            cursor-pointer
            @click="gotoDetail(item.productId, item.shopId)"
          >
            <img :src="item.productPic" c-w-187 c-h-187 c-mb-12 flex items-center justify-center />
            <div v-if="item.status !== 'SELL_ON'" position-absolute top-5 left-6>
              <div c-w-135 c-h-135 b-1 c-bc-fff e-br c-fs-14 e-c-f flex items-center justify-center flex-col c-bg-101010 opacity-50>
                <div v-if="item.status === 'SELL_OUT'">已售罄</div>
                <div v-else>已下架</div>
                <div>Drop off</div>
              </div>
            </div>

            <div c-ellipsis-2 e-c-3 c-ml-9 c-h-32>
              {{ item.productName }}
            </div>
            <div c-c-e31436 fw-700 c-fs-16 c-lh-24 c-ml-9>￥ {{ (item.productPrice && useConversionPrice(item.productPrice)) || 0 }}</div>
            <div flex justify-between>
              <div v-if="item.evaluatePerson" e-c-9 c-mb-13 c-ml-9>
                <text c-c-e31436>{{ item.evaluatePerson }}</text> 人已评价
              </div>
              <div class="cancelCollection" @click.stop="cancelCollectionFn(item.productId, item.shopId)">
                <el-icon><StarFilled /></el-icon><span class="cancelCollection__span"> 取消收藏 </span>
              </div>
            </div>
          </div>
        </div>
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

<style scoped lang="scss">
@include b(cancelCollection) {
  color: #e31436;
  font-size: 16px;
  float: right;
  display: flex;
  padding: 0 5px 8px 0;
  @include e(span) {
    color: #333;
    font-size: 12px;
  }
}
</style>
