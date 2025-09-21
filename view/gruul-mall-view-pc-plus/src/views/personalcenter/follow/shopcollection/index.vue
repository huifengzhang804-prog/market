<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { doGetConcernListFromMine } from '@/apis/consumer'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doCancelAttentionAndAttention } from '@/apis/consumer'
export interface ConcernItem {
  logo: string
  newProducts: boolean
  numberFollowers: number
  shopId: string
  shopName: string
  popularAttentionVOList: any[]
}

const $router = useRouter()
const pageConfig = shallowReactive({
  size: 4,
  current: 1,
  total: 0,
})
const shopList = ref<ConcernItem[]>([])

initshopList()

async function initshopList() {
  const { size, current } = pageConfig
  const { code, data, msg } = await doGetConcernListFromMine({ size, current })
  if (code !== 200) return ElMessage.error(msg ? msg : '获取店铺关注列表失败')
  if (data) {
    pageConfig.total = data.total
    shopList.value = data.records
  }
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
  initshopList()
}
const gotoshop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId },
  })
}
// 取消关注
const handleConcern = async (info: ConcernItem) => {
  const { code, msg } = await doCancelAttentionAndAttention(info.shopName, info.shopId, false, info.logo)
  if (code !== 200) {
    if (code === 2) return
    return ElMessage.error(msg || `取消关注失败`)
  }
  ElMessage.success(`取消关注成功`)
  initshopList()
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left>关注店铺</div>
        <div v-for="shop in shopList" :key="shop.shopId" b-b b-b-dashed c-bc-eee flex c-pb-25 c-mt-21>
          <img :src="shop.logo" c-w-82 c-h-82 cursor-pointer @click="gotoshop(shop.shopId)" />
          <div e-c-3 c-ml-23 c-w-917>
            <div text-left flex justify-between>
              <div c-fs-16 fw-700 cursor-pointer @click="gotoshop(shop.shopId)">
                {{ shop.shopName }}
              </div>
              <div c-fs-14 cursor-pointer @click="gotoshop(shop.shopId)">进店看看</div>
            </div>
            <div c-fs-14 text-left c-mt-12 fw-700>{{ shop.numberFollowers }} 人关注</div>
            <div c-fs-12 text-left cursor-pointer c-mt-12 @click="handleConcern(shop)">
              <q-icon name="icon-31guanzhu1xuanzhong" size="12px" color="#e31436" /> 取消关注
            </div>
            <div c-fs-12 text-left flex flex-wrap c-gap-33>
              <div
                v-for="item in shop.popularAttentionVOList"
                :key="item.productId"
                b-1
                c-bc-eaeaea
                c-w-204
                c-mb-15
                cursor-pointer
                @click="gotoDetail(item.productId, shop.shopId)"
              >
                <img :src="item.pic" c-w-204 c-h-204 c-mb-12 />
                <div c-ellipsis-2 e-c-3 c-ml-9>
                  {{ item.productName }}
                </div>
                <div c-c-e31436 fw-700 c-fs-16 c-lh-24 c-ml-9>￥ {{ useConversionPrice(item.productPrice) }}</div>
                <div v-if="item.evaluated" e-c-9 c-mb-13 c-ml-9>
                  <text c-c-e31436>{{ item.evaluated }}</text> 人已评价
                </div>
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
            :page-size="4"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
